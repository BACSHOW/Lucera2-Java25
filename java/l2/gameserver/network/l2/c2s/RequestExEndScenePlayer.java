/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestExEndScenePlayer
extends L2GameClientPacket {
    private int Kg;

    @Override
    protected void readImpl() {
        this.Kg = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (!player.isInMovie() || player.getMovieId() != this.Kg) {
            player.sendActionFailed();
            return;
        }
        player.setIsInMovie(false);
        player.setMovieId(0);
        player.decayMe();
        player.spawnMe();
    }
}

