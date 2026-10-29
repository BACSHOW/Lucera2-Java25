/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _633_InTheForgottenVillage
extends Quest
implements ScriptFile {
    private static int bQu = 31388;
    private static int bQv = 7544;
    private static int bQw = 7545;
    private static Map<Integer, Double> bQx = new HashMap<Integer, Double>();
    private static Map<Integer, Double> bQy = new HashMap<Integer, Double>();

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _633_InTheForgottenVillage() {
        super(1);
        bQx.put(21557, 32.8);
        bQx.put(21558, 32.8);
        bQx.put(21559, 33.7);
        bQx.put(21560, 33.7);
        bQx.put(21563, 34.2);
        bQx.put(21564, 34.8);
        bQx.put(21565, 35.1);
        bQx.put(21566, 35.9);
        bQx.put(21567, 35.9);
        bQx.put(21572, 36.5);
        bQx.put(21574, 38.3);
        bQx.put(21575, 38.3);
        bQx.put(21580, 38.5);
        bQx.put(21581, 39.5);
        bQx.put(21583, 39.7);
        bQx.put(21584, 40.1);
        bQy.put(21553, 34.7);
        bQy.put(21554, 34.7);
        bQy.put(21561, 45.0);
        bQy.put(21578, 50.1);
        bQy.put(21596, 35.9);
        bQy.put(21597, 37.0);
        bQy.put(21598, 44.1);
        bQy.put(21599, 39.5);
        bQy.put(21600, 40.8);
        bQy.put(21601, 41.1);
        this.addStartNpc(bQu);
        this.addQuestItem(bQv);
        for (int n : bQy.keySet()) {
            this.addKillId(n);
        }
        for (int n : bQx.keySet()) {
            this.addKillId(n);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "day_mina_q0633_0104.htm";
        }
        if (string.equalsIgnoreCase("633_4")) {
            questState.takeItems(bQv, -1L);
            questState.playSound("ItemSound.quest_finish");
            string2 = "day_mina_q0633_0204.htm";
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("633_1")) {
            string2 = "day_mina_q0633_0201.htm";
        } else if (string.equalsIgnoreCase("633_3") && questState.getCond() == 2) {
            if (questState.getQuestItemsCount(bQv) >= 200L) {
                questState.takeItems(bQv, -1L);
                questState.giveItems(57, 25000L);
                questState.addExpAndSp(305235L, 0L);
                questState.playSound("ItemSound.quest_finish");
                questState.setCond(1);
                string2 = "day_mina_q0633_0202.htm";
            } else {
                string2 = "day_mina_q0633_0203.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        int n3 = questState.getState();
        if (n == bQu) {
            if (n3 == 1) {
                if (questState.getPlayer().getLevel() >= 65) {
                    string = "day_mina_q0633_0101.htm";
                } else {
                    string = "day_mina_q0633_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "day_mina_q0633_0106.htm";
            } else if (n2 == 2) {
                string = "day_mina_q0633_0105.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        long l;
        int n = npcInstance.getNpcId();
        if (bQy.containsKey(n)) {
            questState.rollAndGive(bQw, 1, bQy.get(n));
        } else if (bQx.containsKey(n) && (l = questState.getQuestItemsCount(bQv)) < 200L && Rnd.chance(bQx.get(n))) {
            questState.giveItems(bQv, 1L);
            if (l >= 199L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

