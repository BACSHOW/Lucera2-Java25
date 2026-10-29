/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _164_BloodFiend
extends Quest
implements ScriptFile {
    private static final int aHy = 30149;
    private static final int aHU = 1044;
    private static final int aHV = 27021;

    public _164_BloodFiend() {
        super(0);
        this.addStartNpc(30149);
        this.addKillId(27021);
        this.addAttackId(27021);
        this.addQuestItem(1044);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "cel_q0318_04.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        Player player = questState.getPlayer();
        switch (n) {
            case 1: {
                if (player.getRace() == Race.darkelf) {
                    string = "cel_q0318_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 21) {
                    string = "cel_q0318_03.htm";
                    break;
                }
                string = "cel_q0318_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(1044) < 1L) {
                    string = "cel_q0318_05.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1044) < 1L) break;
                string = "cel_q0318_06.htm";
                questState.giveItems(57, 42130L);
                questState.takeItems(1044, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(1044) == 0L && questState.getCond() == 1) {
            Functions.npcSayCustomMessage(npcInstance, "NpcString.16405", new Object[0]);
            questState.giveItems(1044, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        }
        return null;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("kirunak");
        if (n == 0) {
            Functions.npcSayCustomMessage(npcInstance, "NpcString.16404", new Object[0]);
            questState.set("kirunak", "1");
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

