/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.KeyPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SendStatus;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolVersion
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);
    private int aaW;

    @Override
    protected void readImpl() {
        this.aaW = this.readD();
    }

    @Override
    protected void runImpl() {
        if (this.aaW == -2) {
            ((GameClient)this._client).closeNow(false);
            return;
        }
        if (this.aaW == -3) {
            _log.info("Status request from IP : " + ((GameClient)this.getClient()).getIpAddr());
            ((GameClient)this.getClient()).close(new SendStatus());
            return;
        }
        if (this.aaW < Config.MIN_PROTOCOL_REVISION || this.aaW > Config.MAX_PROTOCOL_REVISION) {
            Log.network("Unknown protocol revision : " + this.aaW + ", client : " + this._client);
            ((GameClient)this.getClient()).close(new KeyPacket(null));
            return;
        }
        ((GameClient)this.getClient()).setRevision(this.aaW);
        this.sendPacket((L2GameServerPacket)new KeyPacket(((GameClient)this._client).enableCrypt()));
    }
}

