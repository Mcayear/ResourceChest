package cn.vusv.resourcechest.config.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChestLocationData {
    private int count;
    private long time;

    // 构造方法
    public ChestLocationData(int count, long time) {
        this.count = count;
        this.time = time;
    }
}