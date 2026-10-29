/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;

public class EtisEtina
extends Fighter {
    private boolean bE = false;
    private NpcInstance bF;
    private NpcInstance bG;

    public EtisEtina(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.getCurrentHpPercents() < 70.0 && !this.bE) {
            this.bE = true;
            this.bF = NpcUtils.spawnSingle(18950, Location.findAroundPosition(npcInstance, 150), npcInstance.getReflection());
            this.bG = NpcUtils.spawnSingle(18951, Location.findAroundPosition(npcInstance, 150), npcInstance.getReflection());
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        if (this.bF != null && !this.bF.isDead()) {
            this.bF.decayMe();
        }
        if (this.bG != null && !this.bG.isDead()) {
            this.bG.decayMe();
        }
        super.onEvtDead(creature);
    }
}

