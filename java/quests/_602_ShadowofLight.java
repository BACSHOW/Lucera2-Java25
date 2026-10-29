/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _602_ShadowofLight
extends Quest
implements ScriptFile {
    private static final int bJz = 31683;
    private static final int bJJ = 21299;
    private static final int bJK = 21304;
    private static final int bJG = 6698;
    private static final int bJF = 6699;
    private static final int bJH = 6700;
    private static final int bJL = 7189;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _602_ShadowofLight() {
        super(0);
        this.addStartNpc(31683);
        this.addKillId(21299, 21304);
        this.addQuestItem(7189);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("shadow_of_light", String.valueOf(11), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "eye_of_argos_q0602_0104.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (questState.getQuestItemsCount(7189) >= 100L) {
                int n = Rnd.get(1000);
                questState.takeItems(7189, 100L);
                if (n < 200) {
                    questState.giveItems(6699, 3L);
                    questState.giveItems(57, 40000L);
                    questState.addExpAndSp(120000L, 20000L);
                } else if (n < 400) {
                    questState.giveItems(6698, 3L);
                    questState.giveItems(57, 60000L);
                    questState.addExpAndSp(110000L, 15000L);
                } else if (n < 500) {
                    questState.giveItems(6700, 3L);
                    questState.giveItems(57, 40000L);
                    questState.addExpAndSp(150000L, 10000L);
                } else if (n < 1000) {
                    questState.giveItems(57, 100000L);
                    questState.addExpAndSp(140000L, 11250L);
                }
                questState.unset("shadow_of_light");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "eye_of_argos_q0602_0201.htm";
            } else {
                string2 = "eye_of_argos_q0602_0202.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("shadow_of_light");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31683) break;
                if (questState.getPlayer().getLevel() >= 68) {
                    string = "eye_of_argos_q0602_0101.htm";
                    break;
                }
                string = "eye_of_argos_q0602_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 31683 || n < 11 || n > 12) break;
                string = n == 12 && questState.getQuestItemsCount(7189) >= 100L ? "eye_of_argos_q0602_0105.htm" : "eye_of_argos_q0602_0106.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("shadow_of_light");
        int n2 = npcInstance.getNpcId();
        if (n == 11) {
            int n3;
            if (n2 == 21299) {
                int n4 = Rnd.get(1000);
                if (n4 < 560) {
                    if (questState.getQuestItemsCount(7189) + 1L >= 100L) {
                        questState.setCond(2);
                        questState.set("shadow_of_light", String.valueOf(12), true);
                        questState.giveItems(7189, 100L - questState.getQuestItemsCount(7189));
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.giveItems(7189, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21304 && (n3 = Rnd.get(1000)) < 800) {
                if (questState.getQuestItemsCount(7189) + 1L >= 100L) {
                    questState.setCond(2);
                    questState.set("shadow_of_light", String.valueOf(12), true);
                    questState.giveItems(7189, 100L - questState.getQuestItemsCount(7189));
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(7189, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

