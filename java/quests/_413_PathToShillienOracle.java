/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _413_PathToShillienOracle
extends Quest
implements ScriptFile {
    public final int SIDRA = 30330;
    public final int ADONIUS = 30375;
    public final int TALBOT = 30377;
    public final int ZOMBIE_SOLDIER = 20457;
    public final int ZOMBIE_WARRIOR = 20458;
    public final int SHIELD_SKELETON = 20514;
    public final int SKELETON_INFANTRYMAN = 20515;
    public final int DARK_SUCCUBUS = 20776;
    public final int SIDRAS_LETTER1_ID = 1262;
    public final int BLANK_SHEET1_ID = 1263;
    public final int BLOODY_RUNE1_ID = 1264;
    public final int GARMIEL_BOOK_ID = 1265;
    public final int PRAYER_OF_ADON_ID = 1266;
    public final int PENITENTS_MARK_ID = 1267;
    public final int ASHEN_BONES_ID = 1268;
    public final int ANDARIEL_BOOK_ID = 1269;
    public final int ORB_OF_ABYSS_ID = 1270;
    public final int[] ASHEN_BONES_DROP = new int[]{20457, 20458, 20514, 20515};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _413_PathToShillienOracle() {
        super(0);
        this.addStartNpc(30330);
        this.addTalkId(30375);
        this.addTalkId(30377);
        this.addKillId(20776);
        for (int n : this.ASHEN_BONES_DROP) {
            this.addKillId(n);
        }
        this.addQuestItem(1268);
        this.addQuestItem(1262);
        this.addQuestItem(1269);
        this.addQuestItem(1267);
        this.addQuestItem(1265);
        this.addQuestItem(1266);
        this.addQuestItem(1263);
        this.addQuestItem(1264);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            string2 = "master_sidra_q0413_06.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(1262, 1L);
        } else if (string.equalsIgnoreCase("413_1")) {
            if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 38 && questState.getQuestItemsCount(1270) < 1L) {
                string2 = "master_sidra_q0413_05.htm";
            } else if (questState.getPlayer().getClassId().getId() != 38) {
                string2 = questState.getPlayer().getClassId().getId() == 42 ? "master_sidra_q0413_02a.htm" : "master_sidra_q0413_03.htm";
            } else if (questState.getPlayer().getLevel() < 18 && questState.getPlayer().getClassId().getId() == 38) {
                string2 = "master_sidra_q0413_02.htm";
            } else if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 38 && questState.getQuestItemsCount(1270) > 0L) {
                string2 = "master_sidra_q0413_04.htm";
            }
        } else if (string.equalsIgnoreCase("30377_1")) {
            string2 = "magister_talbot_q0413_02.htm";
            questState.takeItems(1262, -1L);
            questState.giveItems(1263, 5L);
            questState.playSound("ItemSound.quest_itemget");
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("30375_1")) {
            string2 = "priest_adonius_q0413_02.htm";
        } else if (string.equalsIgnoreCase("30375_2")) {
            string2 = "priest_adonius_q0413_03.htm";
        } else if (string.equalsIgnoreCase("30375_3")) {
            string2 = "priest_adonius_q0413_04.htm";
            questState.takeItems(1266, -1L);
            questState.giveItems(1267, 1L);
            questState.playSound("ItemSound.quest_itemget");
            questState.setCond(5);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30330) {
            if (n2 < 1) {
                string = "master_sidra_q0413_01.htm";
            } else if (n2 == 1) {
                string = "master_sidra_q0413_07.htm";
            } else if (n2 == 2 | n2 == 3) {
                string = "master_sidra_q0413_08.htm";
            } else if (n2 > 3 && n2 < 7) {
                string = "master_sidra_q0413_09.htm";
            } else if (n2 == 7 && questState.getQuestItemsCount(1269) > 0L && questState.getQuestItemsCount(1265) > 0L) {
                string = "master_sidra_q0413_10.htm";
                questState.exitCurrentQuest(true);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1270, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 3120L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
            }
        } else if (n == 30377) {
            if (n2 == 1 && questState.getQuestItemsCount(1262) > 0L) {
                string = "magister_talbot_q0413_01.htm";
            } else if (n2 == 2) {
                if (questState.getQuestItemsCount(1264) < 1L) {
                    string = "magister_talbot_q0413_03.htm";
                } else if (questState.getQuestItemsCount(1264) > 0L) {
                    string = "magister_talbot_q0413_04.htm";
                }
            } else if (n2 == 3 && questState.getQuestItemsCount(1264) > 4L) {
                string = "magister_talbot_q0413_05.htm";
                questState.takeItems(1264, -1L);
                questState.giveItems(1265, 1L);
                questState.giveItems(1266, 1L);
                questState.playSound("ItemSound.quest_itemget");
                questState.setCond(4);
            } else if (n2 > 3 && n2 < 7) {
                string = "magister_talbot_q0413_06.htm";
            } else if (n2 == 7) {
                string = "magister_talbot_q0413_07.htm";
            }
        } else if (n == 30375) {
            if (n2 == 4 && questState.getQuestItemsCount(1266) > 0L) {
                string = "priest_adonius_q0413_01.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(1268) < 1L) {
                string = "priest_adonius_q0413_05.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(1268) < 10L) {
                string = "priest_adonius_q0413_06.htm";
            } else if (n2 == 6 && questState.getQuestItemsCount(1268) > 9L) {
                string = "priest_adonius_q0413_07.htm";
                questState.takeItems(1268, -1L);
                questState.takeItems(1267, -1L);
                questState.giveItems(1269, 1L);
                questState.playSound("ItemSound.quest_itemget");
                questState.setCond(7);
            } else if (n2 == 7 && questState.getQuestItemsCount(1269) > 0L) {
                string = "priest_adonius_q0413_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20776 && n2 == 2 && questState.getQuestItemsCount(1263) > 0L) {
            questState.giveItems(1264, 1L);
            questState.takeItems(1263, 1L);
            if (questState.getQuestItemsCount(1263) < 1L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        for (int n3 : this.ASHEN_BONES_DROP) {
            if (n != n3 || n2 != 5 || questState.getQuestItemsCount(1268) >= 10L) continue;
            questState.giveItems(1268, 1L);
            if (questState.getQuestItemsCount(1268) > 9L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(6);
                continue;
            }
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

