/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.InstantZone;

public class InstanceZone
extends Functions
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"instancezone"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_INSTANCE_COMMAND) {
            return false;
        }
        if (player == null) {
            return false;
        }
        if (player.getActiveReflection() != null) {
            player.sendMessage(new CustomMessage("INSTANT_ZONE_CURRENTLY_IN_USE_S1", player, new Object[0]).addString(player.getActiveReflection().getName()));
        }
        boolean bl = true;
        boolean bl2 = false;
        for (int n : player.getInstanceReuses().keySet()) {
            int n2;
            InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(n);
            if (instantZone == null || (n2 = instantZone.getMinutesToNextEntrance(player)) <= 0) continue;
            bl = false;
            if (!bl2) {
                player.sendMessage(new CustomMessage("INSTANCE_ZONE_TIME_LIMIT", player, new Object[0]));
                bl2 = true;
            }
            player.sendMessage(new CustomMessage("S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES", player, new Object[0]).addString(instantZone.getName()).addNumber(n2 / 60).addNumber(n2 % 60));
        }
        if (bl) {
            player.sendMessage(new CustomMessage("THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT", player, new Object[0]));
        }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }
}

