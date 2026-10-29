/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.network.l2.s2c.ShortCutPacket;

public class ShortCutRegister
extends ShortCutPacket {
    private ShortCutPacket.ShortcutInfo amt;

    public ShortCutRegister(Player player, ShortCut shortCut) {
        this.amt = ShortCutRegister.convert(player, shortCut);
    }

    @Override
    protected final void writeImpl() {
        this.writeC(68);
        this.amt.write(this);
    }
}

