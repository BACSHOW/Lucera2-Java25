/*
 * Decompiled with CFR 0.152.
 */
package handler.admincommands;

import bosses.AntharasManager;
import bosses.BaiumManager;
import bosses.EpicBossState;
import bosses.FrintezzaManager;
import bosses.ValakasManager;
import handler.admincommands.ScriptAdminCommand;
import java.text.SimpleDateFormat;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminBossStatus
extends ScriptAdminCommand {
    private static final Logger _log = LoggerFactory.getLogger(AdminBossStatus.class);
    private static SimpleDateFormat lC = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    private static BossStatusInfo g(int n) {
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

    private static BossStatusInfo h(int n) {
        BossStatusInfo bossStatusInfo = AdminBossStatus.g(n);
        if (bossStatusInfo != null) {
            return bossStatusInfo;
        }
        if (RaidBossSpawnManager.getInstance().getRaidBossStatusId(n) != RaidBossSpawnManager.Status.UNDEFINED) {
            return new RaidBossStatusInfo(n);
        }
        return null;
    }

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        switch (commands) {
            case admin_boss_status: {
                if (stringArray.length != 2) {
                    player.sendMessage("Usage: //boss_status <npc_id>");
                    return false;
                }
                int n = Integer.parseInt(stringArray[1]);
                try {
                    this.c(player, n);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void c(Player player, int n) {
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        BossStatusInfo bossStatusInfo = AdminBossStatus.h(n);
        if (bossStatusInfo == null) {
            player.sendMessage(String.format("No boss status for npc with id %d", n));
            return;
        }
        BossStatusInfo.BossStatus bossStatus = bossStatusInfo.getStatus();
        long l = bossStatusInfo.getRespawnDate();
        if (l > 0L) {
            player.sendMessage(String.format("Boss %d[%s] status: %s. Respawn date: %s", n, npcTemplate.getName(), bossStatus.toString(), lC.format(l * 1000L)));
        } else {
            player.sendMessage(String.format("Boss %d[%s] status: %s", n, npcTemplate.getName(), bossStatus.toString()));
        }
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

    public static enum Commands {
        admin_boss_status;

    }
}

