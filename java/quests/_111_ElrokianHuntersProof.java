/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.scripts.ScriptFile;

public class _111_ElrokianHuntersProof
extends Quest
implements ScriptFile {
    private static final int aEz = 32113;
    private static final int aEA = 32115;
    private static final int aEB = 32116;
    private static final int[] aEC = new int[]{22196, 22197, 22198, 22218, 22223};
    private static final int[] aED = new int[]{22200, 22201, 22202, 22219, 22224};
    private static final int[] aEE = new int[]{22203, 22204, 22205, 22220, 22225};
    private static final int[] aEF = new int[]{22208, 22209, 22210, 22221, 22226};
    private static final int aEG = 8768;
    private static final int aEH = 8770;
    private static final int aEI = 8771;
    private static final int aEJ = 8772;
    private static final int axu = 8763;
    private static final int axv = 8764;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _111_ElrokianHuntersProof() {
        super(1);
        this.addStartNpc(32113);
        this.addTalkId(32115);
        this.addTalkId(32116);
        this.addKillId(aEC);
        this.addKillId(aED);
        this.addKillId(aEE);
        this.addKillId(aEF);
        this.addQuestItem(8768, 8770, 8771, 8772);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        Player player = questState.getPlayer();
        if (string.equalsIgnoreCase("marquez_q111_2.htm") && n == 0) {
            questState.setCond(2);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("asamah_q111_2.htm")) {
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("marquez_q111_4.htm")) {
            questState.setCond(4);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("marquez_q111_6.htm")) {
            questState.setCond(6);
            questState.takeItems(8768, -1L);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("kirikachin_q111_2.htm")) {
            questState.setCond(7);
            player.sendPacket((IStaticPacket)new PlaySound("EtcSound.elcroki_song_full"));
        } else if (string.equalsIgnoreCase("kirikachin_q111_3.htm")) {
            questState.setCond(8);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("asamah_q111_4.htm")) {
            questState.setCond(9);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("asamah_q111_5.htm")) {
            questState.setCond(10);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("asamah_q111_7.htm")) {
            questState.takeItems(8770, -1L);
            questState.takeItems(8771, -1L);
            questState.takeItems(8772, -1L);
            questState.setCond(12);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("asamah_q111_8.htm")) {
            questState.giveItems(57, 1022636L);
            questState.giveItems(8763, 1L);
            questState.giveItems(8764, 100L);
            questState.setState(3);
            questState.exitCurrentQuest(false);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 32113) {
            if (questState.getPlayer().getLevel() >= 75 && n2 == 0) {
                string = "marquez_q111_1.htm";
            } else if (questState.getPlayer().getLevel() < 75 && n2 == 0) {
                string = "marquez_q111_0.htm";
            } else if (n2 == 3) {
                string = "marquez_q111_3.htm";
            } else if (n2 == 5) {
                string = "marquez_q111_5.htm";
            }
        } else if (n == 32115) {
            if (n2 == 2) {
                string = "asamah_q111_1.htm";
            } else if (n2 == 8) {
                string = "asamah_q111_3.htm";
            } else if (n2 == 11) {
                string = "asamah_q111_6.htm";
            }
        } else if (n == 32116 && n2 == 6) {
            string = "kirikachin_q111_1.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n2 == 4) {
            for (int n3 : aEC) {
                if (n != n3 || questState.getQuestItemsCount(8768) >= 50L) continue;
                questState.giveItems(8768, 1L, false);
                if (questState.getQuestItemsCount(8768) == 50L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(5);
                    return null;
                }
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 10) {
            for (int n4 : aED) {
                if (n != n4 || questState.getQuestItemsCount(8770) >= 10L) continue;
                questState.giveItems(8770, 1L, false);
                return null;
            }
            for (int n4 : aEE) {
                if (n != n4 || questState.getQuestItemsCount(8771) >= 10L) continue;
                questState.giveItems(8771, 1L, false);
                return null;
            }
            for (int n4 : aEF) {
                if (n != n4 || questState.getQuestItemsCount(8772) >= 10L) continue;
                questState.giveItems(8772, 1L, false);
                return null;
            }
            if (questState.getQuestItemsCount(8770) >= 10L && questState.getQuestItemsCount(8771) >= 10L && questState.getQuestItemsCount(8772) >= 10L) {
                questState.setCond(11);
                questState.playSound("ItemSound.quest_middle");
                return null;
            }
        }
        return null;
    }
}

