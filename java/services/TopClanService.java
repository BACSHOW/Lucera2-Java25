/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.Arrays;
import java.util.Comparator;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.ClanTable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class TopClanService
extends Functions
implements IVoicedCommandHandler,
ScriptFile {
    private static String bYm = "scripts/services/";
    private String[] zF = new String[]{"topclans", "topclan"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.SERVICES_TOP_CLANS_STATISTIC) {
            return false;
        }
        if (this.zF[0].equalsIgnoreCase(string) || this.zF[1].equalsIgnoreCase(string)) {
            this.topClans(player);
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        if (!Config.SERVICES_TOP_CLANS_STATISTIC) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return this.zF;
    }

    public void topClans() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        this.topClans(player);
    }

    public void topClans(Player player) {
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_TOP_CLANS_STATISTIC) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        Clan[] clanArray = ArrayUtils.clone(ClanTable.getInstance().getClans());
        Arrays.sort(clanArray, Comparator.comparingInt(Clan::getCustomPoints).reversed());
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile(bYm + "top_clans.htm");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Math.min(clanArray.length, Config.SERVICES_TOP_CLANS_STATISTIC_LIST); ++i) {
            Clan clan = clanArray[i];
            String string = StringHolder.getInstance().getNotNull(player, "TopClanService.StringAddress");
            string = string.replace("%clanName%", StringUtils.trimToEmpty(clan.getName()));
            string = string.replace("%clanLeaderName%", StringUtils.trimToEmpty(clan.getLeaderName()));
            string = string.replace("%clanRP%", String.valueOf(clan.getCustomPoints()));
            stringBuilder.append(string);
        }
        npcHtmlMessage.replace("%content%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public void onLoad() {
        if (Config.SERVICES_TOP_CLANS_STATISTIC) {
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

