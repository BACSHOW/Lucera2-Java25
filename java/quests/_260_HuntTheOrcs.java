/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import quests._255_Tutorial;

public class _260_HuntTheOrcs
extends Quest
implements ScriptFile {
    private static final int bdy = 30221;
    private static final int ORC_AMULET = 1114;
    private static final int ORC_NECKLACE = 1115;

    public _260_HuntTheOrcs() {
        super(0);
        this.addStartNpc(30221);
        this.addKillId(20468, 20469, 20470, 20471, 20472, 20473);
        this.addQuestItem(1114, 1115);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("sentinel_rayjien_q0260_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("sentinel_rayjien_q0260_06.htm")) {
            questState.setCond(0);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        String string = "no-quest";
        switch (n2) {
            case 1: {
                if (n != 30221) break;
                if (questState.getPlayer().getLevel() >= 6 && questState.getPlayer().getRace() == Race.elf) {
                    string = "sentinel_rayjien_q0260_02.htm";
                    break;
                }
                if (questState.getPlayer().getRace() != Race.elf) {
                    string = "sentinel_rayjien_q0260_00.htm";
                    break;
                }
                if (questState.getPlayer().getLevel() >= 6) break;
                string = "sentinel_rayjien_q0260_01.htm";
                break;
            }
            case 2: {
                if (n != 30221) break;
                if (questState.getQuestItemsCount(1114) == 0L && questState.getQuestItemsCount(1115) == 0L) {
                    string = "sentinel_rayjien_q0260_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1114) <= 0L && questState.getQuestItemsCount(1115) <= 0L) break;
                string = "sentinel_rayjien_q0260_05.htm";
                if (questState.getQuestItemsCount(1114) + questState.getQuestItemsCount(1115) >= 10L) {
                    questState.giveItems(57, questState.getQuestItemsCount(1114) * 12L + questState.getQuestItemsCount(1115) * 30L + 1000L);
                } else {
                    questState.giveItems(57, questState.getQuestItemsCount(1114) * 12L + questState.getQuestItemsCount(1115) * 30L);
                }
                questState.takeAllItems(1114, 1115);
                if (questState.getPlayer().getLevel() >= 25 || questState.getPlayer().getClassId().getLevel() != 1 || questState.getPlayer().getVarB("p1q2")) break;
                questState.getPlayer().setVar("p1q2", "1", -1L);
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n Go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                QuestState questState2 = questState.getPlayer().getQuestState(_255_Tutorial.class);
                if (questState2 == null || questState2.getInt("tutorial_quest_ex") == 10) break;
                questState.showQuestionMark(26);
                questState2.set("tutorial_quest_ex", "10");
                if (questState.getPlayer().getClassId().isMage()) {
                    questState.playTutorialVoice("tutorial_voice_027");
                    questState.giveItems(5790, 3000L);
                    break;
                }
                questState.playTutorialVoice("tutorial_voice_026");
                questState.giveItems(5789, 6000L);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (questState.getCond() > 0) {
            if (n == 20468 || n == 20469 || n == 20470) {
                questState.rollAndGive(1114, 1, 14.0);
            } else if (n == 20471 || n == 20472 || n == 20473) {
                questState.rollAndGive(1115, 1, 14.0);
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

