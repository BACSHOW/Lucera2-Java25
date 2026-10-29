/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestDuelAnswerStart
extends L2GameClientPacket {
    private int py;
    private int abH;

    @Override
    protected void readImpl() {
        this.abH = this.readD();
        this.readD();
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
        if (request == null || !request.isTypeOf(Request.L2RequestType.DUEL)) {
            return;
        }
        if (!request.isInProgress()) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        if (player.isActionsDisabled()) {
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
        if (this.abH != request.getInteger("duelType")) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        DuelEvent duelEvent = (DuelEvent)EventHolder.getInstance().getEvent(EventType.PVP_EVENT, this.abH);
        switch (this.py) {
            case 0: {
                request.cancel();
                if (this.abH == 1) {
                    player2.sendPacket((IStaticPacket)SystemMsg.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
                    break;
                }
                player2.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL).addName(player));
                break;
            }
            case -1: {
                request.cancel();
                player2.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST).addName(player));
                break;
            }
            case 1: {
                SystemMessage2 systemMessage2;
                SystemMessage2 systemMessage22;
                if (!duelEvent.canDuel(player2, player, false)) {
                    request.cancel();
                    return;
                }
                if (this.abH == 1) {
                    systemMessage22 = new SystemMessage2(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_TO_A_PARTY_DUEL);
                    systemMessage2 = new SystemMessage2(SystemMsg.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY);
                } else {
                    systemMessage22 = new SystemMessage2(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_A_DUEL);
                    systemMessage2 = new SystemMessage2(SystemMsg.C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL);
                }
                player.sendPacket((IStaticPacket)systemMessage22.addName(player2));
                player2.sendPacket((IStaticPacket)systemMessage2.addName(player));
                try {
                    duelEvent.createDuel(player2, player);
                    break;
                }
                finally {
                    request.done();
                }
            }
        }
    }
}

