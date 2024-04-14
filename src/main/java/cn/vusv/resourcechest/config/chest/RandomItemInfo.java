package cn.vusv.resourcechest.config.chest;

import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RandomItemInfo {
    public int maxCount;

    public boolean noRepetition;
    public RandomItemInfo(ConfigSection cfg){
        this.maxCount = cfg.getInt("maxCount", 1);
        this.noRepetition = cfg.getBoolean("noRepetition", true);
    }
}
