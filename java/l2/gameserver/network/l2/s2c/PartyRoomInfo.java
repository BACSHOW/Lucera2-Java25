/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PartyRoomInfo
extends L2GameServerPacket {
    private int _id;
    private int p;
    private int s;
    private int acq;
    private int acr;
    private int akt;
    private String _title;

    public PartyRoomInfo(MatchingRoom matchingRoom) {
        this._id = matchingRoom.getId();
        this.p = matchingRoom.getMinLevel();
        this.s = matchingRoom.getMaxLevel();
        this.acq = matchingRoom.getLootType();
        this.acr = matchingRoom.getMaxMembersSize();
        this.akt = matchingRoom.getLocationId();
        this._title = matchingRoom.getTopic();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(151);
        this.writeD(this._id);
        this.writeD(this.acr);
        this.writeD(this.p);
        this.writeD(this.s);
        this.writeD(this.acq);
        this.writeD(this.akt);
        this.writeS(this._title);
    }
}

