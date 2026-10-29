/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.collections.MultiValueSet;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request
extends MultiValueSet<String> {
    private static final long serialVersionUID = 1L;
    private static final Logger _log = LoggerFactory.getLogger(Request.class);
    private static final AtomicInteger AE = new AtomicInteger();
    private final int _id = AE.incrementAndGet();
    private L2RequestType KZ;
    private HardReference<Player> La;
    private HardReference<Player> Lb;
    private volatile boolean Lc;
    private volatile boolean Ld;
    private volatile boolean Le;
    private volatile boolean Lf;
    private long qi;
    private Future<?> fX;

    public Request(L2RequestType l2RequestType, Player player, Player player2) {
        this.La = player.getRef();
        this.Lb = player2.getRef();
        this.KZ = l2RequestType;
        player.setRequest(this);
        player2.setRequest(this);
    }

    public Request setTimeout(long l) {
        this.qi = l > 0L ? System.currentTimeMillis() + l : 0L;
        this.fX = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                Request.this.timeout();
            }
        }, l);
        return this;
    }

    public int getId() {
        return this._id;
    }

    public void cancel() {
        this.Le = true;
        if (this.fX != null) {
            this.fX.cancel(false);
        }
        this.fX = null;
        Player player = this.getRequestor();
        if (player != null && player.getRequest() == this) {
            player.setRequest(null);
        }
        if ((player = this.getReciever()) != null && player.getRequest() == this) {
            player.setRequest(null);
        }
    }

    public void done() {
        this.Lf = true;
        if (this.fX != null) {
            this.fX.cancel(false);
        }
        this.fX = null;
        Player player = this.getRequestor();
        if (player != null && player.getRequest() == this) {
            player.setRequest(null);
        }
        if ((player = this.getReciever()) != null && player.getRequest() == this) {
            player.setRequest(null);
        }
    }

    public void timeout() {
        Player player = this.getReciever();
        if (player != null && player.getRequest() == this) {
            player.sendPacket((IStaticPacket)Msg.TIME_EXPIRED);
        }
        this.cancel();
    }

    public Player getOtherPlayer(Player player) {
        if (player == this.getRequestor()) {
            return this.getReciever();
        }
        if (player == this.getReciever()) {
            return this.getRequestor();
        }
        return null;
    }

    public Player getRequestor() {
        return this.La.get();
    }

    public Player getReciever() {
        return this.Lb.get();
    }

    public boolean isInProgress() {
        if (this.Le) {
            return false;
        }
        if (this.Lf) {
            return false;
        }
        if (this.qi == 0L) {
            return true;
        }
        return this.qi > System.currentTimeMillis();
    }

    public boolean isTypeOf(L2RequestType l2RequestType) {
        return this.KZ == l2RequestType;
    }

    public void confirm(Player player) {
        if (player == this.getRequestor()) {
            this.Lc = true;
        } else if (player == this.getReciever()) {
            this.Ld = true;
        }
    }

    public boolean isConfirmed(Player player) {
        if (player == this.getRequestor()) {
            return this.Lc;
        }
        if (player == this.getReciever()) {
            return this.Ld;
        }
        return false;
    }

    public static enum L2RequestType {
        CUSTOM,
        PARTY,
        PARTY_ROOM,
        CLAN,
        ALLY,
        TRADE,
        TRADE_REQUEST,
        FRIEND,
        CHANNEL,
        DUEL;

    }
}

