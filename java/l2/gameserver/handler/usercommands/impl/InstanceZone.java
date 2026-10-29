/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.InstantZone;

public class InstanceZone
implements IUserCommandHandler {
    private static final int[] zy = new int[]{114};

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (zy[0] != n) {
            return false;
        }
        if (player.getActiveReflection() != null) {
            player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.INSTANT_ZONE_CURRENTLY_IN_USE_S1).addInstanceName(player.getActiveReflection().getInstancedZoneId()));
        }
        boolean bl = true;
        boolean bl2 = false;
        for (int n2 : player.getInstanceReuses().keySet()) {
            int n3;
            InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(n2);
            if (instantZone == null || (n3 = instantZone.getMinutesToNextEntrance(player)) <= 0) continue;
            bl = false;
            if (!bl2) {
                player.sendPacket((IStaticPacket)SystemMsg.INSTANCE_ZONE_TIME_LIMIT);
                bl2 = true;
            }
            player.sendPacket((IStaticPacket)((SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES).addInstanceName(instantZone.getId())).addInteger(n3 / 60)).addInteger(n3 % 60));
        }
        if (bl) {
            player.sendPacket((IStaticPacket)SystemMsg.THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT);
        }
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return zy;
    }
}

