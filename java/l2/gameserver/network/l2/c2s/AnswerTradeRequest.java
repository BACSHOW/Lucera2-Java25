/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.concurrent.CopyOnWriteArrayList;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.TradeStart;

public class AnswerTradeRequest
extends L2GameClientPacket {
    private int py;

    @Override
    protected void readImpl() {
        this.py = this.readD();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Request request = player.getRequest();
        if (request == null || !request.isTypeOf(Request.L2RequestType.TRADE_REQUEST)) {
            player.sendActionFailed();
            return;
        }
        if (!request.isInProgress()) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        if (player.isOutOfControl()) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        Player player2 = request.getRequestor();
        if (player2 == null) {
            request.cancel();
            player.sendPacket((IStaticPacket)SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            player.sendActionFailed();
            return;
        }
        if (player2.getRequest() != request) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        if (this.py == 0) {
            request.cancel();
            player2.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE).addString(player.getName()));
            return;
        }
        if (!player2.isInActingRange(player)) {
            request.cancel();
            player.sendPacket((IStaticPacket)SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return;
        }
        if (player2.isActionsDisabled()) {
            request.cancel();
            player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addString(player2.getName()));
            player.sendActionFailed();
            return;
        }
        try {
            new Request(Request.L2RequestType.TRADE, player, player2);
            player2.setTradeList(new CopyOnWriteArrayList<TradeItem>());
            player2.sendPacket(new IStaticPacket[]{new SystemMessage2(SystemMsg.YOU_BEGIN_TRADING_WITH_C1).addString(player.getName()), new TradeStart(player2, player)});
            player.setTradeList(new CopyOnWriteArrayList<TradeItem>());
            player.sendPacket(new IStaticPacket[]{new SystemMessage2(SystemMsg.YOU_BEGIN_TRADING_WITH_C1).addString(player2.getName()), new TradeStart(player, player2)});
        }
        finally {
            request.done();
        }
    }
}

