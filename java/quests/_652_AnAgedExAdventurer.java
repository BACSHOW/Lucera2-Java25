/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _652_AnAgedExAdventurer
extends Quest
implements ScriptFile {
    private static final int bUa = 32012;
    private static final int bUb = 30180;
    private static final int bUc = 1464;
    private static final int bUd = 956;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _652_AnAgedExAdventurer() {
        super(0);
        this.addStartNpc(32012);
        this.addTalkId(30180);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("retired_oldman_tantan_q0652_03.htm") && questState.getQuestItemsCount(1464) >= 100L) {
            questState.setCond(1);
            questState.setState(2);
            questState.takeItems(1464, 100L);
            questState.playSound("ItemSound.quest_accept");
            string2 = "retired_oldman_tantan_q0652_04.htm";
        } else {
            string2 = "retired_oldman_tantan_q0652_03.htm";
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_giveup");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 32012) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() < 46) {
                    string = "retired_oldman_tantan_q0652_01a.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "retired_oldman_tantan_q0652_01.htm";
                }
            }
        } else if (n == 30180 && n2 == 1) {
            string = "sara_q0652_01.htm";
            questState.giveItems(57, 5026L, true);
            if (Rnd.chance(50)) {
                questState.giveItems(956, 1L, false);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }
}

