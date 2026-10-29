/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.nio.BufferUnderflowException;
import java.util.List;
import l2.commons.net.nio.impl.ReceivablePacket;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Log;

public abstract class L2GameClientPacket
extends ReceivablePacket<GameClient> {
    @Override
    public final boolean read() {
        try {
            this.readImpl();
            if (Config.DEBUG_PKG) {
                System.out.println(":RECV: " + this.getClass().getSimpleName());
            }
            return true;
        }
        catch (BufferUnderflowException bufferUnderflowException) {
            ((GameClient)this._client).onPacketReadFail();
            Log.network("Client: " + this._client + " - Failed reading: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), bufferUnderflowException);
        }
        catch (Exception exception) {
            Log.network("Client: " + this._client + " - Failed reading: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), exception);
        }
        return false;
    }

    protected abstract void readImpl() throws Exception;

    @Override
    public final void run() {
        GameClient gameClient = (GameClient)this.getClient();
        try {
            this.runImpl();
        }
        catch (Exception exception) {
            Log.network("Client: " + gameClient + " - Failed running: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), exception);
        }
    }

    protected abstract void runImpl() throws Exception;

    protected String readS(int n) {
        String string = this.readS();
        return string.length() > n ? string.substring(0, n) : string;
    }

    protected void sendPacket(L2GameServerPacket l2GameServerPacket) {
        ((GameClient)this.getClient()).sendPacket(l2GameServerPacket);
    }

    protected void sendPacket(L2GameServerPacket ... l2GameServerPacketArray) {
        ((GameClient)this.getClient()).sendPacket(l2GameServerPacketArray);
    }

    protected void sendPackets(List<L2GameServerPacket> list) {
        ((GameClient)this.getClient()).sendPackets(list);
    }

    public String getType() {
        return "[C] " + this.getClass().getSimpleName();
    }
}

