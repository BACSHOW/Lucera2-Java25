/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExReplySentPost
extends L2GameServerPacket {
    private final Mail XW;

    public ExReplySentPost(Mail mail) {
        this.XW = mail;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(173);
        this.writeD(this.XW.getMessageId());
        this.writeD(this.XW.isPayOnDelivery() ? 1 : 0);
        this.writeS(this.XW.getReceiverName());
        this.writeS(this.XW.getTopic());
        this.writeS(this.XW.getBody());
        this.writeD(this.XW.getAttachments().size());
        for (ItemInstance itemInstance : this.XW.getAttachments()) {
            this.writeItemInfo(itemInstance);
            this.writeD(itemInstance.getObjectId());
        }
        this.writeQ(this.XW.getPrice());
        this.writeD(0);
    }
}

