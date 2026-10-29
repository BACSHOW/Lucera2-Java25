/*
 * Decompiled with CFR 0.152.
 */
package events.Christmas;

import java.util.Calendar;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;

public class NewYearTimer
implements ScriptFile {
    private static NewYearTimer gO;

    public static NewYearTimer getInstance() {
        if (gO == null) {
            new NewYearTimer();
        }
        return gO;
    }

    public NewYearTimer() {
        if (gO != null) {
            return;
        }
        gO = this;
        if (!NewYearTimer.isActive()) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, Calendar.getInstance().get(1));
        calendar.set(2, 0);
        calendar.set(5, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        while (this.a(calendar) < 0L) {
            calendar.set(1, calendar.get(1) + 1);
        }
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("\u0421 \u041d\u043e\u0432\u044b\u043c, " + calendar.get(1) + ", \u0413\u043e\u0434\u043e\u043c!!!"), this.a(calendar));
        calendar.add(13, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("1"), this.a(calendar));
        calendar.add(13, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("2"), this.a(calendar));
        calendar.add(13, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("3"), this.a(calendar));
        calendar.add(13, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("4"), this.a(calendar));
        calendar.add(13, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("5"), this.a(calendar));
    }

    private long a(Calendar calendar) {
        return calendar.getTime().getTime() - System.currentTimeMillis();
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    private static boolean isActive() {
        return ServerVariables.getString("Christmas", "off").equalsIgnoreCase("on");
    }

    @Override
    public void onShutdown() {
    }

    private class NewYearAnnouncer
    extends RunnableImpl {
        private final String message;

        private NewYearAnnouncer(String string) {
            this.message = string;
        }

        @Override
        public void runImpl() throws Exception {
            Announcements.getInstance().announceToAll(this.message);
            if (this.message.length() == 1) {
                return;
            }
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                Skill skill = SkillTable.getInstance().getInfo(3266, 1);
                MagicSkillUse magicSkillUse = new MagicSkillUse(player, player, 3266, 1, skill.getHitTime(), 0L);
                player.broadcastPacket(magicSkillUse);
            }
            gO = null;
            new NewYearTimer();
        }
    }
}

