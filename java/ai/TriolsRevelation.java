/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.Collections;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;

public class TriolsRevelation
extends Mystic {
    private static int dc = 3;
    private static int dd = 500;
    private static Skill de = SkillTable.getInstance().getInfo(4088, 8);

    public TriolsRevelation(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
        this.AI_TASK_ATTACK_DELAY = 1250L;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead() || !Rnd.chance(dc)) {
            return false;
        }
        List<Player> list = World.getAroundPlayers(npcInstance, dd, 200);
        if (list == null || list.isEmpty()) {
            return false;
        }
        Collections.shuffle(list);
        for (Player player : list) {
            if (player.getEffectList().getEffectsCountForSkill(de.getId()) >= 1) continue;
            de.getEffects(npcInstance, player, false, false);
            break;
        }
        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

