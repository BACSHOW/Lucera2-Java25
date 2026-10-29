/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.ActionFail;

public class AttackRequest
extends L2GameClientPacket {
    private int CS;
    private int aaw;
    private int aax;
    private int aay;
    private int aaz;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.aaw = this.readD();
        this.aax = this.readD();
        this.aay = this.readD();
        this.aaz = this.readC();
    }

    @Override
    protected void runImpl() {
        long l;
        GameClient gameClient = (GameClient)this.getClient();
        Player player = gameClient.getActiveChar();
        if (player == null) {
            return;
        }
        player.setActive();
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (!player.getPlayerAccess().CanAttack) {
            player.sendActionFailed();
            return;
        }
        GameObject gameObject = player.getVisibleObject(this.CS);
        if (gameObject == null) {
            player.sendActionFailed();
            return;
        }
        if (player.getAggressionTarget() != null && player.getAggressionTarget() != gameObject && !player.getAggressionTarget().isDead()) {
            player.sendActionFailed();
            return;
        }
        if (gameObject.isPlayer() && (player.isInBoat() || gameObject.isInBoat())) {
            player.sendPacket(Msg.THIS_IS_NOT_ALLOWED_WHILE_USING_A_FERRY, ActionFail.STATIC);
            return;
        }
        if (gameObject.isPlayable()) {
            if (player.isInZonePeace()) {
                player.sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE, ActionFail.STATIC);
                return;
            }
            if (((Playable)gameObject).isInZonePeace()) {
                player.sendPacket(Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE, ActionFail.STATIC);
                return;
            }
        }
        if ((l = System.currentTimeMillis()) - gameClient.getLastIncomePacketTimeStamp(AttackRequest.class) < (long)Config.ATTACK_PACKET_DELAY) {
            player.sendActionFailed();
            return;
        }
        gameClient.setLastIncomePacketTimeStamp(AttackRequest.class, l);
        if (player.getTarget() != gameObject) {
            gameObject.onAction(player, this.aaz == 1);
            return;
        }
        if (gameObject.getObjectId() != player.getObjectId() && !player.isInStoreMode() && !player.isProcessingRequest()) {
            gameObject.onForcedAttack(player, this.aaz == 1);
        }
    }
}

