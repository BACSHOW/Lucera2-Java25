/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.skillclasses.Call;
import l2.gameserver.templates.StatsSet;

public class ClanGate
extends Skill {
    public ClanGate(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (!creature.isPlayer()) {
            return false;
        }
        Player player = (Player)creature;
        Clan clan = player.getClan();
        if (clan == null || !player.isClanLeader() || clan.getCastle() == 0) {
            player.sendPacket((IStaticPacket)Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return false;
        }
        SystemMessage systemMessage = Call.canSummonHere(player);
        if (systemMessage != null) {
            creature.sendPacket((IStaticPacket)systemMessage);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = (Player)creature;
        Clan clan = player.getClan();
        clan.broadcastToOtherOnlineMembers(Msg.COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED, player);
        this.getEffects(creature, creature, this.getActivateRate() > 0, true);
    }
}

