/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;

public class AttackMobNotPlayerFighter
extends Fighter {
    public AttackMobNotPlayerFighter(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        List<QuestState> list;
        NpcInstance npcInstance = this.getActor();
        if (creature == null) {
            return;
        }
        Player player = creature.getPlayer();
        if (player != null && (list = player.getQuestsForEvent(npcInstance, QuestEventType.ATTACKED_WITH_QUEST)) != null) {
            for (QuestState questState : list) {
                questState.getQuest().notifyAttack(npcInstance, questState);
            }
        }
        this.onEvtAggression(creature, n);
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature == null) {
            return;
        }
        if (!npcInstance.isRunning()) {
            this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
        }
        if (this.getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
        }
    }
}

