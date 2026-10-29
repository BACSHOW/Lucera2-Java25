/*
 * Decompiled with CFR 0.152.
 */
package services;

import bosses.AntharasManager;
import bosses.BaiumManager;
import bosses.EpicBossState;
import bosses.FrintezzaManager;
import bosses.ValakasManager;
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

public class BossStatusService
extends Functions
implements IVoicedCommandHandler,
ScriptFile {
    private String[] zF = new String[]{"boss_status", "epic"};
    private static BossStatusInfo[] bWa = null;
    private static SimpleDateFormat lC = new SimpleDateFormat(Config.SERVICES_BOSS_STATUS_FORMAT);

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.SERVICES_BOSS_STATUS_ENABLE) {
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
        if (!Config.SERVICES_BOSS_STATUS_ENABLE) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return this.zF;
    }

    private static BossStatusInfo af(int n) {
        switch (n) {
            case 29020: {
                return new EpicBossStatusInfo(n, BaiumManager.getEpicBossState());
            }
            case 29028: {
                return new EpicBossStatusInfo(n, ValakasManager.getEpicBossState());
            }
            case 29045: {
                return new EpicBossStatusInfo(n, FrintezzaManager.getInstance().getEpicBossState());
            }
            case 29068: {
                return new EpicBossStatusInfo(n, AntharasManager.getEpicBossState());
            }
        }
        return null;
    }

    private static BossStatusInfo[] dR() {
        ArrayList<BossStatusInfo> arrayList = new ArrayList<BossStatusInfo>();
        for (int n : Config.SERVICES_BOSS_STATUS_ADDITIONAL_IDS) {
            BossStatusInfo bossStatusInfo = BossStatusService.af(n);
            if (null != bossStatusInfo) {
                arrayList.add(bossStatusInfo);
                continue;
            }
            arrayList.add(new RaidBossStatusInfo(n));
        }
        return arrayList.toArray(new BossStatusInfo[arrayList.size()]);
    }

    private static BossStatusInfo[] dS() {
        if (bWa == null) {
            bWa = BossStatusService.dR();
            return bWa;
        }
        return bWa;
    }

    private static String a(Player player, BossStatusInfo bossStatusInfo) {
        String string = StringHolder.getInstance().getNotNull(player, "scripts.services.BossStatusService." + (Object)((Object)bossStatusInfo.getStatus()));
        long l = bossStatusInfo.getRespawnDate();
        string = string.replace("%name%", bossStatusInfo.getName());
        string = string.replace("%npc_id%", String.valueOf(bossStatusInfo.getNpcId()));
        string = string.replace("%respawn_date%", l > 0L ? lC.format(l * 1000L) : "");
        return string;
    }

    public void listBossStatusesWithMsg(Player player) {
        if (!Config.SERVICES_BOSS_STATUS_ENABLE) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        BossStatusInfo[] bossStatusInfoArray = BossStatusService.dS();
        StringBuilder stringBuilder = new StringBuilder();
        for (BossStatusInfo bossStatusInfo : bossStatusInfoArray) {
            stringBuilder.append(BossStatusService.a(player, bossStatusInfo));
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        npcHtmlMessage.setFile("scripts/services/command_bossstatus.htm");
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        npcHtmlMessage.replace("%current_date%", TimeUtils.toSimpleFormat(System.currentTimeMillis()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void listBossStatuses() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        NpcInstance npcInstance = this.getNpc();
        if (!Config.SERVICES_BOSS_STATUS_ENABLE) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        BossStatusInfo[] bossStatusInfoArray = BossStatusService.dS();
        StringBuilder stringBuilder = new StringBuilder();
        for (BossStatusInfo bossStatusInfo : bossStatusInfoArray) {
            stringBuilder.append(BossStatusService.a(player, bossStatusInfo));
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.setFile("scripts/services/bossstatus.htm");
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        npcHtmlMessage.replace("%current_date%", TimeUtils.toSimpleFormat(System.currentTimeMillis()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public void onLoad() {
        if (Config.SERVICES_BOSS_STATUS_ENABLE) {
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

    public static final class EpicBossStatusInfo
    extends BossStatusInfo {
        private final EpicBossState lG;

        public EpicBossStatusInfo(int n, EpicBossState epicBossState) {
            super(n);
            this.lG = epicBossState;
        }

        @Override
        public BossStatusInfo.BossStatus getStatus() {
            switch (this.lG.getState()) {
                case ALIVE: {
                    return BossStatusInfo.BossStatus.ALIVE;
                }
                case NOTSPAWN: {
                    return BossStatusInfo.BossStatus.READY;
                }
                case DEAD: {
                    return BossStatusInfo.BossStatus.DEAD;
                }
            }
            return BossStatusInfo.BossStatus.RESPAWN;
        }

        @Override
        public long getRespawnDate() {
            return this.lG.getRespawnDate() / 1000L;
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

