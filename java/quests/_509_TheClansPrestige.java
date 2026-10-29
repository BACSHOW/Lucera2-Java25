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

public class _509_TheClansPrestige
extends Quest
implements ScriptFile {
    private static final int bJl = 31331;
    private static final int bJm = 25523;
    private static final int bJn = 25514;
    private static final int bJo = 25322;
    private static final int bJp = 25293;
    private static final int bJq = 25290;
    private static final int bJr = 8491;
    private static final int bJs = 8493;
    private static final int bJt = 8492;
    private static final int bJu = 8490;
    private static final int bJv = 8489;

    public _509_TheClansPrestige() {
        super(1);
        this.addStartNpc(31331);
        this.addKillId(25523, 25514, 25322, 25293, 25290);
        this.addQuestItem(8491, 8493, 8492, 8490, 8489);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("pledge_make_well_known");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31331) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("pledge_make_well_known", String.valueOf(0), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "grandmagister_valdis_q0509_04.htm";
            } else if (string.equalsIgnoreCase("reply=100")) {
                string2 = "grandmagister_valdis_q0509_06.htm";
            } else if (string.equalsIgnoreCase("reply=101")) {
                questState.unset("pledge_make_well_known");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "grandmagister_valdis_q0509_07.htm";
            } else if (string.equalsIgnoreCase("reply=102")) {
                questState.set("pledge_make_well_known", String.valueOf(0), true);
                string2 = "grandmagister_valdis_q0509_08.htm";
            } else if (string.equalsIgnoreCase("reply=110")) {
                string2 = "grandmagister_valdis_q0509_01a.htm";
            } else if (string.equalsIgnoreCase("reply=1")) {
                if (n == 0) {
                    questState.set("pledge_make_well_known", String.valueOf(1), true);
                    string2 = "grandmagister_valdis_q0509_09.htm";
                }
            } else if (string.equalsIgnoreCase("reply=2")) {
                if (n == 0) {
                    questState.set("pledge_make_well_known", String.valueOf(2), true);
                    string2 = "grandmagister_valdis_q0509_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply=3")) {
                if (n == 0) {
                    questState.set("pledge_make_well_known", String.valueOf(3), true);
                    string2 = "grandmagister_valdis_q0509_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply=4")) {
                if (n == 0) {
                    questState.set("pledge_make_well_known", String.valueOf(4), true);
                    string2 = "grandmagister_valdis_q0509_12.htm";
                }
            } else if (string.equalsIgnoreCase("reply=5") && n == 0) {
                questState.set("pledge_make_well_known", String.valueOf(5), true);
                string2 = "grandmagister_valdis_q0509_13.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("pledge_make_well_known");
        Clan clan = questState.getPlayer().getClan();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31331) break;
                if (clan != null && clan.getLeader().getPlayer() == questState.getPlayer()) {
                    if (clan.getLevel() >= 6) {
                        string = "grandmagister_valdis_q0509_01.htm";
                        break;
                    }
                    string = "grandmagister_valdis_q0509_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "grandmagister_valdis_q0509_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 31331) break;
                if (n == 0) {
                    string = "grandmagister_valdis_q0509_05.htm";
                    break;
                }
                if (clan.getLeader().getPlayer() != questState.getPlayer()) {
                    questState.unset("pledge_gain_fame");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string = "grandmagister_valdis_q0509_05a.htm";
                    break;
                }
                if (n == 1 && questState.getQuestItemsCount(8489) == 0L) {
                    string = "grandmagister_valdis_q0509_16.htm";
                    break;
                }
                if (n == 1 && questState.getQuestItemsCount(8489) >= 1L) {
                    int n4 = clan.incReputation(200, true, "_509_AClansFame");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n4));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8489, 1L);
                    questState.set("pledge_make_well_known", String.valueOf(0), true);
                    string = "grandmagister_valdis_q0509_17.htm";
                    break;
                }
                if (n == 2 && questState.getQuestItemsCount(8490) == 0L) {
                    string = "grandmagister_valdis_q0509_18.htm";
                    break;
                }
                if (n == 2 && questState.getQuestItemsCount(8490) >= 1L) {
                    int n5 = clan.incReputation(438, true, "_509_AClansFame");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n5));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8490, 1L);
                    questState.set("pledge_make_well_known", String.valueOf(0), true);
                    string = "grandmagister_valdis_q0509_19.htm";
                    break;
                }
                if (n == 3 && questState.getQuestItemsCount(8491) == 0L) {
                    string = "grandmagister_valdis_q0509_20.htm";
                    break;
                }
                if (n == 3 && questState.getQuestItemsCount(8491) >= 1L) {
                    int n6 = clan.incReputation(400, true, "_509_AClansFame");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n6));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8491, 1L);
                    questState.set("pledge_make_well_known", String.valueOf(0), true);
                    string = "grandmagister_valdis_q0509_21.htm";
                    break;
                }
                if (n == 4 && questState.getQuestItemsCount(8492) == 0L) {
                    string = "grandmagister_valdis_q0509_22.htm";
                    break;
                }
                if (n == 4 && questState.getQuestItemsCount(8492) >= 1L) {
                    int n7 = clan.incReputation(250, true, "_509_AClansFame");
                    questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n7));
                    questState.playSound("ItemSound.quest_fanfare_1");
                    questState.takeItems(8492, 1L);
                    questState.set("pledge_make_well_known", String.valueOf(0), true);
                    string = "grandmagister_valdis_q0509_23.htm";
                    break;
                }
                if (n == 5 && questState.getQuestItemsCount(8493) == 0L) {
                    string = "grandmagister_valdis_q0509_24.htm";
                    break;
                }
                if (n != 5 || questState.getQuestItemsCount(8493) < 1L) break;
                int n8 = clan.incReputation(150, true, "_509_AClansFame");
                questState.getPlayer().sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n8));
                questState.playSound("ItemSound.quest_fanfare_1");
                questState.takeItems(8493, 1L);
                questState.set("pledge_make_well_known", String.valueOf(0), true);
                string = "grandmagister_valdis_q0509_25.htm";
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
        int n = questState.getInt("pledge_make_well_known");
        int n2 = npcInstance.getNpcId();
        if (n2 == 25523) {
            if (n == 3 && questState.getQuestItemsCount(8491) == 0L) {
                questState.giveItems(8491, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25514) {
            if (n == 5 && questState.getQuestItemsCount(8493) == 0L) {
                questState.giveItems(8493, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25322) {
            if (n == 4 && questState.getQuestItemsCount(8492) == 0L) {
                questState.giveItems(8492, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25293) {
            if (n == 2 && questState.getQuestItemsCount(8490) == 0L) {
                questState.giveItems(8490, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 25290 && n == 1 && questState.getQuestItemsCount(8489) == 0L) {
            questState.giveItems(8489, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
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
}

