/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class QuestList
extends L2GameServerPacket {
    private List<int[]> alC;

    public QuestList(Player player) {
        QuestState[] questStateArray = player.getAllQuestsStates();
        this.alC = new ArrayList<int[]>(questStateArray.length);
        for (QuestState questState : questStateArray) {
            if (!questState.getQuest().isVisible() || !questState.isStarted()) continue;
            this.alC.add(new int[]{questState.getQuest().getQuestIntId(), questState.getInt("cond")});
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(128);
        this.writeH(this.alC.size());
        for (int[] nArray : this.alC) {
            this.writeD(nArray[0]);
            this.writeD(nArray[1]);
        }
    }
}

