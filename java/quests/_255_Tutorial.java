/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnItemPickupListener;
import l2.gameserver.listener.actor.player.OnLevelUpListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.network.l2.s2c.TutorialCloseHtml;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;

public class _255_Tutorial
extends Quest
implements ScriptFile {
    private static final int aJk = 6353;
    private static final int bcv = 5588;
    private static final int aJl = 0;
    private static final int aJr = 10;
    private static final int bcw = 18;
    private static final int bcx = 25;
    private static final int bcy = 31;
    private static final int bcz = 38;
    private static final int bcA = 44;
    private static final int aJm = 49;
    private static final int bcB = 53;
    private static final int bcC = 1;
    private static final int bcD = 4;
    private static final int bcE = 7;
    private static final int bcF = 11;
    private static final int bcG = 15;
    private static final int bcH = 19;
    private static final int bcI = 22;
    private static final int bcJ = 26;
    private static final int bcK = 29;
    private static final int bcL = 45;
    private static final int bcM = 47;
    private static final int bcN = 50;
    private static final int bcO = 54;
    private static final int bcP = 56;
    private static final int bcQ = 32;
    private static final int bcR = 35;
    private static final int bcS = 39;
    private static final int bcT = 42;
    private static final int bcU = 1;
    private static final int bcV = 2;
    private static final int bcW = 8;
    private static final int bcX = 256;
    private static final int bcY = 512;
    private static final int bcZ = 1024;
    private static final int bda = 0x8000000;
    private static final int bdb = 2048;
    private static final int bdc = 0x10000000;
    private static final int bdd = 0x20000000;
    private static final int bde = 0x40000000;
    private static final int bdf = 0x4000000;
    private static final int bdg = 4096;
    private static final int bdh = 0x1000000;
    private static final int bdi = 0x200000;
    private static final int aJn = 0x100000;
    private static final int bdj = 0x800000;
    private static TutorialShowListener bdk;

    public _255_Tutorial() {
        super(0);
        CharListenerList.addGlobal(new OnPlayerEnterListenerImpl());
        CharListenerList.addGlobal(new OnItemPickupListenerImpl());
        CharListenerList.addGlobal(new OnLevelUpListenerImpl());
        CharListenerList.addGlobal(new OnDeathListenerImpl());
        bdk = new TutorialShowListener();
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = "";
        int n = questState.getPlayer().getClassId().getId();
        int n2 = questState.getInt("tutorial_quest");
        int n3 = questState.getInt("tutorial_quest_ex");
        int n4 = questState.getPlayer().getLevel();
        if (string.startsWith("UC")) {
            if (n4 < 6) {
                switch (n2) {
                    case 0: {
                        questState.startQuestTimer("QT", 10000L);
                        questState.set("tutorial_quest", String.valueOf(1 | n2 & 0xFF6FFFFF));
                        questState.set("tutorial_quest_ex", "-2");
                        break;
                    }
                    case 1: {
                        questState.showQuestionMark(1);
                        questState.playTutorialVoice("tutorial_voice_006");
                        questState.playSound("ItemSound.quest_tutorial");
                        questState.showQuestionMark(1);
                        break;
                    }
                    case 2: {
                        questState.showQuestionMark(2);
                        questState.playSound("ItemSound.quest_tutorial");
                        break;
                    }
                    case 3: {
                        int n5 = 0;
                        if (questState.getQuestItemsCount(6353) == 1L) {
                            n5 = 3;
                        }
                        if (n3 == 2) {
                            n5 = 1;
                            break;
                        }
                        if (n5 == 1) {
                            questState.showQuestionMark(3);
                            questState.playSound("ItemSound.quest_tutorial");
                            break;
                        }
                        if (n5 == 2) {
                            questState.showQuestionMark(4);
                            questState.playSound("ItemSound.quest_tutorial");
                            break;
                        }
                        if (n5 != 3) break;
                        questState.showQuestionMark(5);
                        questState.playSound("ItemSound.quest_tutorial");
                        break;
                    }
                    case 4: {
                        questState.showQuestionMark(12);
                        questState.playSound("ItemSound.quest_tutorial");
                    }
                }
            }
        } else if (string.startsWith("QT")) {
            if (n3 == -2) {
                if (n == 0) {
                    questState.playTutorialVoice("tutorial_voice_001a");
                    questState.showTutorialHTML("tutorial_human_fighter001.htm");
                } else if (n == 10) {
                    questState.playTutorialVoice("tutorial_voice_001b");
                    questState.showTutorialHTML("tutorial_human_mage001.htm");
                } else if (n == 18) {
                    questState.playTutorialVoice("tutorial_voice_001c");
                    questState.showTutorialHTML("tutorial_elven_fighter001.htm");
                } else if (n == 25) {
                    questState.playTutorialVoice("tutorial_voice_001d");
                    questState.showTutorialHTML("tutorial_elven_mage001.htm");
                } else if (n == 31) {
                    questState.playTutorialVoice("tutorial_voice_001e");
                    questState.showTutorialHTML("tutorial_delf_fighter001.htm");
                } else if (n == 38) {
                    questState.playTutorialVoice("tutorial_voice_001f");
                    questState.showTutorialHTML("tutorial_delf_mage001.htm");
                } else if (n == 44) {
                    questState.playTutorialVoice("tutorial_voice_001g");
                    questState.showTutorialHTML("tutorial_orc_fighter001.htm");
                } else if (n == 49) {
                    questState.playTutorialVoice("tutorial_voice_001h");
                    questState.showTutorialHTML("tutorial_orc_mage001.htm");
                } else if (n == 53) {
                    questState.playTutorialVoice("tutorial_voice_001i");
                    questState.showTutorialHTML("tutorial_dwarven_fighter001.htm");
                }
                if (questState.getQuestItemsCount(5588) == 0L) {
                    questState.giveItems(5588, 1L);
                }
                questState.cancelQuestTimer("QT");
                questState.startQuestTimer("QT", 30000L);
                questState.set("tutorial_quest_ex", "-3");
            } else if (n3 == -3) {
                questState.playTutorialVoice("tutorial_voice_002");
            } else if (n3 == -4) {
                questState.playTutorialVoice("tutorial_voice_008");
                questState.set("tutorial_quest_ex", "-5");
            }
        } else if (string.startsWith("tutorial_close_")) {
            int n6 = 0;
            if (!string.equalsIgnoreCase("tutorial_close_")) {
                n6 = Integer.valueOf(string.substring(15));
            }
            switch (n6) {
                case 0: {
                    questState.getPlayer().sendPacket((IStaticPacket)TutorialCloseHtml.STATIC);
                    break;
                }
                case 1: {
                    questState.getPlayer().sendPacket((IStaticPacket)TutorialCloseHtml.STATIC);
                    questState.playTutorialVoice("tutorial_voice_006");
                    questState.showQuestionMark(1);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.startQuestTimer("QT", 30000L);
                    if (n3 >= 0) break;
                    questState.set("tutorial_quest_ex", "-4");
                    break;
                }
                case 2: {
                    questState.playTutorialVoice("tutorial_voice_003");
                    questState.showTutorialHTML("tutorial_02.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0 | 1);
                    if (n3 >= 0) break;
                    questState.set("tutorial_quest_ex", "-5");
                    break;
                }
                case 3: {
                    questState.showTutorialHTML("tutorial_03.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0 | 2);
                    break;
                }
                case 4: {
                    questState.showTutorialHTML("tutorial_04.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0 | 4);
                    break;
                }
                case 5: {
                    questState.showTutorialHTML("tutorial_05.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0 | 8);
                    break;
                }
                case 6: {
                    questState.showTutorialHTML("tutorial_06.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0 | 0x10);
                    break;
                }
                case 7: {
                    questState.showTutorialHTML("tutorial_100.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0);
                    break;
                }
                case 8: {
                    questState.showTutorialHTML("tutorial_101.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0);
                    break;
                }
                case 9: {
                    questState.showTutorialHTML("tutorial_102.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0);
                    break;
                }
                case 10: {
                    questState.showTutorialHTML("tutorial_103.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0);
                    break;
                }
                case 11: {
                    questState.showTutorialHTML("tutorial_104.htm");
                    questState.onTutorialClientEvent(n2 & 0x7FFFFFF0);
                    break;
                }
                case 12: {
                    questState.getPlayer().sendPacket((IStaticPacket)TutorialCloseHtml.STATIC);
                }
            }
        } else if (string.startsWith("CE")) {
            int n7 = Integer.valueOf(string.substring(2));
            int n8 = n2;
            int n9 = n8 & 0x7FFFFFF0;
            switch (n7) {
                case 1: {
                    if (n4 >= 6) break;
                    questState.playTutorialVoice("tutorial_voice_004");
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.showTutorialHTML("tutorial_03.htm");
                    questState.onTutorialClientEvent(n9 | 2);
                    break;
                }
                case 2: {
                    if (n4 >= 6) break;
                    questState.playTutorialVoice("tutorial_voice_005");
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.showTutorialHTML("tutorial_05.htm");
                    questState.onTutorialClientEvent(n9 | 8);
                    break;
                }
                case 8: {
                    if (n4 >= 6) break;
                    if (n == 0) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-71424, 258336, -3109, questState.getPlayer()), 500L);
                    } else if (n == 10) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-91036, 248044, -3568, questState.getPlayer()), 500L);
                    } else if (n == 18 || n == 25) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46112, 41200, -3504, questState.getPlayer()), 500L);
                    } else if (n == 31 || n == 38) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28384, 11056, -4233, questState.getPlayer()), 500L);
                    } else if (n == 44 || n == 49) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-56736, -113680, -672, questState.getPlayer()), 500L);
                    } else if (n == 53) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(108567, -173994, -406, questState.getPlayer()), 500L);
                    }
                    questState.showTutorialHTML("tutorial_human_fighter007.htm");
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.playTutorialVoice("tutorial_voice_007");
                    questState.set("tutorial_quest", String.valueOf(n9 | 2));
                    if (n3 >= 0) break;
                    questState.set("tutorial_quest_ex", "-5");
                    break;
                }
                case 256: {
                    if (n4 >= 6) break;
                    questState.playTutorialVoice("tutorial_voice_017");
                    questState.showQuestionMark(10);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFFFFFEFF));
                    questState.onTutorialClientEvent(n9 & 0xFFFFFEFF | 0x800000);
                    break;
                }
                case 512: {
                    questState.playTutorialVoice("tutorial_voice_016");
                    questState.showQuestionMark(8);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFFFFFDFF));
                    break;
                }
                case 1024: {
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFFFFFBFF));
                    if (n == 0) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83020, 242553, -3718, questState.getPlayer()), 500L);
                    } else if (n == 18) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45061, 52468, -2796, questState.getPlayer()), 500L);
                    } else if (n == 31) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10447, 14620, -4242, questState.getPlayer()), 500L);
                    } else if (n == 44) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46389, -113905, -21, questState.getPlayer()), 500L);
                    } else if (n == 53) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115271, -182692, -1445, questState.getPlayer()), 500L);
                    }
                    if (!questState.getPlayer().getClassId().isMage() || n == 49) {
                        questState.playTutorialVoice("tutorial_voice_014");
                        questState.showQuestionMark(9);
                        questState.playSound("ItemSound.quest_tutorial");
                    }
                    questState.onTutorialClientEvent(n9 | 0x8000000);
                    break;
                }
                case 0x8000000: {
                    questState.showQuestionMark(24);
                    questState.playTutorialVoice("tutorial_voice_020");
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.onTutorialClientEvent(n9 & 0xF7FFFFFF);
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xF7FFFFFF));
                    questState.onTutorialClientEvent(n9 | 0x800);
                    break;
                }
                case 2048: {
                    if (questState.getPlayer().getClassId().isMage()) {
                        questState.playTutorialVoice("tutorial_voice_019");
                        questState.showQuestionMark(11);
                        questState.playSound("ItemSound.quest_tutorial");
                        if (n == 10) {
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-84981, 244764, -3726, questState.getPlayer()), 500L);
                        } else if (n == 25) {
                            ThreadPoolManager.getInstance().schedule(new RadarTask(45701, 52459, -2796, questState.getPlayer()), 500L);
                        } else if (n == 38) {
                            ThreadPoolManager.getInstance().schedule(new RadarTask(10344, 14445, -4242, questState.getPlayer()), 500L);
                        } else if (n == 49) {
                            ThreadPoolManager.getInstance().schedule(new RadarTask(-46225, -113312, -21, questState.getPlayer()), 500L);
                        }
                        questState.set("tutorial_quest", String.valueOf(n8 & 0xFFFFF7FF));
                    }
                    questState.onTutorialClientEvent(n9 | 0x10000000);
                    break;
                }
                case 0x10000000: {
                    if (n == 0) {
                        questState.playTutorialVoice("tutorial_voice_021");
                        questState.showQuestionMark(25);
                        questState.playSound("ItemSound.quest_tutorial");
                        questState.set("tutorial_quest", String.valueOf(n8 & 0xEFFFFFFF));
                    }
                    questState.onTutorialClientEvent(n9 | 0x20000000);
                    break;
                }
                case 0x20000000: {
                    if (n == 53 || n == 10 || n == 18 || n == 25 || n == 38 || n == 31) {
                        questState.playTutorialVoice("tutorial_voice_021");
                        questState.showQuestionMark(25);
                        questState.playSound("ItemSound.quest_tutorial");
                        questState.set("tutorial_quest", String.valueOf(n8 & 0xDFFFFFFF));
                    } else {
                        questState.playTutorialVoice("tutorial_voice_030");
                        questState.showQuestionMark(27);
                        questState.playSound("ItemSound.quest_tutorial");
                        questState.set("tutorial_quest", String.valueOf(n8 & 0xDFFFFFFF));
                    }
                    questState.onTutorialClientEvent(n9 | 0x40000000);
                    break;
                }
                case 0x40000000: {
                    if (n != 44 && n != 49) break;
                    questState.playTutorialVoice("tutorial_voice_021");
                    questState.showQuestionMark(25);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xBFFFFFFF));
                    break;
                }
                case 0x4000000: {
                    questState.showQuestionMark(17);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFBFFFFFF));
                    questState.onTutorialClientEvent(n9 | 0x1000);
                    break;
                }
                case 4096: {
                    questState.showQuestionMark(13);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFFFFEFFF));
                    questState.onTutorialClientEvent(n9 | 0x1000000);
                    break;
                }
                case 0x1000000: {
                    questState.playTutorialVoice("tutorial_voice_023");
                    questState.showQuestionMark(15);
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFEFFFFFF));
                    questState.onTutorialClientEvent(n9 | 0x1000000);
                    break;
                }
                case 0x200000: {
                    if (n4 >= 6) break;
                    questState.showQuestionMark(23);
                    questState.playTutorialVoice("tutorial_voice_012");
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFFDFFFFF));
                    break;
                }
                case 0x100000: {
                    if (n4 >= 6) break;
                    questState.showQuestionMark(5);
                    questState.playTutorialVoice("tutorial_voice_013");
                    questState.playSound("ItemSound.quest_tutorial");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFFEFFFFF));
                    break;
                }
                case 0x800000: {
                    if (n4 >= 6) break;
                    questState.playTutorialVoice("tutorial_voice_018");
                    questState.showTutorialHTML("tutorial_21z.htm");
                    questState.set("tutorial_quest", String.valueOf(n8 & 0xFF7FFFFF));
                }
            }
        } else if (string.startsWith("QM")) {
            int n10 = n2 & 0x7FFFFF00;
            int n11 = Integer.valueOf(string.substring(2));
            switch (n11) {
                case 1: {
                    questState.playTutorialVoice("tutorial_voice_007");
                    if (n3 < 0) {
                        questState.set("tutorial_quest_ex", "-5");
                    }
                    if (n == 0) {
                        questState.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-71424, 258336, -3109, questState.getPlayer()), 500L);
                    } else if (n == 10) {
                        questState.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-91036, 248044, -3568, questState.getPlayer()), 500L);
                    } else if (n == 18 || n == 25) {
                        questState.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46112, 41200, -3504, questState.getPlayer()), 500L);
                    } else if (n == 31 || n == 38) {
                        questState.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28384, 11056, -4233, questState.getPlayer()), 500L);
                    } else if (n == 44 || n == 49) {
                        questState.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-56736, -113680, -672, questState.getPlayer()), 500L);
                    } else if (n == 53) {
                        questState.showTutorialHTML("tutorial_human_fighter007.htm");
                        ThreadPoolManager.getInstance().schedule(new RadarTask(108567, -173994, -406, questState.getPlayer()), 500L);
                    }
                    questState.set("tutorial_quest", String.valueOf(n10 | 2));
                    break;
                }
                case 2: {
                    if (n == 0) {
                        questState.showTutorialHTML("tutorial_human_fighter008.htm");
                        break;
                    }
                    if (n == 10) {
                        questState.showTutorialHTML("tutorial_human_mage008.htm");
                        break;
                    }
                    if (n == 18 || n == 25) {
                        questState.showTutorialHTML("tutorial_elf008.htm");
                        break;
                    }
                    if (n == 31 || n == 38) {
                        questState.showTutorialHTML("tutorial_delf008.htm");
                        break;
                    }
                    if (n == 44 || n == 49) {
                        questState.showTutorialHTML("tutorial_orc008.htm");
                        break;
                    }
                    if (n != 53) break;
                    questState.showTutorialHTML("tutorial_dwarven_fighter008.htm");
                    break;
                }
                case 3: {
                    questState.showTutorialHTML("tutorial_09.htm");
                    questState.onTutorialClientEvent(n10 | 0x100000);
                    break;
                }
                case 4: {
                    questState.showTutorialHTML("tutorial_10.htm");
                    break;
                }
                case 5: {
                    if (n == 0) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-71424, 258336, -3109, questState.getPlayer()), 500L);
                    } else if (n == 10) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-91036, 248044, -3568, questState.getPlayer()), 500L);
                    } else if (n == 18 || n == 25) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46112, 41200, -3504, questState.getPlayer()), 500L);
                    } else if (n == 31 || n == 38) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28384, 11056, -4233, questState.getPlayer()), 500L);
                    } else if (n == 44 || n == 49) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-56736, -113680, -672, questState.getPlayer()), 500L);
                    } else if (n == 53) {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(108567, -173994, -406, questState.getPlayer()), 500L);
                    }
                    questState.showTutorialHTML("tutorial_11.htm");
                    break;
                }
                case 7: {
                    questState.showTutorialHTML("tutorial_15.htm");
                    questState.set("tutorial_quest", String.valueOf(n10 | 5));
                    break;
                }
                case 8: {
                    questState.showTutorialHTML("tutorial_18.htm");
                    break;
                }
                case 9: {
                    if ((questState.getPlayer().getRace() == Race.human || questState.getPlayer().getRace() == Race.elf || questState.getPlayer().getRace() == Race.darkelf) && questState.getPlayer().getClassId().level() == 0) {
                        questState.showTutorialHTML("tutorial_fighter017.htm");
                    }
                    if (questState.getPlayer().getRace() == Race.dwarf && questState.getPlayer().getClassId().level() == 0) {
                        questState.showTutorialHTML("tutorial_fighter_dwarf017.htm");
                    }
                    if (questState.getPlayer().getRace() != Race.orc || questState.getPlayer().getClassId().level() != 0) break;
                    questState.showTutorialHTML("tutorial_fighter_orc017.htm");
                    break;
                }
                case 10: {
                    questState.showTutorialHTML("tutorial_19.htm");
                    break;
                }
                case 11: {
                    if (questState.getPlayer().getRace() == Race.human) {
                        questState.showTutorialHTML("tutorial_mage020.htm");
                    }
                    if (questState.getPlayer().getRace() == Race.elf || questState.getPlayer().getRace() == Race.darkelf) {
                        questState.showTutorialHTML("tutorial_mage_elf020.htm");
                    }
                    if (questState.getPlayer().getRace() != Race.orc) break;
                    questState.showTutorialHTML("tutorial_mage_orc020.htm");
                    break;
                }
                case 12: {
                    questState.showTutorialHTML("tutorial_15.htm");
                    break;
                }
                case 13: {
                    if (n == 0) {
                        questState.showTutorialHTML("tutorial_21.htm");
                        break;
                    }
                    if (n == 10) {
                        questState.showTutorialHTML("tutorial_21a.htm");
                        break;
                    }
                    if (n == 18) {
                        questState.showTutorialHTML("tutorial_21b.htm");
                        break;
                    }
                    if (n == 25) {
                        questState.showTutorialHTML("tutorial_21c.htm");
                        break;
                    }
                    if (n == 44) {
                        questState.showTutorialHTML("tutorial_21d.htm");
                        break;
                    }
                    if (n == 49) {
                        questState.showTutorialHTML("tutorial_21e.htm");
                        break;
                    }
                    if (n == 53) {
                        questState.showTutorialHTML("tutorial_21f.htm");
                        break;
                    }
                    if (n == 31) {
                        questState.showTutorialHTML("tutorial_21g.htm");
                        break;
                    }
                    if (n != 38) break;
                    questState.showTutorialHTML("tutorial_21h.htm");
                    break;
                }
                case 15: {
                    questState.showTutorialHTML("tutorial_28.htm");
                    break;
                }
                case 16: {
                    questState.showTutorialHTML("tutorial_30.htm");
                    break;
                }
                case 17: {
                    questState.showTutorialHTML("tutorial_27.htm");
                    break;
                }
                case 19: {
                    questState.showTutorialHTML("tutorial_07.htm");
                    break;
                }
                case 20: {
                    questState.showTutorialHTML("tutorial_14.htm");
                    break;
                }
                case 21: {
                    questState.showTutorialHTML("tutorial_newbie001.htm");
                    break;
                }
                case 22: {
                    questState.showTutorialHTML("tutorial_14.htm");
                    break;
                }
                case 23: {
                    questState.showTutorialHTML("tutorial_24.htm");
                    break;
                }
                case 24: {
                    if (questState.getPlayer().getRace() == Race.human) {
                        questState.showTutorialHTML("tutorial_newbie003a.htm");
                    }
                    if (questState.getPlayer().getRace() == Race.elf) {
                        questState.showTutorialHTML("tutorial_newbie003b.htm");
                    }
                    if (questState.getPlayer().getRace() == Race.darkelf) {
                        questState.showTutorialHTML("tutorial_newbie003c.htm");
                    }
                    if (questState.getPlayer().getRace() == Race.orc) {
                        questState.showTutorialHTML("tutorial_newbie003d.htm");
                    }
                    if (questState.getPlayer().getRace() != Race.dwarf) break;
                    questState.showTutorialHTML("tutorial_newbie003e.htm");
                    break;
                }
                case 25: {
                    if (n == 0) {
                        questState.showTutorialHTML("tutorial_newbie002a.htm");
                    }
                    if (n == 10) {
                        questState.showTutorialHTML("tutorial_newbie002b.htm");
                    }
                    if (n == 18 || n == 25) {
                        questState.showTutorialHTML("tutorial_newbie002c.htm");
                    }
                    if (n == 38) {
                        questState.showTutorialHTML("tutorial_newbie002d.htm");
                    }
                    if (n == 31) {
                        questState.showTutorialHTML("tutorial_newbie002e.htm");
                    }
                    if (n == 53) {
                        questState.showTutorialHTML("tutorial_newbie002g.htm");
                    }
                    if (n != 49 && n != 44) break;
                    questState.showTutorialHTML("tutorial_newbie002f.htm");
                    break;
                }
                case 26: {
                    if (questState.getPlayer().getClassId().level() == 0 || n == 49) {
                        questState.showTutorialHTML("tutorial_newbie004a.htm");
                        break;
                    }
                    questState.showTutorialHTML("tutorial_newbie004b.htm");
                    break;
                }
                case 27: {
                    if (n != 0 && n != 49 && n != 44) break;
                    questState.showTutorialHTML("tutorial_newbie002h.htm");
                }
            }
        } else if (string.equalsIgnoreCase("reply_1")) {
            questState.showTutorialHTML("tutorial_22g.htm");
        } else if (string.equalsIgnoreCase("reply_2")) {
            questState.showTutorialHTML("tutorial_22w.htm");
        } else if (string.equalsIgnoreCase("reply_3")) {
            questState.showTutorialHTML("tutorial_22ap.htm");
        } else if (string.equalsIgnoreCase("reply_4")) {
            questState.showTutorialHTML("tutorial_22ad.htm");
        } else if (string.equalsIgnoreCase("reply_5")) {
            questState.showTutorialHTML("tutorial_22bt.htm");
        } else if (string.equalsIgnoreCase("reply_6")) {
            questState.showTutorialHTML("tutorial_22bh.htm");
        } else if (string.equalsIgnoreCase("reply_7")) {
            questState.showTutorialHTML("tutorial_22cs.htm");
        } else if (string.equalsIgnoreCase("reply_8")) {
            questState.showTutorialHTML("tutorial_22cn.htm");
        } else if (string.equalsIgnoreCase("reply_9")) {
            questState.showTutorialHTML("tutorial_22cw.htm");
        } else if (string.equalsIgnoreCase("reply_10")) {
            questState.showTutorialHTML("tutorial_22db.htm");
        } else if (string.equalsIgnoreCase("reply_11")) {
            questState.showTutorialHTML("tutorial_22dp.htm");
        } else if (string.equalsIgnoreCase("reply_12")) {
            questState.showTutorialHTML("tutorial_22et.htm");
        } else if (string.equalsIgnoreCase("reply_13")) {
            questState.showTutorialHTML("tutorial_22es.htm");
        } else if (string.equalsIgnoreCase("reply_14")) {
            questState.showTutorialHTML("tutorial_22fp.htm");
        } else if (string.equalsIgnoreCase("reply_15")) {
            questState.showTutorialHTML("tutorial_22fs.htm");
        } else if (string.equalsIgnoreCase("reply_16")) {
            questState.showTutorialHTML("tutorial_22gs.htm");
        } else if (string.equalsIgnoreCase("reply_17")) {
            questState.showTutorialHTML("tutorial_22ge.htm");
        } else if (string.equalsIgnoreCase("reply_18")) {
            questState.showTutorialHTML("tutorial_22ko.htm");
        } else if (string.equalsIgnoreCase("reply_19")) {
            questState.showTutorialHTML("tutorial_22kw.htm");
        } else if (string.equalsIgnoreCase("reply_20")) {
            questState.showTutorialHTML("tutorial_22ns.htm");
        } else if (string.equalsIgnoreCase("reply_21")) {
            questState.showTutorialHTML("tutorial_22nb.htm");
        } else if (string.equalsIgnoreCase("reply_22")) {
            questState.showTutorialHTML("tutorial_22oa.htm");
        } else if (string.equalsIgnoreCase("reply_23")) {
            questState.showTutorialHTML("tutorial_22op.htm");
        } else if (string.equalsIgnoreCase("reply_24")) {
            questState.showTutorialHTML("tutorial_22ps.htm");
        } else if (string.equalsIgnoreCase("reply_24")) {
            questState.showTutorialHTML("tutorial_22pp.htm");
        } else if (string.equalsIgnoreCase("reply_26")) {
            if (n == 1) {
                questState.showTutorialHTML("tutorial_22.htm");
            } else if (n == 4) {
                questState.showTutorialHTML("tutorial_22ad.htm");
            } else if (n == 7) {
                questState.showTutorialHTML("tutorial_22b.htm");
            } else if (n == 11) {
                questState.showTutorialHTML("tutorial_22c.htm");
            } else if (n == 15) {
                questState.showTutorialHTML("tutorial_22d.htm");
            } else if (n == 19) {
                questState.showTutorialHTML("tutorial_22e.htm");
            } else if (n == 22) {
                questState.showTutorialHTML("tutorial_22f.htm");
            } else if (n == 26) {
                questState.showTutorialHTML("tutorial_22g.htm");
            } else if (n == 29) {
                questState.showTutorialHTML("tutorial_22h.htm");
            } else if (n == 45) {
                questState.showTutorialHTML("tutorial_22i.htm");
            } else if (n == 47) {
                questState.showTutorialHTML("tutorial_22j.htm");
            } else if (n == 50) {
                questState.showTutorialHTML("tutorial_22k.htm");
            } else if (n == 54) {
                questState.showTutorialHTML("tutorial_22l.htm");
            } else if (n == 56) {
                questState.showTutorialHTML("tutorial_22m.htm");
            } else if (n == 32) {
                questState.showTutorialHTML("tutorial_22n.htm");
            } else if (n == 35) {
                questState.showTutorialHTML("tutorial_22o.htm");
            } else if (n == 39) {
                questState.showTutorialHTML("tutorial_22p.htm");
            } else if (n == 42) {
                questState.showTutorialHTML("tutorial_22q.htm");
            } else {
                questState.showTutorialHTML("tutorial_22qe.htm");
            }
        } else if (string.equalsIgnoreCase("reply_27")) {
            questState.showTutorialHTML("tutorial_29.htm");
        } else if (string.equalsIgnoreCase("reply_28")) {
            questState.showTutorialHTML("tutorial_28.htm");
        } else if (string.equalsIgnoreCase("reply_29")) {
            questState.showTutorialHTML("tutorial_07a.htm");
        } else if (string.equalsIgnoreCase("reply_30")) {
            questState.showTutorialHTML("tutorial_07b.htm");
        } else if (string.equalsIgnoreCase("reply_32")) {
            questState.showTutorialHTML("tutorial_22qa.htm");
        } else if (string.equalsIgnoreCase("reply_34")) {
            questState.showTutorialHTML("tutorial_22qd.htm");
        }
        if (string2.isEmpty()) {
            return null;
        }
        questState.showTutorialHTML(string2);
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
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

    public class OnDeathListenerImpl
    implements OnDeathListener {
        final Quest q = QuestManager.getQuest(_255_Tutorial.class);

        @Override
        public void onDeath(Creature creature, Creature creature2) {
            if (creature.getPlayer() != null && creature.getPlayer().getLevel() < 9) {
                creature.getPlayer().processQuestEvent(this.q.getName(), "CE512", null);
            }
        }
    }

    private class RadarTask
    extends RunnableImpl {
        private final int x;
        private final int y;
        private final int z;
        private final Player Nr;

        RadarTask(int n, int n2, int n3, Player player) {
            this.x = n;
            this.y = n2;
            this.z = n3;
            this.Nr = player;
        }

        @Override
        public void runImpl() throws Exception {
            this.Nr.sendPacket((IStaticPacket)new RadarControl(0, 1, this.x, this.y, this.z));
        }
    }

    public static class OnItemPickupListenerImpl
    implements OnItemPickupListener {
        @Override
        public void onItemPickup(Player player, ItemInstance itemInstance) {
            Quest quest = QuestManager.getQuest(_255_Tutorial.class);
            if (itemInstance.getItemId() == 6353 || itemInstance.getItemId() == 57) {
                if (itemInstance.getItemId() == 57 && !player.getVarB("QUEST_STATE_OF_255_ADENA_GIVEN")) {
                    player.processQuestEvent(quest.getName(), "CE2097152", null);
                    player.setVar("QUEST_STATE_OF_255_ADENA_GIVEN", "true", -1L);
                }
                if (itemInstance.getItemId() == 6353 && !player.getVarB("QUEST_STATE_OF_255_BLUE_GEM_GIVEN")) {
                    player.processQuestEvent(quest.getName(), "CE1048576", null);
                    player.setVar("QUEST_STATE_OF_255_BLUE_GEM_GIVEN", "true", -1L);
                }
            }
        }
    }

    public static class TutorialShowListener
    implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(Creature creature, double d, Creature creature2, Skill skill) {
            Quest quest = QuestManager.getQuest(_255_Tutorial.class);
            Player player = creature.getPlayer();
            if (player.getCurrentHpPercents() < 25.0) {
                player.removeListener(bdk);
                player.processQuestEvent(quest.getName(), "CE256", null);
            } else if (player.getLevel() > 5) {
                player.removeListener(bdk);
            }
        }
    }

    public static class OnLevelUpListenerImpl
    implements OnLevelUpListener {
        @Override
        public void onLevelUp(Player player, int n) {
            QuestState questState = player.getQuestState(_255_Tutorial.class);
            Quest quest = QuestManager.getQuest(_255_Tutorial.class);
            if (questState == null) {
                return;
            }
            switch (n) {
                case 5: {
                    player.processQuestEvent(quest.getName(), "CE1024", null);
                    break;
                }
                case 6: {
                    player.processQuestEvent(quest.getName(), "CE134217728", null);
                    break;
                }
                case 7: {
                    player.processQuestEvent(quest.getName(), "CE2048", null);
                    break;
                }
                case 9: {
                    player.processQuestEvent(quest.getName(), "CE268435456", null);
                    break;
                }
                case 10: {
                    player.processQuestEvent(quest.getName(), "CE536870912", null);
                    break;
                }
                case 12: {
                    player.processQuestEvent(quest.getName(), "CE1073741824", null);
                    break;
                }
                case 15: {
                    player.processQuestEvent(quest.getName(), "CE67108864", null);
                    break;
                }
                case 20: {
                    player.processQuestEvent(quest.getName(), "CE4096", null);
                    break;
                }
                case 35: {
                    player.processQuestEvent(quest.getName(), "CE16777216", null);
                }
            }
        }
    }

    public static class OnPlayerEnterListenerImpl
    implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            Quest quest;
            if (ArrayUtils.contains(Config.ALT_INITIAL_QUESTS, 255) && (quest = QuestManager.getQuest(_255_Tutorial.class)) != null) {
                if (player.getQuestState(_255_Tutorial.class) == null) {
                    quest.newQuestState(player, 2);
                    player.processQuestEvent(quest.getName(), "UC", null);
                } else {
                    player.processQuestEvent(quest.getName(), "UC", null);
                }
                if (player.getLevel() < 6) {
                    player.addListener(bdk);
                }
            }
        }
    }
}

