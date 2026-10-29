/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _002_WhatWomenWant
extends Quest
implements ScriptFile {
    int ARUJIEN = 30223;
    int MIRABEL = 30146;
    int HERBIEL = 30150;
    int GREENIS = 30157;
    int ARUJIENS_LETTER1 = 1092;
    int ARUJIENS_LETTER2 = 1093;
    int ARUJIENS_LETTER3 = 1094;
    int POETRY_BOOK = 689;
    int GREENIS_LETTER = 693;
    int MYSTICS_EARRING = 113;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _002_WhatWomenWant() {
        super(0);
        this.addStartNpc(this.ARUJIEN);
        this.addTalkId(this.MIRABEL);
        this.addTalkId(this.HERBIEL);
        this.addTalkId(this.GREENIS);
        this.addQuestItem(this.GREENIS_LETTER, this.ARUJIENS_LETTER3, this.ARUJIENS_LETTER1, this.ARUJIENS_LETTER2, this.POETRY_BOOK);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "arujien_q0002_04.htm";
            questState.giveItems(this.ARUJIENS_LETTER1, 1L, false);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("2_1")) {
            string2 = "arujien_q0002_08.htm";
            questState.takeItems(this.ARUJIENS_LETTER3, -1L);
            questState.giveItems(this.POETRY_BOOK, 1L, false);
            questState.setCond(4);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("2_2")) {
            string2 = "arujien_q0002_09.htm";
            questState.takeItems(this.ARUJIENS_LETTER3, -1L);
            questState.giveItems(57, 450L, true);
            if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("ng1")) {
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            }
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == this.ARUJIEN) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.elf && questState.getPlayer().getRace() != Race.human) {
                    string = "arujien_q0002_00.htm";
                } else if (questState.getPlayer().getLevel() >= 2) {
                    string = "arujien_q0002_02.htm";
                } else {
                    string = "arujien_q0002_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1 && questState.getQuestItemsCount(this.ARUJIENS_LETTER1) > 0L) {
                string = "arujien_q0002_05.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(this.ARUJIENS_LETTER2) > 0L) {
                string = "arujien_q0002_06.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(this.ARUJIENS_LETTER3) > 0L) {
                string = "arujien_q0002_07.htm";
            } else if (n2 == 4 && questState.getQuestItemsCount(this.POETRY_BOOK) > 0L) {
                string = "arujien_q0002_11.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(this.GREENIS_LETTER) > 0L) {
                string = "arujien_q0002_09.htm";
                questState.takeItems(this.GREENIS_LETTER, -1L);
                questState.giveItems(this.MYSTICS_EARRING, 1L, false);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("ng1")) {
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else if (n == this.MIRABEL) {
            if (n2 == 1 && questState.getQuestItemsCount(this.ARUJIENS_LETTER1) > 0L) {
                string = "mint_q0002_01.htm";
                questState.takeItems(this.ARUJIENS_LETTER1, -1L);
                questState.giveItems(this.ARUJIENS_LETTER2, 1L, false);
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 2) {
                string = "mint_q0002_02.htm";
            }
        } else if (n == this.HERBIEL) {
            if (n2 == 2 && questState.getQuestItemsCount(this.ARUJIENS_LETTER2) > 0L) {
                string = "green_q0002_01.htm";
                questState.takeItems(this.ARUJIENS_LETTER2, -1L);
                questState.giveItems(this.ARUJIENS_LETTER3, 1L, false);
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 3) {
                string = "green_q0002_02.htm";
            }
        } else if (n == this.GREENIS) {
            if (n2 == 4 && questState.getQuestItemsCount(this.POETRY_BOOK) > 0L) {
                string = "grain_q0002_02.htm";
                questState.takeItems(this.POETRY_BOOK, -1L);
                questState.giveItems(this.GREENIS_LETTER, 1L, false);
                questState.setCond(5);
                questState.playSound("ItemSound.quest_middle");
            } else {
                string = n2 == 5 && questState.getQuestItemsCount(this.GREENIS_LETTER) > 0L ? "grain_q0002_03.htm" : "grain_q0002_01.htm";
            }
        }
        return string;
    }
}

