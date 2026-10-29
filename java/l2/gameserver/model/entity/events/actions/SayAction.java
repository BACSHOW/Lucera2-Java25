/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.Say2;

public class SayAction
implements EventAction {
    private int Qt;
    private ChatType Qu;
    private String Qz;
    private NpcString Qv;
    private SysString QA;
    private SystemMsg QB;

    protected SayAction(int n, ChatType chatType) {
        this.Qt = n;
        this.Qu = chatType;
    }

    public SayAction(int n, ChatType chatType, SysString sysString, SystemMsg systemMsg) {
        this(n, chatType);
        this.QA = sysString;
        this.QB = systemMsg;
    }

    public SayAction(int n, ChatType chatType, String string, NpcString npcString) {
        this(n, chatType);
        this.Qv = npcString;
        this.Qz = string;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        List<Player> list = globalEvent.broadcastPlayers(this.Qt);
        for (Player player : list) {
            this.R(player);
        }
    }

    private void R(Player player) {
        if (player == null) {
            return;
        }
        Say2 say2 = null;
        say2 = this.QA != null ? new Say2(0, this.Qu, this.QA, this.QB) : new Say2(0, this.Qu, this.Qz, this.Qv, new String[0]);
        player.sendPacket((IStaticPacket)say2);
    }
}

