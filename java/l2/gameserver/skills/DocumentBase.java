package l2.gameserver.skills;

import java.io.File;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.conditions.ConditionClanPlayerMinPledgeRank;
import l2.gameserver.stats.conditions.ConditionFirstEffectSuccess;
import l2.gameserver.stats.conditions.ConditionGameTime;
import l2.gameserver.stats.conditions.ConditionHasSkill;
import l2.gameserver.stats.conditions.ConditionIsPremium;
import l2.gameserver.stats.conditions.ConditionLogicAnd;
import l2.gameserver.stats.conditions.ConditionLogicNot;
import l2.gameserver.stats.conditions.ConditionLogicOr;
import l2.gameserver.stats.conditions.ConditionPlayerAgathion;
import l2.gameserver.stats.conditions.ConditionPlayerChargesMax;
import l2.gameserver.stats.conditions.ConditionPlayerChargesMin;
import l2.gameserver.stats.conditions.ConditionPlayerClassId;
import l2.gameserver.stats.conditions.ConditionPlayerClassIsMage;
import l2.gameserver.stats.conditions.ConditionPlayerCubic;
import l2.gameserver.stats.conditions.ConditionPlayerForbiddenClassId;
import l2.gameserver.stats.conditions.ConditionPlayerGender;
import l2.gameserver.stats.conditions.ConditionPlayerHasBuff;
import l2.gameserver.stats.conditions.ConditionPlayerHasBuffId;
import l2.gameserver.stats.conditions.ConditionPlayerInCombat;
import l2.gameserver.stats.conditions.ConditionPlayerInTeam;
import l2.gameserver.stats.conditions.ConditionPlayerInstanceZone;
import l2.gameserver.stats.conditions.ConditionPlayerIsClanLeader;
import l2.gameserver.stats.conditions.ConditionPlayerIsHero;
import l2.gameserver.stats.conditions.ConditionPlayerIsInAcademy;
import l2.gameserver.stats.conditions.ConditionPlayerMaxLevel;
import l2.gameserver.stats.conditions.ConditionPlayerMaxPK;
import l2.gameserver.stats.conditions.ConditionPlayerMinLevel;
import l2.gameserver.stats.conditions.ConditionPlayerMinMaxDamage;
import l2.gameserver.stats.conditions.ConditionPlayerOlympiad;
import l2.gameserver.stats.conditions.ConditionPlayerOnPvPEvent;
import l2.gameserver.stats.conditions.ConditionPlayerPercentCp;
import l2.gameserver.stats.conditions.ConditionPlayerPercentHp;
import l2.gameserver.stats.conditions.ConditionPlayerPercentMp;
import l2.gameserver.stats.conditions.ConditionPlayerPrivateStore;
import l2.gameserver.stats.conditions.ConditionPlayerRace;
import l2.gameserver.stats.conditions.ConditionPlayerRiding;
import l2.gameserver.stats.conditions.ConditionPlayerSkillMinSeed;
import l2.gameserver.stats.conditions.ConditionPlayerState;
import l2.gameserver.stats.conditions.ConditionPlayerSummonSiegeGolem;
import l2.gameserver.stats.conditions.ConditionSlotItemId;
import l2.gameserver.stats.conditions.ConditionTargetAggro;
import l2.gameserver.stats.conditions.ConditionTargetCastleDoor;
import l2.gameserver.stats.conditions.ConditionTargetClan;
import l2.gameserver.stats.conditions.ConditionTargetDirection;
import l2.gameserver.stats.conditions.ConditionTargetForbiddenClassId;
import l2.gameserver.stats.conditions.ConditionTargetHasBuff;
import l2.gameserver.stats.conditions.ConditionTargetHasBuffId;
import l2.gameserver.stats.conditions.ConditionTargetHasForbiddenSkill;
import l2.gameserver.stats.conditions.ConditionTargetInTheSameParty;
import l2.gameserver.stats.conditions.ConditionTargetMob;
import l2.gameserver.stats.conditions.ConditionTargetMobId;
import l2.gameserver.stats.conditions.ConditionTargetNpcClass;
import l2.gameserver.stats.conditions.ConditionTargetPercentCp;
import l2.gameserver.stats.conditions.ConditionTargetPercentHp;
import l2.gameserver.stats.conditions.ConditionTargetPercentMp;
import l2.gameserver.stats.conditions.ConditionTargetPlayable;
import l2.gameserver.stats.conditions.ConditionTargetPlayer;
import l2.gameserver.stats.conditions.ConditionTargetPlayerNotMe;
import l2.gameserver.stats.conditions.ConditionTargetPlayerRace;
import l2.gameserver.stats.conditions.ConditionTargetRace;
import l2.gameserver.stats.conditions.ConditionTargetSummon;
import l2.gameserver.stats.conditions.ConditionUsingArmor;
import l2.gameserver.stats.conditions.ConditionUsingBlowSkill;
import l2.gameserver.stats.conditions.ConditionUsingItemType;
import l2.gameserver.stats.conditions.ConditionUsingSkill;
import l2.gameserver.stats.conditions.ConditionZoneName;
import l2.gameserver.stats.conditions.ConditionZoneType;
import l2.gameserver.stats.funcs.EFunction;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ArmorTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@Deprecated
abstract class DocumentBase {
   private static final Logger _log = LoggerFactory.getLogger(DocumentBase.class);
   private File file;

