/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.StringHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.entity.oly.CompetitionState;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.entity.oly.Stadium;
import l2.gameserver.model.entity.oly.StadiumPool;
import l2.gameserver.model.entity.oly.participants.SinglePlayerParticipant;
import l2.gameserver.model.entity.oly.participants.TeamParticipant;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompetitionController {
    private static final Logger _log = LoggerFactory.getLogger(CompetitionController.class);
    public static final int COMPETITION_PAUSE = 30000;
    public static final int COMPETITION_PREPARATION_DELAY = 60;
    public static final int BACKPORT_DELAY = 20;
    private static CompetitionController Ru;
    private ConcurrentLinkedQueue<Competition> Rv = new ConcurrentLinkedQueue();
    private ScheduledFuture<?> Rw;
    private int Rx = 0;
    private static final String Ry = "SELECT `oc`.`char_id` AS `char_obj_id`, `on1`.`char_name` AS `char_name`, `on1`.`class_id` AS `char_class_id`, `on2`.`char_id` AS `rival_obj_id`, `on2`.`char_name` AS `rival_name`, `on2`.`class_id` AS `rival_class_id`, `oc`.`result` AS `result`, `oc`.`rule` AS `rules`, `oc`.`elapsed_time` AS `elapsed_time`, `oc`.`mtime` AS `mtime` FROM `oly_comps` AS `oc` JOIN `oly_nobles` AS `on1` ON `oc`.`char_id` = `on1`.`char_id` JOIN `oly_nobles` AS `on2` ON `oc`.`rival_id` = `on2`.`char_id` WHERE `oc`.`char_id` = ? AND `oc`.`season` = ? ";
    private static final String Rz = "INSERT INTO `oly_comps` (`season`, `char_id`, `rival_id`, `rule`, `result`, `elapsed_time`, `mtime`) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static final CompetitionController getInstance() {
        if (Ru == null) {
            Ru = new CompetitionController();
        }
        return Ru;
    }

    private CompetitionController() {
    }

    public boolean isActiveCompetitionInPrgress() {
        return !this.Rv.isEmpty();
    }

    public Collection<Competition> getCompetitions() {
        return this.Rv;
    }

    private final synchronized boolean a(CompetitionType competitionType, int n, boolean bl) {
        if (!StadiumPool.getInstance().isStadiumAvailable()) {
            _log.warn("OlyCompetitionController: not enough stadiums.");
            return false;
        }
        if (!ParticipantPool.getInstance().isEnough(competitionType, n, bl)) {
            ParticipantPool.getInstance().broadcastToEntrys(competitionType, Msg.THE_MATCH_MAY_BE_DELAYED_DUE_TO_NOT_ENOUGH_COMBATANTS, n);
            return false;
        }
        Player[][] playerArray = ParticipantPool.getInstance().retrieveEntrys(competitionType, n, bl);
        while (OlyController.getInstance().isRegAllowed() && StadiumPool.getInstance().isStadiumAvailable() && playerArray != null && playerArray[0] != null && playerArray[1] != null) {
            Stadium stadium = StadiumPool.getInstance().pollStadium();
            if (stadium == null) {
                _log.error("OlyCompetitionController: stadium == null wtf?");
                return false;
            }
            this.a(competitionType, stadium, playerArray[0], playerArray[1]);
            playerArray = ParticipantPool.getInstance().retrieveEntrys(competitionType, n, bl);
        }
        return true;
    }

    private void a(CompetitionType competitionType, Stadium stadium, Player[] playerArray, Player[] playerArray2) {
        Competition competition = new Competition(competitionType, stadium);
        if (competitionType == CompetitionType.TEAM_CLASS_FREE) {
            competition.setPlayers(new Participant[]{new TeamParticipant(Participant.SIDE_BLUE, competition, playerArray), new TeamParticipant(Participant.SIDE_RED, competition, playerArray2)});
        } else if (competitionType == CompetitionType.CLASS_FREE || competitionType == CompetitionType.CLASS_INDIVIDUAL || competitionType == CompetitionType.CLASS_TYPE_INDIVIDUAL) {
            competition.setPlayers(new Participant[]{new SinglePlayerParticipant(Participant.SIDE_BLUE, competition, playerArray[0]), new SinglePlayerParticipant(Participant.SIDE_RED, competition, playerArray2[0])});
        }
        competition.scheduleTask(new StadiumTeleportTask(competition), 100L);
        competition.start();
        this.Rv.add(competition);
    }

    public void FinishCompetition(Competition competition) {
        if (competition == null) {
            return;
        }
        try {
            competition.finish();
            if (competition.getState() != CompetitionState.INIT) {
                competition.teleportParticipantsBack();
            }
            StadiumPool.getInstance().putStadium(competition.getStadium());
            this.Rv.remove(competition);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean a(CompetitionType competitionType) {
        if (competitionType == CompetitionType.CLASS_INDIVIDUAL) {
            boolean bl = false;
            for (ClassId classId : ClassId.values()) {
                if (classId.level() != 3 || !this.a(competitionType, classId.getId(), false)) continue;
                bl = true;
            }
            return bl;
        }
        if (competitionType == CompetitionType.CLASS_TYPE_INDIVIDUAL) {
            boolean bl = false;
            if (this.a(competitionType, 0, false)) {
                bl = true;
            }
            if (this.a(competitionType, 0, true)) {
                bl = true;
            }
            return bl;
        }
        return this.a(competitionType, 0, false);
    }

    public void scheduleStartTask() {
        if (OlyController.getInstance().isRegAllowed()) {
            this.Rw = ThreadPoolManager.getInstance().schedule(new CompetitionStarterTask(), Math.min(30000 * (this.Rx + 1), 60000));
        }
    }

    public void cancelStartTask() {
        if (this.Rw != null) {
            this.Rw.cancel(true);
            this.Rw = null;
        }
    }

    public synchronized SystemMessage AddParticipationRequest(CompetitionType competitionType, Player[] playerArray) {
        if (!OlyController.getInstance().isRegAllowed()) {
            return Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS;
        }
        for (Player player : playerArray) {
            if (!player.isNoble()) {
                return new SystemMessage(1501).addName(player);
            }
            if (player.isInDuel()) {
                return new SystemMessage(1599);
            }
            int n = Calendar.getInstance().get(11) * 100 + Calendar.getInstance().get(12);
            if (Config.OLY_COMPETITION_CUSTOM_START_TIME > 0 && (n < Config.OLY_COMPETITION_CUSTOM_START_TIME || n >= Config.OLY_COMPETITION_CUSTOM_END_TIME)) {
                return new SystemMessage(1651);
            }
            if (player.getBaseClassId() != player.getClassId().getId() || player.getClassId().getLevel() < 4) {
                return new SystemMessage(1500).addName(player);
            }
            if (ParticipantPool.getInstance().isRegistred(player)) {
                return new SystemMessage(1502).addName(player);
            }
            if ((double)player.getInventoryLimit() * 0.8 <= (double)player.getInventory().getSize()) {
                return new SystemMessage(1691).addName(player);
            }
            if (player.isCursedWeaponEquipped()) {
                return new SystemMessage(1857).addName(player).addItemName(player.getCursedWeaponEquippedId());
            }
            if (NoblesController.getInstance().getPointsOf(player.getObjectId()) < 1) {
                return new SystemMessage(SystemMsg.S1).addString(new CustomMessage("THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_REQUIREMENTS_ARE_NOT_MET_IN_ORDER_TO_PARTICIPATE_IN", player, new Object[0]).toString());
            }
            if (Config.OLY_RESTRICT_CLASS_IDS.length > 0 && ArrayUtils.contains(Config.OLY_RESTRICT_CLASS_IDS, player.getActiveClassId())) {
                return new SystemMessage(SystemMsg.S1).addString(new CustomMessage("olympiad.restrictedclasses", player, new Object[0]).toString());
            }
            if (Config.OLY_RESTRICT_HWID && player.getNetConnection().getHwid() != null && ParticipantPool.getInstance().isHWIDRegistred(player.getNetConnection().getHwid())) {
                return new SystemMessage(SystemMsg.S1).addString(new CustomMessage("olympiad.iphwid.check", player, new Object[0]).toString());
            }
            if (!Config.OLY_RESTRICT_IP || player.getNetConnection().getIpAddr() == null || !ParticipantPool.getInstance().isIPRegistred(player.getNetConnection().getIpAddr())) continue;
            return new SystemMessage(SystemMsg.S1).addString(new CustomMessage("olympiad.iphwid.check", player, new Object[0]).toString());
        }
        ParticipantPool.getInstance().createEntry(competitionType, playerArray);
        switch (competitionType) {
            case CLASS_INDIVIDUAL: 
            case CLASS_TYPE_INDIVIDUAL: {
                return Msg.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES;
            }
            case CLASS_FREE: {
                return Msg.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES;
            }
            case TEAM_CLASS_FREE: {
                return Msg.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES;
            }
        }
        return null;
    }

    public void scheduleFinishCompetition(Competition competition, int n, long l) {
        competition.scheduleTask(new FinishCompetitionTask(competition, n), l);
    }

    public void scheduleCompetitionPreparation(Competition competition) {
        competition.scheduleTask(new CompetitionPreparationTask(competition), 1000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addCompetitionResult(int n, NoblesController.NobleRecord nobleRecord, int n2, NoblesController.NobleRecord nobleRecord2, int n3, CompetitionType competitionType, boolean bl, boolean bl2, long l) {
        if (nobleRecord == null || nobleRecord2 == null || competitionType == null) {
            return;
        }
        if (bl2) {
            Log.add(String.format("CompetitionResult: %s(%d) - %d disconnected against %s(%d) in %s", nobleRecord2.char_name, nobleRecord2.char_id, n3, nobleRecord.char_name, nobleRecord.char_id, competitionType.name()), "olympiad");
        } else if (!bl) {
            Log.add(String.format("CompetitionResult: %s(%d) + %d win against %s(%d) - %d in %s", nobleRecord.char_name, nobleRecord.char_id, n2, nobleRecord2.char_name, nobleRecord2.char_id, n3, competitionType.name()), "olympiad");
        } else {
            Log.add(String.format("CompetitionResult: %s(%d) tie against %s(%d) in %s", nobleRecord.char_name, nobleRecord.char_id, nobleRecord2.char_name, nobleRecord2.char_id, competitionType.name()), "olympiad");
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(Rz);
            if (!bl2) {
                preparedStatement.setInt(1, n);
                preparedStatement.setInt(2, nobleRecord.char_id);
                preparedStatement.setInt(3, nobleRecord2.char_id);
                preparedStatement.setInt(4, competitionType.getTypeIdx());
                preparedStatement.setByte(5, bl ? (byte)0 : 1);
                preparedStatement.setInt(6, (int)l);
                preparedStatement.setInt(7, (int)(System.currentTimeMillis() / 1000L));
                preparedStatement.executeUpdate();
            }
            preparedStatement.setInt(1, n);
            preparedStatement.setInt(2, nobleRecord2.char_id);
            preparedStatement.setInt(3, nobleRecord.char_id);
            preparedStatement.setInt(4, competitionType.getTypeIdx());
            preparedStatement.setByte(5, bl ? (byte)0 : -1);
            preparedStatement.setInt(6, (int)l);
            preparedStatement.setInt(7, (int)(System.currentTimeMillis() / 1000L));
            preparedStatement.executeUpdate();
        }
        catch (Exception exception) {
            try {
                _log.warn("Can't save competition result", exception);
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
    public Collection<CompetitionResults> getCompetitionResults(int n, int n2) {
        ArrayList<CompetitionResults> arrayList = new ArrayList<CompetitionResults>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(Ry);
            preparedStatement.setInt(1, n);
            preparedStatement.setInt(2, n2);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                arrayList.add(new CompetitionResults(resultSet.getInt("char_obj_id"), resultSet.getInt("char_class_id"), resultSet.getString("char_name"), resultSet.getInt("rival_obj_id"), resultSet.getInt("rival_class_id"), resultSet.getString("rival_name"), CompetitionType.getTypeOf(resultSet.getByte("rules")), resultSet.getByte("result"), resultSet.getInt("elapsed_time"), resultSet.getLong("mtime")));
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("Can't load competitions records", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return arrayList;
    }

    public void showCompetitionList(Player player) {
        if (!OlyController.getInstance().isRegAllowed()) {
            player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }
        if (player.isOlyParticipant() || ParticipantPool.getInstance().isRegistred(player)) {
            player.sendPacket((IStaticPacket)Msg.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Stadium stadium : StadiumPool.getInstance().getAllStadiums()) {
            stringBuilder.append("<a action=\"bypass -h _olympiad?command=move_op_field&field=").append(stadium.getStadiumId() + 1).append("\">");
            stringBuilder.append(new CustomMessage("Olympiad.CompetitionState.ARENA", player, new Object[0]).toString()).append(stadium.getStadiumId() + 1);
            stringBuilder.append("&nbsp;&nbsp;&nbsp;");
            boolean bl = true;
            for (Competition competition : this.Rv) {
                if (competition.getStadium() != stadium || competition.getState() == CompetitionState.INIT) continue;
                stringBuilder.append(competition._participants[0].getName()).append(" : ").append(competition._participants[1].getName());
                stringBuilder.append("&nbsp;");
                switch (competition.getState()) {
                    case STAND_BY: {
                        stringBuilder.append(new CustomMessage("Olympiad.CompetitionState.STAND_BY", player, new Object[0]).toString());
                        break;
                    }
                    case PLAYING: {
                        stringBuilder.append(new CustomMessage("Olympiad.CompetitionState.PLAYING", player, new Object[0]).toString());
                        break;
                    }
                    case FINISH: {
                        stringBuilder.append(new CustomMessage("Olympiad.CompetitionState.FINISH", player, new Object[0]).toString());
                    }
                }
                bl = false;
            }
            if (bl) {
                stringBuilder.append(new CustomMessage("Olympiad.CompetitionState.EMPTY", player, new Object[0]).toString());
            }
            stringBuilder.append("</a><br>");
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        npcHtmlMessage.setFile("oly/arenas.htm");
        npcHtmlMessage.replace("%arenas%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void watchCompetition(Player player, int n) {
        if (!OlyController.getInstance().isRegAllowed()) {
            player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }
        if (player.getPet() != null || player.isMounted()) {
            return;
        }
        if (player.isInStoreMode()) {
            return;
        }
        if (n < 1 || n > 22) {
            return;
        }
        if (player.isOlyParticipant() || ParticipantPool.getInstance().isRegistred(player)) {
            player.sendPacket((IStaticPacket)Msg.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
            return;
        }
        Stadium stadium = StadiumPool.getInstance().getStadium(n - 1);
        if (stadium.getObserverCount() > Config.OLY_MAX_SPECTATORS_PER_STADIUM) {
            player.sendMessage(new CustomMessage("CompetitionController.oly.ToManyObservers", player, new Object[0]));
            return;
        }
        if (player.isOlyObserver()) {
            player.switchOlympiadObserverArena(stadium);
        } else {
            player.enterOlympiadObserverMode(stadium);
        }
    }

    public class CompetitionResults {
        int char_id;
        int rival_id;
        String char_name;
        String rival_name;
        byte result;
        int char_class_id;
        int rival_class_id;
        int elapsed_time;
        CompetitionType type;
        long mtime;

        private CompetitionResults(int n, int n2, String string, int n3, int n4, String string2, CompetitionType competitionType, byte by, int n5, long l) {
            this.char_id = n;
            this.char_class_id = n2;
            this.char_name = string;
            this.rival_id = n3;
            this.rival_name = string2;
            this.rival_class_id = n4;
            this.type = competitionType;
            this.result = by;
            this.elapsed_time = n5;
            this.mtime = l;
        }

        public String toString(Player player, MutableInt mutableInt, MutableInt mutableInt2, MutableInt mutableInt3) {
            String string = null;
            if (this.result == 0) {
                string = StringHolder.getInstance().getNotNull(player, "hero.history.tie");
            } else if (this.result > 0) {
                string = StringHolder.getInstance().getNotNull(player, "hero.history.win");
            } else if (this.result < 2) {
                string = StringHolder.getInstance().getNotNull(player, "hero.history.loss");
            }
            if (this.result > 0) {
                mutableInt.increment();
            } else if (this.result == 0) {
                mutableInt3.increment();
            } else if (this.result < 0) {
                mutableInt2.increment();
            }
            string = string.replace("%classId%", String.valueOf(this.rival_class_id));
            string = string.replace("%name%", this.rival_name);
            string = string.replace("%date%", TimeUtils.toHeroRecordFormat(this.mtime));
            string = string.replace("%time%", String.format("%02d:%02d", this.elapsed_time / 60, this.elapsed_time % 60));
            string = string.replace("%victory_count%", mutableInt.toString());
            string = string.replace("%tie_count%", mutableInt3.toString());
            string = string.replace("%loss_count%", mutableInt2.toString());
            return string;
        }
    }

    public class FinishCompetitionTask
    implements Runnable {
        private Competition RA;
        private int RB;

        public FinishCompetitionTask(Competition competition, int n) {
            this.RA = competition;
            this.RB = n;
        }

        @Override
        public void run() {
            if (this.RA.getState() != CompetitionState.FINISH) {
                this.RA.setState(CompetitionState.FINISH);
                this.RA.ValidateWinner();
                this.RA.scheduleTask(new FinishCompetitionTask(this.RA, 20), 100L);
            } else if (this.RA.getState() == CompetitionState.FINISH) {
                if (this.RB > 0) {
                    this.RA.broadcastPacket(new SystemMessage(1499).addNumber(this.RB), true, false);
                    int n = this.RB > 5 ? this.RB / 2 : 1;
                    this.RB -= n;
                    this.RA.scheduleTask(new FinishCompetitionTask(this.RA, this.RB), n * 1000);
                } else {
                    CompetitionController.getInstance().FinishCompetition(this.RA);
                }
            }
        }
    }

    public class CompetitionPreparationTask
    implements Runnable {
        private Competition RA;
        private int RB;

        public CompetitionPreparationTask(Competition competition) {
            this(competition, 60);
        }

        public CompetitionPreparationTask(Competition competition, int n) {
            this.RA = competition;
            this.RB = n;
        }

        @Override
        public void run() {
            if (this.RB > 0) {
                if (this.RB < 10 || this.RB % 10 == 0) {
                    this.RA.broadcastPacket(new SystemMessage(1495).addNumber(this.RB), true, true);
                }
                long l = 1000L;
                switch (this.RB) {
                    case 55: 
                    case 60: {
                        this.RB -= 5;
                        l = 5000L;
                        break;
                    }
                    case 20: 
                    case 30: 
                    case 40: 
                    case 50: {
                        this.RB -= 10;
                        l = 10000L;
                        break;
                    }
                    case 10: {
                        this.RB -= 5;
                        l = 5000L;
                        break;
                    }
                    case 5: {
                        this.RA.applyBuffs();
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: {
                        --this.RB;
                        l = 1000L;
                    }
                }
                this.RA.scheduleTask(new CompetitionPreparationTask(this.RA, this.RB), this.RB > 0 ? l : 2000L);
            } else {
                this.RA.getStadium().setZonesActive(true);
                if (Config.OLY_RESTORE_HPCPMP_ON_START_MATCH) {
                    this.RA.restoreHPCPMP();
                }
                this.RA.broadcastEverybodyOlympiadUserInfo();
                this.RA.broadcastEverybodyEffectIcons();
                this.RA.broadcastPacket(new PlaySound("ns17_f"), true, true);
                this.RA.broadcastPacket(Msg.STARTS_THE_GAME, true, true);
                this.RA.setState(CompetitionState.PLAYING);
                CompetitionController.getInstance().scheduleFinishCompetition(this.RA, -1, Config.OLYMPIAD_COMPETITION_TIME);
            }
        }
    }

    public class StadiumTeleportTask
    implements Runnable {
        private Competition RA;
        private int RB;

        public StadiumTeleportTask(Competition competition) {
            this(competition, Config.OLYMPIAD_STADIUM_TELEPORT_DELAY);
        }

        public StadiumTeleportTask(Competition competition, int n) {
            this.RA = competition;
            this.RB = n;
            if (this.RA.getState() == null) {
                this.RA.setState(CompetitionState.INIT);
            }
        }

        @Override
        public void run() {
            if (this.RB > 0) {
                this.RA.broadcastPacket(new SystemMessage(1492).addNumber(this.RB), true, false);
                long l = 1000L;
                switch (this.RB) {
                    case 30: 
                    case 45: {
                        this.RB -= 15;
                        l = 15000L;
                        break;
                    }
                    case 15: {
                        this.RB = 5;
                        l = 5000L;
                        break;
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        --this.RB;
                        l = 1000L;
                    }
                }
                if (this.RA.ValidateParticipants()) {
                    return;
                }
                this.RA.scheduleTask(new StadiumTeleportTask(this.RA, this.RB), this.RB > 0 ? l : 1000L);
            } else {
                if (this.RA.ValidateParticipants()) {
                    return;
                }
                this.RA.getStadium().setZonesActive(false);
                this.RA.teleportParticipantsOnStadium();
                this.RA.setState(CompetitionState.STAND_BY);
                CompetitionController.getInstance().scheduleCompetitionPreparation(this.RA);
                OlyController.getInstance().announceCompetition(this.RA.getType(), this.RA.getStadium().getStadiumId());
            }
        }
    }

    private class CompetitionStarterTask
    implements Runnable {
        private CompetitionStarterTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                if (!OlyController.getInstance().isRegAllowed()) {
                    return;
                }
                for (CompetitionType competitionType : CompetitionType.values()) {
                    if (CompetitionController.this.a(competitionType)) {
                        CompetitionController.getInstance().Rx = 0;
                        continue;
                    }
                    if (CompetitionController.getInstance().Rx >= 5) continue;
                    CompetitionController.getInstance().Rx++;
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            finally {
                CompetitionController.getInstance().scheduleStartTask();
            }
        }
    }
}

