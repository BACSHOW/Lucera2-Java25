/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.scripts.Events;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.WarehouseFunctions;

public final class NpcFriendInstance
extends MerchantInstance {
    private static final int Uh = 7221;
    private static final int Ui = 7222;
    private static final int Uj = 7223;
    private static final int Uk = 7224;
    private static final int Ul = 7225;
    private static final int Um = 7211;
    private static final int Un = 7212;
    private static final int Uo = 7213;
    private static final int Up = 7214;
    private static final int Uq = 7215;

    public NpcFriendInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onAction(Player player, boolean bl) {
        boolean bl2;
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()), new ValidateLocation(this));
            if (this.isAutoAttackable(player)) {
                player.sendPacket((IStaticPacket)this.makeStatusUpdate(9, 10));
            }
            player.sendActionFailed();
            return;
        }
        player.sendPacket((IStaticPacket)new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()));
        if (Events.onAction(player, this, bl)) {
            return;
        }
        if (this.isAutoAttackable(player)) {
            player.getAI().Attack(this, false, bl);
            return;
        }
        if (!this.isInActingRange(player)) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            }
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && !player.isGM()) {
            player.sendActionFailed();
            return;
        }
        if (!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting() || player.isAlikeDead()) {
            return;
        }
        if (this.hasRandomAnimation()) {
            this.onRandomAnimation();
        }
        player.sendActionFailed();
        player.setLastNpcInteractionTime();
        String string = "";
        boolean bl3 = player.getInventory().getCountOf(7211) > 0L || player.getInventory().getCountOf(7212) > 0L || player.getInventory().getCountOf(7213) > 0L || player.getInventory().getCountOf(7214) > 0L || player.getInventory().getCountOf(7215) > 0L;
        boolean bl4 = bl2 = player.getInventory().getCountOf(7221) > 0L || player.getInventory().getCountOf(7222) > 0L || player.getInventory().getCountOf(7223) > 0L || player.getInventory().getCountOf(7224) > 0L || player.getInventory().getCountOf(7225) > 0L;
        if (this.getNpcId() >= 31370 && this.getNpcId() <= 31376 && bl2 || this.getNpcId() >= 31377 && this.getNpcId() < 31384 && bl3) {
            string = "npc_friend/" + this.getNpcId() + "-nofriend.htm";
            this.showChatWindow(player, string, new Object[0]);
            return;
        }
        switch (this.getNpcId()) {
            case 31370: 
            case 31371: 
            case 31373: 
            case 31377: 
            case 31378: 
            case 31380: 
            case 31553: 
            case 31554: {
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31372: {
                if (player.getInventory().getCountOf(7213) > 0L || player.getInventory().getCountOf(7214) > 0L || player.getInventory().getCountOf(7215) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-bufflist.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31379: {
                if (player.getInventory().getCountOf(7223) > 0L || player.getInventory().getCountOf(7224) > 0L || player.getInventory().getCountOf(7225) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-bufflist.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31374: {
                if (player.getInventory().getCountOf(7212) > 0L || player.getInventory().getCountOf(7213) > 0L || player.getInventory().getCountOf(7214) > 0L || player.getInventory().getCountOf(7215) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-warehouse.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31381: {
                if (player.getInventory().getCountOf(7222) > 0L || player.getInventory().getCountOf(7223) > 0L || player.getInventory().getCountOf(7224) > 0L || player.getInventory().getCountOf(7225) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-warehouse.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31375: {
                if (player.getInventory().getCountOf(7213) > 0L || player.getInventory().getCountOf(7214) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-special1.htm";
                    break;
                }
                if (player.getInventory().getCountOf(7215) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-special2.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31382: {
                if (player.getInventory().getCountOf(7223) > 0L || player.getInventory().getCountOf(7224) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-special1.htm";
                    break;
                }
                if (player.getInventory().getCountOf(7225) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-special2.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31376: {
                if (player.getInventory().getCountOf(7214) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-normal.htm";
                    break;
                }
                if (player.getInventory().getCountOf(7215) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-special.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31383: {
                if (player.getInventory().getCountOf(7224) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-normal.htm";
                    break;
                }
                if (player.getInventory().getCountOf(7225) > 0L) {
                    string = "npc_friend/" + this.getNpcId() + "-special.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31555: {
                if (player.getRam() == 1) {
                    string = "npc_friend/" + this.getNpcId() + "-special1.htm";
                    break;
                }
                if (player.getRam() == 2) {
                    string = "npc_friend/" + this.getNpcId() + "-special2.htm";
                    break;
                }
                string = "npc_friend/" + this.getNpcId() + ".htm";
                break;
            }
            case 31556: {
                string = player.getRam() == 2 ? "npc_friend/" + this.getNpcId() + "-bufflist.htm" : "npc_friend/" + this.getNpcId() + ".htm";
            }
        }
        this.showChatWindow(player, string, new Object[0]);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!NpcFriendInstance.canBypassCheck(player, this)) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
        String string2 = stringTokenizer.nextToken();
        if (string2.equalsIgnoreCase("Buff")) {
            if (stringTokenizer.countTokens() < 1) {
                return;
            }
            int n = Integer.parseInt(stringTokenizer.nextToken());
            int n2 = 0;
            switch (this.getNpcId()) {
                case 31372: {
                    n2 = 7186;
                    break;
                }
                case 31379: {
                    n2 = 7187;
                    break;
                }
                case 31556: {
                    n2 = 7251;
                }
            }
            int n3 = 0;
            int n4 = 0;
            long l = 0L;
            switch (n) {
                case 1: {
                    n3 = 4359;
                    n4 = 2;
                    l = 2L;
                    break;
                }
                case 2: {
                    n3 = 4360;
                    n4 = 2;
                    l = 2L;
                    break;
                }
                case 3: {
                    n3 = 4345;
                    n4 = 3;
                    l = 3L;
                    break;
                }
                case 4: {
                    n3 = 4355;
                    n4 = 2;
                    l = 3L;
                    break;
                }
                case 5: {
                    n3 = 4352;
                    n4 = 1;
                    l = 3L;
                    break;
                }
                case 6: {
                    n3 = 4354;
                    n4 = 3;
                    l = 3L;
                    break;
                }
                case 7: {
                    n3 = 4356;
                    n4 = 1;
                    l = 6L;
                    break;
                }
                case 8: {
                    n3 = 4357;
                    n4 = 2;
                    l = 6L;
                }
            }
            if (n3 != 0 && player.getInventory().destroyItemByItemId(n2, l)) {
                player.doCast(SkillTable.getInstance().getInfo(n3, n4), player, true);
            } else {
                this.showChatWindow(player, "npc_friend/" + this.getNpcId() + "-havenotitems.htm", new Object[0]);
            }
        } else if (string.startsWith("Chat")) {
            int n = Integer.parseInt(string.substring(5));
            String string3 = "";
            string3 = "npc_friend/" + this.getNpcId() + "-" + n + ".htm";
            if (!string3.equals("")) {
                this.showChatWindow(player, string3, new Object[0]);
            }
        } else if (string.startsWith("Buy")) {
            int n = Integer.parseInt(string.substring(4));
            this.showShopWindow(player, n, false);
        } else if (string2.equalsIgnoreCase("Sell")) {
            this.showShopWindow(player);
        } else if (string.startsWith("WithdrawP")) {
            int n = Integer.parseInt(string.substring(10));
            if (n == 99) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.setFile("npc-friend/personal.htm");
                npcHtmlMessage.replace("%npcname%", this.getName());
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            } else {
                WarehouseFunctions.showRetrieveWindow(player, n);
            }
        } else if (string.equals("DepositP")) {
            WarehouseFunctions.showDepositWindow(player);
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}

