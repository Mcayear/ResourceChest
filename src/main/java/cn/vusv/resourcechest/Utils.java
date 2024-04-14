package cn.vusv.resourcechest;
import RcRPG.RcRPGMain;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Ornament;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import RcTaskBook.books.Book;
import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.utils.Tools;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.lang.LangCode;
import cn.nukkit.level.Position;

import java.time.Instant;
import java.util.LinkedHashMap;

import static cn.ankele.plugin.utils.BaseCommand.createItem;
import static java.lang.Integer.parseInt;

public class Utils {
    public static long getNowTime() {
        Instant timestamp = Instant.now();
        long millis = timestamp.toEpochMilli();
        return millis;
    }
    public static String time(int seconds){
        int ss = seconds % 60;
        seconds /= 60;
        int min = seconds % 60;
        seconds /= 60;
        int hours = seconds % 24;
        return strzero(hours) + ":" + strzero(min) + ":" + strzero(ss);
    }
    private static String strzero(int time){
        if(time < 10)
            return "0" + time;
        return String.valueOf(time);
    }

    public static String posToString(Position pos) {
        return Math.round(pos.x)+" "+Math.round(pos.y)+" "+Math.round(pos.z)+" "+pos.level.getName();
    }
    public static Item parseItemString(String str, LangCode langCode) {
        if (str.isEmpty()) return Item.AIR_ITEM;
        String[] arr = str.split("@");
        if (arr[0].equals("mi")) {// mi@1 代金券
            if (Server.getInstance().getPluginManager().getPlugin("MagicItem") == null) {
                ResourceChestMain.getInstance().getLogger().warning("你没有使用 MagicItem 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            LinkedHashMap<String, ItemBean> items = MagicItem.getItemsMap();
            LinkedHashMap<String, Object> otherItems = MagicItem.getOthers();
            String[] args = arr[1].split(" ");
            if (items.containsKey(args[1])) {
                ItemBean item = items.get(args[1]);
                Item back = createItem(item);
                back.setCount(parseInt(args[0]));
                return back;
            } else if (otherItems.containsKey(args[1])) {
                String[] otherItemArr = ((String) otherItems.get(args[1])).split(":");
                Item item = Item.get(parseInt(otherItemArr[0]), parseInt(otherItemArr[1]));
                item.setCount(parseInt(args[0]));
                item.setCompoundTag(Tools.hexStringToBytes(otherItemArr[3]));
                return item;
            } else {
                ResourceChestMain.getInstance().getLogger().warning("MagicItem物品不存在：" + args[1]);
            }
        } else if (arr[0].equals("item")) {
            String[] args = arr[1].split(" ");
            Item item = Item.fromString(args[0]);
            item.setDamage(parseInt(args[1]));
            item.setCount(parseInt(args[2]));
            return item;
        } else if (arr[0].equals("nweapon") || arr[0].equals("rcrpg")) {
            if (Server.getInstance().getPluginManager().getPlugin("RcRPG") == null) {
                ResourceChestMain.getInstance().getLogger().warning("你没有使用 RcRPG 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            String[] args = arr[1].split(" ");//Main.loadWeapon
            String type = args[0];
            String itemName = args[1];
            int count = 1;

            if (args.length > 2) {
                count = parseInt(args[2]);
            }

            switch (type) {
                case "护甲", "防具", "armour", "armor" -> {
                    if (RcRPGMain.loadArmour.containsKey(itemName)) {
                        return Armour.getItem(itemName, count);
                    }
                }
                case "武器", "weapon" -> {
                    if (RcRPGMain.loadWeapon.containsKey(itemName)) {
                        return Weapon.getItem(itemName, count);
                    }
                }
                case "宝石", "stone", "gem" -> {
                    if (RcRPGMain.loadStone.containsKey(itemName)) {
                        return Stone.getItem(itemName, count);
                    }
                }
                case "饰品", "ornament", "jewelry" -> {
                    if (RcRPGMain.loadOrnament.containsKey(itemName)) {
                        return Ornament.getItem(itemName, count);
                    }
                }
                case "锻造图" -> {
                }
                case "宝石券", "精工石", "强化石", "锻造石" -> {
                }
            }
            return Item.AIR_ITEM;
        } else if (arr[0].equals("taskbook")) {
            if (Server.getInstance().getPluginManager().getPlugin("RcTaskBook") == null) {
                ResourceChestMain.getInstance().getLogger().warning("你没有使用 RcTaskBook 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            String[] args = arr[1].split(" ");
            return Book.getBook(langCode, args[1], Integer.parseInt(args[0]));
        } else {
            ResourceChestMain.getInstance().getLogger().warning("物品配置有误：" + str);
        }
        return Item.AIR_ITEM;
    }
    public static Item parseItemString(String str) {
        return parseItemString(str, LangCode.zh_CN);
    }


    public static int defaultVaule(int value) {
        if (value == 0) {
            return 1;
        }
        return value;
    }
}
