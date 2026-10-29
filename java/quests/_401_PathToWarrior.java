/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _401_PathToWarrior
extends Quest
implements ScriptFile {
    int AURON = 30010;
    int SIMPLON = 30253;
    int TRACKER_SKELETON = 20035;
    int POISON_SPIDER = 20038;
    int TRACKER_SKELETON_LD = 20042;
    int ARACHNID_TRACKER = 20043;
    int EINS_LETTER_ID = 1138;
    int WARRIOR_GUILD_MARK_ID = 1139;
    int RUSTED_BRONZE_SWORD1_ID = 1140;
    int RUSTED_BRONZE_SWORD2_ID = 1141;
    int SIMPLONS_LETTER_ID = 1143;
    int POISON_SPIDER_LEG2_ID = 1144;
    int MEDALLION_OF_WARRIOR_ID = 1145;
    int RUSTED_BRONZE_SWORD3_ID = 1142;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _401_PathToWarrior() {
        super(0);
        this.addStartNpc(this.AURON);
        this.addTalkId(this.SIMPLON);
        this.addKillId(this.TRACKER_SKELETON);
        this.addKillId(this.POISON_SPIDER);
        this.addKillId(this.TRACKER_SKELETON_LD);
        this.addKillId(this.ARACHNID_TRACKER);
        this.addQuestItem(this.SIMPLONS_LETTER_ID, this.RUSTED_BRONZE_SWORD2_ID, this.EINS_LETTER_ID, this.WARRIOR_GUILD_MARK_ID, this.RUSTED_BRONZE_SWORD1_ID, this.POISON_SPIDER_LEG2_ID, this.RUSTED_BRONZE_SWORD3_ID);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("401_1")) {
            string2 = questState.getPlayer().getClassId().getId() == 0 ? (questState.getPlayer().getLevel() >= 18 ? (questState.getQuestItemsCount(this.MEDALLION_OF_WARRIOR_ID) > 0L ? "ein_q0401_04.htm" : "ein_q0401_05.htm") : "ein_q0401_02.htm") : (questState.getPlayer().getClassId().getId() == 1 ? "ein_q0401_02a.htm" : "ein_q0401_03.htm");
        } else if (string.equalsIgnoreCase("401_2")) {
            string2 = "ein_q0401_10.htm";
        } else if (string.equalsIgnoreCase("401_3")) {
            string2 = "ein_q0401_11.htm";
            questState.takeItems(this.SIMPLONS_LETTER_ID, 1L);
            questState.takeItems(this.RUSTED_BRONZE_SWORD2_ID, 1L);
            questState.giveItems(this.RUSTED_BRONZE_SWORD3_ID, 1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("1")) {
            if (questState.getQuestItemsCount(this.EINS_LETTER_ID) == 0L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.giveItems(this.EINS_LETTER_ID, 1L);
                string2 = "ein_q0401_06.htm";
            }
        } else if (string.equalsIgnoreCase("30253_1")) {
            string2 = "trader_simplon_q0401_02.htm";
            questState.takeItems(this.EINS_LETTER_ID, 1L);
            questState.giveItems(this.WARRIOR_GUILD_MARK_ID, 1L);
            questState.setCond(2);
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
            questState.setCond(0);
        }
        if (n == this.AURON && n3 == 0) {
            string = "ein_q0401_01.htm";
        } else if (n == this.AURON && questState.getQuestItemsCount(this.EINS_LETTER_ID) > 0L) {
            string = "ein_q0401_07.htm";
        } else if (n == this.AURON && questState.getQuestItemsCount(this.WARRIOR_GUILD_MARK_ID) == 1L) {
            string = "ein_q0401_08.htm";
        } else if (n == this.SIMPLON && questState.getQuestItemsCount(this.EINS_LETTER_ID) > 0L) {
            string = "trader_simplon_q0401_01.htm";
        } else if (n == this.SIMPLON && questState.getQuestItemsCount(this.WARRIOR_GUILD_MARK_ID) > 0L) {
            if (questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD1_ID) < 1L) {
                string = "trader_simplon_q0401_03.htm";
            } else if (questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD1_ID) < 10L) {
                string = "trader_simplon_q0401_04.htm";
            } else if (questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD1_ID) >= 10L) {
                questState.takeItems(this.WARRIOR_GUILD_MARK_ID, -1L);
                questState.takeItems(this.RUSTED_BRONZE_SWORD1_ID, -1L);
                questState.giveItems(this.RUSTED_BRONZE_SWORD2_ID, 1L);
                questState.giveItems(this.SIMPLONS_LETTER_ID, 1L);
                questState.setCond(4);
                string = "trader_simplon_q0401_05.htm";
            }
        } else if (n == this.SIMPLON && questState.getQuestItemsCount(this.SIMPLONS_LETTER_ID) > 0L) {
            string = "trader_simplon_q0401_06.htm";
        } else if (n == this.AURON && questState.getQuestItemsCount(this.SIMPLONS_LETTER_ID) > 0L && questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD2_ID) > 0L && questState.getQuestItemsCount(this.WARRIOR_GUILD_MARK_ID) == 0L && questState.getQuestItemsCount(this.EINS_LETTER_ID) == 0L) {
            string = "ein_q0401_09.htm";
        } else if (n == this.AURON && questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD3_ID) > 0L && questState.getQuestItemsCount(this.WARRIOR_GUILD_MARK_ID) == 0L && questState.getQuestItemsCount(this.EINS_LETTER_ID) == 0L) {
            if (questState.getQuestItemsCount(this.POISON_SPIDER_LEG2_ID) < 20L) {
                string = "ein_q0401_12.htm";
            } else if (questState.getQuestItemsCount(this.POISON_SPIDER_LEG2_ID) > 19L) {
                questState.takeItems(this.POISON_SPIDER_LEG2_ID, -1L);
                questState.takeItems(this.RUSTED_BRONZE_SWORD3_ID, -1L);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(this.MEDALLION_OF_WARRIOR_ID, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 1500L);
                    }
                }
                string = "ein_q0401_13.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == this.TRACKER_SKELETON || n == this.TRACKER_SKELETON_LD) {
            if (n2 == 2 && questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD1_ID) < 10L) {
                questState.giveItems(this.RUSTED_BRONZE_SWORD1_ID, 1L);
                if (questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD1_ID) == 10L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(3);
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if ((n == this.ARACHNID_TRACKER || n == this.POISON_SPIDER) && questState.getQuestItemsCount(this.POISON_SPIDER_LEG2_ID) < 20L && questState.getQuestItemsCount(this.RUSTED_BRONZE_SWORD3_ID) == 1L && questState.getItemEquipped(7) == this.RUSTED_BRONZE_SWORD3_ID) {
            questState.giveItems(this.POISON_SPIDER_LEG2_ID, 1L);
            if (questState.getQuestItemsCount(this.POISON_SPIDER_LEG2_ID) == 20L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(6);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

