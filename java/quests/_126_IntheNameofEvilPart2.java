/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import quests._125_InTheNameOfEvilPart1;

public class _126_IntheNameofEvilPart2
extends Quest
implements ScriptFile {
    private static final int aGj = 32114;
    private static final int aGk = 32115;
    private static final int aGo = 32119;
    private static final int aGp = 32120;
    private static final int aGq = 32121;
    private static final int aGF = 32122;
    private static final int aGG = 32109;
    private static final int aGH = 8783;
    private static final int aGD = 8781;
    private static final int aGE = 8782;
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

    public _126_IntheNameofEvilPart2() {
        super(0);
        this.addStartNpc(32115);
        this.addTalkId(32114, 32119, 32120, 32121, 32122, 32109);
        this.addQuestItem(8783, 8781);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("name_of_cruel_god_two");
        int n2 = questState.getInt("name_of_cruel_god_two_ex");
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "asama_q0126_07.htm";
        } else if (string.equalsIgnoreCase("reply_4")) {
            questState.setCond(2);
            questState.set("name_of_cruel_god_two", String.valueOf(1), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "asama_q0126_10.htm";
        } else if (string.equalsIgnoreCase("reply_62") && n == 406) {
            questState.setCond(21);
            questState.set("name_of_cruel_god_two", String.valueOf(407), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "asama_q0126_16.htm";
        } else if (string.equalsIgnoreCase("reply_71") && n == 407) {
            questState.set("name_of_cruel_god_two", String.valueOf(408), true);
            string2 = "asama_q0126_26.htm";
        } else if (string.equalsIgnoreCase("reply_72") && n == 408) {
            questState.setCond(22);
            questState.set("name_of_cruel_god_two", String.valueOf(409), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "asama_q0126_28.htm";
        } else if (string.equalsIgnoreCase("reply_1") && (n == 409 || n == 410)) {
            string2 = "mushika_q0126_03.htm";
            if (n == 409) {
                questState.setCond(23);
                questState.set("name_of_cruel_god_two", String.valueOf(410), true);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (string.equalsIgnoreCase("reply_2") && n == 410) {
            string2 = "mushika_q0126_08.htm";
        } else if (string.equalsIgnoreCase("reply_3") && n == 410) {
            questState.giveItems(57, 298496L);
            questState.giveItems(729, 1L);
            string2 = "mushika_q0126_09.htm";
            questState.unset("name_of_cruel_god_two");
            questState.unset("name_of_cruel_god_two_ex");
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        } else if (string.equalsIgnoreCase("reply_5") && n == 2) {
            questState.setCond(3);
            questState.set("name_of_cruel_god_two", String.valueOf(3), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "ulu_kaimu_stone_q0126_03.htm";
        } else if (string.equalsIgnoreCase("reply_6") && n == 3) {
            string2 = "ulu_kaimu_stone_q0126_05.htm";
        } else if (string.equalsIgnoreCase("reply_7")) {
            string2 = "ulu_kaimu_stone_q0126_06.htm";
        } else if (string.equalsIgnoreCase("reply_8") && n == 3) {
            questState.setCond(4);
            questState.set("name_of_cruel_god_two", String.valueOf(4), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "ulu_kaimu_stone_q0126_07.htm";
        } else if (string.equalsIgnoreCase("reply_9")) {
            string2 = "ulu_kaimu_stone_q0126_09.htm";
            questState.playSound("EtcSound.elcroki_song_1st");
        } else if (string.equalsIgnoreCase("reply_10")) {
            string2 = "ulu_kaimu_stone_q0126_10.htm";
        } else if (string.equalsIgnoreCase("reply_11") && n == 4) {
            questState.setCond(5);
            questState.set("name_of_cruel_god_two", String.valueOf(5), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "ulu_kaimu_stone_q0126_11.htm";
        } else if (string.equalsIgnoreCase("reply_12")) {
            string2 = "ulu_kaimu_stone_q0126_13.htm";
            questState.playSound("EtcSound.elcroki_song_1st");
        } else if (string.equalsIgnoreCase("reply_12a") && n == 5) {
            string2 = "balu_kaimu_stone_q0126_03.htm";
        } else if (string.equalsIgnoreCase("reply_13") && n == 5) {
            questState.setCond(6);
            questState.set("name_of_cruel_god_two", String.valueOf(7), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "balu_kaimu_stone_q0126_04.htm";
        } else if (string.equalsIgnoreCase("reply_400") && n == 7) {
            string2 = "balu_kaimu_stone_q0126_06.htm";
        } else if (string.equalsIgnoreCase("reply_14") && n == 7) {
            questState.setCond(7);
            questState.set("name_of_cruel_god_two", String.valueOf(8), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "balu_kaimu_stone_q0126_07.htm";
        } else if (string.equalsIgnoreCase("reply_15")) {
            string2 = "balu_kaimu_stone_q0126_09.htm";
            questState.playSound("EtcSound.elcroki_song_2nd");
        } else if (string.equalsIgnoreCase("reply_16")) {
            string2 = "balu_kaimu_stone_q0126_10.htm";
        } else if (string.equalsIgnoreCase("reply_17") && n == 8) {
            questState.setCond(8);
            questState.set("name_of_cruel_god_two", String.valueOf(9), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "balu_kaimu_stone_q0126_11.htm";
        } else if (string.equalsIgnoreCase("reply_18")) {
            string2 = "balu_kaimu_stone_q0126_13.htm";
            questState.playSound("EtcSound.elcroki_song_2nd");
        } else if (string.equalsIgnoreCase("reply_18a") && n == 9) {
            questState.setCond(9);
            questState.set("name_of_cruel_god_two", String.valueOf(11), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "jiuta_kaimu_stone_q0126_03.htm";
        } else if (string.equalsIgnoreCase("reply_19") && n == 11) {
            string2 = "jiuta_kaimu_stone_q0126_05.htm";
        } else if (string.equalsIgnoreCase("reply_20")) {
            string2 = "jiuta_kaimu_stone_q0126_06.htm";
        } else if (string.equalsIgnoreCase("reply_21")) {
            string2 = "jiuta_kaimu_stone_q0126_07.htm";
        } else if (string.equalsIgnoreCase("reply_22")) {
            string2 = "jiuta_kaimu_stone_q0126_08.htm";
        } else if (string.equalsIgnoreCase("reply_23") && n == 11) {
            questState.setCond(10);
            questState.set("name_of_cruel_god_two", String.valueOf(12), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "jiuta_kaimu_stone_q0126_09.htm";
        } else if (string.equalsIgnoreCase("reply_24")) {
            string2 = "jiuta_kaimu_stone_q0126_11.htm";
            questState.playSound("EtcSound.elcroki_song_3rd");
        } else if (string.equalsIgnoreCase("reply_25")) {
            string2 = "jiuta_kaimu_stone_q0126_12.htm";
        } else if (string.equalsIgnoreCase("reply_26")) {
            string2 = "jiuta_kaimu_stone_q0126_13.htm";
        } else if (string.equalsIgnoreCase("reply_27")) {
            string2 = "jiuta_kaimu_stone_q0126_14.htm";
        } else if (string.equalsIgnoreCase("reply_28") && n == 12) {
            questState.giveItems(8782, 1L);
            questState.setCond(11);
            questState.set("name_of_cruel_god_two", String.valueOf(13), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "jiuta_kaimu_stone_q0126_15.htm";
        } else if (string.equalsIgnoreCase("reply_29")) {
            string2 = "jiuta_kaimu_stone_q0126_17.htm";
            questState.playSound("EtcSound.elcroki_song_3rd");
        } else if (string.equalsIgnoreCase("reply_30") && n == 13) {
            string2 = "grave_of_brave_man_q0126_03.htm";
            npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
        } else if (string.equalsIgnoreCase("reply_31")) {
            string2 = "grave_of_brave_man_q0126_04.htm";
        } else if (string.equalsIgnoreCase("reply_32")) {
            string2 = "grave_of_brave_man_q0126_05.htm";
        } else if (string.equalsIgnoreCase("reply_33") && n == 13) {
            questState.takeItems(8782, -1L);
            questState.set("name_of_cruel_god_two", String.valueOf(14), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(0), true);
            string2 = "grave_of_brave_man_q0126_06.htm";
        } else if (string.equalsIgnoreCase("reply_300") && n == 14) {
            string2 = "grave_of_brave_man_q0126_08.htm";
        } else if (string.equalsIgnoreCase("reply_34")) {
            string2 = "grave_of_brave_man_q0126_09.htm";
        } else if (string.equalsIgnoreCase("reply_35")) {
            string2 = "grave_of_brave_man_q0126_10.htm";
        } else if (string.equalsIgnoreCase("reply_36")) {
            string2 = "grave_of_brave_man_q0126_11.htm";
        } else if (string.equalsIgnoreCase("reply_37")) {
            string2 = "grave_of_brave_man_q0126_12.htm";
        } else if (string.equalsIgnoreCase("reply_38")) {
            string2 = "grave_of_brave_man_q0126_13.htm";
        } else if (string.equalsIgnoreCase("reply_39") && n == 14) {
            string2 = "grave_of_brave_man_q0126_14.htm";
        } else if (string.equalsIgnoreCase("reply_40") && n == 14) {
            questState.setCond(13);
            questState.set("name_of_cruel_god_two", String.valueOf(15), true);
            string2 = "grave_of_brave_man_q0126_15.htm";
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("reply_41") && n == 15) {
            string2 = "grave_of_brave_man_q0126_17.htm";
        } else if (string.equalsIgnoreCase("reply_42") && n == 15) {
            questState.setCond(14);
            questState.set("name_of_cruel_god_two", String.valueOf(16), true);
            string2 = "grave_of_brave_man_q0126_18.htm";
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("reply_43") && n == 16) {
            questState.set("name_of_cruel_god_two", String.valueOf(100), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(7473), true);
            string2 = "grave_of_brave_man_q0126_21.htm";
        } else if (string.equalsIgnoreCase("reply_80") && n >= 100 && n < 200) {
            questState.set("name_of_cruel_god_two", String.valueOf(100), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(7473), true);
            string2 = "grave_of_brave_man_q0126_22.htm";
        } else if (string.equalsIgnoreCase("reply_44") && n == 100) {
            string2 = "grave_of_brave_man_q0126_24.htm";
        } else if (string.equalsIgnoreCase("reply_100") && n == 100) {
            questState.set("name_of_cruel_god_two", String.valueOf(101), true);
            string2 = "grave_of_brave_man_q0126_26.htm";
        } else if (string.equalsIgnoreCase("reply_101") && n == 100) {
            questState.set("name_of_cruel_god_two", String.valueOf(101), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_27.htm";
        } else if (string.equalsIgnoreCase("reply_45") && n == 101) {
            string2 = "grave_of_brave_man_q0126_28.htm";
        } else if (string.equalsIgnoreCase("reply_102") && n == 101) {
            questState.set("name_of_cruel_god_two", String.valueOf(102), true);
            string2 = "grave_of_brave_man_q0126_30.htm";
        } else if (string.equalsIgnoreCase("reply_103") && n == 101) {
            questState.set("name_of_cruel_god_two", String.valueOf(102), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_31.htm";
        } else if (string.equalsIgnoreCase("reply_46") && n == 102) {
            string2 = "grave_of_brave_man_q0126_32.htm";
        } else if (string.equalsIgnoreCase("reply_104") && n == 102) {
            questState.set("name_of_cruel_god_two", String.valueOf(103), true);
            string2 = "grave_of_brave_man_q0126_34.htm";
        } else if (string.equalsIgnoreCase("reply_105") && n == 102) {
            questState.set("name_of_cruel_god_two", String.valueOf(103), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_35.htm";
        } else if (string.equalsIgnoreCase("reply_47") && n == 103) {
            string2 = "grave_of_brave_man_q0126_36.htm";
        } else if (string.equalsIgnoreCase("reply_106") && n == 103) {
            questState.set("name_of_cruel_god_two", String.valueOf(104), true);
            string2 = "grave_of_brave_man_q0126_38.htm";
        } else if (string.equalsIgnoreCase("reply_107") && n == 103) {
            questState.set("name_of_cruel_god_two", String.valueOf(104), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_39.htm";
        } else if (string.equalsIgnoreCase("reply_48") && n == 104) {
            string2 = "grave_of_brave_man_q0126_40.htm";
        } else if (string.equalsIgnoreCase("reply_108") && n == 104) {
            if (n2 == 7473) {
                questState.setCond(15);
                questState.set("name_of_cruel_god_two", String.valueOf(200), true);
                questState.set("name_of_cruel_god_two_ex", String.valueOf(8302), true);
                string2 = "grave_of_brave_man_q0126_42.htm";
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "grave_of_brave_man_q0126_43.htm";
            }
        } else if (string.equalsIgnoreCase("reply_109") && n == 104) {
            string2 = "grave_of_brave_man_q0126_44.htm";
        } else if (string.equalsIgnoreCase("reply_49") && n == 200) {
            string2 = "grave_of_brave_man_q0126_45.htm";
        } else if (string.equalsIgnoreCase("reply_110") && n == 200) {
            questState.set("name_of_cruel_god_two", String.valueOf(201), true);
            string2 = "grave_of_brave_man_q0126_47.htm";
        } else if (string.equalsIgnoreCase("reply_111") && n == 200) {
            questState.set("name_of_cruel_god_two", String.valueOf(201), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_48.htm";
        } else if (string.equalsIgnoreCase("reply_50") && n == 201) {
            string2 = "grave_of_brave_man_q0126_49.htm";
        } else if (string.equalsIgnoreCase("reply_112") && n == 201) {
            questState.set("name_of_cruel_god_two", String.valueOf(202), true);
            string2 = "grave_of_brave_man_q0126_51.htm";
        } else if (string.equalsIgnoreCase("reply_113") && n == 201) {
            questState.set("name_of_cruel_god_two", String.valueOf(202), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_52.htm";
        } else if (string.equalsIgnoreCase("reply_51") && n == 202) {
            string2 = "grave_of_brave_man_q0126_53.htm";
        } else if (string.equalsIgnoreCase("reply_114") && n == 202) {
            questState.set("name_of_cruel_god_two", String.valueOf(203), true);
            string2 = "grave_of_brave_man_q0126_55.htm";
        } else if (string.equalsIgnoreCase("reply_115") && n == 202) {
            questState.set("name_of_cruel_god_two", String.valueOf(203), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_56.htm";
        } else if (string.equalsIgnoreCase("reply_52") && n == 203) {
            string2 = "grave_of_brave_man_q0126_57.htm";
        } else if (string.equalsIgnoreCase("reply_116") && n == 203) {
            questState.set("name_of_cruel_god_two", String.valueOf(204), true);
            string2 = "grave_of_brave_man_q0126_59.htm";
        } else if (string.equalsIgnoreCase("reply_117") && n == 203) {
            questState.set("name_of_cruel_god_two", String.valueOf(204), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_60.htm";
        } else if (string.equalsIgnoreCase("reply_53") && n == 204) {
            string2 = "grave_of_brave_man_q0126_61.htm";
        } else if (string.equalsIgnoreCase("reply_118") && n == 204) {
            if (n2 == 8302) {
                questState.setCond(16);
                questState.set("name_of_cruel_god_two", String.valueOf(300), true);
                questState.set("name_of_cruel_god_two_ex", String.valueOf(1134), true);
                string2 = "grave_of_brave_man_q0126_63.htm";
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.set("name_of_cruel_god_two", String.valueOf(200), true);
                questState.set("name_of_cruel_god_two_ex", String.valueOf(8302), true);
                string2 = "grave_of_brave_man_q0126_64.htm";
            }
        } else if (string.equalsIgnoreCase("reply_119") && n == 204) {
            questState.set("name_of_cruel_god_two", String.valueOf(200), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(8302), true);
            string2 = "grave_of_brave_man_q0126_65.htm";
        } else if (string.equalsIgnoreCase("reply_54") && n == 300) {
            string2 = "grave_of_brave_man_q0126_66.htm";
        } else if (string.equalsIgnoreCase("reply_120") && n == 300) {
            questState.set("name_of_cruel_god_two", String.valueOf(301), true);
            string2 = "grave_of_brave_man_q0126_68.htm";
        } else if (string.equalsIgnoreCase("reply_121") && n == 300) {
            questState.set("name_of_cruel_god_two", String.valueOf(301), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_69.htm";
        } else if (string.equalsIgnoreCase("reply_55") && n == 301) {
            string2 = "grave_of_brave_man_q0126_70.htm";
        } else if (string.equalsIgnoreCase("reply_122") && n == 301) {
            questState.set("name_of_cruel_god_two", String.valueOf(302), true);
            string2 = "grave_of_brave_man_q0126_72.htm";
        } else if (string.equalsIgnoreCase("reply_123") && n == 301) {
            questState.set("name_of_cruel_god_two", String.valueOf(302), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_73.htm";
        } else if (string.equalsIgnoreCase("reply_56") && n == 302) {
            string2 = "grave_of_brave_man_q0126_74.htm";
        } else if (string.equalsIgnoreCase("reply_124") && n == 302) {
            questState.set("name_of_cruel_god_two", String.valueOf(303), true);
            string2 = "grave_of_brave_man_q0126_76.htm";
        } else if (string.equalsIgnoreCase("reply_125") && n == 302) {
            questState.set("name_of_cruel_god_two", String.valueOf(303), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_77.htm";
        } else if (string.equalsIgnoreCase("reply_57") && n == 303) {
            string2 = "grave_of_brave_man_q0126_78.htm";
        } else if (string.equalsIgnoreCase("reply_126") && n == 303) {
            questState.set("name_of_cruel_god_two", String.valueOf(304), true);
            string2 = "grave_of_brave_man_q0126_80.htm";
        } else if (string.equalsIgnoreCase("reply_127") && n == 303) {
            questState.set("name_of_cruel_god_two", String.valueOf(304), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(97756), true);
            string2 = "grave_of_brave_man_q0126_81.htm";
        } else if (string.equalsIgnoreCase("reply_58") && n == 304) {
            string2 = "grave_of_brave_man_q0126_82.htm";
        } else if (string.equalsIgnoreCase("reply_128") && n == 304) {
            if (n2 == 1134) {
                questState.setCond(17);
                questState.set("name_of_cruel_god_two", String.valueOf(400), true);
                questState.set("name_of_cruel_god_two_ex", String.valueOf(0), true);
                string2 = "grave_of_brave_man_q0126_84.htm";
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.set("name_of_cruel_god_two", String.valueOf(300), true);
                questState.set("name_of_cruel_god_two_ex", String.valueOf(1134), true);
                string2 = "grave_of_brave_man_q0126_85.htm";
            }
        } else if (string.equalsIgnoreCase("reply_129") && n == 304) {
            questState.set("name_of_cruel_god_two", String.valueOf(300), true);
            questState.set("name_of_cruel_god_two_ex", String.valueOf(1134), true);
            string2 = "grave_of_brave_man_q0126_86.htm";
        } else if (string.equalsIgnoreCase("reply_130") && n == 400 && questState.getQuestItemsCount(8783) == 0L) {
            questState.giveItems(8783, 1L);
            questState.playSound("EtcSound.elcroki_song_full");
            string2 = "grave_of_brave_man_q0126_87.htm";
            npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
        } else if (string.equalsIgnoreCase("reply_131") && n == 400 && questState.getQuestItemsCount(8783) >= 1L) {
            questState.set("name_of_cruel_god_two", String.valueOf(401), true);
            string2 = "grave_of_brave_man_q0126_88.htm";
        } else if (string.equalsIgnoreCase("reply_132") && n == 401) {
            questState.setCond(18);
            questState.set("name_of_cruel_god_two", String.valueOf(402), true);
            string2 = "grave_of_brave_man_q0126_90.htm";
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("reply_1a") && n == 402 && questState.getQuestItemsCount(8783) >= 1L) {
            questState.setCond(19);
            questState.set("name_of_cruel_god_two", String.valueOf(404), true);
            string2 = "statue_of_shilen_q0126_05.htm";
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("reply_2a") && n == 404) {
            string2 = "statue_of_shilen_q0126_07.htm";
        } else if (string.equalsIgnoreCase("reply_3a")) {
            string2 = "statue_of_shilen_q0126_08.htm";
        } else if (string.equalsIgnoreCase("reply_4a")) {
            string2 = "statue_of_shilen_q0126_10.htm";
        } else if (string.equalsIgnoreCase("reply_5a")) {
            string2 = "statue_of_shilen_q0126_11.htm";
        } else if (string.equalsIgnoreCase("reply_6a")) {
            string2 = "statue_of_shilen_q0126_12.htm";
        } else if (string.equalsIgnoreCase("reply_7a") && n == 404) {
            questState.set("name_of_cruel_god_two", String.valueOf(405), true);
            string2 = "statue_of_shilen_q0126_13.htm";
        } else if (string.equalsIgnoreCase("reply_8a") && n == 405) {
            string2 = "statue_of_shilen_q0126_15.htm";
        } else if (string.equalsIgnoreCase("reply_9a")) {
            string2 = "statue_of_shilen_q0126_16.htm";
        } else if (string.equalsIgnoreCase("reply_10a")) {
            string2 = "statue_of_shilen_q0126_17.htm";
        } else if (string.equalsIgnoreCase("reply_11a")) {
            string2 = "statue_of_shilen_q0126_18.htm";
        } else if (string.equalsIgnoreCase("reply_12b") && n == 405) {
            questState.setCond(20);
            questState.set("name_of_cruel_god_two", String.valueOf(406), true);
            questState.takeItems(8783, -1L);
            string2 = "statue_of_shilen_q0126_19.htm";
            questState.playSound("ItemSound.quest_middle");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_125_InTheNameOfEvilPart1.class);
        int n = questState.getInt("name_of_cruel_god_two");
        int n2 = questState.getInt("name_of_cruel_god_two_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 32115) break;
                if (questState2 != null && questState2.isCompleted()) {
                    if (questState.getPlayer().getLevel() >= 77) {
                        string = "asama_q0126_01.htm";
                        break;
                    }
                    string = "asama_q0126_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "asama_q0126_04.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n3 == 32115) {
                    if (n < 1) {
                        string = "asama_q0126_08.htm";
                        break;
                    }
                    if (n == 1) {
                        string = "asama_q0126_11.htm";
                        break;
                    }
                    if (n >= 2 && n < 406) {
                        string = "asama_q0126_12.htm";
                        break;
                    }
                    if (n == 406) {
                        string = "asama_q0126_13.htm";
                        break;
                    }
                    if (n > 409) {
                        string = "asama_q0126_14.htm";
                        break;
                    }
                    if (n == 407) {
                        string = "asama_q0126_17.htm";
                        break;
                    }
                    if (n == 408) {
                        string = "asama_q0126_27.htm";
                        break;
                    }
                    if (n != 409) break;
                    string = "asama_q0126_29.htm";
                    break;
                }
                if (n3 == 32114) {
                    if (n == 409) {
                        string = "mushika_q0126_01.htm";
                        break;
                    }
                    if (n < 409) {
                        string = "mushika_q0126_02.htm";
                        break;
                    }
                    if (n != 410) break;
                    string = "mushika_q0126_03a.htm";
                    break;
                }
                if (n3 == 32119) {
                    if (n == 1) {
                        npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                        questState.set("name_of_cruel_god_two", String.valueOf(2), true);
                        string = "ulu_kaimu_stone_q0126_01.htm";
                        break;
                    }
                    if (n < 1) {
                        string = "ulu_kaimu_stone_q0126_01a.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "ulu_kaimu_stone_q0126_02.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "ulu_kaimu_stone_q0126_04.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "ulu_kaimu_stone_q0126_08.htm";
                        break;
                    }
                    if (n < 5) break;
                    string = "ulu_kaimu_stone_q0126_12.htm";
                    break;
                }
                if (n3 == 32120) {
                    if (n == 5) {
                        npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                        string = "balu_kaimu_stone_q0126_01.htm";
                        break;
                    }
                    if (n < 5) {
                        string = "balu_kaimu_stone_q0126_02.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "balu_kaimu_stone_q0126_05.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "balu_kaimu_stone_q0126_08.htm";
                        break;
                    }
                    if (n < 9) break;
                    string = "balu_kaimu_stone_q0126_12.htm";
                    break;
                }
                if (n3 == 32121) {
                    if (n == 9) {
                        npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                        string = "jiuta_kaimu_stone_q0126_01.htm";
                        break;
                    }
                    if (n < 9) {
                        string = "jiuta_kaimu_stone_q0126_02.htm";
                        break;
                    }
                    if (n == 11) {
                        string = "jiuta_kaimu_stone_q0126_04.htm";
                        break;
                    }
                    if (n == 12) {
                        string = "jiuta_kaimu_stone_q0126_10.htm";
                        break;
                    }
                    if (n < 13) break;
                    string = "jiuta_kaimu_stone_q0126_16.htm";
                    break;
                }
                if (n3 == 32122) {
                    if (n == 13) {
                        string = "grave_of_brave_man_q0126_01.htm";
                        if (n2 == 1818) break;
                        questState.setCond(12);
                        questState.set("name_of_cruel_god_two_ex", String.valueOf(1818), true);
                        questState.playSound("ItemSound.quest_middle");
                        break;
                    }
                    if (n < 13) {
                        string = "grave_of_brave_man_q0126_02.htm";
                        break;
                    }
                    if (n == 14) {
                        string = "grave_of_brave_man_q0126_07.htm";
                        break;
                    }
                    if (n == 15) {
                        string = "grave_of_brave_man_q0126_16.htm";
                        break;
                    }
                    if (n == 16) {
                        string = "grave_of_brave_man_q0126_19.htm";
                        break;
                    }
                    if (n == 100) {
                        string = "grave_of_brave_man_q0126_25.htm";
                        break;
                    }
                    if (n == 101) {
                        string = "grave_of_brave_man_q0126_29.htm";
                        break;
                    }
                    if (n == 102) {
                        string = "grave_of_brave_man_q0126_33.htm";
                        break;
                    }
                    if (n == 103) {
                        string = "grave_of_brave_man_q0126_37.htm";
                        break;
                    }
                    if (n == 104) {
                        string = "grave_of_brave_man_q0126_41.htm";
                        break;
                    }
                    if (n == 200) {
                        string = "grave_of_brave_man_q0126_46.htm";
                        break;
                    }
                    if (n == 200) {
                        string = "grave_of_brave_man_q0126_46.htm";
                        break;
                    }
                    if (n == 201) {
                        string = "grave_of_brave_man_q0126_50.htm";
                        break;
                    }
                    if (n == 202) {
                        string = "grave_of_brave_man_q0126_54.htm";
                        break;
                    }
                    if (n == 203) {
                        string = "grave_of_brave_man_q0126_58.htm";
                        break;
                    }
                    if (n == 204) {
                        string = "grave_of_brave_man_q0126_62.htm";
                        break;
                    }
                    if (n == 300) {
                        string = "grave_of_brave_man_q0126_67.htm";
                        break;
                    }
                    if (n == 301) {
                        string = "grave_of_brave_man_q0126_71.htm";
                        break;
                    }
                    if (n == 302) {
                        string = "grave_of_brave_man_q0126_75.htm";
                        break;
                    }
                    if (n == 303) {
                        string = "grave_of_brave_man_q0126_79.htm";
                        break;
                    }
                    if (n == 304) {
                        string = "grave_of_brave_man_q0126_83.htm";
                        break;
                    }
                    if (n == 400 && questState.getQuestItemsCount(8783) == 0L) {
                        questState.giveItems(8783, 1L);
                        questState.playSound("EtcSound.elcroki_song_full");
                        string = "grave_of_brave_man_q0126_86a.htm";
                        npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                        break;
                    }
                    if (n == 400 && questState.getQuestItemsCount(8783) == 1L) {
                        string = "grave_of_brave_man_q0126_87a.htm";
                        break;
                    }
                    if (n == 401) {
                        string = "grave_of_brave_man_q0126_89.htm";
                        break;
                    }
                    if (n < 402) break;
                    string = "grave_of_brave_man_q0126_91.htm";
                    break;
                }
                if (n3 != 32109) break;
                if (n == 402 && questState.getQuestItemsCount(8783) >= 1L) {
                    string = "statue_of_shilen_q0126_02.htm";
                    break;
                }
                if (n < 402) {
                    string = "statue_of_shilen_q0126_03.htm";
                    break;
                }
                if (n > 406) {
                    string = "statue_of_shilen_q0126_04.htm";
                    break;
                }
                if (n == 404) {
                    string = "statue_of_shilen_q0126_06.htm";
                    break;
                }
                if (n == 405) {
                    string = "statue_of_shilen_q0126_14.htm";
                    break;
                }
                if (n != 406) break;
                string = "statue_of_shilen_q0126_20.htm";
            }
        }
        return string;
    }
}

