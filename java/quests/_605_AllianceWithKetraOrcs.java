/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _605_AllianceWithKetraOrcs
extends Quest
implements ScriptFile {
    private static final int Um = 7211;
    private static final int Un = 7212;
    private static final int Uo = 7213;
    private static final int Up = 7214;
    private static final int Uq = 7215;
    private static final int bKe = 7216;
    private static final int bKf = 7217;
    private static final int bKg = 7218;
    private static final int bKh = 7219;
    private static final int bKi = 7220;
    private static final int bKj = 21350;
    private static final int bKk = 21351;
    private static final int bKl = 21353;
    private static final int bKm = 21354;
    private static final int bKn = 21355;
    private static final int bKo = 21357;
    private static final int bKp = 21358;
    private static final int bKq = 21360;
    private static final int bKr = 21361;
    private static final int bKs = 21362;
    private static final int bKt = 21369;
    private static final int bKu = 21370;
    private static final int bKv = 21365;
    private static final int bKw = 21366;
    private static final int bKx = 21368;
    private static final int bKy = 21373;
    private static final int bKz = 21375;
    private static final int bKA = 21374;
    private static final int bKB = 21371;
    private static final int bKC = 21372;
    private static final int bKD = 31371;
    private static final int bKE = 25306;
    private static final int bKF = 25305;
    private static final int bKG = 25302;
    private static final int bKH = 21344;
    private static final int bKI = 25299;
    private static final int bKJ = 21346;
    private static final int bKK = 21345;
    private static final int bKL = 21332;
    private static final int bKM = 21336;
    private static final int bKN = 21324;
    private static final int bKO = 21343;
    private static final int bKP = 21334;
    private static final int bKQ = 21339;
    private static final int bKR = 21342;
    private static final int bKS = 21340;
    private static final int bKT = 21328;
    private static final int bKU = 21338;
    private static final int bKV = 21329;
    private static final int bKW = 21327;
    private static final int bKX = 21331;
    private static final int bKY = 21347;
    private static final int bKZ = 21325;
    private static final int bLa = 21349;
    private static final int bLb = 21348;

    public _605_AllianceWithKetraOrcs() {
        super(1);
        this.addStartNpc(31371);
        this.addKillId(21350);
        this.addKillId(21351);
        this.addKillId(21353);
        this.addKillId(21354);
        this.addKillId(21355);
        this.addKillId(21357);
        this.addKillId(21358);
        this.addKillId(21360);
        this.addKillId(21361);
        this.addKillId(21362);
        this.addKillId(21369);
        this.addKillId(21370);
        this.addKillId(21365);
        this.addKillId(21366);
        this.addKillId(21368);
        this.addKillId(21373);
        this.addKillId(21375);
        this.addKillId(21374);
        this.addKillId(21371);
        this.addKillId(21372);
        this.addKillId(25306, 25305, 25302, 21344, 25299, 21346, 21345, 21332, 21336, 21324, 21343, 21334, 21339, 21342, 21340, 21328, 21338, 21329, 21327, 21331, 21347, 21325, 21349, 21348);
        this.addQuestItem(7216, 7217, 7218);
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
            if (questState.getQuestItemsCount(7221) > 0L || questState.getQuestItemsCount(7222) > 0L || questState.getQuestItemsCount(7223) > 0L || questState.getQuestItemsCount(7224) > 0L || questState.getQuestItemsCount(7225) > 0L) {
                return "herald_wakan_q0605_02.htm";
            }
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            if (questState.getQuestItemsCount(7211) < 1L && questState.getQuestItemsCount(7212) < 1L && questState.getQuestItemsCount(7213) < 1L && questState.getQuestItemsCount(7214) < 1L && questState.getQuestItemsCount(7215) < 1L) {
                string2 = "herald_wakan_q0605_04.htm";
                questState.setCond(1);
            } else if (questState.getQuestItemsCount(7211) > 0L) {
                string2 = "herald_wakan_q0605_05.htm";
                questState.setCond(2);
            } else if (questState.getQuestItemsCount(7212) > 0L) {
                string2 = "herald_wakan_q0605_06.htm";
                questState.setCond(3);
            } else if (questState.getQuestItemsCount(7213) > 0L) {
                string2 = "herald_wakan_q0605_07.htm";
                questState.setCond(4);
            } else if (questState.getQuestItemsCount(7214) > 0L) {
                string2 = "herald_wakan_q0605_08.htm";
                questState.setCond(5);
            } else if (questState.getQuestItemsCount(7215) > 0L) {
                string2 = "herald_wakan_q0605_09.htm";
                questState.setCond(6);
            }
        } else if (string.equalsIgnoreCase("reply_1") && questState.getQuestItemsCount(7211) < 1L && questState.getQuestItemsCount(7212) < 1L && questState.getQuestItemsCount(7213) < 1L && questState.getQuestItemsCount(7214) < 1L && questState.getQuestItemsCount(7215) < 1L && questState.getQuestItemsCount(7216) >= 100L) {
            string2 = "herald_wakan_q0605_12.htm";
            questState.giveItems(7211, 1L);
            questState.takeItems(7216, -1L);
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("reply_2") && questState.getQuestItemsCount(7211) > 0L && questState.getQuestItemsCount(7216) >= 200L && questState.getQuestItemsCount(7217) >= 100L) {
            string2 = "herald_wakan_q0605_15.htm";
            questState.giveItems(7212, 1L);
            questState.takeItems(7211, -1L);
            questState.takeItems(7216, -1L);
            questState.takeItems(7217, -1L);
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("reply_3") && questState.getQuestItemsCount(7216) >= 300L && questState.getQuestItemsCount(7217) >= 200L && questState.getQuestItemsCount(7218) >= 100L) {
            string2 = "herald_wakan_q0605_18.htm";
            questState.giveItems(7213, 1L);
            questState.takeItems(7212, -1L);
            questState.takeItems(7216, -1L);
            questState.takeItems(7217, -1L);
            questState.takeItems(7218, -1L);
            questState.setCond(4);
        } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(7216) >= 300L && questState.getQuestItemsCount(7217) >= 300L && questState.getQuestItemsCount(7218) >= 200L && questState.getQuestItemsCount(7219) > 0L) {
            string2 = "herald_wakan_q0605_21.htm";
            questState.giveItems(7214, 1L);
            questState.takeItems(7213, -1L);
            questState.takeItems(7216, -1L);
            questState.takeItems(7217, -1L);
            questState.takeItems(7218, -1L);
            questState.takeItems(7219, -1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("reply_5")) {
            string2 = "herald_wakan_q0605_25.htm";
        } else if (string.equalsIgnoreCase("reply_6")) {
            string2 = "herald_wakan_q0605_26.htm";
            questState.takeItems(7215, -1L);
            questState.takeItems(7211, -1L);
            questState.takeItems(7212, -1L);
            questState.takeItems(7213, -1L);
            questState.takeItems(7214, -1L);
            questState.takeItems(7216, -1L);
            questState.takeItems(7217, -1L);
            questState.takeItems(7218, -1L);
            questState.takeItems(7219, -1L);
            questState.takeItems(7220, -1L);
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
                    string = "herald_wakan_q0605_01.htm";
                    break;
                }
                string = "herald_wakan_q0605_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(7211) < 1L && questState.getQuestItemsCount(7212) < 1L && questState.getQuestItemsCount(7213) < 1L && questState.getQuestItemsCount(7214) < 1L && questState.getQuestItemsCount(7215) < 1L && questState.getQuestItemsCount(7216) < 100L) {
                    string = "herald_wakan_q0605_10.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7211) < 1L && questState.getQuestItemsCount(7212) < 1L && questState.getQuestItemsCount(7213) < 1L && questState.getQuestItemsCount(7214) < 1L && questState.getQuestItemsCount(7215) < 1L && questState.getQuestItemsCount(7216) >= 100L) {
                    string = "herald_wakan_q0605_11.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7211) > 0L && (questState.getQuestItemsCount(7216) < 200L || questState.getQuestItemsCount(7217) < 100L)) {
                    string = "herald_wakan_q0605_13.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7211) > 0L && questState.getQuestItemsCount(7216) >= 200L && questState.getQuestItemsCount(7217) >= 100L) {
                    string = "herald_wakan_q0605_14.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7212) > 0L && (questState.getQuestItemsCount(7216) < 300L || questState.getQuestItemsCount(7217) < 200L || questState.getQuestItemsCount(7218) < 100L)) {
                    string = "herald_wakan_q0605_16.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7212) > 0L && questState.getQuestItemsCount(7216) >= 300L && questState.getQuestItemsCount(7217) >= 200L && questState.getQuestItemsCount(7218) >= 100L) {
                    string = "herald_wakan_q0605_17.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7213) > 0L && (questState.getQuestItemsCount(7216) < 300L || questState.getQuestItemsCount(7217) < 300L || questState.getQuestItemsCount(7218) < 200L || questState.getQuestItemsCount(7219) < 1L)) {
                    string = "herald_wakan_q0605_19.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7213) > 0L && questState.getQuestItemsCount(7216) >= 300L && questState.getQuestItemsCount(7217) >= 300L && questState.getQuestItemsCount(7218) >= 200L && questState.getQuestItemsCount(7219) > 0L) {
                    string = "herald_wakan_q0605_20.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7214) > 0L && (questState.getQuestItemsCount(7216) < 400L || questState.getQuestItemsCount(7217) < 400L || questState.getQuestItemsCount(7218) < 200L || questState.getQuestItemsCount(7220) < 1L)) {
                    string = "herald_wakan_q0605_22.htm";
                    break;
                }
                if (questState.getQuestItemsCount(7214) > 0L && questState.getQuestItemsCount(7216) >= 400L && questState.getQuestItemsCount(7217) >= 400L && questState.getQuestItemsCount(7218) >= 200L && questState.getQuestItemsCount(7220) > 0L) {
                    string = "herald_wakan_q0605_23.htm";
                    questState.giveItems(7215, 1L);
                    questState.takeItems(7214, -1L);
                    questState.takeItems(7216, -1L);
                    questState.takeItems(7217, -1L);
                    questState.takeItems(7218, -1L);
                    questState.takeItems(7220, -1L);
                    questState.setCond(6);
                    questState.playSound("ItemSound.quest_middle");
                    break;
                }
                if (questState.getQuestItemsCount(7215) <= 0L) break;
                string = "herald_wakan_q0605_24.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (questState.getState() == 2 && (questState.getQuestItemsCount(7211) + questState.getQuestItemsCount(7212) + questState.getQuestItemsCount(7213) + questState.getQuestItemsCount(7214) + questState.getQuestItemsCount(7215) == 0L && questState.getQuestItemsCount(7216) < 100L || questState.getQuestItemsCount(7211) >= 1L && questState.getQuestItemsCount(7216) < 200L || questState.getQuestItemsCount(7212) >= 1L && questState.getQuestItemsCount(7216) < 300L || questState.getQuestItemsCount(7213) >= 1L && questState.getQuestItemsCount(7216) < 300L || questState.getQuestItemsCount(7214) >= 1L && questState.getQuestItemsCount(7216) < 400L) && n == 21351 || n == 21350 || n == 21354 || n == 21353 || n == 21355) {
            questState.rollAndGive(7216, 1, 50.0);
        } else if (questState.getState() == 2 && (questState.getQuestItemsCount(7211) >= 1L && questState.getQuestItemsCount(7217) < 100L || questState.getQuestItemsCount(7212) >= 1L && questState.getQuestItemsCount(7217) < 200L || questState.getQuestItemsCount(7213) >= 1L && questState.getQuestItemsCount(7217) < 300L || questState.getQuestItemsCount(7214) >= 1L && questState.getQuestItemsCount(7217) < 400L) && (n == 21357 || n == 21358 || n == 21360 || n == 21361 || n == 21362 || n == 21369 || n == 21370)) {
            questState.rollAndGive(7217, 1, 51.0);
        } else if (questState.getState() == 2 && (questState.getQuestItemsCount(7212) >= 1L && questState.getQuestItemsCount(7218) < 100L || questState.getQuestItemsCount(7213) >= 1L && questState.getQuestItemsCount(7218) < 200L || questState.getQuestItemsCount(7214) >= 1L && questState.getQuestItemsCount(7218) < 200L) && (n == 21365 || n == 21366 || n == 21368 || n == 21373 || n == 21375 || n == 21374 || n == 21371 || n == 21372 || n == 31371)) {
            questState.rollAndGive(7218, 1, 50.0);
        } else if (n == 25306 || n == 25305 || n == 25302 || n == 21344 || n == 25299 || n == 21346 || n == 21345 || n == 21332 || n == 21336 || n == 21324 || n == 21343 || n == 21334 || n == 21339 || n == 21342 || n == 21340 || n == 21328 || n == 21338 || n == 21329 || n == 21327 || n == 21331 || n == 21347 || n == 21325 || n == 21349 || n == 21348) {
            if (questState.getQuestItemsCount(7215) >= 1L) {
                questState.takeItems(7215, -1L);
            } else if (questState.getQuestItemsCount(7214) >= 1L) {
                questState.takeItems(7214, -1L);
            } else if (questState.getQuestItemsCount(7213) >= 1L) {
                questState.takeItems(7213, -1L);
            } else if (questState.getQuestItemsCount(7212) >= 1L) {
                questState.takeItems(7212, -1L);
            } else if (questState.getQuestItemsCount(7211) >= 1L) {
                questState.takeItems(7211, -1L);
            }
            questState.takeItems(7216, -1L);
            questState.takeItems(7217, -1L);
            questState.takeItems(7218, -1L);
            questState.exitCurrentQuest(true);
        }
        return null;
    }
}

