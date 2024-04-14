package cn.vusv.resourcechest.config.player;

import cn.nukkit.Player;
import lombok.Getter;
import lombok.Setter;

public class ChestOpenState {
    public String chestName;

    @Setter
    @Getter
    public boolean hasTake = false;
    @Setter
    @Getter
    public String loca;
    public ChestOpenState(String chestName, String loca) {
        this.chestName = chestName;
        this.loca = loca;
    }

}
