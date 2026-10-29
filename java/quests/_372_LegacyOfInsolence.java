/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _372_LegacyOfInsolence
extends Quest
implements ScriptFile {
    private static final int btr = 30844;
    private static final int bts = 30839;
    private static final int btt = 30855;
    private static final int bqT = 30929;
    private static final int bnM = 31001;
    private static final int btu = 20817;
    private static final int btv = 20821;
    private static final int btw = 20825;
    private static final int bnS = 20829;
    private static final int btx = 21069;
    private static final int bty = 21063;
    private static final int btz = 5989;
    private static final int btA = 5990;
    private static final int btB = 5991;
    private static final int btC = 5992;
    private static final int btD = 5993;
    private static final int btE = 5994;
    private static final int btF = 5995;
    private static final int btG = 5996;
    private static final int btH = 5997;
    private static final int btI = 5998;
    private static final int btJ = 5999;
    private static final int btK = 6000;
    private static final int btL = 6001;
    private static final int btM = 5496;
    private static final int btN = 5508;
    private static final int btO = 5525;
    private static final int btP = 5368;
    private static final int btQ = 5392;
    private static final int btR = 5426;
    private static final int btS = 5497;
    private static final int btT = 5509;
    private static final int btU = 5526;
    private static final int btV = 5370;
    private static final int btW = 5394;
    private static final int btX = 5428;
    private static final int btY = 5502;
    private static final int btZ = 5514;
    private static final int bua = 5527;
    private static final int bub = 5380;
    private static final int buc = 5404;
    private static final int bud = 5430;
    private static final int bue = 5503;
    private static final int buf = 5515;
    private static final int bug = 5528;
    private static final int buh = 5382;
    private static final int bui = 5406;
    private static final int buj = 5432;
    private static final int buk = 5984;
    private static final int bul = 5985;
    private static final int bum = 5986;
    private static final int bun = 5987;
    private static final int buo = 5988;
    private static final int bup = 5972;
    private static final int buq = 5973;
    private static final int bur = 5974;
    private static final int bus = 5975;
    private static final int but = 5976;
    private static final int buu = 5977;
    private static final int buv = 5978;
    private static final int buw = 5979;
    private static final int bux = 5980;
    private static final int buy = 5981;
    private static final int buz = 5982;
    private static final int buA = 5983;
    private static final int buB = 5966;
    private static final int buC = 5967;
    private static final int buD = 5968;
    private static final int buE = 5969;

    public _372_LegacyOfInsolence() {
        super(1);
        this.addStartNpc(30844);
        this.addTalkId(30839, 30855, 30929, 31001);
        this.addKillId(20817, 20821, 20825, 20829, 21069, 21063);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30844) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("legacy_of_insolence", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "whouse_keeper_walderal_q0372_04.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=1")) {
                string2 = "whouse_keeper_walderal_q0372_03.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=3")) {
                if (questState.getQuestItemsCount(5989) < 1L || questState.getQuestItemsCount(5990) < 1L || questState.getQuestItemsCount(5991) < 1L || questState.getQuestItemsCount(5992) < 1L || questState.getQuestItemsCount(5993) < 1L || questState.getQuestItemsCount(5994) < 1L || questState.getQuestItemsCount(5995) < 1L || questState.getQuestItemsCount(5996) < 1L || questState.getQuestItemsCount(5997) < 1L || questState.getQuestItemsCount(5998) < 1L || questState.getQuestItemsCount(5999) < 1L || questState.getQuestItemsCount(6000) < 1L || questState.getQuestItemsCount(6001) < 1L) {
                    string2 = "whouse_keeper_walderal_q0372_06.htm";
                } else if (questState.getQuestItemsCount(5989) >= 1L && questState.getQuestItemsCount(5990) >= 1L && questState.getQuestItemsCount(5991) >= 1L && questState.getQuestItemsCount(5992) >= 1L && questState.getQuestItemsCount(5993) >= 1L && questState.getQuestItemsCount(5994) >= 1L && questState.getQuestItemsCount(5995) >= 1L && questState.getQuestItemsCount(5996) >= 1L && questState.getQuestItemsCount(5997) >= 1L && questState.getQuestItemsCount(5998) >= 1L && questState.getQuestItemsCount(5999) >= 1L && questState.getQuestItemsCount(6000) >= 1L && questState.getQuestItemsCount(6001) >= 1L) {
                    string2 = "whouse_keeper_walderal_q0372_07.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=4")) {
                string2 = "whouse_keeper_walderal_q0372_08.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=5")) {
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "whouse_keeper_walderal_q0372_09.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=6")) {
                string2 = "whouse_keeper_walderal_q0372_11.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=7")) {
                if (questState.getQuestItemsCount(5989) < 1L || questState.getQuestItemsCount(5990) < 1L || questState.getQuestItemsCount(5991) < 1L || questState.getQuestItemsCount(5992) < 1L || questState.getQuestItemsCount(5993) < 1L || questState.getQuestItemsCount(5994) < 1L || questState.getQuestItemsCount(5995) < 1L || questState.getQuestItemsCount(5996) < 1L || questState.getQuestItemsCount(5997) < 1L || questState.getQuestItemsCount(5998) < 1L || questState.getQuestItemsCount(5999) < 1L || questState.getQuestItemsCount(6000) < 1L || questState.getQuestItemsCount(6001) < 1L) {
                    string2 = "whouse_keeper_walderal_q0372_07e.htm";
                } else if (questState.getQuestItemsCount(5989) >= 1L && questState.getQuestItemsCount(5990) >= 1L && questState.getQuestItemsCount(5991) >= 1L && questState.getQuestItemsCount(5992) >= 1L && questState.getQuestItemsCount(5993) >= 1L && questState.getQuestItemsCount(5994) >= 1L && questState.getQuestItemsCount(5995) >= 1L && questState.getQuestItemsCount(5996) >= 1L && questState.getQuestItemsCount(5997) >= 1L && questState.getQuestItemsCount(5998) >= 1L && questState.getQuestItemsCount(5999) >= 1L && questState.getQuestItemsCount(6000) >= 1L && questState.getQuestItemsCount(6001) >= 1L) {
                    questState.takeItems(5989, 1L);
                    questState.takeItems(5990, 1L);
                    questState.takeItems(5991, 1L);
                    questState.takeItems(5992, 1L);
                    questState.takeItems(5993, 1L);
                    questState.takeItems(5994, 1L);
                    questState.takeItems(5995, 1L);
                    questState.takeItems(5996, 1L);
                    questState.takeItems(5997, 1L);
                    questState.takeItems(5998, 1L);
                    questState.takeItems(5999, 1L);
                    questState.takeItems(6000, 1L);
                    questState.takeItems(6001, 1L);
                    int n2 = Rnd.get(100);
                    if (n2 < 10) {
                        questState.giveItems(5496, 1L);
                    } else if (n2 < 20) {
                        questState.giveItems(5508, 1L);
                    } else if (n2 < 30) {
                        questState.giveItems(5525, 1L);
                    } else if (n2 < 40) {
                        questState.giveItems(5496, 1L);
                        questState.giveItems(5508, 1L);
                        questState.giveItems(5525, 1L);
                    } else if (n2 < 51) {
                        questState.giveItems(5368, 1L);
                    } else if (n2 < 62) {
                        questState.giveItems(5392, 1L);
                    } else if (n2 < 79) {
                        questState.giveItems(5426, 1L);
                    } else if (n2 < 100) {
                        questState.giveItems(5368, 1L);
                        questState.giveItems(5392, 1L);
                        questState.giveItems(5426, 1L);
                    }
                    string2 = "whouse_keeper_walderal_q0372_07a.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=8")) {
                if (questState.getQuestItemsCount(5989) < 1L || questState.getQuestItemsCount(5990) < 1L || questState.getQuestItemsCount(5991) < 1L || questState.getQuestItemsCount(5992) < 1L || questState.getQuestItemsCount(5993) < 1L || questState.getQuestItemsCount(5994) < 1L || questState.getQuestItemsCount(5995) < 1L || questState.getQuestItemsCount(5996) < 1L || questState.getQuestItemsCount(5997) < 1L || questState.getQuestItemsCount(5998) < 1L || questState.getQuestItemsCount(5999) < 1L || questState.getQuestItemsCount(6000) < 1L || questState.getQuestItemsCount(6001) < 1L) {
                    string2 = "whouse_keeper_walderal_q0372_07e.htm";
                } else if (questState.getQuestItemsCount(5989) >= 1L && questState.getQuestItemsCount(5990) >= 1L && questState.getQuestItemsCount(5991) >= 1L && questState.getQuestItemsCount(5992) >= 1L && questState.getQuestItemsCount(5993) >= 1L && questState.getQuestItemsCount(5994) >= 1L && questState.getQuestItemsCount(5995) >= 1L && questState.getQuestItemsCount(5996) >= 1L && questState.getQuestItemsCount(5997) >= 1L && questState.getQuestItemsCount(5998) >= 1L && questState.getQuestItemsCount(5999) >= 1L && questState.getQuestItemsCount(6000) >= 1L && questState.getQuestItemsCount(6001) >= 1L) {
                    questState.takeItems(5989, 1L);
                    questState.takeItems(5990, 1L);
                    questState.takeItems(5991, 1L);
                    questState.takeItems(5992, 1L);
                    questState.takeItems(5993, 1L);
                    questState.takeItems(5994, 1L);
                    questState.takeItems(5995, 1L);
                    questState.takeItems(5996, 1L);
                    questState.takeItems(5997, 1L);
                    questState.takeItems(5998, 1L);
                    questState.takeItems(5999, 1L);
                    questState.takeItems(6000, 1L);
                    questState.takeItems(6001, 1L);
                    int n3 = Rnd.get(100);
                    if (n3 < 10) {
                        questState.giveItems(5497, 1L);
                    } else if (n3 < 20) {
                        questState.giveItems(5509, 1L);
                    } else if (n3 < 30) {
                        questState.giveItems(5526, 1L);
                    } else if (n3 < 40) {
                        questState.giveItems(5497, 1L);
                        questState.giveItems(5509, 1L);
                        questState.giveItems(5526, 1L);
                    } else if (n3 < 51) {
                        questState.giveItems(5370, 1L);
                    } else if (n3 < 62) {
                        questState.giveItems(5394, 1L);
                    } else if (n3 < 79) {
                        questState.giveItems(5428, 1L);
                    } else if (n3 < 100) {
                        questState.giveItems(5370, 1L);
                        questState.giveItems(5394, 1L);
                        questState.giveItems(5428, 1L);
                    }
                    string2 = "whouse_keeper_walderal_q0372_07b.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=9")) {
                if (questState.getQuestItemsCount(5989) < 1L || questState.getQuestItemsCount(5990) < 1L || questState.getQuestItemsCount(5991) < 1L || questState.getQuestItemsCount(5992) < 1L || questState.getQuestItemsCount(5993) < 1L || questState.getQuestItemsCount(5994) < 1L || questState.getQuestItemsCount(5995) < 1L || questState.getQuestItemsCount(5996) < 1L || questState.getQuestItemsCount(5997) < 1L || questState.getQuestItemsCount(5998) < 1L || questState.getQuestItemsCount(5999) < 1L || questState.getQuestItemsCount(6000) < 1L || questState.getQuestItemsCount(6001) < 1L) {
                    string2 = "whouse_keeper_walderal_q0372_07e.htm";
                } else if (questState.getQuestItemsCount(5989) >= 1L && questState.getQuestItemsCount(5990) >= 1L && questState.getQuestItemsCount(5991) >= 1L && questState.getQuestItemsCount(5992) >= 1L && questState.getQuestItemsCount(5993) >= 1L && questState.getQuestItemsCount(5994) >= 1L && questState.getQuestItemsCount(5995) >= 1L && questState.getQuestItemsCount(5996) >= 1L && questState.getQuestItemsCount(5997) >= 1L && questState.getQuestItemsCount(5998) >= 1L && questState.getQuestItemsCount(5999) >= 1L && questState.getQuestItemsCount(6000) >= 1L && questState.getQuestItemsCount(6001) >= 1L) {
                    questState.takeItems(5989, 1L);
                    questState.takeItems(5990, 1L);
                    questState.takeItems(5991, 1L);
                    questState.takeItems(5992, 1L);
                    questState.takeItems(5993, 1L);
                    questState.takeItems(5994, 1L);
                    questState.takeItems(5995, 1L);
                    questState.takeItems(5996, 1L);
                    questState.takeItems(5997, 1L);
                    questState.takeItems(5998, 1L);
                    questState.takeItems(5999, 1L);
                    questState.takeItems(6000, 1L);
                    questState.takeItems(6001, 1L);
                    int n4 = Rnd.get(100);
                    if (n4 < 17) {
                        questState.giveItems(5502, 1L);
                    } else if (n4 < 34) {
                        questState.giveItems(5514, 1L);
                    } else if (n4 < 49) {
                        questState.giveItems(5527, 1L);
                    } else if (n4 < 58) {
                        questState.giveItems(5502, 1L);
                        questState.giveItems(5514, 1L);
                        questState.giveItems(5527, 1L);
                    } else if (n4 < 70) {
                        questState.giveItems(5380, 1L);
                    } else if (n4 < 82) {
                        questState.giveItems(5404, 1L);
                    } else if (n4 < 92) {
                        questState.giveItems(5430, 1L);
                    } else if (n4 < 100) {
                        questState.giveItems(5380, 1L);
                        questState.giveItems(5404, 1L);
                        questState.giveItems(5430, 1L);
                    }
                    string2 = "whouse_keeper_walderal_q0372_07c.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=10")) {
                if (questState.getQuestItemsCount(5989) < 1L || questState.getQuestItemsCount(5990) < 1L || questState.getQuestItemsCount(5991) < 1L || questState.getQuestItemsCount(5992) < 1L || questState.getQuestItemsCount(5993) < 1L || questState.getQuestItemsCount(5994) < 1L || questState.getQuestItemsCount(5995) < 1L || questState.getQuestItemsCount(5996) < 1L || questState.getQuestItemsCount(5997) < 1L || questState.getQuestItemsCount(5998) < 1L || questState.getQuestItemsCount(5999) < 1L || questState.getQuestItemsCount(6000) < 1L || questState.getQuestItemsCount(6001) < 1L) {
                    string2 = "whouse_keeper_walderal_q0372_07e.htm";
                } else if (questState.getQuestItemsCount(5989) >= 1L && questState.getQuestItemsCount(5990) >= 1L && questState.getQuestItemsCount(5991) >= 1L && questState.getQuestItemsCount(5992) >= 1L && questState.getQuestItemsCount(5993) >= 1L && questState.getQuestItemsCount(5994) >= 1L && questState.getQuestItemsCount(5995) >= 1L && questState.getQuestItemsCount(5996) >= 1L && questState.getQuestItemsCount(5997) >= 1L && questState.getQuestItemsCount(5998) >= 1L && questState.getQuestItemsCount(5999) >= 1L && questState.getQuestItemsCount(6000) >= 1L && questState.getQuestItemsCount(6001) >= 1L) {
                    questState.takeItems(5989, 1L);
                    questState.takeItems(5990, 1L);
                    questState.takeItems(5991, 1L);
                    questState.takeItems(5992, 1L);
                    questState.takeItems(5993, 1L);
                    questState.takeItems(5994, 1L);
                    questState.takeItems(5995, 1L);
                    questState.takeItems(5996, 1L);
                    questState.takeItems(5997, 1L);
                    questState.takeItems(5998, 1L);
                    questState.takeItems(5999, 1L);
                    questState.takeItems(6000, 1L);
                    questState.takeItems(6001, 1L);
                    int n5 = Rnd.get(100);
                    if (n5 < 17) {
                        questState.giveItems(5503, 1L);
                    } else if (n5 < 34) {
                        questState.giveItems(5515, 1L);
                    } else if (n5 < 49) {
                        questState.giveItems(5528, 1L);
                    } else if (n5 < 58) {
                        questState.giveItems(5503, 1L);
                        questState.giveItems(5515, 1L);
                        questState.giveItems(5528, 1L);
                    } else if (n5 < 70) {
                        questState.giveItems(5382, 1L);
                    } else if (n5 < 82) {
                        questState.giveItems(5406, 1L);
                    } else if (n5 < 92) {
                        questState.giveItems(5432, 1L);
                    } else if (n5 < 100) {
                        questState.giveItems(5382, 1L);
                        questState.giveItems(5406, 1L);
                        questState.giveItems(5432, 1L);
                    }
                    string2 = "whouse_keeper_walderal_q0372_07d.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=372&reply=99")) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
                string2 = "whouse_keeper_walderal_q0372_05b.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("legacy_of_insolence");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30844) break;
                if (questState.getPlayer().getLevel() < 59) {
                    string = "whouse_keeper_walderal_q0372_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() < 59) break;
                string = "whouse_keeper_walderal_q0372_02.htm";
                break;
            }
            case 2: {
                if (n2 == 30844) {
                    if (n != 1) break;
                    string = "whouse_keeper_walderal_q0372_05.htm";
                    break;
                }
                if (n2 == 30839) {
                    if (questState.getQuestItemsCount(5984) < 1L || questState.getQuestItemsCount(5985) < 1L || questState.getQuestItemsCount(5986) < 1L || questState.getQuestItemsCount(5987) < 1L || questState.getQuestItemsCount(5988) < 1L) {
                        string = "trader_holly_q0372_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(5984) < 1L || questState.getQuestItemsCount(5985) < 1L || questState.getQuestItemsCount(5986) < 1L || questState.getQuestItemsCount(5987) < 1L || questState.getQuestItemsCount(5988) < 1L) break;
                    questState.takeItems(5984, 1L);
                    questState.takeItems(5985, 1L);
                    questState.takeItems(5986, 1L);
                    questState.takeItems(5987, 1L);
                    questState.takeItems(5988, 1L);
                    int n4 = Rnd.get(100);
                    if (n4 < 30) {
                        questState.giveItems(5496, 1L);
                    } else if (n4 < 60) {
                        questState.giveItems(5508, 1L);
                    } else if (n4 < 80) {
                        questState.giveItems(5525, 1L);
                    } else if (n4 < 90) {
                        questState.giveItems(5496, 1L);
                        questState.giveItems(5508, 1L);
                        questState.giveItems(5525, 1L);
                    } else if (n4 < 100) {
                        questState.giveItems(57, 4000L);
                    }
                    string = "trader_holly_q0372_02.htm";
                    break;
                }
                if (n2 == 30855) {
                    if (questState.getQuestItemsCount(5972) < 1L || questState.getQuestItemsCount(5973) < 1L || questState.getQuestItemsCount(5974) < 1L || questState.getQuestItemsCount(5975) < 1L || questState.getQuestItemsCount(5976) < 1L || questState.getQuestItemsCount(5977) < 1L || questState.getQuestItemsCount(5978) < 1L) {
                        string = "magister_desmond_q0372_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(5972) < 1L || questState.getQuestItemsCount(5973) < 1L || questState.getQuestItemsCount(5974) < 1L || questState.getQuestItemsCount(5975) < 1L || questState.getQuestItemsCount(5976) < 1L || questState.getQuestItemsCount(5977) < 1L || questState.getQuestItemsCount(5978) < 1L) break;
                    questState.takeItems(5972, 1L);
                    questState.takeItems(5973, 1L);
                    questState.takeItems(5974, 1L);
                    questState.takeItems(5975, 1L);
                    questState.takeItems(5976, 1L);
                    questState.takeItems(5977, 1L);
                    questState.takeItems(5978, 1L);
                    int n5 = Rnd.get(100);
                    if (n5 < 31) {
                        questState.giveItems(5503, 1L);
                    } else if (n5 < 62) {
                        questState.giveItems(5515, 1L);
                    } else if (n5 < 75) {
                        questState.giveItems(5528, 1L);
                    } else if (n5 < 83) {
                        questState.giveItems(5503, 1L);
                        questState.giveItems(5515, 1L);
                        questState.giveItems(5528, 1L);
                    } else if (n5 < 100) {
                        questState.giveItems(57, 4000L);
                    }
                    string = "magister_desmond_q0372_02.htm";
                    break;
                }
                if (n2 == 30929) {
                    if (questState.getQuestItemsCount(5979) < 1L || questState.getQuestItemsCount(5980) < 1L || questState.getQuestItemsCount(5981) < 1L || questState.getQuestItemsCount(5982) < 1L || questState.getQuestItemsCount(5983) < 1L) {
                        string = "patrin_q0372_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(5979) < 1L || questState.getQuestItemsCount(5980) < 1L || questState.getQuestItemsCount(5981) < 1L || questState.getQuestItemsCount(5982) < 1L || questState.getQuestItemsCount(5983) < 1L) break;
                    questState.takeItems(5979, 1L);
                    questState.takeItems(5980, 1L);
                    questState.takeItems(5981, 1L);
                    questState.takeItems(5982, 1L);
                    questState.takeItems(5983, 1L);
                    int n6 = Rnd.get(100);
                    if (n6 < 30) {
                        questState.giveItems(5497, 1L);
                    } else if (n6 < 60) {
                        questState.giveItems(5509, 1L);
                    } else if (n6 < 80) {
                        questState.giveItems(5526, 1L);
                    } else if (n6 < 90) {
                        questState.giveItems(5497, 1L);
                        questState.giveItems(5509, 1L);
                        questState.giveItems(5526, 1L);
                    } else if (n6 < 100) {
                        questState.giveItems(57, 4000L);
                    }
                    string = "patrin_q0372_02.htm";
                    break;
                }
                if (n2 != 31001) break;
                if (questState.getQuestItemsCount(5972) < 1L || questState.getQuestItemsCount(5973) < 1L || questState.getQuestItemsCount(5974) < 1L || questState.getQuestItemsCount(5975) < 1L || questState.getQuestItemsCount(5976) < 1L || questState.getQuestItemsCount(5977) < 1L || questState.getQuestItemsCount(5978) < 1L) {
                    string = "claudia_a_q0372_01.htm";
                    break;
                }
                if (questState.getQuestItemsCount(5972) < 1L || questState.getQuestItemsCount(5973) < 1L || questState.getQuestItemsCount(5974) < 1L || questState.getQuestItemsCount(5975) < 1L || questState.getQuestItemsCount(5976) < 1L || questState.getQuestItemsCount(5977) < 1L || questState.getQuestItemsCount(5978) < 1L) break;
                questState.takeItems(5972, 1L);
                questState.takeItems(5973, 1L);
                questState.takeItems(5974, 1L);
                questState.takeItems(5975, 1L);
                questState.takeItems(5976, 1L);
                questState.takeItems(5977, 1L);
                questState.takeItems(5978, 1L);
                int n7 = Rnd.get(100);
                if (n7 < 31) {
                    questState.giveItems(5502, 1L);
                } else if (n7 < 62) {
                    questState.giveItems(5514, 1L);
                } else if (n7 < 75) {
                    questState.giveItems(5527, 1L);
                } else if (n7 < 83) {
                    questState.giveItems(5502, 1L);
                    questState.giveItems(5514, 1L);
                    questState.giveItems(5527, 1L);
                } else if (n7 < 100) {
                    questState.giveItems(57, 4000L);
                }
                string = "claudia_a_q0372_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20817) {
            if (Rnd.get(1000) < 302) {
                questState.rollAndGive(5966, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20821) {
            if (Rnd.get(100) < 41) {
                questState.rollAndGive(5966, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20825) {
            if (Rnd.get(1000) < 447) {
                questState.rollAndGive(5966, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20829) {
            if (Rnd.get(1000) < 451) {
                questState.rollAndGive(5967, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21069) {
            if (Rnd.get(100) < 28) {
                questState.rollAndGive(5968, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21063 && Rnd.get(100) < 29) {
            questState.rollAndGive(5969, 1, 100.0);
            questState.playSound("ItemSound.quest_itemget");
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

