/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class BossInstance
extends RaidBossInstance {
    private boolean TB;

    public BossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public final boolean isMovementDisabled() {
        return this.getNpcId() == 29006 || super.isMovementDisabled();
    }

    public void setTeleported(boolean bl) {
        this.TB = bl;
    }

    public boolean isTeleported() {
        return this.TB;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }
}

