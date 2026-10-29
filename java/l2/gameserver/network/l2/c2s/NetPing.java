/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class NetPing
extends L2GameClientPacket {
    public static final int MIN_CLIP_RANGE = 1433;
    public static final int MAX_CLIP_RANGE = 6144;
    private int aaT;
    private int aaU;
    private int ZU;

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        if (gameClient.getRevision() == 0) {
            gameClient.closeNow(false);
        } else {
            gameClient.onPing(this.aaT, this.ZU, Math.max(1433, Math.min(this.aaU, 6144)));
        }
    }

    @Override
    protected void readImpl() {
        this.aaT = this.readD();
        this.ZU = this.readD();
        this.aaU = this.readD();
    }
}

