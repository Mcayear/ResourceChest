package cn.vusv.resourcechest;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Sound;
import cn.vusv.resourcechest.config.ChestConfig;
import cn.vusv.resourcechest.config.PlayerConfig;
import cn.vusv.resourcechest.config.player.ChestLocationData;
import cn.vusv.resourcechest.config.player.ChestOpenState;

/**
 * author: Mcayear
 * NukkitExamplePlugin Project
 */
public class EventListener implements Listener {
    private final ResourceChestMain plugin;

    public EventListener(ResourceChestMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!PlayerConfig.PlayerPlaceStateMap.containsKey(player)) return;
        if (!event.getBlock().getName().endsWith("Chest")) return;
        String chestName = PlayerConfig.PlayerPlaceStateMap.get(player);
        ChestConfig.ChestConfigMap.get(chestName).placeChest(event.getBlock());
        PlayerConfig.PlayerPlaceStateMap.remove(player);
        player.sendMessage(ResourceChestMain.getI18n().tr(player.getLanguageCode(), "messages.successful_placement", chestName));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!event.getBlock().getName().endsWith("Chest")) return;
        String loca = Utils.posToString(event.getBlock());
        if (!ChestConfig.ChestLocationMap.containsKey(loca)) {
            return;
        }
        String chestName = ChestConfig.ChestLocationMap.get(loca);
        if (ChestConfig.ChestConfigMap.get(chestName).removeChest(loca)) {
            player.sendMessage(ResourceChestMain.getI18n().tr(player.getLanguageCode(), "messages.chest_removed", chestName));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onOpenChest(PlayerInteractEvent event) {
        if (!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getBlock().getName().endsWith("Chest")) return;
        Player player = event.getPlayer();
        String loca = Utils.posToString(event.getBlock());
        if (!ChestConfig.ChestLocationMap.containsKey(loca)) return;
        event.setCancelled(true);
        String chestName = ChestConfig.ChestLocationMap.get(loca);
        if (PlayerConfig.PlayerOpenChestStateMap.containsKey(player)) {
            ChestOpenState state = PlayerConfig.PlayerOpenChestStateMap.get(player);
            player.sendMessage(ResourceChestMain.getI18n().tr(player.getLanguageCode(), "messages.already_opened_chest", state.chestName));
        }

        PlayerConfig pConfig = PlayerConfig.existPlayerConfig(player.getName());
        ChestLocationData locaData = pConfig.getPlayerData(chestName, loca);
        int openCount = locaData.getCount();
        ChestConfig chestConfig = ChestConfig.ChestConfigMap.get(chestName);
        if (chestConfig.openCountMax > -1 && openCount >= chestConfig.openCountMax) {
            player.sendMessage(ResourceChestMain.getI18n().tr(player.getLanguageCode(), "messages.cannot_open_more", chestName));
            return;
        }

		int time_ = (int) ((locaData.getTime() + chestConfig.refreshInterval*1000 - Utils.getNowTime())/1000);
        if (time_ > 1) {
            player.sendMessage(ResourceChestMain.getI18n().tr(player.getLanguageCode(), "messages.wait_to_open", chestName, Utils.time(time_)));
            return;
        }
        chestConfig.sendInventory(player, loca);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCloseChest(InventoryCloseEvent event) {
        Player player = event.getPlayer();
        if (PlayerConfig.PlayerOpenChestStateMap.containsKey(player)) {
            PlayerConfig.PlayerOpenChestStateMap.remove(player);
            player.getLevel().addSound(player, Sound.RANDOM_CHESTCLOSED);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (PlayerConfig.PlayerOpenChestStateMap.containsKey(player)) {
            PlayerConfig.PlayerOpenChestStateMap.remove(player);
        }
    }
}
