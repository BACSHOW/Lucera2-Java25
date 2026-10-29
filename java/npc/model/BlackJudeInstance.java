/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class BlackJudeInstance
extends NpcInstance {
    public BlackJudeInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!BlackJudeInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equals("tryRemovePenalty")) {
            if (player.getDeathPenalty().getLevel() > 0) {
                this.showChatWindow(player, 2, "%price%", this.getPrice(player));
            } else {
                this.showChatWindow(player, 1, new Object[0]);
            }
        } else if (string.equals("removePenalty")) {
            if (player.getDeathPenalty().getLevel() > 0) {
                if (player.getAdena() >= (long)this.getPrice(player)) {
                    player.reduceAdena(this.getPrice(player), true);
                    this.doCast(SkillTable.getInstance().getInfo(5077, 1), player, false);
                } else {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                }
            } else {
                this.showChatWindow(player, 1, new Object[0]);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    public int getPrice(Player player) {
        int n = player.getLevel();
        if (n <= 19) {
            return 3600;
        }
        if (n >= 20 && n <= 39) {
            return 16400;
        }
        if (n >= 40 && n <= 51) {
            return 36200;
        }
        if (n >= 52 && n <= 60) {
            return 50400;
        }
        if (n >= 61 && n <= 75) {
            return 78200;
        }
        return 102800;
    }
}

