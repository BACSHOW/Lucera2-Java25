/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Couple {
    private static final Logger _log = LoggerFactory.getLogger(Couple.class);
    private int _id = 0;
    private int Pm = 0;
    private int Pn = 0;
    private boolean JM = false;
    private long Po;
    private long Pp;
    private boolean Pq;

    public Couple(int n) {
        this._id = n;
    }

    public Couple(Player player, Player player2) {
        long l;
        this._id = IdFactory.getInstance().getNextId();
        this.Pm = player.getObjectId();
        this.Pn = player2.getObjectId();
        this.Po = l = System.currentTimeMillis();
        this.Pp = l;
        player.setCoupleId(this._id);
        player.setPartnerId(this.Pn);
        player2.setCoupleId(this._id);
        player2.setPartnerId(this.Pm);
    }

    public void marry() {
        this.Pp = System.currentTimeMillis();
        this.JM = true;
        this.setChanged(true);
    }

    public void divorce() {
        CoupleManager.getInstance().getCouples().remove(this);
        CoupleManager.getInstance().getDeletedCouples().add(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void store(Connection connection) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("REPLACE INTO `couples` (`id`, `player1Id`, `player2Id`, `maried`, `affiancedDate`, `weddingDate`) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, this._id);
            preparedStatement.setInt(2, this.Pm);
            preparedStatement.setInt(3, this.Pn);
            preparedStatement.setBoolean(4, this.JM);
            preparedStatement.setLong(5, this.Po);
            preparedStatement.setLong(6, this.Pp);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        finally {
            DbUtils.closeQuietly(preparedStatement);
        }
    }

    public final int getId() {
        return this._id;
    }

    public final int getPlayer1Id() {
        return this.Pm;
    }

    public final int getPlayer2Id() {
        return this.Pn;
    }

    public final boolean getMaried() {
        return this.JM;
    }

    public final long getAffiancedDate() {
        return this.Po;
    }

    public final long getWeddingDate() {
        return this.Pp;
    }

    public void setPlayer1Id(int n) {
        this.Pm = n;
    }

    public void setPlayer2Id(int n) {
        this.Pn = n;
    }

    public void setMaried(boolean bl) {
        this.JM = bl;
    }

    public void setAffiancedDate(long l) {
        this.Po = l;
    }

    public void setWeddingDate(long l) {
        this.Pp = l;
    }

    public boolean isChanged() {
        return this.Pq;
    }

    public void setChanged(boolean bl) {
        this.Pq = bl;
    }
}

