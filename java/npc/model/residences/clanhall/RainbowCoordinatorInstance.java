/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import java.util.List;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class RainbowCoordinatorInstance
extends NpcInstance {
    public RainbowCoordinatorInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!RainbowCoordinatorInstance.canBypassCheck(player, this)) {
            return;
        }
        ClanHall clanHall = this.getClanHall();
        ClanHallMiniGameEvent clanHallMiniGameEvent = (ClanHallMiniGameEvent)clanHall.getSiegeEvent();
        if (clanHallMiniGameEvent == null) {
            return;
        }
        if (clanHallMiniGameEvent.isArenaClosed()) {
            this.showChatWindow(player, "residence2/clanhall/game_manager003.htm", new Object[0]);
            return;
        }
        List list = clanHallMiniGameEvent.getObjects("attackers");
        CMGSiegeClanObject cMGSiegeClanObject = (CMGSiegeClanObject)clanHallMiniGameEvent.getSiegeClan("attackers", player.getClan());
        if (cMGSiegeClanObject == null) {
            this.showChatWindow(player, "residence2/clanhall/game_manager014.htm", new Object[0]);
            return;
        }
        if (cMGSiegeClanObject.getPlayers().isEmpty()) {
            Party party = player.getParty();
            if (party == null) {
                this.showChatWindow(player, player.isClanLeader() ? "residence2/clanhall/game_manager005.htm" : "residence2/clanhall/game_manager002.htm", new Object[0]);
                return;
            }
            if (!player.isClanLeader()) {
                this.showChatWindow(player, "residence2/clanhall/game_manager004.htm", new Object[0]);
                return;
            }
            if (party.getMemberCount() < 5) {
                this.showChatWindow(player, "residence2/clanhall/game_manager003.htm", new Object[0]);
                return;
            }
            if (party.getPartyLeader() != player) {
                this.showChatWindow(player, "residence2/clanhall/game_manager006.htm", new Object[0]);
                return;
            }
            for (Player serializable2 : party.getPartyMembers()) {
                if (serializable2.getClan() == player.getClan()) continue;
                this.showChatWindow(player, "residence2/clanhall/game_manager007.htm", new Object[0]);
                return;
            }
            int n = list.indexOf(cMGSiegeClanObject);
            SpawnExObject spawnExObject = (SpawnExObject)clanHallMiniGameEvent.getFirstObject("arena_" + n);
            Location location = (Location)spawnExObject.getSpawns().get(0).getCurrentSpawnRange();
            for (Player player2 : party.getPartyMembers()) {
                cMGSiegeClanObject.addPlayer(player2.getObjectId());
                player2.teleToLocation(Location.coordsRandomize(location, 100, 200));
            }
        } else {
            this.showChatWindow(player, "residence2/clanhall/game_manager013.htm", new Object[0]);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        this.showChatWindow(player, "residence2/clanhall/game_manager001.htm", new Object[0]);
    }
}

