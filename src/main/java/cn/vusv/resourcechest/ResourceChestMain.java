package cn.vusv.resourcechest;

import cn.nukkit.Server;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.vusv.resourcechest.command.RChestCommand;
import cn.vusv.resourcechest.config.ChestConfig;
import cn.vusv.resourcechest.config.PlayerConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * author: MagicDroidX
 * NukkitExamplePlugin Project
 */
public class ResourceChestMain extends PluginBase {
    @Getter
    public static ResourceChestMain instance;
    @Getter
    public static PluginI18n i18n;


    @Override
    public void onLoad() {
        //save Plugin Instance
        instance = this;
        //register the plugin i18n
        i18n = PluginI18nManager.register(this);
        //register the command of plugin
        this.getServer().getCommandMap().register("ResourceChest", new RChestCommand("rchest"));
    }

    @Override
    public void onEnable() {
        //Register the EventListener
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        init();
    }

    public void init() {
        loadPlayerConfig();
        loadChestConfig();
    }

    private void loadPlayerConfig() {
        if (!new File(this.getDataFolder() + "/players").exists()) {
            this.getLogger().info("未检测到 players 文件夹，正在创建");
            if (!new File(this.getDataFolder() + "/players").mkdirs()) {
                this.getLogger().error("players 文件夹创建失败");
                return;
            } else {
                this.getLogger().info("players 文件夹创建完成，正在载入数据");
            }
        }
        PlayerConfig.init();
    }

    private void loadChestConfig() {
        if (!new File(this.getDataFolder() + "/chests").exists()) {
            this.getLogger().info("未检测到 chests 文件夹，正在创建");
            ResourceChestMain.getInstance().saveResource("chests.yml", "./chests/default.yml", false);
            if (!new File(this.getDataFolder() + "/chests").mkdirs()) {
                this.getLogger().error("chests 文件夹创建失败");
                return;
            } else {
                this.getLogger().info("chests 文件夹创建完成，正在载入数据");
            }
        }
        ChestConfig.init();
    }
    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.DARK_RED + "I've been disabled!");
    }
}


