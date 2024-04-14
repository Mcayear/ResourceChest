package cn.vusv.resourcechest.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.utils.TextFormat;
import cn.vusv.resourcechest.ResourceChestMain;
import cn.vusv.resourcechest.config.ChestConfig;
import cn.vusv.resourcechest.config.PlayerConfig;

public class RChestCommand extends Command {
    protected ResourceChestMain api;
    protected PluginI18n i18n;

    public RChestCommand(String name) {
        /*
        1.the name of the command must be lowercase
        2.Here the description is set in with the key in the language file,Look at en_US.lang or zh_CN.lang.
        This can send different command description to players of different language.
        You must extends PluginCommand to have this feature.
        */
        super(name, "资源箱命令");

        /*
         * The following begins to set the command parameters, first need to clean,
         * because NK will fill in several parameters by default, we do not need.
         * */
        this.getCommandParameters().clear();

        /*
         * 1.getCommandParameters return a Map<String,cn.nukkit.command.data.Com mandParameter[]>,
         * in which each entry can be regarded as a subcommand or a command pattern.
         * 2.Each subcommand cannot be repeated.
         * 3.Optional arguments must be used at the end of the subcommand or consecutively.
         */
        this.getCommandParameters().put("admin-rchest", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new String[]{"admin"})
        });
        this.getCommandParameters().put("reload-rchest", new CommandParameter[]{
                CommandParameter.newEnum("reload", false, new String[]{"reload"})
        });
        this.getCommandParameters().put("create-rchest", new CommandParameter[]{
                CommandParameter.newEnum("create", false, new String[]{"create"}),
                CommandParameter.newType("chestName", false, CommandParamType.STRING)
        });
        this.getCommandParameters().put("place-rchest", new CommandParameter[]{
                CommandParameter.newEnum("place", false, new String[]{"place"}),
                CommandParameter.newType("chestName", false, CommandParamType.STRING)
        });
        api = ResourceChestMain.getInstance();
        i18n = ResourceChestMain.getI18n();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        LangCode langCode = sender.isPlayer() ? ((Player)sender).getLanguageCode() : LangCode.zh_CN;

        if (args.length < 1) {
            sender.sendMessage("缺少参数");
            return false;
        }
        switch (args[0]) {
            case "admin" -> {
                if (!sender.isPlayer()) {
                    sender.sendMessage(i18n.tr(langCode, "command.error.admin_rchest_gui"));
                    return false;
                }
                sender.sendMessage(i18n.tr(langCode, "command.error.admin_rchest_not_completed"));
                return false;
            }
            case "reload" -> {
                ResourceChestMain.getInstance().init();
                sender.sendMessage(i18n.tr(langCode, "command.success.reload_rchest"));
                return true;
            }
            case "create" -> {
                String chestName = args[1];
                if (ChestConfig.createChestConfig(chestName)) {
                    sender.sendMessage(i18n.tr(langCode, "command.success.create_rchest_success", chestName));
                    return true;
                } else {
                    sender.sendMessage(i18n.tr(langCode, "command.error.create_rchest_fail", chestName));
                    return false;
                }
            }
            case "place" -> {
                if (!sender.isPlayer()) {
                    sender.sendMessage(i18n.tr(langCode, "command.error.place_rchest_player_only"));
                    return false;
                }
                String chestName = args[1];
                if (ChestConfig.existChestConfig(chestName)) {
                    PlayerConfig.PlayerPlaceStateMap.put((Player) sender, chestName);
                    sender.sendMessage(i18n.tr(langCode, "command.success.place_rchest_apply_config", chestName));
                    return true;
                } else {
                    sender.sendMessage(i18n.tr(langCode, "command.error.place_rchest_config_not_exist", chestName));
                    return false;
                }
            }
            default -> {
                return false;
            }
        }
    }
}
