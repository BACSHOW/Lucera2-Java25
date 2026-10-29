/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.EventTrigger;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ReflectionUtils;

public final class OddGlobeInstance
extends NpcInstance {
    private static final int ayd = 151;

    public OddGlobeInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!OddGlobeInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("monastery_enter")) {
            Reflection reflection = player.getActiveReflection();
            if (reflection != null) {
                if (player.canReenterInstance(151)) {
                    player.teleToLocation(reflection.getTeleportLoc(), reflection);
                }
            } else if (player.canEnterInstance(151)) {
                Reflection reflection2 = ReflectionUtils.enterReflection(player, 151);
                ZoneListener zoneListener = new ZoneListener();
                reflection2.getZone("[ssq_holy_burial_ground]").addListener(zoneListener);
                ZoneListener2 zoneListener2 = new ZoneListener2();
                reflection2.getZone("[ssq_holy_seal]").addListener(zoneListener2);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    public class ZoneListener2
    implements OnZoneEnterLeaveListener {
        private boolean aye = false;

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            Player player = creature.getPlayer();
            if (player == null || !creature.isPlayer()) {
                return;
            }
            player.broadcastPacket(new EventTrigger(21100100, true));
            if (!this.aye) {
                this.aye = true;
                player.showQuestMovie(26);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
        }
    }

    public class ZoneListener
    implements OnZoneEnterLeaveListener {
        private boolean aye = false;

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            Player player = creature.getPlayer();
            if (player == null || !creature.isPlayer() || this.aye) {
                return;
            }
            this.aye = true;
            player.showQuestMovie(24);
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
        }
    }
}

