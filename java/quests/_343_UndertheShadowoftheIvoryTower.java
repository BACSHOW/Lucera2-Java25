/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _343_UndertheShadowoftheIvoryTower
extends Quest
implements ScriptFile {
    public final int CEMA = 30834;
    public final int ICARUS = 30835;
    public final int MARSHA = 30934;
    public final int TRUMPIN = 30935;
    public final int[] MOBS = new int[]{20563, 20564, 20565, 20566};
    public final int ORB = 4364;
    public final int ECTOPLASM = 4365;
    public final int[] AllowClass = new int[]{11, 12, 13, 14, 26, 27, 28, 39, 40, 41};
    public final int CHANCE = 50;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _343_UndertheShadowoftheIvoryTower() {
        super(0);
        this.addStartNpc(30834);
        this.addTalkId(30834);
        this.addTalkId(30835);
        this.addTalkId(30934);
        this.addTalkId(30935);
        for (int n : this.MOBS) {
            this.addKillId(n);
        }
        this.addQuestItem(4364);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = Rnd.get(3);
        int n2 = Rnd.get(2);
        long l = questState.getQuestItemsCount(4364);
        if (string.equalsIgnoreCase("30834-03.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30834-08.htm")) {
            if (l > 0L) {
                questState.giveItems(57, l * 120L);
                questState.takeItems(4364, -1L);
            } else {
                string2 = "30834-08.htm";
            }
        } else if (string.equalsIgnoreCase("30834-09.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("30934-02.htm") || string.equalsIgnoreCase("30934-03.htm")) {
            if (l < 10L) {
                string2 = "noorbs.htm";
            } else if (string.equalsIgnoreCase("30934-03.htm")) {
                if (l >= 10L) {
                    questState.takeItems(4364, 10L);
                    questState.set("playing", "1");
                } else {
                    string2 = "noorbs.htm";
                }
            }
        } else if (string.equalsIgnoreCase("30934-04.htm")) {
            if (questState.getInt("playing") > 0) {
                if (n == 0) {
                    string2 = "30934-05.htm";
                    questState.giveItems(4364, 10L);
                } else if (n == 1) {
                    string2 = "30934-06.htm";
                } else {
                    string2 = "30934-04.htm";
                    questState.giveItems(4364, 20L);
                }
                questState.unset("playing");
            } else {
                string2 = "Player is cheating";
                questState.takeItems(4364, -1L);
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("30934-05.htm")) {
            if (questState.getInt("playing") > 0) {
                if (n == 0) {
                    string2 = "30934-04.htm";
                    questState.giveItems(4364, 20L);
                } else if (n == 1) {
                    string2 = "30934-05.htm";
                    questState.giveItems(4364, 10L);
                } else {
                    string2 = "30934-06.htm";
                }
                questState.unset("playing");
            } else {
                string2 = "Player is cheating";
                questState.takeItems(4364, -1L);
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("30934-06.htm")) {
            if (questState.getInt("playing") > 0) {
                if (n == 0) {
                    string2 = "30934-04.htm";
                    questState.giveItems(4364, 20L);
                } else if (n == 1) {
                    string2 = "30934-06.htm";
                } else {
                    string2 = "30934-05.htm";
                    questState.giveItems(4364, 10L);
                }
                questState.unset("playing");
            } else {
                string2 = "Player is cheating";
                questState.takeItems(4364, -1L);
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("30935-02.htm") || string.equalsIgnoreCase("30935-03.htm")) {
            questState.unset("toss");
            if (l < 10L) {
                string2 = "noorbs.htm";
            }
        } else if (string.equalsIgnoreCase("30935-05.htm")) {
            if (l >= 10L) {
                if (n2 == 0) {
                    int n3 = questState.getInt("toss");
                    if (n3 == 4) {
                        questState.unset("toss");
                        questState.giveItems(4364, 150L);
                        string2 = "30935-07.htm";
                    } else {
                        questState.set("toss", String.valueOf(n3 + 1));
                        string2 = "30935-04.htm";
                    }
                } else {
                    questState.unset("toss");
                    questState.takeItems(4364, 10L);
                }
            } else {
                string2 = "noorbs.htm";
            }
        } else if (string.equalsIgnoreCase("30935-06.htm")) {
            if (l >= 10L) {
                int n4 = questState.getInt("toss");
                questState.unset("toss");
                if (n4 == 1) {
                    questState.giveItems(4364, 10L);
                } else if (n4 == 2) {
                    questState.giveItems(4364, 30L);
                } else if (n4 == 3) {
                    questState.giveItems(4364, 70L);
                } else if (n4 == 4) {
                    questState.giveItems(4364, 150L);
                }
            } else {
                string2 = "noorbs.htm";
            }
        } else if (string.equalsIgnoreCase("30835-02.htm")) {
            if (questState.getQuestItemsCount(4365) > 0L) {
                questState.takeItems(4365, 1L);
                int n5 = Rnd.get(1000);
                if (n5 <= 119) {
                    questState.giveItems(955, 1L);
                } else if (n5 <= 169) {
                    questState.giveItems(951, 1L);
                } else if (n5 <= 329) {
                    questState.giveItems(2511, Rnd.get(200) + 401);
                } else if (n5 <= 559) {
                    questState.giveItems(2510, Rnd.get(200) + 401);
                } else if (n5 <= 561) {
                    questState.giveItems(316, 1L);
                } else if (n5 <= 578) {
                    questState.giveItems(630, 1L);
                } else if (n5 <= 579) {
                    questState.giveItems(188, 1L);
                } else if (n5 <= 581) {
                    questState.giveItems(885, 1L);
                } else if (n5 <= 582) {
                    questState.giveItems(103, 1L);
                } else if (n5 <= 584) {
                    questState.giveItems(917, 1L);
                } else {
                    questState.giveItems(736, 1L);
                }
            } else {
                string2 = "30835-03.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n == 30834) {
            if (n2 != 2) {
                for (int n3 : this.AllowClass) {
                    if (questState.getPlayer().getClassId().getId() != n3 || questState.getPlayer().getLevel() < 40) continue;
                    string = "30834-01.htm";
                }
                if (!string.equals("30834-01.htm")) {
                    string = "30834-07.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = questState.getQuestItemsCount(4364) > 0L ? "30834-06.htm" : "30834-05.htm";
            }
        } else if (n == 30835) {
            string = "30835-01.htm";
        } else if (n == 30934) {
            string = "30934-01.htm";
        } else if (n == 30935) {
            string = "30935-01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (Rnd.chance(50)) {
            questState.giveItems(4364, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

