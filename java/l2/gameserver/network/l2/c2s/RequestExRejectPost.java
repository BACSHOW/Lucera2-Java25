/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;

public class RequestExRejectPost
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
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }
        if (player.isInTrade()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
            return;
        }
        if (player.getEnchantScroll() != null) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        Mail mail = MailDAO.getInstance().getReceivedMailByMailId(player.getObjectId(), this.abM);
        if (mail != null) {
            if (mail.getType() != Mail.SenderType.NORMAL || mail.getAttachments().isEmpty()) {
                player.sendActionFailed();
                return;
            }
            int n = 1296000 + (int)(System.currentTimeMillis() / 1000L);
            Mail mail2 = mail.reject();
            MailDAO.getInstance().deleteReceivedMailByMailId(mail.getReceiverId(), mail.getMessageId());
            MailDAO.getInstance().deleteSentMailByMailId(mail.getReceiverId(), mail.getMessageId());
            mail2.setExpireTime(n);
            mail2.save();
            Player player2 = World.getPlayer(mail2.getReceiverId());
            if (player2 != null) {
                player2.sendPacket((IStaticPacket)ExNoticePostArrived.STATIC_TRUE);
            }
        }
        player.sendPacket((IStaticPacket)new ExShowReceivedPostList(player));
    }
}

