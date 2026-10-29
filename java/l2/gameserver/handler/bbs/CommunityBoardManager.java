/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.bbs;

import java.util.HashMap;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardManager {
    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardManager.class);
    private static final CommunityBoardManager zo = new CommunityBoardManager();
    private final Map<String, ICommunityBoardHandler> zp = new HashMap<String, ICommunityBoardHandler>();
    private final StatsSet zq = new StatsSet();

    public static CommunityBoardManager getInstance() {
        return zo;
    }

    private CommunityBoardManager() {
    }

    public void registerHandler(ICommunityBoardHandler iCommunityBoardHandler) {
        for (String string : iCommunityBoardHandler.getBypassCommands()) {
            if (this.zp.containsKey(string)) {
                _log.warn("CommunityBoard: dublicate bypass registered! First handler: " + this.zp.get(string).getClass().getSimpleName() + " second: " + iCommunityBoardHandler.getClass().getSimpleName());
            }
            this.zp.put(string, iCommunityBoardHandler);
        }
    }

    public void removeHandler(ICommunityBoardHandler iCommunityBoardHandler) {
        for (String string : iCommunityBoardHandler.getBypassCommands()) {
            this.zp.remove(string);
        }
        _log.info("CommunityBoard: " + iCommunityBoardHandler.getClass().getSimpleName() + " unloaded.");
    }

    public ICommunityBoardHandler getCommunityHandler(String string, Player player) {
        if (!Config.COMMUNITYBOARD_ENABLED || this.zp.isEmpty()) {
            return null;
        }
        if (player != null && Config.BBS_PEACE_ONLY && !player.isInZonePeace()) {
            player.sendMessage(new CustomMessage("alt.bbs_available_at_town_only", player, new Object[0]));
            return null;
        }
        for (Map.Entry<String, ICommunityBoardHandler> entry : this.zp.entrySet()) {
            if (!string.contains(entry.getKey())) continue;
            return entry.getValue();
        }
        return null;
    }

    public void setProperty(String string, String string2) {
        this.zq.set(string, string2);
    }

    public void setProperty(String string, int n) {
        this.zq.set(string, n);
    }

    public int getIntProperty(String string) {
        return this.zq.getInteger(string, 0);
    }
}

