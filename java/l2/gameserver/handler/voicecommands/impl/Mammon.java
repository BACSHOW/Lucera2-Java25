/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.scripts.Functions;

public class Mammon
extends Functions
implements IVoicedCommandHandler {
    private static final int zM = 31113;
    private static final int zN = 31126;
    private String[] zF = new String[]{"merchant", "mammon"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_MAMMON_SEARCH_COMMAND) {
            return false;
        }
        if (string.equals(this.zF[0])) {
            Mammon.h(player, 31113);
            return true;
        }
        if (string.equals(this.zF[1])) {
            Mammon.h(player, 31126);
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    private static void h(Player player, int n) {
        List<NpcInstance> list = GameObjectsStorage.getAllByNpcId(n, false);
        for (NpcInstance npcInstance : list) {
            player.sendPacket((IStaticPacket)new RadarControl(2, 2, npcInstance.getLoc()));
            player.sendPacket((IStaticPacket)new RadarControl(0, 1, npcInstance.getLoc()));
        }
    }
}

