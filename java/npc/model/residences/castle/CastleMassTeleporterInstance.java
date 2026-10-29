/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import java.util.List;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class CastleMassTeleporterInstance
extends NpcInstance {
    private Future<?> ayo = null;
    private Location PG;

    public CastleMassTeleporterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.PG = Location.parseLoc(npcTemplate.getAIParams().getString("teleport_loc"));
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!CastleMassTeleporterInstance.canBypassCheck(player, this)) {
            return;
        }
        if (this.ayo != null) {
            this.showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", new Object[0]);
            return;
        }
        this.ayo = ThreadPoolManager.getInstance().schedule(new TeleportTask(), this.dN() ? 480000L : 30000L);
        this.showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", new Object[0]);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        if (this.ayo != null) {
            this.showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", new Object[0]);
        } else if (this.dN()) {
            this.showChatWindow(player, "residence2/castle/gludio_mass_teleporter002.htm", new Object[0]);
        } else {
            this.showChatWindow(player, "residence2/castle/gludio_mass_teleporter001.htm", new Object[0]);
        }
    }

    private boolean dN() {
        SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
        if (siegeEvent == null || !siegeEvent.isInProgress()) {
            return false;
        }
        List list = siegeEvent.getObjects("control_towers");
        for (Object __raw_siegeToggleNpcObject_59 : (Iterable<?>)(Object)(list)) {
           SiegeToggleNpcObject siegeToggleNpcObject = (SiegeToggleNpcObject)__raw_siegeToggleNpcObject_59;
            if (!siegeToggleNpcObject.isAlive()) continue;
            return false;
        }
        return true;
    }

    @Override
    protected boolean canInteractWithKarmaPlayer() {
        return true;
    }

    private class TeleportTask
    extends RunnableImpl {
        private TeleportTask() {
        }

        @Override
        public void runImpl() throws Exception {
            Functions.npcShoutCustomMessage(CastleMassTeleporterInstance.this, "NpcString.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE", CastleMassTeleporterInstance.this.getCastle().getName());
            for (Player player : World.getAroundPlayers(CastleMassTeleporterInstance.this, 650, 50)) {
                player.teleToLocation(Location.findPointToStay(CastleMassTeleporterInstance.this.PG, 10, 100, player.getGeoIndex()));
            }
            CastleMassTeleporterInstance.this.ayo = null;
        }
    }
}

