/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dbutils.DbUtils;
import l2.commons.math.SafeMath;
import l2.gameserver.dao.CastleDAO;
import l2.gameserver.dao.CastleHiredGuardDAO;
import l2.gameserver.dao.ClanDataDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.items.ItemContainer;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.templates.manor.CropProcure;
import l2.gameserver.templates.manor.SeedProduction;
import l2.gameserver.utils.GameStats;
import l2.gameserver.utils.Log;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Castle
extends Residence {
    private static final Logger _log = LoggerFactory.getLogger(Castle.class);
    private static final String SQ = "DELETE FROM `castle_manor_production` WHERE `castle_id`=?;";
    private static final String SR = "DELETE FROM `castle_manor_production` WHERE `castle_id`=? AND `period`=?;";
    private static final String SS = "DELETE FROM `castle_manor_procure` WHERE `castle_id`=?;";
    private static final String ST = "DELETE FROM `castle_manor_procure` WHERE `castle_id`=? AND `period`=?;";
    private static final String SU = "UPDATE `castle_manor_procure` SET `can_buy`=? WHERE `crop_id`=? AND `castle_id`=? AND `period`=?";
    private static final String SV = "UPDATE `castle_manor_production` SET `can_produce`=? WHERE `seed_id`=? AND `castle_id`=? AND `period`=?";
    private final IntObjectMap<MerchantGuard> SW = new HashIntObjectMap<MerchantGuard>();
    private List<CropProcure> SX;
    private List<SeedProduction> OD;
    private List<CropProcure> SY;
    private List<SeedProduction> SZ;
    private boolean Ta;
    private int Tb;
    private double Tc;
    private long Td;
    private long Te;
    private long Tf;
    private final NpcString Tg;
    private Set<ItemInstance> Th = new CopyOnWriteArraySet<ItemInstance>();

    public Castle(StatsSet statsSet) {
        super(statsSet);
        this.Tg = NpcString.valueOf(1001000 + this._id);
    }

    @Override
    public ResidenceType getType() {
        return ResidenceType.Castle;
    }

    @Override
    public void changeOwner(Clan clan) {
        Object object;
        if (clan != null && clan.getCastle() != 0 && (object = ResidenceHolder.getInstance().getResidence(Castle.class, clan.getCastle())) != null) {
            ((Castle)object).changeOwner(null);
        }
        object = null;
        if (this.getOwnerId() > 0 && (clan == null || clan.getClanId() != this.getOwnerId())) {
            this.removeSkills();
            this.removeItems();
            this.setTaxPercent(null, 0);
            this.cancelCycleTask();
            object = this.getOwner();
            if (object != null) {
                Object object2;
                long l = this.getTreasury();
                if (l > 0L && (object2 = ((Clan)object).getWarehouse()) != null) {
                    ((ItemContainer)object2).addItem(57, l);
                    this.addToTreasuryNoTax(-l, false, false);
                    Log.add(this.getName() + "|" + -l + "|Castle:changeOwner", "treasury");
                }
                for (Player player : ((Clan)object).getOnlineMembers(0)) {
                    if (player == null || player.getInventory() == null) continue;
                    player.getInventory().validateItems();
                }
                ((Clan)object).setHasCastle(0);
            }
        }
        if (clan != null) {
            clan.setHasCastle(this.getId());
        }
        this.a(clan);
        this.rewardSkills();
        this.update();
    }

    @Override
    protected void loadData() {
        this.Tb = 0;
        this.Tc = 0.0;
        this.Td = 0L;
        this.SX = new ArrayList<CropProcure>();
        this.OD = new ArrayList<SeedProduction>();
        this.SY = new ArrayList<CropProcure>();
        this.SZ = new ArrayList<SeedProduction>();
        this.Ta = false;
        this._owner = ClanDataDAO.getInstance().getOwner(this);
        CastleDAO.getInstance().select(this);
        CastleHiredGuardDAO.getInstance().load(this);
    }

    public void setTaxPercent(int n) {
        this.Tb = Math.min(Math.max(0, n), 100);
        this.Tc = (double)this.Tb / 100.0;
    }

    public void setTreasury(long l) {
        this.Td = l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void a(Clan clan) {
        PreparedStatement preparedStatement;
        Connection connection;
        block4: {
            this._owner = clan;
            connection = null;
            preparedStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `hasCastle`=0 WHERE `hasCastle`=? LIMIT 1");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                if (clan == null) break block4;
                preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `hasCastle`=? WHERE `clan_id`=? LIMIT 1");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.setInt(2, this.getOwnerId());
                preparedStatement.execute();
                clan.broadcastClanStatus(true, false, false);
            }
            catch (Exception exception) {
                try {
                    _log.error("", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public int getTaxPercent() {
        if (this.Tb > 5 && SevenSigns.getInstance().getSealOwner(3) == 1) {
            this.Tb = 5;
        }
        return this.Tb;
    }

    public int getTaxPercent0() {
        return this.Tb;
    }

    public long getCollectedShops() {
        return this.Te;
    }

    public long getCollectedSeed() {
        return this.Tf;
    }

    public void setCollectedShops(long l) {
        this.Te = l;
    }

    public void setCollectedSeed(long l) {
        this.Tf = l;
    }

    public void addToTreasury(long l, boolean bl, boolean bl2) {
        Castle castle;
        if (this.getOwnerId() <= 0) {
            return;
        }
        if (l == 0L) {
            return;
        }
        if (l > 1L && this._id != 5 && this._id != 8 && (castle = ResidenceHolder.getInstance().getResidence(Castle.class, this._id >= 7 ? 8 : 5)) != null) {
            long l2 = (long)((double)l * castle.getTaxRate());
            if (castle.getOwnerId() > 0) {
                castle.addToTreasury(l2, bl, bl2);
                if (this._id == 5) {
                    Log.add("Aden|" + l2 + "|Castle:adenTax", "treasury");
                } else if (this._id == 8) {
                    Log.add("Rune|" + l2 + "|Castle:runeTax", "treasury");
                }
            }
            l -= l2;
        }
        this.addToTreasuryNoTax(l, bl, bl2);
    }

    public void addToTreasuryNoTax(long l, boolean bl, boolean bl2) {
        if (this.getOwnerId() <= 0) {
            return;
        }
        if (l == 0L) {
            return;
        }
        GameStats.addAdena(l);
        this.Td = SafeMath.addAndLimit(this.Td, l);
        if (bl) {
            this.Te += l;
        }
        if (bl2) {
            this.Tf += l;
        }
        this.setJdbcState(JdbcEntityState.UPDATED);
        this.update();
    }

    public int getCropRewardType(int n) {
        int n2 = 0;
        for (CropProcure cropProcure : this.SX) {
            if (cropProcure.getId() != n) continue;
            n2 = cropProcure.getReward();
        }
        return n2;
    }

    public void setTaxPercent(Player player, int n) {
        this.setTaxPercent(n);
        this.setJdbcState(JdbcEntityState.UPDATED);
        this.update();
        if (player != null) {
            player.sendMessage(new CustomMessage("l2p.gameserver.model.entity.Castle.OutOfControl.CastleTaxChangetTo", player, new Object[0]).addString(this.getName()).addNumber(n));
        }
    }

    public double getTaxRate() {
        if (this.Tc > 0.05 && SevenSigns.getInstance().getSealOwner(3) == 1) {
            this.Tc = 0.05;
        }
        return this.Tc;
    }

    public long getTreasury() {
        return this.Td;
    }

    public List<SeedProduction> getSeedProduction(int n) {
        return n == 0 ? this.OD : this.SZ;
    }

    public List<CropProcure> getCropProcure(int n) {
        return n == 0 ? this.SX : this.SY;
    }

    public void setSeedProduction(List<SeedProduction> list, int n) {
        if (n == 0) {
            this.OD = list;
        } else {
            this.SZ = list;
        }
    }

    public void setCropProcure(List<CropProcure> list, int n) {
        if (n == 0) {
            this.SX = list;
        } else {
            this.SY = list;
        }
    }

    public synchronized SeedProduction getSeed(int n, int n2) {
        for (SeedProduction seedProduction : this.getSeedProduction(n2)) {
            if (seedProduction.getId() != n) continue;
            return seedProduction;
        }
        return null;
    }

    public synchronized CropProcure getCrop(int n, int n2) {
        for (CropProcure cropProcure : this.getCropProcure(n2)) {
            if (cropProcure.getId() != n) continue;
            return cropProcure;
        }
        return null;
    }

    public long getManorCost(int n) {
        List<SeedProduction> list;
        List<CropProcure> list2;
        if (n == 0) {
            list2 = this.SX;
            list = this.OD;
        } else {
            list2 = this.SY;
            list = this.SZ;
        }
        long l = 0L;
        if (list != null) {
            for (SeedProduction object : list) {
                l += Manor.getInstance().getSeedBuyPrice(object.getId()) * object.getStartProduce();
            }
        }
        if (list2 != null) {
            for (CropProcure cropProcure : list2) {
                l += cropProcure.getPrice() * cropProcure.getStartAmount();
            }
        }
        return l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveSeedData() {
        PreparedStatement preparedStatement;
        Connection connection;
        block10: {
            connection = null;
            preparedStatement = null;
            try {
                String[] stringArray;
                String string;
                int n;
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(SQ);
                preparedStatement.setInt(1, this.getId());
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                if (this.OD != null) {
                    n = 0;
                    string = "INSERT INTO `castle_manor_production` VALUES ";
                    stringArray = new String[this.OD.size()];
                    for (SeedProduction seedProduction : this.OD) {
                        stringArray[n] = "(" + this.getId() + "," + seedProduction.getId() + "," + seedProduction.getCanProduce() + "," + seedProduction.getStartProduce() + "," + seedProduction.getPrice() + "," + 0 + ")";
                        ++n;
                    }
                    if (stringArray.length > 0) {
                        string = string + stringArray[0];
                        for (int i = 1; i < stringArray.length; ++i) {
                            string = string + "," + stringArray[i];
                        }
                        preparedStatement = connection.prepareStatement(string);
                        preparedStatement.execute();
                        DbUtils.close(preparedStatement);
                    }
                }
                if (this.SZ == null) break block10;
                n = 0;
                string = "INSERT INTO `castle_manor_production` VALUES ";
                stringArray = new String[this.SZ.size()];
                for (SeedProduction seedProduction : this.SZ) {
                    stringArray[n] = "(" + this.getId() + "," + seedProduction.getId() + "," + seedProduction.getCanProduce() + "," + seedProduction.getStartProduce() + "," + seedProduction.getPrice() + "," + 1 + ")";
                    ++n;
                }
                if (stringArray.length <= 0) break block10;
                string = string + stringArray[0];
                for (int i = 1; i < stringArray.length; ++i) {
                    string = string + "," + stringArray[i];
                }
                preparedStatement = connection.prepareStatement(string);
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
            }
            catch (Exception exception) {
                try {
                    _log.error("Error adding seed production data for castle " + this.getName() + "!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveSeedData(int n) {
        PreparedStatement preparedStatement;
        Connection connection;
        block6: {
            connection = null;
            preparedStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(SR);
                preparedStatement.setInt(1, this.getId());
                preparedStatement.setInt(2, n);
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                List<SeedProduction> list = null;
                list = this.getSeedProduction(n);
                if (list == null) break block6;
                int n2 = 0;
                String string = "INSERT INTO `castle_manor_production` VALUES ";
                String[] stringArray = new String[list.size()];
                for (SeedProduction seedProduction : list) {
                    stringArray[n2] = "(" + this.getId() + "," + seedProduction.getId() + "," + seedProduction.getCanProduce() + "," + seedProduction.getStartProduce() + "," + seedProduction.getPrice() + "," + n + ")";
                    ++n2;
                }
                if (stringArray.length <= 0) break block6;
                string = string + stringArray[0];
                for (int i = 1; i < stringArray.length; ++i) {
                    string = string + "," + stringArray[i];
                }
                preparedStatement = connection.prepareStatement(string);
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
            }
            catch (Exception exception) {
                try {
                    _log.error("Error adding seed production data for castle " + this.getName() + "!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveCropData() {
        PreparedStatement preparedStatement;
        Connection connection;
        block10: {
            connection = null;
            preparedStatement = null;
            try {
                String[] stringArray;
                String string;
                int n;
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(SS);
                preparedStatement.setInt(1, this.getId());
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                if (this.SX != null) {
                    n = 0;
                    string = "INSERT INTO `castle_manor_procure` VALUES ";
                    stringArray = new String[this.SX.size()];
                    for (CropProcure cropProcure : this.SX) {
                        stringArray[n] = "(" + this.getId() + "," + cropProcure.getId() + "," + cropProcure.getAmount() + "," + cropProcure.getStartAmount() + "," + cropProcure.getPrice() + "," + cropProcure.getReward() + "," + 0 + ")";
                        ++n;
                    }
                    if (stringArray.length > 0) {
                        string = string + stringArray[0];
                        for (int i = 1; i < stringArray.length; ++i) {
                            string = string + "," + stringArray[i];
                        }
                        preparedStatement = connection.prepareStatement(string);
                        preparedStatement.execute();
                        DbUtils.close(preparedStatement);
                    }
                }
                if (this.SY == null) break block10;
                n = 0;
                string = "INSERT INTO `castle_manor_procure` VALUES ";
                stringArray = new String[this.SY.size()];
                for (CropProcure cropProcure : this.SY) {
                    stringArray[n] = "(" + this.getId() + "," + cropProcure.getId() + "," + cropProcure.getAmount() + "," + cropProcure.getStartAmount() + "," + cropProcure.getPrice() + "," + cropProcure.getReward() + "," + 1 + ")";
                    ++n;
                }
                if (stringArray.length <= 0) break block10;
                string = string + stringArray[0];
                for (int i = 1; i < stringArray.length; ++i) {
                    string = string + "," + stringArray[i];
                }
                preparedStatement = connection.prepareStatement(string);
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
            }
            catch (Exception exception) {
                try {
                    _log.error("Error adding crop data for castle " + this.getName() + "!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveCropData(int n) {
        PreparedStatement preparedStatement;
        Connection connection;
        block6: {
            connection = null;
            preparedStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(ST);
                preparedStatement.setInt(1, this.getId());
                preparedStatement.setInt(2, n);
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                List<CropProcure> list = null;
                list = this.getCropProcure(n);
                if (list == null) break block6;
                int n2 = 0;
                String string = "INSERT INTO `castle_manor_procure` VALUES ";
                String[] stringArray = new String[list.size()];
                for (CropProcure cropProcure : list) {
                    stringArray[n2] = "(" + this.getId() + "," + cropProcure.getId() + "," + cropProcure.getAmount() + "," + cropProcure.getStartAmount() + "," + cropProcure.getPrice() + "," + cropProcure.getReward() + "," + n + ")";
                    ++n2;
                }
                if (stringArray.length <= 0) break block6;
                string = string + stringArray[0];
                for (int i = 1; i < stringArray.length; ++i) {
                    string = string + "," + stringArray[i];
                }
                preparedStatement = connection.prepareStatement(string);
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
            }
            catch (Exception exception) {
                try {
                    _log.error("Error adding crop data for castle " + this.getName() + "!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateCrop(int n, long l, int n2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SU);
            preparedStatement.setLong(1, l);
            preparedStatement.setInt(2, n);
            preparedStatement.setInt(3, this.getId());
            preparedStatement.setInt(4, n2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("Error adding crop data for castle " + this.getName() + "!", exception);
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
    public void updateSeed(int n, long l, int n2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SV);
            preparedStatement.setLong(1, l);
            preparedStatement.setInt(2, n);
            preparedStatement.setInt(3, this.getId());
            preparedStatement.setInt(4, n2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("Error adding seed production data for castle " + this.getName() + "!", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public boolean isNextPeriodApproved() {
        return this.Ta;
    }

    public void setNextPeriodApproved(boolean bl) {
        this.Ta = bl;
    }

    @Override
    public void update() {
        CastleDAO.getInstance().update(this);
    }

    public NpcString getNpcStringName() {
        return this.Tg;
    }

    public void addMerchantGuard(MerchantGuard merchantGuard) {
        this.SW.put(merchantGuard.getItemId(), merchantGuard);
    }

    public MerchantGuard getMerchantGuard(int n) {
        return this.SW.get(n);
    }

    public IntObjectMap<MerchantGuard> getMerchantGuards() {
        return this.SW;
    }

    public Set<ItemInstance> getSpawnMerchantTickets() {
        return this.Th;
    }

    @Override
    public void startCycleTask() {
    }
}

