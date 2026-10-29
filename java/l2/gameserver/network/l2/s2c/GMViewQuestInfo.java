/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GMViewQuestInfo
extends L2GameServerPacket {
    private final Player ajj;

    public GMViewQuestInfo(Player player) {
        this.ajj = player;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(147);
        this.writeS(this.ajj.getName());
        Quest[] questArray = this.ajj.getAllActiveQuests();
        if (questArray.length == 0) {
            this.writeC(0);
            this.writeH(0);
            this.writeH(0);
            return;
        }
        this.writeH(questArray.length);
        for (Quest quest : questArray) {
            this.writeD(quest.getQuestIntId());
            QuestState questState = this.ajj.getQuestState(quest.getName());
            this.writeD(questState == null ? 0 : questState.getInt("cond"));
        }
    }
}

