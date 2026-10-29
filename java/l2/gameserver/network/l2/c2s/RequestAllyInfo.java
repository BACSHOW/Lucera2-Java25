/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.ClanTable;

public class RequestAllyInfo
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Alliance alliance = player.getAlliance();
        if (alliance == null) {
            return;
        }
        int n = 0;
        Clan clan = player.getAlliance().getLeader();
        n = ClanTable.getInstance().getAlliance(clan.getAllyId()).getMembers().length;
        int[] nArray = new int[n + 1];
        int[] nArray2 = new int[n + 1];
        Clan[] clanArray = player.getAlliance().getMembers();
        for (int i = 0; i < n; ++i) {
            nArray[i + 1] = clanArray[i].getOnlineMembers(0).size();
            nArray2[i + 1] = clanArray[i].getAllSize();
            nArray[0] = nArray[0] + nArray[i + 1];
            nArray2[0] = nArray2[0] + nArray2[i + 1];
        }
        ArrayList<SystemMessage> arrayList = new ArrayList<SystemMessage>(7 + 5 * n);
        arrayList.add(Msg._ALLIANCE_INFORMATION_);
        arrayList.add(new SystemMessage(492).addString(player.getClan().getAlliance().getAllyName()));
        arrayList.add(new SystemMessage(493).addNumber(nArray[0]).addNumber(nArray2[0]));
        arrayList.add(new SystemMessage(494).addString(clan.getName()).addString(clan.getLeaderName()));
        arrayList.add(new SystemMessage(495).addNumber(n));
        arrayList.add(Msg._CLAN_INFORMATION_);
        for (int i = 0; i < n; ++i) {
            arrayList.add(new SystemMessage(497).addString(clanArray[i].getName()));
            arrayList.add(new SystemMessage(498).addString(clanArray[i].getLeaderName()));
            arrayList.add(new SystemMessage(499).addNumber(clanArray[i].getLevel()));
            arrayList.add(new SystemMessage(493).addNumber(nArray[i + 1]).addNumber(nArray2[i + 1]));
            arrayList.add(Msg.__DASHES__);
        }
        arrayList.add(Msg.__EQUALS__);
        player.sendPacket(arrayList);
    }
}

