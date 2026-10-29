/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _382_KailsMagicCoin
extends Quest
implements ScriptFile {
    private static int bxI = 5898;
    private static int bxO = 30687;
    private static final Map<Integer, int[]> bxP = new HashMap<Integer, int[]>();

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _382_KailsMagicCoin() {
        super(0);
        this.addStartNpc(bxO);
        for (int n : bxP.keySet()) {
            this.addKillId(n);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("head_blacksmith_vergara_q0382_03.htm")) {
            if (questState.getPlayer().getLevel() >= 55 && questState.getQuestItemsCount(bxI) > 0L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
            } else {
                string2 = "head_blacksmith_vergara_q0382_01.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("list")) {
            MultiSellHolder.getInstance().SeparateAndSend(382, questState.getPlayer(), 0.0);
            string2 = null;
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (questState.getQuestItemsCount(bxI) == 0L || questState.getPlayer().getLevel() < 55) {
            string = "head_blacksmith_vergara_q0382_01.htm";
            questState.exitCurrentQuest(true);
        } else {
            string = n == 0 ? "head_blacksmith_vergara_q0382_02.htm" : "head_blacksmith_vergara_q0382_04.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2 || questState.getQuestItemsCount(bxI) == 0L) {
            return null;
        }
        int[] nArray = bxP.get(npcInstance.getNpcId());
        questState.rollAndGive(nArray[Rnd.get(nArray.length)], 1, 10.0);
        return null;
    }

    static {
        bxP.put(21017, new int[]{5961});
        bxP.put(21019, new int[]{5962});
        bxP.put(21020, new int[]{5963});
        bxP.put(21022, new int[]{5961, 5962, 5963});
    }
}

