/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.Config;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExPlayScene;
import l2.gameserver.scripts.ScriptFile;

public class _127_KamaelAWindowToTheFuture
extends Quest
implements ScriptFile {
    private final int aGJ = 31288;
    private final int aGK = 32092;
    private final int aAa = 31350;
    private final int aGL = 30113;
    private final int aGM = 30187;
    private final int aGN = 30862;
    private final int aGO = 30756;
    private final int aGP = 8939;
    private final int aGQ = 8940;
    private final int aGR = 8941;
    private final int aGS = 8942;
    private final int aGT = 8943;
    private final int aGU = 8944;

    public _127_KamaelAWindowToTheFuture() {
        super(0);
        this.addStartNpc(31350);
        this.addTalkId(31288, 32092, 30113, 30187, 30862, 30756);
        this.addQuestItem(8939, 8940, 8941, 8942, 8943, 8944);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
            string2 = "falsepriest_dominic_q0127_05.htm";
            questState.giveItems(8939, 1L);
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=1") && questState.getQuestItemsCount(8939) < 1L) {
            string2 = "kai_q0127_04.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=1") && questState.getQuestItemsCount(8939) >= 1L) {
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
            string2 = "kai_q0127_02.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=6")) {
            questState.setCond(3);
            questState.giveItems(8940, 1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "kai_q0127_11.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=7") && questState.getQuestItemsCount(8940) < 1L) {
            string2 = "warehouse_chief_older_q0127_02.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=7") && questState.getQuestItemsCount(8940) >= 1L) {
            questState.setCond(4);
            questState.playSound("ItemSound.quest_middle");
            string2 = "warehouse_chief_older_q0127_03.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=10")) {
            questState.setCond(5);
            questState.giveItems(8941, 1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "warehouse_chief_older_q0127_07.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=11") && questState.getQuestItemsCount(8941) < 1L) {
            string2 = "high_prefect_aklan_q0127_02.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=11") && questState.getQuestItemsCount(8941) >= 1L) {
            string2 = "high_prefect_aklan_q0127_03.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=13")) {
            questState.giveItems(8944, 1L);
            questState.setCond(6);
            questState.playSound("ItemSound.quest_middle");
            string2 = "high_prefect_aklan_q0127_06.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=14") && questState.getQuestItemsCount(8944) < 1L) {
            string2 = "grandmaster_oltlin_q0127_02.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=14") && questState.getQuestItemsCount(8944) >= 1L) {
            string2 = "grandmaster_oltlin_q0127_03.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=16")) {
            questState.giveItems(8943, 1L);
            questState.setCond(7);
            questState.playSound("ItemSound.quest_middle");
            string2 = "grandmaster_oltlin_q0127_06.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=17") && questState.getQuestItemsCount(8943) < 0L) {
            string2 = "juria_q0127_02.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=17") && questState.getQuestItemsCount(8943) >= 1L) {
            string2 = "juria_q0127_03.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=19")) {
            questState.giveItems(8942, 1L);
            questState.setCond(8);
            questState.playSound("ItemSound.quest_middle");
            string2 = "juria_q0127_06.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=20") && (questState.getQuestItemsCount(8940) < 1L || questState.getQuestItemsCount(8941) < 1L || questState.getQuestItemsCount(8942) < 1L || questState.getQuestItemsCount(8943) < 1L || questState.getQuestItemsCount(8944) < 1L || questState.getQuestItemsCount(8939) < 1L)) {
            string2 = "sir_kristof_rodemai_q0127_02.htm";
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=20") && questState.getQuestItemsCount(8940) >= 1L && questState.getQuestItemsCount(8941) >= 1L && questState.getQuestItemsCount(8942) >= 1L && questState.getQuestItemsCount(8943) >= 1L && questState.getQuestItemsCount(8944) >= 1L && questState.getQuestItemsCount(8939) >= 1L) {
            string2 = "sir_kristof_rodemai_q0127_03.htm";
        } else if (string.equalsIgnoreCase("kamaelstory")) {
            string2 = "sir_kristof_rodemai_q0127_07.htm";
            questState.getPlayer().sendPacket((IStaticPacket)ExPlayScene.STATIC);
        } else if (string.equalsIgnoreCase("sir_kristof_rodemai_q0127_08")) {
            string2 = "sir_kristof_rodemai_q0127_08.htm";
            questState.setCond(9);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("menu_select?ask=127&reply=23") && questState.getQuestItemsCount(8940) >= 1L && questState.getQuestItemsCount(8941) >= 1L && questState.getQuestItemsCount(8942) >= 1L && questState.getQuestItemsCount(8943) >= 1L && questState.getQuestItemsCount(8944) >= 1L && questState.getQuestItemsCount(8939) >= 1L) {
            questState.takeItems(8940, -1L);
            questState.takeItems(8941, -1L);
            questState.takeItems(8942, -1L);
            questState.takeItems(8943, -1L);
            questState.takeItems(8944, -1L);
            questState.takeItems(8939, -1L);
            questState.giveItems(57, 159100L, Config.RATE_QUESTS_ADENA_REWARD < 1.0);
            string2 = "falsepriest_dominic_q0127_09.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 31350) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 1) {
                    string = "falsepriest_dominic_q0127_01.htm";
                } else {
                    string = "falsepriest_dominic_q0127_02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "falsepriest_dominic_q0127_06.htm";
            } else if (n2 > 1 && n2 < 9) {
                string = "falsepriest_dominic_q0127_08.htm";
            } else if (n2 == 9) {
                string = "falsepriest_dominic_q0127_07.htm";
            }
        } else if (n == 30187) {
            if (n2 == 1) {
                string = "kai_q0127_01.htm";
            } else if (n2 == 2) {
                string = "kai_q0127_03.htm";
            } else if (n2 == 3) {
                string = "kai_q0127_11.htm";
            } else if (n2 > 3) {
                string = "kai_q0127_13.htm";
            }
        } else if (n == 32092) {
            if (n2 == 3) {
                string = "warehouse_chief_older_q0127_01.htm";
            } else if (n2 == 4) {
                string = "warehouse_chief_older_q0127_03.htm";
            } else if (n2 == 5) {
                string = "warehouse_chief_older_q0127_08.htm";
            } else if (n2 > 5) {
                string = "warehouse_chief_older_q0127_09.htm";
            }
        } else if (n == 31288) {
            if (n2 == 5) {
                string = "high_prefect_aklan_q0127_01.htm";
            } else if (n2 == 6) {
                string = "high_prefect_aklan_q0127_07.htm";
            } else if (n2 > 6) {
                string = "high_prefect_aklan_q0127_08.htm";
            }
        } else if (n == 30862) {
            if (n2 == 6) {
                string = "grandmaster_oltlin_q0127_01.htm";
            } else if (n2 == 7) {
                string = "grandmaster_oltlin_q0127_07.htm";
            } else if (n2 > 7) {
                string = "grandmaster_oltlin_q0127_08.htm";
            }
        } else if (n == 30113) {
            if (n2 == 7) {
                string = "juria_q0127_01.htm";
            } else if (n2 == 8) {
                string = "juria_q0127_07.htm";
            } else if (n2 > 8) {
                string = "juria_q0127_08.htm";
            }
        } else if (n == 30756) {
            if (n2 == 8) {
                string = "sir_kristof_rodemai_q0127_01.htm";
            } else if (n2 == 9) {
                string = "sir_kristof_rodemai_q0127_09.htm";
            }
        }
        return string;
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

