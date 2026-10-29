/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.quest;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.commons.logging.LogUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.commons.util.TroveUtils;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.quest.QuestNpcLogInfo;
import l2.gameserver.model.quest.QuestRates;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.model.quest.QuestTimer;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExQuestNpcLogList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Quest {
    private static final Logger _log = LoggerFactory.getLogger(Quest.class);
    public static final String SOUND_ITEMGET = "ItemSound.quest_itemget";
    public static final String SOUND_ACCEPT = "ItemSound.quest_accept";
    public static final String SOUND_MIDDLE = "ItemSound.quest_middle";
    public static final String SOUND_FINISH = "ItemSound.quest_finish";
    public static final String SOUND_GIVEUP = "ItemSound.quest_giveup";
    public static final String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
    public static final String SOUND_JACKPOT = "ItemSound.quest_jackpot";
    public static final String SOUND_LIQUID_MIX_01 = "SkillSound5.liquid_mix_01";
    public static final String SOUND_LIQUID_SUCCESS_01 = "SkillSound5.liquid_success_01";
    public static final String SOUND_LIQUID_FAIL_01 = "SkillSound5.liquid_fail_01";
    public static final String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
    public static final String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
    public static final String SOUND_FANFARE1 = "ItemSound.quest_fanfare_1";
    public static final String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
    public static final String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
    public static final String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
    public static final String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
    public static final String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
    public static final String SOUND_ED_DRONE_02 = "AmbSound.ed_drone_02";
    public static final String SOUND_CD_CRYSTAL_LOOP = "AmbSound.cd_crystal_loop";
    public static final String SOUND_DT_PERCUSSION_01 = "AmbSound.dt_percussion_01";
    public static final String SOUND_AC_PERCUSSION_02 = "AmbSound.ac_percussion_02";
    public static final String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
    public static final String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";
    public static final String SOUND_MT_CREAK01 = "AmbSound.mt_creak01";
    public static final String SOUND_D_WIND_LOOT_02 = "AmdSound.d_wind_loot_02";
    public static final String SOUND_CHARSTAT_OPEN_01 = "InterfaceSound.charstat_open_01";
    public static final String SOUND_DD_HORROR_01 = "AmbSound.dd_horror_01";
    public static final String SOUND_HORROR1 = "SkillSound5.horror_01";
    public static final String SOUND_HORROR2 = "SkillSound5.horror_02";
    public static final String SOUND_ELCROKI_SONG_FULL = "EtcSound.elcroki_song_full";
    public static final String SOUND_ELCROKI_SONG_1ST = "EtcSound.elcroki_song_1st";
    public static final String SOUND_ELCROKI_SONG_2ND = "EtcSound.elcroki_song_2nd";
    public static final String SOUND_ELCROKI_SONG_3RD = "EtcSound.elcroki_song_3rd";
    public static final String SOUND_ITEMDROP_ARMOR_LEATHER = "ItemSound.itemdrop_armor_leather";
    public static final String SOUND_EG_DRON_02 = "AmbSound.eg_dron_02";
    public static final String SOUND_MHFIGHTER_CRY = "ChrSound.MHFighter_cry";
    public static final String SOUND_ITEMDROP_WEAPON_SPEAR = "ItemSound.itemdrop_weapon_spear";
    public static final String SOUND_FDELF_CRY = "ChrSound.FDElf_Cry";
    public static final String SOUND_DD_HORROR_02 = "AmdSound.dd_horror_02";
    public static final String SOUND_D_HORROR_03 = "AmbSound.d_horror_03";
    public static final String SOUND_D_HORROR_15 = "AmbSound.d_horror_15";
    public static final String SOUND_ANTARAS_FEAR = "SkillSound3.antaras_fear";
    public static final String SOUND_T_WINGFLAP_04 = "AmbSound.t_wingflap_04";
    public static final String THUNDER_02 = "AmbSound.thunder_02";
    public static final String NO_QUEST_DIALOG = "no-quest";
    protected String _descr;
    public static final int ADENA_ID = 57;
    public static final int PARTY_NONE = 0;
    public static final int PARTY_ONE = 1;
    public static final int PARTY_ALL = 2;
    private Map<Integer, Map<String, QuestTimer>> YT = new ConcurrentHashMap<Integer, Map<String, QuestTimer>>();
    private TIntHashSet YU = new TIntHashSet();
    private TIntObjectHashMap<List<QuestNpcLogInfo>> YV = TroveUtils.emptyIntObjectMap();
    protected final String _name = this.getClass().getSimpleName();
    protected final int _party;
    protected final int _questId = Integer.parseInt(this._name.split("_")[1]);
    public static final int CREATED = 1;
    public static final int STARTED = 2;
    public static final int COMPLETED = 3;
    public static final int DELAYED = 4;

    public void addQuestItem(int ... nArray) {
        for (int n : nArray) {
            if (n == 0) continue;
            ItemTemplate itemTemplate = null;
            itemTemplate = ItemHolder.getInstance().getTemplate(n);
            if (this.YU.contains(n)) {
                _log.warn("Item " + itemTemplate + " multiple times in quest drop in " + this.getName());
            }
            this.YU.add(n);
        }
    }

    public int[] getItems() {
        return this.YU.toArray();
    }

    public boolean isQuestItem(int n) {
        return this.YU.contains(n);
    }

    public static void updateQuestInDb(QuestState questState) {
        Quest.updateQuestVarInDb(questState, "<state>", questState.getStateName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateQuestVarInDb(QuestState questState, String string, String string2) {
        Player player = questState.getPlayer();
        if (player == null) {
            return;
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO `character_quests` (`char_id`,`name`,`var`,`value`) VALUES (?,?,?,?)");
            preparedStatement.setInt(1, questState.getPlayer().getObjectId());
            preparedStatement.setString(2, questState.getQuest().getName());
            preparedStatement.setString(3, string);
            preparedStatement.setString(4, string2);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                _log.error("could not insert char quest:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void deleteQuestInDb(QuestState questState) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM `character_quests` WHERE `char_id`=? AND `name`=?");
            preparedStatement.setInt(1, questState.getPlayer().getObjectId());
            preparedStatement.setString(2, questState.getQuest().getName());
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                _log.error("could not delete char quest:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void deleteQuestVarInDb(QuestState questState, String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM `character_quests` WHERE `char_id`=? AND `name`=? AND `var`=?");
            preparedStatement.setInt(1, questState.getPlayer().getObjectId());
            preparedStatement.setString(2, questState.getQuest().getName());
            preparedStatement.setString(3, string);
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                _log.error("could not delete char quest:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void restoreQuestStates(Player player) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatement2 = null;
        ResultSet resultSet = null;
        try {
            Object object;
            String string;
            String string2;
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement2 = connection.prepareStatement("DELETE FROM `character_quests` WHERE `char_id`=? AND `name`=?");
            preparedStatement = connection.prepareStatement("SELECT `name`,`value` FROM `character_quests` WHERE `char_id`=? AND `var`=?");
            preparedStatement.setInt(1, player.getObjectId());
            preparedStatement.setString(2, "<state>");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                string2 = resultSet.getString("name");
                string = resultSet.getString("value");
                if (string.equalsIgnoreCase("Start")) {
                    preparedStatement2.setInt(1, player.getObjectId());
                    preparedStatement2.setString(2, string2);
                    preparedStatement2.executeUpdate();
                    continue;
                }
                object = QuestManager.getQuest(string2);
                if (object == null) {
                    if (Config.DONTLOADQUEST || ArrayUtils.contains(Config.IGNORE_QUESTS, ((Quest)object).getQuestIntId())) continue;
                    _log.warn("Unknown quest " + string2 + " for player " + player.getName());
                    continue;
                }
                new QuestState((Quest)object, player, Quest.getStateId(string));
            }
            DbUtils.close(preparedStatement, resultSet);
            preparedStatement = connection.prepareStatement("SELECT `name`,`var`,`value` FROM `character_quests` WHERE `char_id`=?");
            preparedStatement.setInt(1, player.getObjectId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                string2 = resultSet.getString("name");
                string = resultSet.getString("var");
                object = resultSet.getString("value");
                QuestState questState = player.getQuestState(string2);
                if (questState == null) continue;
                if (string.equals("cond") && Integer.parseInt((String)object) < 0) {
                    object = String.valueOf(Integer.parseInt((String)object) | 1);
                }
                questState.set(string, (String)object, false);
            }
        }
        catch (Exception exception) {
            try {
                _log.error("could not insert char quest:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(preparedStatement2);
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(preparedStatement2);
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(preparedStatement2);
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    public static String getStateName(int n) {
        switch (n) {
            case 1: {
                return "Start";
            }
            case 2: {
                return "Started";
            }
            case 3: {
                return "Completed";
            }
            case 4: {
                return "Delayed";
            }
        }
        return "Start";
    }

    public static int getStateId(String string) {
        if (string.equalsIgnoreCase("Start")) {
            return 1;
        }
        if (string.equalsIgnoreCase("Started")) {
            return 2;
        }
        if (string.equalsIgnoreCase("Completed")) {
            return 3;
        }
        if (string.equalsIgnoreCase("Delayed")) {
            return 4;
        }
        return 1;
    }

    public Quest(int n) {
        this._party = n;
        QuestManager.addQuest(this);
    }

    public List<QuestNpcLogInfo> getNpcLogList(int n) {
        return this.YV.get(n);
    }

    public void addAttackId(int ... nArray) {
        for (int n : nArray) {
            this.addEventId(n, QuestEventType.ATTACKED_WITH_QUEST);
        }
    }

    public NpcTemplate addEventId(int n, QuestEventType questEventType) {
        try {
            NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
            if (npcTemplate != null) {
                npcTemplate.addQuestEvent(questEventType, this);
            }
            return npcTemplate;
        }
        catch (Exception exception) {
            _log.error("", exception);
            return null;
        }
    }

    public void addKillId(int ... nArray) {
        for (int n : nArray) {
            this.addEventId(n, QuestEventType.MOB_KILLED_WITH_QUEST);
        }
    }

    public void addKillNpcWithLog(int n, String string, int n2, int ... nArray) {
        List<QuestNpcLogInfo> list;
        if (nArray.length == 0) {
            throw new IllegalArgumentException("Npc list cant be empty!");
        }
        this.addKillId(nArray);
        if (this.YV.isEmpty()) {
            this.YV = new TIntObjectHashMap(5);
        }
        if ((list = this.YV.get(n)) == null) {
            list = new ArrayList<QuestNpcLogInfo>(5);
            this.YV.put(n, list);
        }
        list.add(new QuestNpcLogInfo(nArray, string, n2));
    }

    public boolean updateKill(NpcInstance npcInstance, QuestState questState) {
        Player player = questState.getPlayer();
        if (player == null) {
            return false;
        }
        List<QuestNpcLogInfo> list = this.getNpcLogList(questState.getCond());
        if (list == null) {
            return false;
        }
        boolean bl = true;
        boolean bl2 = false;
        for (QuestNpcLogInfo questNpcLogInfo : list) {
            int n = questState.getInt(questNpcLogInfo.getVarName());
            if (!bl2 && ArrayUtils.contains(questNpcLogInfo.getNpcIds(), npcInstance.getNpcId())) {
                bl2 = true;
                if (n < questNpcLogInfo.getMaxCount()) {
                    questState.set(questNpcLogInfo.getVarName(), ++n);
                    player.sendPacket((IStaticPacket)new ExQuestNpcLogList(questState));
                }
            }
            if (n == questNpcLogInfo.getMaxCount()) continue;
            bl = false;
        }
        return bl;
    }

    public void addKillId(Collection<Integer> collection) {
        for (int n : collection) {
            this.addKillId(n);
        }
    }

    public NpcTemplate addSkillUseId(int n) {
        return this.addEventId(n, QuestEventType.MOB_TARGETED_BY_SKILL);
    }

    public void addStartNpc(int ... nArray) {
        for (int n : nArray) {
            this.addStartNpc(n);
        }
    }

    public NpcTemplate addStartNpc(int n) {
        this.addTalkId(n);
        return this.addEventId(n, QuestEventType.QUEST_START);
    }

    public void addFirstTalkId(int ... nArray) {
        for (int n : nArray) {
            this.addEventId(n, QuestEventType.NPC_FIRST_TALK);
        }
    }

    public void addTalkId(int ... nArray) {
        for (int n : nArray) {
            this.addEventId(n, QuestEventType.QUEST_TALK);
        }
    }

    public void addTalkId(Collection<Integer> collection) {
        for (int n : collection) {
            this.addTalkId(n);
        }
    }

    public String getDescr(Player player) {
        String string;
        QuestState questState = player.getQuestState(this);
        String string2 = string = this._descr != null ? this._descr : new CustomMessage("q." + this._questId, player, new Object[0]).toString();
        if (questState == null || questState.isCreated() && questState.isNowAvailable()) {
            return string;
        }
        if (questState.isCompleted() || !questState.isNowAvailable()) {
            return new CustomMessage("quest.Done", player, string).toString();
        }
        return new CustomMessage("quest.InProgress", player, string).toString();
    }

    public String getName() {
        return this._name;
    }

    public int getQuestIntId() {
        return this._questId;
    }

    public int getParty() {
        return this._party;
    }

    public QuestState newQuestState(Player player, int n) {
        QuestState questState = new QuestState(this, player, n);
        Quest.updateQuestInDb(questState);
        return questState;
    }

    public QuestState newQuestStateAndNotSave(Player player, int n) {
        return new QuestState(this, player, n);
    }

    public void notifyAttack(NpcInstance npcInstance, QuestState questState) {
        String string = null;
        try {
            string = this.onAttack(npcInstance, questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return;
        }
        this.b(npcInstance, questState.getPlayer(), string);
    }

    public void notifyDeath(Creature creature, Creature creature2, QuestState questState) {
        String string = null;
        try {
            string = this.onDeath(creature, creature2, questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return;
        }
        this.b(null, questState.getPlayer(), string);
    }

    public void notifyEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = null;
        try {
            string2 = this.onEvent(string, questState, npcInstance);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return;
        }
        this.b(npcInstance, questState.getPlayer(), string2);
    }

    public void notifyKill(NpcInstance npcInstance, QuestState questState) {
        String string = null;
        try {
            string = this.onKill(npcInstance, questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return;
        }
        this.b(npcInstance, questState.getPlayer(), string);
    }

    public void notifyKill(Player player, QuestState questState) {
        String string = null;
        try {
            string = this.onKill(player, questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return;
        }
        this.b(null, questState.getPlayer(), string);
    }

    public final boolean notifyFirstTalk(NpcInstance npcInstance, Player player) {
        String string = null;
        try {
            string = this.onFirstTalk(npcInstance, player);
        }
        catch (Exception exception) {
            this.a(player, exception);
            return true;
        }
        return this.a(npcInstance, player, string, true);
    }

    public boolean notifyTalk(NpcInstance npcInstance, QuestState questState) {
        String string = null;
        try {
            string = this.onTalk(npcInstance, questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return true;
        }
        return this.b(npcInstance, questState.getPlayer(), string);
    }

    public boolean notifySkillUse(NpcInstance npcInstance, Skill skill, QuestState questState) {
        String string = null;
        try {
            string = this.onSkillUse(npcInstance, skill, questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
            return true;
        }
        return this.b(npcInstance, questState.getPlayer(), string);
    }

    public void notifyCreate(QuestState questState) {
        try {
            this.onCreate(questState);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
        }
    }

    public void notifyOlympiadResult(QuestState questState, CompetitionType competitionType, boolean bl) {
        try {
            this.onOlympiadResult(questState, competitionType, bl);
        }
        catch (Exception exception) {
            this.a(questState.getPlayer(), exception);
        }
    }

    public void onOlympiadResult(QuestState questState, CompetitionType competitionType, boolean bl) {
    }

    public void onCreate(QuestState questState) {
    }

    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        return null;
    }

    public String onDeath(Creature creature, Creature creature2, QuestState questState) {
        return null;
    }

    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        return null;
    }

    public String onKill(NpcInstance npcInstance, QuestState questState) {
        return null;
    }

    public String onKill(Player player, QuestState questState) {
        return null;
    }

    public String onFirstTalk(NpcInstance npcInstance, Player player) {
        return null;
    }

    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        return null;
    }

    public String onSkillUse(NpcInstance npcInstance, Skill skill, QuestState questState) {
        return null;
    }

    public void onAbort(QuestState questState) {
    }

    public boolean canAbortByPacket() {
        return true;
    }

    private void a(Player player, Throwable throwable) {
        _log.error("", throwable);
        if (player != null && player.isGM()) {
            String string = "<html><body><title>Script error</title>" + LogUtils.dumpStack(throwable).replace("\n", "<br>") + "</body></html>";
            this.b(null, player, string);
        }
    }

    protected void showHtmlFile(Player player, String string, boolean bl) {
        this.showHtmlFile(player, string, bl, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    protected void showHtmlFile(Player player, String string, boolean bl, Object ... objectArray) {
        if (player == null) {
            return;
        }
        GameObject gameObject = player.getTarget();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(gameObject == null ? 5 : gameObject.getObjectId());
        npcHtmlMessage.setFile("quests/" + this.getClass().getSimpleName() + "/" + string);
        if (objectArray.length % 2 == 0) {
            for (int i = 0; i < objectArray.length; i += 2) {
                npcHtmlMessage.replace(String.valueOf(objectArray[i]), String.valueOf(objectArray[i + 1]));
            }
        }
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    protected void showSimpleHtmFile(Player player, String string) {
        if (player == null) {
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        npcHtmlMessage.setFile(string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private boolean b(NpcInstance npcInstance, Player player, String string) {
        return this.a(npcInstance, player, string, false);
    }

    private boolean a(NpcInstance npcInstance, Player player, String string, boolean bl) {
        boolean bl2 = this.Z(player);
        if (bl) {
            bl2 = false;
        }
        if (string == null) {
            return true;
        }
        if (string.isEmpty()) {
            return false;
        }
        if (string.startsWith("no_quest") || string.equalsIgnoreCase("noquest") || string.equalsIgnoreCase(NO_QUEST_DIALOG)) {
            this.showSimpleHtmFile(player, "no-quest.htm");
        } else if (string.equalsIgnoreCase("completed")) {
            this.showSimpleHtmFile(player, "completed-quest.htm");
        } else if (string.endsWith(".htm")) {
            this.showHtmlFile(player, string, bl2);
        } else {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(npcInstance == null ? 5 : npcInstance.getObjectId());
            npcHtmlMessage.setHtml(string);
            player.sendPacket((IStaticPacket)npcHtmlMessage);
        }
        return true;
    }

    private boolean Z(Player player) {
        QuestState questState = player.getQuestState(this.getName());
        if (questState != null && questState.getState() != 1) {
            return false;
        }
        return this.isVisible();
    }

    void pauseQuestTimers(QuestState questState) {
        if (questState.getTimers().isEmpty()) {
            return;
        }
        for (QuestTimer questTimer : questState.getTimers().values()) {
            questTimer.setQuestState(null);
            questTimer.pause();
        }
        this.YT.put(questState.getPlayer().getObjectId(), questState.getTimers());
    }

    void resumeQuestTimers(QuestState questState) {
        Map<String, QuestTimer> map = this.YT.remove(questState.getPlayer().getObjectId());
        if (map == null) {
            return;
        }
        questState.getTimers().putAll(map);
        for (QuestTimer questTimer : questState.getTimers().values()) {
            questTimer.setQuestState(questState);
            questTimer.start();
        }
    }

    protected String str(long l) {
        return String.valueOf(l);
    }

    public NpcInstance addSpawn(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return this.addSpawn(n, new Location(n2, n3, n4, n5), n6, n7);
    }

    public NpcInstance addSpawn(int n, Location location, int n2, int n3) {
        NpcInstance npcInstance = Functions.spawn(n2 > 50 ? Location.findPointToStay(location, 0, n2, ReflectionManager.DEFAULT.getGeoIndex()) : location, n);
        if (n3 > 0 && npcInstance != null) {
            ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(npcInstance), n3);
        }
        return npcInstance;
    }

    public static NpcInstance addSpawnToInstance(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return Quest.addSpawnToInstance(n, new Location(n2, n3, n4, n5), n6, n7);
    }

    public static NpcInstance addSpawnToInstance(int n, Location location, int n2, int n3) {
        try {
            NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
            if (npcTemplate != null) {
                NpcInstance npcInstance = NpcHolder.getInstance().getTemplate(n).getNewInstance();
                npcInstance.setReflection(n3);
                npcInstance.setSpawnedLoc(n2 > 50 ? Location.findPointToStay(location, 50, n2, npcInstance.getGeoIndex()) : location);
                npcInstance.spawnMe(npcInstance.getSpawnedLoc());
                return npcInstance;
            }
        }
        catch (Exception exception) {
            _log.warn("Could not spawn Npc " + n);
        }
        return null;
    }

    public boolean isVisible() {
        return true;
    }

    public QuestRates getRates() {
        QuestRates questRates = Config.QUEST_RATES.get(this.getQuestIntId());
        if (questRates == null) {
            questRates = new QuestRates(this.getQuestIntId());
            Config.QUEST_RATES.put(this.getQuestIntId(), questRates);
        }
        return questRates;
    }

    private QuestState j(Player player, int n) {
        QuestState questState = player.getQuestState(this.getName());
        if (questState != null && (n < 0 || questState.getCond() == n)) {
            return questState;
        }
        return null;
    }

    public List<QuestState> getPartyMembersWithQuest(Player player, int n) {
        LinkedList<QuestState> linkedList = new LinkedList<QuestState>();
        Party party = player.getParty();
        if (party != null) {
            for (Player player2 : party.getPartyMembers()) {
                QuestState questState;
                if (!player.isInRange(player2, (long)Config.ALT_PARTY_DISTRIBUTION_RANGE) || (questState = this.j(player2, n)) == null) continue;
                linkedList.add(questState);
            }
        } else {
            QuestState questState = this.j(player, n);
            if (questState != null) {
                linkedList.add(questState);
            }
        }
        return linkedList;
    }

    public QuestState getRandomPartyMemberWithQuest(Player player, int n) {
        List<QuestState> list = this.getPartyMembersWithQuest(player, n);
        if (list.size() > 0) {
            return list.get(Rnd.get(list.size()));
        }
        return null;
    }

    public class DeSpawnScheduleTimerTask
    extends RunnableImpl {
        NpcInstance _npc = null;

        public DeSpawnScheduleTimerTask(NpcInstance npcInstance) {
            this._npc = npcInstance;
        }

        @Override
        public void runImpl() throws Exception {
            if (this._npc != null) {
                if (this._npc.getSpawn() != null) {
                    this._npc.getSpawn().deleteAll();
                } else {
                    this._npc.deleteMe();
                }
            }
        }
    }
}

