/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _345_MethodToRaiseTheDead
extends Quest
implements ScriptFile {
    private static final int bnp = 30970;
    private static final int bnq = 30971;
    private static final int bnr = 30912;
    private static final int bns = 30973;
    private static final int bnt = 20789;
    private static final int bnu = 20791;
    private static final int bnv = 4274;
    private static final int bnw = 4275;
    private static final int bnx = 4276;
    private static final int bny = 4277;
    private static final int bnz = 4278;
    private static final int bnA = 4280;
    private static final int bnB = 4281;
    private static final int bnC = 4407;
    private static final int biY = 3456;

    public _345_MethodToRaiseTheDead() {
        super(0);
        this.addStartNpc(30970);
        this.addTalkId(30970, 30912, 30973, 30971);
        this.addQuestItem(4274, 4275, 4276, 4277, 4278, 4281);
        this.addKillId(20789, 20791);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("how_to_face_the_dead");
        int n2 = questState.getInt("how_to_face_the_dead_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 30970) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "dorothy_the_locksmith_q0345_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "dorothy_the_locksmith_q0345_04.htm";
                questState.set("how_to_face_the_dead", String.valueOf(1), true);
            } else if (string.equalsIgnoreCase("reply_2") && n == 1 && questState.getQuestItemsCount(4274) >= 1L && questState.getQuestItemsCount(4275) >= 1L && questState.getQuestItemsCount(4276) >= 1L && questState.getQuestItemsCount(4277) >= 1L && questState.getQuestItemsCount(4278) >= 1L) {
                questState.setCond(2);
                questState.set("how_to_face_the_dead", String.valueOf(2), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "dorothy_the_locksmith_q0345_07.htm";
            }
        } else if (n3 == 30912) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "magister_xenovia_q0345_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 2) {
                    if (questState.getQuestItemsCount(57) >= 1000L) {
                        questState.setCond(3);
                        questState.set("how_to_face_the_dead", String.valueOf(3), true);
                        questState.giveItems(4281, 1L);
                        questState.takeItems(57, 1000L);
                        questState.playSound("ItemSound.quest_middle");
                        string2 = "magister_xenovia_q0345_03.htm";
                    } else {
                        string2 = "magister_xenovia_q0345_04.htm";
                    }
                }
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "magister_xenovia_q0345_06.htm";
            }
        } else if (n3 == 30973) {
            if (string.equalsIgnoreCase("reply_1")) {
                if (n2 == 1) {
                    string2 = "medium_jar_q0345_03.htm";
                } else if (n2 == 2) {
                    string2 = "medium_jar_q0345_05.htm";
                } else if (n2 == 3) {
                    string2 = "medium_jar_q0345_07.htm";
                }
            }
            if (string.equalsIgnoreCase("reply_2") && n == 7 && n2 == 1) {
                questState.setCond(6);
                questState.set("how_to_face_the_dead", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "medium_jar_q0345_04.htm";
            }
            if (string.equalsIgnoreCase("reply_3") && n == 7 && n2 == 2) {
                questState.setCond(6);
                questState.set("how_to_face_the_dead", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "medium_jar_q0345_06.htm";
            }
            if (string.equalsIgnoreCase("reply_4") && n == 7 && n2 == 3) {
                questState.setCond(7);
                questState.set("how_to_face_the_dead", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "medium_jar_q0345_08.htm";
            }
        } else if (n3 == 30971) {
            if (string.equalsIgnoreCase("reply_4")) {
                string2 = "mad_doctor_orpheus_q0345_10.htm";
            } else if (string.equalsIgnoreCase("reply_5") && questState.getQuestItemsCount(4280) > 0L) {
                questState.giveItems(57, questState.getQuestItemsCount(4280) * 104L);
                questState.takeItems(4280, -1L);
                string2 = "mad_doctor_orpheus_q0345_11.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("how_to_face_the_dead");
        int n2 = questState.getInt("how_to_face_the_dead_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 30970) break;
                if (questState.getPlayer().getLevel() < 35 || questState.getPlayer().getLevel() > 42) {
                    string = "dorothy_the_locksmith_q0345_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "dorothy_the_locksmith_q0345_02.htm";
                break;
            }
            case 2: {
                if (n3 == 30970) {
                    if (n == 0) {
                        string = "dorothy_the_locksmith_q0345_04.htm";
                        questState.set("how_to_face_the_dead", String.valueOf(1), true);
                        break;
                    }
                    if (n == 1 && (questState.getQuestItemsCount(4274) == 0L || questState.getQuestItemsCount(4275) == 0L || questState.getQuestItemsCount(4276) == 0L || questState.getQuestItemsCount(4277) == 0L || questState.getQuestItemsCount(4278) == 0L)) {
                        string = "dorothy_the_locksmith_q0345_05.htm";
                        break;
                    }
                    if (n == 1 && questState.getQuestItemsCount(4274) >= 1L && questState.getQuestItemsCount(4275) >= 1L && questState.getQuestItemsCount(4276) >= 1L && questState.getQuestItemsCount(4277) >= 1L && questState.getQuestItemsCount(4278) >= 1L) {
                        string = "dorothy_the_locksmith_q0345_06.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "dorothy_the_locksmith_q0345_08.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "dorothy_the_locksmith_q0345_09.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "dorothy_the_locksmith_q0345_12.htm";
                        break;
                    }
                    if (n == 8 && (n2 == 1 || n2 == 2)) {
                        questState.giveItems(4407, 3L);
                        questState.giveItems(57, 5390L + 70L * questState.getQuestItemsCount(4280));
                        questState.takeItems(4280, -1L);
                        questState.unset("how_to_face_the_dead");
                        questState.unset("how_to_face_the_dead_ex");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                        string = "dorothy_the_locksmith_q0345_13.htm";
                        break;
                    }
                    if (n != 8 || n2 != 3) break;
                    int n5 = Rnd.get(100);
                    if (n5 <= 92) {
                        questState.giveItems(4407, 5L);
                    } else {
                        questState.giveItems(3456, 1L);
                    }
                    questState.giveItems(57, 3040L + 70L * questState.getQuestItemsCount(4280));
                    questState.takeItems(4280, -1L);
                    questState.unset("how_to_face_the_dead");
                    questState.unset("how_to_face_the_dead_ex");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string = "dorothy_the_locksmith_q0345_14.htm";
                    break;
                }
                if (n3 == 30912) {
                    if (n == 2) {
                        string = "magister_xenovia_q0345_01.htm";
                        break;
                    }
                    if (n != 7 && n != 8 && questState.getQuestItemsCount(4281) < 1L) break;
                    string = "magister_xenovia_q0345_07.htm";
                    break;
                }
                if (n3 == 30973) {
                    if (n == 3) {
                        questState.takeItems(4281, -1L);
                        questState.takeItems(4274, -1L);
                        questState.takeItems(4275, -1L);
                        questState.takeItems(4276, -1L);
                        questState.takeItems(4277, -1L);
                        questState.takeItems(4278, -1L);
                        questState.set("how_to_face_the_dead", String.valueOf(7), true);
                        int n6 = Rnd.get(100);
                        if (n6 <= 39) {
                            questState.set("how_to_face_the_dead_ex", String.valueOf(1), true);
                        } else if (n6 <= 79) {
                            questState.set("how_to_face_the_dead_ex", String.valueOf(2), true);
                        } else {
                            questState.set("how_to_face_the_dead_ex", String.valueOf(3), true);
                        }
                        string = "medium_jar_q0345_01.htm";
                        break;
                    }
                    if (n == 7 && n2 == 1) {
                        string = "medium_jar_q0345_03t.htm";
                        break;
                    }
                    if (n == 7 && n2 == 2) {
                        string = "medium_jar_q0345_05t.htm";
                        break;
                    }
                    if (n == 7 && n2 == 3) {
                        string = "medium_jar_q0345_07t.htm";
                        break;
                    }
                    if (n != 8) break;
                    string = "medium_jar_q0345_09.htm";
                    break;
                }
                if (n3 != 30971 || questState.getQuestItemsCount(4280) <= 0L) break;
                string = "mad_doctor_orpheus_q0345_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("how_to_face_the_dead");
        int n2 = npcInstance.getNpcId();
        if (n == 1 && (n2 == 20789 || n2 == 20791)) {
            int n3 = Rnd.get(100);
            if (n3 <= 5) {
                if (questState.getQuestItemsCount(4274) == 0L) {
                    questState.giveItems(4274, 1L);
                } else {
                    questState.giveItems(4280, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            } else if (n3 <= 11) {
                if (questState.getQuestItemsCount(4275) == 0L) {
                    questState.giveItems(4275, 1L);
                } else {
                    questState.giveItems(4280, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            } else if (n3 <= 17) {
                if (questState.getQuestItemsCount(4276) == 0L) {
                    questState.giveItems(4276, 1L);
                } else {
                    questState.giveItems(4280, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            } else if (n3 <= 23) {
                if (questState.getQuestItemsCount(4277) == 0L) {
                    questState.giveItems(4277, 1L);
                } else {
                    questState.giveItems(4280, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            } else if (n3 <= 29) {
                if (questState.getQuestItemsCount(4278) == 0L) {
                    questState.giveItems(4278, 1L);
                } else {
                    questState.giveItems(4280, 1L);
                }
                questState.playSound("ItemSound.quest_itemget");
            } else if (n3 <= 60) {
                questState.giveItems(4280, 1L);
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

