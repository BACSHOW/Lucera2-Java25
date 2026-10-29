/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.scripts.ScriptFile;
import quests._255_Tutorial;

public class _202_HmageTutorial
extends Quest
implements ScriptFile {
    private static final int aJo = 30017;
    private static final int aJp = 30019;
    private static final int aJi = 18342;
    private static final int aJq = 1068;
    private static final int aJk = 6353;
    private static final int ayb = 5790;
    private static final int aya = 5789;
    private static final int aJr = 10;
    private static final int aJn = 0x100000;

    public _202_HmageTutorial() {
        super(0);
        this.addTalkId(30017, 30019);
        this.addFirstTalkId(30017, 30019);
        this.addKillId(18342);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        QuestState questState2 = questState.getPlayer().getQuestState(_255_Tutorial.class);
        if (questState2 == null || questState == null) {
            return null;
        }
        Player player = questState.getPlayer();
        if (player == null) {
            return null;
        }
        int n = questState2.getInt("tutorial_quest_ex");
        if (string.equalsIgnoreCase("timer_newbie_helper")) {
            if (n == 0) {
                questState.playTutorialVoice("tutorial_voice_009b");
                questState2.set("tutorial_quest_ex", String.valueOf(1), true);
            }
            if (n == 3) {
                questState.playTutorialVoice("tutorial_voice_010b");
            }
            return null;
        }
        if (string.equalsIgnoreCase("timer_grand_master")) {
            if (n >= 4) {
                questState.showQuestionMark(7);
                questState.playSound("ItemSound.quest_tutorial");
                questState.playTutorialVoice("tutorial_voice_025");
            }
            return null;
        }
        if (string.equalsIgnoreCase("reply_31") && questState.getQuestItemsCount(1068) > 0L) {
            if (!player.getClassId().isMage() && questState.getQuestItemsCount(5789) <= 200L) {
                questState.giveItems(5789, 200L);
                questState.playTutorialVoice("tutorial_voice_026");
                questState.addExpAndSp(0L, 50L);
            }
            if (player.getClassId().isMage() && questState.getQuestItemsCount(5789) <= 200L && questState.getQuestItemsCount(5790) <= 100L) {
                if (questState.getPlayer().getActiveClassId() == 49) {
                    questState.giveItems(5789, 200L);
                    questState.playTutorialVoice("tutorial_voice_026");
                } else {
                    questState.giveItems(5790, 100L);
                    questState.playTutorialVoice("tutorial_voice_027");
                }
                questState.addExpAndSp(0L, 50L);
            }
            string2 = "gallin002.htm";
            questState.takeItems(1068, -1L);
            questState.startQuestTimer("timer_grand_master", 60000L);
            if (n <= 3) {
                questState2.set("tutorial_quest_ex", String.valueOf(4), true);
            }
        } else if (string.equalsIgnoreCase("reply_42")) {
            string2 = "gallin006.htm";
            ThreadPoolManager.getInstance().schedule(new RadarTask(-84081, 243277, -3723, questState.getPlayer()), 200L);
        }
        return string2;
    }

    @Override
    public String onFirstTalk(NpcInstance npcInstance, Player player) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        boolean bl = player.getClassId().getRace() != Race.orc && player.getClassId().isMage();
        QuestState questState = player.getQuestState(_255_Tutorial.class);
        if (questState == null) {
            return string;
        }
        QuestState questState2 = player.getQuestState(_202_HmageTutorial.class);
        if (questState2 == null) {
            this.newQuestState(player, 2);
            questState2 = player.getQuestState(_202_HmageTutorial.class);
        }
        int n2 = questState2.getPlayer().getClassId().getId();
        int n3 = questState.getInt("tutorial_quest_ex");
        int n4 = questState.getInt("tutorial_quest");
        switch (n) {
            case 30019: {
                int n5 = n4 & 0x7FFFFF00;
                if (n3 < 0) {
                    if (n2 == 10 && player.getRace() == Race.human) {
                        questState2.startQuestTimer("timer_newbie_helper", 30000L);
                        string = "doff001.htm";
                        questState.set("tutorial_quest_ex", "0");
                        questState.onTutorialClientEvent(n5 | 0x100000);
                        break;
                    }
                    string = "doff006.htm";
                    break;
                }
                if ((n3 == 1 || n3 == 2 || n3 == 0) && questState2.getQuestItemsCount(6353) < 1L) {
                    string = "doff002.htm";
                    break;
                }
                if ((n3 == 1 || n3 == 2 || n3 == 0) && questState2.getQuestItemsCount(6353) > 0L) {
                    string = "doff003.htm";
                    questState2.takeItems(6353, -1L);
                    questState.set("tutorial_quest_ex", String.valueOf(3), true);
                    questState2.giveItems(1068, 1L);
                    questState2.startQuestTimer("timer_newbie_helper", 30000L);
                    questState.set("tutorial_quest", String.valueOf(n5 | 4), true);
                    if (!bl || questState2.getQuestItemsCount(5790) > 0L) break;
                    questState2.playTutorialVoice("tutorial_voice_027");
                    questState2.giveItems(5790, 100L);
                    break;
                }
                if (n3 == 3) {
                    string = "doff004.htm";
                    break;
                }
                if (n3 <= 3) break;
                string = "doff005.htm";
                break;
            }
            case 30017: {
                if (questState2.getQuestItemsCount(1068) > 0L) {
                    string = "gallin001.htm";
                    break;
                }
                if (questState2.getQuestItemsCount(1068) == 0L && n3 > 3) {
                    string = "gallin004.htm";
                    break;
                }
                if (questState2.getQuestItemsCount(1068) != 0L || n3 > 3) break;
                string = "gallin003.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        QuestState questState2 = questState.getPlayer().getQuestState(_255_Tutorial.class);
        if (questState2 == null) {
            return null;
        }
        int n = questState2.getInt("tutorial_quest_ex");
        if (!(n != 1 && n != 0 || questState.getPlayer().getVarB("QUEST_STATE_OF_255_TUTORIAL_VOICE_0"))) {
            questState.playTutorialVoice("tutorial_voice_011");
            questState.showQuestionMark(3);
            questState2.set("tutorial_quest", String.valueOf(2), true);
            questState.getPlayer().setVar("QUEST_STATE_OF_255_TUTORIAL_VOICE_0", "true", -1L);
        }
        if ((n == 1 || n == 2 || n == 0) && questState.getQuestItemsCount(6353) < 1L && Rnd.get(2) <= 1) {
            questState.dropItemDelay(npcInstance, 6353, 1L);
            if (!questState.getPlayer().getVarB("QUEST_STATE_OF_255_TUTORIAL_VOICE_1")) {
                questState.playSound("ItemSound.quest_tutorial");
                questState.getPlayer().setVar("QUEST_STATE_OF_255_TUTORIAL_VOICE_1", "true", -1L);
            }
        }
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
}

