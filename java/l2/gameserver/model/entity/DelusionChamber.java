/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class DelusionChamber
extends DimensionalRift {
    private Future<?> Pr;

    public DelusionChamber(Party party, int n, int n2) {
        super(party, n, n2);
    }

    @Override
    public synchronized void createNewKillRiftTimer() {
        if (this.Pr != null) {
            this.Pr.cancel(false);
            this.Pr = null;
        }
        this.Pr = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                if (DelusionChamber.this.getParty() != null && !DelusionChamber.this.getParty().getPartyMembers().isEmpty()) {
                    for (Player player : DelusionChamber.this.getParty().getPartyMembers()) {
                        String string;
                        if (player.getReflection() != DelusionChamber.this || (string = player.getVar("backCoords")) == null || string.equals("")) continue;
                        player.teleToLocation(Location.parseLoc(string), ReflectionManager.DEFAULT);
                        player.unsetVar("backCoords");
                    }
                }
                DelusionChamber.this.collapse();
            }
        }, 100L);
    }

    @Override
    public void partyMemberExited(Player player) {
        if (this.getPlayersInside(false) < 2 || this.getPlayersInside(true) == 0) {
            this.createNewKillRiftTimer();
            return;
        }
    }

    @Override
    public void manualExitRift(Player player, NpcInstance npcInstance) {
        if (!player.isInParty() || player.getParty().getReflection() != this) {
            return;
        }
        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npcInstance);
            return;
        }
        this.createNewKillRiftTimer();
    }

    @Override
    public String getName() {
        InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this._roomType + 120);
        return instantZone.getName();
    }

    @Override
    protected int getManagerId() {
        return 32664;
    }
}

