/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class AndreasVanHalterInstance
extends RaidBossInstance {
    private static final String axr = "[guard_of_andreas]";
    private static final int[] axs = new int[]{19160016, 19160017};

    public AndreasVanHalterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        SpawnManager.getInstance().spawn(axr);
    }

    @Override
    protected void onDespawn() {
        super.onDespawn();
        SpawnManager.getInstance().despawn(axr);
        int[] nArray = axs;
        int n = nArray.length;
        for (int i = 0; i < n; ++i) {
            Integer n2 = nArray[i];
            DoorInstance doorInstance = this.getReflection().getDoor(n2);
            if (doorInstance == null) continue;
            doorInstance.openMe();
        }
    }
}

