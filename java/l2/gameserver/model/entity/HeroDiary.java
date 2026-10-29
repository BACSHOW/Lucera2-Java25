/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Map;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.HtmlUtils;

public class HeroDiary {
    private static final SimpleDateFormat Pu = new SimpleDateFormat("HH:** dd.MM.yyyy");
    public static final int ACTION_RAID_KILLED = 1;
    public static final int ACTION_HERO_GAINED = 2;
    public static final int ACTION_CASTLE_TAKEN = 3;
    private int _id;
    private long Pv;
    private int Pw;

    public HeroDiary(int n, long l, int n2) {
        this._id = n;
        this.Pv = l;
        this.Pw = n2;
    }

    public Map.Entry<String, String> toString(Player player) {
        CustomMessage customMessage = null;
        switch (this._id) {
            case 1: {
                customMessage = new CustomMessage("l2p.gameserver.model.entity.Hero.RaidBossKilled", player, new Object[0]).addString(HtmlUtils.htmlNpcName(this.Pw));
                break;
            }
            case 2: {
                customMessage = new CustomMessage("l2p.gameserver.model.entity.Hero.HeroGained", player, new Object[0]);
                break;
            }
            case 3: {
                customMessage = new CustomMessage("l2p.gameserver.model.entity.Hero.CastleTaken", player, new Object[0]).addString(HtmlUtils.htmlResidenceName(this.Pw));
                break;
            }
            default: {
                return null;
            }
        }
        return new AbstractMap.SimpleEntry<String, String>(Pu.format(this.Pv), customMessage.toString());
    }
}

