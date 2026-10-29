/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExChangePostState
extends L2GameServerPacket {
    private boolean agt;
    private Mail[] agu;
    private int agv;

    public ExChangePostState(boolean bl, int n, Mail ... mailArray) {
        this.agt = bl;
        this.agu = mailArray;
        this.agv = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(179);
        this.writeD(this.agt ? 1 : 0);
        this.writeD(this.agu.length);
        for (Mail mail : this.agu) {
            this.writeD(mail.getMessageId());
            this.writeD(this.agv);
        }
    }
}

