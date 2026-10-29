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

public class _206_DwarfTutorial
extends Quest
implements ScriptFile {
    private static final int aJA = 30528;
    private static final int aJB = 30530;
    private static final int aJi = 18342;
    private static final int aJC = 1498;
    private static final int aJk = 6353;
    private static final int aya = 5789;
    private static final int ayb = 5790;
    private static final int aJn = 0x100000;

    public _206_DwarfTutorial() {
        super(0);
        this.addTalkId(30528, 30530);
        this.addFirstTalkId(30528, 30530);
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
                questState.playTutorialVoice("tutorial_voice_009a");
                questState2.set("tutorial_quest_ex", String.valueOf(1), true);
            }
            if (n == 3) {
                questState.playTutorialVoice("tutorial_voice_010f");
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
        if (string.equalsIgnoreCase("reply_31") && questState.getQuestItemsCount(1498) > 0L) {
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
            string2 = "foreman_laferon002.htm";
            questState.takeItems(1498, 1L);
            questState.startQuestTimer("timer_grand_master", 60000L);
            if (n <= 3) {
                questState2.set("tutorial_quest_ex", String.valueOf(4), true);
            }
        } else if (string.equalsIgnoreCase("reply_42")) {
            string2 = "foreman_laferon006.htm";
            ThreadPoolManager.getInstance().schedule(new RadarTask(115632, -177996, -905, questState.getPlayer()), 200L);
        }
        return string2;
    }

    @Override
    public String onFirstTalk(NpcInstance npcInstance, Player player) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        QuestState questState = player.getQuestState(_255_Tutorial.class);
        if (questState == null) {
            return string;
        }
        QuestState questState2 = player.getQuestState(_206_DwarfTutorial.class);
        if (questState2 == null) {
            this.newQuestState(player, 2);
            questState2 = player.getQuestState(_206_DwarfTutorial.class);
        }
        int n2 = questState.getInt("tutorial_quest_ex");
        int n3 = questState.getInt("tutorial_quest");
        switch (n) {
            case 30530: {
                int n4 = n3 & 0x7FFFFF00;
                if (n2 < 0) {
                    if (player.getRace() == Race.dwarf) {
                        questState2.startQuestTimer("timer_newbie_helper", 30000L);
                        string = "carl001.htm";
                        questState.set("tutorial_quest_ex", "0");
                        questState.onTutorialClientEvent(n4 | 0x100000);
                        break;
                    }
                    string = "carl006.htm";
                    break;
                }
                if ((n2 == 1 || n2 == 2 || n2 == 0) && questState2.getQuestItemsCount(6353) < 1L) {
                    string = "carl002.htm";
                    break;
                }
                if ((n2 == 1 || n2 == 2 || n2 == 0) && questState2.getQuestItemsCount(6353) > 0L) {
                    string = "miner_mai003.htm";
                    questState2.takeItems(6353, -1L);
                    questState.set("tutorial_quest_ex", String.valueOf(3), true);
                    questState2.giveItems(1498, 1L);
                    questState2.startQuestTimer("timer_newbie_helper", 30000L);
                    questState.set("tutorial_quest", String.valueOf(n4 | 4), true);
                    if (questState2.getQuestItemsCount(5789) > 0L) break;
                    questState2.giveItems(5789, 200L);
                    questState2.playTutorialVoice("tutorial_voice_026");
                    break;
                }
                if (n2 == 3) {
                    string = "miner_mai004.htm";
                    break;
                }
                if (n2 <= 3) break;
                string = "carl005.htm";
                break;
            }
            case 30528: {
                if (questState2.getQuestItemsCount(1498) > 0L) {
                    string = "foreman_laferon001.htm";
                    break;
                }
                if (questState2.getQuestItemsCount(1498) == 0L && n2 > 3) {
                    string = "foreman_laferon004.htm";
                    break;
                }
                if (questState2.getQuestItemsCount(1498) != 0L || n2 > 3) break;
                string = "foreman_laferon003.htm";
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

