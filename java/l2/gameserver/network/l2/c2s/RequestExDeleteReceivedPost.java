/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.List;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;
import org.apache.commons.lang3.ArrayUtils;

public class RequestExDeleteReceivedPost
extends L2GameClientPacket {
    private int _count;
    private int[] abN;

    @Override
    protected void readImpl() {
        this._count = this.readD();
        if (this._count * 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abN = new int[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abN[i] = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0) {
            return;
        }
        List<Mail> list = MailDAO.getInstance().getReceivedMailByOwnerId(player.getObjectId());
        if (!list.isEmpty()) {
            for (Mail mail : list) {
                if (!ArrayUtils.contains(this.abN, mail.getMessageId()) || !mail.getAttachments().isEmpty()) continue;
                MailDAO.getInstance().deleteReceivedMailByMailId(player.getObjectId(), mail.getMessageId());
            }
        }
        player.sendPacket((IStaticPacket)new ExShowReceivedPostList(player));
    }
}

