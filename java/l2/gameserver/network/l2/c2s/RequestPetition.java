/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public final class RequestPetition
extends L2GameClientPacket {
    private String AL;
    private int _type;

    @Override
    protected void readImpl() {
        this.AL = this.readS();
        this._type = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        PetitionManager.getInstance().handle(player, this._type, this.AL);
    }
}

