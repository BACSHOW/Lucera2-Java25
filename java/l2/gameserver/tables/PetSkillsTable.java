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
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.Summon;
import l2.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetSkillsTable {
    private static final Logger _log = LoggerFactory.getLogger(PetSkillsTable.class);
    private TIntObjectHashMap<List<SkillLearn>> aqP = new TIntObjectHashMap();
    private static PetSkillsTable aqQ = new PetSkillsTable();

    public static PetSkillsTable getInstance() {
        return aqQ;
    }

    public void reload() {
        aqQ = new PetSkillsTable();
    }

    private PetSkillsTable() {
        this.load();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void load() {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM `pets_skills` ORDER BY `templateId`");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                n = resultSet.getInt("templateId");
                n3 = resultSet.getInt("skillId");
                n4 = resultSet.getInt("skillLvl");
                n5 = resultSet.getInt("minLvl");
                List<SkillLearn> list = this.aqP.get(n);
                if (list == null) {
                    list = new ArrayList<SkillLearn>();
                    this.aqP.put(n, list);
                }
                SkillLearn skillLearn = new SkillLearn(n3, n4, n5, 0, 0, 0L, false, false);
                list.add(skillLearn);
                ++n2;
            }
        }
        catch (Exception exception) {
            try {
                _log.error("Error while creating pet skill tree (Pet ID " + n + ")", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        _log.info("PetSkillsTable: Loaded " + n2 + " skills.");
    }

    public int getAvailableLevel(Summon summon, int n) {
        List<SkillLearn> list = this.aqP.get(summon.getNpcId());
        if (list == null) {
            return 0;
        }
        int n2 = 0;
        for (SkillLearn skillLearn : list) {
            if (skillLearn.getId() != n) continue;
            if (skillLearn.getLevel() == 0) {
                int n3;
                if (summon.getLevel() < 70) {
                    n2 = summon.getLevel() / 10;
                    if (n2 <= 0) {
                        n2 = 1;
                    }
                } else {
                    n2 = 7 + (summon.getLevel() - 70) / 5;
                }
                if (n2 <= (n3 = SkillTable.getInstance().getMaxLevel(skillLearn.getId()))) break;
                n2 = n3;
                break;
            }
            if (skillLearn.getMinLevel() > summon.getLevel() || skillLearn.getLevel() <= n2) continue;
            n2 = skillLearn.getLevel();
        }
        return n2;
    }

    public List<Integer> getAvailableSkills(Summon summon) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        List<SkillLearn> list = this.aqP.get(summon.getNpcId());
        if (list == null) {
            return arrayList;
        }
        for (SkillLearn skillLearn : list) {
            if (skillLearn == null || arrayList.contains(skillLearn.getId())) continue;
            arrayList.add(skillLearn.getId());
        }
        return arrayList;
    }
}

