/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.templates.npc.NpcTemplate;

public class BlacksmithInstance
extends NpcInstance {
    protected static final int COND_ALL_FALSE = 0;
    protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    protected static final int COND_OWNER = 2;

    public BlacksmithInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket((IStaticPacket)new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()));
            player.sendPacket((IStaticPacket)new ValidateLocation(this));
        } else {
            player.sendPacket((IStaticPacket)new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel()));
            if (!this.isInActingRange(player)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                player.sendActionFailed();
            } else {
                if (CastleManorManager.getInstance().isDisabled()) {
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("npcdefault.htm");
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                } else {
                    this.k(player, 0);
                }
                player.sendActionFailed();
            }
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!BlacksmithInstance.canBypassCheck(player, this)) {
            return;
        }
        if (CastleManorManager.getInstance().isDisabled()) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
            npcHtmlMessage.setFile("npcdefault.htm");
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        int n = this.validateCondition(player);
        if (n <= 0) {
            return;
        }
        if (n == 1) {
            return;
        }
        if (n == 2) {
            if (string.startsWith("Chat")) {
                int n2 = 0;
                try {
                    n2 = Integer.parseInt(string.substring(5));
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
                this.k(player, n2);
            } else {
                super.onBypassFeedback(player, string);
            }
        }
    }

    private void k(Player player, int n) {
        player.sendActionFailed();
        String string = "castle/blacksmith/castleblacksmith-no.htm";
        int n2 = this.validateCondition(player);
        if (n2 > 0) {
            if (n2 == 1) {
                string = "castle/blacksmith/castleblacksmith-busy.htm";
            } else if (n2 == 2) {
                string = n == 0 ? "castle/blacksmith/castleblacksmith.htm" : "castle/blacksmith/castleblacksmith-" + n + ".htm";
            }
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(string);
        npcHtmlMessage.replace("%castleid%", Integer.toString(this.getCastle().getId()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    protected int validateCondition(Player player) {
        if (player.isGM()) {
            return 2;
        }
        if (this.getCastle() != null && this.getCastle().getId() > 0 && player.getClan() != null) {
            if (((SiegeEvent)this.getCastle().getSiegeEvent()).isInProgress()) {
                return 1;
            }
            if (this.getCastle().getOwnerId() == player.getClanId() && (player.getClanPrivileges() & 0x10000) == 65536) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    protected boolean canInteractWithKarmaPlayer() {
        return true;
    }
}

