/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.ItemFunctions;

public class ReflectionUtils {
    public static DoorInstance getDoor(int n) {
        return ReflectionManager.DEFAULT.getDoor(n);
    }

    public static List<DoorInstance> getDoors(int ... nArray) {
        ArrayList<DoorInstance> arrayList = new ArrayList<DoorInstance>(nArray.length);
        for (int i = 0; i < nArray.length; ++i) {
            DoorInstance doorInstance = ReflectionUtils.getDoor(nArray[i]);
            if (doorInstance == null) continue;
            arrayList.add(doorInstance);
        }
        return arrayList;
    }

    public static Zone getZone(String string) {
        return ReflectionManager.DEFAULT.getZone(string);
    }

    public static List<Zone> getZonesByType(Zone.ZoneType zoneType) {
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        if (collection.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Zone> arrayList = new ArrayList<Zone>(5);
        for (Zone zone : collection) {
            if (!zone.isType(zoneType)) continue;
            arrayList.add(zone);
        }
        return arrayList;
    }

    public static Reflection enterReflection(Player player, int n) {
        InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(n);
        return ReflectionUtils.enterReflection(player, new Reflection(), instantZone);
    }

    public static Reflection enterReflection(Player player, Reflection reflection, int n) {
        InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(n);
        return ReflectionUtils.enterReflection(player, reflection, instantZone);
    }

    public static Reflection enterReflection(Player player, Reflection reflection, InstantZone instantZone) {
        reflection.init(instantZone);
        if (reflection.getReturnLoc() == null) {
            reflection.setReturnLoc(player.getLoc());
        }
        switch (instantZone.getEntryType()) {
            case SOLO: {
                if (instantZone.getRemovedItemId() > 0) {
                    ItemFunctions.removeItem(player, instantZone.getRemovedItemId(), instantZone.getRemovedItemCount(), true);
                }
                if (instantZone.getGiveItemId() > 0) {
                    ItemFunctions.addItem((Playable)player, instantZone.getGiveItemId(), (long)instantZone.getGiveItemCount(), true);
                }
                if (instantZone.isDispelBuffs()) {
                    player.dispelBuffs();
                }
                if (instantZone.getSetReuseUponEntry() && instantZone.getResetReuse().next(System.currentTimeMillis()) > System.currentTimeMillis()) {
                    player.setInstanceReuse(instantZone.getId(), System.currentTimeMillis());
                }
                player.setVar("backCoords", player.getLoc().toXYZString(), -1L);
                player.teleToLocation(instantZone.getTeleportCoord(), reflection);
                break;
            }
            case PARTY: {
                Party party = player.getParty();
                party.setReflection(reflection);
                reflection.setParty(party);
                for (Player player2 : party.getPartyMembers()) {
                    if (instantZone.getRemovedItemId() > 0) {
                        ItemFunctions.removeItem(player2, instantZone.getRemovedItemId(), instantZone.getRemovedItemCount(), true);
                    }
                    if (instantZone.getGiveItemId() > 0) {
                        ItemFunctions.addItem((Playable)player2, instantZone.getGiveItemId(), (long)instantZone.getGiveItemCount(), true);
                    }
                    if (instantZone.isDispelBuffs()) {
                        player2.dispelBuffs();
                    }
                    if (instantZone.getSetReuseUponEntry()) {
                        player2.setInstanceReuse(instantZone.getId(), System.currentTimeMillis());
                    }
                    player2.setVar("backCoords", player.getLoc().toXYZString(), -1L);
                    player2.teleToLocation(instantZone.getTeleportCoord(), reflection);
                }
                break;
            }
            case COMMAND_CHANNEL: {
                Party party = player.getParty();
                CommandChannel commandChannel = party.getCommandChannel();
                commandChannel.setReflection(reflection);
                reflection.setCommandChannel(commandChannel);
                for (Player player3 : commandChannel) {
                    if (instantZone.getRemovedItemId() > 0) {
                        ItemFunctions.removeItem(player3, instantZone.getRemovedItemId(), instantZone.getRemovedItemCount(), true);
                    }
                    if (instantZone.getGiveItemId() > 0) {
                        ItemFunctions.addItem((Playable)player3, instantZone.getGiveItemId(), (long)instantZone.getGiveItemCount(), true);
                    }
                    if (instantZone.isDispelBuffs()) {
                        player3.dispelBuffs();
                    }
                    if (instantZone.getSetReuseUponEntry()) {
                        player3.setInstanceReuse(instantZone.getId(), System.currentTimeMillis());
                    }
                    player3.setVar("backCoords", player.getLoc().toXYZString(), -1L);
                    player3.teleToLocation(instantZone.getTeleportCoord(), reflection);
                }
                break;
            }
        }
        return reflection;
    }
}

