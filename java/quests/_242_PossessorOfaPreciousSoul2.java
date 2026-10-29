/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import quests._241_PossessorOfaPreciousSoul1;

public class _242_PossessorOfaPreciousSoul2
extends Quest
implements ScriptFile {
    private static final int bbY = 30738;
    private static final int bbZ = 31752;
    private static final int bca = 31746;
    private static final int bcb = 31748;
    private static final int bby = 31743;
    private static final int bcc = 31751;
    private static final int bbB = 31744;
    private static final int bbD = 31742;
    private static final int bcd = 31747;
    private static final int bce = 30759;
    private static final int bcf = 27317;
    private static final int bcg = 7595;
    private static final int bch = 7596;
    private static final int bci = 7590;
    private static final int bbX = 7677;
    private static final int bcj = 7678;

    public _242_PossessorOfaPreciousSoul2() {
        super(0);
        this.addStartNpc(31742);
        this.addTalkId(31743, 31744, 31751, 31752, 30759, 30738, 31746, 31748, 31747);
        this.addKillId(27317);
        this.addQuestItem(7595, 7596, 7590);
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
        int n = questState.getInt("noble_soul_noblesse_2");
        int n2 = questState.getInt("noble_soul_noblesse_2_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 31742) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("noble_soul_noblesse_2", String.valueOf(1), true);
                questState.set("noble_soul_noblesse_2_ex", String.valueOf(0), true);
                questState.takeItems(7677, -1L);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "virgil_q0242_03.htm";
            }
        } else if (n3 == 31743) {
            if (string.equalsIgnoreCase("menu_select?ask=242&reply=1")) {
                string2 = "kasandra_q0242_02.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=242&reply=2")) {
                string2 = "kasandra_q0242_03.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=242&reply=3")) {
                string2 = "kasandra_q0242_04.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=242&reply=4")) {
                questState.setCond(2);
                questState.set("noble_soul_noblesse_2", String.valueOf(2), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "kasandra_q0242_05.htm";
            }
        } else if (n3 == 31744) {
            if (string.equalsIgnoreCase("menu_select?ask=242&reply=1") && n == 2) {
                questState.setCond(3);
                questState.set("noble_soul_noblesse_2", String.valueOf(3), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ogmar_q0242_02.htm";
            }
        } else if (n3 == 31751) {
            if (string.equalsIgnoreCase("menu_select?ask=242&reply=1") && n >= 3) {
                questState.setCond(4);
                questState.set("noble_soul_noblesse_2", String.valueOf(4), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "mysterious_knight_q0242_02.htm";
            }
        } else if (n3 == 30759) {
            if (string.equalsIgnoreCase("menu_select?ask=242&reply=1") && n == 6) {
                questState.setCond(7);
                questState.set("noble_soul_noblesse_2", String.valueOf(7), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "witch_kalis_q0242_02.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=242&reply=2") && n == 8) {
                questState.setCond(9);
                questState.set("noble_soul_noblesse_2", String.valueOf(9), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "witch_kalis_q0242_05.htm";
            }
        } else if (n3 == 30738) {
            if (string.equalsIgnoreCase("menu_select?ask=242&reply=1") && n == 7) {
                questState.setCond(8);
                questState.set("noble_soul_noblesse_2", String.valueOf(8), true);
                questState.giveItems(7596, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "alchemist_matild_q0242_02.htm";
            }
        } else if (n3 == 31746) {
            if (string.equalsIgnoreCase("24201")) {
                if (npcInstance != null) {
                    npcInstance.doDie(npcInstance);
                    Location location = Location.findPointToStay(npcInstance.getX(), npcInstance.getY(), npcInstance.getZ(), 50, 150, npcInstance.getGeoIndex());
                    questState.addSpawn(31747, location.getX(), location.getY(), location.getZ(), 300000);
                }
                return null;
            }
        } else if (n3 == 31748) {
            if (string.equalsIgnoreCase("menu_select?ask=242&reply=1") && n == 9) {
                if (n2 >= 3) {
                    questState.setCond(10);
                    questState.set("noble_soul_noblesse_2", String.valueOf(10), true);
                    questState.takeItems(7595, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "holding_cornerstone_q0242_03.htm";
                    npcInstance.doCast(SkillTable.getInstance().getInfo(4546, 1), questState.getPlayer(), true);
                    if (npcInstance != null) {
                        npcInstance.doDie(npcInstance);
                    }
                } else {
                    questState.set("noble_soul_noblesse_2_ex", String.valueOf(n2 + 1), true);
                    questState.takeItems(7595, 1L);
                    string2 = "holding_cornerstone_q0242_03.htm";
                    npcInstance.doCast(SkillTable.getInstance().getInfo(4546, 1), questState.getPlayer(), true);
                    if (npcInstance != null) {
                        npcInstance.doDie(npcInstance);
                    }
                }
            }
        } else if (n3 == 31747 && string.equalsIgnoreCase("24202")) {
            if (npcInstance != null) {
                npcInstance.doDie(npcInstance);
            }
            return null;
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (!questState.getPlayer().isSubClassActive()) {
            return "quest_not_subclass001.htm";
        }
        String string = "no-quest";
        Player player = questState.getPlayer();
        int n = questState.getInt("noble_soul_noblesse_2");
        int n2 = questState.getInt("noble_soul_noblesse_2_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 31742) break;
                QuestState questState2 = questState.getPlayer().getQuestState(_241_PossessorOfaPreciousSoul1.class);
                if (questState2 != null && questState2.getState() == 3 && questState.getPlayer().getLevel() >= 60) {
                    string = "virgil_q0242_01.htm";
                    break;
                }
                string = "virgil_q0242_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n3 == 31742) {
                    if (n == 1) {
                        string = "virgil_q0242_04.htm";
                        break;
                    }
                    if (n > 1 && n < 11) {
                        string = "virgil_q0242_05.htm";
                        break;
                    }
                    if (n < 11) break;
                    string = "virgil_q0242_06.htm";
                    break;
                }
                if (n3 == 31743) {
                    if (n == 1) {
                        string = "kasandra_q0242_01.htm";
                        break;
                    }
                    if (n >= 2 && n < 11) {
                        string = "kasandra_q0242_06.htm";
                        break;
                    }
                    if (n < 11) break;
                    questState.giveItems(7678, 1L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string = "kasandra_q0242_07.htm";
                    player.sendPacket((IStaticPacket)new SocialAction(questState.getPlayer().getObjectId(), 3));
                    break;
                }
                if (n3 == 31744) {
                    if (n == 2) {
                        string = "ogmar_q0242_01.htm";
                        break;
                    }
                    if (n < 3) break;
                    string = "ogmar_q0242_03.htm";
                    break;
                }
                if (n3 == 31751) {
                    if (n == 3) {
                        string = "mysterious_knight_q0242_01.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "mysterious_knight_q0242_03.htm";
                        break;
                    }
                    if (n == 5) {
                        questState.setCond(6);
                        questState.set("noble_soul_noblesse_2", String.valueOf(6), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "mysterious_knight_q0242_04.htm";
                        break;
                    }
                    if (n != 6) break;
                    string = "mysterious_knight_q0242_05.htm";
                    break;
                }
                if (n3 == 31752) {
                    if (n == 4 && n2 < 3) {
                        questState.set("noble_soul_noblesse_2_ex", String.valueOf(n2 + 1), true);
                        string = "dead_angel_q0242_01.htm";
                        npcInstance.doDie(npcInstance);
                        break;
                    }
                    questState.setCond(5);
                    questState.set("noble_soul_noblesse_2", String.valueOf(5), true);
                    questState.set("noble_soul_noblesse_2_ex", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    string = "dead_angel_q0242_02.htm";
                    break;
                }
                if (n3 == 30759) {
                    if (n == 6) {
                        string = "witch_kalis_q0242_01.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "witch_kalis_q0242_03.htm";
                        break;
                    }
                    if (n == 8) {
                        questState.takeItems(7596, -1L);
                        string = "witch_kalis_q0242_04.htm";
                        break;
                    }
                    if (n < 9) break;
                    string = "witch_kalis_q0242_06.htm";
                    break;
                }
                if (n3 == 30738) {
                    if (n == 7) {
                        string = "alchemist_matild_q0242_01.htm";
                        break;
                    }
                    if (n != 8) break;
                    string = "alchemist_matild_q0242_03.htm";
                    break;
                }
                if (n3 == 31746) {
                    if (n < 10) {
                        string = "fallen_unicorn_q0242_01.htm";
                        break;
                    }
                    if (n < 10) break;
                    string = "fallen_unicorn_q0242_02.htm";
                    questState.startQuestTimer("24201", 3000L, npcInstance);
                    break;
                }
                if (n3 == 31748) {
                    if (questState.getQuestItemsCount(7595) < 1L && n == 9) {
                        string = "holding_cornerstone_q0242_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(7595) < 1L || n != 9) break;
                    string = "holding_cornerstone_q0242_02.htm";
                    break;
                }
                if (n3 != 31747) break;
                if (n == 10) {
                    questState.setCond(11);
                    questState.set("noble_soul_noblesse_2", String.valueOf(11), true);
                    questState.playSound("ItemSound.quest_middle");
                    string = "white_unicorn_q0242_01.htm";
                    questState.startQuestTimer("24202", 3000L, npcInstance);
                    break;
                }
                if (n != 11) break;
                string = "white_unicorn_q0242_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (!questState.getPlayer().isSubClassActive()) {
            return null;
        }
        int n = questState.getInt("noble_soul_noblesse_2");
        int n2 = npcInstance.getNpcId();
        if (n2 == 27317 && n == 9 && questState.getQuestItemsCount(7595) < 4L) {
            questState.giveItems(7595, 1L);
            if (questState.getQuestItemsCount(7595) >= 4L) {
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

