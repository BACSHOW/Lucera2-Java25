/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _171_ActsOfEvil
extends Quest
implements ScriptFile {
    private static final int aIH = 30381;
    private static final int aII = 30420;
    private static final int aIJ = 30207;
    private static final int aIK = 30437;
    private static final int aIL = 30425;
    private static final int aIM = 30617;
    private static final int aIN = 4239;
    private static final int aIO = 4240;
    private static final int aIP = 4241;
    private static final int aIQ = 4242;
    private static final int aIR = 4243;
    private static final int aIS = 4244;
    private static final int aIT = 4245;
    private static final int aIU = 4246;
    private static final int aIV = 4247;
    private static final int aIW = 4248;
    private static final int aIX = 4249;
    private static final int aIY = 20496;
    private static final int aIZ = 20497;
    private static final int aJa = 20498;
    private static final int aJb = 20499;
    private static final int aJc = 20062;
    private static final int aJd = 20438;
    private static final int aJe = 20066;
    private static final int aJf = 27190;

    public _171_ActsOfEvil() {
        super(1);
        this.addStartNpc(30381);
        this.addTalkId(30207, 30420, 30437, 30425, 30617);
        this.addKillId(20062, 20438, 27190, 20496, 20497, 20498, 20499, 20066);
        this.addQuestItem(4239, 4240, 4241, 4242, 4243, 4244, 4245, 4246, 4247, 4248, 4249);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30381) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "guard_alvah_q0171_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                questState.setCond(5);
                questState.playSound("ItemSound.quest_middle");
                string2 = "guard_alvah_q0171_07.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.setCond(7);
                questState.playSound("ItemSound.quest_middle");
                string2 = "guard_alvah_q0171_12.htm";
            }
        } else if (n == 30437) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "trader_rolento_q0171_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "trader_rolento_q0171_03.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                questState.giveItems(4247, 1L);
                questState.giveItems(4248, 1L);
                questState.takeItems(4245, questState.getQuestItemsCount(4245));
                questState.setCond(9);
                questState.playSound("ItemSound.quest_middle");
                string2 = "trader_rolento_q0171_04.htm";
            }
        } else if (n == 30617) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "turek_chief_burai_q0171_03.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "turek_chief_burai_q0171_04.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                questState.takeItems(4246, questState.getQuestItemsCount(4246));
                questState.takeItems(4247, questState.getQuestItemsCount(4247));
                questState.takeItems(4248, questState.getQuestItemsCount(4248));
                questState.setCond(10);
                questState.playSound("ItemSound.quest_middle");
                string2 = "turek_chief_burai_q0171_05.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        Player player = questState.getPlayer();
        switch (n2) {
            case 1: {
                if (n != 30381) break;
                if (player.getLevel() < 27) {
                    string = "guard_alvah_q0171_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() < 27) break;
                string = "guard_alvah_q0171_02.htm";
                break;
            }
            case 2: {
                if (n == 30381) {
                    if (n3 == 1) {
                        string = "guard_alvah_q0171_04.htm";
                        break;
                    }
                    if (n3 == 2 || n3 == 3) {
                        string = "guard_alvah_q0171_05.htm";
                        break;
                    }
                    if (n3 == 4) {
                        string = "guard_alvah_q0171_06.htm";
                        break;
                    }
                    if (n3 == 5 && (questState.getQuestItemsCount(4241) == 0L || questState.getQuestItemsCount(4242) == 0L || questState.getQuestItemsCount(4243) == 0L || questState.getQuestItemsCount(4244) == 0L)) {
                        string = "guard_alvah_q0171_08.htm";
                        break;
                    }
                    if (n3 == 5 && questState.getQuestItemsCount(4241) >= 1L && questState.getQuestItemsCount(4242) >= 1L && questState.getQuestItemsCount(4243) >= 1L && questState.getQuestItemsCount(4244) >= 1L) {
                        questState.takeItems(4241, questState.getQuestItemsCount(4241));
                        questState.takeItems(4242, questState.getQuestItemsCount(4242));
                        questState.takeItems(4243, questState.getQuestItemsCount(4243));
                        questState.takeItems(4244, questState.getQuestItemsCount(4244));
                        questState.setCond(6);
                        questState.playSound("ItemSound.quest_middle");
                        string = "guard_alvah_q0171_09.htm";
                        break;
                    }
                    if (n3 == 6 && (questState.getQuestItemsCount(4245) == 0L || questState.getQuestItemsCount(4246) == 0L)) {
                        string = "guard_alvah_q0171_10.htm";
                        break;
                    }
                    if (n3 == 6 && questState.getQuestItemsCount(4245) >= 1L && questState.getQuestItemsCount(4246) >= 1L) {
                        string = "guard_alvah_q0171_11.htm";
                        break;
                    }
                    if (n3 == 7) {
                        string = "guard_alvah_q0171_13.htm";
                        break;
                    }
                    if (n3 == 8) {
                        string = "guard_alvah_q0171_14.htm";
                        break;
                    }
                    if (n3 == 9) {
                        string = "guard_alvah_q0171_15.htm";
                        break;
                    }
                    if (n3 == 10) {
                        string = "guard_alvah_q0171_16.htm";
                        break;
                    }
                    if (n3 != 11) break;
                    questState.giveItems(57, 95000L);
                    questState.playSound("ItemSound.quest_finish");
                    string = "guard_alvah_q0171_17.htm";
                    questState.exitCurrentQuest(false);
                    break;
                }
                if (n == 30420) {
                    if (n3 == 2 && questState.getQuestItemsCount(4239) < 20L) {
                        string = "tweety_q0171_01.htm";
                        break;
                    }
                    if (n3 == 2 && questState.getQuestItemsCount(4239) >= 20L) {
                        questState.giveItems(4240, 1L);
                        questState.takeItems(4239, questState.getQuestItemsCount(4239));
                        questState.setCond(3);
                        questState.playSound("ItemSound.quest_middle");
                        string = "tweety_q0171_02.htm";
                        break;
                    }
                    if (n3 == 3) {
                        string = "tweety_q0171_03.htm";
                        break;
                    }
                    if (n3 < 4) break;
                    string = "tweety_q0171_04.htm";
                    break;
                }
                if (n == 30207) {
                    if (n3 == 1) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                        string = "trader_arodin_q0171_01.htm";
                        break;
                    }
                    if (n3 == 2 && questState.getQuestItemsCount(4239) < 20L) {
                        string = "trader_arodin_q0171_02.htm";
                        break;
                    }
                    if (n3 == 2 && questState.getQuestItemsCount(4239) >= 20L) {
                        string = "trader_arodin_q0171_03.htm";
                        break;
                    }
                    if (n3 == 3) {
                        questState.takeItems(4240, questState.getQuestItemsCount(4240));
                        questState.setCond(4);
                        questState.playSound("ItemSound.quest_middle");
                        string = "trader_arodin_q0171_04.htm";
                        break;
                    }
                    if (n3 < 4) break;
                    string = "trader_arodin_q0171_05.htm";
                    break;
                }
                if (n == 30437) {
                    if (n3 == 8) {
                        string = "trader_rolento_q0171_02.htm";
                        break;
                    }
                    if (n3 == 9) {
                        string = "trader_rolento_q0171_05.htm";
                        break;
                    }
                    if (n3 < 10) break;
                    string = "trader_rolento_q0171_06.htm";
                    break;
                }
                if (n == 30425) {
                    if (n3 == 7) {
                        questState.setCond(8);
                        questState.playSound("ItemSound.quest_middle");
                        string = "neti_q0171_01.htm";
                        break;
                    }
                    if (n3 == 8) {
                        string = "neti_q0171_02.htm";
                        break;
                    }
                    if (n3 < 9) break;
                    string = "neti_q0171_03.htm";
                    break;
                }
                if (n != 30617) break;
                if (n3 < 9) {
                    string = "turek_chief_burai_q0171_01.htm";
                    break;
                }
                if (n3 == 9) {
                    string = "turek_chief_burai_q0171_02.htm";
                    break;
                }
                if (n3 == 10 && questState.getQuestItemsCount(4249) < 30L) {
                    string = "turek_chief_burai_q0171_06.htm";
                    break;
                }
                if (n3 == 10 && questState.getQuestItemsCount(4249) >= 30L) {
                    questState.giveItems(57, 8000L);
                    questState.takeItems(4249, questState.getQuestItemsCount(4249));
                    questState.setCond(11);
                    questState.playSound("ItemSound.quest_middle");
                    string = "turek_chief_burai_q0171_07.htm";
                    break;
                }
                if (n3 != 11) break;
                string = "turek_chief_burai_q0171_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20496 && n2 == 2 && questState.getQuestItemsCount(4239) < 20L) {
            NpcInstance npcInstance2;
            if (Rnd.get(100) < 53) {
                questState.rollAndGive(4239, 1, 100.0);
                if (questState.getQuestItemsCount(4239) >= 19L) {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
            if (questState.getQuestItemsCount(4239) == 5L) {
                npcInstance2 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance2, "NpcString.17151", new Object[0]);
                if (npcInstance2 != null) {
                    npcInstance2.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
            if (questState.getQuestItemsCount(4239) >= 10L && Rnd.get(100) <= 24) {
                npcInstance2 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance2, "NpcString.17151", new Object[0]);
                if (npcInstance2 != null) {
                    npcInstance2.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
        } else if (n == 20497 && n2 == 2 && questState.getQuestItemsCount(4239) < 20L) {
            NpcInstance npcInstance3;
            if (Rnd.get(100) < 55) {
                questState.rollAndGive(4239, 1, 100.0);
                if (questState.getQuestItemsCount(4239) >= 19L) {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
            if (questState.getQuestItemsCount(4239) == 5L) {
                npcInstance3 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance3, "NpcString.17151", new Object[0]);
                if (npcInstance3 != null) {
                    npcInstance3.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
            if (questState.getQuestItemsCount(4239) >= 10L && Rnd.get(100) <= 24) {
                npcInstance3 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance3, "NpcString.17151", new Object[0]);
                if (npcInstance3 != null) {
                    npcInstance3.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
        } else if (n == 20498 && n2 == 2 && questState.getQuestItemsCount(4239) < 20L) {
            NpcInstance npcInstance4;
            if (Rnd.get(100) < 51) {
                questState.rollAndGive(4239, 1, 100.0);
                if (questState.getQuestItemsCount(4239) >= 19L) {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
            if (questState.getQuestItemsCount(4239) == 5L) {
                npcInstance4 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance4, "NpcString.17151", new Object[0]);
                if (npcInstance4 != null) {
                    npcInstance4.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
            if (questState.getQuestItemsCount(4239) >= 10L && Rnd.get(100) <= 24) {
                npcInstance4 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance4, "NpcString.17151", new Object[0]);
                if (npcInstance4 != null) {
                    npcInstance4.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
        } else if (n == 20499 && n2 == 2 && questState.getQuestItemsCount(4239) < 20L) {
            NpcInstance npcInstance5;
            if (Rnd.get(2) == 1) {
                questState.rollAndGive(4239, 1, 100.0);
                if (questState.getQuestItemsCount(4239) >= 19L) {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
            if (questState.getQuestItemsCount(4239) == 5L) {
                npcInstance5 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance5, "NpcString.17151", new Object[0]);
                if (npcInstance5 != null) {
                    npcInstance5.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
            if (questState.getQuestItemsCount(4239) >= 10L && Rnd.get(100) <= 24) {
                npcInstance5 = questState.addSpawn(27190, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ(), 200000);
                Functions.npcSayCustomMessage(npcInstance5, "NpcString.17151", new Object[0]);
                if (npcInstance5 != null) {
                    npcInstance5.getAggroList().addDamageHate(questState.getPlayer(), 0, 2000);
                }
            }
        } else if (n == 20062 && n2 == 5) {
            if (questState.getQuestItemsCount(4241) == 0L) {
                questState.rollAndGive(4241, 1, 100.0);
            } else if (questState.getQuestItemsCount(4241) >= 1L && questState.getQuestItemsCount(4242) == 0L) {
                if (Rnd.get(100) <= 19) {
                    questState.rollAndGive(4242, 1, 100.0);
                }
            } else if (questState.getQuestItemsCount(4241) >= 1L && questState.getQuestItemsCount(4242) >= 1L && questState.getQuestItemsCount(4243) == 0L) {
                if (Rnd.get(100) <= 19) {
                    questState.rollAndGive(4243, 1, 100.0);
                }
            } else if (questState.getQuestItemsCount(4241) >= 1L && questState.getQuestItemsCount(4242) >= 1L && questState.getQuestItemsCount(4243) >= 1L && questState.getQuestItemsCount(4244) == 0L && Rnd.get(100) <= 19) {
                questState.rollAndGive(4244, 1, 100.0);
            }
        } else if (n == 20438 && n2 == 6) {
            if (Rnd.get(100) <= 9) {
                if (questState.getQuestItemsCount(4245) == 0L) {
                    questState.rollAndGive(4245, 1, 100.0);
                }
                if (questState.getQuestItemsCount(4246) == 0L) {
                    questState.rollAndGive(4246, 1, 100.0);
                }
            }
        } else if (n == 20066 && n2 == 10 && questState.getQuestItemsCount(4249) < 30L && Rnd.get(100) <= 49) {
            questState.rollAndGive(4249, 1, 100.0);
            if (questState.getQuestItemsCount(4249) >= 29L) {
                questState.playSound("ItemSound.quest_middle");
            }
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

