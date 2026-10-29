/*
 * Decompiled with CFR 0.152.
 */
package ai.events;

import l2.commons.math.random.RndSelector;
import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.tables.SkillTable;

public class SpecialTree
extends DefaultAI {
    private static final RndSelector<Integer> dP = new RndSelector(5);
    private boolean dQ = false;
    private int _timer = 0;

    public SpecialTree(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        if (this.dQ) {
            ++this._timer;
            if (this._timer >= 180) {
                this._timer = 0;
                NpcInstance npcInstance = this.getActor();
                if (npcInstance == null) {
                    return false;
                }
                this.addTaskBuff(npcInstance, SkillTable.getInstance().getInfo(2139, 1));
                if (Rnd.chance(33)) {
                    npcInstance.broadcastPacketToOthers(new MagicSkillUse(npcInstance, npcInstance, dP.select(), 1, 500, 0L));
                }
            }
        }
        return super.thinkActive();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        this.dQ = !this.getActor().isInZonePeace();
        this._timer = 0;
    }

    static {
        dP.add(2140, 20);
        dP.add(2142, 20);
        dP.add(2145, 20);
        dP.add(2147, 20);
        dP.add(2149, 20);
    }
}

