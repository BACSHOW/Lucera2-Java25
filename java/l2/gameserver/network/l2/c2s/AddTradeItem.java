/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SendTradeDone;
import l2.gameserver.network.l2.s2c.TradeOtherAdd;
import l2.gameserver.network.l2.s2c.TradeOwnAdd;
import l2.gameserver.network.l2.s2c.TradeUpdate;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.templates.OptionDataTemplate;

public class AddTradeItem
extends L2GameClientPacket {
    private int aau;
    private int CS;
    private long aav;

    @Override
    protected void readImpl() {
        this.aau = this.readD();
        this.CS = this.readD();
        this.aav = this.readD();
    }

    @Override
    protected void runImpl() {
        long l;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this.aav < 1L) {
            return;
        }
        Request request = player.getRequest();
        if (request == null || !request.isTypeOf(Request.L2RequestType.TRADE)) {
            player.sendActionFailed();
            return;
        }
        if (!request.isInProgress()) {
            request.cancel();
            player.sendPacket((IStaticPacket)SendTradeDone.FAIL);
            player.sendActionFailed();
            return;
        }
        if (player.isOutOfControl()) {
            request.cancel();
            player.sendPacket((IStaticPacket)SendTradeDone.FAIL);
            player.sendActionFailed();
            return;
        }
        Player player2 = request.getOtherPlayer(player);
        if (player2 == null) {
            request.cancel();
            player.sendPacket((IStaticPacket)SendTradeDone.FAIL);
            player.sendPacket((IStaticPacket)Msg.THAT_PLAYER_IS_NOT_ONLINE);
            player.sendActionFailed();
            return;
        }
        if (player2.getRequest() != request) {
            request.cancel();
            player.sendPacket((IStaticPacket)SendTradeDone.FAIL);
            player.sendActionFailed();
            return;
        }
        if (request.isConfirmed(player) || request.isConfirmed(player2)) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED);
            player.sendActionFailed();
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByObjectId(this.CS);
        if (itemInstance == null || !itemInstance.canBeTraded(player)) {
            player.sendPacket((IStaticPacket)SystemMsg.THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD);
            return;
        }
        long l2 = l = Math.min(this.aav, itemInstance.getCount());
        List<TradeItem> list = player.getTradeList();
        TradeItem object = null;
        try {
            for (TradeItem object2 : player.getTradeList()) {
                if (object2.getObjectId() != this.CS) continue;
                long optionDataTemplate = object2.getCount();
                l = SafeMath.addAndCheck(l, optionDataTemplate);
                l = Math.min(l, itemInstance.getCount());
                object2.setCount(l);
                l2 = Math.max(0L, object2.getCount() - optionDataTemplate);
                object = object2;
                break;
            }
        }
        catch (ArithmeticException arithmeticException) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        if (object == null) {
            object = new TradeItem(itemInstance);
            object.setCount(l);
            list.add(object);
        }
        if (Config.ALT_ALLOW_TRADE_AUGMENTED && itemInstance.isAugmented()) {
            Object object3 = null;
            if (itemInstance.getVariationStat1() > 0 || itemInstance.getVariationStat2() > 0) {
                OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(itemInstance.getVariationStat1());
                OptionDataTemplate optionDataTemplate2 = OptionDataHolder.getInstance().getTemplate(itemInstance.getVariationStat2());
                if (optionDataTemplate2 != null && !optionDataTemplate2.getSkills().isEmpty()) {
                    object3 = optionDataTemplate2.getSkills().get(0);
                }
                if (optionDataTemplate != null && !optionDataTemplate.getSkills().isEmpty()) {
                    object3 = optionDataTemplate.getSkills().get(0);
                }
            }
            if (object3 != null) {
                if (((Skill)object3).isActive()) {
                    player2.sendMessage(new CustomMessage("trade.AugmentItemActive", player, player, itemInstance, object3, ((Skill)object3).getLevel()));
                } else if (!((StatTemplate)object3).getTriggerList().isEmpty()) {
                    player2.sendMessage(new CustomMessage("trade.AugmentItemChance", player, player, itemInstance, object3, ((Skill)object3).getLevel()));
                } else if (((Skill)object3).isPassive()) {
                    player2.sendMessage(new CustomMessage("trade.AugmentItemPassive", player, player, itemInstance, object3, ((Skill)object3).getLevel()));
                } else {
                    player2.sendMessage(new CustomMessage("trade.AugmentItem", player, player, itemInstance, object3, ((Skill)object3).getLevel()));
                }
            } else {
                player2.sendMessage(new CustomMessage("trade.AugmentItemWithoutSkill", player, player, itemInstance));
            }
        }
        player.sendPacket(new TradeOwnAdd(object, l2), new TradeUpdate(object, itemInstance.getCount() - object.getCount()));
        player2.sendPacket((IStaticPacket)new TradeOtherAdd(object, l2));
    }
}

