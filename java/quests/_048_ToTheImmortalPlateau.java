/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _048_ToTheImmortalPlateau
extends Quest
implements ScriptFile {
    private static final int aCz = 7563;
    private static final int aCA = 7564;
    private static final int aCB = 7565;
    private static final int aCC = 7568;
    private static final int aCD = 7567;
    private static final int aCE = 7566;
    private static final int aCF = 7570;
    private static final int aCJ = 7557;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _048_ToTheImmortalPlateau() {
        super(0);
        this.addStartNpc(30097);
        this.addTalkId(30097);
        this.addTalkId(30097);
        this.addTalkId(30097);
        this.addTalkId(30094);
        this.addTalkId(30090);
        this.addTalkId(30116);
        this.addQuestItem(7563, 7564, 7565, 7568, 7567, 7566);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(7563, 1L);
            string2 = "galladuchi_q0048_0104.htm";
        } else if (string.equals("2")) {
            questState.setCond(2);
            questState.takeItems(7563, 1L);
            questState.giveItems(7568, 1L);
            string2 = "gentler_q0048_0201.htm";
        } else if (string.equals("3")) {
            questState.setCond(3);
            questState.takeItems(7568, 1L);
            questState.giveItems(7564, 1L);
            string2 = "galladuchi_q0048_0301.htm";
        } else if (string.equals("4")) {
            questState.setCond(4);
            questState.takeItems(7564, 1L);
            questState.giveItems(7567, 1L);
            string2 = "sandra_q0048_0401.htm";
        } else if (string.equals("5")) {
            questState.setCond(5);
            questState.takeItems(7567, 1L);
            questState.giveItems(7565, 1L);
            string2 = "galladuchi_q0048_0501.htm";
        } else if (string.equals("6")) {
            questState.setCond(6);
            questState.takeItems(7565, 1L);
            questState.giveItems(7566, 1L);
            string2 = "dustin_q0048_0601.htm";
        } else if (string.equals("7")) {
            questState.giveItems(7557, 1L);
            questState.takeItems(7566, 1L);
            string2 = "galladuchi_q0048_0701.htm";
            questState.setCond(0);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            if (questState.getPlayer().getRace() != Race.orc || questState.getQuestItemsCount(7570) == 0L) {
                string = "galladuchi_q0048_0102.htm";
                questState.exitCurrentQuest(true);
            } else if (questState.getPlayer().getLevel() < 3) {
                string = "galladuchi_q0048_0103.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "galladuchi_q0048_0101.htm";
            }
        } else if (n == 30097 && questState.getCond() == 1) {
            string = "galladuchi_q0048_0105.htm";
        } else if (n == 30097 && questState.getCond() == 2) {
            string = "galladuchi_q0048_0201.htm";
        } else if (n == 30097 && questState.getCond() == 3) {
            string = "galladuchi_q0048_0303.htm";
        } else if (n == 30097 && questState.getCond() == 4) {
            string = "galladuchi_q0048_0401.htm";
        } else if (n == 30097 && questState.getCond() == 5) {
            string = "galladuchi_q0048_0503.htm";
        } else if (n == 30097 && questState.getCond() == 6) {
            string = "galladuchi_q0048_0601.htm";
        } else if (n == 30094 && questState.getCond() == 1) {
            string = "gentler_q0048_0101.htm";
        } else if (n == 30094 && questState.getCond() == 2) {
            string = "gentler_q0048_0203.htm";
        } else if (n == 30090 && questState.getCond() == 3) {
            string = "sandra_q0048_0301.htm";
        } else if (n == 30090 && questState.getCond() == 4) {
            string = "sandra_q0048_0403.htm";
        } else if (n == 30116 && questState.getCond() == 5) {
            string = "dustin_q0048_0501.htm";
        } else if (n == 30116 && questState.getCond() == 6) {
            string = "dustin_q0048_0603.htm";
        }
        return string;
    }
}

