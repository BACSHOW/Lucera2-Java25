/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _646_SignsOfRevolt
extends Quest
implements ScriptFile {
    private static int aEN = 32016;
    private static int bTz = 22029;
    private static int bTA = 22044;
    private static int bTB = 22047;
    private static int bTC = 22049;
    private static int bTD = 1880;
    private static int bTE = 1881;
    private static int bTF = 1882;
    private static int bTG = 8087;
    private static int bTH = 75;

    public _646_SignsOfRevolt() {
        super(0);
        this.addStartNpc(aEN);
        int n = bTz;
        while (n <= bTA) {
            this.addKillId(n++);
        }
        this.addKillId(bTB);
        this.addKillId(bTC);
        this.addQuestItem(bTG);
    }

    private static String d(QuestState questState, int n, int n2) {
        if (questState.getQuestItemsCount(bTG) < 180L) {
            return null;
        }
        questState.takeItems(bTG, -1L);
        questState.giveItems(n, n2, true);
        questState.playSound("ItemSound.quest_finish");
        questState.exitCurrentQuest(true);
        return "torant_q0646_0202.htm";
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("torant_q0646_0103.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else {
            if (string.equalsIgnoreCase("reward_adena") && n == 2) {
                return _646_SignsOfRevolt.d(questState, 57, 21600);
            }
            if (string.equalsIgnoreCase("reward_cbp") && n == 2) {
                return _646_SignsOfRevolt.d(questState, bTE, 12);
            }
            if (string.equalsIgnoreCase("reward_steel") && n == 2) {
                return _646_SignsOfRevolt.d(questState, bTD, 9);
            }
            if (string.equalsIgnoreCase("reward_leather") && n == 2) {
                return _646_SignsOfRevolt.d(questState, bTF, 20);
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != aEN) {
            return string;
        }
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getLevel() < 40) {
                string = "torant_q0646_0102.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "torant_q0646_0101.htm";
                questState.setCond(0);
            }
        } else if (n == 2) {
            string = questState.getQuestItemsCount(bTG) >= 180L ? "torant_q0646_0105.htm" : "torant_q0646_0106.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        Player player = questState.getRandomPartyMember(2, Config.ALT_PARTY_DISTRIBUTION_RANGE);
        if (player == null) {
            return null;
        }
        QuestState questState2 = player.getQuestState(questState.getQuest().getName());
        long l = questState2.getQuestItemsCount(bTG);
        if (l < 180L && Rnd.chance(bTH)) {
            questState2.giveItems(bTG, 1L);
            if (l == 179L) {
                questState2.playSound("ItemSound.quest_middle");
                questState2.setCond(2);
            } else {
                questState2.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

