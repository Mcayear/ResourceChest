package cn.vusv.resourcechest;

import cn.nukkit.scheduler.PluginTask;

/**
 * author: MagicDroidX
 * ResourceChestMain Project
 */
public class BroadcastPluginTask extends PluginTask<ResourceChestMain> {

    public BroadcastPluginTask(ResourceChestMain owner) {
        super(owner);
    }

    @Override
    public void onRun(int currentTick) {
        this.getOwner().getLogger().info("I've run on tick " + currentTick);
    }
}
