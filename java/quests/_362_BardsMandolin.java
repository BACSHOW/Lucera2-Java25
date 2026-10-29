/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _362_BardsMandolin
extends Quest
implements ScriptFile {
    private static int brX = 30957;
    private static int NANARIN = 30956;
    private static int brY = 30958;
    private static int brZ = 30837;
    private static int bsa = 4316;
    private static int bsb = 4317;
    private static int bsc = 4410;

    public _362_BardsMandolin() {
        super(0);
        this.addStartNpc(brX);
        this.addTalkId(NANARIN);
        this.addTalkId(brY);
        this.addTalkId(brZ);
        this.addQuestItem(bsa);
        this.addQuestItem(bsb);
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        if (questState.getState() == 1) {
            if (n != brX) {
                return string;
            }
            questState.setCond(0);
        }
        int n2 = questState.getCond();
        if (n == brX) {
            if (n2 == 0) {
                string = "30957_1.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(bsa) > 0L && questState.getQuestItemsCount(bsb) == 0L) {
                string = "30957_3.htm";
                questState.setCond(4);
                questState.giveItems(bsb, 1L);
            } else if (n2 == 4 && questState.getQuestItemsCount(bsa) > 0L && questState.getQuestItemsCount(bsb) > 0L) {
                string = "30957_6.htm";
            } else if (n2 == 5) {
                string = "30957_4.htm";
            }
        } else if (n == brZ && n2 == 1) {
            string = "30837_1.htm";
            questState.setCond(2);
        } else if (n == brY && n2 == 2) {
            string = "30958_1.htm";
            questState.setCond(3);
            questState.giveItems(bsa, 1L);
            questState.playSound("ItemSound.quest_itemget");
        } else if (n == NANARIN && n2 == 4 && questState.getQuestItemsCount(bsa) > 0L && questState.getQuestItemsCount(bsb) > 0L) {
            string = "30956_1.htm";
            questState.takeItems(bsa, 1L);
            questState.takeItems(bsb, 1L);
            questState.setCond(5);
        }
        return string;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getState();
        int n2 = questState.getCond();
        if (string.equalsIgnoreCase("30957_2.htm") && n == 1 && n2 == 0) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30957_5.htm") && n == 2 && n2 == 5) {
            questState.giveItems(57, 10000L);
            questState.giveItems(bsc, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
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

