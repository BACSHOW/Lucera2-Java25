/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.ScriptFile;

public class _104_SpiritOfMirror
extends Quest
implements ScriptFile {
    private static final int aDp = 748;
    private static final int aDq = 1135;
    private static final int aDr = 1136;
    private static final int aDs = 1137;
    private static final int aDt = 747;
    static final SystemMessage2 CACHE_SYSMSG_GALLINS_OAK_WAND = SystemMessage2.removeItems(748, 1L);

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _104_SpiritOfMirror() {
        super(0);
        this.addStartNpc(30017);
        this.addTalkId(30041, 30043, 30045);
        this.addKillId(27003, 27004, 27005);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("gallin_q0104_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(748, 3L);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30017) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.human) {
                    string = "gallin_q0104_00.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    if (questState.getPlayer().getLevel() >= 10) {
                        string = "gallin_q0104_02.htm";
                        return string;
                    }
                    string = "gallin_q0104_06.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1 && questState.getQuestItemsCount(748) >= 1L && (questState.getQuestItemsCount(1135) == 0L || questState.getQuestItemsCount(1136) == 0L || questState.getQuestItemsCount(1137) == 0L)) {
                string = "gallin_q0104_04.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(1135) >= 1L && questState.getQuestItemsCount(1136) >= 1L && questState.getQuestItemsCount(1137) >= 1L) {
                questState.takeAllItems(1135, 1136, 1137);
                questState.giveItems(747, 1L);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q3")) {
                    questState.getPlayer().setVar("p1q3", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    questState.giveItems(1060, 100L);
                    for (int i = 4412; i <= 4417; ++i) {
                        questState.giveItems(i, 10L);
                    }
                    if (questState.getPlayer().getClassId().isMage()) {
                        questState.playTutorialVoice("tutorial_voice_027");
                        questState.giveItems(5790, 3000L);
                        questState.giveItems(2509, 500L);
                    } else {
                        questState.playTutorialVoice("tutorial_voice_026");
                        questState.giveItems(5789, 7000L);
                        questState.giveItems(1835, 1000L);
                    }
                }
                string = "gallin_q0104_05.htm";
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
            }
        } else if ((n == 30041 || n == 30043 || n == 30045) && n2 == 1) {
            if (n == 30041 && questState.getInt("id1") == 0) {
                questState.set("id1", "1");
            }
            string = "arnold_q0104_01.htm";
            if (n == 30043 && questState.getInt("id2") == 0) {
                questState.set("id2", "1");
            }
            string = "johnson_q0104_01.htm";
            if (n == 30045 && questState.getInt("id3") == 0) {
                questState.set("id3", "1");
            }
            string = "ken_q0104_01.htm";
            if (questState.getInt("id1") + questState.getInt("id2") + questState.getInt("id3") == 3) {
                questState.setCond(2);
                questState.unset("id1");
                questState.unset("id2");
                questState.unset("id3");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        if ((n == 1 || n == 2) && questState.getPlayer().getActiveWeaponInstance() != null && questState.getPlayer().getActiveWeaponInstance().getItemId() == 748) {
            ItemInstance itemInstance = questState.getPlayer().getActiveWeaponInstance();
            if (n2 == 27003 && questState.getQuestItemsCount(1135) == 0L) {
                if (questState.getPlayer().getInventory().destroyItem(itemInstance, 1L)) {
                    questState.giveItems(1135, 1L);
                    questState.getPlayer().sendPacket((IStaticPacket)CACHE_SYSMSG_GALLINS_OAK_WAND);
                    long l = questState.getQuestItemsCount(1135) + questState.getQuestItemsCount(1136) + questState.getQuestItemsCount(1137);
                    if (l == 3L) {
                        questState.setCond(3);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 27004 && questState.getQuestItemsCount(1136) == 0L) {
                if (questState.getPlayer().getInventory().destroyItem(itemInstance, 1L)) {
                    questState.giveItems(1136, 1L);
                    questState.getPlayer().sendPacket((IStaticPacket)CACHE_SYSMSG_GALLINS_OAK_WAND);
                    long l = questState.getQuestItemsCount(1135) + questState.getQuestItemsCount(1136) + questState.getQuestItemsCount(1137);
                    if (l == 3L) {
                        questState.setCond(3);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 27005 && questState.getQuestItemsCount(1137) == 0L && questState.getPlayer().getInventory().destroyItem(itemInstance, 1L)) {
                questState.giveItems(1137, 1L);
                questState.getPlayer().sendPacket((IStaticPacket)CACHE_SYSMSG_GALLINS_OAK_WAND);
                long l = questState.getQuestItemsCount(1135) + questState.getQuestItemsCount(1136) + questState.getQuestItemsCount(1137);
                if (l == 3L) {
                    questState.setCond(3);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

