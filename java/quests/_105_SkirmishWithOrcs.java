/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _105_SkirmishWithOrcs
extends Quest
implements ScriptFile {
    private static final int aDu = 30218;
    private static final int aDv = 1836;
    private static final int aDw = 1837;
    private static final int aDx = 1838;
    private static final int aDy = 1839;
    private static final int aDz = 1840;
    private static final int aDA = 1841;
    private static final int aDB = 1842;
    private static final int aDC = 1843;
    private static final int aDD = 1844;
    private static final int aDE = 1845;
    private static final int aDF = 981;
    private static final int aDG = 754;
    private static final int aDH = 27059;
    private static final int aDI = 27060;
    private static final int aDJ = 27061;
    private static final int aDK = 27062;
    private static final int aDL = 27064;
    private static final int aDM = 27065;
    private static final int aDN = 27067;
    private static final int aDO = 27068;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _105_SkirmishWithOrcs() {
        super(0);
        this.addStartNpc(30218);
        this.addKillId(27059);
        this.addKillId(27060);
        this.addKillId(27061);
        this.addKillId(27062);
        this.addKillId(27064);
        this.addKillId(27065);
        this.addKillId(27067);
        this.addKillId(27068);
        this.addQuestItem(1836, 1837, 1838, 1839, 1840, 1841, 1842, 1843, 1844, 1845);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("sentinel_kendnell_q0105_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            if (questState.getQuestItemsCount(1836) + questState.getQuestItemsCount(1837) + questState.getQuestItemsCount(1838) + questState.getQuestItemsCount(1839) == 0L) {
                int n = Rnd.get(4);
                if (n == 0) {
                    questState.giveItems(1836, 1L, false);
                } else if (n == 1) {
                    questState.giveItems(1837, 1L, false);
                } else if (n == 2) {
                    questState.giveItems(1838, 1L, false);
                } else {
                    questState.giveItems(1839, 1L, false);
                }
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getRace() != Race.elf) {
                string = "sentinel_kendnell_q0105_00.htm";
                questState.exitCurrentQuest(true);
            } else if (questState.getPlayer().getLevel() < 10) {
                string = "sentinel_kendnell_q0105_10.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "sentinel_kendnell_q0105_02.htm";
            }
        } else if (n == 1 && questState.getQuestItemsCount(1836) + questState.getQuestItemsCount(1837) + questState.getQuestItemsCount(1838) + questState.getQuestItemsCount(1839) != 0L) {
            string = "sentinel_kendnell_q0105_05.htm";
        } else if (n == 2 && questState.getQuestItemsCount(1844) != 0L) {
            string = "sentinel_kendnell_q0105_06.htm";
            questState.takeItems(1836, -1L);
            questState.takeItems(1837, -1L);
            questState.takeItems(1838, -1L);
            questState.takeItems(1839, -1L);
            questState.takeItems(1844, 1L);
            int n2 = Rnd.get(4);
            if (n2 == 0) {
                questState.giveItems(1840, 1L, false);
            } else if (n2 == 1) {
                questState.giveItems(1841, 1L, false);
            } else if (n2 == 2) {
                questState.giveItems(1842, 1L, false);
            } else {
                questState.giveItems(1843, 1L, false);
            }
            questState.setCond(3);
            questState.setState(2);
        } else if (n == 3 && questState.getQuestItemsCount(1840) + questState.getQuestItemsCount(1841) + questState.getQuestItemsCount(1842) + questState.getQuestItemsCount(1843) == 1L) {
            string = "sentinel_kendnell_q0105_07.htm";
        } else if (n == 4 && questState.getQuestItemsCount(1845) > 0L) {
            string = "sentinel_kendnell_q0105_08.htm";
            questState.takeItems(1840, -1L);
            questState.takeItems(1841, -1L);
            questState.takeItems(1842, -1L);
            questState.takeItems(1843, -1L);
            questState.takeItems(1845, -1L);
            if (questState.getPlayer().getClassId().isMage()) {
                questState.giveItems(754, 1L, false);
            } else {
                questState.giveItems(981, 1L, false);
            }
            if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q3")) {
                questState.getPlayer().setVar("p1q3", "1", -1L);
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                questState.giveItems(1060, 100L);
                for (int i = 4412; i <= 4417; ++i) {
                    questState.giveItems(i, 10L);
                }
                if (questState.getPlayer().getClassId().isMage()) {
                    questState.playTutorialVoice("tutorial_voice_027");
                    questState.giveItems(5790, 3000L);
                    questState.giveItems(2509, 500L);
                } else {
                    questState.playTutorialVoice("tutorial_voice_026");
                    questState.giveItems(5789, 7000L);
                    questState.giveItems(1835, 1000L);
                }
            }
            questState.exitCurrentQuest(false);
            questState.playSound("ItemSound.quest_finish");
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n2 == 1 && questState.getQuestItemsCount(1844) == 0L) {
            if (n == 27059 && questState.getQuestItemsCount(1836) > 0L) {
                questState.giveItems(1844, 1L, false);
            } else if (n == 27060 && questState.getQuestItemsCount(1837) > 0L) {
                questState.giveItems(1844, 1L, false);
            } else if (n == 27061 && questState.getQuestItemsCount(1838) > 0L) {
                questState.giveItems(1844, 1L, false);
            } else if (n == 27062 && questState.getQuestItemsCount(1839) > 0L) {
                questState.giveItems(1844, 1L, false);
            }
            if (questState.getQuestItemsCount(1844) > 0L) {
                questState.setCond(2);
                questState.setState(2);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (n2 == 3 && questState.getQuestItemsCount(1845) == 0L) {
            if (n == 27064 && questState.getQuestItemsCount(1840) > 0L) {
                questState.giveItems(1845, 1L, false);
            } else if (n == 27065 && questState.getQuestItemsCount(1841) > 0L) {
                questState.giveItems(1845, 1L, false);
            } else if (n == 27067 && questState.getQuestItemsCount(1842) > 0L) {
                questState.giveItems(1845, 1L, false);
            } else if (n == 27068 && questState.getQuestItemsCount(1843) > 0L) {
                questState.giveItems(1845, 1L, false);
            }
            if (questState.getQuestItemsCount(1845) > 0L) {
                questState.setCond(4);
                questState.setState(2);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        return null;
    }
}

