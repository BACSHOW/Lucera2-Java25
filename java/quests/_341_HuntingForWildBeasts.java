/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _341_HuntingForWildBeasts
extends Quest
implements ScriptFile {
    private static int blR = 30078;
    private static int bna = 20021;
    private static int bnb = 20203;
    private static int bnc = 20310;
    private static int bnd = 20335;
    private static int bne = 4259;
    private static int bnf = 40;

    public _341_HuntingForWildBeasts() {
        super(0);
        this.addStartNpc(blR);
        this.addKillId(bna);
        this.addKillId(bnb);
        this.addKillId(bnc);
        this.addKillId(bnd);
        this.addQuestItem(bne);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept") && questState.getState() == 1) {
            string2 = "pano_q0341_04.htm";
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != blR) {
            return string;
        }
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getLevel() >= 20) {
                string = "pano_q0341_01.htm";
                questState.setCond(0);
            } else {
                string = "pano_q0341_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 2) {
            if (questState.getQuestItemsCount(bne) >= 20L) {
                string = "pano_q0341_05.htm";
                questState.takeItems(bne, -1L);
                questState.giveItems(57, 3710L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string = "pano_q0341_06.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        long l = questState.getQuestItemsCount(bne);
        if (l < 20L && Rnd.chance(bnf)) {
            questState.giveItems(bne, 1L);
            if (l == 19L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
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

