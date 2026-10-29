/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.actions;

import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.NpcSay;
import l2.gameserver.utils.MapUtils;

public class NpcSayAction
implements EventAction {
    private int _npcId;
    private int Qt;
    private ChatType Qu;
    private NpcString Qv;

    public NpcSayAction(int n, int n2, ChatType chatType, NpcString npcString) {
        this._npcId = n;
        this.Qt = n2;
        this.Qu = chatType;
        this.Qv = npcString;
    }

    @Override
    public void call(GlobalEvent globalEvent) {
        NpcInstance npcInstance = GameObjectsStorage.getByNpcId(this._npcId);
        if (npcInstance == null) {
            return;
        }
        if (this.Qt <= 0) {
            int n = MapUtils.regionX(npcInstance);
            int n2 = MapUtils.regionY(npcInstance);
            int n3 = Config.SHOUT_OFFSET;
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                if (npcInstance.getReflection() != player.getReflection()) continue;
                int n4 = MapUtils.regionX(player);
                int n5 = MapUtils.regionY(player);
                if (n4 < n - n3 || n4 > n + n3 || n5 < n2 - n3 || n5 > n2 + n3) continue;
                this.a(npcInstance, player);
            }
        } else {
            for (Player player : World.getAroundPlayers(npcInstance, this.Qt, Math.max(this.Qt / 2, 200))) {
                if (npcInstance.getReflection() != player.getReflection()) continue;
                this.a(npcInstance, player);
            }
        }
    }

    private void a(NpcInstance npcInstance, Player player) {
        player.sendPacket((IStaticPacket)new NpcSay(npcInstance, this.Qu, this.Qv, new String[0]));
    }
}

