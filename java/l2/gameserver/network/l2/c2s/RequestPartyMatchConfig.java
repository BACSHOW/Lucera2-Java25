/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.CCMatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ListPartyWaiting;

public class RequestPartyMatchConfig
extends L2GameClientPacket {
    private int _page;
    private int acn;
    private int aco;

    @Override
    protected void readImpl() {
        this._page = this.readD();
        this.acn = this.readD();
        this.aco = this.readD();
    }

    @Override
    protected void runImpl() {
        CommandChannel commandChannel;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Party party = player.getParty();
        CommandChannel commandChannel2 = commandChannel = party != null ? party.getCommandChannel() : null;
        if (commandChannel != null && commandChannel.getChannelLeader() == player) {
            if (commandChannel.getMatchingRoom() == null) {
                CCMatchingRoom cCMatchingRoom = new CCMatchingRoom(player, 1, player.getLevel(), 50, party.getLootDistribution(), player.getName());
                commandChannel.setMatchingRoom(cCMatchingRoom);
            }
        } else if (commandChannel != null && !commandChannel.getParties().contains(party)) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_COMMAND_CHANNEL_AFFILIATED_PARTYS_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
        } else if (party != null && !party.isLeader(player)) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
        } else {
            MatchingRoomManager.getInstance().addToWaitingList(player);
            player.sendPacket((IStaticPacket)new ListPartyWaiting(this.acn, this.aco == 1, this._page, player));
        }
    }
}

