/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import org.apache.commons.lang3.tuple.Pair;

public class ClanBuffService
extends RunnableImpl
implements ScriptFile {
    private static final ClanBuffService bWD = new ClanBuffService();
    private static ScheduledFuture<?> bWE;
    private static int bWF;
    private static List<Pair<Integer, Skill>> bWG;

    private static final List<Pair<Integer, Skill>> F(String string) {
        ArrayList<Pair<Integer, Skill>> arrayList = new ArrayList<Pair<Integer, Skill>>();
        StringTokenizer stringTokenizer = new StringTokenizer(string, ";");
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken().trim();
            if (string2.isEmpty()) continue;
            int n = Math.max(Math.max(string2.indexOf(44), string2.indexOf(45)), string2.indexOf(58));
            if (n <= 0) {
                throw new IllegalArgumentException("Can't parse \"" + string2 + "\"");
            }
            arrayList.add(Pair.of(Integer.parseInt(string2.substring(0, n).trim()), SkillTable.getInstance().getInfo(bWF, Integer.parseInt(string2.substring(n + 1).trim()))));
        }
        arrayList.sort(new Comparator<Pair<Integer, Skill>>(){

            @Override
            public int compare(Pair<Integer, Skill> pair, Pair<Integer, Skill> pair2) {
                return Integer.compare(pair.getKey(), pair2.getKey());
            }
        });
        return arrayList;
    }

    private static Skill a(int n, List<Pair<Integer, Skill>> list) {
        Skill skill = null;
        for (Pair<Integer, Skill> pair : list) {
            if (n < pair.getLeft()) {
                return skill;
            }
            skill = pair.getRight();
        }
        return skill;
    }

    @Override
    public void onLoad() {
        if (Config.SERVICES_CLAN_BUFF_ENABLED) {
            bWF = Config.SERVICES_CLAN_BUFF_SKILL_ID;
            bWG = ClanBuffService.F(Config.SERVICES_CLAN_BUFF_LEVELS_MIN_ONLINE);
            bWE = ThreadPoolManager.getInstance().scheduleAtFixedRate(bWD, 60000L, Config.SERVICES_CLAN_BUFF_TASK_DELAY * 60L * 1000L);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        if (bWE != null) {
            bWE.cancel(false);
            bWE = null;
        }
    }

    @Override
    public void runImpl() throws Exception {
        for (Clan clan : ClanTable.getInstance().getClans()) {
            List<Player> list = clan.getOnlineMembers(0);
            if (list.isEmpty()) continue;
            Skill skill = ClanBuffService.a(list.size(), bWG);
            if (skill == null) {
                for (Player player : list) {
                    player.getEffectList().stopEffect(bWF);
                }
                continue;
            }
            for (Player player : list) {
                if (player.isDead() || player.isInDuel() || player.isAlikeDead() || player.isOlyParticipant() || ParticipantPool.getInstance().isRegistred(player)) continue;
                skill.getEffects(player, player, false, false, 0L, 0.0, false);
            }
        }
    }
}

