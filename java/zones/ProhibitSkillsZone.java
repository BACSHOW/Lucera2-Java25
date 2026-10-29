/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProhibitSkillsZone
implements ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(ProhibitSkillsZone.class);
    private static final String cav = "prohibitSkills";

    @Override
    public void onLoad() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            int[] nArray = zone.getParams().getIntegerArray(cav, ArrayUtils.EMPTY_INT_ARRAY);
            if (nArray.length <= 0) continue;
            zone.addListener(new ProhibitSkillsZoneListener(nArray));
            ++n;
        }
        if (n > 0) {
            sQ.info("SkillProhibitZone: added {} skill(s) prohibit zone(s).", (Object)n);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
    }

    private static final class ProhibitSkillsZoneListener
    implements OnZoneEnterLeaveListener {
        private final int[] caw;

        private ProhibitSkillsZoneListener(int[] nArray) {
            this.caw = nArray;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (!creature.isPlayer()) {
                return;
            }
            Player player = creature.getPlayer();
            LinkedList<Effect> linkedList = new LinkedList<Effect>();
            for (int i = 0; i < this.caw.length; ++i) {
                List<Effect> list;
                int n = this.caw[i];
                Skill skill = player.getKnownSkill(n);
                if (skill == null) continue;
                if (skill.isToggle() && (list = player.getEffectList().getEffectsBySkill(skill)) != null && !list.isEmpty()) {
                    linkedList.addAll(list);
                }
                player.addUnActiveSkill(skill);
                player.sendPacket((IStaticPacket)new SkillCoolTime(player));
                player.sendPacket((IStaticPacket)new SkillList(player));
                player.updateStats();
                player.updateEffectIcons();
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            if (!creature.isPlayer()) {
                return;
            }
            Player player = creature.getPlayer();
            for (int i = 0; i < this.caw.length; ++i) {
                int n = this.caw[i];
                if (!player.isUnActiveSkill(n)) continue;
                Skill skill = player.getKnownSkill(n);
                if (skill != null) {
                    player.removeUnActiveSkill(skill);
                }
                player.sendPacket((IStaticPacket)new SkillList(player));
                player.sendPacket((IStaticPacket)new SkillCoolTime(player));
                player.updateStats();
                player.updateEffectIcons();
            }
        }
    }
}

