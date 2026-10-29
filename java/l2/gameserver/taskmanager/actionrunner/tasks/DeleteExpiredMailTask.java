/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.actionrunner.tasks;

import java.util.List;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2.gameserver.taskmanager.actionrunner.tasks.AutomaticTask;

public class DeleteExpiredMailTask
extends AutomaticTask {
    @Override
    public void doTask() throws Exception {
        int n = (int)(System.currentTimeMillis() / 1000L);
        List<Mail> list = MailDAO.getInstance().getExpiredMail(n);
        for (Mail mail : list) {
            if (!mail.getAttachments().isEmpty()) {
                if (mail.getType() == Mail.SenderType.NORMAL) {
                    Player player = World.getPlayer(mail.getSenderId());
                    Mail mail2 = mail.reject();
                    MailDAO.getInstance().deleteReceivedMailByMailId(mail.getReceiverId(), mail.getMessageId());
                    MailDAO.getInstance().deleteSentMailByMailId(mail.getReceiverId(), mail.getMessageId());
                    mail.delete();
                    mail2.setExpireTime(n + 1296000);
                    mail2.save();
                    if (player == null) continue;
                    player.sendPacket((IStaticPacket)ExNoticePostArrived.STATIC_TRUE);
                    player.sendPacket((IStaticPacket)Msg.THE_MAIL_HAS_ARRIVED);
                    continue;
                }
                mail.setExpireTime(n + 86400);
                mail.setJdbcState(JdbcEntityState.UPDATED);
                mail.update();
                continue;
            }
            MailDAO.getInstance().deleteReceivedMailByMailId(mail.getReceiverId(), mail.getMessageId());
            MailDAO.getInstance().deleteSentMailByMailId(mail.getReceiverId(), mail.getMessageId());
            mail.delete();
        }
    }

    @Override
    public long reCalcTime(boolean bl) {
        return System.currentTimeMillis() + 600000L;
    }
}