   DocumentBase(File var1) {
      this.file = var1;
   }

   Document parse() {
      Document var1;
      try {
         DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
         var2.setValidating(false);
         var2.setIgnoringComments(true);
         var1 = var2.newDocumentBuilder().parse(this.file);
      } catch (Exception var4) {
         _log.error("Error loading file " + this.file, var4);
         return null;
      }

      try {
         this.parseDocument(var1);
         return var1;
      } catch (Exception var3) {
         _log.error("Error in file " + this.file, var3);
         return null;
      }
   }

   protected abstract void parseDocument(Document var1);

   protected abstract Object getTableValue(String var1);

   protected abstract Object getTableValue(String var1, int var2);

   protected void parseTemplate(Node var1, StatTemplate var2) {
      var1 = var1.getFirstChild();
      if (var1 != null) {
         for (; var1 != null; var1 = var1.getNextSibling()) {
            if (var1.getNodeType() != 3) {
               String var3 = var1.getNodeName();
               if (EFunction.VALUES_BY_LOWER_NAME.containsKey(var3.toLowerCase())) {
                  this.attachFunc(var1, var2, EFunction.VALUES_BY_LOWER_NAME.get(var3.toLowerCase()));
               } else if ("effect".equalsIgnoreCase(var3)) {
                  if (var2 instanceof EffectTemplate) {
                     throw new RuntimeException("Nested effects");
                  }

                  this.attachEffect(var1, var2);
               } else {
                  if (!(var2 instanceof EffectTemplate)) {
                     throw new RuntimeException("Unknown template " + var3);
                  }

                  if ("def".equalsIgnoreCase(var3)) {
                     EffectTemplate var4 = (EffectTemplate)var2;
                     StatsSet var5 = var4.getParam();
                     Skill var6 = (Skill)var5.getObject("object");
                     this.parseBeanSet(var1, var5, var6.getLevel());
                  } else {
                     Condition var8 = this.parseCondition(var1);
                     if (var8 != null) {
                        ((EffectTemplate)var2).attachCond(var8);
                     }
                  }
               }
            }
         }
      }
   }

   protected void parseTrigger(Node var1, StatTemplate var2) {
      for (Node var12 = var1.getFirstChild(); var12 != null; var12 = var12.getNextSibling()) {
         if ("trigger".equalsIgnoreCase(var12.getNodeName())) {
            NamedNodeMap var3 = var12.getAttributes();
            int var4 = this.parseNumber(var3.getNamedItem("id").getNodeValue()).intValue();
            int var5 = this.parseNumber(var3.getNamedItem("level").getNodeValue()).intValue();
            TriggerType var6 = TriggerType.valueOf(var3.getNamedItem("type").getNodeValue());
            double var7 = this.parseNumber(var3.getNamedItem("chance").getNodeValue()).doubleValue();
            TriggerInfo var9 = new TriggerInfo(var4, var5, var6, var7);
            var2.addTrigger(var9);

            for (Node var10 = var12.getFirstChild(); var10 != null; var10 = var10.getNextSibling()) {
               Condition var11 = this.parseCondition(var12.getFirstChild());
               if (var11 != null) {
                  var9.addCondition(var11);
               }
            }
         }
      }
   }

