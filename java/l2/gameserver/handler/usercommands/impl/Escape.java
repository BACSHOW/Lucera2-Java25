/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.SkillTable;

public class Escape
implements IUserCommandHandler {
    private static final int[] zy = new int[]{52};

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (n != zy[0]) {
            return false;
        }
        if (player.isMovementDisabled() || player.isOlyParticipant() || player.isAfraid()) {
            return false;
        }
        if (player.getTeleMode() != 0 || !player.getPlayerAccess().UseTeleport || this.K(player)) {
            player.sendMessage(new CustomMessage("common.TryLater", player, new Object[0]));
            return false;
        }
        if (player.isInDuel() || player.getTeam() != TeamType.NONE) {
            player.sendMessage(new CustomMessage("common.RecallInDuel", player, new Object[0]));
            return false;
        }
        player.abortAttack(true, true);
        player.abortCast(true, true);
        player.stopMove();
        Skill skill = player.getPlayerAccess().FastUnstuck ? SkillTable.getInstance().getInfo(1050, 2) : SkillTable.getInstance().getInfo(2099, 1);
        if (skill != null && skill.checkCondition(player, player, false, false, true)) {
            player.getAI().Cast(skill, player, false, true);
        }
        return true;
    }

    private boolean K(Player player) {
        return (Boolean)Scripts.getInstance().callScripts(player, "events.TvT2.PvPEvent", "isEventPartisipant") != false;
    }

    @Override
    public final int[] getUserCommandList() {
        return zy;
    }
}

