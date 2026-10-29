/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _381_LetsBecomeARoyalMember
extends Quest
implements ScriptFile {
    private static int bxF = 5899;
    private static int bxG = 5900;
    private static int ble = 3813;
    private static int bxH = 7569;
    private static int bxI = 5898;
    private static int blJ = 30232;
    private static int bxJ = 30090;
    private static int bxK = 21018;
    private static int bxL = 27316;
    private static int bxM = 5;
    private static int bxN = 100;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _381_LetsBecomeARoyalMember() {
        super(0);
        this.addStartNpc(blJ);
        this.addTalkId(bxJ);
        this.addKillId(bxK);
        this.addKillId(bxL);
        this.addQuestItem(bxF);
        this.addQuestItem(bxG);
        this.addQuestItem(bxH);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("warehouse_keeper_sorint_q0381_02.htm")) {
            if (questState.getPlayer().getLevel() >= 55 && questState.getQuestItemsCount(ble) > 0L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "warehouse_keeper_sorint_q0381_03.htm";
            } else {
                string2 = "warehouse_keeper_sorint_q0381_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("sandra_q0381_02.htm") && questState.getCond() == 1) {
            questState.set("id", "1");
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        long l = questState.getQuestItemsCount(bxG);
        if (n2 == blJ) {
            if (n == 0) {
                string = "warehouse_keeper_sorint_q0381_01.htm";
            } else if (n == 1) {
                long l2 = questState.getQuestItemsCount(bxF);
                if (l2 > 0L && l > 0L) {
                    questState.takeItems(bxF, -1L);
                    questState.takeItems(bxG, -1L);
                    questState.giveItems(bxI, 1L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string = "warehouse_keeper_sorint_q0381_06.htm";
                } else if (l == 0L) {
                    string = "warehouse_keeper_sorint_q0381_05.htm";
                } else if (l2 == 0L) {
                    string = "warehouse_keeper_sorint_q0381_04.htm";
                }
            }
        } else {
            long l3 = questState.getQuestItemsCount(bxH);
            if (l > 0L) {
                string = "sandra_q0381_05.htm";
            } else if (l3 > 0L) {
                questState.takeItems(bxH, -1L);
                questState.giveItems(bxG, 1L);
                questState.playSound("ItemSound.quest_itemget");
                string = "sandra_q0381_04.htm";
            } else {
                string = questState.getInt("id") == 0 ? "sandra_q0381_01.htm" : "sandra_q0381_03.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = npcInstance.getNpcId();
        long l = questState.getQuestItemsCount(bxG);
        long l2 = questState.getQuestItemsCount(bxF);
        long l3 = questState.getQuestItemsCount(bxH);
        if (n == bxK && l2 == 0L) {
            if (Rnd.chance(bxM)) {
                questState.giveItems(bxF, 1L);
                if (l > 0L || l3 > 0L) {
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == bxL && l3 + l == 0L && questState.getInt("id") != 0 && Rnd.chance(bxN)) {
            questState.giveItems(bxH, 1L);
            if (l2 > 0L) {
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

