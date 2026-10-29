/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.dao.JdbcEntityState;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExChangePostState;
import l2.gameserver.network.l2.s2c.ExReplyReceivedPost;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;

public class RequestExRequestReceivedPost
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
        Mail mail = MailDAO.getInstance().getReceivedMailByMailId(player.getObjectId(), this.abM);
        if (mail != null) {
            if (mail.isUnread()) {
                mail.setUnread(false);
                mail.setJdbcState(JdbcEntityState.UPDATED);
                mail.update();
                player.sendPacket((IStaticPacket)new ExChangePostState(true, 1, mail));
            }
            player.sendPacket((IStaticPacket)new ExReplyReceivedPost(mail));
            return;
        }
        player.sendPacket((IStaticPacket)new ExShowReceivedPostList(player));
    }
}

