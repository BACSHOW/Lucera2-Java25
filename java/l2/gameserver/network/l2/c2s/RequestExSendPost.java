/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.HashMap;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2.gameserver.network.l2.s2c.ExReplyWritePost;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;

public class RequestExSendPost
extends L2GameClientPacket {
    private int abU;
    private String abV;
    private String Yl;
    private String abW;
    private int _count;
    private int[] abp;
    private long[] abq;
    private long _price;

    @Override
    protected void readImpl() {
        this.abV = this.readS(35);
        this.abU = this.readD();
        this.Yl = this.readS(127);
        this.abW = this.readS(Short.MAX_VALUE);
        this._count = this.readD();
        if (this._count * 8 + 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.abq = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.abq[i] = this.readD();
            if (this.abq[i] >= 1L && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            return;
        }
        this._price = this.readQ();
        if (this._price < 0L) {
            this._count = 0;
            this._price = 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        int n;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isGM() && this.abV.equalsIgnoreCase("ONLINE_ALL")) {
            HashMap<Integer, Long> hashMap = new HashMap<Integer, Long>();
            if (this.abp != null && this.abp.length > 0) {
                for (int i = 0; i < this.abp.length; ++i) {
                    ItemInstance gameObject = player.getInventory().getItemByObjectId(this.abp[i]);
                    hashMap.put(gameObject.getItemId(), this.abq[i]);
                }
            }
            for (Player player2 : GameObjectsStorage.getAllPlayersForIterate()) {
                if (player2 == null || !player2.isOnline()) continue;
                Functions.sendSystemMail(player2, this.Yl, this.abW, hashMap);
            }
            player.sendPacket((IStaticPacket)ExReplyWritePost.STATIC_TRUE);
            player.sendPacket((IStaticPacket)Msg.MAIL_SUCCESSFULLY_SENT);
            return;
        }
        if (!Config.ALLOW_MAIL) {
            player.sendMessage(new CustomMessage("mail.Disabled", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }
        if (player.isInTrade()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
            return;
        }
        if (player.getEnchantScroll() != null) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
            return;
        }
        if (player.getName().equalsIgnoreCase(this.abV)) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
            return;
        }
        if (this._count > 0 && !player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        if (this._price > 0L && (!player.getPlayerAccess().UseTrade || player.isTradeBannedByGM())) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            player.sendActionFailed();
            return;
        }
        if (player.isInBlockList(this.abV)) {
            player.sendPacket((IStaticPacket)new SystemMessage(2057).addString(this.abV));
            return;
        }
        Player player2 = World.getPlayer(this.abV);
        if (player2 != null) {
            n = player2.getObjectId();
            this.abV = player2.getName();
            if (player2.isInBlockList(player)) {
                player.sendPacket((IStaticPacket)new SystemMessage(1228).addString(this.abV));
                return;
            }
        } else {
            n = CharacterDAO.getInstance().getObjectIdByName(this.abV);
            if (n > 0 && mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + n + " AND target_Id=" + player.getObjectId()) > 0) {
                player.sendPacket((IStaticPacket)new SystemMessage(1228).addString(this.abV));
                return;
            }
        }
        if (n == 0) {
            player.sendPacket((IStaticPacket)Msg.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return;
        }
        int n2 = (this.abU == 1 ? 12 : 360) * 3600 + (int)(System.currentTimeMillis() / 1000L);
        if (this._count > 8) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        long l = 100 + this._count * 1000;
        ArrayList<ItemInstance> arrayList = new ArrayList<ItemInstance>();
        player.getInventory().writeLock();
        try {
            Object object;
            int n3;
            if (player.getAdena() < l) {
                player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
                return;
            }
            if (this._count > 0) {
                for (n3 = 0; n3 < this._count; ++n3) {
                    object = player.getInventory().getItemByObjectId(this.abp[n3]);
                    if (object != null && ((ItemInstance)object).getCount() >= this.abq[n3] && (((ItemInstance)object).getItemId() != 57 || ((ItemInstance)object).getCount() >= this.abq[n3] + l) && ((ItemInstance)object).canBeTraded(player)) continue;
                    player.sendPacket((IStaticPacket)Msg.THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER);
                    return;
                }
            }
            if (!player.reduceAdena(l, true)) {
                player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
                return;
            }
            if (this._count > 0) {
                for (n3 = 0; n3 < this._count; ++n3) {
                    object = player.getInventory().removeItemByObjectId(this.abp[n3], this.abq[n3]);
                    Log.LogItem(player, Log.ItemLog.PostSend, (ItemInstance)object);
                    ((ItemInstance)object).setOwnerId(player.getObjectId());
                    ((ItemInstance)object).setLocation(ItemInstance.ItemLocation.MAIL);
                    ((ItemInstance)object).save();
                    arrayList.add((ItemInstance)object);
                }
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        Mail mail = new Mail();
        mail.setSenderId(player.getObjectId());
        mail.setSenderName(player.getName());
        mail.setReceiverId(n);
        mail.setReceiverName(this.abV);
        mail.setTopic(this.Yl);
        mail.setBody(this.abW);
        mail.setPrice(this.abU > 0 ? this._price : 0L);
        mail.setUnread(true);
        mail.setType(Mail.SenderType.NORMAL);
        mail.setExpireTime(n2);
        for (ItemInstance itemInstance : arrayList) {
            mail.addAttachment(itemInstance);
        }
        mail.save();
        player.sendPacket((IStaticPacket)ExReplyWritePost.STATIC_TRUE);
        player.sendPacket((IStaticPacket)Msg.MAIL_SUCCESSFULLY_SENT);
        if (player2 != null) {
            player2.sendPacket((IStaticPacket)ExNoticePostArrived.STATIC_TRUE);
            player2.sendPacket((IStaticPacket)Msg.THE_MAIL_HAS_ARRIVED);
        }
    }
}

