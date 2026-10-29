/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLinkHtml
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestLinkHtml.class);
    private String acc;

    @Override
    protected void readImpl() {
        this.acc = this.readS();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this.acc.contains("..") || !this.acc.endsWith(".htm")) {
            _log.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + this.acc + "', skipped");
            return;
        }
        try {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
            npcHtmlMessage.setFile("" + this.acc);
            this.sendPacket((L2GameServerPacket)npcHtmlMessage);
        }
        catch (Exception exception) {
            _log.warn("Bad RequestLinkHtml: ", exception);
        }
    }
}

