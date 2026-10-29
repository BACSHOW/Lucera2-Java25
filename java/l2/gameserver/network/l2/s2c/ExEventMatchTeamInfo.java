/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExEventMatchTeamInfo
extends L2GameServerPacket {
    private int agD;
    private int agE;
    private List<EventMatchTeamInfo> members = new ArrayList<EventMatchTeamInfo>();

    public ExEventMatchTeamInfo(List<Player> list, Player player) {
        this.agD = list.get(0).getObjectId();
        this.agE = list.get(0).getParty().getLootDistribution();
        for (Player player2 : list) {
            if (player2.equals(player)) continue;
            this.members.add(new EventMatchTeamInfo(player2));
        }
    }

    @Override
    protected void writeImpl() {
        this.writeEx(28);
    }

    public static class EventMatchTeamInfo {
        public String _name;
        public String pet_Name;
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
        public int pet_id;
        public int pet_NpcId;
        public int pet_curHp;
        public int pet_maxHp;
        public int pet_curMp;
        public int pet_maxMp;
        public int pet_level;

        public EventMatchTeamInfo(Player player) {
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
            Summon summon = player.getPet();
            if (summon != null) {
                this.pet_id = summon.getObjectId();
                this.pet_NpcId = summon.getNpcId() + 1000000;
                this.pet_Name = summon.getName();
                this.pet_curHp = (int)summon.getCurrentHp();
                this.pet_maxHp = summon.getMaxHp();
                this.pet_curMp = (int)summon.getCurrentMp();
                this.pet_maxMp = summon.getMaxMp();
                this.pet_level = summon.getLevel();
            } else {
                this.pet_id = 0;
            }
        }
    }
}

