/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExShowReceivedPostList
extends L2GameServerPacket {
    private final List<Mail> aif;

    public ExShowReceivedPostList(Player player) {
        this.aif = MailDAO.getInstance().getReceivedMailByOwnerId(player.getObjectId());
        CollectionUtils.eqSort(this.aif);
    }

    @Override
    protected void writeImpl() {
        this.writeEx(170);
        this.writeD((int)(System.currentTimeMillis() / 1000L));
        this.writeD(this.aif.size());
        for (Mail mail : this.aif) {
            this.writeD(mail.getMessageId());
            this.writeS(mail.getTopic());
            this.writeS(mail.getSenderName());
            this.writeD(mail.isPayOnDelivery() ? 1 : 0);
            this.writeD(mail.getExpireTime());
            this.writeD(mail.isUnread() ? 1 : 0);
            this.writeD(mail.getType() == Mail.SenderType.NORMAL ? 0 : 1);
            this.writeD(mail.getAttachments().isEmpty() ? 0 : 1);
            this.writeD(0);
            this.writeD(mail.getType().ordinal());
            this.writeD(0);
        }
    }
}

