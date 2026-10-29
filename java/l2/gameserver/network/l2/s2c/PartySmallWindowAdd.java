/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PartySmallWindowAll;

public class PartySmallWindowAdd
extends L2GameServerPacket {
    private int objectId;
    private final PartySmallWindowAll.PartySmallWindowMemberInfo aku;

    public PartySmallWindowAdd(Player player, Player player2) {
        this.objectId = player.getObjectId();
        this.aku = new PartySmallWindowAll.PartySmallWindowMemberInfo(player2);
    }

    @Override
    protected final void writeImpl() {
        this.writeC(79);
        this.writeD(this.objectId);
        this.writeD(0);
        this.writeD(this.aku._id);
        this.writeS(this.aku._name);
        this.writeD(this.aku.curCp);
        this.writeD(this.aku.maxCp);
        this.writeD(this.aku.curHp);
        this.writeD(this.aku.maxHp);
        this.writeD(this.aku.curMp);
        this.writeD(this.aku.maxMp);
        this.writeD(this.aku.level);
        this.writeD(this.aku.class_id);
        this.writeD(0);
        this.writeD(this.aku.race_id);
    }
}

