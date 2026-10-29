package l2.gameserver.network.l2.fermata;

import java.util.Collection;
import java.util.Objects;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public final class FermataBroadcaster {

    private FermataBroadcaster() {
    }

    public static boolean sendIfCapable(GameClient client, L2GameServerPacket packet, int capabilityBit) {
        if (!Config.ENABLE_FERMATA_EXTENSIONS) {
            return false;
        }
        if (client == null) {
            return false;
        }
        FermataContext ctx = client.getFermata();
        if (ctx == null || !ctx.isActive()) {
            return false;
        }
        if (capabilityBit != 0 && !ctx.hasNegotiatedCapability(capabilityBit)) {
            return false;
        }
        client.sendPacket(packet);
        return true;
    }

    public static boolean sendIfCapable(Player player, L2GameServerPacket packet, int capabilityBit) {
        if (player == null) {
            return false;
        }
        return sendIfCapable(player.getNetConnection(), packet, capabilityBit);
    }

    public static int broadcastIfCapable(l2.gameserver.model.Creature actor, L2GameServerPacket packet, int capabilityBit) {
        if (actor == null) {
            return 0;
        }
        return broadcastIfCapable(l2.gameserver.model.World.getAroundPlayers(actor), packet, capabilityBit);
    }

    public static int broadcastIfCapable(Collection<Player> recipients, L2GameServerPacket packet, int capabilityBit) {
        if (!Config.ENABLE_FERMATA_EXTENSIONS) {
            return 0;
        }
        if (recipients == null || recipients.isEmpty()) {
            return 0;
        }
        Objects.requireNonNull(packet, "packet");

        int delivered = 0;
        for (Player p : recipients) {
            if (sendIfCapable(p, packet, capabilityBit)) {
                delivered++;
            }
        }
        return delivered;
    }
}
