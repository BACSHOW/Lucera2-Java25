/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.NpcStringContainer;

public class ExSendUIEvent
extends NpcStringContainer {
    private int CS;
    private boolean ahO;
    private boolean ahP;
    private int ahQ;
    private int ahR;

    public ExSendUIEvent(Player player, boolean bl, boolean bl2, int n, int n2, String ... stringArray) {
        this(player, bl, bl2, n, n2, NpcString.NONE, stringArray);
    }

    public ExSendUIEvent(Player player, boolean bl, boolean bl2, int n, int n2, NpcString npcString, String ... stringArray) {
        super(npcString, stringArray);
        this.CS = player.getObjectId();
        this.ahO = bl;
        this.ahP = bl2;
        this.ahQ = n;
        this.ahR = n2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(254);
        this.writeH(142);
        this.writeD(this.CS);
        this.writeD(this.ahO ? 1 : 0);
        this.writeD(0);
        this.writeD(0);
        this.writeS(this.ahP ? "1" : "0");
        this.writeS(String.valueOf(this.ahQ / 60));
        this.writeS(String.valueOf(this.ahQ % 60));
        this.writeS(String.valueOf(this.ahR / 60));
        this.writeS(String.valueOf(this.ahR % 60));
        this.writeElements();
    }
}

