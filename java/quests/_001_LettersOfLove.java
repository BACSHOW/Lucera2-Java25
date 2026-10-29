/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _001_LettersOfLove
extends Quest
implements ScriptFile {
    private static final int azq = 30048;
    private static final int azr = 30006;
    private static final int azs = 30033;
    private static final int azt = 687;
    private static final int azu = 688;
    private static final int azv = 1079;
    private static final int azw = 1080;
    private static final int azx = 906;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _001_LettersOfLove() {
        super(0);
        this.addStartNpc(30048);
        this.addTalkId(30006);
        this.addTalkId(30033);
        this.addQuestItem(687);
        this.addQuestItem(688);
        this.addQuestItem(1079);
        this.addQuestItem(1080);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "daring_q0001_06.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.giveItems(687, 1L, false);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        switch (n) {
            case 30048: {
                if (n2 == 0) {
                    if (questState.getPlayer().getLevel() >= 2) {
                        string = "daring_q0001_02.htm";
                        break;
                    }
                    string = "daring_q0001_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (n2 == 1) {
                    string = "daring_q0001_07.htm";
                    break;
                }
                if (n2 == 2 && questState.getQuestItemsCount(688) == 1L) {
                    string = "daring_q0001_08.htm";
                    questState.takeItems(688, -1L);
                    questState.giveItems(1079, 1L, false);
                    questState.setCond(3);
                    questState.playSound("ItemSound.quest_middle");
                    break;
                }
                if (n2 == 3) {
                    string = "daring_q0001_09.htm";
                    break;
                }
                if (n2 != 4 || questState.getQuestItemsCount(1080) != 1L) break;
                string = "daring_q0001_10.htm";
                questState.takeItems(1080, -1L);
                questState.giveItems(906, 1L, false);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("ng1")) {
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
                break;
            }
            case 30006: {
                if (n2 == 1 && questState.getQuestItemsCount(688) == 0L && questState.getQuestItemsCount(687) > 0L) {
                    string = "rapunzel_q0001_01.htm";
                    questState.takeItems(687, -1L);
                    questState.giveItems(688, 1L, false);
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                    break;
                }
                if (n2 == 2 && questState.getQuestItemsCount(688) > 0L) {
                    string = "rapunzel_q0001_02.htm";
                    break;
                }
                if (n2 <= 2 || questState.getQuestItemsCount(1080) <= 0L && questState.getQuestItemsCount(1079) <= 0L) break;
                string = "rapunzel_q0001_03.htm";
                break;
            }
            case 30033: {
                if (n2 == 3 && questState.getQuestItemsCount(1079) == 1L) {
                    string = "baul_q0001_01.htm";
                    questState.takeItems(1079, -1L);
                    questState.giveItems(1080, 1L, false);
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                    break;
                }
                if (n2 != 4) break;
                string = "baul_q0001_02.htm";
            }
        }
        return string;
    }
}

