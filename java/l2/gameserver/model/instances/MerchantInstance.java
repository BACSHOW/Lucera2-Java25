/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.BuyList;
import l2.gameserver.network.l2.s2c.ExGetPremiumItemList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SellRefundList;
import l2.gameserver.network.l2.s2c.ShopPreviewList;
import l2.gameserver.templates.mapregion.DomainArea;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantInstance
extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(MerchantInstance.class);
    private static final int TM = 6001;

    public MerchantInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? "" + n : n + "-" + n2;
        if (this.getTemplate().getHtmRoot() != null) {
            return this.getTemplate().getHtmRoot() + string + ".htm";
        }
        String string2 = "merchant/" + string + ".htm";
        if (HtmCache.getInstance().getNullable(string2, player) != null) {
            return string2;
        }
        string2 = "teleporter/" + string + ".htm";
        if (HtmCache.getInstance().getNullable(string2, player) != null) {
            return string2;
        }
        string2 = "petmanager/" + string + ".htm";
        if (HtmCache.getInstance().getNullable(string2, player) != null) {
            return string2;
        }
        return "default/" + string + ".htm";
    }

    private void i(Player player, int n) {
        if (!player.getPlayerAccess().UseShop) {
            return;
        }
        BuyListHolder.NpcTradeList npcTradeList = BuyListHolder.getInstance().getBuyList(n);
        if (npcTradeList != null) {
            ShopPreviewList shopPreviewList = new ShopPreviewList(npcTradeList, player);
            player.sendPacket((IStaticPacket)shopPreviewList);
        } else {
            _log.warn("no buylist with id:" + n);
            player.sendActionFailed();
        }
    }

    protected void showShopWindow(Player player, int n, boolean bl) {
        Object object;
        if (!player.getPlayerAccess().UseShop) {
            return;
        }
        double d = 0.0;
        if (bl && (object = this.getCastle(player)) != null) {
            d = ((Castle)object).getTaxRate();
        }
        if ((object = BuyListHolder.getInstance().getBuyList(n)) == null || ((BuyListHolder.NpcTradeList)object).getNpcId() == this.getNpcId()) {
            player.sendPacket((IStaticPacket)new BuyList((BuyListHolder.NpcTradeList)object, player, d));
        } else {
            _log.warn("[L2MerchantInstance] possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
            _log.warn("buylist id:" + n + " / list_npc = " + ((BuyListHolder.NpcTradeList)object).getNpcId() + " / npc = " + this.getNpcId());
        }
    }

    protected void showShopWindow(Player player) {
        this.showShopWindow(player, 0, false);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!MerchantInstance.canBypassCheck(player, this)) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
        String string2 = stringTokenizer.nextToken();
        if (string2.equalsIgnoreCase("Buy")) {
            int n = 0;
            if (stringTokenizer.countTokens() > 0) {
                n = Integer.parseInt(stringTokenizer.nextToken());
            }
            this.showShopWindow(player, n, true);
        } else if (string2.equalsIgnoreCase("Sell")) {
            player.sendPacket((IStaticPacket)new SellRefundList(player, false));
        } else if (string2.equalsIgnoreCase("Wear")) {
            if (stringTokenizer.countTokens() < 1) {
                return;
            }
            int n = Integer.parseInt(stringTokenizer.nextToken());
            this.i(player, n);
        } else if (string2.equalsIgnoreCase("Multisell")) {
            if (stringTokenizer.countTokens() < 1) {
                return;
            }
            int n = Integer.parseInt(stringTokenizer.nextToken());
            Castle castle = this.getCastle(player);
            MultiSellHolder.getInstance().SeparateAndSend(n, player, castle != null ? castle.getTaxRate() : 0.0);
        } else if (string2.equalsIgnoreCase("Exchange")) {
            if (player.getLevel() < 25) {
                MultiSellHolder.getInstance().SeparateAndSend(6001, player, 0.0);
            } else {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, "merchant/merchant_for_newbie001.htm", 0));
            }
        } else if (string2.equalsIgnoreCase("ReceivePremium")) {
            if (player.getPremiumItemList().isEmpty()) {
                player.sendPacket((IStaticPacket)Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
                return;
            }
            player.sendPacket((IStaticPacket)new ExGetPremiumItemList(player));
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public Castle getCastle(Player player) {
        if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX || this.getReflection() == ReflectionManager.GIRAN_HARBOR && Config.SERVICES_GIRAN_HARBOR_NOTAX) {
            return null;
        }
        if (this.getReflection() == ReflectionManager.GIRAN_HARBOR) {
            String string = player.getVar("backCoords");
            if (string != null && !string.isEmpty()) {
                Location location = Location.parseLoc(string);
                DomainArea domainArea = MapRegionManager.getInstance().getRegionData(DomainArea.class, location);
                if (domainArea != null) {
                    return ResidenceHolder.getInstance().getResidence(Castle.class, domainArea.getId());
                }
            }
            return super.getCastle();
        }
        return super.getCastle(player);
    }

    @Override
    public boolean isMerchantNpc() {
        return true;
    }
}

