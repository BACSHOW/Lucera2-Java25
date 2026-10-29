/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;

public class _235_MimirsElixir
extends Quest
implements ScriptFile {
    private static final int bbj = 30721;
    private static final int bbk = 30718;
    private static final int bbl = 31149;
    private static final int bbm = 20965;
    private static final int bbn = 21090;
    private static final int aZX = 5011;
    private static final int bbo = 5905;
    private static final int bbp = 6318;
    private static final int bbq = 6319;
    private static final int bbr = 6320;
    private static final int bbt = 6321;
    private static final int bbu = 6322;
    private static final int aGI = 729;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _235_MimirsElixir() {
        super(0);
        this.addStartNpc(30721);
        this.addTalkId(30721, 30718, 31149);
        this.addKillId(20965, 21090);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("water_of_mimir");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30721) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("water_of_mimir", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "magister_ladd_q0235_06.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "magister_ladd_q0235_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "magister_ladd_q0235_03.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "magister_ladd_q0235_04.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "magister_ladd_q0235_05.htm";
            } else if (string.equalsIgnoreCase("reply_5") && n == 1) {
                questState.setCond(2);
                questState.set("water_of_mimir", String.valueOf(2), true);
                string2 = "magister_ladd_q0235_09.htm";
            } else if (string.equalsIgnoreCase("reply_6") && n == 5) {
                questState.setCond(6);
                questState.set("water_of_mimir", String.valueOf(6), true);
                questState.giveItems(5905, 1L);
                string2 = "magister_ladd_q0235_12.htm";
            } else if (string.equalsIgnoreCase("reply_7") && n == 8) {
                string2 = "magister_ladd_q0235_15.htm";
            } else if (string.equalsIgnoreCase("reply_8") && n == 8 && questState.getQuestItemsCount(5905) >= 1L && questState.getQuestItemsCount(6319) >= 1L) {
                npcInstance.doCast(SkillTable.getInstance().getInfo(4339, 1), questState.getPlayer(), true);
                questState.takeItems(5905, -1L);
                questState.takeItems(6319, -1L);
                questState.takeItems(5011, -1L);
                questState.giveItems(729, 1L);
                questState.unset("water_of_mimir");
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
                string2 = "magister_ladd_q0235_16.htm";
            }
        } else if (n2 == 30718) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "magister_joan_q0235_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.setCond(3);
                questState.set("water_of_mimir", String.valueOf(3), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "magister_joan_q0235_03.htm";
            } else if (string.equalsIgnoreCase("reply_3") && n == 4 && questState.getQuestItemsCount(6322) >= 1L) {
                questState.setCond(5);
                questState.set("water_of_mimir", String.valueOf(5), true);
                questState.giveItems(6321, 1L);
                questState.takeItems(6322, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "magister_joan_q0235_06.htm";
            }
        } else if (n2 == 31149) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "alchemical_mixing_jar_q0235_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = questState.getQuestItemsCount(6320) < 1L ? "alchemical_mixing_jar_q0235_03.htm" : "alchemical_mixing_jar_q0235_04.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "alchemical_mixing_jar_q0235_05.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = questState.getQuestItemsCount(6321) >= 1L ? "alchemical_mixing_jar_q0235_06.htm" : "alchemical_mixing_jar_q0235_06a.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "alchemical_mixing_jar_q0235_07.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = questState.getQuestItemsCount(6318) < 1L ? "alchemical_mixing_jar_q0235_08.htm" : "alchemical_mixing_jar_q0235_09.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                string2 = "alchemical_mixing_jar_q0235_10.htm";
            } else if (string.equalsIgnoreCase("reply_8")) {
                string2 = "alchemical_mixing_jar_q0235_11.htm";
            } else if (string.equalsIgnoreCase("reply_9") && questState.getQuestItemsCount(6318) >= 1L && questState.getQuestItemsCount(6320) >= 1L && questState.getQuestItemsCount(6321) >= 1L && n == 7) {
                questState.setCond(8);
                questState.set("water_of_mimir", String.valueOf(8), true);
                questState.giveItems(6319, 1L);
                questState.takeItems(6320, -1L);
                questState.takeItems(6321, -1L);
                questState.takeItems(6318, -1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "alchemical_mixing_jar_q0235_12.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("water_of_mimir");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30721) break;
                if (questState.getPlayer().getLevel() >= 75 && questState.getQuestItemsCount(5011) >= 1L) {
                    string = "magister_ladd_q0235_01.htm";
                    break;
                }
                if (questState.getPlayer().getLevel() >= 75 && questState.getQuestItemsCount(5011) < 1L) {
                    string = "magister_ladd_q0235_01b.htm";
                    break;
                }
                if (questState.getPlayer().getLevel() >= 75) break;
                string = "magister_ladd_q0235_01a.htm";
                break;
            }
            case 2: {
                if (n2 == 30721) {
                    if (n == 1 && questState.getQuestItemsCount(6320) < 1L) {
                        string = "magister_ladd_q0235_07.htm";
                        break;
                    }
                    if (n == 1 && questState.getQuestItemsCount(6320) >= 1L) {
                        string = "magister_ladd_q0235_08.htm";
                        break;
                    }
                    if (n >= 2 && n < 5) {
                        string = "magister_ladd_q0235_10.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "magister_ladd_q0235_11.htm";
                        break;
                    }
                    if (n >= 6 && n < 8) {
                        string = "magister_ladd_q0235_13.htm";
                        break;
                    }
                    if (n != 8) break;
                    string = "magister_ladd_q0235_14.htm";
                    break;
                }
                if (n2 == 30718) {
                    if (n == 2) {
                        string = "magister_joan_q0235_01.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "magister_joan_q0235_04.htm";
                        break;
                    }
                    if (n != 4) break;
                    string = "magister_joan_q0235_05.htm";
                    break;
                }
                if (n2 != 31149 || n != 7 || questState.getQuestItemsCount(5905) < 1L) break;
                string = "alchemical_mixing_jar_q0235_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("water_of_mimir");
        int n2 = npcInstance.getNpcId();
        if (n == 3 && n2 == 20965 && questState.getQuestItemsCount(6322) == 0L) {
            if (Rnd.get(5) == 0) {
                questState.setCond(4);
                questState.set("water_of_mimir", String.valueOf(4), true);
                questState.giveItems(6322, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 6 && n2 == 21090 && questState.getQuestItemsCount(6318) == 0L && Rnd.get(5) == 0) {
            questState.setCond(7);
            questState.set("water_of_mimir", String.valueOf(7), true);
            questState.giveItems(6318, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

