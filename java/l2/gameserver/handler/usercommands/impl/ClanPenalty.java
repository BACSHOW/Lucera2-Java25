/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import java.text.SimpleDateFormat;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.Strings;

public class ClanPenalty
implements IUserCommandHandler {
    private static final int[] zy = new int[]{100, 114};

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (zy[0] != n) {
            return false;
        }
        long l = 0L;
        if (player.getLeaveClanTime() != 0L) {
            l = player.getLeaveClanTime() + 86400000L;
        }
        long l2 = 0L;
        if (player.getDeleteClanTime() != 0L) {
            l2 = player.getDeleteClanTime() + 864000000L;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String string = HtmCache.getInstance().getNotNull("command/penalty.htm", player);
        if (player.getClanId() == 0) {
            if (l == 0L && l2 == 0L) {
                string = string.replaceFirst("%reason%", "No penalty is imposed.");
                string = string.replaceFirst("%expiration%", " ");
            } else if (l > 0L && l2 == 0L) {
                string = string.replaceFirst("%reason%", "Penalty for leaving clan.");
                string = string.replaceFirst("%expiration%", simpleDateFormat.format(l));
            } else if (l2 > 0L) {
                string = string.replaceFirst("%reason%", "Penalty for dissolving clan.");
                string = string.replaceFirst("%expiration%", simpleDateFormat.format(l2));
            }
        } else if (player.getClan().canInvite()) {
            string = string.replaceFirst("%reason%", "No penalty is imposed.");
            string = string.replaceFirst("%expiration%", " ");
        } else {
            string = string.replaceFirst("%reason%", "Penalty for expelling clan member.");
            string = string.replaceFirst("%expiration%", simpleDateFormat.format(player.getClan().getExpelledMemberTime()));
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        npcHtmlMessage.setHtml(Strings.bbParse(string));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return zy;
    }
}

