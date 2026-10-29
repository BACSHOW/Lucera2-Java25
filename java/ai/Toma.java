/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.utils.Location;

public class Toma
extends DefaultAI {
    private Location[] db = new Location[]{new Location(151680, -174891, -1807, 41400), new Location(154153, -220105, -3402), new Location(178834, -184336, -352)};
    private static long ct = 1800000L;
    private long cu = System.currentTimeMillis();

    public Toma(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        if (System.currentTimeMillis() - this.cu < ct) {
            return false;
        }
        NpcInstance npcInstance = this.getActor();
        Location location = this.db[Rnd.get(this.db.length)];
        if (npcInstance.getLoc().equals(location)) {
            return false;
        }
        npcInstance.broadcastPacketToOthers(new MagicSkillUse(npcInstance, npcInstance, 4671, 1, 1000, 0L));
        ThreadPoolManager.getInstance().schedule(this.new Teleport(location), 1000L);
        this.cu = System.currentTimeMillis();
        return true;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}

