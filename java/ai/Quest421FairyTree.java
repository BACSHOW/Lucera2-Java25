/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.tables.SkillTable;

public class Quest421FairyTree
extends Fighter {
    private static final Skill cB = SkillTable.getInstance().getInfo(4243, 1);

    public Quest421FairyTree(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature != null) {
            List<QuestState> list;
            Player player;
            if (creature.isPlayer() && Rnd.chance(29)) {
                cB.getEffects(npcInstance, creature, false, false);
            } else if (creature.isPet() && (player = creature.getPlayer()) != null && (list = player.getQuestsForEvent(npcInstance, QuestEventType.ATTACKED_WITH_QUEST)) != null) {
                for (QuestState questState : list) {
                    questState.getQuest().notifyAttack(npcInstance, questState);
                }
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

