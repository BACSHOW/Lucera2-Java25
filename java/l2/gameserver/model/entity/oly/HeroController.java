/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.HeroDiary;
import l2.gameserver.model.entity.oly.CompetitionController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeroController {
    private static final Logger _log = LoggerFactory.getLogger(HeroController.class);
    public static final int[] HERO_WEAPONS = new int[]{6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621};
    private static final String RG = "SELECT  `oly_heroes`.`char_id` AS `char_id`, `oly_nobles`.`char_name` AS `name`, `oly_nobles`.`class_id` AS `class_id`, `oly_heroes`.`count` AS `count`, `oly_heroes`.`played` AS `played`, `oly_heroes`.`active` AS `active`, `oly_heroes`.`message` AS `message` FROM    `oly_heroes`,`oly_nobles` WHERE   `oly_heroes`.`char_id` = `oly_nobles`.`char_id`";
    private static final String RH = "REPLACE INTO `oly_heroes` (`char_id`, `count`, `played`, `active`, `message`) VALUES (?, ?, ?, ?, ?)";
    private static final String RI = "DELETE FROM `oly_heroes` WHERE `char_id` = ?";
    private static HeroController RJ;
    private ArrayList<HeroRecord> RK = new ArrayList();
    private ArrayList<HeroRecord> RL = new ArrayList();
    private static Map<Integer, List<HeroDiary>> RM;
    private static Map<Integer, String> RN;

    public static HeroController getInstance() {
        if (RJ == null) {
            RJ = new HeroController();
        }
        return RJ;
    }

    private HeroController() {
        RM = new ConcurrentHashMap<Integer, List<HeroDiary>>();
        RN = new ConcurrentHashMap<Integer, String>();
        this.cQ();
    }

    private synchronized Collection<NoblesController.NobleRecord> cP() {
        _log.info("HeroController: Calculating heroes contenders.");
        HashMap<ClassId, NoblesController.NobleRecord> hashMap = new HashMap<ClassId, NoblesController.NobleRecord>();
        for (NoblesController.NobleRecord nobleRecord : NoblesController.getInstance().getNoblesRecords()) {
            try {
                if (nobleRecord.comp_done < Config.OLY_MIN_HERO_COMPS || nobleRecord.comp_win < Config.OLY_MIN_HERO_WIN) continue;
                ClassId classId = null;
                for (ClassId classId2 : ClassId.values()) {
                    if (classId2.getId() != nobleRecord.class_id || classId2.level() != 3) continue;
                    classId = classId2;
                }
                if (classId == null) {
                    _log.warn("HeroController: Not third or null ClassID for character '" + nobleRecord.char_name + "'");
                    continue;
                }
                if (hashMap.containsKey((Object)classId)) {
                    NoblesController.NobleRecord nobleRecord2 = (NoblesController.NobleRecord)hashMap.get((Object)classId);
                    if (nobleRecord.points_current <= nobleRecord2.points_current && (nobleRecord.points_current != nobleRecord2.points_current || nobleRecord.comp_win <= nobleRecord2.comp_win) && (nobleRecord.points_current != nobleRecord2.points_current || nobleRecord.comp_win != nobleRecord2.comp_win || nobleRecord.comp_done <= nobleRecord2.comp_done)) continue;
                    hashMap.put(classId, nobleRecord);
                    continue;
                }
                hashMap.put(classId, nobleRecord);
            }
            catch (Exception exception) {
                _log.warn("HeroController: Exception while claculating new heroes", exception);
            }
        }
        for (NoblesController.NobleRecord nobleRecord : hashMap.values()) {
            Log.add(String.format("HeroController: %s(%d) pretended to be a hero. points_current = %d", nobleRecord.char_name, nobleRecord.char_id, nobleRecord.points_current), "olympiad");
        }
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(hashMap.values());
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cQ() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(RG);
            while (resultSet.next()) {
                int n = resultSet.getInt("char_id");
                String string = resultSet.getString("name");
                int n2 = resultSet.getInt("class_id");
                int n3 = resultSet.getInt("count");
                boolean bl = resultSet.getInt("played") != 0;
                boolean bl2 = resultSet.getInt("active") != 0;
                String string2 = resultSet.getString("message");
                HeroRecord heroRecord = new HeroRecord(n, string, n2, n3, bl2, bl, string2);
                if (bl) {
                    this.RK.add(heroRecord);
                }
                this.RL.add(heroRecord);
            }
            this.cS();
        }
        catch (Exception exception) {
            try {
                _log.warn("Exception while loading heroes", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, statement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        DbUtils.closeQuietly(connection, statement, resultSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveHeroes() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(RH);
            for (HeroRecord heroRecord : this.RL) {
                preparedStatement.setInt(1, heroRecord.char_id);
                preparedStatement.setInt(2, heroRecord.count);
                preparedStatement.setInt(3, heroRecord.played ? 1 : 0);
                preparedStatement.setInt(4, heroRecord.active ? 1 : 0);
                preparedStatement.setString(5, heroRecord.message);
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("Exception while saving heroes", exception);
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
    public synchronized void removeHero(Player player) {
        HeroRecord heroRecord = null;
        for (HeroRecord object2 : this.RL) {
            if (object2.char_id != player.getObjectId()) continue;
            heroRecord = object2;
            break;
        }
        if (heroRecord == null) {
            return;
        }
        Connection object3 = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            object3 = DatabaseFactory.getInstance().getConnection();
            PreparedStatement preparedStatement = object3.prepareStatement(RI);
            preparedStatement.setInt(1, heroRecord.char_id);
            preparedStatement.executeUpdate();
            this.RL.remove(heroRecord);
            DbUtils.closeQuietly((Connection)object3, preparedStatement, resultSet);
        }
        catch (SQLException sQLException) {
            _log.warn("HeroController: Can't remove Hero ", sQLException);
        }
        finally {
            DbUtils.closeQuietly((Connection)object3, statement, resultSet);
        }
    }

    private void cR() {
        _log.info("HeroController: Clearing previus season heroes.");
        mysql.set("UPDATE `oly_heroes` SET `played` = 0, `active` = 0");
        if (!this.RK.isEmpty()) {
            for (HeroRecord heroRecord : this.RK) {
                if (!heroRecord.active) continue;
                Player player = GameObjectsStorage.getPlayer(heroRecord.char_id);
                if (player != null) {
                    player.getInventory().unEquipItemInBodySlot(256);
                    player.getInventory().unEquipItemInBodySlot(128);
                    player.getInventory().unEquipItemInBodySlot(16384);
                    player.getInventory().unEquipItemInBodySlot(65536);
                    player.getInventory().unEquipItemInBodySlot(524288);
                    player.getInventory().unEquipItemInBodySlot(262144);
                    for (ItemInstance itemInstance : player.getInventory().getItems()) {
                        if (itemInstance == null || !itemInstance.isHeroWeapon()) continue;
                        player.getInventory().destroyItem(itemInstance);
                    }
                    for (ItemInstance itemInstance : player.getWarehouse().getItems()) {
                        if (itemInstance == null || itemInstance.isEquipable() || !itemInstance.isHeroWeapon()) continue;
                        player.getWarehouse().destroyItem(itemInstance);
                    }
                    player.unsetVar("CustomHeroEndTime");
                    player.setHero(false);
                    player.updatePledgeClass();
                    player.broadcastUserInfo(true);
                    HeroController.S(player);
                    HeroController.removeSkills(player, true);
                }
                heroRecord.played = false;
                heroRecord.active = false;
            }
        }
        this.saveHeroes();
        this.RK.clear();
    }

    public synchronized void ComputeNewHeroNobleses() {
        _log.info("HeroController: Computing new heroes.");
        try {
            NoblesController.getInstance().SaveNobleses();
            this.saveHeroes();
            Collection<NoblesController.NobleRecord> collection = this.cP();
            this.cR();
            for (NoblesController.NobleRecord nobleRecord : collection) {
                HeroRecord heroRecord = null;
                for (HeroRecord heroRecord2 : this.RL) {
                    if (nobleRecord.char_id != heroRecord2.char_id) continue;
                    heroRecord = heroRecord2;
                }
                if (heroRecord == null) {
                    heroRecord = new HeroRecord(nobleRecord.char_id, nobleRecord.char_name, nobleRecord.class_id, 0, false, true, "");
                    this.RL.add(heroRecord);
                }
                ++heroRecord.count;
                heroRecord.played = true;
                this.RK.add(heroRecord);
            }
            this.saveHeroes();
            NoblesController.getInstance().TransactNewSeason();
            NoblesController.getInstance().ComputeRanks();
            NoblesController.getInstance().SaveNobleses();
            this.cS();
        }
        catch (Exception exception) {
            _log.warn("HeroController: Can't compute heroes.", exception);
        }
    }

    private void cS() {
        for (HeroRecord heroRecord : this.RK) {
            if (heroRecord == null) continue;
            Map.Entry<Clan, Alliance> entry = ClanTable.getInstance().getClanAndAllianceByCharId(heroRecord.char_id);
            if (entry.getKey() != null) {
                heroRecord.clan_name = entry.getKey().getName();
                heroRecord.clan_crest = entry.getKey().getCrestId();
            } else {
                heroRecord.clan_name = "";
                heroRecord.clan_crest = 0;
            }
            if (entry.getValue() != null) {
                heroRecord.ally_name = entry.getValue().getAllyName();
                heroRecord.ally_crest = entry.getValue().getAllyCrestId();
                continue;
            }
            heroRecord.ally_name = "";
            heroRecord.ally_crest = 0;
        }
    }

    public Collection<HeroRecord> getCurrentHeroes() {
        return this.RK;
    }

    public boolean isCurrentHero(Player player) {
        if (player == null) {
            return false;
        }
        if (this.RK.isEmpty()) {
            return false;
        }
        return this.isCurrentHero(player.getObjectId());
    }

    public boolean isInactiveHero(Player player) {
        if (player == null) {
            return false;
        }
        return this.isInactiveHero(player.getObjectId());
    }

    public void activateHero(Player player) {
        if (player == null) {
            return;
        }
        if (this.RK.isEmpty()) {
            return;
        }
        for (HeroRecord heroRecord : this.RK) {
            if (heroRecord.char_id != player.getObjectId() || !heroRecord.played) continue;
            heroRecord.active = true;
            if (player.getBaseClassId() == player.getActiveClassId()) {
                HeroController.addSkills(player);
                player.sendPacket((IStaticPacket)new SkillList(player));
            }
            player.setHero(true);
            player.unsetVar("CustomHeroEndTime");
            player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
            player.updatePledgeClass();
            player.getPlayer().sendUserInfo(true);
            if (player.getClan() != null) {
                Clan clan = player.getClan();
                if (clan.getLevel() >= Config.MIN_CLAN_LEVEL_FOR_REPUTATION && Config.ADD_CLAN_REPUTATION_POINT_ON_HERO_GAIN) {
                    int n = player.getClan().incReputation(Config.AMOUNT_CLAN_REPUTATION_POINT_ON_HERO_GAIN, true, "Hero:activateHero:" + player);
                    clan.broadcastToOtherOnlineMembers(new SystemMessage(1776).addString(player.getName()).addNumber(n), player);
                }
                clan.setCustomPoints(clan.getCustomPoints() + Config.EVENT_CLAN_HERO_POINTS);
                if (Config.SERVICES_TOP_CLANS_STATISTIC_ANNOUNCE) {
                    Announcements.getInstance().announceByCustomMessage("scripts.services.CustomClanPointsHero", new String[]{String.valueOf(clan.getName()), String.valueOf(player.getName()), String.valueOf(Config.EVENT_CLAN_HERO_POINTS)});
                }
            }
            player.broadcastUserInfo(true);
            this.saveHeroes();
            if (ArrayUtils.isEmpty(Config.OLY_HERO_CUSTOM_REWARD_AMOUNT) || Config.OLY_HERO_CUSTOM_REWARD_AMOUNT.length != Config.OLY_HERO_CUSTOM_REWARD_ID.length || Config.OLY_HERO_CUSTOM_REWARD_CHANCE.length != Config.OLY_HERO_CUSTOM_REWARD_ID.length) continue;
            for (int i = 0; i < Config.OLY_HERO_CUSTOM_REWARD_ID.length; ++i) {
                if (Config.OLY_HERO_CUSTOM_REWARD_ID[i] == 0 || Config.OLY_HERO_CUSTOM_REWARD_AMOUNT[i] <= 0 || !Rnd.chance(Config.OLY_HERO_CUSTOM_REWARD_CHANCE[i])) continue;
                ItemFunctions.addItem((Playable)player, Config.OLY_HERO_CUSTOM_REWARD_ID[i], (long)Config.OLY_HERO_CUSTOM_REWARD_AMOUNT[i], true);
            }
        }
    }

    public boolean isCurrentHero(int n) {
        if (this.RK.isEmpty()) {
            return false;
        }
        for (HeroRecord heroRecord : this.RK) {
            if (heroRecord.char_id != n) continue;
            return heroRecord.active && heroRecord.played;
        }
        return false;
    }

    public boolean isInactiveHero(int n) {
        if (this.RK.isEmpty()) {
            return false;
        }
        for (HeroRecord heroRecord : this.RK) {
            if (heroRecord.char_id != n) continue;
            return heroRecord.played && !heroRecord.active;
        }
        return false;
    }

    public static void addSkills(Player player) {
        HeroController.addSkills(player, false);
    }

    public static void addSkills(Player player, boolean bl) {
        for (SkillLearn skillLearn : SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.HERO)) {
            Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill == null || player.getSkillLevel(skill.getId()) >= skill.getLevel()) continue;
            player.addSkill(skill, false);
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
    }

    public static void removeSkills(Player player) {
        HeroController.removeSkills(player, false);
    }

    public static void removeSkills(Player player, boolean bl) {
        for (SkillLearn skillLearn : SkillAcquireHolder.getInstance().getAllSkillLearn(player, player.getClassId(), AcquireType.HERO)) {
            Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill == null) continue;
            player.removeSkill(skill, false);
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
    }

    public void showHistory(Player player, int n, int n2) {
        HeroRecord heroRecord = null;
        for (HeroRecord object22 : this.getCurrentHeroes()) {
            if (!object22.active || !object22.played || object22.class_id != n) continue;
            heroRecord = object22;
        }
        if (heroRecord == null) {
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        npcHtmlMessage.setFile("oly/monument_hero_history.htm");
        npcHtmlMessage.replace("%title%", StringHolder.getInstance().getNotNull(player, "hero.history"));
        Collection<CompetitionController.CompetitionResults> collection = heroRecord.getCompetitions();
        CompetitionController.CompetitionResults[] competitionResultsArray = collection.toArray(new CompetitionController.CompetitionResults[collection.size()]);
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        for (CompetitionController.CompetitionResults competitionResults : competitionResultsArray) {
            if (competitionResults.result > 0) {
                ++n3;
                continue;
            }
            if (competitionResults.result < 0) {
                ++n4;
                continue;
            }
            if (competitionResults.result != 0) continue;
            ++n5;
        }
        npcHtmlMessage.replace("%wins%", String.valueOf(n3));
        npcHtmlMessage.replace("%ties%", String.valueOf(n5));
        npcHtmlMessage.replace("%losses%", String.valueOf(n4));
        int n6 = 15 * (n2 - 1);
        int n7 = 15 * n2;
        MutableInt mutableInt = new MutableInt(0);
        MutableInt mutableInt2 = new MutableInt(0);
        MutableInt mutableInt3 = new MutableInt(0);
        StringBuilder stringBuilder = new StringBuilder(500);
        for (int i = 0; i < competitionResultsArray.length; ++i) {
            CompetitionController.CompetitionResults competitionResults = competitionResultsArray[i];
            if (competitionResults.result > 0) {
                mutableInt.increment();
            } else if (competitionResults.result < 0) {
                mutableInt2.increment();
            } else if (competitionResults.result == 0) {
                mutableInt3.increment();
            }
            if (i < n6) continue;
            if (i >= n7) break;
            stringBuilder.append("<tr><td>");
            stringBuilder.append(competitionResults.toString(player, mutableInt, mutableInt2, mutableInt3));
            stringBuilder.append("</td></tr");
        }
        if (n6 > 0) {
            npcHtmlMessage.replace("%buttprev%", "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
            npcHtmlMessage.replace("%prev_bypass%", "_match?class=" + n + "&page=" + (n2 - 1));
        } else {
            npcHtmlMessage.replace("%buttprev%", "");
        }
        if (competitionResultsArray.length > n7) {
            npcHtmlMessage.replace("%buttnext%", "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
            npcHtmlMessage.replace("%prev_bypass%", "_match?class=" + n + "&page=" + (n2 + 1));
        } else {
            npcHtmlMessage.replace("%buttnext%", "");
        }
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadDiary(int n) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        block5: {
            ArrayList<HeroDiary> arrayList = new ArrayList<HeroDiary>();
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT * FROM  `heroes_diary` WHERE `charId`=? ORDER BY `time` ASC");
                preparedStatement.setInt(1, n);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    long l = resultSet.getLong("time");
                    int n2 = resultSet.getInt("action");
                    int n3 = resultSet.getInt("param");
                    HeroDiary heroDiary = new HeroDiary(n2, l, n3);
                    arrayList.add(heroDiary);
                }
                RM.put(n, arrayList);
                if (!Config.DEBUG) break block5;
                _log.info("Hero System: Loaded " + arrayList.size() + " diary entries for Hero(object id: #" + n + ")");
            }
            catch (SQLException sQLException) {
                try {
                    _log.warn("Hero System: Couldnt load Hero Diary for CharId: " + n, sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    public void showHeroDiary(Player player, int n, int n2) {
        HeroRecord heroRecord = null;
        for (HeroRecord object : this.getCurrentHeroes()) {
            if (!object.active || !object.played || object.class_id != n) continue;
            heroRecord = object;
        }
        if (heroRecord == null) {
            return;
        }
        List<HeroDiary> list = RM.get(heroRecord.char_id);
        if (list != null) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
            npcHtmlMessage.setFile("oly/monument_hero_info.htm");
            npcHtmlMessage.replace("%title%", StringHolder.getInstance().getNotNull(player, "hero.diary"));
            npcHtmlMessage.replace("%heroname%", heroRecord.name);
            npcHtmlMessage.replace("%message%", heroRecord.message);
            ArrayList arrayList = new ArrayList(list);
            Collections.reverse(arrayList);
            boolean bl = true;
            StringBuilder stringBuilder = new StringBuilder(500);
            int n3 = 0;
            int n4 = 0;
            for (int i = (n2 - 1) * 10; i < arrayList.size(); ++i) {
                n4 = i;
                HeroDiary heroDiary = (HeroDiary)arrayList.get(i);
                Map.Entry<String, String> entry = heroDiary.toString(player);
                stringBuilder.append("<tr><td>");
                if (bl) {
                    stringBuilder.append("<table width=270 bgcolor=\"131210\">");
                } else {
                    stringBuilder.append("<table width=270>");
                }
                stringBuilder.append("<tr><td width=270><font color=\"LEVEL\">" + entry.getKey() + "</font></td></tr>");
                stringBuilder.append("<tr><td width=270>" + entry.getValue() + "</td></tr>");
                stringBuilder.append("<tr><td>&nbsp;</td></tr></table>");
                stringBuilder.append("</td></tr>");
                boolean bl2 = bl = !bl;
                if (++n3 >= 10) break;
            }
            if (n4 < arrayList.size() - 1) {
                npcHtmlMessage.replace("%buttprev%", "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
                npcHtmlMessage.replace("%prev_bypass%", "_diary?class=" + n + "&page=" + (n2 + 1));
            } else {
                npcHtmlMessage.replace("%buttprev%", "");
            }
            if (n2 > 1) {
                npcHtmlMessage.replace("%buttnext%", "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
                npcHtmlMessage.replace("%next_bypass%", "_diary?class=" + n + "&page=" + (n2 - 1));
            } else {
                npcHtmlMessage.replace("%buttnext%", "");
            }
            npcHtmlMessage.replace("%list%", stringBuilder.toString());
            player.sendPacket((IStaticPacket)npcHtmlMessage);
        }
    }

    public void addHeroDiary(int n, int n2, int n3) {
        this.d(n, n2, n3);
        List<HeroDiary> list = RM.get(n);
        if (list != null) {
            list.add(new HeroDiary(n2, System.currentTimeMillis(), n3));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void d(int n, int n2, int n3) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `heroes_diary` (`charId`, `time`, `action`, `param`) values(?,?,?,?)");
            preparedStatement.setInt(1, n);
            preparedStatement.setLong(2, System.currentTimeMillis());
            preparedStatement.setInt(3, n2);
            preparedStatement.setInt(4, n3);
            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (SQLException sQLException) {
            try {
                _log.error("SQL exception while saving DiaryData.", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public void setHeroMessage(int n, String string) {
        HeroRecord heroRecord = null;
        for (HeroRecord heroRecord2 : this.getCurrentHeroes()) {
            if (!heroRecord2.active || !heroRecord2.played || heroRecord2.char_id != n) continue;
            heroRecord = heroRecord2;
        }
        heroRecord.message = Strings.stripSlashes(string);
    }

    public static boolean isHaveHeroWeapon(Player player) {
        for (int n : HERO_WEAPONS) {
            if (player.getInventory().getCountOf(n) <= 0L) continue;
            return true;
        }
        return false;
    }

    private static void S(Player player) {
        boolean bl = false;
        for (int n : HERO_WEAPONS) {
            while (player.getInventory().destroyItemByItemId(n, 1L)) {
                bl = true;
            }
            if (bl) {
                player.sendPacket((IStaticPacket)new SystemMessage(1726).addItemName(n));
            }
            bl = false;
        }
    }

    public static void checkHeroWeaponary(Player player) {
        if (!player.isHero()) {
            HeroController.S(player);
        }
    }

    public class HeroRecord {
        public int char_id;
        public int class_id;
        public int count;
        public boolean active;
        public boolean played;
        public String name;
        public String message;
        public String clan_name;
        public String ally_name;
        public int clan_crest;
        public int ally_crest;
        public Collection<CompetitionController.CompetitionResults> competitions;

        private HeroRecord(int n, String string, int n2, int n3, boolean bl, boolean bl2, String string2) {
            this.char_id = n;
            this.name = string;
            this.count = n3;
            this.class_id = n2;
            this.active = bl;
            this.played = bl2;
            this.message = string2;
        }

        public Collection<CompetitionController.CompetitionResults> getCompetitions() {
            if (this.competitions == null) {
                this.competitions = CompetitionController.getInstance().getCompetitionResults(this.char_id, OlyController.getInstance().getCurrentSeason() - 1);
            }
            return this.competitions;
        }
    }
}

