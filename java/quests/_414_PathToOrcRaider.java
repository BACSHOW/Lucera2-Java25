/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.ScriptFile;

public class _414_PathToOrcRaider
extends Quest
implements ScriptFile {
    private static final int byP = 30570;
    private static final int byQ = 30501;
    private static final int byR = 31978;
    private static final int byS = 20320;
    private static final int byT = 27045;
    private static final int byU = 27054;
    private static final int byV = 27320;
    private static final int byW = 1578;
    private static final int byX = 1579;
    private static final int byY = 1580;
    private static final int byZ = 1581;
    private static final int bza = 1582;
    private static final int bzb = 1583;
    private static final int bzc = 1584;
    private static final int bzd = 1585;
    private static final int bze = 1586;
    private static final int bzf = 1587;
    private static final int bzg = 1588;
    private static final int bzh = 1589;
    private static final int bzi = 1590;
    private static final int bzj = 1591;
    private static final int bzk = 8544;
    private static final int bzl = 1592;

    public _414_PathToOrcRaider() {
        super(0);
        this.addStartNpc(30570);
        this.addTalkId(30501, 31978);
        this.addKillId(20320, 27045, 27054, 27320);
        this.addQuestItem(1578, 1579, 1580, 1589, 1591, 8544);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("path_to_orc_raider");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getPlayer().getClassId().getId();
        int n4 = 44;
        int n5 = 45;
        if (n2 == 30570) {
            if (string.equalsIgnoreCase("quest_accept")) {
                if (questState.getPlayer().getLevel() < 18) {
                    string2 = "prefect_karukia_q0414_02.htm";
                    questState.exitCurrentQuest(true);
                } else if (n3 != n4) {
                    if (n3 == n5) {
                        string2 = "prefect_karukia_q0414_02a.htm";
                        questState.exitCurrentQuest(true);
                    } else {
                        string2 = "prefect_karukia_q0414_03.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else if (questState.getQuestItemsCount(1592) == 1L && n3 == n4) {
                    string2 = "prefect_karukia_q0414_04.htm";
                } else {
                    questState.setCond(1);
                    questState.setState(2);
                    questState.playSound("ItemSound.quest_accept");
                    questState.giveItems(1579, 1L);
                    string2 = "prefect_karukia_q0414_05.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1") && questState.getQuestItemsCount(1579) == 1L && questState.getQuestItemsCount(1580) >= 10L) {
                questState.setCond(3);
                questState.takeItems(1580, -1L);
                questState.takeItems(1579, 1L);
                questState.giveItems(1589, 1L);
                questState.giveItems(1590, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "prefect_karukia_q0414_07a.htm";
            } else if (string.equalsIgnoreCase("reply_2") && questState.getQuestItemsCount(1579) == 1L && questState.getQuestItemsCount(1580) >= 10L) {
                questState.setCond(5);
                questState.set("path_to_orc_raider", String.valueOf(2), true);
                questState.takeItems(1580, -1L);
                questState.takeItems(1579, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "prefect_karukia_q0414_07b.htm";
            }
        } else if (n2 == 31978) {
            if (string.equalsIgnoreCase("reply_3") && n == 2) {
                string2 = "prefect_tazar_q0414_01b.htm";
            } else if (string.equalsIgnoreCase("reply_4") && n == 2) {
                questState.setCond(6);
                questState.set("path_to_orc_raider", String.valueOf(3), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "prefect_tazar_q0414_02.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("path_to_orc_raider");
        int n2 = questState.getPlayer().getVarInt("profession_145");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 30570) break;
                string = "prefect_karukia_q0414_01.htm";
                break;
            }
            case 2: {
                if (n3 == 30570) {
                    if (questState.getQuestItemsCount(1579) == 1L && questState.getQuestItemsCount(1580) < 10L) {
                        string = "prefect_karukia_q0414_06.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(1579) == 1L && questState.getQuestItemsCount(1580) >= 10L && questState.getQuestItemsCount(1581) + questState.getQuestItemsCount(1583) + questState.getQuestItemsCount(1582) + questState.getQuestItemsCount(1584) + questState.getQuestItemsCount(1585) + questState.getQuestItemsCount(1586) + questState.getQuestItemsCount(1587) + questState.getQuestItemsCount(1588) + questState.getQuestItemsCount(1589) + questState.getQuestItemsCount(1590) == 0L) {
                        string = "prefect_karukia_q0414_07.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(1581) + questState.getQuestItemsCount(1583) + questState.getQuestItemsCount(1582) + questState.getQuestItemsCount(1584) + questState.getQuestItemsCount(1585) + questState.getQuestItemsCount(1586) + questState.getQuestItemsCount(1587) + questState.getQuestItemsCount(1588) + questState.getQuestItemsCount(1589) + questState.getQuestItemsCount(1590) > 0L || questState.getQuestItemsCount(1591) > 0L) {
                        string = "prefect_karukia_q0414_08.htm";
                        break;
                    }
                    if (n != 2) break;
                    string = "prefect_karukia_q0414_07b.htm";
                    break;
                }
                if (n3 == 30501) {
                    if (questState.getQuestItemsCount(1581) + questState.getQuestItemsCount(1583) + questState.getQuestItemsCount(1582) + questState.getQuestItemsCount(1584) + questState.getQuestItemsCount(1585) + questState.getQuestItemsCount(1586) + questState.getQuestItemsCount(1587) + questState.getQuestItemsCount(1588) + questState.getQuestItemsCount(1589) + questState.getQuestItemsCount(1590) >= 2L && questState.getQuestItemsCount(1591) == 0L) {
                        string = "prefect_kasman_q0414_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(1591) == 1L) {
                        string = "prefect_kasman_q0414_02.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(1591) < 2L) break;
                    questState.giveItems(1592, 1L);
                    string = "prefect_kasman_q0414_03.htm";
                    if (n2 == 0) {
                        questState.getPlayer().setVar("profession_145", String.valueOf(1), -1L);
                        questState.addExpAndSp(3200L, 3380L);
                    }
                    questState.unset("path_to_orc_raider");
                    questState.getPlayer().sendPacket((IStaticPacket)new SocialAction(questState.getPlayer().getObjectId(), 3));
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    break;
                }
                if (n3 != 31978) break;
                if (n == 2) {
                    string = "prefect_tazar_q0414_01.htm";
                    break;
                }
                if (n == 3 && questState.getQuestItemsCount(8544) == 0L) {
                    string = "prefect_tazar_q0414_03.htm";
                    break;
                }
                if (n != 3 || questState.getQuestItemsCount(8544) <= 0L) break;
                questState.giveItems(1592, 1L);
                questState.takeItems(8544, -1L);
                string = "prefect_tazar_q0414_05.htm";
                if (!questState.getPlayer().getVarB("profession_145")) {
                    questState.getPlayer().setVar("profession_145", "1", -1L);
                    questState.addExpAndSp(3200L, 3380L);
                }
                questState.unset("path_to_orc_raider");
                questState.getPlayer().sendPacket((IStaticPacket)new SocialAction(questState.getPlayer().getObjectId(), 3));
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("path_to_orc_raider");
        int n3 = npcInstance.getNpcId();
        if (n3 == 20320) {
            if (questState.getQuestItemsCount(1579) == 1L && questState.getQuestItemsCount(1580) < 10L && questState.getQuestItemsCount(1578) <= 20L) {
                if ((long)Rnd.get(100) < questState.getQuestItemsCount(1578) * 5L) {
                    questState.takeItems(1578, -1L);
                    questState.addSpawn(27045, npcInstance.getX(), npcInstance.getY(), npcInstance.getZ());
                } else {
                    questState.giveItems(1578, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n3 == 27045) {
            if (questState.getQuestItemsCount(1579) == 1L && questState.getQuestItemsCount(1580) < 10L) {
                questState.takeItems(1578, -1L);
                if (questState.getQuestItemsCount(1580) >= 10L) {
                    questState.setCond(2);
                    questState.giveItems(1580, 1L);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(1580, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n3 == 27054) {
            if (questState.getQuestItemsCount(1581) + questState.getQuestItemsCount(1583) + questState.getQuestItemsCount(1582) + questState.getQuestItemsCount(1584) + questState.getQuestItemsCount(1585) + questState.getQuestItemsCount(1586) + questState.getQuestItemsCount(1587) + questState.getQuestItemsCount(1588) + questState.getQuestItemsCount(1589) + questState.getQuestItemsCount(1590) > 0L && questState.getQuestItemsCount(1591) < 2L && Rnd.get(10) < 2) {
                questState.giveItems(1591, 1L);
                if (questState.getQuestItemsCount(1590) == 1L) {
                    questState.takeItems(1590, 1L);
                } else if (questState.getQuestItemsCount(1589) == 1L) {
                    questState.takeItems(1589, 1L);
                } else if (questState.getQuestItemsCount(1588) == 1L) {
                    questState.takeItems(1588, 1L);
                } else if (questState.getQuestItemsCount(1587) == 1L) {
                    questState.takeItems(1587, 1L);
                } else if (questState.getQuestItemsCount(1586) == 1L) {
                    questState.takeItems(1586, 1L);
                } else if (questState.getQuestItemsCount(1585) == 1L) {
                    questState.takeItems(1585, 1L);
                } else if (questState.getQuestItemsCount(1584) == 1L) {
                    questState.takeItems(1584, 1L);
                } else if (questState.getQuestItemsCount(1582) == 1L) {
                    questState.takeItems(1582, 1L);
                } else if (questState.getQuestItemsCount(1583) == 1L) {
                    questState.takeItems(1583, 1L);
                } else if (questState.getQuestItemsCount(1581) == 1L) {
                    questState.takeItems(1581, 1L);
                }
                if (questState.getQuestItemsCount(1591) >= 2L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n3 == 27320 && n2 == 3 && questState.getQuestItemsCount(8544) == 0L && (n = Rnd.get(100)) < 60) {
            questState.setCond(7);
            questState.giveItems(8544, 1L);
            questState.playSound("ItemSound.quest_middle");
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

