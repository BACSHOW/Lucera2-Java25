/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.ScriptFile;

public class _510_AClansReputation
extends Quest
implements ScriptFile {
    private static final int bJw = 31331;
    private static final int bJx = 8767;
    private static final int bJy = 50;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _510_AClansReputation() {
        super(2);
        this.addStartNpc(31331);
        int n = 22215;
        while (n <= 22217) {
            this.addKillId(n++);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getCond();
        String string2 = string;
        if (string.equals("31331-3.htm")) {
            if (n == 0) {
                questState.setCond(1);
                questState.setState(2);
            }
        } else if (string.equals("31331-6.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        Player player = questState.getPlayer();
        Clan clan = player.getClan();
        if (player.getClan() == null || !player.isClanLeader()) {
            questState.exitCurrentQuest(true);
            string = "31331-0.htm";
        } else if (player.getClan().getLevel() < 5) {
            questState.exitCurrentQuest(true);
            string = "31331-0.htm";
        } else {
            int n = questState.getCond();
            int n2 = questState.getState();
            if (n2 == 1 && n == 0) {
                string = "31331-1.htm";
            } else if (n2 == 2 && n == 1) {
                long l = questState.getQuestItemsCount(8767);
                if (l == 0L) {
                    string = "31331-4.htm";
                } else if (l >= 1L) {
                    string = "31331-7.htm";
                    questState.takeItems(8767, -1L);
                    int n3 = 50 * (int)l * (int)Config.RATE_CLAN_REP_SCORE;
                    int n4 = clan.incReputation(n3, true, "_510_AClansReputation");
                    player.sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n4));
                }
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        if (!questState.getPlayer().isClanLeader()) {
            questState.exitCurrentQuest(true);
        } else if (questState.getState() == 2 && (n = npcInstance.getNpcId()) >= 22215 && n <= 22218) {
            questState.giveItems(8767, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

