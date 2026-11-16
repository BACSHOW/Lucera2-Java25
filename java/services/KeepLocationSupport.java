package services;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.utils.Location;

/** Uses the native Keep Location option and native movement/return logic. */
final class KeepLocationSupport
{
    private KeepLocationSupport() {}

    static void rememberStart(Player player)
    {
        if (player == null)
            return;
        AutoFarmContext context = player.getFarmSystem();
        if (context != null && context.isAutofarming())
            context.setKeepLocation(copy(player.getLoc()));
    }

    static boolean toggle(Player player)
    {
        AutoFarmContext context = player.getFarmSystem();
        synchronized (context)
        {
            boolean enabled = !context.isKeepLocation();
            Location origin = context.isAutofarming() ? context.getKeepLocation() : null;
            if (origin == null)
                origin = player.getLoc();
            // false persists farmKeepLocation through the native setter.
            context.setKeepLocation(copy(origin), enabled, false);
            return context.isKeepLocation();
        }
    }

    static void preserveOrigin(AutoFarmContext context, Runnable restart)
    {
        Location origin = copy(context.getKeepLocation());
        try
        {
            restart.run();
        }
        finally
        {
            if (origin != null)
                context.setKeepLocation(origin);
        }
    }

    private static Location copy(Location location)
    {
        return location == null ? null : new Location(location.x, location.y, location.z);
    }
}
