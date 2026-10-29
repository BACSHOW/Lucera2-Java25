/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import l2.commons.dbutils.DbUtils;
import l2.commons.util.Rnd;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;

public class _501_ProofOfClanAlliance
extends Quest
implements ScriptFile {
    private static final int bHM = 30756;
    private static final int bHN = 30757;
    private static final int bHO = 30758;
    private static final int bHP = 30759;
    private static final int bHQ = 3832;
    private static final int bHR = 3833;
    private static final int bHS = 3834;
    private static final int bHT = 3835;
    private static final int bHU = 3837;
    private static final int bHV = 3874;
    private static final int bHW = 3873;
    private static final int bHX = 3872;
    private static final int bHY = 3889;
    private static final int[] bHZ = new int[]{27173, 27174, 27175, 27176, 27177};
    private static final int[][] bIa = new int[][]{{20685, 3833}, {20644, 3832}, {20576, 3834}};
    private static final int bIb = 35;
    private static final int bIc = 10000;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _501_ProofOfClanAlliance() {
        super(0);
        this.addStartNpc(30756);
        this.addStartNpc(30757);
        this.addStartNpc(30758);
        this.addTalkId(30759);
        this.addQuestItem(3837);
        this.addQuestItem(3872);
        for (int[] nArray : bIa) {
            this.addKillId(nArray[0]);
            this.addQuestItem(nArray[1]);
        }
        for (int n : bHZ) {
            this.addKillId(n);
        }
    }

    public QuestState getLeader(QuestState questState) {
        Clan clan = questState.getPlayer().getClan();
        QuestState questState2 = null;
        if (clan != null && clan.getLeader() != null && clan.getLeader().getPlayer() != null) {
            questState2 = clan.getLeader().getPlayer().getQuestState(this.getName());
        }
        return questState2;
    }

    public void removeQuestFromMembers(QuestState questState, boolean bl) {
        this.removeQuestFromOfflineMembers(questState);
        this.removeQuestFromOnlineMembers(questState, bl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeQuestFromOfflineMembers(QuestState questState) {
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return;
        }
        int n = questState.getPlayer().getClan().getClanId();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM character_quests WHERE name = ? AND char_id IN (SELECT obj_id FROM characters WHERE clanId = ? AND online = 0)");
            preparedStatement.setString(1, this.getName());
            preparedStatement.setInt(2, n);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                exception.printStackTrace();
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public void removeQuestFromOnlineMembers(QuestState questState, boolean bl) {
        QuestState questState2;
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return;
        }
        Creature creature = null;
        if (bl && (questState2 = this.getLeader(questState)) != null) {
            creature = questState2.getPlayer();
        }
        if (creature != null) {
            creature.stopImmobilized();
            creature.getEffectList().stopEffect(4082);
        }
        for (Player player : questState.getPlayer().getClan().getOnlineMembers(questState.getPlayer().getClan().getLeaderId())) {
            if (player == null || player.getQuestState(this.getName()) == null) continue;
            player.getQuestState(this.getName()).exitCurrentQuest(true);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return "noquest";
        }
        QuestState questState2 = this.getLeader(questState);
        if (questState2 == null) {
            this.removeQuestFromMembers(questState, true);
            return "Quest Failed";
        }
        String string2 = string;
        if (questState.getPlayer().isClanLeader()) {
            if (string.equalsIgnoreCase("30756-03.htm")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
            } else if (string.equalsIgnoreCase("30759-03.htm")) {
                questState.setCond(2);
                questState.set("dead_list", " ");
            } else if (string.equalsIgnoreCase("30759-07.htm")) {
                questState.takeItems(3837, -1L);
                questState.giveItems(3872, 1L);
                questState.addNotifyOfDeath(questState.getPlayer(), false);
                questState.setCond(3);
                questState.set("chest_count", "0");
                questState.set("chest_game", "0");
                questState.set("chest_try", "0");
                questState.startQuestTimer("poison_timer", 3600000L);
                questState.getPlayer().altUseSkill(SkillTable.getInstance().getInfo(4082, 1), questState.getPlayer());
                questState.getPlayer().startImmobilized();
                string2 = "30759-07.htm";
            }
        }
        if (string.equalsIgnoreCase("poison_timer")) {
            this.removeQuestFromMembers(questState, true);
            string2 = "30759-09.htm";
        } else if (string.equalsIgnoreCase("chest_timer")) {
            string2 = "";
            if (questState2.getInt("chest_game") < 2) {
                this.stop_chest_game(questState);
            }
        } else if (string.equalsIgnoreCase("30757-04.htm")) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.addAll(Arrays.asList(questState2.get("dead_list").split(" ")));
            arrayList.add(questState.getPlayer().getName());
            String string3 = "";
            for (String string4 : arrayList) {
                string3 = string3 + string4 + " ";
            }
            questState2.set("dead_list", string3);
            questState.addNotifyOfDeath(questState2.getPlayer(), false);
            if (Rnd.chance(50)) {
                questState.getPlayer().reduceCurrentHp(questState.getPlayer().getCurrentHp() * 8.0, questState.getPlayer(), null, true, true, false, false, false, false, false);
            }
            questState.giveItems(3837, 1L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30757-05.htm")) {
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("30758-03.htm")) {
            this.start_chest_game(questState);
        } else if (string.equalsIgnoreCase("30758-07.htm")) {
            if (questState.getQuestItemsCount(57) < 10000L) {
                string2 = "30758-06.htm";
            } else {
                questState.takeItems(57, 10000L);
            }
        }
        return string2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String[] stringArray;
        String string = "noquest";
        int n = questState.getCond();
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return string;
        }
        QuestState questState2 = this.getLeader(questState);
        if (questState2 == null) {
            this.removeQuestFromMembers(questState, true);
            return "Quest Failed";
        }
        int n2 = npcInstance.getNpcId();
        if (n2 == 30756) {
            if (!questState.getPlayer().isClanLeader()) {
                questState.exitCurrentQuest(true);
                return "30756-10.htm";
            }
            if (questState.getPlayer().getClan().getLevel() <= 2) {
                questState.exitCurrentQuest(true);
                return "30756-08.htm";
            }
            if (questState.getPlayer().getClan().getLevel() >= 4) {
                questState.exitCurrentQuest(true);
                return "30756-09.htm";
            }
            if (questState.getQuestItemsCount(3873) > 0L) {
                questState.playSound("ItemSound.quest_fanfare_2");
                questState.takeItems(3873, -1L);
                questState.giveItems(3874, 1L);
                questState.addExpAndSp(0L, 120000L);
                string = "30756-07.htm";
                questState.exitCurrentQuest(true);
                return string;
            }
            if (n == 1 || n == 2) {
                return "30756-06.htm";
            }
            if (questState.getQuestItemsCount(3874) == 0L) {
                questState.setCond(0);
                return "30756-01.htm";
            }
            questState.exitCurrentQuest(true);
            return string;
        }
        if (n2 == 30759) {
            if (questState.getPlayer().isClanLeader()) {
                if (n == 1) {
                    return "30759-01.htm";
                }
                if (n == 2) {
                    string = "30759-05.htm";
                    if (questState.getQuestItemsCount(3837) != 3L) return string;
                    int n3 = 0;
                    try {
                        n3 = questState.get("dead_list").split(" ").length;
                        return string;
                    }
                    finally {
                        if (n3 == 3) {
                            string = "30759-06.htm";
                        }
                    }
                }
                if (n != 3) return string;
                if (questState.getQuestItemsCount(3832) > 0L && questState.getQuestItemsCount(3833) > 0L && questState.getQuestItemsCount(3834) > 0L && questState.getQuestItemsCount(3835) > 0L && questState.getQuestItemsCount(3872) > 0L) {
                    questState.takeItems(3872, 1L);
                    questState.takeItems(3832, 1L);
                    questState.takeItems(3833, 1L);
                    questState.takeItems(3834, 1L);
                    questState.takeItems(3835, 1L);
                    questState.giveItems(3889, 1L);
                    questState.giveItems(3873, 1L);
                    questState.cancelQuestTimer("poison_timer");
                    this.removeQuestFromMembers(questState, false);
                    questState.getPlayer().stopImmobilized();
                    questState.getPlayer().getEffectList().stopEffect(4082);
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_finish");
                    return "30759-08.htm";
                }
                if (questState.getQuestItemsCount(3873) != 0L) return string;
                return "30759-10.htm";
            }
            if (questState2.getCond() != 3) return string;
            return "30759-11.htm";
        }
        if (n2 == 30757) {
            int n4;
            String[] stringArray2;
            if (questState.getPlayer().isClanLeader()) {
                return "30757-03.htm";
            }
            if (questState.getPlayer().getLevel() <= 39) {
                questState.exitCurrentQuest(true);
                return "30757-02.htm";
            }
            try {
                stringArray2 = questState2.get("dead_list").split(" ");
                n4 = stringArray2.length;
            }
            catch (Exception exception) {
                this.removeQuestFromMembers(questState, true);
                return "Who are you?";
            }
            if (n4 >= 3) return string;
            for (String string2 : stringArray2) {
                if (!questState.getPlayer().getName().equalsIgnoreCase(string2)) continue;
                return "you cannot die again!";
            }
            return "30757-01.htm";
        }
        if (n2 != 30758) return string;
        if (questState.getPlayer().isClanLeader()) {
            return "30757-03.htm";
        }
        try {
            stringArray = questState2.get("dead_list").split(" ");
        }
        catch (Exception exception) {
            questState.exitCurrentQuest(true);
            return "Who are you?";
        }
        Boolean bl = false;
        if (stringArray != null) {
            for (String string3 : stringArray) {
                if (!questState.getPlayer().getName().equalsIgnoreCase(string3)) continue;
                bl = true;
            }
        }
        if (!bl.booleanValue()) {
            questState.exitCurrentQuest(true);
            return "Who are you?";
        }
        int n5 = questState2.getInt("chest_game");
        if (n5 == 0) {
            if (questState2.getInt("chest_try") != 0) return "30758-05.htm";
            return "30758-01.htm";
        }
        if (n5 == 1) {
            return "30758-09.htm";
        }
        if (n5 != 2) return string;
        questState.playSound("ItemSound.quest_finish");
        questState.giveItems(3835, 1L);
        questState.cancelQuestTimer("chest_timer");
        this.stop_chest_game(questState);
        questState2.set("chest_game", "3");
        return "30758-08.htm";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return "noquest";
        }
        QuestState questState2 = this.getLeader(questState);
        if (questState2 == null) {
            this.removeQuestFromMembers(questState, true);
            return "Quest Failed";
        }
        int n = npcInstance.getNpcId();
        if (!questState2.isRunningQuestTimer("poison_timer")) {
            this.stop_chest_game(questState);
            return "Quest Failed";
        }
        for (int[] nArray : bIa) {
            if (n != nArray[0] || questState.getInt(String.valueOf(nArray[1])) != 0 || !Rnd.chance(35)) continue;
            questState.giveItems(nArray[1], 1L);
            questState2.set(String.valueOf(nArray[1]), "1");
            questState.playSound("ItemSound.quest_middle");
            return null;
        }
        for (int n2 : bHZ) {
            if (n != n2) continue;
            if (!questState2.isRunningQuestTimer("chest_timer")) {
                this.stop_chest_game(questState);
                return "Time is up!";
            }
            if (Rnd.chance(25)) {
                Functions.npcSay(npcInstance, "###### BINGO! ######");
                int n3 = questState2.getInt("chest_count");
                if (n3 < 4) {
                    questState2.set("chest_count", String.valueOf(++n3));
                }
                if (n3 >= 4) {
                    this.stop_chest_game(questState);
                    questState2.set("chest_game", "2");
                    questState2.cancelQuestTimer("chest_timer");
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
            return null;
        }
        return null;
    }

    public void start_chest_game(QuestState questState) {
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return;
        }
        QuestState questState2 = this.getLeader(questState);
        if (questState2 == null) {
            this.removeQuestFromMembers(questState, true);
            return;
        }
        questState2.set("chest_game", "1");
        questState2.set("chest_count", "0");
        int n = questState2.getInt("chest_try");
        questState2.set("chest_try", String.valueOf(n + 1));
        for (NpcInstance object : GameObjectsStorage.getAllByNpcId(bHZ, false)) {
            object.deleteMe();
        }
        for (int i = 1; i <= 5; ++i) {
            for (int n2 : bHZ) {
                questState2.addSpawn(n2, 102100, 103450, -3400, 0, 100, 60000);
            }
        }
        questState2.startQuestTimer("chest_timer", 60000L);
    }

    public void stop_chest_game(QuestState questState) {
        QuestState questState2 = this.getLeader(questState);
        if (questState2 == null) {
            this.removeQuestFromMembers(questState, true);
            return;
        }
        for (NpcInstance npcInstance : GameObjectsStorage.getAllByNpcId(bHZ, false)) {
            npcInstance.deleteMe();
        }
        questState2.set("chest_game", "0");
    }

    @Override
    public String onDeath(Creature creature, Creature creature2, QuestState questState) {
        if (questState.getPlayer() == null || questState.getPlayer().getClan() == null) {
            questState.exitCurrentQuest(true);
            return null;
        }
        QuestState questState2 = this.getLeader(questState);
        if (questState2 == null) {
            this.removeQuestFromMembers(questState, true);
            return null;
        }
        if (questState.getPlayer() == creature2) {
            questState2.cancelQuestTimer("poison_timer");
            questState2.cancelQuestTimer("chest_timer");
            this.removeQuestFromMembers(questState, true);
        }
        return null;
    }
}

