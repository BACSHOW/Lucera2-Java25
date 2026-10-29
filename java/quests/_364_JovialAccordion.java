/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _364_JovialAccordion
extends Quest
implements ScriptFile {
    private static int BARBADO = 30959;
    private static int brX = 30957;
    private static int bsd = 30060;
    private static int bse = 30960;
    private static int bsf = 30961;
    private static int bsg = 4323;
    private static int bsh = 4324;
    private static int bsi = 4321;
    private static int bsj = 4421;

    public _364_JovialAccordion() {
        super(0);
        this.addStartNpc(BARBADO);
        this.addTalkId(brX);
        this.addTalkId(bsd);
        this.addTalkId(bse);
        this.addTalkId(bsf);
        this.addQuestItem(bsg);
        this.addQuestItem(bsh);
        this.addQuestItem(bsi);
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        if (questState.getState() == 1) {
            if (n != BARBADO) {
                return string;
            }
            questState.setCond(0);
            questState.set("ok", "0");
        }
        int n2 = questState.getCond();
        if (n == BARBADO) {
            if (n2 == 0) {
                string = "30959-01.htm";
            } else if (n2 == 3) {
                string = "30959-03.htm";
                questState.giveItems(bsj, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else if (n2 > 0) {
                string = "30959-02.htm";
            }
        } else if (n == brX) {
            if (n2 == 1) {
                string = "30957-01.htm";
            } else if (n2 == 3) {
                string = "30957-05.htm";
            } else if (n2 == 2) {
                if (questState.getInt("ok") == 1 && questState.getQuestItemsCount(bsg) == 0L) {
                    questState.setCond(3);
                    string = "30957-04.htm";
                } else {
                    string = "30957-03.htm";
                }
            }
        } else if (n == bsd && n2 == 2 && questState.getQuestItemsCount(bsi) > 0L) {
            questState.set("ok", "1");
            questState.takeItems(bsi, -1L);
            string = "30060-01.htm";
        } else if (n == bse && n2 == 2) {
            string = "30960-01.htm";
        } else if (n == bsf && n2 == 2) {
            string = "30961-01.htm";
        }
        return string;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getState();
        int n2 = questState.getCond();
        if (string.equalsIgnoreCase("30959-02.htm") && n == 1 && n2 == 0) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30957-02.htm") && n == 2 && n2 == 1) {
            questState.setCond(2);
            questState.giveItems(bsg, 1L);
            questState.giveItems(bsh, 1L);
        } else if (string.equalsIgnoreCase("30960-03.htm") && n2 == 2 && questState.getQuestItemsCount(bsh) > 0L) {
            questState.takeItems(bsh, -1L);
            questState.giveItems(bsi, 1L);
            string2 = "30960-02.htm";
        } else if (string.equalsIgnoreCase("30961-03.htm") && n2 == 2 && questState.getQuestItemsCount(bsg) > 0L) {
            questState.takeItems(bsg, -1L);
            string2 = "30961-02.htm";
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

