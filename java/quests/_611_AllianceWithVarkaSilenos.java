/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _611_AllianceWithVarkaSilenos
extends Quest
implements ScriptFile {
    private static final int Uh = 7221;
    private static final int Ui = 7222;
    private static final int Uj = 7223;
    private static final int Uk = 7224;
    private static final int Ul = 7225;
    private static final int bLE = 7226;
    private static final int bLF = 7227;
    private static final int bLG = 7228;
    private static final int bLH = 7229;
    private static final int bLI = 7230;
    private static final int bKW = 21327;
    private static final int bKN = 21324;
    private static final int bKT = 21328;
    private static final int bKZ = 21325;
    private static final int bKV = 21329;
    private static final int bKU = 21338;
    private static final int bKX = 21331;
    private static final int bKL = 21332;
    private static final int bLJ = 21335;
    private static final int bKP = 21334;
    private static final int bKO = 21343;
    private static final int bKH = 21344;
    private static final int bKM = 21336;
    private static final int bKS = 21340;
    private static final int bKQ = 21339;
    private static final int bKR = 21342;
    private static final int bKY = 21347;
    private static final int bLb = 21348;
    private static final int bLa = 21349;
    private static final int bKK = 21345;
    private static final int bKJ = 21346;
    private static final int bLK = 31378;
    private static final int bKz = 21375;
    private static final int bKA = 21374;
    private static final int bLL = 25315;
    private static final int bLM = 25312;
    private static final int bKu = 21370;
    private static final int bLN = 25309;
    private static final int bKC = 21372;
    private static final int bKB = 21371;
    private static final int bKv = 21365;
    private static final int bKk = 21351;
    private static final int bKt = 21369;
    private static final int bKj = 21350;
    private static final int bKm = 21354;
    private static final int bKr = 21361;
    private static final int bKq = 21360;
    private static final int bKw = 21366;
    private static final int bKx = 21368;
    private static final int bKo = 21357;
    private static final int bKl = 21353;
    private static final int bLc = 21364;
    private static final int bKs = 21362;
    private static final int bKn = 21355;
    private static final int bKp = 21358;
    private static final int bKy = 21373;
    private static final int bLO = 25316;

    public _611_AllianceWithVarkaSilenos() {
        super(1);
        this.addStartNpc(31378);
        this.addKillId(21327);
        this.addKillId(21324);
        this.addKillId(21328);
        this.addKillId(21325);
        this.addKillId(21329);
        this.addKillId(21338);
        this.addKillId(21331);
        this.addKillId(21332);
        this.addKillId(21335);
        this.addKillId(21334);
        this.addKillId(21343);
        this.addKillId(21344);
        this.addKillId(21336);
        this.addKillId(21340);
        this.addKillId(21339);
        this.addKillId(21342);
        this.addKillId(21347);
        this.addKillId(21349);
        this.addKillId(21348);
        this.addKillId(21345);
        this.addKillId(21346);
        this.addKillId(21375, 21374, 25315, 25312, 21370, 25309, 21372, 21371, 21365, 21351, 21369, 21350, 21354, 21361, 21360, 21366, 21368, 21357, 21353, 21364, 21362, 21355, 21358, 21373, 25316);
        this.addQuestItem(7226);
        this.addQuestItem(7227);
        this.addQuestItem(7228);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            if (questState.getQuestItemsCount(7211) > 0L || questState.getQuestItemsCount(7212) > 0L || questState.getQuestItemsCount(7213) > 0L || questState.getQuestItemsCount(7214) > 0L || questState.getQuestItemsCount(7215) > 0L) {
                return "herald_naran_q0611_02.htm";
            }
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            if (questState.getQuestItemsCount(7221) < 1L && questState.getQuestItemsCount(7222) < 1L && questState.getQuestItemsCount(7223) < 1L && questState.getQuestItemsCount(7224) < 1L && questState.getQuestItemsCount(7225) < 1L) {
                string2 = "herald_naran_q0611_04.htm";
                questState.setCond(1);
            } else if (questState.getQuestItemsCount(7221) > 0L) {
                string2 = "herald_naran_q0611_05.htm";
                questState.setCond(2);
            } else if (questState.getQuestItemsCount(7222) > 0L) {
                string2 = "herald_naran_q0611_06.htm";
                questState.setCond(3);
            } else if (questState.getQuestItemsCount(7223) > 0L) {
                string2 = "herald_naran_q0611_07.htm";
                questState.setCond(4);
            } else if (questState.getQuestItemsCount(7224) > 0L) {
                string2 = "herald_naran_q0611_08.htm";
                questState.setCond(5);
            } else if (questState.getQuestItemsCount(7225) > 0L) {
                string2 = "herald_naran_q0611_09.htm";
                questState.setCond(6);
            }
        } else if (string.equalsIgnoreCase("reply_1") && questState.getQuestItemsCount(7221) < 1L && questState.getQuestItemsCount(7222) < 1L && questState.getQuestItemsCount(7223) < 1L && questState.getQuestItemsCount(7224) < 1L && questState.getQuestItemsCount(7225) < 1L && questState.getQuestItemsCount(7226) >= 100L) {
            string2 = "herald_naran_q0611_12.htm";
            questState.giveItems(7221, 1L);
            questState.takeItems(7226, -1L);
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("reply_2") && questState.getQuestItemsCount(7221) > 0L && questState.getQuestItemsCount(7226) >= 200L && questState.getQuestItemsCount(7227) >= 100L) {
            string2 = "herald_naran_q0611_15.htm";
            questState.giveItems(7222, 1L);
            questState.takeItems(7221, -1L);
            questState.takeItems(7226, -1L);
            questState.takeItems(7227, -1L);
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("reply_3") && questState.getQuestItemsCount(7226) >= 300L && questState.getQuestItemsCount(7227) >= 200L && questState.getQuestItemsCount(7228) >= 100L) {
            string2 = "herald_naran_q0611_18.htm";
            questState.giveItems(7223, 1L);
            questState.takeItems(7222, -1L);
            questState.takeItems(7226, -1L);
            questState.takeItems(7227, -1L);
            questState.takeItems(7228, -1L);
            questState.setCond(4);
        } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(7226) >= 300L && questState.getQuestItemsCount(7227) >= 300L && questState.getQuestItemsCount(7228) >= 200L && questState.getQuestItemsCount(7229) > 0L) {
            string2 = "herald_naran_q0611_21.htm";
            questState.giveItems(7224, 1L);
            questState.takeItems(7222, -1L);
            questState.takeItems(7223, -1L);
            questState.takeItems(7226, -1L);
            questState.takeItems(7227, -1L);
            questState.takeItems(7228, -1L);
            questState.takeItems(7229, -1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("reply_5")) {
            string2 = "herald_naran_q0611_25.htm";
        } else if (string.equalsIgnoreCase("reply_6")) {
            string2 = "herald_naran_q0611_26.htm";
            questState.takeItems(7221, -1L);
            questState.takeItems(7222, -1L);
            questState.takeItems(7223, -1L);
            questState.takeItems(7224, -1L);
            questState.takeItems(7225, -1L);
            questState.takeItems(7226, -1L);
            questState.takeItems(7227, -1L);
            questState.takeItems(7228, -1L);
            questState.takeItems(7229, -1L);
            questState.takeItems(7230, -1L);
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        switch (n) {
            case 1: {
                if (questState.getPlayer().getLevel() >= 74) {
                    string = "herald_naran_q0611_01.htm";
                    break;
                }
                string = "herald_naran_q0611_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(7221) < 1L && questState.getQuestItemsCount(7222) < 1L && questState.getQuestItemsCount(7223) < 1L && questState.getQuestItemsCount(7224) < 1L && questState.getQuestItemsCount(7225) < 1L && questState.getQuestItemsCount(7226) < 100L) {
                    string = "herald_naran_q0611_10.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7221) < 1L && questState.getQuestItemsCount(7222) < 1L && questState.getQuestItemsCount(7223) < 1L && questState.getQuestItemsCount(7224) < 1L && questState.getQuestItemsCount(7225) < 1L && questState.getQuestItemsCount(7226) >= 100L) {
                    string = "herald_naran_q0611_11.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7221) > 0L && (questState.getQuestItemsCount(7226) < 200L || questState.getQuestItemsCount(7227) < 100L)) {
                    string = "herald_naran_q0611_13.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7221) > 0L && questState.getQuestItemsCount(7226) >= 200L && questState.getQuestItemsCount(7227) >= 100L) {
                    string = "herald_naran_q0611_14.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7222) > 0L && (questState.getQuestItemsCount(7226) < 300L || questState.getQuestItemsCount(7227) < 200L || questState.getQuestItemsCount(7228) < 100L)) {
                    string = "herald_naran_q0611_16.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7222) > 0L && questState.getQuestItemsCount(7226) >= 300L && questState.getQuestItemsCount(7227) >= 200L && questState.getQuestItemsCount(7228) >= 100L) {
                    string = "herald_naran_q0611_17.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7223) > 0L && (questState.getQuestItemsCount(7226) < 300L || questState.getQuestItemsCount(7227) < 300L || questState.getQuestItemsCount(7228) < 200L || questState.getQuestItemsCount(7229) < 1L)) {
                    string = "herald_naran_q0611_19.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7223) > 0L && questState.getQuestItemsCount(7226) >= 300L && questState.getQuestItemsCount(7227) >= 300L && questState.getQuestItemsCount(7228) >= 200L && questState.getQuestItemsCount(7229) > 0L) {
                    string = "herald_naran_q0611_20.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7224) > 0L && (questState.getQuestItemsCount(7226) < 400L || questState.getQuestItemsCount(7227) < 400L || questState.getQuestItemsCount(7228) < 200L || questState.getQuestItemsCount(7230) < 1L)) {
                    string = "herald_naran_q0611_22.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7224) > 0L && questState.getQuestItemsCount(7226) >= 400L && questState.getQuestItemsCount(7227) >= 400L && questState.getQuestItemsCount(7228) >= 200L && questState.getQuestItemsCount(7230) > 0L) {
                    string = "herald_naran_q0611_23.htm";
                    questState.giveItems(7225, 1L);
                    questState.takeItems(7224, -1L);
                    questState.takeItems(7226, -1L);
                    questState.takeItems(7227, -1L);
                    questState.takeItems(7228, -1L);
                    questState.takeItems(7230, -1L);
                    questState.setCond(6);
                    questState.playSound("ItemSound.quest_middle");
                    break;
                }
                if (questState.getQuestItemsCount(7225) <= 0L) break;
                string = "herald_naran_q0611_24.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = Rnd.get(1000);
        int n2 = npcInstance.getNpcId();
        if ((questState.getQuestItemsCount(7221) + questState.getQuestItemsCount(7222) + questState.getQuestItemsCount(7223) + questState.getQuestItemsCount(7224) + questState.getQuestItemsCount(7225) == 0L && questState.getQuestItemsCount(7226) < 100L || questState.getQuestItemsCount(7221) >= 1L && questState.getQuestItemsCount(7226) < 200L || questState.getQuestItemsCount(7222) >= 1L && questState.getQuestItemsCount(7226) < 300L || questState.getQuestItemsCount(7223) >= 1L && questState.getQuestItemsCount(7226) < 300L || questState.getQuestItemsCount(7224) >= 1L && questState.getQuestItemsCount(7226) < 400L) && (n2 == 21327 || n2 == 21324 || n2 == 21328 || n2 == 21325 || n2 == 21329)) {
            if (n < 509) {
                questState.rollAndGive(7226, 1, 100.0);
            }
        } else if ((questState.getQuestItemsCount(7221) >= 1L && questState.getQuestItemsCount(7227) < 100L || questState.getQuestItemsCount(7222) >= 1L && questState.getQuestItemsCount(7227) < 200L || questState.getQuestItemsCount(7223) >= 1L && questState.getQuestItemsCount(7227) < 300L || questState.getQuestItemsCount(7224) >= 1L && questState.getQuestItemsCount(7227) < 400L) && (n2 == 21338 || n2 == 21331 || n2 == 21332 || n2 == 21335 || n2 == 21334 || n2 == 21343 || n2 == 21344 || n2 == 21336)) {
            if (n < 527) {
                questState.rollAndGive(7227, 1, 100.0);
            }
        } else if ((questState.getQuestItemsCount(7222) >= 1L && questState.getQuestItemsCount(7228) < 100L || questState.getQuestItemsCount(7223) >= 1L && questState.getQuestItemsCount(7228) < 200L || questState.getQuestItemsCount(7224) >= 1L && questState.getQuestItemsCount(7228) < 200L) && (n2 == 21340 || n2 == 21339 || n2 == 21342 || n2 == 21347 || n2 == 21349 || n2 == 21348 || n2 == 21345 || n2 == 21346)) {
            if (n < 527) {
                questState.rollAndGive(7228, 1, 100.0);
            }
        } else if (n2 == 21375 || n2 == 21374 || n2 == 25315 || n2 == 25312 || n2 == 21370 || n2 == 25309 || n2 == 21372 || n2 == 21371 || n2 == 21365 || n2 == 21351 || n2 == 21369 || n2 == 21350 || n2 == 21354 || n2 == 21361 || n2 == 21360 || n2 == 21366 || n2 == 21368 || n2 == 21357 || n2 == 21353 || n2 == 21364 || n2 == 21362 || n2 == 21355 || n2 == 21358 || n2 == 21373 || n2 == 25316) {
            if (questState.getQuestItemsCount(7225) >= 1L) {
                questState.takeItems(7225, -1L);
            } else if (questState.getQuestItemsCount(7224) >= 1L) {
                questState.takeItems(7224, -1L);
            } else if (questState.getQuestItemsCount(7223) >= 1L) {
                questState.takeItems(7223, -1L);
            } else if (questState.getQuestItemsCount(7222) >= 1L) {
                questState.takeItems(7222, -1L);
            } else if (questState.getQuestItemsCount(7221) >= 1L) {
                questState.takeItems(7221, -1L);
            }
            questState.takeItems(7226, -1L);
            questState.takeItems(7227, -1L);
            questState.takeItems(7228, -1L);
            questState.exitCurrentQuest(true);
        }
        return null;
    }
}

