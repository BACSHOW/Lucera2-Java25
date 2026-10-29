/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoBuffZone
implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(AutoBuffZone.class);

    private static final boolean aO(Player player) {
        if (player == null || player.isDead()) {
            return false;
        }
        if (player.isCursedWeaponEquipped() || player.isFakeDeath() || player.isFlying()) {
            return false;
        }
        if (player.isInDuel() || player.isOlyParticipant() || player.isInStoreMode() || player.isSitting()) {
            return false;
        }
        return player.getEvent(SiegeEvent.class) == null;
    }

    private static List<Pair<Skill, Integer>> P(String string) {
        ArrayList<Pair<Skill, Integer>> arrayList = new ArrayList<Pair<Skill, Integer>>();
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",;");
        while (stringTokenizer.hasMoreTokens()) {
            int n;
            String string2 = stringTokenizer.nextToken().trim();
            if (string2.isEmpty()) continue;
            int n2 = string2.indexOf(47);
            String string3 = string2;
            Integer n3 = null;
            if (n2 > 0) {
                n3 = Integer.parseInt(string2.substring(n2 + 1).trim());
                string3 = string2.substring(0, n2).trim();
            }
            if ((n = string3.indexOf(58)) < 0) {
                throw new IllegalArgumentException("Can't parse \"" + string2 + "\"");
            }
            int n4 = Integer.parseInt(string3.substring(0, n).trim());
            int n5 = Integer.parseInt(string3.substring(n + 1).trim());
            Skill skill = SkillTable.getInstance().getInfo(n4, n5);
            if (skill == null) {
                throw new IllegalArgumentException("Unknown skill \"" + string2 + "\"");
            }
            arrayList.add(Pair.of(skill, n3));
        }
        arrayList.trimToSize();
        return Collections.unmodifiableList(arrayList);
    }

    private static Map<Predicate<Player>, Pair<Skill, Integer>> Q(String string) {
        LinkedHashMap<Predicate<Player>, Pair<Skill, Integer>> linkedHashMap = new LinkedHashMap<Predicate<Player>, Pair<Skill, Integer>>();
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",;");
        while (stringTokenizer.hasMoreTokens()) {
            int n;
            String string2 = StringUtils.trimToEmpty(stringTokenizer.nextToken().trim());
            if (string2.isEmpty()) continue;
            if (string2.charAt(string2.length() - 1) != ')') {
                throw new IllegalArgumentException("Can't parse \"" + string2 + "\". No class(id)");
            }
            int n2 = string2.indexOf(40);
            if (n2 <= 0) {
                throw new IllegalArgumentException("Can't parse \"" + string2 + "\". No class(id)");
            }
            String string3 = StringUtils.trimToEmpty(string2.substring(0, n2));
            ClassId classId2 = StringUtils.isNumeric(string3) ? ClassId.getClassById(Integer.parseInt(string3)) : Stream.of(ClassId.values()).filter(classId -> StringUtils.equalsIgnoreCase(classId.toString(), string3)).findFirst().orElseThrow(() -> new IllegalArgumentException("Can't parse \"" + string2 + "\""));
            String string4 = StringUtils.trimToEmpty(string2.substring(n2 + 1, string2.length() - 1));
            int n3 = string4.indexOf(47);
            Integer n4 = null;
            if (n3 > 0) {
                n4 = Integer.parseInt(string4.substring(n3 + 1).trim());
                string4 = string4.substring(0, n3).trim();
            }
            if ((n = string4.indexOf(58)) < 0) {
                throw new IllegalArgumentException("Can't parse \"" + string2 + "\"");
            }
            int n5 = Integer.parseInt(string4.substring(0, n).trim());
            int n6 = Integer.parseInt(string4.substring(n + 1).trim());
            Skill skill = SkillTable.getInstance().getInfo(n5, n6);
            if (skill == null) {
                throw new IllegalArgumentException("Unknown skill \"" + string2 + "\"");
            }
            linkedHashMap.put(player -> player != null && player.isConnected() && player.getClassId() == classId2, Pair.of(skill, n4));
        }
        return Collections.unmodifiableMap(linkedHashMap);
    }

    private static void init() {
        Object object;
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone object2 : collection) {
            String string = object2.getParams().getString("zoneBuffs", null);
            if (StringUtils.isBlank(string) || ((List)(object = AutoBuffZone.P(string))).isEmpty()) continue;
            object2.addListener(new AutoBuffZoneListener((List)object));
            ++n;
        }
        if (n > 0) LOG.info("AutoBuffZone: Loaded " + n + " auto buff zone(s).");
        int n2 = 0;
        for (Zone zone : collection) {
            Map<Predicate<Player>, Pair<Skill, Integer>> map;
            object = zone.getParams().getString("zoneClassedBuffs", null);
            if (StringUtils.isBlank((CharSequence)object) || (map = AutoBuffZone.Q((String)object)).isEmpty()) continue;
            zone.addListener(new AutoBuffClassedZoneListener(map));
            ++n2;
        }
        if (n2 > 0) LOG.info("AutoBuffZone: Loaded " + n2 + " classed auto buff zone(s).");
    }

    @Override
    public void onLoad() {
        AutoBuffZone.init();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static class AutoBuffClassedZoneListener
    implements OnZoneEnterLeaveListener {
        private final Map<Predicate<Player>, Pair<Skill, Integer>> bZp;

        private AutoBuffClassedZoneListener(Map<Predicate<Player>, Pair<Skill, Integer>> map) {
            this.bZp = map;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (creature == null || !creature.isPlayer()) {
                return;
            }
            Player player = creature.getPlayer();
            if (!AutoBuffZone.aO(player)) {
                return;
            }
            final HardReference<Player> hardReference = player.getRef();
            ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                @Override
                public void runImpl() {
                    Player player = (Player)hardReference.get();
                    if (player == null) {
                        return;
                    }
                    for (Map.Entry entry : bZp.entrySet()) {
                        Predicate predicate = (Predicate)entry.getKey();
                        if (!predicate.test(player)) continue;
                        Pair pair = (Pair)entry.getValue();
                        Skill skill = (Skill)pair.getLeft();
                        Integer n = (Integer)pair.getRight();
                        if (player.getEffectList().containEffectFromSkills(skill.getId())) continue;
                        if (n != null) {
                            skill.getEffects(player, player, false, false, (long)n.intValue() * 1000L, 1.0, false);
                            continue;
                        }
                        skill.getEffects(player, player, false, false);
                    }
                }
            });
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
        }
    }

    private static class AutoBuffZoneListener
    implements OnZoneEnterLeaveListener {
        private final List<Pair<Skill, Integer>> bZq;

        private AutoBuffZoneListener(List<Pair<Skill, Integer>> list) {
            this.bZq = list;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (creature == null || !creature.isPlayer()) {
                return;
            }
            Player player = creature.getPlayer();
            if (!AutoBuffZone.aO(player)) {
                return;
            }
            final HardReference<Player> hardReference = player.getRef();
            ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                @Override
                public void runImpl() throws Exception {
                    Player player = (Player)hardReference.get();
                    if (player == null) {
                        return;
                    }
                    for (Pair pair : bZq) {
                        Skill skill = (Skill)pair.getLeft();
                        Integer n = (Integer)pair.getRight();
                        if (player.getEffectList().containEffectFromSkills(skill.getId())) continue;
                        if (n != null) {
                            skill.getEffects(player, player, false, false, (long)n.intValue() * 1000L, 1.0, false);
                            continue;
                        }
                        skill.getEffects(player, player, false, false);
                    }
                }
            });
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
        }
    }
}

