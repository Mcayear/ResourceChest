package cn.vusv.resourcechest.config;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import cn.vusv.resourcechest.ResourceChestMain;
import cn.vusv.resourcechest.Utils;
import cn.vusv.resourcechest.config.chest.RandomItemInfo;
import cn.vusv.resourcechest.config.player.ChestLocationData;
import cn.vusv.resourcechest.config.player.ChestOpenState;
import me.iwareq.fakeinventories.FakeInventory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ChestConfig {
    public static LinkedHashMap<String, ChestConfig> ChestConfigMap;
    public static LinkedHashMap<String, String> ChestLocationMap;

    public static void init() {
        ChestConfigMap = new LinkedHashMap<>();
        ChestLocationMap = new LinkedHashMap<>();
        File[] files = new File(ResourceChestMain.getInstance().getDataFolder() + "/chests").listFiles();

        for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().replace(".yml", "");
            ChestConfig chestConfig = new ChestConfig(fileName, new Config(file, Config.YAML));
            ChestConfigMap.put(fileName, chestConfig);
            for (String v : chestConfig.pos) {
                ChestLocationMap.put(v, fileName);
            }
        }
    }

    public static boolean existChestConfig(String chestName) {
        return ChestConfigMap.containsKey(chestName);
    }

    public static boolean createChestConfig(String chestName) {
        return ResourceChestMain.getInstance().saveResource("chests.yml", "./chests/" + chestName + ".yml", false);
    }

    public static boolean deleteChestConfig(String chestName) {
        return ResourceChestMain.getInstance().saveResource("chests.yml", "./chests/" + chestName + ".yml", false);
    }

    public String chestName;
    public Config cfg;
    public boolean canPutItem;
    public int openCountMax;
    public double refreshInterval;
    public RandomItemInfo randomItemInfo;
    public List<String> fixedItem;
    public List<String> randomItem;
    public List<String> pos;

    public ChestConfig(String chestName, Config config) {
        this.chestName = chestName;
        this.cfg = config;
        this.canPutItem = config.getBoolean("canPutItem", false);
        this.openCountMax = config.getInt("openCountMax", -1);
        this.refreshInterval = config.get("refreshInterval", 86400.0);
        this.randomItemInfo = new RandomItemInfo(config.getSection("randomItemInfo"));
        this.fixedItem = config.getStringList("fixedItem");
        this.randomItem = config.getStringList("randomItem");
        this.pos = config.getStringList("pos");
    }

    public boolean placeChest(Position position) {
        String loca = Utils.posToString(position);
        if (this.pos.equals(loca)) {
            return false;
        } else {
            ChestLocationMap.put(loca, this.chestName);
            this.pos.add(loca);
            this.cfg.set("pos", this.pos);
            this.cfg.save();
            return true;
        }
    }

    public boolean removeChest(Position position) {
        String loca = Utils.posToString(position);
        return removeChest(loca);
    }

    public boolean removeChest(String loca) {
        if (this.pos.equals(loca)) {
            ChestLocationMap.remove(loca);
            this.pos.remove(loca);
            this.cfg.set("pos", this.pos);
            this.cfg.save();
            return true;
        } else {
            return false;
        }
    }

    public boolean sendInventory(Player player, String loca) {
        FakeInventory inv;
        Map<Integer, Item> items = new LinkedHashMap<>();
        int maxSize = this.fixedItem.size() + this.randomItemInfo.maxCount;
        final Item AIR = Item.fromString("minecraft:air");
        if (maxSize > 54) {
            ResourceChestMain.getInstance().getLogger().warning(chestName + " 的物品数量大于54！请减少。");
            return false;
        } else if (maxSize > 27) {
            inv = new FakeInventory(InventoryType.DOUBLE_CHEST);
            for (int i = 0; i < 54; i++) {
                items.put(i, AIR);
            }
        } else {
            inv = new FakeInventory(InventoryType.CHEST);
            for (int i = 0; i < 27; i++) {
                items.put(i, AIR);
            }
        }

        for (int i = 0; i < fixedItem.size(); i++) {
            items.put(i, Utils.parseItemString(fixedItem.get(i), player.getLanguageCode()));
        }

        int randomMaxCount = this.randomItemInfo.maxCount;
        List<String> randomLists = this.randomItem;

        while (randomMaxCount > 0) {
            if (this.randomItemInfo.noRepetition && randomLists.isEmpty()) {
                break;
            }

            String randomItemConfig;
            if (this.randomItemInfo.noRepetition) {
                randomItemConfig = randomLists.remove(ThreadLocalRandom.current().nextInt(randomLists.size()));
            } else {
                randomItemConfig = randomLists.get(ThreadLocalRandom.current().nextInt(randomLists.size()));
            }

            Item item = Utils.parseItemString(randomItemConfig, player.getLanguageCode());
            randomMaxCount--;
            if (item == null) continue;

            boolean isReplace = false;
            for (Map.Entry<Integer, Item> entry : items.entrySet()) {
                if (entry.getValue().equals(AIR)) continue;
                items.put(entry.getKey(), item);
                isReplace = true;
                break;
            }
            if (!isReplace) {
                break;
            }
        }

        String name = ResourceChestMain.getI18n().tr(player.getLanguageCode(), "resourcechest.title", this.chestName);
        if (!name.isEmpty()) {
            inv.setTitle(name);
        }

        inv.setContents(items);
        inv.setDefaultItemHandler((item, event) -> {
            for (InventoryAction action : event.getTransaction().getActions()) {
                if (action instanceof SlotChangeAction slotChange) {
                    if (slotChange.getInventory() instanceof FakeInventory) {
                        //Item sourceItem = action.getSourceItem();
                        Item targetItem = action.getTargetItem();
                        if (targetItem.isNull() || !item.equals(targetItem, true, true)) {

                            //if (this.canPutItem) return;
                            ChestOpenState state = PlayerConfig.PlayerOpenChestStateMap.get(player);
                            if (state.hasTake) return;
                            state.setHasTake(true);

                            // 为 player 记录开箱子的次数
                            PlayerConfig pConfig = PlayerConfig.existPlayerConfig(player.getName());
                            ChestLocationData locaData = pConfig.getPlayerData(chestName, state.loca);
                            pConfig.setPlayerData(chestName, state.loca, locaData.getCount() + 1, Utils.getNowTime());
                        }
                        break;
                    }
                }
            }
        });
        player.getLevel().addSound(player, Sound.RANDOM_CHESTOPEN);
        player.addWindow(inv);
        PlayerConfig.PlayerOpenChestStateMap.put(player, new ChestOpenState(chestName, loca));
        return true;
    }

}