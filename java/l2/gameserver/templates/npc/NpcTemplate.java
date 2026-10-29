package l2.gameserver.templates.npc;

import gnu.trove.TIntObjectHashMap;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.util.TroveUtils;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.instances.ReflectionBossInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.StatsSet;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NpcTemplate extends CharTemplate {
   private static final Logger _log = LoggerFactory.getLogger(NpcTemplate.class);
   public static final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR;
   public static final Constructor<CharacterAI> DEFAULT_AI_CONSTRUCTOR;

   static {
      Constructor<NpcInstance> typeCtor = null;
      try {
         typeCtor = (Constructor<NpcInstance>) NpcInstance.class.getConstructor(Integer.TYPE, NpcTemplate.class);
      } catch (Exception e) {
         try {
            typeCtor = (Constructor<NpcInstance>) NpcInstance.class.getDeclaredConstructor(Integer.TYPE, NpcTemplate.class);
         } catch (Exception ignored) {
            Constructor<?>[] ctors = NpcInstance.class.getConstructors();
            if (ctors.length > 0) {
               typeCtor = (Constructor<NpcInstance>) ctors[0];
            }
         }
      }
      DEFAULT_TYPE_CONSTRUCTOR = typeCtor;

      Constructor<CharacterAI> aiCtor = null;
      try {
         aiCtor = (Constructor<CharacterAI>) CharacterAI.class.getConstructor(l2.gameserver.model.Creature.class);
      } catch (Exception e) {
         try {
            aiCtor = (Constructor<CharacterAI>) CharacterAI.class.getDeclaredConstructor(l2.gameserver.model.Creature.class);
         } catch (Exception ignored) {
            Constructor<?>[] ctors = CharacterAI.class.getConstructors();
            if (ctors.length > 0) {
               aiCtor = (Constructor<CharacterAI>) ctors[0];
            }
         }
      }
      DEFAULT_AI_CONSTRUCTOR = aiCtor;
   }
   public final int npcId;
   public final String name;
   public final String title;
   public final int level;
   public final long rewardExp;
   public final int rewardSp;
   public final int rewardRp;
   public final int aggroRange;
   public final int rhand;
   public final int lhand;
   public final int weaponEnchant;
   public final double rateHp;
   private Faction avU = Faction.NONE;
   public final String jClass;
   public final int displayId;
   public final NpcTemplate.ShotsType shots;
   public boolean isRaid = false;
   private final StatsSet avV;
   private int race = 0;
   private final int LM;
   private Map<RewardType, RewardList> _rewards = Collections.emptyMap();
   private TIntObjectHashMap<TeleportLocation[]> avW = TroveUtils.emptyIntObjectMap();
   private List<MinionData> avX = Collections.emptyList();
   private List<AbsorbInfo> avY = Collections.emptyList();
   private List<ClassId> avZ = Collections.emptyList();
   private Map<QuestEventType, Quest[]> awa = Collections.emptyMap();
   private TIntObjectHashMap<Skill> awb = TroveUtils.emptyIntObjectMap();
   private Skill[] awc = Skill.EMPTY_ARRAY;
   private Skill[] _dotSkills = Skill.EMPTY_ARRAY;
   private Skill[] _debuffSkills = Skill.EMPTY_ARRAY;
   private Skill[] _buffSkills = Skill.EMPTY_ARRAY;
   private Skill[] _stunSkills = Skill.EMPTY_ARRAY;
   private Skill[] _healSkills = Skill.EMPTY_ARRAY;
   private Class<NpcInstance> awd = NpcInstance.class;
   private Constructor<NpcInstance> awe = DEFAULT_TYPE_CONSTRUCTOR;
   private Class<CharacterAI> arX = CharacterAI.class;
   private Constructor<CharacterAI> arY = DEFAULT_AI_CONSTRUCTOR;
   private String awf;

   public NpcTemplate(StatsSet var1) {
      super(var1);
      this.npcId = var1.getInteger("npcId");
      this.displayId = var1.getInteger("displayId");
      this.name = var1.getString("name");
      this.title = var1.getString("title");
      this.level = var1.getInteger("level");
      this.rewardExp = var1.getLong("rewardExp");
      this.rewardSp = var1.getInteger("rewardSp");
      this.rewardRp = var1.getInteger("rewardRp");
      this.aggroRange = var1.getInteger("aggroRange");
      this.rhand = var1.getInteger("rhand", 0);
      this.lhand = var1.getInteger("lhand", 0);
      this.weaponEnchant = var1.getInteger("weaponEnchant", 0);
      this.rateHp = var1.getDouble("baseHpRate");
      this.jClass = var1.getString("texture", null);
      this.awf = var1.getString("htm_root", null);
      this.shots = var1.getEnum("shots", NpcTemplate.ShotsType.class, NpcTemplate.ShotsType.NONE);
      this.LM = var1.getInteger("castle_id", 0);
      this.avV = (StatsSet)var1.getObject("aiParams", StatsSet.EMPTY);
      this.setType(var1.getString("type", null));
      this.B(var1.getString("ai_type", null));
   }

   public Class<? extends NpcInstance> getInstanceClass() {
      return this.awd;
   }

   public Constructor<? extends NpcInstance> getInstanceConstructor() {
      return this.awe;
   }

   public boolean isInstanceOf(Class<?> var1) {
      return var1.isAssignableFrom(this.awd);
   }

   public NpcInstance getNewInstance() {
      try {
         return this.awe.newInstance(IdFactory.getInstance().getNextId(), this);
      } catch (Exception var2) {
         _log.error("Unable to create instance of NPC " + this.npcId, var2);
         return null;
      }
   }

   public CharacterAI getNewAI(NpcInstance var1) {
      try {
         return this.arY.newInstance(var1);
      } catch (Exception var3) {
         _log.error("Unable to create ai of NPC " + this.npcId, var3);
         return new CharacterAI(var1);
      }
   }

   private void setType(String var1) {
      Object var2 = null;

      try {
         var2 = Class.forName("l2.gameserver.model.instances." + var1 + "Instance");
      } catch (ClassNotFoundException var4) {
         var2 = Scripts.getInstance().getClasses().get("npc.model." + var1 + "Instance");
      }

      if (var2 == null) {
         _log.error("Not found type class for type: " + var1 + ". NpcId: " + this.npcId);
      } else {
         this.awd = (Class<NpcInstance>)var2;
         Constructor<NpcInstance> ctor = null;
         try {
            ctor = (Constructor<NpcInstance>) this.awd.getConstructor(Integer.TYPE, NpcTemplate.class);
         } catch (Exception e) {
            try {
               ctor = (Constructor<NpcInstance>) this.awd.getDeclaredConstructor(Integer.TYPE, NpcTemplate.class);
            } catch (Exception ignored) {
               Constructor<?>[] ctors = this.awd.getConstructors();
               if (ctors.length > 0) {
                  ctor = (Constructor<NpcInstance>) ctors[0];
               }
            }
         }
         this.awe = ctor != null ? ctor : DEFAULT_TYPE_CONSTRUCTOR;
      }

      if (this.awd.isAnnotationPresent(Deprecated.class)) {
         _log.error("Npc type: " + var1 + ", is deprecated. NpcId: " + this.npcId);
      }

      this.isRaid = this.isInstanceOf(RaidBossInstance.class) && !this.isInstanceOf(ReflectionBossInstance.class);
   }

   private void B(String var1) {
      Object var2 = null;

      try {
         var2 = Class.forName("l2.gameserver.ai." + var1);
      } catch (ClassNotFoundException var4) {
         var2 = Scripts.getInstance().getClasses().get("ai." + var1);
      }

      if (var2 == null) {
         _log.error("Not found ai class for ai: " + var1 + ". NpcId: " + this.npcId);
      } else {
         this.arX = (Class<CharacterAI>)var2;
         Constructor<CharacterAI> ctor = null;
         try {
            ctor = (Constructor<CharacterAI>) this.arX.getConstructor(l2.gameserver.model.Creature.class);
         } catch (Exception e) {
            try {
               ctor = (Constructor<CharacterAI>) this.arX.getDeclaredConstructor(l2.gameserver.model.Creature.class);
            } catch (Exception ignored) {
               Constructor<?>[] ctors = this.arX.getConstructors();
               if (ctors.length > 0) {
                  ctor = (Constructor<CharacterAI>) ctors[0];
               }
            }
         }
         this.arY = ctor != null ? ctor : DEFAULT_AI_CONSTRUCTOR;
      }

      if (this.arX.isAnnotationPresent(Deprecated.class)) {
         _log.error("Ai type: " + var1 + ", is deprecated. NpcId: " + this.npcId);
      }
   }

   public void addTeachInfo(ClassId var1) {
      if (this.avZ.isEmpty()) {
         this.avZ = new ArrayList<>(1);
      }

      this.avZ.add(var1);
   }

   public List<ClassId> getTeachInfo() {
      return this.avZ;
   }

   public boolean canTeach(ClassId var1) {
      return this.avZ.contains(var1);
   }

   public void addTeleportList(int var1, TeleportLocation[] var2) {
      if (this.avW.isEmpty()) {
         this.avW = new TIntObjectHashMap(1);
      }

      this.avW.put(var1, var2);
   }

   public TeleportLocation[] getTeleportList(int var1) {
      return (TeleportLocation[])this.avW.get(var1);
   }

   public TIntObjectHashMap<TeleportLocation[]> getTeleportList() {
      return this.avW;
   }

   public void putRewardList(RewardType var1, RewardList var2) {
      if (this._rewards.isEmpty()) {
         this._rewards = new HashMap<>(RewardType.values().length);
      }

      this._rewards.put(var1, var2);
   }

   public RewardList getRewardList(RewardType var1) {
      return this._rewards.get(var1);
   }

   public Map<RewardType, RewardList> getRewards() {
      return this._rewards;
   }

   public void addAbsorbInfo(AbsorbInfo var1) {
      if (this.avY.isEmpty()) {
         this.avY = new ArrayList<>(1);
      }

      this.avY.add(var1);
   }

   public void addMinion(MinionData var1) {
      if (this.avX.isEmpty()) {
         this.avX = new ArrayList<>(1);
      }

      this.avX.add(var1);
   }

   public void setFaction(Faction var1) {
      this.avU = var1;
   }

   public Faction getFaction() {
      return this.avU;
   }

   public void addSkill(Skill var1) {
      if (this.awb.isEmpty()) {
         this.awb = new TIntObjectHashMap();
      }

      this.awb.put(var1.getId(), var1);
      if (!var1.isNotUsedByAI()
         && var1.getTargetType() != Skill.SkillTargetType.TARGET_NONE
         && var1.getSkillType() != Skill.SkillType.NOTDONE
         && var1.isActive()) {
         switch (var1.getSkillType()) {
            case PDAM:
            case MANADAM:
            case MDAM:
            case DRAIN:
            case DRAIN_SOUL:
               boolean var2 = false;
               if (var1.hasEffects()) {
                  for (EffectTemplate var6 : var1.getEffectTemplates()) {
                     switch (var6.getEffectType()) {
                        case Stun:
                           this._stunSkills = (Skill[])ArrayUtils.add(this._stunSkills, var1);
                           var2 = true;
                           break;
                        case DamOverTime:
                        case DamOverTimeLethal:
                        case ManaDamOverTime:
                        case LDManaDamOverTime:
                           this._dotSkills = (Skill[])ArrayUtils.add(this._dotSkills, var1);
                           var2 = true;
                     }
                  }
               }

               if (!var2) {
                  this.awc = (Skill[])ArrayUtils.add(this.awc, var1);
               }
               break;
            case DOT:
            case MDOT:
            case POISON:
            case BLEED:
               this._dotSkills = (Skill[])ArrayUtils.add(this._dotSkills, var1);
               break;
            case DEBUFF:
            case SLEEP:
            case ROOT:
            case PARALYZE:
            case MUTE:
            case TELEPORT_NPC:
            case AGGRESSION:
               this._debuffSkills = (Skill[])ArrayUtils.add(this._debuffSkills, var1);
               break;
            case BUFF:
               this._buffSkills = (Skill[])ArrayUtils.add(this._buffSkills, var1);
               break;
            case STUN:
               this._stunSkills = (Skill[])ArrayUtils.add(this._stunSkills, var1);
               break;
            case HEAL:
            case HEAL_PERCENT:
            case HOT:
               this._healSkills = (Skill[])ArrayUtils.add(this._healSkills, var1);
         }
      }
   }

   public Skill[] getDamageSkills() {
      return this.awc;
   }

   public Skill[] getDotSkills() {
      return this._dotSkills;
   }

   public Skill[] getDebuffSkills() {
      return this._debuffSkills;
   }

   public Skill[] getBuffSkills() {
      return this._buffSkills;
   }

   public Skill[] getStunSkills() {
      return this._stunSkills;
   }

   public Skill[] getHealSkills() {
      return this._healSkills;
   }

   public List<MinionData> getMinionData() {
      return this.avX;
   }

   public TIntObjectHashMap<Skill> getSkills() {
      return this.awb;
   }

   public void addQuestEvent(QuestEventType var1, Quest var2) {
      if (this.awa.isEmpty()) {
         this.awa = new HashMap<>();
      }

      if (this.awa.get(var1) == null) {
         this.awa.put(var1, new Quest[]{var2});
      } else {
         Quest[] var3 = this.awa.get(var1);
         int var4 = var3.length;
         Quest[] var5 = new Quest[var4 + 1];

         for (int var6 = 0; var6 < var4; var6++) {
            if (var3[var6].getName().equals(var2.getName())) {
               var3[var6] = var2;
               return;
            }

            var5[var6] = var3[var6];
         }

         var5[var4] = var2;
         this.awa.put(var1, var5);
      }
   }

   public Quest[] getEventQuests(QuestEventType var1) {
      return this.awa.get(var1);
   }

   public int getRace() {
      return this.race;
   }

   public void setRace(int var1) {
      this.race = var1;
   }

   public boolean isUndead() {
      return this.race == 1;
   }

   @Override
   public String toString() {
      return "Npc template " + this.name + "[" + this.npcId + "]";
   }

   @Override
   public int getNpcId() {
      return this.npcId;
   }

   public String getName() {
      return this.name;
   }

   public final String getJClass() {
      return this.jClass;
   }

   public final StatsSet getAIParams() {
      return this.avV;
   }

   public List<AbsorbInfo> getAbsorbInfo() {
      return this.avY;
   }

   public int getCastleId() {
      return this.LM;
   }

   public Map<QuestEventType, Quest[]> getQuestEvents() {
      return this.awa;
   }

   public String getHtmRoot() {
      return this.awf;
   }

   public int getWeaponEnchant() {
      return this.weaponEnchant;
   }

   public enum ShotsType {
      NONE,
      SOUL,
      SPIRIT,
      BSPIRIT,
      SOUL_SPIRIT,
      SOUL_BSPIRIT;
   }
}
