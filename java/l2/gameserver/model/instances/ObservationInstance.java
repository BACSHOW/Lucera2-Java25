/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.StringTokenizer;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public final class ObservationInstance
extends NpcInstance {
    public ObservationInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ObservationInstance.canBypassCheck(player, this)) {
            return;
        }
        if (player.isOlyParticipant()) {
            return;
        }
        if (string.startsWith("observeSiege")) {
            String string2 = string.substring(13);
            StringTokenizer stringTokenizer = new StringTokenizer(string2);
            stringTokenizer.nextToken();
            ArrayList<Zone> arrayList = new ArrayList<Zone>();
            World.getZones(arrayList, new Location(Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken())), ReflectionManager.DEFAULT);
            for (Zone zone : arrayList) {
                if (!zone.isType(Zone.ZoneType.SIEGE) || !zone.isActive()) continue;
                this.m(player, string2);
                return;
            }
            player.sendPacket((IStaticPacket)SystemMsg.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
        } else if (string.startsWith("observe")) {
            this.m(player, string.substring(8));
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = "";
        string = n2 == 0 ? "" + n : n + "-" + n2;
        return "observation/" + string + ".htm";
    }

    private void m(Player player, String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        int n = Integer.parseInt(stringTokenizer.nextToken());
        int n2 = Integer.parseInt(stringTokenizer.nextToken());
        int n3 = Integer.parseInt(stringTokenizer.nextToken());
        int n4 = Integer.parseInt(stringTokenizer.nextToken());
        if (!player.reduceAdena(n, true)) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            player.sendActionFailed();
            return;
        }
        player.enterObserverMode(new Location(n2, n3, n4));
    }
}

