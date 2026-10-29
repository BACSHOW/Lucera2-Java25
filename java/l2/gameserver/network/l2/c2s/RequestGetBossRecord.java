/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Map;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExGetBossRecord;

public class RequestGetBossRecord
extends L2GameClientPacket {
    private int abY;

    @Override
    protected void readImpl() {
        this.abY = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        int n = 0;
        int n2 = 0;
        if (player == null) {
            return;
        }
        ArrayList<ExGetBossRecord.BossRecordInfo> arrayList = new ArrayList<ExGetBossRecord.BossRecordInfo>();
        Map<Integer, Integer> map = RaidBossSpawnManager.getInstance().getPointsForOwnerId(player.getObjectId());
        if (map != null && !map.isEmpty()) {
            block4: for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                switch (entry.getKey()) {
                    case -1: {
                        n2 = entry.getValue();
                        continue block4;
                    }
                    case 0: {
                        n = entry.getValue();
                        continue block4;
                    }
                }
                arrayList.add(new ExGetBossRecord.BossRecordInfo(entry.getKey(), entry.getValue(), 0));
            }
        }
        player.sendPacket((IStaticPacket)new ExGetBossRecord(n2, n, arrayList));
    }
}

