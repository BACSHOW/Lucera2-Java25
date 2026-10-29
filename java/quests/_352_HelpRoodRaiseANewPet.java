/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _352_HelpRoodRaiseANewPet
extends Quest
implements ScriptFile {
    private static int bqv = 31067;
    private static int bqw = 20786;
    private static int bqx = 20787;
    private static int bqy = 5860;
    private static int bqz = 5861;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _352_HelpRoodRaiseANewPet() {
        super(0);
        this.addStartNpc(bqv);
        this.addKillId(bqw, bqx);
        this.addQuestItem(bqy, bqz);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == bqv) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("how_about_new_pet", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "pet_manager_rood_q0352_05.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "pet_manager_rood_q0352_09.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "pet_manager_rood_q0352_10.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                questState.unset("how_about_new_pet");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "pet_manager_rood_q0352_11.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "pet_manager_rood_q0352_04.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("how_about_new_pet");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != bqv) break;
                if (questState.getPlayer().getLevel() >= 39) {
                    string = "pet_manager_rood_q0352_02.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "pet_manager_rood_q0352_01.htm";
                break;
            }
            case 2: {
                if (n2 != bqv || n != 1) break;
                if (questState.getQuestItemsCount(bqy) < 1L && questState.getQuestItemsCount(bqz) < 1L) {
                    string = "pet_manager_rood_q0352_06.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bqy) >= 1L && questState.getQuestItemsCount(bqz) < 1L) {
                    if (questState.getQuestItemsCount(bqy) >= 10L) {
                        questState.giveItems(57, questState.getQuestItemsCount(bqy) * 34L + 4000L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(bqy) * 34L + 2000L);
                    }
                    questState.takeItems(bqy, -1L);
                    string = "pet_manager_rood_q0352_07.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bqz) < 1L) break;
                questState.giveItems(57, 4000L + (questState.getQuestItemsCount(bqy) * 34L + questState.getQuestItemsCount(bqz) * 1025L));
                questState.takeItems(bqz, -1L);
                questState.takeItems(bqy, -1L);
                string = "pet_manager_rood_q0352_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("how_about_new_pet");
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 == bqw) {
                int n3 = Rnd.get(100);
                if (n3 < 46) {
                    questState.giveItems(bqy, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n3 < 48) {
                    questState.giveItems(bqz, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == bqx) {
                int n4 = Rnd.get(100);
                if (n4 < 69) {
                    questState.giveItems(bqy, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n4 < 71) {
                    questState.giveItems(bqz, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

