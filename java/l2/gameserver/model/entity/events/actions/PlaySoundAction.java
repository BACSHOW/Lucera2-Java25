/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import java.util.List;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PlaySound;

public class PlaySoundAction
implements EventAction {
    private int Qt;
    private String Qx;
    private PlaySound.Type Qy;

    public PlaySoundAction(int n, String string, PlaySound.Type type) {
        this.Qt = n;
        this.Qx = string;
        this.Qy = type;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        GameObject gameObject = globalEvent.getCenterObject();
        PlaySound playSound = null;
        playSound = gameObject != null ? new PlaySound(this.Qy, this.Qx, 1, gameObject.getObjectId(), gameObject.getLoc()) : new PlaySound(this.Qy, this.Qx, 0, 0, 0, 0, 0);
        List<Player> list = globalEvent.broadcastPlayers(this.Qt);
        for (Player player : list) {
            if (player == null) continue;
            player.sendPacket((IStaticPacket)playSound);
        }
    }
}

