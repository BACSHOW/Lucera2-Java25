/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _215_TrialOfPilgrim
extends Quest
implements ScriptFile {
    private static final int aMy = 2721;
    private static final int aMz = 2722;
    private static final int aMA = 2723;
    private static final int aMB = 2724;
    private static final int aMC = 2725;
    private static final int aMD = 2726;
    private static final int aME = 2727;
    private static final int aMF = 2728;
    private static final int aMG = 2729;
    private static final int aMH = 2730;
    private static final int aMI = 2731;
    private static final int aMJ = 2732;
    private static final int aMK = 2733;
    private static final int aKb = 77382;
    private static final int aKc = 16000;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _215_TrialOfPilgrim() {
        super(0);
        this.addStartNpc(30648);
        this.addTalkId(30648);
        this.addTalkId(30036);
        this.addTalkId(30117);
        this.addTalkId(30362);
        this.addTalkId(30550);
        this.addTalkId(30571);
        this.addTalkId(30612);
        this.addTalkId(30648);
        this.addTalkId(30649);
        this.addTalkId(30650);
        this.addTalkId(30651);
        this.addTalkId(30652);
        this.addKillId(27116);
        this.addKillId(27117);
        this.addKillId(27118);
        this.addQuestItem(2722, 2723, 2725, 2726, 2733, 2728, 2729, 2731, 2732, 2727, 2724, 2730);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            string2 = "hermit_santiago_q0215_04.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(2723, 1L);
        } else if (string.equals("30648_1")) {
            string2 = "hermit_santiago_q0215_05.htm";
        } else if (string.equals("30648_2")) {
            string2 = "hermit_santiago_q0215_06.htm";
        } else if (string.equals("30648_3")) {
            string2 = "hermit_santiago_q0215_07.htm";
        } else if (string.equals("30648_4")) {
            string2 = "hermit_santiago_q0215_08.htm";
        } else if (string.equals("30648_5")) {
            string2 = "hermit_santiago_q0215_05.htm";
        } else if (string.equals("30649_1")) {
            string2 = "ancestor_martankus_q0215_04.htm";
            questState.giveItems(2724, 1L);
            questState.takeItems(2725, 1L);
            questState.setCond(5);
        } else if (string.equals("30650_1")) {
            if (questState.getQuestItemsCount(57) >= 100000L) {
                string2 = "gerald_priest_of_earth_q0215_02.htm";
                questState.giveItems(2726, 1L);
                questState.takeItems(57, 100000L);
                questState.setCond(7);
            } else {
                string2 = "gerald_priest_of_earth_q0215_03.htm";
            }
        } else if (string.equals("30650_2")) {
            string2 = "gerald_priest_of_earth_q0215_03.htm";
        } else if (string.equals("30362_1")) {
            string2 = "andellria_q0215_05.htm";
            questState.takeItems(2731, 1L);
            questState.setCond(16);
        } else if (string.equals("30362_2")) {
            string2 = "andellria_q0215_04.htm";
            questState.setCond(16);
        } else if (string.equals("30652_1")) {
            string2 = "uruha_q0215_02.htm";
            questState.giveItems(2731, 1L);
            questState.takeItems(2732, 1L);
            questState.setCond(15);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(2721) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            questState.setState(2);
            questState.setCond(0);
            questState.set("id", "0");
        }
        if (n == 30648 && questState.getCond() == 0) {
            if (questState.getPlayer().getClassId().getId() == 15 || questState.getPlayer().getClassId().getId() == 29 || questState.getPlayer().getClassId().getId() == 42 || questState.getPlayer().getClassId().getId() == 50) {
                if (questState.getPlayer().getLevel() >= 35) {
                    string = "hermit_santiago_q0215_03.htm";
                } else {
                    string = "hermit_santiago_q0215_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "hermit_santiago_q0215_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30648 && questState.getCond() == 1 && questState.getQuestItemsCount(2723) > 0L) {
            string = "hermit_santiago_q0215_09.htm";
        } else if (n == 30648 && questState.getCond() == 17 && questState.getQuestItemsCount(2722) > 0L) {
            string = "hermit_santiago_q0215_10.htm";
            questState.takeItems(2722, -1L);
            questState.giveItems(2721, 1L);
            if (!questState.getPlayer().getVarB("prof2.1")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(77382L, 16000L);
                questState.getPlayer().setVar("prof2.1", "1", -1L);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.unset("cond");
            questState.exitCurrentQuest(false);
        } else if (n == 30571 && questState.getCond() == 1 && questState.getQuestItemsCount(2723) > 0L) {
            string = "seer_tanapi_q0215_01.htm";
            questState.takeItems(2723, 1L);
            questState.setCond(2);
        } else if (n == 30571 && questState.getCond() == 2) {
            string = "seer_tanapi_q0215_02.htm";
        } else if (n == 30571 && questState.getCond() == 5 && questState.getQuestItemsCount(2724) > 0L) {
            string = "seer_tanapi_q0215_03.htm";
        } else if (n == 30649 && questState.getCond() == 2) {
            string = "ancestor_martankus_q0215_01.htm";
            questState.setCond(3);
        } else if (n == 30649 && questState.getCond() == 3) {
            string = "ancestor_martankus_q0215_02.htm";
        } else if (n == 30649 && questState.getCond() == 4 && questState.getQuestItemsCount(2725) > 0L) {
            string = "ancestor_martankus_q0215_03.htm";
        } else if (n == 30550 && questState.getCond() == 5 && questState.getQuestItemsCount(2724) > 0L) {
            string = "gauri_twinklerock_q0215_01.htm";
            questState.giveItems(2733, 1L);
            questState.setCond(6);
        } else if (n == 30550 && questState.getCond() == 6) {
            string = "gauri_twinklerock_q0215_02.htm";
        } else if (n == 30650 && questState.getCond() == 6 && questState.getQuestItemsCount(2733) > 0L) {
            string = "gerald_priest_of_earth_q0215_01.htm";
        } else if (n == 30650 && questState.getCond() >= 8 && questState.getQuestItemsCount(2727) > 0L && questState.getQuestItemsCount(2726) > 0L) {
            string = "gerald_priest_of_earth_q0215_04.htm";
            questState.giveItems(57, 100000L, Config.RATE_QUESTS_ADENA_REWARD < 1.0);
            questState.takeItems(2726, 1L);
        } else if (n == 30651 && questState.getCond() == 6 && questState.getQuestItemsCount(2733) > 0L) {
            string = "wanderer_dorf_q0215_01.htm";
            questState.giveItems(2727, 1L);
            questState.takeItems(2733, 1L);
            questState.setCond(8);
        } else if (n == 30651 && questState.getCond() == 7 && questState.getQuestItemsCount(2733) > 0L) {
            string = "wanderer_dorf_q0215_02.htm";
            questState.giveItems(2727, 1L);
            questState.takeItems(2733, 1L);
            questState.setCond(8);
        } else if (n == 30651 && questState.getCond() == 8) {
            string = "wanderer_dorf_q0215_03.htm";
        } else if (n == 30117 && questState.getCond() == 8) {
            string = "primoz_q0215_01.htm";
            questState.setCond(9);
        } else if (n == 30117 && questState.getCond() == 9) {
            string = "primoz_q0215_02.htm";
        } else if (n == 30036 && questState.getCond() == 9) {
            string = "potter_q0215_01.htm";
            questState.giveItems(2728, 1L);
            questState.setCond(10);
        } else if (n == 30036 && questState.getCond() == 10) {
            string = "potter_q0215_02.htm";
        } else if (n == 30036 && questState.getCond() == 11) {
            string = "potter_q0215_03.htm";
            questState.giveItems(2730, 1L);
            questState.takeItems(2728, 1L);
            questState.takeItems(2729, 1L);
            questState.setCond(12);
        } else if (n == 30036 && questState.getCond() == 12 && questState.getQuestItemsCount(2730) > 0L) {
            string = "potter_q0215_04.htm";
        } else if (n == 30362 && questState.getCond() == 12) {
            string = "andellria_q0215_01.htm";
            questState.setCond(13);
        } else if (n == 30362 && questState.getCond() == 13) {
            string = "andellria_q0215_02.htm";
        } else if (n == 30362 && questState.getCond() == 15 && questState.getQuestItemsCount(2731) > 0L) {
            string = "andellria_q0215_03.htm";
        } else if (n == 30362 && questState.getCond() == 16) {
            string = "andellria_q0215_06.htm";
        } else if (n == 30362 && questState.getCond() == 15 && questState.getQuestItemsCount(2731) == 0L) {
            string = "andellria_q0215_07.htm";
        } else if (n == 30652 && questState.getCond() == 14 && questState.getQuestItemsCount(2732) > 0L) {
            string = "uruha_q0215_01.htm";
        } else if (n == 30652 && questState.getCond() == 15 && questState.getQuestItemsCount(2731) > 0L) {
            string = "uruha_q0215_03.htm";
        } else if (n == 30612 && questState.getCond() == 16) {
            string = "sage_kasian_q0215_01.htm";
            questState.giveItems(2722, 1L);
            if (questState.getQuestItemsCount(2731) > 0L) {
                questState.takeItems(2731, 1L);
            }
            if (questState.getQuestItemsCount(2726) > 0L) {
                questState.takeItems(2726, 1L);
            }
            questState.setCond(17);
            questState.takeItems(2727, 1L);
            questState.takeItems(2724, 1L);
            questState.takeItems(2730, 1L);
        } else if (n == 30612 && questState.getCond() == 17) {
            string = "sage_kasian_q0215_02.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 27116) {
            if (questState.getCond() == 3 && questState.getQuestItemsCount(2725) == 0L && Rnd.chance(30)) {
                questState.giveItems(2725, 1L);
                questState.setCond(4);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (n == 27117) {
            if (questState.getCond() == 10 && questState.getQuestItemsCount(2729) == 0L) {
                questState.giveItems(2729, 1L);
                questState.setCond(11);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (n == 27118 && questState.getCond() == 13 && questState.getQuestItemsCount(2732) == 0L && Rnd.chance(20)) {
            questState.giveItems(2732, 1L);
            questState.setCond(14);
            questState.playSound("ItemSound.quest_middle");
        }
        return null;
    }
}

