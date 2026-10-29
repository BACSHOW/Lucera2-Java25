/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.tables;

import gnu.trove.TIntObjectHashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.templates.FishTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishTable {
    private static final Logger _log = LoggerFactory.getLogger(FishTable.class);
    private static final FishTable aqI = new FishTable();
    private TIntObjectHashMap<List<FishTemplate>> aqJ;

    public static final FishTable getInstance() {
        return aqI;
    }

    private FishTable() {
        this.load();
    }

    public void reload() {
        this.load();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void load() {
        this.aqJ = new TIntObjectHashMap();
        int n = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `id`, `level`, `name`, `hp`, `hpregen`, `fish_type`, `fish_group`, `fish_guts`, `guts_check_time`, `wait_time`, `combat_time` FROM `fish` ORDER BY `id`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int n2 = resultSet.getInt("id");
                int n3 = resultSet.getInt("level");
                String string = resultSet.getString("name");
                int n4 = resultSet.getInt("hp");
                int n5 = resultSet.getInt("hpregen");
                int n6 = resultSet.getInt("fish_type");
                int n7 = resultSet.getInt("fish_group");
                int n8 = resultSet.getInt("fish_guts");
                int n9 = resultSet.getInt("guts_check_time");
                int n10 = resultSet.getInt("wait_time");
                int n11 = resultSet.getInt("combat_time");
                FishTemplate fishTemplate = new FishTemplate(n2, n3, string, n4, n5, n6, n7, n8, n9, n10, n11);
                List<FishTemplate> list = this.aqJ.get(n7);
                if (list == null) {
                    list = new ArrayList<FishTemplate>();
                    this.aqJ.put(n7, list);
                }
                list.add(fishTemplate);
                ++n;
            }
            DbUtils.close(preparedStatement, resultSet);
            _log.info("FishTable: Loaded " + n + " fish.");
        }
        catch (Exception exception) {
            try {
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

    public List<FishTemplate> getFish(int n, int n2, int n3) {
        ArrayList<FishTemplate> arrayList = new ArrayList<FishTemplate>();
        List<FishTemplate> list = this.aqJ.get(n);
        if (list == null) {
            _log.warn("No fish defined for group : " + n + "!");
            return null;
        }
        for (FishTemplate fishTemplate : list) {
            if (fishTemplate.getType() != n2 || fishTemplate.getLevel() != n3) continue;
            arrayList.add(fishTemplate);
        }
        if (arrayList.isEmpty()) {
            _log.warn("No fish for group : " + n + " type: " + n2 + " level: " + n3 + "!");
        }
        return arrayList;
    }
}

