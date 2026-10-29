/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.templates.npc.NpcTemplate;
import quests._234_FatesWhisper;
import quests._235_MimirsElixir;

public class SubclassQuestBossInstance
extends RaidBossInstance {
    public SubclassQuestBossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDeath(Creature creature) {
        super.onDeath(creature);
        if (creature != null && creature.isPlayable()) {
            Player player = creature.getPlayer();
            if (player.isDead() || player.getParty() == null) {
                return;
            }
            Party party = player.getParty();
            for (Player player2 : party) {
                QuestState questState = player2.getQuestState(_235_MimirsElixir.class);
                QuestState questState2 = player2.getQuestState(_234_FatesWhisper.class);
                if (player2 == null || player2.isDead() || player2.getLevel() < 76 || this.getDistance3D(player2) > (double)Config.ALT_PARTY_DISTRIBUTION_RANGE) continue;
                questState2.setState(3);
                questState2.playSound("ItemSound.quest_finish");
                questState.setState(3);
                questState.playSound("ItemSound.quest_finish");
            }
        }
    }
}

