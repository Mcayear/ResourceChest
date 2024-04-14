package cn.vusv.resourcechest.config;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.vusv.resourcechest.ResourceChestMain;
import cn.vusv.resourcechest.Utils;
import cn.vusv.resourcechest.config.player.ChestLocationData;
import cn.vusv.resourcechest.config.player.ChestOpenState;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

public class PlayerConfig {
    public static LinkedHashMap<String, PlayerConfig> PlayerConfigMap;
    public static LinkedHashMap<Player, String> PlayerPlaceStateMap;
    public static LinkedHashMap<Player, ChestOpenState> PlayerOpenChestStateMap;
    public static void init() {
        PlayerConfigMap = new LinkedHashMap<>();
        PlayerPlaceStateMap = new LinkedHashMap<>();
        PlayerOpenChestStateMap = new LinkedHashMap<>();
        File[] files = new File(ResourceChestMain.getInstance().getDataFolder() + "/players").listFiles();

        for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().replace(".yml", "");
            PlayerConfigMap.put(fileName, new PlayerConfig(fileName, new Config(file, Config.YAML)));
        }
    }

    /**
     * 检查玩家配置，并创建
     * @param playerName
     * @return
     */
    public static PlayerConfig existPlayerConfig(String playerName) {
        if (!PlayerConfigMap.containsKey(playerName)) {
            Config cfg = new Config(
                    new File(ResourceChestMain.getInstance().getDataFolder() + "/players", playerName + ".yml"),
                    Config.YAML,
                    new ConfigSection()
            );
            cfg.save();
            PlayerConfigMap.put(playerName, new PlayerConfig(playerName, cfg));
        }
        return PlayerConfigMap.get(playerName);
    }


    public String playerName;
    public Config cfg;
    public PlayerConfig(String playerName, Config config) {
        this.playerName = playerName;
        this.cfg = config;
    }

    public boolean setPlayerData(String chestName, Position pos, int count, long time) {
        return setPlayerData(chestName, Utils.posToString(pos), count, time);
    }

    public boolean setPlayerData(String chestName, String loca, int count, long time) {
        if (!this.cfg.exists(chestName)) {
            this.cfg.set(chestName, new ConfigSection());
        }
        this.cfg.getSection(chestName).set(loca, List.of(count, time));
        this.cfg.save();
        return true;
    }
    /**
     * 获取指定箱子，指定位置的数据
     * @param chestName
     * @param pos
     * @return
     */
    public ChestLocationData getPlayerData(String chestName, Position pos) {
        return getPlayerData(chestName, Utils.posToString(pos));
    }

    /**
     * 获取指定箱子，指定位置的数据
     * @param chestName
     * @param loca
     * @return
     */
    public ChestLocationData getPlayerData(String chestName, String loca) {
        ConfigSection config = this.cfg.getSection(chestName);

        List<Object> values = config.getList(loca, List.of(0, 0L));
        return new ChestLocationData((int) values.get(0), (long) values.get(1));
    }
}