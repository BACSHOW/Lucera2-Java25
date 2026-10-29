/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _053_LinnaeusSpecialBait
extends Quest
implements ScriptFile {
    int Linnaeu = 31577;
    int CrimsonDrake = 20670;
    int HeartOfCrimsonDrake = 7624;
    int FlameFishingLure = 7613;
    Integer FishSkill = 1315;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _053_LinnaeusSpecialBait() {
        super(0);
        this.addStartNpc(this.Linnaeu);
        this.addTalkId(this.Linnaeu);
        this.addKillId(this.CrimsonDrake);
        this.addQuestItem(this.HeartOfCrimsonDrake);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("fisher_linneaus_q0053_0104.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("fisher_linneaus_q0053_0201.htm")) {
            if (questState.getQuestItemsCount(this.HeartOfCrimsonDrake) < 100L) {
                string2 = "fisher_linneaus_q0053_0202.htm";
            } else {
                questState.unset("cond");
                questState.takeItems(this.HeartOfCrimsonDrake, -1L);
                questState.giveItems(this.FlameFishingLure, 4L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        int n3 = questState.getState();
        if (n == this.Linnaeu) {
            if (n3 == 1) {
                if (questState.getPlayer().getLevel() < 60) {
                    string = "fisher_linneaus_q0053_0103.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getSkillLevel(this.FishSkill) >= 21) {
                    string = "fisher_linneaus_q0053_0101.htm";
                } else {
                    string = "fisher_linneaus_q0053_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1 || n2 == 2) {
                if (questState.getQuestItemsCount(this.HeartOfCrimsonDrake) < 100L) {
                    string = "fisher_linneaus_q0053_0106.htm";
                    questState.setCond(1);
                } else {
                    string = "fisher_linneaus_q0053_0105.htm";
                }
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == this.CrimsonDrake && questState.getCond() == 1 && questState.getQuestItemsCount(this.HeartOfCrimsonDrake) < 100L && Rnd.chance(30)) {
            questState.giveItems(this.HeartOfCrimsonDrake, 1L);
            if (questState.getQuestItemsCount(this.HeartOfCrimsonDrake) == 100L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

