/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.ScriptFile;
import quests._242_PossessorOfaPreciousSoul2;

public class _246_PossessorOfaPreciousSoul3
extends Quest
implements ScriptFile {
    private static final int bbv = 31740;
    private static final int bbj = 30721;
    private static final int bck = 31741;
    private static final int bcl = 21541;
    private static final int bcm = 21544;
    private static final int bcn = 25325;
    private static final int bco = 7591;
    private static final int bcp = 7592;
    private static final int bcq = 7593;
    private static final int bcr = 7594;
    private static final int bcj = 7678;
    private static final int bcs = 7679;

    public _246_PossessorOfaPreciousSoul3() {
        super(1);
        this.addStartNpc(31740);
        this.addTalkId(31741, 30721);
        this.addKillId(21541, 21544, 25325);
        this.addQuestItem(7591, 7592, 7593, 7594);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        Player player = questState.getPlayer();
        int n = questState.getInt("noble_soul_noblesse_3_cookie");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31740) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("noble_soul_noblesse_3", String.valueOf(11), true);
                questState.takeItems(7678, -1L);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "caradine_q0246_0104.htm";
            }
        } else if (n2 == 31741) {
            if (string.equalsIgnoreCase("menu_select?ask=246&reply=1") && n == 1) {
                questState.setCond(2);
                questState.set("noble_soul_noblesse_3", String.valueOf(21), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ossian_q0246_0201.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=246&reply=1") && n == 2) {
                if (questState.getQuestItemsCount(7591) >= 1L && questState.getQuestItemsCount(7592) >= 1L) {
                    questState.setCond(4);
                    questState.set("noble_soul_noblesse_3", String.valueOf(31), true);
                    questState.takeItems(7591, 1L);
                    questState.takeItems(7592, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "ossian_q0246_0301.htm";
                } else {
                    string2 = "ossian_q0246_0302.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=246&reply=1") && n == 3) {
                if (questState.getQuestItemsCount(7593) >= 1L) {
                    questState.setCond(6);
                    questState.set("noble_soul_noblesse_3", String.valueOf(41), true);
                    questState.takeItems(7593, 1L);
                    questState.giveItems(7594, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "ossian_q0246_0401.htm";
                } else {
                    string2 = "ossian_q0246_0402.htm";
                }
            }
        } else if (n2 == 30721 && string.equalsIgnoreCase("menu_select?ask=246&reply=3") && n == 4) {
            if (questState.getQuestItemsCount(7594) >= 1L) {
                questState.takeItems(7594, -1L);
                questState.giveItems(7679, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
                player.sendPacket((IStaticPacket)new SocialAction(questState.getPlayer().getObjectId(), 3));
                string2 = "magister_ladd_q0246_0501.htm";
            } else {
                string2 = "magister_ladd_q0246_0502.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (!questState.getPlayer().isSubClassActive()) {
            return "quest_not_subclass001.htm";
        }
        String string = "no-quest";
        int n = questState.getInt("noble_soul_noblesse_3");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31740) break;
                QuestState questState2 = questState.getPlayer().getQuestState(_242_PossessorOfaPreciousSoul2.class);
                if (questState.getQuestItemsCount(7678) >= 1L && questState2 != null && questState2.getState() == 3 && questState.getPlayer().isSubClassActive() && questState.getPlayer().getLevel() >= 65) {
                    string = "caradine_q0246_0101.htm";
                    break;
                }
                string = "caradine_q0246_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 31740) {
                    if (n != 11) break;
                    string = "caradine_q0246_0105.htm";
                    break;
                }
                if (n2 == 31741) {
                    if (n == 11) {
                        questState.set("noble_soul_noblesse_3_cookie", String.valueOf(1), true);
                        string = "ossian_q0246_0101.htm";
                        break;
                    }
                    if (n <= 22 && n >= 21) {
                        if (n == 22 && questState.getQuestItemsCount(7591) >= 1L && questState.getQuestItemsCount(7592) >= 1L) {
                            questState.set("noble_soul_noblesse_3_cookie", String.valueOf(2), true);
                            string = "ossian_q0246_0202.htm";
                            break;
                        }
                        string = "ossian_q0246_0203.htm";
                        break;
                    }
                    if (n <= 32 && n >= 31) {
                        if (n == 32 && questState.getQuestItemsCount(7593) >= 1L) {
                            questState.set("noble_soul_noblesse_3_cookie", String.valueOf(3), true);
                            string = "ossian_q0246_0303.htm";
                            break;
                        }
                        string = "ossian_q0246_0304.htm";
                        break;
                    }
                    if (n != 41) break;
                    string = "ossian_q0246_0403.htm";
                    break;
                }
                if (n2 != 30721 || questState.getQuestItemsCount(7594) < 1L || n != 41) break;
                questState.set("noble_soul_noblesse_3_cookie", String.valueOf(4), true);
                string = "magister_ladd_q0246_0401.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (!questState.getPlayer().isSubClassActive()) {
            return null;
        }
        int n = questState.getInt("noble_soul_noblesse_3");
        int n2 = npcInstance.getNpcId();
        if (n2 == 21541) {
            int n3;
            if (n == 21 && (n3 = Rnd.get(1000)) < 200) {
                if (questState.getQuestItemsCount(7591) + 1L >= 1L) {
                    if (questState.getQuestItemsCount(7591) < 1L) {
                        questState.giveItems(7591, 1L - questState.getQuestItemsCount(7591));
                        questState.playSound("ItemSound.quest_middle");
                    }
                    if (questState.getQuestItemsCount(7592) >= 1L) {
                        questState.setCond(3);
                        questState.set("noble_soul_noblesse_3", String.valueOf(22), true);
                    }
                } else {
                    questState.giveItems(7591, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21544) {
            int n4;
            if (n == 21 && (n4 = Rnd.get(1000)) < 200) {
                if (questState.getQuestItemsCount(7592) + 1L >= 1L) {
                    if (questState.getQuestItemsCount(7592) < 1L) {
                        questState.giveItems(7592, 1L - questState.getQuestItemsCount(7592));
                        questState.playSound("ItemSound.quest_middle");
                    }
                    if (questState.getQuestItemsCount(7591) >= 1L) {
                        questState.setCond(3);
                        questState.set("noble_soul_noblesse_3", String.valueOf(22), true);
                    }
                } else {
                    questState.giveItems(7592, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 25325 && n == 31) {
            Player player = questState.getPlayer();
            List<Player> list = questState.getPartyMembers(2, Config.ALT_PARTY_DISTRIBUTION_RANGE, player);
            for (Player player2 : list) {
                QuestState questState2 = player2.getQuestState(this);
                if (questState2 == null || !player2.isSubClassActive() || questState2.getQuestItemsCount(7593) != 0L) continue;
                questState2.setCond(5);
                questState2.set("noble_soul_noblesse_3", String.valueOf(32), true);
                questState2.giveItems(7593, 1L);
                questState2.playSound("ItemSound.quest_middle");
            }
        }
        return null;
    }
}

