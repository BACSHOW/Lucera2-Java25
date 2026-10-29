/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class DeleteObject
extends L2GameServerPacket {
    private int CS;

    public DeleteObject(GameObject gameObject) {
        this.CS = gameObject.getObjectId();
    }

    public int getObjectId() { return this.CS; }

    @Override
    protected final void writeImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || player.getObjectId() == this.CS) {
            return;
        }
        this.writeC(18);
        this.writeD(this.CS);
        this.writeD(1);
    }

    @Override
    public String getType() {
        return super.getType() + " " + GameObjectsStorage.findObject(this.CS) + " (" + this.CS + ")";
    }
}
