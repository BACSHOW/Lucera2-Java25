/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;

public class TeleportSiegeGuardInstance
extends NpcInstance {
    protected static final int COND_ALL_FALSE = 0;
    protected static final int BUSY_BECAUSE_SIEGE_NOT_INPROGRESS = 1;
    protected static final int COND_OWNER = 2;

    public TeleportSiegeGuardInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        player.sendActionFailed();
        String string = null;
        int n2 = this.validateCondition(player);
        string = n2 == 2 ? "castle/teleporter/" + this.getNpcId() + ".htm" : "castle/teleporter/castleteleporter-no.htm";
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public boolean isAttackable(Creature creature) {
        Player player = creature.getPlayer();
        if (player == null) {
            return false;
        }
        SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
        SiegeEvent siegeEvent2 = creature.getEvent(SiegeEvent.class);
        Clan clan = player.getClan();
        if (siegeEvent == null) {
            return false;
        }
        return clan == null || siegeEvent != siegeEvent2 || siegeEvent.getSiegeClan("defenders", clan) == null;
    }

    protected int validateCondition(Player player) {
        if (this.getCastle() != null && this.getCastle().getId() > 0 && player.getClan() != null) {
            if (((SiegeEvent)this.getCastle().getSiegeEvent()).isInProgress()) {
                return 1;
            }
            if (this.getCastle().getOwnerId() == player.getClanId()) {
                return 2;
            }
        }
        return 0;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }
}

