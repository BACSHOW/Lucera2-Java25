/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _220_TestimonyOfGlory
extends Quest
implements ScriptFile {
    private static final int aPx = 30514;
    private static final int aPy = 30642;
    private static final int aPz = 30515;
    private static final int aPA = 30501;
    private static final int aPB = 30615;
    private static final int aPC = 30616;
    private static final int aPD = 30617;
    private static final int aPE = 30618;
    private static final int aPF = 30619;
    private static final int aPG = 30571;
    private static final int aPH = 30565;
    private static final int aPI = 3204;
    private static final int aPJ = 3205;
    private static final int aPK = 3206;
    private static final int aPL = 3207;
    private static final int aPM = 3208;
    private static final int aPN = 3209;
    private static final int aPO = 3210;
    private static final int aPP = 3211;
    private static final int aPQ = 3212;
    private static final int aPR = 3213;
    private static final int aPS = 3214;
    private static final int aPT = 3215;
    private static final int aPU = 3216;
    private static final int aPV = 3217;
    private static final int aPW = 3218;
    private static final int aPX = 3219;
    private static final int aPY = 3220;
    private static final int aPZ = 3221;
    private static final int aQa = 3222;
    private static final int aQb = 3223;
    private static final int aQc = 3224;
    private static final int aQd = 3225;
    private static final int aQe = 3226;
    private static final int aQf = 3227;
    private static final int aQg = 3228;
    private static final int aQh = 3229;
    private static final int aQi = 3230;
    private static final int aQj = 3231;
    private static final int aQk = 3232;
    private static final int aQl = 3233;
    private static final int aQm = 3234;
    private static final int aQn = 3235;
    private static final int aQo = 3236;
    private static final int aQp = 3237;
    private static final int aQq = 3203;
    private static final int aPp = 20192;
    private static final int aPq = 20193;
    private static final int aQr = 20550;
    private static final int aQs = 20563;
    private static final int aLe = 20234;
    private static final int aQt = 27080;
    private static final int aQu = 27081;
    private static final int aQv = 27082;
    private static final int aQw = 27083;
    private static final int aQx = 20583;
    private static final int aQy = 20584;
    private static final int aQz = 20585;
    private static final int aQA = 20586;
    private static final int aQB = 20587;
    private static final int aQC = 20588;
    private static final int aQD = 20601;
    private static final int aQE = 20602;
    private static final int aQF = 20778;
    private static final int aQG = 20779;
    private static final int aQH = 27086;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 0, 20563, 3204, 3205, 10, 70, 1}, {1, 0, 20192, 3204, 3206, 10, 70, 1}, {1, 0, 20193, 3204, 3206, 10, 70, 1}, {1, 0, 20550, 3204, 3207, 10, 70, 1}, {4, 0, 20234, 3233, 3234, 30, 70, 1}, {4, 0, 27082, 3225, 3224, 4, 100, 1}, {4, 0, 27083, 3227, 3226, 2, 100, 1}, {6, 0, 20583, 3217, 3219, 20, 50, 1}, {6, 0, 20584, 3217, 3219, 20, 60, 1}, {6, 0, 20585, 3217, 3219, 20, 70, 1}, {6, 0, 20586, 3217, 3219, 20, 80, 1}, {6, 0, 20587, 3217, 3219, 20, 90, 1}, {6, 0, 20588, 3217, 3219, 20, 100, 1}, {6, 0, 20601, 3217, 3218, 20, 50, 1}, {6, 0, 20602, 3217, 3218, 20, 60, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _220_TestimonyOfGlory() {
        super(0);
        this.addStartNpc(30514);
        this.addTalkId(30642);
        this.addTalkId(30515);
        this.addTalkId(30501);
        this.addTalkId(30615);
        this.addTalkId(30616);
        this.addTalkId(30617);
        this.addTalkId(30618);
        this.addTalkId(30619);
        this.addTalkId(30571);
        this.addTalkId(30565);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addKillId(27080);
        this.addKillId(27081);
        this.addKillId(20778);
        this.addKillId(20779);
        this.addKillId(27086);
        this.addQuestItem(3204, 3208, 3209, 3210, 3228, 3229, 3230, 3231, 3232, 3211, 3221, 3222, 3223, 3225, 3212, 3214, 3227, 3215, 3233, 3216, 3217, 3220, 3235, 3236, 3237, 3205, 3206, 3207, 3234, 3224, 3226, 3219, 3218);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("RETURN")) {
            return null;
        }
        if (string.equalsIgnoreCase("30514-05.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.giveItems(3204, 1L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30642-03.htm")) {
            questState.takeItems(3208, -1L);
            questState.giveItems(3210, 1L);
            questState.setCond(4);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30571-03.htm")) {
            questState.takeItems(3220, -1L);
            questState.giveItems(3235, 1L);
            questState.setCond(9);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30642-07.htm")) {
            questState.takeItems(3211, -1L);
            questState.takeItems(3212, -1L);
            questState.takeItems(3213, -1L);
            questState.takeItems(3214, -1L);
            questState.takeItems(3215, -1L);
            questState.takeItems(3210, -1L);
            if (questState.getPlayer().getLevel() >= 38) {
                questState.giveItems(3217, 1L);
                questState.setCond(6);
                questState.setState(2);
            } else {
                string2 = "30642-06.htm";
                questState.giveItems(3216, 1L);
            }
        } else if (string.equalsIgnoreCase("BREKA")) {
            if (questState.getQuestItemsCount(3211) > 0L) {
                string2 = "30515-02.htm";
            } else if (questState.getQuestItemsCount(3228) > 0L) {
                string2 = "30515-04.htm";
            } else {
                string2 = "30515-03.htm";
                questState.giveItems(3228, 1L);
            }
        } else if (string.equalsIgnoreCase("ENKU")) {
            if (questState.getQuestItemsCount(3212) > 0L) {
                string2 = "30515-05.htm";
            } else if (questState.getQuestItemsCount(3229) > 0L) {
                string2 = "30515-07.htm";
            } else {
                string2 = "30515-06.htm";
                questState.giveItems(3229, 1L);
            }
        } else if (string.equalsIgnoreCase("VUKU")) {
            if (questState.getQuestItemsCount(3213) > 0L) {
                string2 = "30501-02.htm";
            } else if (questState.getQuestItemsCount(3230) > 0L) {
                string2 = "30501-04.htm";
            } else {
                string2 = "30501-03.htm";
                questState.giveItems(3230, 1L);
            }
        } else if (string.equalsIgnoreCase("TUREK")) {
            if (questState.getQuestItemsCount(3214) > 0L) {
                string2 = "30501-05.htm";
            } else if (questState.getQuestItemsCount(3231) > 0L) {
                string2 = "30501-07.htm";
            } else {
                string2 = "30501-06.htm";
                questState.giveItems(3231, 1L);
            }
        } else if (string.equalsIgnoreCase("TUNATH")) {
            if (questState.getQuestItemsCount(3215) > 0L) {
                string2 = "30501-08.htm";
            } else if (questState.getQuestItemsCount(3232) > 0L) {
                string2 = "30501-10.htm";
            } else {
                string2 = "30501-09.htm";
                questState.giveItems(3232, 1L);
            }
        } else if (string.equalsIgnoreCase("30615-04.htm")) {
            boolean bl = false;
            NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(27080);
            if (npcInstance2 != null) {
                bl = true;
            }
            if ((npcInstance2 = GameObjectsStorage.getByNpcId(27081)) != null) {
                bl = true;
            }
            if (bl) {
                if (!questState.isRunningQuestTimer("Wait1")) {
                    questState.startQuestTimer("Wait1", 300000L);
                }
                string2 = "<html><head><body>Please wait 5 minutes</body></html>";
            } else {
                questState.takeItems(3228, -1L);
                questState.giveItems(3223, 1L);
                questState.cancelQuestTimer("Wait1");
                questState.startQuestTimer("PashikasSonOfVoltarQuestMonster", 200000L);
                questState.startQuestTimer("VultusSonOfVoltarQuestMonster", 200000L);
                questState.addSpawn(27080);
                questState.addSpawn(27081);
                questState.playSound("Itemsound.quest_before_battle");
            }
        } else if (string.equalsIgnoreCase("30616-04.htm")) {
            NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27082);
            if (npcInstance3 != null) {
                if (!questState.isRunningQuestTimer("Wait2")) {
                    questState.startQuestTimer("Wait2", 300000L);
                }
                string2 = "<html><head><body>Please wait 5 minutes</body></html>";
            } else {
                questState.takeItems(3229, -1L);
                questState.giveItems(3225, 1L);
                questState.cancelQuestTimer("Wait2");
                questState.startQuestTimer("EnkuOrcOverlordQuestMonster", 200000L);
                questState.addSpawn(27082);
                questState.addSpawn(27082);
                questState.addSpawn(27082);
                questState.addSpawn(27082);
                questState.playSound("Itemsound.quest_before_battle");
            }
        } else if (string.equalsIgnoreCase("30617-04.htm")) {
            NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27083);
            if (npcInstance4 != null) {
                if (!questState.isRunningQuestTimer("Wait3")) {
                    questState.startQuestTimer("Wait3", 300000L);
                }
                string2 = "<html><head><body>Please wait 5 minutes</body></html>";
            } else {
                questState.takeItems(3231, -1L);
                questState.giveItems(3227, 1L);
                questState.cancelQuestTimer("Wait3");
                questState.startQuestTimer("MakumBugbearThugQuestMonster", 200000L);
                questState.addSpawn(27083);
                questState.addSpawn(27083);
                questState.playSound("Itemsound.quest_before_battle");
            }
        } else if (string.equalsIgnoreCase("30618-03.htm")) {
            questState.takeItems(3232, -1L);
            questState.giveItems(3215, 1L);
            if (questState.getQuestItemsCount(3211) != 0L && questState.getQuestItemsCount(3212) != 0L && questState.getQuestItemsCount(3213) != 0L && questState.getQuestItemsCount(3214) != 0L && questState.getQuestItemsCount(3215) != 0L) {
                questState.setCond(5);
                questState.setState(2);
            }
        } else if (string.equalsIgnoreCase("30619-03.htm")) {
            questState.takeItems(3230, -1L);
            questState.giveItems(3233, 1L);
        } else if (string.equalsIgnoreCase("Wait1") || string.equalsIgnoreCase("PashikasSonOfVoltarQuestMonster") || string.equalsIgnoreCase("VultusSonOfVoltarQuestMonster")) {
            NpcInstance npcInstance5 = GameObjectsStorage.getByNpcId(27080);
            if (npcInstance5 != null) {
                npcInstance5.deleteMe();
            }
            if ((npcInstance5 = GameObjectsStorage.getByNpcId(27081)) != null) {
                npcInstance5.deleteMe();
            }
            questState.cancelQuestTimer("Wait1");
            questState.cancelQuestTimer("PashikasSonOfVoltarQuestMonster");
        } else if (string.equalsIgnoreCase("Wait2") || string.equalsIgnoreCase("EnkuOrcOverlordQuestMonster")) {
            NpcInstance npcInstance6 = GameObjectsStorage.getByNpcId(27082);
            if (npcInstance6 != null) {
                npcInstance6.deleteMe();
            }
            if ((npcInstance6 = GameObjectsStorage.getByNpcId(27082)) != null) {
                npcInstance6.deleteMe();
            }
            if ((npcInstance6 = GameObjectsStorage.getByNpcId(27082)) != null) {
                npcInstance6.deleteMe();
            }
            if ((npcInstance6 = GameObjectsStorage.getByNpcId(27082)) != null) {
                npcInstance6.deleteMe();
            }
            questState.cancelQuestTimer("Wait2");
            questState.cancelQuestTimer("EnkuOrcOverlordQuestMonster");
        } else if (string.equalsIgnoreCase("Wait3") || string.equalsIgnoreCase("MakumBugbearThugQuestMonster")) {
            NpcInstance npcInstance7 = GameObjectsStorage.getByNpcId(27083);
            if (npcInstance7 != null) {
                npcInstance7.deleteMe();
            }
            if ((npcInstance7 = GameObjectsStorage.getByNpcId(27083)) != null) {
                npcInstance7.deleteMe();
            }
            questState.cancelQuestTimer("Wait3");
            questState.cancelQuestTimer("MakumBugbearThugQuestMonster");
        } else if (string.equalsIgnoreCase("Wait4") || string.equalsIgnoreCase("RevenantOfTantosChief")) {
            NpcInstance npcInstance8 = GameObjectsStorage.getByNpcId(27086);
            if (npcInstance8 != null) {
                npcInstance8.deleteMe();
            }
            questState.cancelQuestTimer("Wait4");
            questState.cancelQuestTimer("RevenantOfTantosChief");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30514) {
            if (questState.getQuestItemsCount(3203) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() == 45 || questState.getPlayer().getClassId().getId() == 47 || questState.getPlayer().getClassId().getId() == 50) {
                    if (questState.getPlayer().getLevel() >= 37) {
                        string = "30514-03.htm";
                    } else {
                        string = "30514-01.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30514-02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "30514-06.htm";
            } else if (n2 == 2) {
                questState.takeItems(3204, -1L);
                questState.takeItems(3205, -10L);
                questState.takeItems(3206, -10L);
                questState.takeItems(3207, -10L);
                questState.giveItems(3208, 1L);
                questState.giveItems(3209, 1L);
                string = "30514-08.htm";
                questState.setCond(3);
                questState.setState(2);
            } else if (n2 == 3) {
                string = "30514-09.htm";
            } else if (n2 == 4) {
                string = "30514-10.htm";
            }
        } else if (n == 30642) {
            if (n2 == 3) {
                string = "30642-01.htm";
            } else if (n2 == 4) {
                string = "30642-04.htm";
            } else if (n2 == 5) {
                if (questState.getQuestItemsCount(3210) > 0L) {
                    string = "30642-05.htm";
                } else if (questState.getQuestItemsCount(3216) > 0L) {
                    if (questState.getPlayer().getLevel() >= 38) {
                        questState.takeItems(3216, -1L);
                        questState.giveItems(3217, 1L);
                        string = "30642-09.htm";
                        questState.setCond(6);
                        questState.setState(2);
                    } else {
                        string = "30642-08.htm";
                    }
                }
            } else if (n2 == 6) {
                string = "30642-10.htm";
            } else if (n2 == 7) {
                questState.takeItems(3209, -1L);
                questState.takeItems(3217, -1L);
                questState.takeItems(3218, -1L);
                questState.takeItems(3219, -1L);
                questState.giveItems(3220, 1L);
                string = "30642-11.htm";
                questState.setCond(8);
                questState.setState(2);
            } else if (n2 == 8) {
                string = "30642-12.htm";
            }
        } else if (n == 30515) {
            if (n2 == 4) {
                string = "30515-01.htm";
            }
        } else if (n == 30501) {
            if (n2 == 4) {
                string = "30501-01.htm";
            }
        } else if (n == 30615) {
            if (n2 == 4) {
                if (questState.getQuestItemsCount(3228) > 0L) {
                    string = "30615-02.htm";
                } else if (questState.getQuestItemsCount(3223) > 0L && (questState.getQuestItemsCount(3221) == 0L || questState.getQuestItemsCount(3222) == 0L)) {
                    string = "30615-05.htm";
                    boolean bl = false;
                    NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(27080);
                    if (npcInstance2 == null) {
                        bl = true;
                        questState.addSpawn(27080);
                        questState.startQuestTimer("PashikasSonOfVoltarQuestMonster", 200000L);
                    }
                    if ((npcInstance2 = GameObjectsStorage.getByNpcId(27081)) == null) {
                        bl = true;
                        questState.addSpawn(27081);
                        questState.startQuestTimer("VultusSonOfVoltarQuestMonster", 200000L);
                    }
                    if (bl) {
                        questState.playSound("Itemsound.quest_before_battle");
                        questState.cancelQuestTimer("Wait1");
                    } else {
                        questState.startQuestTimer("Wait1", 300000L);
                        string = "<html><head><body>Please wait 5 minutes</body></html>";
                    }
                } else if (questState.getQuestItemsCount(3221) > 0L && questState.getQuestItemsCount(3222) > 0L) {
                    questState.takeItems(3221, -1L);
                    questState.takeItems(3222, -1L);
                    questState.takeItems(3223, -1L);
                    questState.giveItems(3211, 1L);
                    string = "30615-06.htm";
                    if (questState.getQuestItemsCount(3211) > 0L && questState.getQuestItemsCount(3212) > 0L && questState.getQuestItemsCount(3213) > 0L && questState.getQuestItemsCount(3214) > 0L && questState.getQuestItemsCount(3215) > 0L) {
                        questState.setCond(5);
                        questState.setState(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else {
                    string = questState.getQuestItemsCount(3211) > 0L ? "30615-07.htm" : "30615-01.htm";
                }
            }
        } else if (n == 30616) {
            if (n2 == 4) {
                if (questState.getQuestItemsCount(3229) > 0L) {
                    string = "30616-02.htm";
                } else if (questState.getQuestItemsCount(3225) > 0L && questState.getQuestItemsCount(3224) < 4L) {
                    string = "30616-05.htm";
                    NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27082);
                    if (npcInstance3 != null) {
                        questState.startQuestTimer("Wait2", 300000L);
                        string = "<html><head><body>Please wait 5 minutes</body></html>";
                    } else {
                        questState.cancelQuestTimer("Wait2");
                        questState.startQuestTimer("EnkuOrcOverlordQuestMonster", 200000L);
                        questState.addSpawn(27082);
                        questState.addSpawn(27082);
                        questState.addSpawn(27082);
                        questState.addSpawn(27082);
                        questState.playSound("Itemsound.quest_before_battle");
                    }
                } else if (questState.getQuestItemsCount(3224) >= 4L) {
                    string = "30616-06.htm";
                    questState.takeItems(3224, -1L);
                    questState.takeItems(3225, -1L);
                    questState.giveItems(3212, 1L);
                    if (questState.getQuestItemsCount(3211) > 0L && questState.getQuestItemsCount(3212) > 0L && questState.getQuestItemsCount(3213) > 0L && questState.getQuestItemsCount(3214) > 0L && questState.getQuestItemsCount(3215) > 0L) {
                        questState.setCond(5);
                        questState.setState(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else {
                    string = questState.getQuestItemsCount(3212) > 0L ? "30616-07.htm" : "30616-01.htm";
                }
            }
        } else if (n == 30617) {
            if (n2 == 4) {
                if (questState.getQuestItemsCount(3231) > 0L) {
                    string = "30617-02.htm";
                } else if (questState.getQuestItemsCount(3227) > 0L && questState.getQuestItemsCount(3226) < 2L) {
                    string = "30617-05.htm";
                    NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27083);
                    if (npcInstance4 != null) {
                        questState.startQuestTimer("Wait3", 300000L);
                        string = "<html><head><body>Please wait 5 minutes</body></html>";
                    } else {
                        questState.cancelQuestTimer("Wait3");
                        questState.startQuestTimer("MakumBugbearThugQuestMonster", 200000L);
                        questState.addSpawn(27083);
                        questState.addSpawn(27083);
                        questState.playSound("Itemsound.quest_before_battle");
                    }
                } else if (questState.getQuestItemsCount(3226) == 2L) {
                    string = "30617-06.htm";
                    questState.takeItems(3226, -1L);
                    questState.takeItems(3227, -1L);
                    questState.giveItems(3214, 1L);
                    if (questState.getQuestItemsCount(3211) > 0L && questState.getQuestItemsCount(3212) > 0L && questState.getQuestItemsCount(3213) > 0L && questState.getQuestItemsCount(3214) > 0L && questState.getQuestItemsCount(3215) > 0L) {
                        questState.setCond(5);
                        questState.setState(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else {
                    string = questState.getQuestItemsCount(3214) > 0L ? "30617-07.htm" : "30617-01.htm";
                }
            }
        } else if (n == 30618) {
            if (n2 == 4) {
                string = questState.getQuestItemsCount(3232) > 0L ? "30618-02.htm" : (questState.getQuestItemsCount(3215) > 0L ? "30618-04.htm" : "30618-01.htm");
            }
        } else if (n == 30619) {
            if (n2 == 4) {
                if (questState.getQuestItemsCount(3230) > 0L) {
                    string = "30619-02.htm";
                } else if (questState.getQuestItemsCount(3233) > 0L) {
                    if (questState.getQuestItemsCount(3234) >= 30L) {
                        string = "30619-05.htm";
                        questState.takeItems(3234, -1L);
                        questState.takeItems(3233, -1L);
                        questState.giveItems(3213, 1L);
                        if (questState.getQuestItemsCount(3211) > 0L && questState.getQuestItemsCount(3212) > 0L && questState.getQuestItemsCount(3213) > 0L && questState.getQuestItemsCount(3214) > 0L && questState.getQuestItemsCount(3215) > 0L) {
                            questState.setCond(5);
                            questState.setState(2);
                            questState.playSound("ItemSound.quest_middle");
                        } else {
                            questState.playSound("ItemSound.quest_itemget");
                        }
                    } else {
                        string = "30619-04.htm";
                    }
                } else {
                    string = questState.getQuestItemsCount(3213) > 0L ? "30619-06.htm" : "30619-01.htm";
                }
            }
        } else if (n == 30571) {
            if (n2 == 8) {
                string = "30571-01.htm";
            } else if (n2 == 9) {
                string = "30571-04.htm";
            } else if (n2 == 10) {
                questState.takeItems(3236, -1L);
                questState.takeItems(3235, -1L);
                questState.giveItems(3237, 1L);
                string = "30571-05.htm";
                questState.setCond(11);
                questState.setState(2);
            } else if (n2 == 11) {
                string = "30571-06.htm";
            }
        }
        if (n == 30565 && n2 == 11) {
            questState.takeItems(3237, -1L);
            questState.giveItems(3203, 1L);
            if (!questState.getPlayer().getVarB("prof2.2")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(91457L, 2500L);
                questState.getPlayer().setVar("prof2.2", "1", -1L);
            }
            string = "30565-02.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
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
        if (n2 == 1 && questState.getQuestItemsCount(3206) >= 10L && questState.getQuestItemsCount(3207) >= 10L && questState.getQuestItemsCount(3205) >= 10L) {
            questState.setCond(2);
            questState.setState(2);
        } else if (n2 == 4) {
            if (n == 27080) {
                questState.cancelQuestTimer("PashikasSonOfVoltarQuestMonster");
                NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(27080);
                if (npcInstance2 != null) {
                    npcInstance2.deleteMe();
                }
                if (questState.getQuestItemsCount(3223) > 0L && questState.getQuestItemsCount(3221) == 0L) {
                    questState.giveItems(3221, 1L);
                }
            } else if (n == 27081) {
                questState.cancelQuestTimer("VultusSonOfVoltarQuestMonster");
                NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27081);
                if (npcInstance3 != null) {
                    npcInstance3.deleteMe();
                }
                if (questState.getQuestItemsCount(3223) > 0L && questState.getQuestItemsCount(3222) == 0L) {
                    questState.giveItems(3222, 1L);
                }
            }
        } else if (n2 == 6 && questState.getQuestItemsCount(3219) >= 20L && questState.getQuestItemsCount(3218) >= 20L) {
            questState.setCond(7);
            questState.setState(2);
        } else if (n2 == 9) {
            if (n == 20778 || n == 20779) {
                NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27086);
                if (npcInstance4 == null) {
                    questState.startQuestTimer("RevenantOfTantosChief", 300000L);
                    questState.addSpawn(27086);
                    questState.playSound("Itemsound.quest_before_battle");
                } else if (!questState.isRunningQuestTimer("Wait4")) {
                    questState.startQuestTimer("Wait4", 300000L);
                }
            } else if (n == 27086) {
                questState.cancelQuestTimer("RevenantOfTantosChief");
                questState.cancelQuestTimer("Wait4");
                NpcInstance npcInstance5 = GameObjectsStorage.getByNpcId(27086);
                if (npcInstance5 != null) {
                    npcInstance5.deleteMe();
                }
                questState.giveItems(3236, 1L);
                questState.setCond(10);
                questState.setState(2);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        return null;
    }
}

