/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExReplySentPost;
import l2.gameserver.network.l2.s2c.ExShowSentPostList;

public class RequestExRequestSentPost
extends L2GameClientPacket {
    private int abM;

    @Override
    protected void readImpl() {
        this.abM = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Mail mail = MailDAO.getInstance().getSentMailByMailId(player.getObjectId(), this.abM);
        if (mail != null) {
            player.sendPacket((IStaticPacket)new ExReplySentPost(mail));
            return;
        }
        player.sendPacket((IStaticPacket)new ExShowSentPostList(player));
    }
}

