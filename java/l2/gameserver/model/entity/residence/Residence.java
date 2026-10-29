/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import l2.commons.dao.JdbcEntity;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.ResidenceFunction;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Residence
implements JdbcEntity {
    private static final Logger _log = LoggerFactory.getLogger(Residence.class);
    public static final long CYCLE_TIME = 3600000L;
    protected final int _id;
    protected final String _name;
    protected Clan _owner;
    protected Zone _zone;
    protected List<ResidenceFunction> _functions = new ArrayList<ResidenceFunction>();
    protected List<Skill> _skills = new ArrayList<Skill>();
    protected SiegeEvent<?, ?> _siegeEvent;
    protected Calendar _siegeDate = Calendar.getInstance();
    protected Calendar _lastSiegeDate = Calendar.getInstance();
    protected Calendar _ownDate = Calendar.getInstance();
    protected ScheduledFuture<?> _cycleTask;
    private int Tp;
    private int Tq;
    private int Tr;
    protected JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;
    protected List<Location> _banishPoints = new ArrayList<Location>();
    protected List<Location> _ownerRestartPoints = new ArrayList<Location>();
    protected List<Location> _otherRestartPoints = new ArrayList<Location>();
    protected List<Location> _chaosRestartPoints = new ArrayList<Location>();

    public Residence(StatsSet statsSet) {
        this._id = statsSet.getInteger("id");
        this._name = statsSet.getString("name");
    }

    public abstract ResidenceType getType();

    public void init() {
        this.initZone();
        this.initEvent();
        this.loadData();
        this.loadFunctions();
        this.rewardSkills();
        this.startCycleTask();
    }

    protected void initZone() {
        this._zone = ReflectionUtils.getZone("residence_" + this._id);
        this._zone.setParam("residence", this);
    }

    protected void initEvent() {
        this._siegeEvent = (SiegeEvent)EventHolder.getInstance().getEvent(EventType.SIEGE_EVENT, this._id);
    }

    public <E extends SiegeEvent> E getSiegeEvent() {
        return (E)this._siegeEvent;
    }

    public int getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    public int getOwnerId() {
        return this._owner == null ? 0 : this._owner.getClanId();
    }

    public Clan getOwner() {
        return this._owner;
    }

    public Zone getZone() {
        return this._zone;
    }

    protected abstract void loadData();

    public abstract void changeOwner(Clan var1);

    public Calendar getOwnDate() {
        return this._ownDate;
    }

    public Calendar getSiegeDate() {
        return this._siegeDate;
    }

    public Calendar getLastSiegeDate() {
        return this._lastSiegeDate;
    }

    public void addSkill(Skill skill) {
        this._skills.add(skill);
    }

    public void addFunction(ResidenceFunction residenceFunction) {
        this._functions.add(residenceFunction);
    }

    public boolean checkIfInZone(Location location, Reflection reflection) {
        return this.checkIfInZone(location.x, location.y, location.z, reflection);
    }

    public boolean checkIfInZone(int n, int n2, int n3, Reflection reflection) {
        return this.getZone() != null && this.getZone().checkIfInZone(n, n2, n3, reflection);
    }

    public void banishForeigner() {
        for (Player player : this._zone.getInsidePlayers()) {
            if (player.getClanId() == this.getOwnerId()) continue;
            player.teleToLocation(this.getBanishPoint());
        }
    }

    public void rewardSkills() {
        Clan clan = this.getOwner();
        if (clan != null) {
            for (Skill skill : this._skills) {
                clan.addSkill(skill, false);
                clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill)});
            }
        }
    }

    public void removeSkills() {
        Clan clan = this.getOwner();
        if (clan != null) {
            for (Skill skill : this._skills) {
                clan.removeSkill(skill.getId());
            }
        }
    }

    public void removeItems() {
        Clan clan = this.getOwner();
        if (clan != null) {
            for (UnitMember unitMember : clan) {
                Player player = unitMember.getPlayer();
                if (player == null || player.isLogoutStarted() || player.isInOfflineMode()) continue;
                for (ItemInstance itemInstance : player.getInventory().getPaperdollItems()) {
                    if (itemInstance == null || !ArrayUtils.contains(ItemTemplate.ITEM_ID_CASTLE_CIRCLET, itemInstance.getItemId()) || itemInstance.getItemId() != ItemTemplate.ITEM_ID_CASTLE_CIRCLET[clan.getCastle()]) continue;
                    player.sendDisarmMessage(itemInstance);
                    player.getInventory().unEquipItem(itemInstance);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void loadFunctions() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM `residence_functions` WHERE `id` = ?");
            preparedStatement.setInt(1, this.getId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ResidenceFunction residenceFunction = this.getFunction(resultSet.getInt("type"));
                residenceFunction.setLvl(resultSet.getInt("lvl"));
                residenceFunction.setEndTimeInMillis((long)resultSet.getInt("endTime") * 1000L);
                residenceFunction.setInDebt(resultSet.getBoolean("inDebt"));
                residenceFunction.setActive(true);
                this.a(residenceFunction);
            }
        }
        catch (Exception exception) {
            try {
                _log.warn("Residence: loadFunctions(): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    public boolean isFunctionActive(int n) {
        ResidenceFunction residenceFunction = this.getFunction(n);
        return residenceFunction != null && residenceFunction.isActive() && residenceFunction.getLevel() > 0;
    }

    public ResidenceFunction getFunction(int n) {
        for (int i = 0; i < this._functions.size(); ++i) {
            if (this._functions.get(i).getType() != n) continue;
            return this._functions.get(i);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateFunctions(int n, int n2) {
        Clan clan = this.getOwner();
        if (clan == null) {
            return false;
        }
        long l = clan.getAdenaCount();
        ResidenceFunction residenceFunction = this.getFunction(n);
        if (residenceFunction == null) {
            return false;
        }
        if (residenceFunction.isActive() && residenceFunction.getLevel() == n2) {
            return true;
        }
        int n3 = n2 == 0 ? 0 : this.getFunction(n).getLease(n2);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            if (!residenceFunction.isActive()) {
                if (l < (long)n3) {
                    boolean bl = false;
                    return bl;
                }
                clan.getWarehouse().destroyItemByItemId(57, n3);
                long l2 = Calendar.getInstance().getTimeInMillis() + 86400000L;
                preparedStatement = connection.prepareStatement("REPLACE `residence_functions` SET `id`=?, `type`=?, `lvl`=?, `endTime`=?");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.setInt(2, n);
                preparedStatement.setInt(3, n2);
                preparedStatement.setInt(4, (int)(l2 / 1000L));
                preparedStatement.execute();
                residenceFunction.setLvl(n2);
                residenceFunction.setEndTimeInMillis(l2);
                residenceFunction.setActive(true);
                this.a(residenceFunction);
            } else {
                if (l >= (long)(n3 - this.getFunction(n).getLease())) {
                    if (n3 > this.getFunction(n).getLease()) {
                        clan.getWarehouse().destroyItemByItemId(57, n3 - this.getFunction(n).getLease());
                    }
                } else {
                    boolean bl = false;
                    DbUtils.closeQuietly(connection, preparedStatement);
                    return bl;
                }
                preparedStatement = connection.prepareStatement("REPLACE `residence_functions` SET `id`=?, `type`=?, `lvl`=?");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.setInt(2, n);
                preparedStatement.setInt(3, n2);
                preparedStatement.execute();
                residenceFunction.setLvl(n2);
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        catch (Exception exception) {
            _log.warn("Exception: SiegeUnit.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + exception);
        }
        finally {
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFunction(int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM `residence_functions` WHERE `id`=? AND `type`=?");
            preparedStatement.setInt(1, this.getId());
            preparedStatement.setInt(2, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.warn("Exception: removeFunctions(int type): " + exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    private void a(ResidenceFunction residenceFunction) {
        if (this.getOwnerId() == 0) {
            return;
        }
        Clan clan = this.getOwner();
        if (clan == null) {
            return;
        }
        if (residenceFunction.getEndTimeInMillis() > System.currentTimeMillis()) {
            ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(residenceFunction), residenceFunction.getEndTimeInMillis() - System.currentTimeMillis());
        } else if (residenceFunction.isInDebt() && clan.getAdenaCount() >= (long)residenceFunction.getLease()) {
            clan.getWarehouse().destroyItemByItemId(57, residenceFunction.getLease());
            residenceFunction.updateRentTime(false);
            ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(residenceFunction), residenceFunction.getEndTimeInMillis() - System.currentTimeMillis());
        } else if (!residenceFunction.isInDebt()) {
            residenceFunction.setInDebt(true);
            residenceFunction.updateRentTime(true);
            ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(residenceFunction), residenceFunction.getEndTimeInMillis() - System.currentTimeMillis());
        } else {
            residenceFunction.setLvl(0);
            residenceFunction.setActive(false);
            this.removeFunction(residenceFunction.getType());
        }
    }

    @Override
    public void setJdbcState(JdbcEntityState jdbcEntityState) {
        this._jdbcEntityState = jdbcEntityState;
    }

    @Override
    public JdbcEntityState getJdbcState() {
        return this._jdbcEntityState;
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }

    public void cancelCycleTask() {
        this.Tp = 0;
        this.Tr = 0;
        this.Tq = 0;
        if (this._cycleTask != null) {
            this._cycleTask.cancel(false);
            this._cycleTask = null;
        }
        this.setJdbcState(JdbcEntityState.UPDATED);
    }

    public void startCycleTask() {
        long l;
        if (this._owner == null) {
            return;
        }
        long l2 = this.getOwnDate().getTimeInMillis();
        if (l2 == 0L) {
            return;
        }
        for (l = System.currentTimeMillis() - l2; l >= 3600000L; l -= 3600000L) {
        }
        this._cycleTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ResidenceCycleTask(), l, 3600000L);
    }

    public void chanceCycle() {
        this.setCycle(this.getCycle() + 1);
        this.setJdbcState(JdbcEntityState.UPDATED);
    }

    public List<Skill> getSkills() {
        return this._skills;
    }

    public void addBanishPoint(Location location) {
        this._banishPoints.add(location);
    }

    public void addOwnerRestartPoint(Location location) {
        this._ownerRestartPoints.add(location);
    }

    public void addOtherRestartPoint(Location location) {
        this._otherRestartPoints.add(location);
    }

    public void addChaosRestartPoint(Location location) {
        this._chaosRestartPoints.add(location);
    }

    public Location getBanishPoint() {
        if (this._banishPoints.isEmpty()) {
            return null;
        }
        return this._banishPoints.get(Rnd.get(this._banishPoints.size()));
    }

    public Location getOwnerRestartPoint() {
        if (this._ownerRestartPoints.isEmpty()) {
            return null;
        }
        return this._ownerRestartPoints.get(Rnd.get(this._ownerRestartPoints.size()));
    }

    public Location getOtherRestartPoint() {
        if (this._otherRestartPoints.isEmpty()) {
            return null;
        }
        return this._otherRestartPoints.get(Rnd.get(this._otherRestartPoints.size()));
    }

    public Location getChaosRestartPoint() {
        if (this._chaosRestartPoints.isEmpty()) {
            return null;
        }
        return this._chaosRestartPoints.get(Rnd.get(this._chaosRestartPoints.size()));
    }

    public Location getNotOwnerRestartPoint(Player player) {
        return player.getKarma() > 0 ? this.getChaosRestartPoint() : this.getOtherRestartPoint();
    }

    public int getCycle() {
        return this.Tp;
    }

    public long getCycleDelay() {
        if (this._cycleTask == null) {
            return 0L;
        }
        return this._cycleTask.getDelay(TimeUnit.SECONDS);
    }

    public void setCycle(int n) {
        this.Tp = n;
    }

    public int getPaidCycle() {
        return this.Tr;
    }

    public void setPaidCycle(int n) {
        this.Tr = n;
    }

    public int getRewardCount() {
        return this.Tq;
    }

    public void setRewardCount(int n) {
        this.Tq = n;
    }

    private class AutoTaskForFunctions
    extends RunnableImpl {
        ResidenceFunction _function;

        public AutoTaskForFunctions(ResidenceFunction residenceFunction) {
            this._function = residenceFunction;
        }

        @Override
        public void runImpl() throws Exception {
            Residence.this.a(this._function);
        }
    }

    public class ResidenceCycleTask
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            Residence.this.chanceCycle();
            Residence.this.update();
        }
    }
}

