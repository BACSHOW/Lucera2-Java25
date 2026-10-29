/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharMoveToLocation;
import l2.gameserver.utils.Location;

public class MoveBackwardToLocation
extends L2GameClientPacket {
    private Location aaN = new Location();
    private Location aaO = new Location();
    private int aaP;

    @Override
    protected void readImpl() {
        this.aaN.x = this.readD();
        this.aaN.y = this.readD();
        this.aaN.z = this.readD();
        this.aaO.x = this.readD();
        this.aaO.y = this.readD();
        this.aaO.z = this.readD();
        if (this._buf.hasRemaining()) {
            this.aaP = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        if (gameClient == null) {
            return;
        }
        Player player = gameClient.getActiveChar();
        if (player == null) {
            return;
        }
        if (this.aaP == 0 && !Config.ALLOW_KEYBOARD_MOVE) {
            player.sendActionFailed();
            return;
        }
        player.setActive();
        if (player.isTeleporting()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFrozen()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }
        if (player.isOlyObserver()) {
            if (player.getOlyObservingStadium().getObservingLoc().distance(this.aaN) < 8192.0) {
                player.sendPacket((IStaticPacket)new CharMoveToLocation(player.getObjectId(), this.aaO, this.aaN));
            } else {
                player.sendActionFailed();
            }
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (Config.ALT_ALLOW_DELAY_NPC_TALK && !player.canMoveAfterInteraction()) {
            player.sendMessage(new CustomMessage("YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC__ONE_MOMENT_PLEASE", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (player.getTeleMode() > 0) {
            if (player.getTeleMode() == 1) {
                player.setTeleMode(0);
            }
            player.sendActionFailed();
            player.teleToLocation(this.aaN);
            return;
        }
        if (player.isInFlyingTransform()) {
            this.aaN.z = Math.min(5950, Math.max(50, this.aaN.z));
        }
        l2.gameserver.utils.MovementTelemetryLogger.logC2S(player, "MoveBackwardToLocation", String.format("TargetLoc=(%d,%d,%d) OrigLoc=(%d,%d,%d) mode=%d", this.aaN.x, this.aaN.y, this.aaN.z, this.aaO.x, this.aaO.y, this.aaO.z, this.aaP));
        player.moveBackwardToLocationForPacket(this.aaN, this.aaP != 0 && !player.getVarB("no_pf"));
    }
}

