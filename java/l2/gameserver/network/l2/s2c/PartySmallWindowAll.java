/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PartySmallWindowAll
extends L2GameServerPacket {
    private int akv;
    private int agE;
    private List<PartySmallWindowMemberInfo> members = new ArrayList<PartySmallWindowMemberInfo>();

    public PartySmallWindowAll(Party party, Player player) {
        this.akv = party.getPartyLeader().getObjectId();
        this.agE = party.getLootDistribution();
        for (Player player2 : party.getPartyMembers()) {
            if (player2 == player) continue;
            this.members.add(new PartySmallWindowMemberInfo(player2));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(78);
        this.writeD(this.akv);
        this.writeD(this.agE);
        this.writeD(this.members.size());
        for (PartySmallWindowMemberInfo partySmallWindowMemberInfo : this.members) {
            this.writeD(partySmallWindowMemberInfo._id);
            this.writeS(partySmallWindowMemberInfo._name);
            this.writeD(partySmallWindowMemberInfo.curCp);
            this.writeD(partySmallWindowMemberInfo.maxCp);
            this.writeD(partySmallWindowMemberInfo.curHp);
            this.writeD(partySmallWindowMemberInfo.maxHp);
            this.writeD(partySmallWindowMemberInfo.curMp);
            this.writeD(partySmallWindowMemberInfo.maxMp);
            this.writeD(partySmallWindowMemberInfo.level);
            this.writeD(partySmallWindowMemberInfo.class_id);
            this.writeD(0);
            this.writeD(partySmallWindowMemberInfo.race_id);
        }
    }

    public static class PartySmallWindowMemberInfo {
        public String _name;
        public int _id;
        public int curCp;
        public int maxCp;
        public int curHp;
        public int maxHp;
        public int curMp;
        public int maxMp;
        public int level;
        public int class_id;
        public int race_id;

        public PartySmallWindowMemberInfo(Player player) {
            this._name = player.getName();
            this._id = player.getObjectId();
            this.curCp = (int)player.getCurrentCp();
            this.maxCp = player.getMaxCp();
            this.curHp = (int)player.getCurrentHp();
            this.maxHp = player.getMaxHp();
            this.curMp = (int)player.getCurrentMp();
            this.maxMp = player.getMaxMp();
            this.level = player.getLevel();
            this.class_id = player.getClassId().getId();
            this.race_id = player.getRace().ordinal();
        }
    }
}

