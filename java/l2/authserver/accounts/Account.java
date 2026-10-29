/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.accounts;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import l2.authserver.database.L2DatabaseFactory;
import l2.commons.dbutils.DbUtils;
import l2.commons.net.utils.NetList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account {
    private static final Logger _log = LoggerFactory.getLogger(Account.class);
    private static final String np = "{CALL `lip_AccountLoad`(?)}";
    private static final String nq = "{CALL `lip_AccountCreate`(?, ?)}";
    private static final String nr = "{CALL `lip_AccountUpdate`(?, ?, ?, ?, ?, ?, ?)}";
    private final String ns;
    private String nt;
    private NetList nu = new NetList();
    private int nv;
    private int nw;
    private String nx;
    private int ny;
    private int nz;
    private String nA;

    public Account(String string) {
        this.ns = string;
    }

    public String getLogin() {
        return this.ns;
    }

    public String getEmail() {
        return this.nA;
    }

    public void setEmail(String string) {
        this.nA = string;
    }

    public String getPasswordHash() {
        return this.nt;
    }

    public void setPasswordHash(String string) {
        this.nt = string;
    }

    public boolean isAllowedIP(String string) {
        return this.nu.isEmpty() || this.nu.isInRange(string);
    }

    public int getAccessLevel() {
        return this.nv;
    }

    public void setAccessLevel(int n) {
        this.nv = n;
    }

    public int getBanExpire() {
        return this.nw;
    }

    public void setBanExpire(int n) {
        this.nw = n;
    }

    public void setLastIP(String string) {
        this.nx = string;
    }

    public String getLastIP() {
        return this.nx;
    }

    public int getLastAccess() {
        return this.ny;
    }

    public void setLastAccess(int n) {
        this.ny = n;
    }

    public int getLastServer() {
        return this.nz;
    }

    public void setLastServer(int n) {
        this.nz = n;
    }

    public String toString() {
        return this.ns;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restore() {
        ResultSet resultSet;
        CallableStatement callableStatement;
        Connection connection;
        block4: {
            connection = null;
            callableStatement = null;
            resultSet = null;
            try {
                connection = L2DatabaseFactory.getInstance().getConnection();
                callableStatement = connection.prepareCall(np);
                callableStatement.setString(1, this.ns);
                resultSet = callableStatement.executeQuery();
                if (!resultSet.next()) break block4;
                this.setPasswordHash(resultSet.getString("password").trim());
                this.setAccessLevel(resultSet.getInt("accessLevel"));
                this.setLastServer(resultSet.getInt("lastServerId"));
                this.setLastIP(resultSet.getString("lastIP"));
                this.setLastAccess(resultSet.getInt("lastactive"));
                this.setEmail(resultSet.getString("email"));
            }
            catch (Exception exception) {
                try {
                    _log.error("", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, callableStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, callableStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, callableStatement, resultSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save() {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = L2DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(nq);
            callableStatement.setString(1, this.getLogin());
            callableStatement.setString(2, this.getPasswordHash());
            callableStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, callableStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, callableStatement);
        }
        DbUtils.closeQuietly(connection, callableStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update() {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = L2DatabaseFactory.getInstance().getConnection();
            callableStatement = connection.prepareCall(nr);
            callableStatement.setString(1, this.getLogin());
            callableStatement.setString(2, this.getPasswordHash());
            callableStatement.setInt(3, this.getAccessLevel());
            callableStatement.setInt(4, this.getLastServer());
            callableStatement.setString(5, this.getLastIP());
            callableStatement.setInt(6, this.getLastAccess());
            callableStatement.setString(7, this.getEmail());
            callableStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, callableStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, callableStatement);
        }
        DbUtils.closeQuietly(connection, callableStatement);
    }
}

