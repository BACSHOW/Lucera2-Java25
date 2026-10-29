/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.collections.MultiValueSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterVariablesDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterVariablesDAO.class);
    private static final CharacterVariablesDAO uI = new CharacterVariablesDAO();
    private static final String uJ = "SELECT `value` FROM `character_variables` WHERE `obj_id`=? AND `type`=? AND `name`=?";
    private static final String uK = "REPLACE INTO `character_variables` (`obj_id`, `type`, `name`, `value`, `expire_time`) VALUES (?,?,?,?,?)";
    private static final String uL = "DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`=? AND `name`=? LIMIT 1";
    private static final String uM = "DELETE FROM `character_variables` WHERE `obj_id`=?";
    private static final String uN = "SELECT `name`,`value` FROM `character_variables` WHERE `obj_id`=?";

    public static CharacterVariablesDAO getInstance() {
        return uI;
    }

    public String getVar(int n, String string) {
        return this.getVar(n, string, "user-var");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getVar(int n, String string, String string2) {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        String string3;
        block4: {
            string3 = null;
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(uJ);
                preparedStatement.setInt(1, n);
                preparedStatement.setString(2, string2);
                preparedStatement.setString(3, string);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block4;
                string3 = Strings.stripSlashes(resultSet.getString("value"));
            }
            catch (Exception exception) {
                try {
                    _log.error("CharacterVariablesDAO.getVar(int,String,String): " + exception, exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return string3;
    }

    public void setVar(int n, String string, String string2, long l) {
        this.setVar(n, string, "user-var", string2, l);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setVar(int n, String string, String string2, String string3, long l) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(uK);
            preparedStatement.setInt(1, n);
            preparedStatement.setString(2, string2);
            preparedStatement.setString(3, string);
            preparedStatement.setString(4, string3);
            preparedStatement.setLong(5, l);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterVariablesDAO.setVar(int,String,String,String,long): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public void deleteVar(int n, String string) {
        this.deleteVar(n, string, "user-var");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteVar(int n, String string, String string2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(uL);
            preparedStatement.setInt(1, n);
            preparedStatement.setString(2, string2);
            preparedStatement.setString(3, string);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterVariablesDAO.deleteVar(int,String,String): " + exception, exception);
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
    public void deleteVars(int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(uM);
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterVariablesDAO.deleteVar(int): " + exception, exception);
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
    protected void deleteVars0(Connection connection, int n) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(uM);
            preparedStatement.setInt(1, n);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            _log.error("CharacterVariablesDAO.deleteVar(int): " + exception, exception);
        }
        finally {
            DbUtils.closeQuietly(preparedStatement);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadVariables(int n, MultiValueSet<String> multiValueSet) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(uN);
            preparedStatement.setInt(1, n);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                String string2 = Strings.stripSlashes(resultSet.getString(2));
                multiValueSet.put(string, string2);
            }
        }
        catch (Exception exception) {
            try {
                _log.error("CharacterVariablesDAO.loadVariables(int,MultiValueSet<String>): " + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }
}

