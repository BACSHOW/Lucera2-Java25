/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.instances.NpcInstance;

public class Priest
extends DefaultAI {
    public Priest(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        return super.thinkActive() || this.defaultThinkBuff(10, 5);
    }

    @Override
    protected boolean createNewTask() {
        return this.defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return 10;
    }

    @Override
    public int getRateDOT() {
        return 15;
    }

    @Override
    public int getRateDEBUFF() {
        return 15;
    }

    @Override
    public int getRateDAM() {
        return 30;
    }

    @Override
    public int getRateSTUN() {
        return 3;
    }

    @Override
    public int getRateBUFF() {
        return 10;
    }

    @Override
    public int getRateHEAL() {
        return 40;
    }
}

