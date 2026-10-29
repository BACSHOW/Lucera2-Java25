/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Mystic;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import quests._024_InhabitantsOfTheForestOfTheDead;

public class Quest024Mystic
extends Mystic {
    public Quest024Mystic(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        Quest quest = QuestManager.getQuest(_024_InhabitantsOfTheForestOfTheDead.class);
        if (quest != null) {
            for (Player player : World.getAroundPlayers(this.getActor(), 300, 200)) {
                QuestState questState = player.getQuestState(_024_InhabitantsOfTheForestOfTheDead.class);
                if (questState == null || questState.getCond() != 3) continue;
                quest.notifyEvent("see_creature", questState, this.getActor());
            }
        }
        return super.thinkActive();
    }
}

