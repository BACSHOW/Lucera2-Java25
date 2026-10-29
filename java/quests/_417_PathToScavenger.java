/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _417_PathToScavenger
extends Quest
implements ScriptFile {
    int RING_OF_RAVEN = 1642;
    int PIPIS_LETTER = 1643;
    int ROUTS_TP_SCROLL = 1644;
    int SUCCUBUS_UNDIES = 1645;
    int MIONS_LETTER = 1646;
    int BRONKS_INGOT = 1647;
    int CHARIS_AXE = 1648;
    int ZIMENFS_POTION = 1649;
    int BRONKS_PAY = 1650;
    int CHALIS_PAY = 1651;
    int ZIMENFS_PAY = 1652;
    int BEAR_PIC = 1653;
    int TARANTULA_PIC = 1654;
    int HONEY_JAR = 1655;
    int BEAD = 1656;
    int BEAD_PARCEL = 1657;
    int Pippi = 30524;
    int Raut = 30316;
    int Shari = 30517;
    int Mion = 30519;
    int Bronk = 30525;
    int Zimenf = 30538;
    int Toma = 30556;
    int Torai = 30557;
    int HunterTarantula = 20403;
    int HoneyBear = 27058;
    int PlunderTarantula = 20508;
    int HunterBear = 20777;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _417_PathToScavenger() {
        super(0);
        this.addStartNpc(this.Pippi);
        this.addTalkId(this.Raut);
        this.addTalkId(this.Shari);
        this.addTalkId(this.Mion);
        this.addTalkId(this.Bronk);
        this.addTalkId(this.Zimenf);
        this.addTalkId(this.Toma);
        this.addTalkId(this.Torai);
        this.addKillId(this.HunterTarantula);
        this.addKillId(this.HoneyBear);
        this.addKillId(this.PlunderTarantula);
        this.addKillId(this.HunterBear);
        this.addQuestItem(this.CHALIS_PAY);
        this.addQuestItem(this.ZIMENFS_PAY);
        this.addQuestItem(this.BRONKS_PAY);
        this.addQuestItem(this.PIPIS_LETTER);
        this.addQuestItem(this.CHARIS_AXE);
        this.addQuestItem(this.ZIMENFS_POTION);
        this.addQuestItem(this.BRONKS_INGOT);
        this.addQuestItem(this.MIONS_LETTER);
        this.addQuestItem(this.HONEY_JAR);
        this.addQuestItem(this.BEAR_PIC);
        this.addQuestItem(this.BEAD_PARCEL);
        this.addQuestItem(this.BEAD);
        this.addQuestItem(this.TARANTULA_PIC);
        this.addQuestItem(this.SUCCUBUS_UNDIES);
        this.addQuestItem(this.ROUTS_TP_SCROLL);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        if (string.equals("1")) {
            questState.set("id", "0");
            if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 53 && questState.getQuestItemsCount(this.RING_OF_RAVEN) == 0L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.giveItems(this.PIPIS_LETTER, 1L);
                string2 = "collector_pipi_q0417_05.htm";
            } else if (questState.getPlayer().getClassId().getId() != 53) {
                string2 = questState.getPlayer().getClassId().getId() == 54 ? "collector_pipi_q0417_02a.htm" : "collector_pipi_q0417_08.htm";
            } else if (questState.getPlayer().getLevel() < 18 && questState.getPlayer().getClassId().getId() == 53) {
                string2 = "collector_pipi_q0417_02.htm";
            } else if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 53 && questState.getQuestItemsCount(this.RING_OF_RAVEN) == 1L) {
                string2 = "collector_pipi_q0417_04.htm";
            }
        } else if (string.equals("30519_1")) {
            if (questState.getQuestItemsCount(this.PIPIS_LETTER) > 0L) {
                questState.takeItems(this.PIPIS_LETTER, 1L);
                questState.setCond(2);
                int n2 = Rnd.get(3);
                if (n2 == 0) {
                    string2 = "trader_mion_q0417_02.htm";
                    questState.giveItems(this.ZIMENFS_POTION, 1L);
                } else if (n2 == 1) {
                    string2 = "trader_mion_q0417_03.htm";
                    questState.giveItems(this.CHARIS_AXE, 1L);
                } else if (n2 == 2) {
                    string2 = "trader_mion_q0417_04.htm";
                    questState.giveItems(this.BRONKS_INGOT, 1L);
                }
            } else {
                string2 = "noquest";
            }
        } else if (string.equals("30519_2")) {
            string2 = "trader_mion_q0417_06.htm";
        } else if (string.equals("30519_3")) {
            string2 = "trader_mion_q0417_07.htm";
            questState.set("id", String.valueOf(questState.getInt("id") + 1));
        } else if (string.equals("30519_4")) {
            int n3 = Rnd.get(2);
            if (n3 == 0) {
                string2 = "trader_mion_q0417_06.htm";
            } else if (n3 == 1) {
                string2 = "trader_mion_q0417_11.htm";
            }
        } else if (string.equals("30519_5")) {
            if (questState.getQuestItemsCount(this.ZIMENFS_POTION, this.CHARIS_AXE, this.BRONKS_INGOT) > 0L) {
                if (questState.getInt("id") / 10 < 2) {
                    string2 = "trader_mion_q0417_07.htm";
                    questState.set("id", String.valueOf(questState.getInt("id") + 1));
                } else if (questState.getInt("id") / 10 >= 2 && n == 0) {
                    string2 = "trader_mion_q0417_09.htm";
                    if (questState.getInt("id") / 10 < 3) {
                        questState.set("id", String.valueOf(questState.getInt("id") + 1));
                    }
                } else if (questState.getInt("id") / 10 >= 3 && n > 0) {
                    string2 = "trader_mion_q0417_10.htm";
                    questState.giveItems(this.MIONS_LETTER, 1L);
                    questState.takeItems(this.CHARIS_AXE, 1L);
                    questState.takeItems(this.ZIMENFS_POTION, 1L);
                    questState.takeItems(this.BRONKS_INGOT, 1L);
                }
            } else {
                string2 = "noquest";
            }
        } else if (string.equals("30519_6")) {
            if (questState.getQuestItemsCount(this.ZIMENFS_PAY) > 0L || questState.getQuestItemsCount(this.CHALIS_PAY) > 0L || questState.getQuestItemsCount(this.BRONKS_PAY) > 0L) {
                int n4 = Rnd.get(3);
                questState.takeItems(this.ZIMENFS_PAY, 1L);
                questState.takeItems(this.CHALIS_PAY, 1L);
                questState.takeItems(this.BRONKS_PAY, 1L);
                if (n4 == 0) {
                    string2 = "trader_mion_q0417_02.htm";
                    questState.giveItems(this.ZIMENFS_POTION, 1L);
                } else if (n4 == 1) {
                    string2 = "trader_mion_q0417_03.htm";
                    questState.giveItems(this.CHARIS_AXE, 1L);
                } else if (n4 == 2) {
                    string2 = "trader_mion_q0417_04.htm";
                    questState.giveItems(this.BRONKS_INGOT, 1L);
                }
            } else {
                string2 = "noquest";
            }
        } else if (string.equals("30316_1")) {
            if (questState.getQuestItemsCount(this.BEAD_PARCEL) > 0L) {
                string2 = "raut_q0417_02.htm";
                questState.takeItems(this.BEAD_PARCEL, 1L);
                questState.giveItems(this.ROUTS_TP_SCROLL, 1L);
                questState.setCond(10);
            } else {
                string2 = "noquest";
            }
        } else if (string.equals("30316_2")) {
            if (questState.getQuestItemsCount(this.BEAD_PARCEL) > 0L) {
                string2 = "raut_q0417_03.htm";
                questState.takeItems(this.BEAD_PARCEL, 1L);
                questState.giveItems(this.ROUTS_TP_SCROLL, 1L);
                questState.setCond(10);
            } else {
                string2 = "noquest";
            }
        } else if (string.equals("30557_1")) {
            string2 = "torai_q0417_02.htm";
        } else if (string.equals("30557_2")) {
            if (questState.getQuestItemsCount(this.ROUTS_TP_SCROLL) > 0L) {
                string2 = "torai_q0417_03.htm";
                questState.takeItems(this.ROUTS_TP_SCROLL, 1L);
                questState.giveItems(this.SUCCUBUS_UNDIES, 1L);
                questState.setCond(11);
            } else {
                string2 = "noquest";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n2 == 1) {
            questState.setState(2);
        }
        if (n == this.Pippi) {
            if (n3 == 0) {
                string = "collector_pipi_q0417_01.htm";
            } else if (questState.getQuestItemsCount(this.PIPIS_LETTER) > 0L) {
                string = "collector_pipi_q0417_06.htm";
            } else if (questState.getQuestItemsCount(this.PIPIS_LETTER) == 0L && n2 == 2) {
                string = "collector_pipi_q0417_01.htm";
            } else if (questState.getQuestItemsCount(this.PIPIS_LETTER) == 0L) {
                string = "collector_pipi_q0417_07.htm";
            }
        } else {
            if (n3 == 0) {
                return "noquest";
            }
            if (n == this.Mion) {
                if (questState.getQuestItemsCount(this.PIPIS_LETTER) > 0L) {
                    string = "trader_mion_q0417_01.htm";
                } else if (questState.getQuestItemsCount(this.CHARIS_AXE, this.BRONKS_INGOT, this.ZIMENFS_POTION) > 0L && questState.getInt("id") / 10 == 0) {
                    string = "trader_mion_q0417_05.htm";
                } else if (questState.getQuestItemsCount(this.CHARIS_AXE, this.BRONKS_INGOT, this.ZIMENFS_POTION) > 0L && questState.getInt("id") / 10 > 0) {
                    string = "trader_mion_q0417_08.htm";
                } else if (questState.getQuestItemsCount(this.CHALIS_PAY, this.BRONKS_PAY, this.ZIMENFS_PAY) > 0L && questState.getInt("id") < 50) {
                    string = "trader_mion_q0417_12.htm";
                } else if (questState.getQuestItemsCount(this.CHALIS_PAY, this.BRONKS_PAY, this.ZIMENFS_PAY) > 0L && questState.getInt("id") >= 50) {
                    string = "trader_mion_q0417_15.htm";
                    questState.giveItems(this.MIONS_LETTER, 1L);
                    questState.takeItems(this.CHALIS_PAY, -1L);
                    questState.takeItems(this.ZIMENFS_PAY, -1L);
                    questState.takeItems(this.BRONKS_PAY, -1L);
                    questState.setCond(4);
                } else if (questState.getQuestItemsCount(this.MIONS_LETTER) > 0L) {
                    string = "trader_mion_q0417_13.htm";
                } else if (questState.getQuestItemsCount(this.BEAR_PIC) > 0L || questState.getQuestItemsCount(this.TARANTULA_PIC) > 0L || questState.getQuestItemsCount(this.BEAD_PARCEL) > 0L || questState.getQuestItemsCount(this.ROUTS_TP_SCROLL) > 0L || questState.getQuestItemsCount(this.SUCCUBUS_UNDIES) > 0L) {
                    string = "trader_mion_q0417_14.htm";
                }
            } else if (n == this.Shari) {
                if (questState.getQuestItemsCount(this.CHARIS_AXE) > 0L) {
                    string = questState.getInt("id") < 20 ? "trader_chali_q0417_01.htm" : "trader_chali_q0417_02.htm";
                    questState.takeItems(this.CHARIS_AXE, 1L);
                    questState.giveItems(this.CHALIS_PAY, 1L);
                    if (questState.getInt("id") >= 50) {
                        questState.setCond(3);
                    }
                    questState.set("id", questState.getInt("id") + 10);
                } else if (questState.getQuestItemsCount(this.CHALIS_PAY) == 1L) {
                    string = "trader_chali_q0417_03.htm";
                }
            } else if (n == this.Bronk) {
                if (questState.getQuestItemsCount(this.BRONKS_INGOT) == 1L) {
                    string = questState.getInt("id") < 20 ? "head_blacksmith_bronk_q0417_01.htm" : "head_blacksmith_bronk_q0417_02.htm";
                    questState.takeItems(this.BRONKS_INGOT, 1L);
                    questState.giveItems(this.BRONKS_PAY, 1L);
                    if (questState.getInt("id") >= 50) {
                        questState.setCond(3);
                    }
                    questState.set("id", questState.getInt("id") + 10);
                } else if (questState.getQuestItemsCount(this.BRONKS_PAY) == 1L) {
                    string = "head_blacksmith_bronk_q0417_03.htm";
                }
            } else if (n == this.Zimenf) {
                if (questState.getQuestItemsCount(this.ZIMENFS_POTION) == 1L) {
                    string = questState.getInt("id") < 20 ? "zimenf_priest_of_earth_q0417_01.htm" : "zimenf_priest_of_earth_q0417_02.htm";
                    questState.takeItems(this.ZIMENFS_POTION, 1L);
                    questState.giveItems(this.ZIMENFS_PAY, 1L);
                    if (questState.getInt("id") >= 50) {
                        questState.setCond(3);
                    }
                    questState.set("id", questState.getInt("id") + 10);
                } else if (questState.getQuestItemsCount(this.ZIMENFS_PAY) == 1L) {
                    string = "zimenf_priest_of_earth_q0417_03.htm";
                }
            } else if (n == this.Toma) {
                if (questState.getQuestItemsCount(this.MIONS_LETTER) == 1L) {
                    string = "master_toma_q0417_01.htm";
                    questState.takeItems(this.MIONS_LETTER, 1L);
                    questState.giveItems(this.BEAR_PIC, 1L);
                    questState.setCond(5);
                    questState.set("id", String.valueOf(0));
                } else if (questState.getQuestItemsCount(this.BEAR_PIC) == 1L && questState.getQuestItemsCount(this.HONEY_JAR) < 5L) {
                    string = "master_toma_q0417_02.htm";
                } else if (questState.getQuestItemsCount(this.BEAR_PIC) == 1L && questState.getQuestItemsCount(this.HONEY_JAR) >= 5L) {
                    string = "master_toma_q0417_03.htm";
                    questState.takeItems(this.HONEY_JAR, questState.getQuestItemsCount(this.HONEY_JAR));
                    questState.takeItems(this.BEAR_PIC, 1L);
                    questState.giveItems(this.TARANTULA_PIC, 1L);
                    questState.setCond(7);
                } else if (questState.getQuestItemsCount(this.TARANTULA_PIC) == 1L && questState.getQuestItemsCount(this.BEAD) < 20L) {
                    string = "master_toma_q0417_04.htm";
                } else if (questState.getQuestItemsCount(this.TARANTULA_PIC) == 1L && questState.getQuestItemsCount(this.BEAD) >= 20L) {
                    string = "master_toma_q0417_05.htm";
                    questState.takeItems(this.BEAD, questState.getQuestItemsCount(this.BEAD));
                    questState.takeItems(this.TARANTULA_PIC, 1L);
                    questState.giveItems(this.BEAD_PARCEL, 1L);
                    questState.setCond(9);
                } else if (questState.getQuestItemsCount(this.BEAD_PARCEL) > 0L) {
                    string = "master_toma_q0417_06.htm";
                } else if (questState.getQuestItemsCount(this.ROUTS_TP_SCROLL) > 0L || questState.getQuestItemsCount(this.SUCCUBUS_UNDIES) > 0L) {
                    string = "master_toma_q0417_07.htm";
                }
            } else if (n == this.Raut) {
                if (questState.getQuestItemsCount(this.BEAD_PARCEL) == 1L) {
                    string = "raut_q0417_01.htm";
                } else if (questState.getQuestItemsCount(this.ROUTS_TP_SCROLL) == 1L) {
                    string = "raut_q0417_04.htm";
                } else if (questState.getQuestItemsCount(this.SUCCUBUS_UNDIES) == 1L) {
                    string = "raut_q0417_05.htm";
                    questState.takeItems(this.SUCCUBUS_UNDIES, 1L);
                    if (questState.getPlayer().getClassId().getLevel() == 1) {
                        questState.giveItems(this.RING_OF_RAVEN, 1L);
                        if (!questState.getPlayer().getVarB("prof1")) {
                            questState.getPlayer().setVar("prof1", "1", -1L);
                            questState.addExpAndSp(3200L, 7080L);
                        }
                    }
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                }
            } else if (n == this.Torai && questState.getQuestItemsCount(this.ROUTS_TP_SCROLL) == 1L) {
                string = "torai_q0417_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        boolean bl;
        int n = npcInstance.getNpcId();
        MonsterInstance monsterInstance = (MonsterInstance)npcInstance;
        boolean bl2 = bl = questState.getCond() > 0;
        if (n == this.HunterBear) {
            if (bl && questState.getQuestItemsCount(this.BEAR_PIC) == 1L && questState.getQuestItemsCount(this.HONEY_JAR) < 5L && Rnd.chance(20)) {
                questState.addSpawn(this.HoneyBear);
            }
        } else if (n == this.HoneyBear) {
            if (bl && questState.getQuestItemsCount(this.BEAR_PIC) == 1L && questState.getQuestItemsCount(this.HONEY_JAR) < 5L && monsterInstance.isSpoiled()) {
                questState.giveItems(this.HONEY_JAR, 1L);
                if (questState.getQuestItemsCount(this.HONEY_JAR) == 5L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(6);
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if ((n == this.HunterTarantula || n == this.PlunderTarantula) && bl && questState.getQuestItemsCount(this.TARANTULA_PIC) == 1L && questState.getQuestItemsCount(this.BEAD) < 20L && monsterInstance.isSpoiled() && Rnd.chance(50)) {
            questState.giveItems(this.BEAD, 1L);
            if (questState.getQuestItemsCount(this.BEAD) == 20L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(8);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

