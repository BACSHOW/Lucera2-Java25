/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ShortCutRegister;

public class RequestShortCutReg
extends L2GameClientPacket {
    private int _type;
    private int _id;
    private int _slot;
    private int _page;
    private int acS;
    private int _characterType;

    @Override
    protected void readImpl() {
        this._type = this.readD();
        int n = this.readD();
        this._id = this.readD();
        this._characterType = this.readD();
        this._slot = n % 12;
        this._page = n / 12;
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this._page < 0 || this._page > 11) {
            player.sendActionFailed();
            return;
        }
        switch (this._type) {
            case 2: {
                this.acS = player.getSkillLevel(this._id);
                break;
            }
            default: {
                this.acS = 0;
            }
        }
        ShortCut shortCut = new ShortCut(this._slot, this._page, this._type, this._id, this.acS, this._characterType);
        player.sendPacket((IStaticPacket)new ShortCutRegister(player, shortCut));
        player.registerShortCut(shortCut);
    }
}

