/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AutoFarmManager {
    private static final Logger _log = LoggerFactory.getLogger(AutoFarmManager.class);
    private final Map<String, Integer> aqZ = new HashMap<String, Integer>();
    private final List<Integer> ara = new ArrayList<Integer>();
    private ScheduledFuture<?> Nj = null;

    protected AutoFarmManager() {
        this.aqZ.clear();
        this.ara.clear();
        this.dy();
    }

    public int getActiveFarms(String string) {
        if (Config.FARM_ACTIVE_LIMITS < 0) {
            return Integer.MAX_VALUE;
        }
        if (this.aqZ.containsKey(string)) {
            return Config.FARM_ACTIVE_LIMITS - this.aqZ.get(string);
        }
        return Config.FARM_ACTIVE_LIMITS;
    }

    public void addActiveFarm(String string, int n) {
        if (Config.FARM_ACTIVE_LIMITS < 0) {
            return;
        }
        if (this.aqZ.containsKey(string)) {
            if (this.isNonCheckPlayer(n)) {
                return;
            }
            int n2 = this.aqZ.get(string) + 1;
            this.aqZ.put(string, n2);
            return;
        }
        this.aqZ.put(string, 1);
    }

    public void removeActiveFarm(String string, int n) {
        if (Config.FARM_ACTIVE_LIMITS < 0) {
            return;
        }
        if (this.aqZ.containsKey(string)) {
            if (this.isNonCheckPlayer(n)) {
                return;
            }
            int n2 = this.aqZ.get(string) - 1;
            this.aqZ.put(string, n2);
        }
    }

    public void addNonCheckPlayer(int n) {
        if (Config.FARM_ACTIVE_LIMITS < 0) {
            return;
        }
        if (!this.ara.contains(n)) {
            this.ara.add(n);
        }
    }

    public boolean isNonCheckPlayer(int n) {
        return this.ara.contains(n);
    }

    private void dy() {
        if (!Config.REFRESH_FARM_TIME) {
            return;
        }
        long l = ServerVariables.getLong("Farm_FreeTime", 0L);
        if (System.currentTimeMillis() > l) {
            this.dz();
        } else {
            this.Nj = ThreadPoolManager.getInstance().schedule(new ClearFarmFreeTime(), l - System.currentTimeMillis());
        }
    }

    private void dz() {
        if (!Config.REFRESH_FARM_TIME) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(true);
        calendar.set(11, 6);
        calendar.set(12, 30);
        calendar.add(5, 1);
        ServerVariables.set("Farm_FreeTime", calendar.getTimeInMillis());
        try {
            Throwable serializable = null;
            try (Connection connection = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM character_variables WHERE name = ?");){
                preparedStatement.setString(1, "farmFreeTime");
                preparedStatement.execute();
            }
            catch (Throwable throwable) {
                Throwable throwable2 = throwable;
                throw throwable;
            }
        }
        catch (Exception exception) {
            _log.warn("Failed to clean up autoFarm free times datas.", exception);
        }
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player == null) continue;
            player.unsetVar("farmFreeTime");
        }
        if (this.Nj != null) {
            this.Nj.cancel(false);
            this.Nj = null;
        }
        this.Nj = ThreadPoolManager.getInstance().schedule(new ClearFarmFreeTime(), calendar.getTimeInMillis() - System.currentTimeMillis());
        _log.info("AutoFarmManager: AutoFarm free times reshresh completed.");
        _log.info("AutoFarmManager: Next autoFarm free times reshresh at: " + Util.formatTime((int)(calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000));
    }

    public static final AutoFarmManager getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final AutoFarmManager _instance = new AutoFarmManager();

        private SingletonHolder() {
        }
    }

    private class ClearFarmFreeTime
    extends RunnableImpl {
        private ClearFarmFreeTime() {
        }

        @Override
        public void runImpl() {
            AutoFarmManager.this.dz();
        }
    }
}

