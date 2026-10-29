/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.tables.SkillTable;
import org.apache.commons.lang3.ArrayUtils;

public class SeducedInvestigator
extends Fighter {
    private int[] cL = new int[]{25659, 25660, 25661, 25662, 25663, 25664};
    private long cM = 0L;

    public SeducedInvestigator(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
        npcInstance.startHealBlocked();
        this.AI_TASK_ACTIVE_DELAY = 5000L;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return false;
        }
        List<NpcInstance> list = npcInstance.getAroundNpc(1000, 300);
        if (list != null && !list.isEmpty()) {
            for (NpcInstance creature : list) {
                if (!ArrayUtils.contains(this.cL, creature.getNpcId())) continue;
                npcInstance.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, creature, 300);
            }
        }
        if (Rnd.chance(0.1) && this.cM + 30000L < System.currentTimeMillis()) {
            List<Player> list2 = World.getAroundPlayers(npcInstance, 500, 200);
            if (list2 == null || list2.size() < 1) {
                return false;
            }
            Player player = (Player)list2.get(Rnd.get(list2.size()));
            if (player.getReflectionId() == npcInstance.getReflectionId()) {
                this.cM = System.currentTimeMillis();
                int[] nArray = new int[]{5970, 5971, 5972, 5973};
                if (npcInstance.getNpcId() == 36562) {
                    npcInstance.doCast(SkillTable.getInstance().getInfo(nArray[0], 1), player, true);
                } else if (npcInstance.getNpcId() == 36563) {
                    npcInstance.doCast(SkillTable.getInstance().getInfo(nArray[1], 1), player, true);
                } else if (npcInstance.getNpcId() == 36564) {
                    npcInstance.doCast(SkillTable.getInstance().getInfo(nArray[2], 1), player, true);
                } else {
                    npcInstance.doCast(SkillTable.getInstance().getInfo(nArray[3], 1), player, true);
                }
            }
        }
        return true;
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        Reflection reflection = npcInstance.getReflection();
        List<Player> list = reflection.getPlayers();
        for (Player player : list) {
            player.sendPacket((IStaticPacket)new ExShowScreenMessage("The Investigator has been killed. The mission is failed.", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
        }
        reflection.startCollapseTimer(5000L);
        super.onEvtDead(creature);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature == null) {
            return;
        }
        if (creature.isPlayable()) {
            return;
        }
        if (creature.getNpcId() == 25659 || creature.getNpcId() == 25660 || creature.getNpcId() == 25661) {
            npcInstance.getAggroList().addDamageHate(creature, 0, 20);
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
        if (creature.isPlayer() || creature.isPet() || creature.isSummon()) {
            return;
        }
        super.onEvtAggression(creature, n);
    }

    @Override
    public boolean checkAggression(Creature creature) {
        if (creature.isPlayable()) {
            return false;
        }
        return super.checkAggression(creature);
    }
}

