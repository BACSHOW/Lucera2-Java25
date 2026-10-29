/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _610_MagicalPowerofWater2
extends Quest
implements ScriptFile {
    private static final int bLs = 31372;
    private static final int bLz = 31560;
    private static final int bLw = 7238;
    int ICE_HEART_OF_ASHUTAR = 7239;
    private static final int bLA = 4589;
    private static final int bLB = 4594;
    private static final int bLC = 25316;
    private NpcInstance bLD = null;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _610_MagicalPowerofWater2() {
        super(1);
        this.addStartNpc(31372);
        this.addTalkId(31560);
        this.addKillId(25316);
        this.addQuestItem(this.ICE_HEART_OF_ASHUTAR);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(25316);
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "shaman_asefa_q0610_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("610_1")) {
            if (ServerVariables.getLong(_610_MagicalPowerofWater2.class.getSimpleName(), 0L) + 10800000L > System.currentTimeMillis()) {
                string2 = "totem_of_barka_q0610_0204.htm";
            } else if (questState.getQuestItemsCount(7238) >= 1L && npcInstance2 == null) {
                questState.takeItems(7238, 1L);
                this.bLD = questState.addSpawn(25316, 104825, -36926, -1136);
                this.bLD.addListener(new DeathListener());
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "totem_of_barka_q0610_0203.htm";
            }
        } else if (string.equalsIgnoreCase("610_3")) {
            if (questState.getQuestItemsCount(this.ICE_HEART_OF_ASHUTAR) >= 1L) {
                questState.takeItems(this.ICE_HEART_OF_ASHUTAR, -1L);
                questState.addExpAndSp(10000L, 0L);
                questState.giveItems(Rnd.get(4589, 4594), 5L, true);
                questState.playSound("ItemSound.quest_finish");
                string2 = "shaman_asefa_q0610_0301.htm";
                questState.exitCurrentQuest(true);
            } else {
                string2 = "shaman_asefa_q0610_0302.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(25316);
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 31372) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 75) {
                    if (questState.getQuestItemsCount(7238) >= 1L) {
                        string = "shaman_asefa_q0610_0101.htm";
                    } else {
                        string = "shaman_asefa_q0610_0102.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "shaman_asefa_q0610_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "shaman_asefa_q0610_0105.htm";
            } else if (n2 == 2) {
                string = "shaman_asefa_q0610_0202.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(this.ICE_HEART_OF_ASHUTAR) >= 1L) {
                string = "shaman_asefa_q0610_0201.htm";
            }
        } else if (n == 31560) {
            if (!npcInstance.isBusy()) {
                if (ServerVariables.getLong(_610_MagicalPowerofWater2.class.getSimpleName(), 0L) + 10800000L > System.currentTimeMillis()) {
                    string = "totem_of_barka_q0610_0204.htm";
                } else if (n2 == 1) {
                    string = "totem_of_barka_q0610_0101.htm";
                } else if (n2 == 2 && npcInstance2 == null) {
                    this.bLD = questState.addSpawn(25316, 104825, -36926, -1136);
                    this.bLD.addListener(new DeathListener());
                    string = "totem_of_barka_q0610_0204.htm";
                }
            } else {
                string = "totem_of_barka_q0610_0202.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(this.ICE_HEART_OF_ASHUTAR) == 0L && npcInstance.getNpcId() == 25316) {
            questState.giveItems(this.ICE_HEART_OF_ASHUTAR, 1L);
            questState.setCond(3);
            if (this.bLD != null) {
                this.bLD.deleteMe();
            }
            this.bLD = null;
        }
        return null;
    }

    private static class DeathListener
    implements OnDeathListener {
        private DeathListener() {
        }

        @Override
        public void onDeath(Creature creature, Creature creature2) {
            ServerVariables.set(_610_MagicalPowerofWater2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }
}

