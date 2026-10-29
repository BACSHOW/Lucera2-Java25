/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Manor;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.items.ClanWarehouse;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.templates.manor.CropProcure;
import l2.gameserver.templates.manor.SeedProduction;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CastleManorManager {
    private static final Logger _log = LoggerFactory.getLogger(CastleManorManager.class);
    private static CastleManorManager Ab;
    public static final int PERIOD_CURRENT = 0;
    public static final int PERIOD_NEXT = 1;
    protected static final String var_name = "ManorApproved";
    private static final String Ac = "SELECT * FROM `castle_manor_procure` WHERE `castle_id`=?";
    private static final String Ad = "SELECT * FROM `castle_manor_production` WHERE `castle_id`=?";
    private static final int Ae;
    private static final int Af;
    private static final int Ag;
    private static final int MANOR_REFRESH_MIN;
    protected static final long MAINTENANCE_PERIOD;
    private boolean Ah;
    private boolean Ai;

    public static CastleManorManager getInstance() {
        if (Ab == null) {
            _log.info("Manor System: Initializing...");
            Ab = new CastleManorManager();
        }
        return Ab;
    }

    private CastleManorManager() {
        this.load();
        this.init();
        this.Ah = false;
        this.Ai = !Config.ALLOW_MANOR;
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : list) {
            castle.setNextPeriodApproved(ServerVariables.getBool(var_name));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void load() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
            for (Castle castle : list) {
                long l;
                long l2;
                int n;
                ArrayList<SeedProduction> arrayList = new ArrayList<SeedProduction>();
                ArrayList<SeedProduction> arrayList2 = new ArrayList<SeedProduction>();
                ArrayList<CropProcure> arrayList3 = new ArrayList<CropProcure>();
                ArrayList<CropProcure> arrayList4 = new ArrayList<CropProcure>();
                preparedStatement = connection.prepareStatement(Ad);
                preparedStatement.setInt(1, castle.getId());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    n = resultSet.getInt("seed_id");
                    l2 = resultSet.getLong("can_produce");
                    l = resultSet.getLong("start_produce");
                    long l3 = resultSet.getLong("seed_price");
                    int n2 = resultSet.getInt("period");
                    if (n2 == 0) {
                        arrayList.add(new SeedProduction(n, l2, l3, l));
                        continue;
                    }
                    arrayList2.add(new SeedProduction(n, l2, l3, l));
                }
                DbUtils.close(preparedStatement, resultSet);
                castle.setSeedProduction(arrayList, 0);
                castle.setSeedProduction(arrayList2, 1);
                preparedStatement = connection.prepareStatement(Ac);
                preparedStatement.setInt(1, castle.getId());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    n = resultSet.getInt("crop_id");
                    l2 = resultSet.getLong("can_buy");
                    l = resultSet.getLong("start_buy");
                    int n3 = resultSet.getInt("reward_type");
                    long l4 = resultSet.getLong("price");
                    int n4 = resultSet.getInt("period");
                    if (n4 == 0) {
                        arrayList3.add(new CropProcure(n, l2, n3, l, l4));
                        continue;
                    }
                    arrayList4.add(new CropProcure(n, l2, n3, l, l4));
                }
                castle.setCropProcure(arrayList3, 0);
                castle.setCropProcure(arrayList4, 1);
                if (!(arrayList3.isEmpty() && arrayList4.isEmpty() && arrayList.isEmpty() && arrayList2.isEmpty())) {
                    _log.info("Manor System: Loaded data for " + castle.getName() + " castle");
                }
                DbUtils.close(preparedStatement, resultSet);
            }
        }
        catch (Exception exception) {
            _log.error("Manor System: Error restoring manor data!", exception);
        }
        finally {
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
    }

    protected void init() {
        Calendar calendar;
        if (ServerVariables.getString(var_name, "").isEmpty()) {
            calendar = Calendar.getInstance();
            calendar.set(11, Ag);
            calendar.set(12, MANOR_REFRESH_MIN);
            calendar.set(13, 0);
            calendar.set(14, 0);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(11, Ae);
            calendar2.set(12, Af);
            calendar2.set(13, 0);
            calendar2.set(14, 0);
            boolean bl = calendar2.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis();
            ServerVariables.set(var_name, bl);
        }
        calendar = Calendar.getInstance();
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(12, 1);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManorTask(), calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), 60000L);
    }

    public void setNextPeriod() {
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : list) {
            Clan clan;
            if (castle.getOwnerId() <= 0 || (clan = ClanTable.getInstance().getClan(castle.getOwnerId())) == null) continue;
            ClanWarehouse clanWarehouse = clan.getWarehouse();
            for (CropProcure cropProcure : castle.getCropProcure(0)) {
                if (cropProcure.getStartAmount() == 0L) continue;
                if (cropProcure.getStartAmount() > cropProcure.getAmount()) {
                    _log.info("Manor System [" + castle.getName() + "]: Start Amount of Crop " + cropProcure.getStartAmount() + " > Amount of current " + cropProcure.getAmount());
                    long l = cropProcure.getStartAmount() - cropProcure.getAmount();
                    l = l * 90L / 100L;
                    if (l < 1L && Rnd.get(99) < 90) {
                        l = 1L;
                    }
                    if (l >= 1L) {
                        int n = Manor.getInstance().getMatureCrop(cropProcure.getId());
                        clanWarehouse.addItem(n, l);
                    }
                }
                if (cropProcure.getAmount() > 0L) {
                    castle.addToTreasuryNoTax(cropProcure.getAmount() * cropProcure.getPrice(), false, false);
                    Log.add(castle.getName() + "|" + cropProcure.getAmount() * cropProcure.getPrice() + "|ManorManager|" + cropProcure.getAmount() + "*" + cropProcure.getPrice(), "treasury");
                }
                castle.setCollectedShops(0L);
                castle.setCollectedSeed(0L);
            }
            castle.setSeedProduction(castle.getSeedProduction(1), 0);
            castle.setCropProcure(castle.getCropProcure(1), 0);
            long l = castle.getManorCost(0);
            if (castle.getTreasury() < l) {
                castle.setSeedProduction(this.t(castle.getId()), 1);
                castle.setCropProcure(this.u(castle.getId()), 1);
                Log.add(castle.getName() + "|" + l + "|ManorManager Error@setNextPeriod", "treasury");
            } else {
                ArrayList<SeedProduction> arrayList = new ArrayList<SeedProduction>();
                ArrayList<CropProcure> arrayList2 = new ArrayList<CropProcure>();
                for (SeedProduction seedProduction : castle.getSeedProduction(0)) {
                    seedProduction.setCanProduce(seedProduction.getStartProduce());
                    arrayList.add(seedProduction);
                }
                for (CropProcure cropProcure : castle.getCropProcure(0)) {
                    cropProcure.setAmount(cropProcure.getStartAmount());
                    arrayList2.add(cropProcure);
                }
                castle.setSeedProduction(arrayList, 1);
                castle.setCropProcure(arrayList2, 1);
            }
            castle.saveCropData();
            castle.saveSeedData();
            PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), Msg.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED);
            castle.setNextPeriodApproved(false);
        }
    }

    public void approveNextPeriod() {
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : list) {
            if (castle.getOwnerId() <= 0) continue;
            long l = castle.getManorCost(1);
            if (castle.getTreasury() < l) {
                castle.setSeedProduction(this.t(castle.getId()), 1);
                castle.setCropProcure(this.u(castle.getId()), 1);
                l = castle.getManorCost(1);
                if (l > 0L) {
                    Log.add(castle.getName() + "|" + -l + "|ManorManager Error@approveNextPeriod", "treasury");
                }
                Clan clan = castle.getOwner();
                PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), Msg.THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION);
            } else {
                castle.addToTreasuryNoTax(-l, false, false);
                Log.add(castle.getName() + "|" + -l + "|ManorManager", "treasury");
            }
            castle.setNextPeriodApproved(true);
        }
    }

    private List<SeedProduction> t(int n) {
        List<Manor.SeedData> list = Manor.getInstance().getSeedsForCastle(n);
        ArrayList<SeedProduction> arrayList = new ArrayList<SeedProduction>(list.size());
        for (Manor.SeedData seedData : list) {
            arrayList.add(new SeedProduction(seedData.getId()));
        }
        return arrayList;
    }

    private List<CropProcure> u(int n) {
        List<Manor.SeedData> list = Manor.getInstance().getCropsForCastle(n);
        ArrayList<CropProcure> arrayList = new ArrayList<CropProcure>(list.size());
        for (Manor.SeedData seedData : list) {
            arrayList.add(new CropProcure(seedData.getCrop()));
        }
        return arrayList;
    }

    public boolean isUnderMaintenance() {
        return this.Ah;
    }

    public void setUnderMaintenance(boolean bl) {
        this.Ah = bl;
    }

    public boolean isDisabled() {
        return this.Ai;
    }

    public void setDisabled(boolean bl) {
        this.Ai = bl;
    }

    public SeedProduction getNewSeedProduction(int n, long l, long l2, long l3) {
        return new SeedProduction(n, l, l2, l3);
    }

    public CropProcure getNewCropProcure(int n, long l, int n2, long l2, long l3) {
        return new CropProcure(n, l, n2, l3, l2);
    }

    public void save() {
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : list) {
            castle.saveSeedData();
            castle.saveCropData();
        }
    }

    static {
        Ae = Config.MANOR_APPROVE_TIME;
        Af = Config.MANOR_APPROVE_MIN;
        Ag = Config.MANOR_REFRESH_TIME;
        MANOR_REFRESH_MIN = Config.MANOR_REFRESH_MIN;
        MAINTENANCE_PERIOD = Config.MANOR_MAINTENANCE_PERIOD / 60000;
    }

    private class ManorTask
    extends RunnableImpl {
        private ManorTask() {
        }

        @Override
        public void runImpl() throws Exception {
            int n = Calendar.getInstance().get(11);
            int n2 = Calendar.getInstance().get(12);
            if (ServerVariables.getBool(CastleManorManager.var_name)) {
                if (n < Ae || n > Ag || n == Ag && n2 >= MANOR_REFRESH_MIN) {
                    ServerVariables.set(CastleManorManager.var_name, false);
                    CastleManorManager.this.setUnderMaintenance(true);
                    _log.info("Manor System: Under maintenance mode started");
                }
            } else if (CastleManorManager.this.isUnderMaintenance()) {
                if (n != Ag || (long)n2 >= (long)MANOR_REFRESH_MIN + MAINTENANCE_PERIOD) {
                    CastleManorManager.this.setUnderMaintenance(false);
                    _log.info("Manor System: Next period started");
                    if (CastleManorManager.this.isDisabled()) {
                        return;
                    }
                    CastleManorManager.this.setNextPeriod();
                    try {
                        CastleManorManager.this.save();
                    }
                    catch (Exception exception) {
                        _log.info("Manor System: Failed to save manor data: " + exception);
                    }
                }
            } else if (n > Ae && n < Ag || n == Ae && n2 >= Af) {
                ServerVariables.set(CastleManorManager.var_name, true);
                _log.info("Manor System: Next period approved");
                if (CastleManorManager.this.isDisabled()) {
                    return;
                }
                CastleManorManager.this.approveNextPeriod();
            }
        }
    }
}

