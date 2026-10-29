/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _618_IntoTheFlame
extends Quest
implements ScriptFile {
    private static final int bMp = 31540;
    private static final int bMn = 31271;
    private static final int bMq = 7265;
    private static final int bMr = 7266;
    private static final int FLOATING_STONE = 7267;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _618_IntoTheFlame() {
        super(1);
        this.addStartNpc(31540);
        this.addTalkId(31271);
        this.addKillId(21274, 21275, 21276, 21278);
        this.addKillId(21282, 21283, 21284, 21286);
        this.addKillId(21290, 21291, 21292, 21294);
        this.addQuestItem(7265);
        this.addQuestItem(7266);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        if (string.equalsIgnoreCase("watcher_valakas_klein_q0618_0104.htm") && n == 0) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("watcher_valakas_klein_q0618_0401.htm")) {
            if (questState.getQuestItemsCount(7266) > 0L && n == 4) {
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                questState.giveItems(7267, 1L);
            } else {
                string2 = "watcher_valakas_klein_q0618_0104.htm";
            }
        } else if (string.equalsIgnoreCase("blacksmith_hilda_q0618_0201.htm") && n == 1) {
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("blacksmith_hilda_q0618_0301.htm")) {
            if (n == 3 && questState.getQuestItemsCount(7265) == 50L) {
                questState.takeItems(7265, -1L);
                questState.giveItems(7266, 1L);
                questState.setCond(4);
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "blacksmith_hilda_q0618_0203.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 31540) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() < 60) {
                    string = "watcher_valakas_klein_q0618_0103.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "watcher_valakas_klein_q0618_0101.htm";
                }
            } else {
                string = n2 == 4 && questState.getQuestItemsCount(7266) > 0L ? "watcher_valakas_klein_q0618_0301.htm" : "watcher_valakas_klein_q0618_0104.htm";
            }
        } else if (n == 31271) {
            string = n2 == 1 ? "blacksmith_hilda_q0618_0101.htm" : (n2 == 3 && questState.getQuestItemsCount(7265) >= 50L ? "blacksmith_hilda_q0618_0202.htm" : (n2 == 4 ? "blacksmith_hilda_q0618_0303.htm" : "blacksmith_hilda_q0618_0203.htm"));
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        long l = questState.getQuestItemsCount(7265);
        if (questState.getCond() == 2 && Rnd.chance(37) && l < 50L) {
            questState.giveItems(7265, 1L);
            if (l == 49L) {
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

