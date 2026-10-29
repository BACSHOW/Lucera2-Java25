/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.ArrayUtils;

public class RaidBossStatusService
extends Functions
implements IVoicedCommandHandler,
ScriptFile {
    private String[] zF = new String[]{"raid", "rb"};
    private static BossStatusInfo[] bXJ = null;
    private static SimpleDateFormat lC = new SimpleDateFormat(Config.SERVICES_RAID_STATUS_FORMAT);

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.SERVICES_RAID_STATUS_ENABLE) {
            return false;
        }
        if (this.zF[0].equalsIgnoreCase(string) || this.zF[1].equalsIgnoreCase(string)) {
            this.listBossStatusesWithMsg(player);
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        if (!Config.SERVICES_RAID_STATUS_ENABLE) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return this.zF;
    }

    private static BossStatusInfo[] dY() {
        ArrayList<RaidBossStatusInfo> arrayList = new ArrayList<RaidBossStatusInfo>();
        for (int n : Config.SERVICES_RAID_STATUS_ADDITIONAL_IDS) {
            arrayList.add(new RaidBossStatusInfo(n));
        }
        return arrayList.toArray(new BossStatusInfo[arrayList.size()]);
    }

    private static BossStatusInfo[] dZ() {
        if (bXJ == null) {
            bXJ = RaidBossStatusService.dY();
            return bXJ;
        }
        return bXJ;
    }

    private static String a(Player player, BossStatusInfo bossStatusInfo) {
        String string = StringHolder.getInstance().getNotNull(player, "scripts.services.RaidStatusService." + (Object)((Object)bossStatusInfo.getStatus()));
        long l = bossStatusInfo.getRespawnDate();
        string = string.replace("%name%", bossStatusInfo.getName());
        string = string.replace("%npc_id%", String.valueOf(bossStatusInfo.getNpcId()));
        string = string.replace("%respawn_date%", l > 0L ? lC.format(l * 1000L) : "");
        return string;
    }

    public void listBossStatusesWithMsg(Player player) {
        if (!Config.SERVICES_RAID_STATUS_ENABLE) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        BossStatusInfo[] bossStatusInfoArray = RaidBossStatusService.dZ();
        StringBuilder stringBuilder = new StringBuilder();
        for (BossStatusInfo bossStatusInfo : bossStatusInfoArray) {
            stringBuilder.append(RaidBossStatusService.a(player, bossStatusInfo));
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        npcHtmlMessage.setFile("scripts/services/command_raidstatus.htm");
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        npcHtmlMessage.replace("%current_date%", TimeUtils.toSimpleFormat(System.currentTimeMillis()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void listRaidBossStatuses() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        NpcInstance npcInstance = this.getNpc();
        if (!Config.SERVICES_BOSS_STATUS_ENABLE) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        BossStatusInfo[] bossStatusInfoArray = RaidBossStatusService.dZ();
        StringBuilder stringBuilder = new StringBuilder();
        for (BossStatusInfo bossStatusInfo : bossStatusInfoArray) {
            stringBuilder.append(RaidBossStatusService.a(player, bossStatusInfo));
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.setFile("scripts/services/raidstatus.htm");
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        npcHtmlMessage.replace("%current_date%", TimeUtils.toSimpleFormat(System.currentTimeMillis()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public void onLoad() {
        if (Config.SERVICES_RAID_STATUS_ENABLE) {
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
    }

    public static final class RaidBossStatusInfo
    extends BossStatusInfo {
        public RaidBossStatusInfo(int n) {
            super(n);
        }

        @Override
        public BossStatusInfo.BossStatus getStatus() {
            if (RaidBossSpawnManager.getInstance().getRaidBossStatusId(this.getNpcId()) == RaidBossSpawnManager.Status.DEAD) {
                return BossStatusInfo.BossStatus.DEAD;
            }
            long l = this.getRespawnDate();
            if (l * 1000L > System.currentTimeMillis()) {
                return BossStatusInfo.BossStatus.DEAD;
            }
            return BossStatusInfo.BossStatus.ALIVE;
        }

        @Override
        public long getRespawnDate() {
            Spawner spawner = RaidBossSpawnManager.getInstance().getSpawnTable().get(this.getNpcId());
            if (spawner == null) {
                return -1L;
            }
            return spawner.getRespawnTime();
        }
    }

    private static abstract class BossStatusInfo {
        private final int lD;

        public BossStatusInfo(int n) {
            this.lD = n;
        }

        public int getNpcId() {
            return this.lD;
        }

        public String getName() {
            NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(this.getNpcId());
            if (npcTemplate == null) {
                return "";
            }
            return npcTemplate.getName();
        }

        public abstract BossStatus getStatus();

        public abstract long getRespawnDate();

        public static enum BossStatus {
            ALIVE,
            DEAD,
            READY,
            RESPAWN;

        }
    }
}

