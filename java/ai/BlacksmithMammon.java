/*
 * Decompiled with CFR 0.152.
 */
package ai;

import ai.MammonMerchant;
import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;

public class BlacksmithMammon
extends MammonMerchant {
    private long bh = 0L;
    private static final long bi = 1800000L;
    private static final Location[] bj = new Location[]{new Location(12655, -248700, -9576), new Location(-20515, -251010, -8160), new Location(-53145, -250500, -7904), new Location(46288, 170096, -4979), new Location(-19378, 13264, -4899), new Location(140480, 79472, -5427)};
    private static final String[] bk = new String[]{"Rulers of the seal! I bring you wondrous gifts!", "Rulers of the seal! I have some excellent weapons to show you!", "I've been so busy lately, in addition to planning my trip!"};

    @Override
    protected Location getRndTeleportLoc() {
        Location location = bj[Rnd.get(bj.length)];
        return location;
    }

    public BlacksmithMammon(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        if (super.thinkActive()) {
            return true;
        }
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (this.bh + 1800000L < System.currentTimeMillis()) {
            this.bh = System.currentTimeMillis();
            Functions.npcShout(npcInstance, bk[Rnd.get(bk.length)]);
        }
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}

