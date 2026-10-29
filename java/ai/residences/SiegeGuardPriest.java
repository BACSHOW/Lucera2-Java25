/*
 * Decompiled with CFR 0.152.
 */
package ai.residences;

import ai.residences.SiegeGuard;
import l2.gameserver.model.instances.NpcInstance;

public class SiegeGuardPriest
extends SiegeGuard {
    public SiegeGuardPriest(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean createNewTask() {
        return this.defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return this._damSkills.length == 0 ? 25 : 0;
    }

    @Override
    public int getRateDOT() {
        return 35;
    }

    @Override
    public int getRateDEBUFF() {
        return 50;
    }

    @Override
    public int getRateDAM() {
        return 60;
    }

    @Override
    public int getRateSTUN() {
        return 10;
    }

    @Override
    public int getRateBUFF() {
        return 25;
    }

    @Override
    public int getRateHEAL() {
        return 90;
    }
}

