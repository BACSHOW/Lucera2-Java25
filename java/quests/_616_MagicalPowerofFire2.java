/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _616_MagicalPowerofFire2
extends Quest
implements ScriptFile {
    private static final int bMi = 31558;
    private static final int bMd = 31379;
    private static final int bMj = 7244;
    private static final int bMg = 7243;
    private static final int bMk = 25306;
    private NpcInstance bMl = null;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _616_MagicalPowerofFire2() {
        super(1);
        this.addStartNpc(31379);
        this.addTalkId(31558);
        this.addKillId(25306);
        this.addQuestItem(7244);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(25306);
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "shaman_udan_q0616_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("616_1")) {
            if (ServerVariables.getLong(_616_MagicalPowerofFire2.class.getSimpleName(), 0L) + 10800000L > System.currentTimeMillis()) {
                string2 = "totem_of_ketra_q0616_0204.htm";
            } else if (questState.getQuestItemsCount(7243) >= 1L && npcInstance2 == null) {
                questState.takeItems(7243, 1L);
                this.bMl = questState.addSpawn(25306, 142528, -82528, -6496);
                this.bMl.addListener(new DeathListener());
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "totem_of_ketra_q0616_0203.htm";
            }
        } else if (string.equalsIgnoreCase("616_3")) {
            if (questState.getQuestItemsCount(7244) >= 1L) {
                questState.takeItems(7244, -1L);
                questState.addExpAndSp(10000L, 0L);
                questState.playSound("ItemSound.quest_finish");
                string2 = "shaman_udan_q0616_0301.htm";
                questState.exitCurrentQuest(true);
            } else {
                string2 = "shaman_udan_q0616_0302.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(25306);
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        switch (n) {
            case 31379: {
                if (n2 == 0) {
                    if (questState.getPlayer().getLevel() >= 75) {
                        if (questState.getQuestItemsCount(7243) >= 1L) {
                            string = "shaman_udan_q0616_0101.htm";
                            break;
                        }
                        string = "shaman_udan_q0616_0102.htm";
                        questState.exitCurrentQuest(true);
                        break;
                    }
                    string = "shaman_udan_q0616_0103.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (n2 == 1) {
                    string = "shaman_udan_q0616_0105.htm";
                    break;
                }
                if (n2 == 2) {
                    string = "shaman_udan_q0616_0202.htm";
                    break;
                }
                if (n2 != 3 || questState.getQuestItemsCount(7244) < 1L) break;
                string = "shaman_udan_q0616_0201.htm";
                break;
            }
            case 31558: {
                if (ServerVariables.getLong(_616_MagicalPowerofFire2.class.getSimpleName(), 0L) + 10800000L > System.currentTimeMillis()) {
                    string = "totem_of_ketra_q0616_0204.htm";
                    break;
                }
                if (npcInstance.isBusy()) {
                    string = "totem_of_ketra_q0616_0202.htm";
                    break;
                }
                if (n2 == 1) {
                    string = "totem_of_ketra_q0616_0101.htm";
                    break;
                }
                if (n2 != 2) break;
                if (npcInstance2 == null) {
                    this.bMl = questState.addSpawn(25306, 142528, -82528, -6496);
                    this.bMl.addListener(new DeathListener());
                    string = "totem_of_ketra_q0616_0204.htm";
                    break;
                }
                string = "<html><body>Already in spawn.</body></html>";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(7244) == 0L) {
            questState.giveItems(7244, 1L);
            questState.setCond(3);
            if (this.bMl != null) {
                this.bMl.deleteMe();
            }
            this.bMl = null;
        }
        return null;
    }

    private static class DeathListener
    implements OnDeathListener {
        private DeathListener() {
        }

        @Override
        public void onDeath(Creature creature, Creature creature2) {
            ServerVariables.set(_616_MagicalPowerofFire2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }
}

