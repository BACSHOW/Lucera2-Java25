/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _419_GetaPet
extends Quest
implements ScriptFile {
    private static final int bAJ = 30731;
    private static final int bAK = 30256;
    private static final int bAL = 30091;
    private static final int bAM = 30072;
    private static final int bAN = 20103;
    private static final int bAO = 20106;
    private static final int bAP = 20108;
    private static final int bAQ = 20460;
    private static final int bAR = 20308;
    private static final int bAS = 20466;
    private static final int bAT = 20025;
    private static final int bAU = 20105;
    private static final int bAV = 20034;
    private static final int bAW = 20474;
    private static final int bAX = 20476;
    private static final int bAY = 20478;
    private static final int bAZ = 20403;
    private static final int bBa = 20508;
    private static final int bBb = 22244;
    private static final int bBc = 50;
    private static final int bBd = 3417;
    private static final int bBe = 3418;
    private static final int bBf = 3419;
    private static final int bBg = 3420;
    private static final int bBh = 3421;
    private static final int bBi = 3422;
    private static final int bBj = 3423;
    private static final int bBk = 3424;
    private static final int bBl = 3425;
    private static final int bBm = 3426;
    private static final int bBn = 3427;
    private static final int bBo = 2375;
    private static final int bBp = 25;
    private static final int bBq = 3;
    private static final int bBr = 4;
    private static final int bBs = 10;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 0, 20103, 3418, 3423, 50, 100, 1}, {1, 0, 20106, 3418, 3423, 50, 100, 1}, {1, 0, 20108, 3418, 3423, 50, 100, 1}, {1, 0, 20460, 3419, 3424, 50, 100, 1}, {1, 0, 20308, 3419, 3424, 50, 100, 1}, {1, 0, 20466, 3419, 3424, 50, 100, 1}, {1, 0, 20025, 3420, 3425, 50, 100, 1}, {1, 0, 20105, 3420, 3425, 50, 100, 1}, {1, 0, 20034, 3420, 3425, 50, 100, 1}, {1, 0, 20474, 3421, 3426, 50, 100, 1}, {1, 0, 20476, 3421, 3426, 50, 100, 1}, {1, 0, 20478, 3421, 3426, 50, 100, 1}, {1, 0, 20403, 3422, 3427, 50, 100, 1}, {1, 0, 20508, 3422, 3427, 50, 100, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _419_GetaPet() {
        super(0);
        this.addStartNpc(30731);
        this.addTalkId(30731);
        this.addTalkId(30256);
        this.addTalkId(30091);
        this.addTalkId(30072);
        this.addQuestItem(3417);
        this.addQuestItem(3419);
        this.addQuestItem(3420);
        this.addQuestItem(3421);
        this.addQuestItem(3422);
        this.addQuestItem(3423);
        this.addQuestItem(3424);
        this.addQuestItem(3425);
        this.addQuestItem(3426);
        this.addQuestItem(3427);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
    }

    public long getCount_proof(QuestState questState) {
        long l = 0L;
        switch (questState.getPlayer().getRace()) {
            case human: {
                l = questState.getQuestItemsCount(3423);
                break;
            }
            case elf: {
                l = questState.getQuestItemsCount(3424);
                break;
            }
            case darkelf: {
                l = questState.getQuestItemsCount(3425);
                break;
            }
            case orc: {
                l = questState.getQuestItemsCount(3426);
                break;
            }
            case dwarf: {
                l = questState.getQuestItemsCount(3427);
            }
        }
        return l;
    }

    public String check_questions(QuestState questState) {
        String string = "";
        int n = questState.getInt("answers");
        int n2 = questState.getInt("question");
        if (n2 > 0) {
            string = "419_q" + String.valueOf(n2) + ".htm";
        } else if (n < 10) {
            String[] stringArray = questState.get("quiz").toString().split(" ");
            int n3 = Rnd.get(stringArray.length);
            String string2 = stringArray[n3];
            questState.set("question", string2);
            String string3 = "";
            if (n3 + 1 == stringArray.length) {
                for (int i = 0; i < stringArray.length - 2; ++i) {
                    string3 = string3 + stringArray[i] + " ";
                }
                string3 = string3 + stringArray[stringArray.length - 2];
            } else {
                for (int i = 0; i < stringArray.length - 1; ++i) {
                    if (i == n3) continue;
                    string3 = string3 + stringArray[i] + " ";
                }
                string3 = string3 + stringArray[stringArray.length - 1];
            }
            questState.set("quiz", string3);
            string = "419_q" + string2 + ".htm";
        } else {
            questState.giveItems(2375, 1L);
            questState.playSound("ItemSound.quest_finish");
            string = "Completed.htm";
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("id");
        if (string.equalsIgnoreCase("details")) {
            string2 = "419_confirm.htm";
        } else if (string.equalsIgnoreCase("agree")) {
            questState.setState(2);
            questState.setCond(1);
            switch (questState.getPlayer().getRace()) {
                case human: {
                    questState.giveItems(3418, 1L);
                    string2 = "419_slay_0.htm";
                    break;
                }
                case elf: {
                    questState.giveItems(3419, 1L);
                    string2 = "419_slay_1.htm";
                    break;
                }
                case darkelf: {
                    questState.giveItems(3420, 1L);
                    string2 = "419_slay_2.htm";
                    break;
                }
                case orc: {
                    questState.giveItems(3421, 1L);
                    string2 = "419_slay_3.htm";
                    break;
                }
                case dwarf: {
                    questState.giveItems(3422, 1L);
                    string2 = "419_slay_4.htm";
                }
            }
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("disagree")) {
            string2 = "419_cancelled.htm";
            questState.exitCurrentQuest(true);
        } else if (n == 1) {
            if (string.equalsIgnoreCase("talk")) {
                string2 = "419_talk.htm";
            } else if (string.equalsIgnoreCase("talk1")) {
                string2 = "419_bella_2.htm";
            } else if (string.equalsIgnoreCase("talk2")) {
                questState.set("progress", String.valueOf(questState.getInt("progress") | 1));
                string2 = "419_bella_3.htm";
            } else if (string.equalsIgnoreCase("talk3")) {
                questState.set("progress", String.valueOf(questState.getInt("progress") | 2));
                string2 = "419_ellie_2.htm";
            } else if (string.equalsIgnoreCase("talk4")) {
                questState.set("progress", String.valueOf(questState.getInt("progress") | 4));
                string2 = "419_metty_2.htm";
            }
        } else if (n == 2) {
            if (string.equalsIgnoreCase("tryme")) {
                string2 = this.check_questions(questState);
            } else if (string.equalsIgnoreCase("wrong")) {
                questState.set("id", "1");
                questState.set("progress", "0");
                questState.unset("quiz");
                questState.unset("answers");
                questState.unset("question");
                questState.giveItems(3417, 1L);
                string2 = "419_failed.htm";
            } else if (string.equalsIgnoreCase("right")) {
                questState.set("answers", String.valueOf(questState.getInt("answers") + 1));
                questState.set("question", "0");
                string2 = this.check_questions(questState);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getInt("id");
        int n3 = questState.getCond();
        if (n3 == 0) {
            if (n == 30731) {
                if (questState.getPlayer().getLevel() < 15) {
                    string = "419_low_level.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "Start.htm";
                }
            }
        } else if (n3 == 1) {
            if (n == 30731) {
                if (n2 == 0) {
                    long l = this.getCount_proof(questState);
                    if (l == 0L) {
                        string = "419_no_slay.htm";
                    } else if (l < 50L) {
                        string = "419_pending_slay.htm";
                    } else {
                        switch (questState.getPlayer().getRace()) {
                            case human: {
                                questState.takeItems(3418, -1L);
                                questState.takeItems(3423, -1L);
                                break;
                            }
                            case elf: {
                                questState.takeItems(3419, -1L);
                                questState.takeItems(3424, -1L);
                                break;
                            }
                            case darkelf: {
                                questState.takeItems(3420, -1L);
                                questState.takeItems(3425, -1L);
                                break;
                            }
                            case orc: {
                                questState.takeItems(3421, -1L);
                                questState.takeItems(3426, -1L);
                                break;
                            }
                            case dwarf: {
                                questState.takeItems(3422, -1L);
                                questState.takeItems(3427, -1L);
                            }
                        }
                        questState.set("id", "1");
                        questState.giveItems(3417, 1L);
                        string = "Slayed.htm";
                    }
                } else if (n2 == 1) {
                    if (questState.getInt("progress") == 7) {
                        questState.takeItems(3417, -1L);
                        questState.set("quiz", "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
                        questState.set("answers", "0");
                        questState.set("id", "2");
                        string = "Talked.htm";
                    } else {
                        string = "419_pending_talk.htm";
                    }
                } else if (n2 == 2) {
                    string = "Talked.htm";
                }
            } else if (n2 == 1) {
                if (n == 30256) {
                    string = "419_bella_1.htm";
                } else if (n == 30091) {
                    string = "419_ellie_1.htm";
                } else if (n == 30072) {
                    string = "419_metty_1.htm";
                }
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            if (n2 != DROPLIST_COND[i][0] || n != DROPLIST_COND[i][2] || DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(DROPLIST_COND[i][3]) <= 0L) continue;
            if (DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]) || DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        return null;
    }
}

