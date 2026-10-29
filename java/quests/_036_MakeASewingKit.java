/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._037_PleaseMakeMeFormalWear;

public class _036_MakeASewingKit
extends Quest
implements ScriptFile {
    int REINFORCED_STEEL = 7163;
    int ARTISANS_FRAME = 1891;
    int ORIHARUKON = 1893;
    int SEWING_KIT = 7078;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _036_MakeASewingKit() {
        super(0);
        this.addStartNpc(30847);
        this.addTalkId(30847);
        this.addTalkId(30847);
        this.addKillId(20566);
        this.addQuestItem(this.REINFORCED_STEEL);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        if (string.equals("head_blacksmith_ferris_q0036_0104.htm") && n == 0) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("head_blacksmith_ferris_q0036_0201.htm") && n == 2) {
            questState.takeItems(this.REINFORCED_STEEL, 5L);
            questState.setCond(3);
        } else if (string.equals("head_blacksmith_ferris_q0036_0301.htm")) {
            if (questState.getQuestItemsCount(this.ORIHARUKON) >= 10L && questState.getQuestItemsCount(this.ARTISANS_FRAME) >= 10L) {
                questState.takeItems(this.ORIHARUKON, 10L);
                questState.takeItems(this.ARTISANS_FRAME, 10L);
                questState.giveItems(this.SEWING_KIT, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "head_blacksmith_ferris_q0036_0203.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0 && questState.getQuestItemsCount(this.SEWING_KIT) == 0L) {
            if (questState.getPlayer().getLevel() >= 60) {
                QuestState questState2 = questState.getPlayer().getQuestState(_037_PleaseMakeMeFormalWear.class);
                if (questState2 != null && questState2.getState() == 2) {
                    if (questState2.getCond() == 6) {
                        string = "head_blacksmith_ferris_q0036_0101.htm";
                    } else {
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "head_blacksmith_ferris_q0036_0103.htm";
            }
        } else if (n == 1 && questState.getQuestItemsCount(this.REINFORCED_STEEL) < 5L) {
            string = "head_blacksmith_ferris_q0036_0106.htm";
        } else if (n == 2 && questState.getQuestItemsCount(this.REINFORCED_STEEL) == 5L) {
            string = "head_blacksmith_ferris_q0036_0105.htm";
        } else if (n == 3 && (questState.getQuestItemsCount(this.ORIHARUKON) < 10L || questState.getQuestItemsCount(this.ARTISANS_FRAME) < 10L)) {
            string = "head_blacksmith_ferris_q0036_0204.htm";
        } else if (n == 3 && questState.getQuestItemsCount(this.ORIHARUKON) >= 10L && questState.getQuestItemsCount(this.ARTISANS_FRAME) >= 10L) {
            string = "head_blacksmith_ferris_q0036_0203.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(this.REINFORCED_STEEL) < 5L) {
            questState.giveItems(this.REINFORCED_STEEL, 1L);
            if (questState.getQuestItemsCount(this.REINFORCED_STEEL) == 5L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