   protected void attachFunc(Node var1, StatTemplate var2, String var3) {
      Stats var4 = Stats.valueOfXml(var1.getAttributes().getNamedItem("stat").getNodeValue());
      String var5 = var1.getAttributes().getNamedItem("order").getNodeValue();
      int var6 = this.parseNumber(var5).intValue();
      Condition var7 = this.parseCondition(var1.getFirstChild());
      double var8 = 0.0;
      if (var1.getAttributes().getNamedItem("val") != null) {
         var8 = this.parseNumber(var1.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
      }

      var2.attachFunc(new FuncTemplate(var7, var3, var4, var6, var8));
   }

   protected void attachFunc(Node var1, StatTemplate var2, EFunction var3) {
      Stats var4 = Stats.valueOfXml(var1.getAttributes().getNamedItem("stat").getNodeValue());
      String var5 = var1.getAttributes().getNamedItem("order").getNodeValue();
      int var6 = this.parseNumber(var5).intValue();
      Condition var7 = this.parseCondition(var1.getFirstChild());
      double var8 = 0.0;
      if (var1.getAttributes().getNamedItem("val") != null) {
         var8 = this.parseNumber(var1.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();
      }

      var2.attachFunc(new FuncTemplate(var7, var3, var4, var6, var8));
   }

   protected void attachEffect(Node var1, Object var2) {
      NamedNodeMap var3 = var1.getAttributes();
      StatsSet var4 = new StatsSet();
      var4.set("name", var3.getNamedItem("name").getNodeValue());
      var4.set("object", var2);
      if (var3.getNamedItem("count") != null) {
         var4.set("count", this.parseNumber(var3.getNamedItem("count").getNodeValue()).intValue());
      }

      if (var3.getNamedItem("time") != null) {
         var4.set("time", this.parseNumber(var3.getNamedItem("time").getNodeValue()).intValue());
      }

      var4.set("value", var3.getNamedItem("val") != null ? this.parseNumber(var3.getNamedItem("val").getNodeValue()).doubleValue() : 0.0);
      var4.set("abnormal", AbnormalEffect.NULL);
      var4.set("abnormal2", AbnormalEffect.NULL);
      var4.set("abnormal3", AbnormalEffect.NULL);
      if (var3.getNamedItem("abnormal") != null) {
         AbnormalEffect var5 = AbnormalEffect.getByName(var3.getNamedItem("abnormal").getNodeValue());
         if (var5.isSpecial()) {
            var4.set("abnormal2", var5);
         }

         if (var5.isEvent()) {
            var4.set("abnormal3", var5);
         } else {
            var4.set("abnormal", var5);
         }
      }

      if (var3.getNamedItem("stackType") != null) {
         var4.set("stackType", var3.getNamedItem("stackType").getNodeValue());
      }

      if (var3.getNamedItem("stackType2") != null) {
         var4.set("stackType2", var3.getNamedItem("stackType2").getNodeValue());
      }

      if (var3.getNamedItem("stackOrder") != null) {
         var4.set("stackOrder", this.parseNumber(var3.getNamedItem("stackOrder").getNodeValue()).intValue());
      }

      if (var3.getNamedItem("applyOnCaster") != null) {
         var4.set("applyOnCaster", Boolean.valueOf(var3.getNamedItem("applyOnCaster").getNodeValue()));
      }

      if (var3.getNamedItem("applyOnSummon") != null) {
         var4.set("applyOnSummon", Boolean.valueOf(var3.getNamedItem("applyOnSummon").getNodeValue()));
      }

      if (var3.getNamedItem("displayId") != null) {
         var4.set("displayId", this.parseNumber(var3.getNamedItem("displayId").getNodeValue()).intValue());
      }

      if (var3.getNamedItem("displayLevel") != null) {
         var4.set("displayLevel", this.parseNumber(var3.getNamedItem("displayLevel").getNodeValue()).intValue());
      }

      if (var3.getNamedItem("chance") != null) {
         var4.set("chance", this.parseNumber(var3.getNamedItem("chance").getNodeValue()).intValue());
      }

      if (var3.getNamedItem("cancelOnAction") != null) {
         var4.set("cancelOnAction", Boolean.valueOf(var3.getNamedItem("cancelOnAction").getNodeValue()));
      }

      if (var3.getNamedItem("cancelOnItemSwitch") != null) {
         var4.set("cancelOnItemSwitch", var3.getNamedItem("cancelOnItemSwitch").getNodeValue());
      }

      if (var3.getNamedItem("isOffensive") != null) {
         var4.set("isOffensive", Boolean.valueOf(var3.getNamedItem("isOffensive").getNodeValue()));
      }

      if (var3.getNamedItem("isReflectable") != null) {
         var4.set("isReflectable", Boolean.valueOf(var3.getNamedItem("isReflectable").getNodeValue()));
      }

      EffectTemplate var7 = new EffectTemplate(var4);
      this.parseTemplate(var1, var7);

      for (Node var6 = var1.getFirstChild(); var6 != null; var6 = var6.getNextSibling()) {
         if ("triggers".equalsIgnoreCase(var6.getNodeName())) {
            this.parseTrigger(var6, var7);
         }
      }

      if (var2 instanceof Skill) {
         ((Skill)var2).attach(var7);
      }
   }

   protected Condition parseCondition(Node var1) {
      while (var1 != null && var1.getNodeType() != 1) {
         var1 = var1.getNextSibling();
      }

      if (var1 == null) {
         return null;
      } else if ("and".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseLogicAnd(var1);
      } else if ("or".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseLogicOr(var1);
      } else if ("not".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseLogicNot(var1);
      } else if ("player".equalsIgnoreCase(var1.getNodeName())) {
         return this.parsePlayerCondition(var1);
      } else if ("target".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseTargetCondition(var1);
      } else if ("has".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseHasCondition(var1);
      } else if ("using".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseUsingCondition(var1);
      } else if ("game".equalsIgnoreCase(var1.getNodeName())) {
         return this.parseGameCondition(var1);
      } else {
         return "zone".equalsIgnoreCase(var1.getNodeName()) ? this.parseZoneCondition(var1) : null;
      }
   }

   protected Condition parseLogicAnd(Node var1) {
      ConditionLogicAnd var2 = new ConditionLogicAnd();

      for (Node var3 = var1.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
         if (var3.getNodeType() == 1) {
            var2.add(this.parseCondition(var3));
         }
      }

      if (var2._conditions == null || var2._conditions.length == 0) {
         _log.error("Empty <and> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseLogicOr(Node var1) {
      ConditionLogicOr var2 = new ConditionLogicOr();

      for (Node var3 = var1.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
         if (var3.getNodeType() == 1) {
            var2.add(this.parseCondition(var3));
         }
      }

      if (var2._conditions == null || var2._conditions.length == 0) {
         _log.error("Empty <or> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseLogicNot(Node var1) {
      for (Node var2 = var1.getFirstChild(); var2 != null; var2 = var2.getNextSibling()) {
         if (var2.getNodeType() == 1) {
            return new ConditionLogicNot(this.parseCondition(var2));
         }
      }

      _log.error("Empty <not> condition in " + this.file);
      return null;
   }

   protected Condition parsePlayerCondition(Node var1) {
      Condition var2 = null;
      NamedNodeMap var3 = var1.getAttributes();

      for (int var4 = 0; var4 < var3.getLength(); var4++) {
         Node var5 = var3.item(var4);
         String var6 = var5.getNodeName();
         if ("race".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionPlayerRace(var5.getNodeValue()));
         } else if ("forbiddenClassIds".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionPlayerForbiddenClassId(var6.split(";")));
         } else if ("minLevel".equalsIgnoreCase(var6)) {
            int var7 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerMinLevel(var7));
         } else if ("summon_siege_golem".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionPlayerSummonSiegeGolem());
         } else if ("maxLevel".equalsIgnoreCase(var6)) {
            int var10 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerMaxLevel(var10));
         } else if ("maxPK".equalsIgnoreCase(var6)) {
            int var11 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerMaxPK(var11));
         } else if ("resting".equalsIgnoreCase(var6)) {
            boolean var12 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerState(ConditionPlayerState.CheckPlayerState.RESTING, var12));
         } else if ("moving".equalsIgnoreCase(var6)) {
            boolean var13 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerState(ConditionPlayerState.CheckPlayerState.MOVING, var13));
         } else if ("running".equalsIgnoreCase(var6)) {
            boolean var14 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerState(ConditionPlayerState.CheckPlayerState.RUNNING, var14));
         } else if ("standing".equalsIgnoreCase(var6)) {
            boolean var15 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerState(ConditionPlayerState.CheckPlayerState.STANDING, var15));
         } else if ("flying".equalsIgnoreCase(var5.getNodeName())) {
            boolean var16 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerState(ConditionPlayerState.CheckPlayerState.FLYING, var16));
         } else if ("flyingTransform".equalsIgnoreCase(var5.getNodeName())) {
            boolean var17 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerState(ConditionPlayerState.CheckPlayerState.FLYING_TRANSFORM, var17));
         } else if ("olympiad".equalsIgnoreCase(var5.getNodeName())) {
            boolean var18 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerOlympiad(var18));
         } else if ("is_in_team".equalsIgnoreCase(var5.getNodeName())) {
            boolean var19 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerInTeam(var19));
         } else if ("on_pvp_event".equalsIgnoreCase(var5.getNodeName())) {
            boolean var20 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerOnPvPEvent(var20));
         } else if ("is_hero".equalsIgnoreCase(var6)) {
            boolean var21 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerIsHero(var21));
         } else if ("is_clan_leader".equalsIgnoreCase(var6)) {
            boolean var22 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerIsClanLeader(var22));
         } else if ("is_in_academy".equalsIgnoreCase(var6)) {
            boolean var23 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerIsInAcademy(var23));
         } else if ("is_premium".equalsIgnoreCase(var6)) {
            boolean var24 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionIsPremium(var24));
         } else if ("is_in_combat".equalsIgnoreCase(var6)) {
            boolean var25 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerInCombat(var25));
         } else if ("class_is_mage".equalsIgnoreCase(var6)) {
            boolean var26 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionPlayerClassIsMage(var26));
         } else if ("min_pledge_rank".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionClanPlayerMinPledgeRank(var5.getNodeValue()));
         } else if ("percentHP".equalsIgnoreCase(var6)) {
            int var27 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerPercentHp(var27));
         } else if ("percentMP".equalsIgnoreCase(var6)) {
            int var28 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerPercentMp(var28));
         } else if ("percentCP".equalsIgnoreCase(var6)) {
            int var29 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerPercentCp(var29));
         } else if ("chargesMin".equalsIgnoreCase(var6)) {
            int var30 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerChargesMin(var30));
         } else if ("chargesMax".equalsIgnoreCase(var6)) {
            int var31 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerChargesMax(var31));
         } else if ("agathion".equalsIgnoreCase(var6)) {
            int var32 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerAgathion(var32));
         } else if ("cubic".equalsIgnoreCase(var6)) {
            int var33 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerCubic(var33));
         } else if ("instance_zone".equalsIgnoreCase(var6)) {
            int var34 = this.parseNumber(var5.getNodeValue()).intValue();
            var2 = this.joinAnd(var2, new ConditionPlayerInstanceZone(var34));
         } else if ("riding".equalsIgnoreCase(var6)) {
            String var35 = var5.getNodeValue();
            if ("strider".equalsIgnoreCase(var35)) {
               var2 = this.joinAnd(var2, new ConditionPlayerRiding(ConditionPlayerRiding.CheckPlayerRiding.STRIDER));
            } else if ("wyvern".equalsIgnoreCase(var35)) {
               var2 = this.joinAnd(var2, new ConditionPlayerRiding(ConditionPlayerRiding.CheckPlayerRiding.WYVERN));
            } else if ("none".equalsIgnoreCase(var35)) {
               var2 = this.joinAnd(var2, new ConditionPlayerRiding(ConditionPlayerRiding.CheckPlayerRiding.NONE));
            }
         } else if ("classId".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionPlayerClassId(var5.getNodeValue().split(",")));
         } else if ("privateStoreType".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionPlayerPrivateStore(var5.getNodeValue().trim()));
         } else if ("gender".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionPlayerGender(var5.getNodeValue()));
         } else if ("hasBuffId".equalsIgnoreCase(var6)) {
            StringTokenizer var36 = new StringTokenizer(var5.getNodeValue(), ";");
            int var8 = Integer.parseInt(var36.nextToken().trim());
            int var9 = -1;
            if (var36.hasMoreTokens()) {
               var9 = Integer.parseInt(var36.nextToken().trim());
            }

            var2 = this.joinAnd(var2, new ConditionPlayerHasBuffId(var8, var9));
         } else if ("hasBuff".equalsIgnoreCase(var6)) {
            StringTokenizer var37 = new StringTokenizer(var5.getNodeValue(), ";");
            EffectType var40 = Enum.valueOf(EffectType.class, var37.nextToken().trim());
            int var42 = -1;
            if (var37.hasMoreTokens()) {
               var42 = Integer.parseInt(var37.nextToken().trim());
            }

            var2 = this.joinAnd(var2, new ConditionPlayerHasBuff(var40, var42));
         } else if ("damage".equalsIgnoreCase(var6)) {
            String[] var38 = var5.getNodeValue().split(";");
            var2 = this.joinAnd(var2, new ConditionPlayerMinMaxDamage(Double.parseDouble(var38[0]), Double.parseDouble(var38[1])));
         } else if ("skillMinSeed".equalsIgnoreCase(var6)) {
            StringTokenizer var39 = new StringTokenizer(var5.getNodeValue(), ";");
            int var41 = Integer.parseInt(var39.nextToken().trim());
            int var43 = Integer.parseInt(var39.nextToken().trim());
            var2 = this.joinAnd(var2, new ConditionPlayerSkillMinSeed(var41, var43));
         }
      }

      if (var2 == null) {
         _log.error("Unrecognized <player> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseTargetCondition(Node var1) {
      Condition var2 = null;
      NamedNodeMap var3 = var1.getAttributes();

      for (int var4 = 0; var4 < var3.getLength(); var4++) {
         Node var5 = var3.item(var4);
         String var6 = var5.getNodeName();
         String var7 = var5.getNodeValue();
         if ("aggro".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetAggro(Boolean.valueOf(var7)));
         } else if ("pvp".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPlayable(Boolean.valueOf(var7)));
         } else if ("player".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPlayer(Boolean.valueOf(var7)));
         } else if ("exclude_caster".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPlayerNotMe(Boolean.valueOf(var7)));
         } else if ("summon".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetSummon(Boolean.valueOf(var7)));
         } else if ("mob".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetMob(Boolean.valueOf(var7)));
         } else if ("targetInTheSameParty".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetInTheSameParty(Boolean.valueOf(var7)));
         } else if ("mobId".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetMobId(Integer.parseInt(var7)));
         } else if ("race".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetRace(var7));
         } else if ("npc_class".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetNpcClass(var7));
         } else if ("playerRace".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPlayerRace(var7));
         } else if ("forbiddenClassIds".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetForbiddenClassId(var7.split(";")));
         } else if ("playerSameClan".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetClan(var7));
         } else if ("castledoor".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetCastleDoor(Boolean.valueOf(var7)));
         } else if ("direction".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetDirection(PositionUtils.TargetDirection.valueOf(var7.toUpperCase())));
         } else if ("percentHP".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPercentHp(this.parseNumber(var5.getNodeValue()).intValue()));
         } else if ("percentMP".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPercentMp(this.parseNumber(var5.getNodeValue()).intValue()));
         } else if ("percentCP".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetPercentCp(this.parseNumber(var5.getNodeValue()).intValue()));
         } else if ("hasBuffId".equalsIgnoreCase(var6)) {
            StringTokenizer var8 = new StringTokenizer(var7, ";");
            int var9 = Integer.parseInt(var8.nextToken().trim());
            int var10 = -1;
            if (var8.hasMoreTokens()) {
               var10 = Integer.parseInt(var8.nextToken().trim());
            }

            var2 = this.joinAnd(var2, new ConditionTargetHasBuffId(var9, var10));
         } else if ("hasBuff".equalsIgnoreCase(var6)) {
            StringTokenizer var11 = new StringTokenizer(var7, ";");
            EffectType var12 = Enum.valueOf(EffectType.class, var11.nextToken().trim());
            int var13 = -1;
            if (var11.hasMoreTokens()) {
               var13 = Integer.parseInt(var11.nextToken().trim());
            }

            var2 = this.joinAnd(var2, new ConditionTargetHasBuff(var12, var13));
         } else if ("hasForbiddenSkill".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionTargetHasForbiddenSkill(this.parseNumber(var5.getNodeValue()).intValue()));
         }
      }

      if (var2 == null) {
         _log.error("Unrecognized <target> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseUsingCondition(Node var1) {
      Condition var2 = null;
      NamedNodeMap var3 = var1.getAttributes();

      for (int var4 = 0; var4 < var3.getLength(); var4++) {
         Node var5 = var3.item(var4);
         String var6 = var5.getNodeName();
         String var7 = var5.getNodeValue();
         if (!"kind".equalsIgnoreCase(var6) && !"weapon".equalsIgnoreCase(var6)) {
            if ("armor".equalsIgnoreCase(var6)) {
               ArmorTemplate.ArmorType var16 = ArmorTemplate.ArmorType.valueOf(var7.toUpperCase());
               var2 = this.joinAnd(var2, new ConditionUsingArmor(var16));
            } else if ("skill".equalsIgnoreCase(var6)) {
               var2 = this.joinAnd(var2, new ConditionUsingSkill(Integer.parseInt(var7)));
            } else if ("blowskill".equalsIgnoreCase(var6)) {
               var2 = this.joinAnd(var2, new ConditionUsingBlowSkill(Boolean.parseBoolean(var7)));
            } else if ("slotitem".equalsIgnoreCase(var6)) {
               StringTokenizer var17 = new StringTokenizer(var7, ";");
               int var9 = Integer.parseInt(var17.nextToken().trim());
               int var18 = Integer.parseInt(var17.nextToken().trim());
               int var19 = 0;
               if (var17.hasMoreTokens()) {
                  var19 = Integer.parseInt(var17.nextToken().trim());
               }

               var2 = this.joinAnd(var2, new ConditionSlotItemId(var18, var9, var19));
            }
         } else {
            long var8 = 0L;
            StringTokenizer var10 = new StringTokenizer(var7, ",");

            label70:
            while (var10.hasMoreTokens()) {
               String var11 = var10.nextToken().trim();

               for (WeaponTemplate.WeaponType var15 : WeaponTemplate.WeaponType.VALUES) {
                  if (var15.toString().equalsIgnoreCase(var11)) {
                     var8 |= var15.mask();
                     continue label70;
                  }
               }

               for (ArmorTemplate.ArmorType var23 : ArmorTemplate.ArmorType.VALUES) {
                  if (var23.toString().equalsIgnoreCase(var11)) {
                     var8 |= var23.mask();
                     continue label70;
                  }
               }

               _log.error("Invalid item kind: \"" + var11 + "\" in " + this.file);
            }

            if (var8 != 0L) {
               var2 = this.joinAnd(var2, new ConditionUsingItemType(var8));
            }
         }
      }

      if (var2 == null) {
         _log.error("Unrecognized <using> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseHasCondition(Node var1) {
      Condition var2 = null;
      NamedNodeMap var3 = var1.getAttributes();

      for (int var4 = 0; var4 < var3.getLength(); var4++) {
         Node var5 = var3.item(var4);
         String var6 = var5.getNodeName();
         String var7 = var5.getNodeValue();
         if ("skill".equalsIgnoreCase(var6)) {
            StringTokenizer var8 = new StringTokenizer(var7, ";");
            Integer var9 = this.parseNumber(var8.nextToken().trim()).intValue();
            short var10 = this.parseNumber(var8.nextToken().trim()).shortValue();
            var2 = this.joinAnd(var2, new ConditionHasSkill(var9, var10));
         } else if ("success".equalsIgnoreCase(var6)) {
            var2 = this.joinAnd(var2, new ConditionFirstEffectSuccess(Boolean.valueOf(var7)));
         }
      }

      if (var2 == null) {
         _log.error("Unrecognized <has> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseGameCondition(Node var1) {
      Condition var2 = null;
      NamedNodeMap var3 = var1.getAttributes();

      for (int var4 = 0; var4 < var3.getLength(); var4++) {
         Node var5 = var3.item(var4);
         if ("night".equalsIgnoreCase(var5.getNodeName())) {
            boolean var6 = Boolean.valueOf(var5.getNodeValue());
            var2 = this.joinAnd(var2, new ConditionGameTime(ConditionGameTime.CheckGameTime.NIGHT, var6));
         }
      }

      if (var2 == null) {
         _log.error("Unrecognized <game> condition in " + this.file);
      }

      return var2;
   }

   protected Condition parseZoneCondition(Node var1) {
      Condition var2 = null;
      NamedNodeMap var3 = var1.getAttributes();

      for (int var4 = 0; var4 < var3.getLength(); var4++) {
         Node var5 = var3.item(var4);
         if ("type".equalsIgnoreCase(var5.getNodeName())) {
            var2 = this.joinAnd(var2, new ConditionZoneType(var5.getNodeValue()));
         } else if ("name".equalsIgnoreCase(var5.getNodeName())) {
            var2 = this.joinAnd(var2, new ConditionZoneName(var5.getNodeValue()));
         }
      }

      if (var2 == null) {
         _log.error("Unrecognized <zone> condition in " + this.file);
      }

      return var2;
   }

   protected void parseBeanSet(Node var1, StatsSet var2, int var3) {
      try {
         String var4 = var1.getAttributes().getNamedItem("name").getNodeValue().trim();
         String var5 = var1.getAttributes().getNamedItem("val").getNodeValue().trim();
         char var6 = var5.length() == 0 ? ' ' : var5.charAt(0);
         if (var5.contains("#") && var6 != '#') {
            for (String var10 : var5.split("[;: ]+")) {
               if (var10.charAt(0) == '#') {
                  var5 = var5.replace(var10, String.valueOf(this.getTableValue(var10, var3)));
               }
            }
         }

         if (var6 == '#') {
            Object var12 = this.getTableValue(var5, var3);
            Number var13 = this.parseNumber(var12.toString());
            var2.set(var4, var13 == null ? var12 : String.valueOf(var13));
         } else if ((Character.isDigit(var6) || var6 == '-') && !var5.contains(" ") && !var5.contains(";")) {
            var2.set(var4, String.valueOf(this.parseNumber(var5)));
         } else {
            var2.set(var4, var5);
         }
      } catch (Exception var11) {
         System.out.println(var1.getAttributes().getNamedItem("name") + " " + var2.get("skill_id"));
         var11.printStackTrace();
      }
   }

   protected Number parseNumber(String var1) {
      if (var1.charAt(0) == '#') {
         var1 = this.getTableValue(var1).toString();
      }

      try {
         if (var1.equalsIgnoreCase("max")) {
            return Double.POSITIVE_INFINITY;
         }

         if (var1.equalsIgnoreCase("min")) {
            return Double.NEGATIVE_INFINITY;
         }

         if (var1.indexOf(46) == -1) {
            byte var2 = 10;
            if (var1.length() > 2 && var1.substring(0, 2).equalsIgnoreCase("0x")) {
               var1 = var1.substring(2);
               var2 = 16;
            }

            return Integer.valueOf(var1, var2);
         } else {
            return Double.valueOf(var1);
         }
      } catch (NumberFormatException var3) {
         return null;
      }
   }

   protected Condition joinAnd(Condition var1, Condition var2) {
      if (var1 == null) {
         return var2;
      } else if (var1 instanceof ConditionLogicAnd) {
         ((ConditionLogicAnd)var1).add(var2);
         return var1;
      } else {
         ConditionLogicAnd var3 = new ConditionLogicAnd();
         var3.add(var1);
         var3.add(var2);
         return var3;
      }
   }
}
