/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.recorder;

import l2.commons.collections.CollectionUtils;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExStorageMaxCount;

public final class PlayerStatsChangeRecorder
extends CharStatsChangeRecorder<Player> {
    public static final int BROADCAST_KARMA = 8;
    public static final int SEND_STORAGE_INFO = 16;
    public static final int SEND_MAX_LOAD = 32;
    public static final int SEND_CUR_LOAD = 64;
    public static final int BROADCAST_CHAR_INFO2 = 128;
    private int ND;
    private int GN;
    private int NE;
    private int[] NF = new int[6];
    private int[] NG = new int[6];
    private long _exp;
    private int _sp;
    private int De;
    private int Df;
    private int Dg;
    private int NH;
    private int NI;
    private int NJ;
    private int NK;
    private int NL;
    private int NM;
    private int NN;
    private int NP;
    private String _title = "";
    private int NQ;

    public PlayerStatsChangeRecorder(Player player) {
        super(player);
    }

    @Override
    protected void refreshStats() {
        this.ND = this.set(4, this.ND, ((Player)this._activeChar).getMaxCp());
        super.refreshStats();
        this.GN = this.set(34, this.GN, ((Player)this._activeChar).getMaxLoad());
        this.NE = this.set(64, this.NE, ((Player)this._activeChar).getCurrentLoad());
        for (Element element : Element.VALUES) {
            this.NF[element.getId()] = this.set(2, this.NF[element.getId()], ((Player)this._activeChar).getAttack(element));
            this.NG[element.getId()] = this.set(2, this.NG[element.getId()], ((Player)this._activeChar).getDefence(element));
        }
        this._exp = this.set(2, this._exp, ((Player)this._activeChar).getExp());
        this._sp = this.set(2, this._sp, ((Player)this._activeChar).getIntSp());
        this.Df = this.set(2, this.Df, ((Player)this._activeChar).getPkKills());
        this.Dg = this.set(2, this.Dg, ((Player)this._activeChar).getPvpKills());
        this.De = this.set(8, this.De, ((Player)this._activeChar).getKarma());
        this.NI = this.set(16, this.NI, ((Player)this._activeChar).getInventoryLimit());
        this.NJ = this.set(16, this.NJ, ((Player)this._activeChar).getWarehouseLimit());
        this.NK = this.set(16, this.NK, Config.WAREHOUSE_SLOTS_CLAN);
        this.NL = this.set(16, this.NL, ((Player)this._activeChar).getTradeLimit());
        this.NM = this.set(16, this.NM, ((Player)this._activeChar).getDwarvenRecipeLimit());
        this.NN = this.set(16, this.NN, ((Player)this._activeChar).getCommonRecipeLimit());
        this.NQ = this.set(1, this.NQ, CollectionUtils.hashCode(((Player)this._activeChar).getCubics()));
        this.NP = this.set(1, this.NP, ((Player)this._activeChar).getMatchingRoom() != null && ((Player)this._activeChar).getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && ((Player)this._activeChar).getMatchingRoom().getLeader() == this._activeChar ? ((Player)this._activeChar).getMatchingRoom().getId() : 0);
        this._team = this.set(128, this._team, ((Player)this._activeChar).getTeam());
        this._title = this.set(1, this._title, ((Player)this._activeChar).getTitle());
    }

    @Override
    protected void onSendChanges() {
        super.onSendChanges();
        if ((this._changes & 0x80) == 128) {
            ((Player)this._activeChar).broadcastCharInfo();
            if (((Player)this._activeChar).getPet() != null) {
                ((Player)this._activeChar).getPet().broadcastCharInfo();
            }
        }
        if ((this._changes & 1) == 1) {
            ((Player)this._activeChar).broadcastCharInfo();
        } else if ((this._changes & 2) == 2) {
            ((Player)this._activeChar).sendUserInfo();
        }
        if ((this._changes & 0x40) == 64) {
            ((Player)this._activeChar).sendStatusUpdate(false, false, 14);
        }
        if ((this._changes & 0x20) == 32) {
            ((Player)this._activeChar).sendStatusUpdate(false, false, 15);
        }
        if ((this._changes & 8) == 8) {
            ((Player)this._activeChar).sendStatusUpdate(true, false, 27);
        }
        if ((this._changes & 0x10) == 16) {
            ((Player)this._activeChar).sendPacket((IStaticPacket)new ExStorageMaxCount((Player)this._activeChar));
        }
    }
}

