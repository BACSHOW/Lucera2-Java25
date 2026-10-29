/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class MatchMassTeleporterInstance
extends NpcInstance {
    private int ayK;
    private long qi;

    public MatchMassTeleporterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.ayK = npcTemplate.getAIParams().getInteger("flag");
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        ClanHall clanHall = this.getClanHall();
        ClanHallTeamBattleEvent clanHallTeamBattleEvent = (ClanHallTeamBattleEvent)clanHall.getSiegeEvent();
        if (this.qi > System.currentTimeMillis()) {
            this.showChatWindow(player, "residence2/clanhall/agit_mass_teleporter001.htm", new Object[0]);
            return;
        }
        if (this.isInActingRange(player)) {
            this.qi = System.currentTimeMillis() + 60000L;
            List list = clanHallTeamBattleEvent.getObjects("tryout_part");
            CTBTeamObject cTBTeamObject = (CTBTeamObject)list.get(this.ayK);
            if (cTBTeamObject.getFlag() != null) {
                for (Player player2 : World.getAroundPlayers(this, 400, 100)) {
                    player2.teleToLocation(Location.findPointToStay(cTBTeamObject.getFlag(), 100, 125));
                }
            }
        }
    }
}

