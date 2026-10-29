/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.model.quest.QuestNpcLogInfo;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExQuestNpcLogList
extends L2GameServerPacket {
    private int _questId;
    private List<int[]> ahD = Collections.emptyList();

    public ExQuestNpcLogList(QuestState questState) {
        this._questId = questState.getQuest().getQuestIntId();
        int n = questState.getCond();
        List<QuestNpcLogInfo> list = questState.getQuest().getNpcLogList(n);
        if (list == null) {
            return;
        }
        this.ahD = new ArrayList<int[]>(list.size());
        for (QuestNpcLogInfo questNpcLogInfo : list) {
            int[] nArray = new int[]{questNpcLogInfo.getNpcIds()[0] + 1000000, questState.getInt(questNpcLogInfo.getVarName())};
            this.ahD.add(nArray);
        }
    }

    @Override
    protected void writeImpl() {
        this.writeEx(197);
        this.writeD(this._questId);
        this.writeC(this.ahD.size());
        for (int i = 0; i < this.ahD.size(); ++i) {
            int[] nArray = this.ahD.get(i);
            this.writeD(nArray[0]);
            this.writeC(0);
            this.writeD(nArray[1]);
        }
    }
}

