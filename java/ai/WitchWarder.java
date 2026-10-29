/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.instances.NpcInstance;

public class WitchWarder
extends Fighter {
    private long bf = 0L;
    private boolean dm = false;
    private static final int ch = 180000;

    public WitchWarder(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (this._def_think) {
            this.doTask();
            this.dm = false;
            return true;
        }
        if (!this.dm) {
            this.dm = true;
            this.bf = System.currentTimeMillis() + 180000L;
        }
        if (this.bf != 0L && this.dm && this.bf < System.currentTimeMillis()) {
            npcInstance.deleteMe();
        }
        return super.thinkActive();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

