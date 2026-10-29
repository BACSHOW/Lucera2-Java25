/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.SiegeUtils;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanTable {
    private static final Logger _log = LoggerFactory.getLogger(ClanTable.class);
    private static ClanTable aqD;
    private final Map<Integer, Clan> aqE = new ConcurrentHashMap<Integer, Clan>();
    private final Map<Integer, Alliance> aqF = new ConcurrentHashMap<Integer, Alliance>();
    private Clan aqG;
    private static final List<Skill> aqH;

    public static ClanTable getInstance() {
        if (aqD == null) {
            new ClanTable();
        }
        return aqD;
    }

    public Clan[] getClans() {
        return this.aqE.values().toArray(new Clan[this.aqE.size()]);
    }

    public Alliance[] getAlliances() {
        return this.aqF.values().toArray(new Alliance[this.aqF.size()]);
    }

    private ClanTable() {
        aqD = this;
        this.restoreClans();
        this.restoreAllies();
        this.dw();
    }

    public Clan getClan(int n) {
        if (n <= 0) {
            return null;
        }
        if (Config.ALT_NPC_CLAN == n) {
            return this.aqG;
        }
        return this.aqE.get(n);
    }

    public String getClanName(int n) {
        Clan clan = this.getClan(n);
        return clan != null ? clan.getName() : "";
    }

    public Clan getClanByCharId(int n) {
        if (n <= 0) {
            return null;
        }
        for (Clan clan : this.getClans()) {
            if (clan == null || !clan.isAnyMember(n)) continue;
            return clan;
        }
        return null;
    }

    public Alliance getAlliance(int n) {
        if (n <= 0) {
            return null;
        }
        return this.aqF.get(n);
    }

    public Alliance getAllianceByCharId(int n) {
        if (n <= 0) {
            return null;
        }
        Clan clan = this.getClanByCharId(n);
        return clan == null ? null : clan.getAlliance();
    }

    public Map.Entry<Clan, Alliance> getClanAndAllianceByCharId(int n) {
        Player player = GameObjectsStorage.getPlayer(n);
        Clan clan = player != null ? player.getClan() : this.getClanByCharId(n);
        return new AbstractMap.SimpleEntry<Clan, Alliance>(clan, clan == null ? null : clan.getAlliance());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restoreClans() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `clan_id` FROM `clan_data`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                arrayList.add(resultSet.getInt("clan_id"));
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("Error while restoring clans!!! " + exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            int n = (Integer)iterator.next();
            Clan clan = Clan.restore(n);
            if (clan == null) {
                _log.warn("Error while restoring clanId: " + n);
                continue;
            }
            if (clan.getAllSize() <= 0) {
                _log.warn("membersCount = 0 for clanId: " + n);
                continue;
            }
            if (clan.getLeader() == null) {
                _log.warn("Not found leader for clanId: " + n);
                continue;
            }
            this.aqE.put(clan.getClanId(), clan);
            if (Config.ALT_NPC_CLAN <= 0) continue;
            this.aqG = this.aqE.get(Config.ALT_NPC_CLAN);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restoreAllies() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `ally_id` FROM `ally_data`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                arrayList.add(resultSet.getInt("ally_id"));
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("Error while restoring allies!!! " + exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            int n = (Integer)iterator.next();
            Alliance alliance = new Alliance(n);
            if (alliance.getMembersCount() <= 0) {
                _log.warn("membersCount = 0 for allyId: " + n);
                continue;
            }
            if (alliance.getLeader() == null) {
                _log.warn("Not found leader for allyId: " + n);
                continue;
            }
            this.aqF.put(alliance.getAllyId(), alliance);
        }
    }

    public Clan getClanByName(String string) {
        if (!Util.isMatchingRegexp(string, Config.CLAN_NAME_TEMPLATE)) {
            return null;
        }
        for (Clan clan : this.aqE.values()) {
            if (!clan.getName().equalsIgnoreCase(string)) continue;
            return clan;
        }
        return null;
    }

    public Alliance getAllyByName(String string) {
        if (!Util.isMatchingRegexp(string, Config.ALLY_NAME_TEMPLATE)) {
            return null;
        }
        for (Alliance alliance : this.aqF.values()) {
            if (!alliance.getAllyName().equalsIgnoreCase(string)) continue;
            return alliance;
        }
        return null;
    }

    public Clan createClan(Player player, String string) {
        if (this.getClanByName(string) == null) {
            UnitMember unitMember = new UnitMember(player);
            unitMember.setLeaderOf(0);
            Clan clan = new Clan(IdFactory.getInstance().getNextId());
            clan.setLevel(Config.CLAN_INIT_LEVEL);
            SubUnit subUnit = new SubUnit(clan, 0, unitMember, string);
            subUnit.addUnitMember(unitMember);
            clan.addSubUnit(subUnit, false);
            clan.store();
            player.setPledgeType(0);
            player.setClan(clan);
            player.setPowerGrade(6);
            unitMember.setPlayerInstance(player, false);
            this.aqE.put(clan.getClanId(), clan);
            if (Config.CLAN_REPUTATION_BONUS_ON_CREATE > 0) {
                clan.incReputation(Config.CLAN_REPUTATION_BONUS_ON_CREATE, false, "ClanReputationOnCreateBonusAdd");
            }
            if (Config.FULL_CLAN_SKILLS_ON_CREATE) {
                for (Skill skill : aqH) {
                    clan.addSkill(skill, true);
                    clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill)});
                }
            }
            return clan;
        }
        return null;
    }

    public void dissolveClan(Clan clan) {
        int n = clan.getLeaderId();
        Player player = clan.getLeader() != null ? clan.getLeader().getPlayer() : null;
        long l = System.currentTimeMillis();
        if (player != null) {
            SiegeUtils.removeSiegeSkills(player);
        }
        for (Player player2 : clan.getOnlineMembers(0)) {
            player2.setClan(null);
            player2.setTitle("");
            player2.sendPacket(PledgeShowMemberListDeleteAll.STATIC, Msg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS);
            player2.broadcastCharInfo();
            player2.setLeaveClanTime(l);
        }
        clan.flush();
        this.deleteClanFromDb(clan.getClanId(), n);
        this.aqE.remove(clan.getClanId());
        if (player != null) {
            player.sendPacket((IStaticPacket)Msg.CLAN_HAS_DISPERSED);
            player.setDeleteClanTime(l);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteClanFromDb(int n, int n2) {
        long l = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean bl = false;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `characters` SET `clanid`=0,`title`='',`pledge_type`=0,`pledge_rank`=0,`lvl_joined_academy`=0,`apprentice`=0,`leaveclan`=? WHERE `clanid`=?");
            preparedStatement.setLong(1, l / 1000L);
            preparedStatement.setInt(2, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("UPDATE `characters` SET `deleteclan`=? WHERE `obj_Id`=?");
            preparedStatement.setLong(1, l / 1000L);
            preparedStatement.setInt(2, n2);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `clan_data` WHERE `clan_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `clan_subpledges` WHERE `clan_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `clan_privs` WHERE `clan_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `siege_clans` WHERE `clan_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `siege_players` WHERE `clan_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `clan_skills` WHERE `clan_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            bl = true;
        }
        catch (Exception exception) {
            try {
                _log.warn("could not dissolve clan:" + exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
        if (bl) {
            CharacterVariablesDAO.getInstance().deleteVars(n);
        }
    }

    public Alliance createAlliance(Player player, String string) {
        Alliance alliance = null;
        if (this.getAllyByName(string) == null) {
            Clan clan = player.getClan();
            alliance = new Alliance(IdFactory.getInstance().getNextId(), string, clan);
            alliance.store();
            this.aqF.put(alliance.getAllyId(), alliance);
            player.getClan().setAllyId(alliance.getAllyId());
            for (Player player2 : player.getClan().getOnlineMembers(0)) {
                player2.broadcastCharInfo();
            }
        }
        return alliance;
    }

    public void dissolveAlly(Player player) {
        int n = player.getAllyId();
        for (Clan clan : player.getAlliance().getMembers()) {
            clan.setAllyId(0);
            clan.broadcastClanStatus(false, true, false);
            clan.broadcastToOnlineMembers(Msg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
            clan.setLeavedAlly();
        }
        this.deleteAllyFromDb(n);
        this.aqF.remove(n);
        player.sendPacket((IStaticPacket)Msg.THE_ALLIANCE_HAS_BEEN_DISSOLVED);
        player.getClan().setDissolvedAlly();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteAllyFromDb(int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `ally_id`=0 WHERE `ally_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM `ally_data` WHERE `ally_id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("could not dissolve clan:" + exception);
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
    public void startClanWar(Clan clan, Clan clan2) {
        clan.setEnemyClan(clan2);
        clan2.setAttackerClan(clan);
        clan.broadcastClanStatus(false, false, true);
        clan2.broadcastClanStatus(false, false, true);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO `clan_wars` (`clan1`, `clan2`) VALUES(?,?)");
            preparedStatement.setInt(1, clan.getClanId());
            preparedStatement.setInt(2, clan2.getClanId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("could not store clan war data:" + exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
        clan.broadcastToOnlineMembers(new SystemMessage(1562).addString(clan2.getName()));
        clan2.broadcastToOnlineMembers(new SystemMessage(1561).addString(clan.getName()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopClanWar(Clan clan, Clan clan2) {
        clan.deleteEnemyClan(clan2);
        clan2.deleteAttackerClan(clan);
        clan.broadcastClanStatus(false, false, true);
        clan2.broadcastClanStatus(false, false, true);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM `clan_wars` WHERE `clan1`=? AND `clan2`=?");
            preparedStatement.setInt(1, clan.getClanId());
            preparedStatement.setInt(2, clan2.getClanId());
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("could not delete war data:" + exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
        clan.broadcastToOnlineMembers(new SystemMessage(1567).addString(clan2.getName()));
        clan2.broadcastToOnlineMembers(new SystemMessage(1566).addString(clan.getName()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dw() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `clan1`, `clan2` FROM `clan_wars`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Clan clan = this.getClan(resultSet.getInt("clan1"));
                Clan clan2 = this.getClan(resultSet.getInt("clan2"));
                if (clan == null || clan2 == null) continue;
                clan.setEnemyClan(clan2);
                clan2.setAttackerClan(clan);
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("could not restore clan wars data:");
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    public void checkClans() {
        long l = System.currentTimeMillis();
        for (Clan clan : this.getClans()) {
            if (clan.getDisbandEndTime() <= 0L || clan.getDisbandEndTime() >= l) continue;
            this.dissolveClan(clan);
        }
    }

    public static void unload() {
        if (aqD != null) {
            aqD = null;
        }
    }

    static {
        aqH = Arrays.asList(SkillTable.getInstance().getInfo(370, 3), SkillTable.getInstance().getInfo(373, 3), SkillTable.getInstance().getInfo(379, 3), SkillTable.getInstance().getInfo(391, 1), SkillTable.getInstance().getInfo(371, 3), SkillTable.getInstance().getInfo(374, 3), SkillTable.getInstance().getInfo(376, 3), SkillTable.getInstance().getInfo(377, 3), SkillTable.getInstance().getInfo(383, 3), SkillTable.getInstance().getInfo(380, 3), SkillTable.getInstance().getInfo(382, 3), SkillTable.getInstance().getInfo(384, 3), SkillTable.getInstance().getInfo(385, 3), SkillTable.getInstance().getInfo(386, 3), SkillTable.getInstance().getInfo(387, 3), SkillTable.getInstance().getInfo(388, 3), SkillTable.getInstance().getInfo(390, 3), SkillTable.getInstance().getInfo(372, 3), SkillTable.getInstance().getInfo(375, 3), SkillTable.getInstance().getInfo(378, 3), SkillTable.getInstance().getInfo(381, 3), SkillTable.getInstance().getInfo(389, 3));
    }
}
