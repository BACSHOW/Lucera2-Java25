/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _275_BlackWingedSpies
extends Quest
implements ScriptFile {
    private static int aXM = 30567;
    private static int bel = 20316;
    private static int bem = 27043;
    private static int ben = 1478;
    private static int beo = 1479;
    private static int bep = 10;

    public _275_BlackWingedSpies() {
        super(0);
        this.addStartNpc(aXM);
        this.addKillId(bel);
        this.addKillId(bem);
        this.addQuestItem(ben);
        this.addQuestItem(beo);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("neruga_chief_tantus_q0275_03.htm") && questState.getState() == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (npcInstance.getNpcId() != aXM) {
            return "noquest";
        }
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getRace() != Race.orc) {
                questState.exitCurrentQuest(true);
                return "neruga_chief_tantus_q0275_00.htm";
            }
            if (questState.getPlayer().getLevel() < 11) {
                questState.exitCurrentQuest(true);
                return "neruga_chief_tantus_q0275_01.htm";
            }
            questState.setCond(0);
            return "neruga_chief_tantus_q0275_02.htm";
        }
        if (n != 2) {
            return "noquest";
        }
        int n2 = questState.getCond();
        if (questState.getQuestItemsCount(ben) < 70L) {
            if (n2 != 1) {
                questState.setCond(1);
            }
            return "neruga_chief_tantus_q0275_04.htm";
        }
        if (n2 == 2) {
            questState.giveItems(57, 4550L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            return "neruga_chief_tantus_q0275_05.htm";
        }
        return "noquest";
    }

    private static void g(QuestState questState) {
        if (questState.getQuestItemsCount(beo) > 0L) {
            questState.takeItems(beo, -1L);
        }
        questState.giveItems(beo, 1L);
        questState.addSpawn(bem);
    }

    public static void give_Darkwing_Bat_Fang(QuestState questState, long l) {
        long l2 = 70L - questState.getQuestItemsCount(ben);
        if (l2 < 1L) {
            return;
        }
        if (l > l2) {
            l = l2;
        }
        questState.giveItems(ben, l);
        questState.playSound(l == l2 ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
        if (l == l2) {
            questState.setCond(2);
        }
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = npcInstance.getNpcId();
        long l = questState.getQuestItemsCount(ben);
        if (n == bel && l < 70L) {
            if (l > 10L && l < 65L && Rnd.chance(bep)) {
                _275_BlackWingedSpies.g(questState);
                return null;
            }
            _275_BlackWingedSpies.give_Darkwing_Bat_Fang(questState, 1L);
        } else if (n == bem && l < 70L && questState.getQuestItemsCount(beo) > 0L) {
            questState.takeItems(beo, -1L);
            _275_BlackWingedSpies.give_Darkwing_Bat_Fang(questState, 5L);
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

