/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Earthquake;

public class BaiumNpc
extends DefaultAI {
    private long bf = 0L;
    private static final int bg = 900000;

    public BaiumNpc(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (this.bf < System.currentTimeMillis()) {
            this.bf = System.currentTimeMillis() + 900000L;
            Earthquake earthquake = new Earthquake(npcInstance.getLoc(), 40, 10);
            List<Creature> list = npcInstance.getAroundCharacters(5000, 10000);
            for (Creature creature : list) {
                if (!creature.isPlayer()) continue;
                creature.sendPacket((IStaticPacket)earthquake);
            }
        }
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

