/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _296_SilkOfTarantula
extends Quest
implements ScriptFile {
    private static final int beQ = 30519;
    private static final int beR = 30548;
    private static final int beS = 20394;
    private static final int beT = 20403;
    private static final int beU = 20508;
    private static final int beV = 1493;
    private static final int beW = 1494;
    private static final int beX = 1508;
    private static final int beY = 1509;

    public _296_SilkOfTarantula() {
        super(0);
        this.addStartNpc(30519);
        this.addTalkId(30548);
        this.addKillId(20394, 20403, 20508);
        this.addQuestItem(1493, 1494);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30519) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "trader_mion_q0296_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                questState.takeItems(1494, -1L);
                string2 = "trader_mion_q0296_06.htm";
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "trader_mion_q0296_07.htm";
            }
        } else if (n == 30548 && string.equalsIgnoreCase("reply_3")) {
            if (questState.getQuestItemsCount(1494) >= 1L) {
                string2 = "defender_nathan_q0296_03.htm";
                questState.giveItems(1493, (long)(15 + Rnd.get(9)) * questState.getQuestItemsCount(1494));
                questState.takeItems(1494, -1L);
            } else {
                string2 = "defender_nathan_q0296_02.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        Player player = questState.getPlayer();
        if (player == null) {
            return null;
        }
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        switch (n2) {
            case 1: {
                if (n != 30519) break;
                if (player.getLevel() < 15) {
                    string = "trader_mion_q0296_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getQuestItemsCount(1508) > 0L || questState.getQuestItemsCount(1509) > 0L) {
                    string = "trader_mion_q0296_02.htm";
                    break;
                }
                string = "trader_mion_q0296_08.htm";
                break;
            }
            case 2: {
                if (n == 30519) {
                    if (questState.getQuestItemsCount(1493) < 1L) {
                        string = "trader_mion_q0296_04.htm";
                    } else if (questState.getQuestItemsCount(1493) >= 1L) {
                        string = "trader_mion_q0296_05.htm";
                        if (questState.getQuestItemsCount(1493) >= 10L) {
                            questState.giveItems(57, questState.getQuestItemsCount(1493) * 30L + 2000L);
                        } else {
                            questState.giveItems(57, questState.getQuestItemsCount(1493) * 30L);
                        }
                        questState.takeItems(1493, -1L);
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                    }
                }
                if (n != 30548) break;
                string = "defender_nathan_q0296_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20394 || n == 20403 || n == 20508) {
            int n2 = Rnd.get(100);
            if (n2 > 95) {
                questState.rollAndGive(1494, 1, 100.0);
            } else if (n2 > 45) {
                questState.rollAndGive(1493, 1, 100.0);
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

