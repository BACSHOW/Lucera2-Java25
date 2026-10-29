/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _371_ShriekOfGhosts
extends Quest
implements ScriptFile {
    private static int bth = 30867;
    private static int bqT = 30929;
    private static int bti = 20818;
    private static int btj = 20820;
    private static int btk = 20824;
    private static int btl = 6002;
    private static int btm = 6003;
    private static int btn = 6004;
    private static int bto = 6005;
    private static int btp = 6006;
    private static int btq = 5903;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _371_ShriekOfGhosts() {
        super(1);
        this.addStartNpc(bth);
        this.addTalkId(bqT);
        this.addKillId(bti, btj, btk);
        this.addQuestItem(btq);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == bth) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("spirits_cry_secrets", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "seer_reva_q0371_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (questState.getQuestItemsCount(btq) < 1L) {
                    string2 = "seer_reva_q0371_06.htm";
                } else if (questState.getQuestItemsCount(btq) >= 1L && questState.getQuestItemsCount(btq) < 100L) {
                    questState.giveItems(57, questState.getQuestItemsCount(btq) * 1000L + 15000L);
                    questState.takeItems(btq, -1L);
                    string2 = "seer_reva_q0371_07.htm";
                } else if (questState.getQuestItemsCount(btq) >= 100L) {
                    questState.giveItems(57, questState.getQuestItemsCount(btq) * 1000L + 37700L);
                    questState.takeItems(btq, -1L);
                    string2 = "seer_reva_q0371_08.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "seer_reva_q0371_09.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (questState.getQuestItemsCount(btq) > 0L) {
                    questState.giveItems(57, questState.getQuestItemsCount(btq) * 1000L);
                }
                questState.takeItems(btq, -1L);
                questState.unset("spirits_cry_secrets");
                questState.exitCurrentQuest(true);
                string2 = "seer_reva_q0371_10.htm";
            }
        } else if (n == bqT && string.equalsIgnoreCase("reply_1")) {
            if (questState.getQuestItemsCount(btl) < 1L) {
                string2 = "patrin_q0371_02.htm";
            } else if (questState.getQuestItemsCount(btl) >= 1L) {
                int n2 = Rnd.get(100);
                if (n2 < 2) {
                    questState.giveItems(btm, 1L);
                    questState.takeItems(btl, 1L);
                    string2 = "patrin_q0371_03.htm";
                } else if (n2 < 32) {
                    questState.giveItems(btn, 1L);
                    questState.takeItems(btl, 1L);
                    string2 = "patrin_q0371_04.htm";
                } else if (n2 < 62) {
                    questState.giveItems(bto, 1L);
                    questState.takeItems(btl, 1L);
                    string2 = "patrin_q0371_05.htm";
                } else if (n2 < 77) {
                    questState.giveItems(btp, 1L);
                    questState.takeItems(btl, 1L);
                    string2 = "patrin_q0371_06.htm";
                } else {
                    questState.giveItems(btl, 1L);
                    string2 = "patrin_q0371_07.htm";
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("spirits_cry_secrets");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != bth) break;
                if (questState.getPlayer().getLevel() < 59) {
                    string = "seer_reva_q0371_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() < 59) break;
                string = "seer_reva_q0371_02.htm";
                break;
            }
            case 2: {
                if (n2 == bth) {
                    if (n == 1 && questState.getQuestItemsCount(btl) < 1L) {
                        string = "seer_reva_q0371_04.htm";
                        break;
                    }
                    if (n != 1 || questState.getQuestItemsCount(btl) < 1L) break;
                    string = "seer_reva_q0371_05.htm";
                    break;
                }
                if (n2 != bqT || n != 1) break;
                string = "patrin_q0371_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("spirits_cry_secrets");
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 == bti) {
                int n3 = Rnd.get(1000);
                if (n3 < 350) {
                    questState.giveItems(btq, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n3 < 400) {
                    questState.giveItems(btl, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == btj) {
                int n4 = Rnd.get(1000);
                if (n4 < 583) {
                    questState.giveItems(btq, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n4 < 673) {
                    questState.giveItems(btl, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == btk) {
                int n5 = Rnd.get(1000);
                if (n5 < 458) {
                    questState.giveItems(btq, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n5 < 538) {
                    questState.giveItems(btl, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

