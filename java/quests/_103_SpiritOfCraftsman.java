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

public class _103_SpiritOfCraftsman
extends Quest
implements ScriptFile {
    private static final int aDg = 968;
    private static final int aDh = 969;
    private static final int aDi = 970;
    private static final int aDj = 1107;
    private static final int aDk = 971;
    private static final int aDl = 972;
    private static final int aDm = 973;
    private static final int aDn = 974;
    private static final int aDo = 975;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _103_SpiritOfCraftsman() {
        super(0);
        this.addStartNpc(30307);
        this.addTalkId(30132);
        this.addTalkId(30144);
        this.addKillId(20015);
        this.addKillId(20020);
        this.addKillId(20455);
        this.addKillId(20517);
        this.addKillId(20518);
        this.addQuestItem(968, 969, 970, 1107, 971, 972, 973, 974);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("blacksmith_karoyd_q0103_05.htm")) {
            questState.giveItems(968, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            questState.setCond(0);
        }
        if (n == 30307 && questState.getCond() == 0) {
            if (questState.getPlayer().getRace() != Race.darkelf) {
                string = "blacksmith_karoyd_q0103_00.htm";
            } else {
                if (questState.getPlayer().getLevel() >= 10) {
                    string = "blacksmith_karoyd_q0103_03.htm";
                    return string;
                }
                string = "blacksmith_karoyd_q0103_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30307 && questState.getCond() == 0) {
            string = "completed";
        } else if (n2 == 2) {
            if (n == 30307 && questState.getCond() >= 1 && (questState.getQuestItemsCount(968) >= 1L || questState.getQuestItemsCount(969) >= 1L || questState.getQuestItemsCount(970) >= 1L)) {
                string = "blacksmith_karoyd_q0103_06.htm";
            } else if (n == 30132 && questState.getCond() == 1 && questState.getQuestItemsCount(968) == 1L) {
                string = "cecon_q0103_01.htm";
                questState.setCond(2);
                questState.takeItems(968, 1L);
                questState.giveItems(969, 1L);
            } else if (n == 30132 && questState.getCond() >= 2 && (questState.getQuestItemsCount(969) >= 1L || questState.getQuestItemsCount(970) >= 1L)) {
                string = "cecon_q0103_02.htm";
            } else if (n == 30144 && questState.getCond() == 2 && questState.getQuestItemsCount(969) >= 1L) {
                string = "harne_q0103_01.htm";
                questState.setCond(3);
                questState.takeItems(969, 1L);
                questState.giveItems(970, 1L);
            } else if (n == 30144 && questState.getCond() == 3 && questState.getQuestItemsCount(970) >= 1L && questState.getQuestItemsCount(1107) < 10L) {
                string = "harne_q0103_02.htm";
            } else if (n == 30144 && questState.getCond() == 4 && questState.getQuestItemsCount(970) == 1L && questState.getQuestItemsCount(1107) >= 10L) {
                string = "harne_q0103_03.htm";
                questState.setCond(5);
                questState.takeItems(970, 1L);
                questState.takeItems(1107, 10L);
                questState.giveItems(971, 1L);
            } else if (n == 30144 && questState.getCond() == 5 && questState.getQuestItemsCount(971) == 1L) {
                string = "harne_q0103_04.htm";
            } else if (n == 30132 && questState.getCond() == 5 && questState.getQuestItemsCount(971) == 1L) {
                string = "cecon_q0103_03.htm";
                questState.setCond(6);
                questState.takeItems(971, 1L);
                questState.giveItems(972, 1L);
            } else if (n == 30132 && questState.getCond() == 6 && questState.getQuestItemsCount(972) == 1L && questState.getQuestItemsCount(973) == 0L && questState.getQuestItemsCount(974) == 0L) {
                string = "cecon_q0103_04.htm";
            } else if (n == 30132 && questState.getCond() == 7 && questState.getQuestItemsCount(973) == 1L) {
                string = "cecon_q0103_05.htm";
                questState.setCond(8);
                questState.takeItems(973, 1L);
                questState.giveItems(974, 1L);
            } else if (n == 30132 && questState.getCond() == 8 && questState.getQuestItemsCount(974) == 1L) {
                string = "cecon_q0103_06.htm";
            } else if (n == 30307 && questState.getCond() == 8 && questState.getQuestItemsCount(974) == 1L) {
                string = "blacksmith_karoyd_q0103_07.htm";
                questState.takeItems(974, 1L);
                questState.giveItems(975, 1L);
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
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if ((n == 20517 || n == 20518 || n == 20455) && questState.getCond() == 3) {
            if (questState.getQuestItemsCount(970) == 1L && questState.getQuestItemsCount(1107) < 10L && Rnd.chance(33)) {
                questState.giveItems(1107, 1L);
                if (questState.getQuestItemsCount(1107) == 10L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(4);
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if ((n == 20015 || n == 20020) && questState.getCond() == 6 && questState.getQuestItemsCount(972) == 1L && Rnd.chance(33)) {
            questState.giveItems(973, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.takeItems(972, 1L);
            questState.setCond(7);
        }
        return null;
    }
}

