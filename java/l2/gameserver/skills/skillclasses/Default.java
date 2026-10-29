/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Default
extends Skill {
    private static final Logger _log = LoggerFactory.getLogger(Default.class);

    public Default(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (creature.isPlayer()) {
            creature.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Default.NotImplemented", (Player)creature, new Object[0]).addNumber(this.getId()).addString("" + (Object)((Object)this.getSkillType())));
        }
        _log.warn("NOTDONE skill: " + this.getId() + ", used by" + creature);
        creature.sendActionFailed();
    }
}

