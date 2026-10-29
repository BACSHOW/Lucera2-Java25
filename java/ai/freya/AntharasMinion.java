/*
 * Decompiled with CFR 0.152.
 */
package ai.freya;

import bosses.AntharasManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;

public class AntharasMinion
extends Fighter {
    public AntharasMinion(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startDebuffImmunity();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        for (Player player : AntharasManager.getZone().getInsidePlayers()) {
            this.notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 5000);
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.getActor().doCast(SkillTable.getInstance().getInfo(5097, 1), this.getActor(), true);
        super.onEvtDead(creature);
    }

    @Override
    protected void returnHome(boolean bl, boolean bl2) {
    }
}

