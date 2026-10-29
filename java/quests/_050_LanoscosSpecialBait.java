/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _050_LanoscosSpecialBait
extends Quest
implements ScriptFile {
    int Lanosco = 31570;
    int SingingWind = 21026;
    int EssenceofWind = 7621;
    int WindFishingLure = 7610;
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

    public _050_LanoscosSpecialBait() {
        super(0);
        this.addStartNpc(this.Lanosco);
        this.addTalkId(this.Lanosco);
        this.addKillId(this.SingingWind);
        this.addQuestItem(this.EssenceofWind);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("fisher_lanosco_q0050_0104.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("fisher_lanosco_q0050_0201.htm")) {
            if (questState.getQuestItemsCount(this.EssenceofWind) < 100L) {
                string2 = "fisher_lanosco_q0050_0202.htm";
            } else {
                questState.unset("cond");
                questState.takeItems(this.EssenceofWind, -1L);
                questState.giveItems(this.WindFishingLure, 4L);
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
        if (n == this.Lanosco) {
            if (n3 == 1) {
                if (questState.getPlayer().getLevel() < 27) {
                    string = "fisher_lanosco_q0050_0103.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getSkillLevel(this.FishSkill) >= 8) {
                    string = "fisher_lanosco_q0050_0101.htm";
                } else {
                    string = "fisher_lanosco_q0050_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1 || n2 == 2) {
                if (questState.getQuestItemsCount(this.EssenceofWind) < 100L) {
                    string = "fisher_lanosco_q0050_0106.htm";
                    questState.setCond(1);
                } else {
                    string = "fisher_lanosco_q0050_0105.htm";
                }
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == this.SingingWind && questState.getCond() == 1 && questState.getQuestItemsCount(this.EssenceofWind) < 100L && Rnd.chance(30)) {
            questState.giveItems(this.EssenceofWind, 1L);
            if (questState.getQuestItemsCount(this.EssenceofWind) == 100L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

