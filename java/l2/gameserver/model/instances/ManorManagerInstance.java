/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.BuyListSeed;
import l2.gameserver.network.l2.s2c.ExShowCropInfo;
import l2.gameserver.network.l2.s2c.ExShowManorDefaultInfo;
import l2.gameserver.network.l2.s2c.ExShowProcureCropDetail;
import l2.gameserver.network.l2.s2c.ExShowSeedInfo;
import l2.gameserver.network.l2.s2c.ExShowSellCropList;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.templates.manor.SeedProduction;
import l2.gameserver.templates.npc.NpcTemplate;

public class ManorManagerInstance
extends MerchantInstance {
    public ManorManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()), new ValidateLocation(this));
        } else {
            MyTargetSelected myTargetSelected = new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel());
            player.sendPacket((IStaticPacket)myTargetSelected);
            if (!this.isInActingRange(player)) {
                if (!player.getAI().isIntendingInteract(this)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                }
                player.sendActionFailed();
            } else {
                if (CastleManorManager.getInstance().isDisabled()) {
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("npcdefault.htm");
                    npcHtmlMessage.replace("%objectId%", String.valueOf(this.getObjectId()));
                    npcHtmlMessage.replace("%npcname%", this.getName());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                } else if (!player.isGM() && player.isClanLeader() && this.getCastle() != null && this.getCastle().getOwnerId() == player.getClanId()) {
                    this.l(player, "manager-lord.htm");
                } else {
                    this.l(player, "manager.htm");
                }
                player.sendActionFailed();
            }
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ManorManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("manor_menu_select")) {
            if (CastleManorManager.getInstance().isUnderMaintenance()) {
                player.sendPacket(ActionFail.STATIC, Msg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                return;
            }
            String string2 = string.substring(string.indexOf("?") + 1);
            StringTokenizer stringTokenizer = new StringTokenizer(string2, "&");
            int n = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
            int n2 = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
            int n3 = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
            Castle castle = this.getCastle();
            int n4 = n2 == -1 ? castle.getId() : n2;
            switch (n) {
                case 1: {
                    if (n4 != castle.getId()) {
                        player.sendPacket((IStaticPacket)Msg._HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR);
                        break;
                    }
                    BuyListHolder.NpcTradeList npcTradeList = new BuyListHolder.NpcTradeList(0);
                    List<SeedProduction> list = castle.getSeedProduction(0);
                    for (SeedProduction seedProduction : list) {
                        TradeItem tradeItem = new TradeItem();
                        tradeItem.setItemId(seedProduction.getId());
                        tradeItem.setOwnersPrice(seedProduction.getPrice());
                        tradeItem.setCount(seedProduction.getCanProduce());
                        if (tradeItem.getCount() <= 0L || tradeItem.getOwnersPrice() <= 0L) continue;
                        npcTradeList.addItem(tradeItem);
                    }
                    BuyListSeed buyListSeed = new BuyListSeed(npcTradeList, n4, player.getAdena());
                    player.sendPacket((IStaticPacket)buyListSeed);
                    break;
                }
                case 2: {
                    player.sendPacket((IStaticPacket)new ExShowSellCropList(player, n4, castle.getCropProcure(0)));
                    break;
                }
                case 3: {
                    if (n3 == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, n4).isNextPeriodApproved()) {
                        player.sendPacket((IStaticPacket)new ExShowSeedInfo(n4, Collections.emptyList()));
                        break;
                    }
                    player.sendPacket((IStaticPacket)new ExShowSeedInfo(n4, ResidenceHolder.getInstance().getResidence(Castle.class, n4).getSeedProduction(n3)));
                    break;
                }
                case 4: {
                    if (n3 == 1 && !ResidenceHolder.getInstance().getResidence(Castle.class, n4).isNextPeriodApproved()) {
                        player.sendPacket((IStaticPacket)new ExShowCropInfo(n4, Collections.emptyList()));
                        break;
                    }
                    player.sendPacket((IStaticPacket)new ExShowCropInfo(n4, ResidenceHolder.getInstance().getResidence(Castle.class, n4).getCropProcure(n3)));
                    break;
                }
                case 5: {
                    player.sendPacket((IStaticPacket)new ExShowManorDefaultInfo());
                    break;
                }
                case 6: {
                    this.showShopWindow(player, Integer.parseInt("3" + this.getNpcId()), false);
                    break;
                }
                case 9: {
                    player.sendPacket((IStaticPacket)new ExShowProcureCropDetail(n2));
                }
            }
        } else if (string.startsWith("help")) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
            stringTokenizer.nextToken();
            String string3 = "manor_client_help00" + stringTokenizer.nextToken() + ".htm";
            this.l(player, string3);
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    public String getHtmlPath() {
        return "manormanager/";
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        return "manormanager/manager.htm";
    }

    private void l(Player player, String string) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(this.getHtmlPath() + string);
        npcHtmlMessage.replace("%objectId%", String.valueOf(this.getObjectId()));
        npcHtmlMessage.replace("%npcId%", String.valueOf(this.getNpcId()));
        npcHtmlMessage.replace("%npcname%", this.getName());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }
}

