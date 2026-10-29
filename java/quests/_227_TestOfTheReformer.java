/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _227_TestOfTheReformer
extends Quest
implements ScriptFile {
    private static final int aUX = 30118;
    private static final int aUY = 30666;
    private static final int aUZ = 30668;
    private static final int aVa = 30732;
    private static final int aVb = 30669;
    private static final int aVc = 30670;
    private static final int aVd = 30667;
    private static final int aVe = 2822;
    private static final int aVf = 2823;
    private static final int aVg = 2824;
    private static final int aVh = 2825;
    private static final int aVi = 2826;
    private static final int aVj = 2827;
    private static final int aVk = 2828;
    private static final int aVl = 3037;
    private static final int aVm = 2829;
    private static final int aVn = 2830;
    private static final int aVo = 2831;
    private static final int aVp = 2832;
    private static final int aVq = 2833;
    private static final int aVr = 2834;
    private static final int aVs = 2835;
    private static final int aVt = 2836;
    private static final int aVu = 2837;
    private static final int aVv = 2838;
    private static final int aVw = 2821;
    private static final int aVx = 27099;
    private static final int aVy = 27128;
    private static final int aVz = 27129;
    private static final int aVA = 27130;
    private static final int aVB = 27131;
    private static final int aVC = 27132;
    private static final int aVD = 20404;
    private static final int aVE = 20104;
    private static final int aVF = 20102;
    private static final int aVG = 20022;
    private static final int aVH = 20100;
    public final int[][] DROPLIST_COND = new int[][]{{18, 0, 20404, 0, 2834, 1, 70, 1}, {18, 0, 20104, 0, 2835, 1, 70, 1}, {18, 0, 20102, 0, 2836, 1, 70, 1}, {18, 0, 20022, 0, 2837, 1, 70, 1}, {18, 0, 20100, 0, 2838, 1, 70, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _227_TestOfTheReformer() {
        super(0);
        this.addStartNpc(30118);
        this.addTalkId(30666);
        this.addTalkId(30668);
        this.addTalkId(30732);
        this.addTalkId(30669);
        this.addTalkId(30670);
        this.addTalkId(30667);
        this.addKillId(27099);
        this.addKillId(27128);
        this.addKillId(27129);
        this.addKillId(27130);
        this.addKillId(27131);
        this.addKillId(27132);
        for (int i = 0; i < this.DROPLIST_COND.length; ++i) {
            this.addKillId(this.DROPLIST_COND[i][2]);
            this.addQuestItem(this.DROPLIST_COND[i][4]);
        }
        this.addQuestItem(2822, 2832, 2823, 2824, 2827, 2833, 2826, 2828, 2829, 2825, 3037, 2830, 2831);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30118-04.htm")) {
            questState.giveItems(2822, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30118-06.htm")) {
            questState.takeItems(2832, -1L);
            questState.takeItems(2822, -1L);
            questState.giveItems(2823, 1L);
            questState.setCond(4);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30666-04.htm")) {
            questState.takeItems(2823, -1L);
            questState.giveItems(2824, 1L);
            questState.setCond(5);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30669-03.htm")) {
            if (GameObjectsStorage.getByNpcId(27131) == null) {
                questState.setCond(12);
                questState.setState(2);
                questState.addSpawn(27131);
                questState.startQuestTimer("Wait4", 300000L);
            } else {
                if (!questState.isRunningQuestTimer("Wait4")) {
                    questState.startQuestTimer("Wait4", 300000L);
                }
                string2 = "<html><head><body>Please wait 5 minutes</body></html>";
            }
        } else if (string.equalsIgnoreCase("30670-03.htm")) {
            if (GameObjectsStorage.getByNpcId(27132) == null) {
                questState.setCond(15);
                questState.setState(2);
                questState.addSpawn(27132);
                questState.startQuestTimer("Wait5", 300000L);
            } else {
                if (!questState.isRunningQuestTimer("Wait5")) {
                    questState.startQuestTimer("Wait5", 300000L);
                }
                string2 = "<html><head><body>Please wait 5 minutes</body></html>";
            }
        } else {
            if (string.equalsIgnoreCase("Wait1")) {
                NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(27128);
                if (npcInstance2 != null) {
                    npcInstance2.deleteMe();
                }
                if (questState.getCond() == 2) {
                    questState.setCond(1);
                }
                return null;
            }
            if (string.equalsIgnoreCase("Wait2")) {
                NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27129);
                if (npcInstance3 != null) {
                    npcInstance3.deleteMe();
                }
                if ((npcInstance3 = GameObjectsStorage.getByNpcId(30732)) != null) {
                    npcInstance3.deleteMe();
                }
                if (questState.getCond() == 6) {
                    questState.setCond(5);
                }
                return null;
            }
            if (string.equalsIgnoreCase("Wait3")) {
                NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27130);
                if (npcInstance4 != null) {
                    npcInstance4.deleteMe();
                }
                return null;
            }
            if (string.equalsIgnoreCase("Wait4")) {
                NpcInstance npcInstance5 = GameObjectsStorage.getByNpcId(27131);
                if (npcInstance5 != null) {
                    npcInstance5.deleteMe();
                }
                if (questState.getCond() == 12) {
                    questState.setCond(11);
                }
                return null;
            }
            if (string.equalsIgnoreCase("Wait5")) {
                NpcInstance npcInstance6 = GameObjectsStorage.getByNpcId(27132);
                if (npcInstance6 != null) {
                    npcInstance6.deleteMe();
                }
                if (questState.getCond() == 15) {
                    questState.setCond(14);
                }
                return null;
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30118) {
            if (questState.getQuestItemsCount(2821) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() == 15 || questState.getPlayer().getClassId().getId() == 42) {
                    if (questState.getPlayer().getLevel() >= 39) {
                        string = "30118-03.htm";
                    } else {
                        string = "30118-01.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30118-02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 3) {
                string = "30118-05.htm";
            } else if (n2 >= 4) {
                string = "30118-07.htm";
            }
        } else if (n == 30666) {
            if (n2 == 4) {
                string = "30666-01.htm";
            } else if (n2 == 5) {
                string = "30666-05.htm";
            } else if (n2 == 10) {
                questState.takeItems(2826, -1L);
                questState.giveItems(2825, 3L);
                string = "30666-06.htm";
                questState.setCond(11);
                questState.setState(2);
            } else if (n2 == 20) {
                questState.takeItems(2827, -1L);
                questState.takeItems(3037, -1L);
                questState.takeItems(2828, -1L);
                questState.takeItems(2830, -1L);
                questState.giveItems(2821, 1L);
                if (!questState.getPlayer().getVarB("prof2.3")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(164032L, 17500L);
                    questState.getPlayer().setVar("prof2.3", "1", -1L);
                }
                string = "30666-07.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30668) {
            if (n2 == 5 || n2 == 6) {
                NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(30732);
                NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27129);
                if (npcInstance2 == null && npcInstance3 == null) {
                    questState.takeItems(2824, -1L);
                    string = "30668-01.htm";
                    questState.setCond(6);
                    questState.setState(2);
                    questState.addSpawn(30732);
                    questState.addSpawn(27129);
                    questState.startQuestTimer("Wait2", 300000L);
                } else {
                    if (!questState.isRunningQuestTimer("Wait2")) {
                        questState.startQuestTimer("Wait2", 300000L);
                    }
                    string = "<html><head><body>Please wait 5 minutes</body></html>";
                }
            } else if (n2 == 8) {
                if (GameObjectsStorage.getByNpcId(27130) == null) {
                    string = "30668-02.htm";
                    questState.addSpawn(27130);
                    questState.startQuestTimer("Wait3", 300000L);
                } else {
                    if (!questState.isRunningQuestTimer("Wait3")) {
                        questState.startQuestTimer("Wait3", 300000L);
                    }
                    string = "<html><head><body>Please wait 5 minutes</body></html>";
                }
            } else if (n2 == 9) {
                questState.takeItems(2833, -1L);
                questState.giveItems(2827, 1L);
                string = "30668-03.htm";
                questState.setCond(10);
                questState.setState(2);
            }
        } else if (n == 30732) {
            if (n2 == 7) {
                questState.giveItems(2826, 1L);
                string = "30732-01.htm";
                questState.setCond(8);
                questState.setState(2);
                NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27129);
                if (npcInstance4 != null) {
                    npcInstance4.deleteMe();
                }
                if ((npcInstance4 = GameObjectsStorage.getByNpcId(30732)) != null) {
                    npcInstance4.deleteMe();
                }
                questState.cancelQuestTimer("Wait2");
            }
        } else if (n == 30669) {
            if (n2 == 11 || n2 == 12) {
                string = "30669-01.htm";
            } else if (n2 == 13) {
                questState.takeItems(2825, 1L);
                questState.giveItems(3037, 1L);
                string = "30669-04.htm";
                questState.setCond(14);
                questState.setState(2);
            }
        } else if (n == 30670) {
            if (n2 == 14 || n2 == 15) {
                string = "30670-01.htm";
            } else if (n2 == 16) {
                questState.takeItems(2825, 1L);
                questState.giveItems(2828, 1L);
                string = "30670-04.htm";
                questState.setCond(17);
                questState.setState(2);
            }
        } else if (n == 30667) {
            if (n2 == 17) {
                questState.takeItems(2825, -1L);
                questState.giveItems(2829, 1L);
                string = "30667-01.htm";
                questState.setCond(18);
                questState.setState(2);
            } else if (n2 == 19) {
                questState.takeItems(2834, -1L);
                questState.takeItems(2835, -1L);
                questState.takeItems(2836, -1L);
                questState.takeItems(2837, -1L);
                questState.takeItems(2838, -1L);
                questState.takeItems(2829, -1L);
                questState.giveItems(2830, 1L);
                string = "30667-03.htm";
                questState.setCond(20);
                questState.setState(2);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < this.DROPLIST_COND.length; ++i) {
            if (n2 != this.DROPLIST_COND[i][0] || n != this.DROPLIST_COND[i][2] || this.DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(this.DROPLIST_COND[i][3]) <= 0L) continue;
            if (this.DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(this.DROPLIST_COND[i][4], this.DROPLIST_COND[i][7], this.DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(this.DROPLIST_COND[i][4], this.DROPLIST_COND[i][7], this.DROPLIST_COND[i][7], this.DROPLIST_COND[i][5], this.DROPLIST_COND[i][6]) || this.DROPLIST_COND[i][1] == n2 || this.DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(this.DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        if (n2 == 18 && questState.getQuestItemsCount(2834) != 0L && questState.getQuestItemsCount(2835) != 0L && questState.getQuestItemsCount(2836) != 0L && questState.getQuestItemsCount(2837) != 0L && questState.getQuestItemsCount(2838) != 0L) {
            questState.setCond(19);
            questState.setState(2);
        } else if (n == 27099 && (n2 == 1 || n2 == 2)) {
            if (questState.getQuestItemsCount(2831) < 6L) {
                questState.giveItems(2831, 1L);
            } else if (GameObjectsStorage.getByNpcId(27128) == null) {
                questState.takeItems(2831, -1L);
                questState.setCond(2);
                questState.setState(2);
                questState.addSpawn(27128);
                questState.startQuestTimer("Wait1", 300000L);
            } else if (!questState.isRunningQuestTimer("Wait1")) {
                questState.startQuestTimer("Wait1", 300000L);
            }
        } else if (n == 27128) {
            NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(27128);
            if (npcInstance2 != null) {
                npcInstance2.deleteMe();
            }
            if (n2 == 2) {
                if (questState.getQuestItemsCount(2832) == 0L) {
                    questState.giveItems(2832, 1L);
                }
                questState.setCond(3);
                questState.setState(2);
                questState.cancelQuestTimer("Wait1");
            }
        } else if (n == 27129) {
            NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27129);
            if (npcInstance3 != null) {
                npcInstance3.deleteMe();
            }
            questState.cancelQuestTimer("Wait2");
            if (n2 == 6) {
                questState.setCond(7);
                questState.setState(2);
            }
        } else if (n == 27130) {
            NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27130);
            if (npcInstance4 != null) {
                npcInstance4.deleteMe();
            }
            questState.cancelQuestTimer("Wait3");
            if (n2 == 8) {
                if (questState.getQuestItemsCount(2833) == 0L) {
                    questState.giveItems(2833, 1L);
                }
                questState.setCond(9);
                questState.setState(2);
            }
        } else if (n == 27131) {
            NpcInstance npcInstance5 = GameObjectsStorage.getByNpcId(27131);
            if (npcInstance5 != null) {
                npcInstance5.deleteMe();
            }
            questState.cancelQuestTimer("Wait4");
            if (n2 == 12) {
                questState.setCond(13);
                questState.setState(2);
            }
        } else if (n == 27132) {
            NpcInstance npcInstance6 = GameObjectsStorage.getByNpcId(27132);
            if (npcInstance6 != null) {
                npcInstance6.deleteMe();
            }
            questState.cancelQuestTimer("Wait5");
            if (n2 == 15) {
                questState.setCond(16);
                questState.setState(2);
            }
        }
        return null;
    }
}

