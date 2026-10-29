/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminDelete
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditNPC) {
            return false;
        }
        switch (commands) {
            case admin_delete: {
                GameObject gameObject;
                GameObject gameObject2 = gameObject = stringArray.length == 1 ? player.getTarget() : GameObjectsStorage.getNpc(NumberUtils.toInt(stringArray[1]));
                if (gameObject != null && gameObject.isNpc()) {
                    NpcInstance npcInstance = (NpcInstance)gameObject;
                    npcInstance.deleteMe();
                    Spawner spawner = npcInstance.getSpawn();
                    if (spawner == null) break;
                    spawner.stopRespawn();
                    break;
                }
                player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_delete;

    }
}

