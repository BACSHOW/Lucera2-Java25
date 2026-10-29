/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;

public final class MercManagerInstance
extends MerchantInstance {
    private static int COND_ALL_FALSE = 0;
    private static int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    private static int COND_OWNER = 2;

    public MercManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!MercManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        int n = this.validateCondition(player);
        if (n <= COND_ALL_FALSE || n == COND_BUSY_BECAUSE_OF_SIEGE) {
            return;
        }
        if (n == COND_OWNER) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
            String string2 = stringTokenizer.nextToken();
            String string3 = "";
            if (stringTokenizer.countTokens() >= 1) {
                string3 = stringTokenizer.nextToken();
            }
            if (string2.equalsIgnoreCase("hire")) {
                if (string3.equals("")) {
                    return;
                }
                this.showShopWindow(player, Integer.parseInt(string3), false);
            } else {
                super.onBypassFeedback(player, string);
            }
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        String string = "castle/mercmanager/mercmanager-no.htm";
        int n2 = this.validateCondition(player);
        if (n2 == COND_BUSY_BECAUSE_OF_SIEGE) {
            string = "castle/mercmanager/mercmanager-busy.htm";
        } else if (n2 == COND_OWNER) {
            string = SevenSigns.getInstance().getCurrentPeriod() == 3 ? (SevenSigns.getInstance().getSealOwner(3) == 2 ? "castle/mercmanager/mercmanager_dawn.htm" : (SevenSigns.getInstance().getSealOwner(3) == 1 ? "castle/mercmanager/mercmanager_dusk.htm" : "castle/mercmanager/mercmanager.htm")) : "castle/mercmanager/mercmanager_nohire.htm";
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, string, n));
    }

    private int validateCondition(Player player) {
        if (player.isGM()) {
            return COND_OWNER;
        }
        if (this.getCastle() != null && this.getCastle().getId() > 0 && player.getClan() != null) {
            if (((SiegeEvent)this.getCastle().getSiegeEvent()).isInProgress()) {
                return COND_BUSY_BECAUSE_OF_SIEGE;
            }
            if (this.getCastle().getOwnerId() == player.getClanId() && (player.getClanPrivileges() & 0x200000) == 0x200000) {
                return COND_OWNER;
            }
        }
        return COND_ALL_FALSE;
    }
}

