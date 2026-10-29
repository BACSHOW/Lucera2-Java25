/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.Collection;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBlock
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestBlock.class);
    private static final int BLOCK = 0;
    private static final int abj = 1;
    private static final int abk = 2;
    private static final int abl = 3;
    private static final int abm = 4;
    private Integer abn;
    private String abo = null;

    @Override
    protected void readImpl() {
        this.abn = this.readD();
        if (this.abn == 0 || this.abn == 1) {
            this.abo = this.readS(16);
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        switch (this.abn) {
            case 0: {
                player.addToBlockList(this.abo);
                break;
            }
            case 1: {
                player.removeFromBlockList(this.abo);
                break;
            }
            case 2: {
                Collection<String> collection = player.getBlockList();
                if (collection == null) break;
                player.sendPacket((IStaticPacket)Msg._IGNORE_LIST_);
                for (String string : collection) {
                    player.sendMessage(string);
                }
                player.sendPacket((IStaticPacket)Msg.__EQUALS__);
                break;
            }
            case 3: {
                player.setBlockAll(true);
                player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
                player.sendEtcStatusUpdate();
                break;
            }
            case 4: {
                player.setBlockAll(false);
                player.sendPacket((IStaticPacket)Msg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
                player.sendEtcStatusUpdate();
                break;
            }
            default: {
                _log.info("Unknown 0x0a block type: " + this.abn);
            }
        }
    }
}

