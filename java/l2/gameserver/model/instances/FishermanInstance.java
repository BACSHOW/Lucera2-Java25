/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.games.FishingChampionShipManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class FishermanInstance
extends MerchantInstance {
    public FishermanInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = "";
        string = n2 == 0 ? "" + n : n + "-" + n2;
        return "fisherman/" + string + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!FishermanInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("FishingSkillList")) {
            FishermanInstance.showFishingSkillList(player);
        } else if (string.startsWith("FishingChampionship") && Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.getInstance().showChampScreen(player, this);
        } else if (string.startsWith("FishingReward") && Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.getInstance().getReward(player);
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}

