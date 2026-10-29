/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class MammonMerchant
extends DefaultAI {
    private static final Location[] bj = new Location[]{new Location(118336, 132592, -4829), new Location(111296, 173792, -5440), new Location(83136, 208992, -5437), new Location(45020, 123830, -5408), new Location(-21680, 77152, -5171), new Location(-52208, 78896, -4739), new Location(-41856, 209904, -5088), new Location(172384, -17823, -4897)};
    private static long ct = 1800000L;
    private long cu = System.currentTimeMillis();

    public MammonMerchant(NpcInstance npcInstance) {
        super(npcInstance);
    }

    protected Location getRndTeleportLoc() {
        Location location = bj[Rnd.get(bj.length)];
        return location;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (System.currentTimeMillis() - this.cu < ct) {
            return false;
        }
        Location location = this.getRndTeleportLoc();
        if (npcInstance.getLoc().equals(location)) {
            return false;
        }
        ThreadPoolManager.getInstance().schedule(this.new Teleport(location), 1000L);
        this.cu = System.currentTimeMillis();
        return true;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}

