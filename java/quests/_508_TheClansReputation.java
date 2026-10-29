/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.ScriptFile;

public class _508_TheClansReputation
extends Quest
implements ScriptFile {
    private static final int bIY = 30868;
    private static final int bIZ = 25051;
    private static final int bJa = 25245;
    private static final int bJb = 25252;
    private static final int bJc = 25255;
    private static final int bJd = 25140;
    private static final int bJe = 25524;
    private static final int bJf = 8494;
    private static final int bJg = 8280;
    private static final int bJh = 8277;
    private static final int bJi = 8279;
    private static final int bJj = 8281;
    private static final int bJk = 8282;

    public _508_TheClansReputation() {
        super(1);
        this.addStartNpc(30868);
        this.addKillId(25051, 25245, 25252, 25255, 25140, 25524);
        this.addQuestItem(8494, 8280, 8277, 8281, 8282, 8279);
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
        int n = questState.getInt("pledge_gain_fame");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30868) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("pledge_gain_fame", String.valueOf(0), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "sir_eric_rodemai_q0508_04.htm";
            } else if (string.equalsIgnoreCase("reply=100")) {
                string2 = "sir_eric_rodemai_q0508_06.htm";
            } else if (string.equalsIgnoreCase("reply=101")) {
                questState.unset("pledge_gain_fame");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "sir_eric_rodemai_q0508_07.htm";
            } else if (string.equalsIgnoreCase("reply=102")) {
                questState.set("pledge_gain_fame", String.valueOf(0), true);
                string2 = "sir_eric_rodemai_q0508_08.htm";
            } else if (string.equalsIgnoreCase("reply=110")) {
                string2 = "sir_eric_rodemai_q0508_01a.htm";
            } else if (string.equalsIgnoreCase("reply=2")) {
                if (n == 0) {
                    questState.set("pledge_gain_fame", String.valueOf(2), true);
                    string2 = "sir_eric_rodemai_q0508_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply=4")) {
                if (n == 0) {
                    questState.set("pledge_gain_fame", String.valueOf(4), true);
                    string2 = "sir_eric_rodemai_q0508_12.htm";
                }
            } else if (string.equalsIgnoreCase("reply=5")) {
                if (n == 0) {
                    questState.set("pledge_gain_fame", String.valueOf(5), true);
                    string2 = "sir_eric_rodemai_q0508_13.htm";
                }
            } else if (string.equalsIgnoreCase("reply=6")) {
                if (n == 0) {
                    questState.set("pledge_gain_fame", String.valueOf(6), true);
                    string2 = "sir_eric_rodemai_q0508_14.htm";
                }
            } else if (string.equalsIgnoreCase("reply=7")) {
                if (n == 0) {
                    questState.set("pledge_gain_fame", String.valueOf(7), true);
                    string2 = "sir_eric_rodemai_q0508_15.htm";
                }
            } else if (string.equalsIgnoreCase("reply=8") && n == 0) {
                questState.set("pledge_gain_fame", String.valueOf(8), true);
                string2 = "sir_eric_rodemai_q0508_15a.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("pledge_gain_fame");
        Clan clan = questState.getPlayer().getClan();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30868) break;
                if (clan != null && clan.getLeader().getPlayer() == questState.getPlayer()) {
                    if (clan.getLevel() >= 5) {
                        string = "sir_eric_rodemai_q0508_01.htm";
                        break;
                    }
                    string = "sir_eric_rodemai_q0508_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "sir_eric_rodemai_q0508_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 30868) break;
                if (n == 0) {
                    string = "sir_eric_rodemai_q0508_05.htm";
                    break;
                }
                if (clan.getLeader().getPlayer() != questState.getPlayer()) {
                    questState.unset("pledge_gain_fame");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string = "sir_eric_rodemai_q0508_05a.htm";
                    break;
                }
                if (n == 2 && questState.getQuestItemsCount(8277) == 0L) {
                    string = "sir_eric_rodemai_q0508_18.htm";
                    break;
                }
                if (n == 2 && questState.getQuestItemsCount(8277) >= 1L) {
                    int n4 = clan.incReputation(85, true, "_508_TheClansReputation");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n4));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8277, 1L);
                    questState.set("pledge_gain_fame", String.valueOf(0), true);
                    string = "sir_eric_rodemai_q0508_19.htm";
                    break;
                }
                if (n == 4 && questState.getQuestItemsCount(8279) == 0L) {
                    string = "sir_eric_rodemai_q0508_22.htm";
                    break;
                }
                if (n == 4 && questState.getQuestItemsCount(8279) >= 1L) {
                    int n5 = clan.incReputation(65, true, "_508_TheClansReputation");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n5));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8279, 1L);
                    questState.set("pledge_gain_fame", String.valueOf(0), true);
                    string = "sir_eric_rodemai_q0508_23.htm";
                    break;
                }
                if (n == 5 && questState.getQuestItemsCount(8280) == 0L) {
                    string = "sir_eric_rodemai_q0508_24.htm";
                    break;
                }
                if (n == 5 && questState.getQuestItemsCount(8280) >= 1L) {
                    int n6 = clan.incReputation(50, true, "_508_TheClansReputation");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n6));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8280, 1L);
                    questState.set("pledge_gain_fame", String.valueOf(0), true);
                    string = "sir_eric_rodemai_q0508_25.htm";
                    break;
                }
                if (n == 6 && questState.getQuestItemsCount(8281) == 0L) {
                    string = "sir_eric_rodemai_q0508_26.htm";
                    break;
                }
                if (n == 6 && questState.getQuestItemsCount(8281) >= 1L) {
                    int n7 = clan.incReputation(125, true, "_508_TheClansReputation");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n7));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8281, 1L);
                    questState.set("pledge_gain_fame", String.valueOf(0), true);
                    string = "sir_eric_rodemai_q0508_27.htm";
                    break;
                }
                if (n == 7 && questState.getQuestItemsCount(8282) == 0L) {
                    string = "sir_eric_rodemai_q0508_28.htm";
                    break;
                }
                if (n == 7 && questState.getQuestItemsCount(8282) >= 1L) {
                    int n8 = clan.incReputation(71, true, "_508_TheClansReputation");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n8));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8282, 1L);
                    questState.set("pledge_gain_fame", String.valueOf(0), true);
                    string = "sir_eric_rodemai_q0508_29.htm";
                    break;
                }
                if (n == 8 && questState.getQuestItemsCount(8494) == 0L) {
                    string = "sir_eric_rodemai_q0508_30.htm";
                    break;
                }
                if (n != 8 || questState.getQuestItemsCount(8494) < 1L) break;
                int n9 = clan.incReputation(80, true, "_508_TheClansReputation");
                questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n9));
                questState.playSound("ItemSound.quest_fanfare_1");
                questState.takeItems(8494, 1L);
                questState.set("pledge_gain_fame", String.valueOf(0), true);
                string = "sir_eric_rodemai_q0508_31.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        Player player;
        try {
            player = questState.getPlayer().getClan().getLeader().getPlayer();
        }
        catch (Exception exception) {
            return null;
        }
        if (player == null) {
            return null;
        }
        if (!questState.getPlayer().equals(player) && player.getDistance(npcInstance) > (double)Config.ALT_PARTY_DISTRIBUTION_RANGE) {
            return null;
        }
        QuestState questState2 = player.getQuestState(this.getName());
        if (questState2 == null || !questState2.isStarted() || questState2.getCond() != 1) {
            return null;
        }
        int n = questState.getInt("pledge_gain_fame");
        int n2 = npcInstance.getNpcId();
        if (n2 == 25051) {
            if (n == 7 && questState.getQuestItemsCount(8282) == 0L) {
                questState.giveItems(8282, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25245) {
            if (n == 6 && questState.getQuestItemsCount(8281) == 0L) {
                questState.giveItems(8281, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25252) {
            if (n == 2 && questState.getQuestItemsCount(8277) == 0L) {
                questState.giveItems(8277, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25255) {
            if (n == 5 && questState.getQuestItemsCount(8280) == 0L) {
                questState.giveItems(8280, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25140) {
            if (n == 4 && questState.getQuestItemsCount(8279) == 0L) {
                questState.giveItems(8279, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25524 && n == 8 && questState.getQuestItemsCount(8494) == 0L) {
            questState.giveItems(8494, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

