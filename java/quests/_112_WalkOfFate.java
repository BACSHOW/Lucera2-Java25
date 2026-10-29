/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _112_WalkOfFate
extends Quest
implements ScriptFile {
    private static final int aEK = 30572;
    private static final int aEL = 32017;
    private static final int aEM = 956;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _112_WalkOfFate() {
        super(0);
        this.addStartNpc(30572);
        this.addTalkId(32017);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("karuda_q0112_0201.htm")) {
            questState.giveItems(57, 4665L, true);
            questState.giveItems(956, 1L, false);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        } else if (string.equalsIgnoreCase("seer_livina_q0112_0104.htm")) {
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
        int n2 = questState.getCond();
        if (n == 30572) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 20) {
                    string = "seer_livina_q0112_0101.htm";
                } else {
                    string = "seer_livina_q0112_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "seer_livina_q0112_0105.htm";
            }
        } else if (n == 32017 && n2 == 1) {
            string = "karuda_q0112_0101.htm";
        }
        return string;
    }
}

