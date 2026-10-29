/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _169_OffspringOfNightmares
extends Quest
implements ScriptFile {
    private static final int aIy = 30145;
    private static final int aIz = 1030;
    private static final int aIA = 1031;
    private static final int aIB = 31;
    private static final int aIC = 20105;
    private static final int aID = 20025;

    public _169_OffspringOfNightmares() {
        super(0);
        this.addStartNpc(30145);
        this.addKillId(20105, 20025);
        this.addQuestItem(1030, 1031);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "vlasti_q0326_04.htm";
        } else if (string.equalsIgnoreCase("reply_1") && questState.getQuestItemsCount(1031) >= 1L) {
            string2 = "vlasti_q0326_08.htm";
            questState.giveItems(31, 1L);
            questState.giveItems(57, 17030L + 10L * questState.getQuestItemsCount(1030));
            questState.takeItems(1030, questState.getQuestItemsCount(1030));
            questState.takeItems(1031, questState.getQuestItemsCount(1031));
            if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q4")) {
                questState.getPlayer().setVar("p1q4", "1", -1L);
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            }
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        Player player = questState.getPlayer();
        int n = questState.getState();
        switch (n) {
            case 1: {
                if (player.getRace() != Race.darkelf) {
                    string = "vlasti_q0326_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 15) {
                    string = "vlasti_q0326_03.htm";
                    break;
                }
                string = "vlasti_q0326_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(1030) >= 1L && questState.getQuestItemsCount(1031) == 0L) {
                    string = "vlasti_q0326_06.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1031) >= 1L) {
                    string = "vlasti_q0326_07.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1030) != 0L || questState.getQuestItemsCount(1031) != 0L) break;
                string = "vlasti_q0326_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20105 || n == 20025 && questState.getCond() == 1) {
            if (Rnd.get(10) > 7 && questState.getQuestItemsCount(1031) == 0L) {
                questState.rollAndGive(1031, 1, 100.0);
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            }
            if (Rnd.get(10) > 4) {
                questState.rollAndGive(1030, 1, 100.0);
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

