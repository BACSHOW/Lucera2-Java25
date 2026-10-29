/*
 * Decompiled with CFR 0.152.
 */
package ai.moveroute;

import ai.moveroute.MoveRouteDefaultAI;
import l2.gameserver.model.instances.NpcInstance;

public class Fighter
extends MoveRouteDefaultAI {
    public Fighter(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        return super.thinkActive() || this.defaultThinkBuff(2);
    }

    @Override
    protected boolean createNewTask() {
        return this.defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return 30;
    }

    @Override
    public int getRateDOT() {
        return 20;
    }

    @Override
    public int getRateDEBUFF() {
        return 20;
    }

    @Override
    public int getRateDAM() {
        return 15;
    }

    @Override
    public int getRateSTUN() {
        return 30;
    }

    @Override
    public int getRateBUFF() {
        return 10;
    }

    @Override
    public int getRateHEAL() {
        return 20;
    }
}

