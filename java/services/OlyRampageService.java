/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.listener.actor.player.OnOlyCompetitionListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.tuple.Pair;

public class OlyRampageService
extends Functions
implements OnOlyCompetitionListener,
ScriptFile {
    private static final String bXB = "oly_rampage_counter";
    private static boolean enabled = false;
    private static List<Pair<Integer, String>> bXC;

    @Override
    public void onLoad() {
        if (Config.OLYMPIAD_WINNER_ANNOUCE) {
            enabled = true;
            ArrayList<Pair<Integer, String>> arrayList = new ArrayList<Pair<Integer, String>>();
            StringTokenizer stringTokenizer = new StringTokenizer(Config.OLYMPIAD_WINNER_MESSAGE, ";");
            while (stringTokenizer.hasMoreTokens()) {
                String string = stringTokenizer.nextToken().trim();
                int n = string.indexOf(58);
                if (n < 0) {
                    throw new RuntimeException("Can't parse \"" + string + "\"");
                }
                arrayList.add(Pair.of(Integer.parseInt(string.substring(0, n).trim()), string.substring(n + 1).trim()));
            }
            bXC = arrayList;
            CharListenerList.addGlobal(this);
        } else {
            enabled = false;
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        CharListenerList.removeGlobal(this);
    }

    @Override
    public void onOlyCompetitionCompleted(Player player, Competition competition, boolean bl) {
        if (!enabled || player == null || competition == null) {
            return;
        }
        int n = player.getVarInt(bXB, 0);
        if (!bl) {
            n = 0;
            player.setVar(bXB, n, -1L);
        } else {
            ++n;
            for (Pair<Integer, String> pair : bXC) {
                if (pair.getLeft() != n) continue;
                Announcements.getInstance().announceByCustomMessage(pair.getRight(), new String[]{player.getName()}, ChatType.ANNOUNCEMENT);
            }
            player.setVar(bXB, n, -1L);
        }
    }
}

