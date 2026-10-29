package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.AbsorbInfo;
import l2.gameserver.templates.npc.Faction;
import l2.gameserver.templates.npc.MinionData;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Element;

public final class NpcParser extends AbstractDirParser<NpcHolder> {
   private static final NpcParser xv = new NpcParser();

   public static NpcParser getInstance() {
      return xv;
   }

   private NpcParser() {
      super(NpcHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/npc/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "npc.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         int var4 = Integer.parseInt(var3.attributeValue("id"));
         int var5 = var3.attributeValue("template_id") == null ? 0 : Integer.parseInt(var3.attributeValue("id"));
         String var6 = var3.attributeValue("name");
         String var7 = var3.attributeValue("title");
         StatsSet var8 = new StatsSet();
         var8.set("npcId", var4);
         var8.set("displayId", var5);
         var8.set("name", var6);
         var8.set("title", var7);
         var8.set("baseCpReg", 0);
         var8.set("baseCpMax", 0);
         Iterator var9 = var3.elementIterator();

         while (var9.hasNext()) {
            Element var10 = (Element)var9.next();
            if (var10.getName().equalsIgnoreCase("set")) {
               var8.set(var10.attributeValue("name"), var10.attributeValue("value"));
            } else if (var10.getName().equalsIgnoreCase("equip")) {
               Iterator var34 = var10.elementIterator();

               while (var34.hasNext()) {
                  Element var37 = (Element)var34.next();
                  int var40 = Integer.parseInt(var37.attributeValue("item_id"));
                  if (ItemHolder.getInstance().getTemplate(var40) == null) {
                     this._log.error("Undefined item " + var40 + " used in slot " + var37.getName() + " of npc " + var4);
                  }

                  var8.set(var37.getName(), String.valueOf(var40));
               }
            } else if (var10.getName().equalsIgnoreCase("ai_params")) {
               StatsSet var33 = new StatsSet();
               Iterator var36 = var10.elementIterator();

               while (var36.hasNext()) {
                  Element var39 = (Element)var36.next();
                  var33.set(var39.attributeValue("name"), var39.attributeValue("value"));
               }

               var8.set("aiParams", var33);
            } else if (var10.getName().equalsIgnoreCase("attributes")) {
               int[] var11 = new int[6];
               int[] var12 = new int[6];
               Iterator var13 = var10.elementIterator();

               while (var13.hasNext()) {
                  Element var14 = (Element)var13.next();
                  if (var14.getName().equalsIgnoreCase("defence")) {
                     l2.gameserver.model.base.Element var15 = l2.gameserver.model.base.Element.getElementByName(var14.attributeValue("attribute"));
                     var12[var15.getId()] = Integer.parseInt(var14.attributeValue("value"));
                  } else if (var14.getName().equalsIgnoreCase("attack")) {
                     l2.gameserver.model.base.Element var49 = l2.gameserver.model.base.Element.getElementByName(var14.attributeValue("attribute"));
                     var11[var49.getId()] = Integer.parseInt(var14.attributeValue("value"));
                  }
               }

               var8.set("baseAttributeAttack", var11);
               var8.set("baseAttributeDefence", var12);
            }
         }

         NpcTemplate var31 = new NpcTemplate(var8);
         LinkedList var32 = new LinkedList();
         Iterator var35 = var3.elementIterator();

         while (var35.hasNext()) {
            Element var38 = (Element)var35.next();
            String var41 = var38.getName();
            if (var41.equalsIgnoreCase("faction")) {
               String var48 = var38.attributeValue("name");
               Faction var56 = new Faction(var48);
               int var62 = Integer.parseInt(var38.attributeValue("range"));
               var56.setRange(var62);
               Iterator var67 = var38.elementIterator();

               while (var67.hasNext()) {
                  Element var71 = (Element)var67.next();
                  int var74 = Integer.parseInt(var71.attributeValue("npc_id"));
                  var56.addIgnoreNpcId(var74);
               }

               var31.setFaction(var56);
            } else if (var41.equalsIgnoreCase("rewardlist")) {
               RewardType var47 = RewardType.valueOf(var38.attributeValue("type"));
               boolean var55 = var38.attributeValue("auto_loot") != null && Boolean.parseBoolean(var38.attributeValue("auto_loot"));
               RewardList var61 = new RewardList(var47, var55);
               Iterator var66 = var38.elementIterator();

               while (var66.hasNext()) {
                  Element var70 = (Element)var66.next();
                  String var73 = var70.getName();
                  if (var73.equalsIgnoreCase("group")) {
                     double var77 = var70.attributeValue("chance") == null ? 1000000.0 : Double.parseDouble(var70.attributeValue("chance")) * 10000.0;
                     RewardGroup var22 = var47 != RewardType.SWEEP && var47 != RewardType.NOT_RATED_NOT_GROUPED ? new RewardGroup(var77) : null;
                     Iterator var80 = var70.elementIterator();

                     while (var80.hasNext()) {
                        Element var81 = (Element)var80.next();
                        RewardData var82 = this.a(var81, var47);
                        if (var82 != null) {
                           if (var47 != RewardType.SWEEP && var47 != RewardType.NOT_RATED_NOT_GROUPED) {
                              var22.addData(var82);
                           } else {
                              this.warn("Can't load rewardlist from group: " + var4 + "; type: " + var47);
                           }
                        }
                     }

                     if (var22 != null) {
                        var61.add(var22);
                     }
                  } else if (var73.equalsIgnoreCase("reward")) {
                     if (var47 != RewardType.SWEEP && var47 != RewardType.NOT_RATED_NOT_GROUPED) {
                        this.warn("Reward can't be without group(and not grouped): " + var4 + "; type: " + var47);
                     } else {
                        RewardData var76 = this.a(var70, var47);
                        if (var76 != null) {
                           RewardGroup var79 = new RewardGroup(1000000.0);
                           var79.addData(var76);
                           var61.add(var79);
                        }
                     }
                  }
               }

               if ((var47 == RewardType.RATED_GROUPED || var47 == RewardType.NOT_RATED_GROUPED) && !var61.validate()) {
                  this.warn("Problems with rewardlist for npc: " + var4 + "; type: " + var47);
               }

               var31.putRewardList(var47, var61);
            } else if (var41.equalsIgnoreCase("skills")) {
               Iterator var46 = var38.elementIterator();

               while (var46.hasNext()) {
                  Element var54 = (Element)var46.next();
                  int var60 = Integer.parseInt(var54.attributeValue("id"));
                  int var65 = Integer.parseInt(var54.attributeValue("level"));
                  if (SkillTable.getInstance().getInfo(var60, var65) == null) {
                     this._log.error("Undefined id " + var60 + " and level " + var65 + " of npc " + var4);
                  }

                  if (var60 == 4416) {
                     var31.setRace(var65);
                  }

                  Skill var69 = SkillTable.getInstance().getInfo(var60, var65);
                  if (var69 != null) {
                     var31.addSkill(var69);
                  }
               }
            } else if (var41.equalsIgnoreCase("minions")) {
               Iterator var45 = var38.elementIterator();

               while (var45.hasNext()) {
                  Element var53 = (Element)var45.next();
                  int var59 = Integer.parseInt(var53.attributeValue("npc_id"));
                  int var64 = Integer.parseInt(var53.attributeValue("count"));
                  var31.addMinion(new MinionData(var59, var64));
               }
            } else if (var41.equalsIgnoreCase("teach_classes")) {
               Iterator var44 = var38.elementIterator();

               while (var44.hasNext()) {
                  Element var52 = (Element)var44.next();
                  int var58 = Integer.parseInt(var52.attributeValue("id"));
                  var31.addTeachInfo(ClassId.VALUES[var58]);
               }
            } else if (var41.equalsIgnoreCase("absorblist")) {
               Iterator var43 = var38.elementIterator();

               while (var43.hasNext()) {
                  Element var51 = (Element)var43.next();
                  int var57 = Integer.parseInt(var51.attributeValue("chance"));
                  int var63 = var51.attributeValue("cursed_chance") == null ? 0 : Integer.parseInt(var51.attributeValue("cursed_chance"));
                  int var68 = Integer.parseInt(var51.attributeValue("min_level"));
                  int var72 = Integer.parseInt(var51.attributeValue("max_level"));
                  boolean var75 = var51.attributeValue("skill") != null && Boolean.parseBoolean(var51.attributeValue("skill"));
                  AbsorbInfo.AbsorbType var78 = AbsorbInfo.AbsorbType.valueOf(var51.attributeValue("type"));
                  var31.addAbsorbInfo(new AbsorbInfo(var75, var78, var57, var63, var68, var72));
               }
            } else if (var41.equalsIgnoreCase("teleportlist")) {
               Iterator var42 = var38.elementIterator();

               while (var42.hasNext()) {
                  Element var50 = (Element)var42.next();
                  int var16 = Integer.parseInt(var50.attributeValue("id"));
                  ArrayList var17 = new ArrayList();
                  Iterator var18 = var50.elementIterator();

                  while (var18.hasNext()) {
                     Element var19 = (Element)var18.next();
                     int var20 = Integer.parseInt(var19.attributeValue("item_id", "57"));
                     long var21 = Integer.parseInt(var19.attributeValue("price"));
                     int var23 = Integer.parseInt(var19.attributeValue("min_level", "0"));
                     int var24 = Integer.parseInt(var19.attributeValue("max_level", "0"));
                     String var25 = var19.attributeValue("name").trim();
                     int var26 = Integer.parseInt(var19.attributeValue("castle_id", "0"));
                     int var27 = Integer.parseInt(var19.attributeValue("key_item_id", "-1"));
                     TeleportLocation var28 = new TeleportLocation(var20, var21, var23, var24, var25, var26, var27);
                     var28.set(Location.parseLoc(var19.attributeValue("loc")));
                     if (var23 > 0 || var24 > 0) {
                        for (Object __raw_var30_260 : (Iterable<?>)(Object)(var32)) {
                           Location var30 = (Location)__raw_var30_260;
                           if (var30.x == var28.x && var30.y == var28.y && var30.z == var28.z) {
                              this._log.warn("Teleport location may intersect for " + var19.asXML());
                           }
                        }
                     }

                     var32.add(var28);
                     var17.add(var28);
                  }

                  var31.addTeleportList(var16, (TeleportLocation[])var17.toArray(new TeleportLocation[var17.size()]));
               }
            }
         }

         this.getHolder().addTemplate(var31);
      }
   }

   private RewardData a(Element var1, RewardType var2) {
      int var3 = Integer.parseInt(var1.attributeValue("item_id"));
      if (var2 == RewardType.SWEEP) {
         if (ArrayUtils.contains(Config.NO_DROP_ITEMS_FOR_SWEEP, var3)) {
            return null;
         }
      } else if (ArrayUtils.contains(Config.NO_DROP_ITEMS, var3)) {
         return null;
      }

      if (Config.ALLOW_ONLY_THESE_DROP_ITEMS_ID != null
         && Config.ALLOW_ONLY_THESE_DROP_ITEMS_ID.length != 0
         && !ArrayUtils.contains(Config.ALLOW_ONLY_THESE_DROP_ITEMS_ID, var3)) {
         return null;
      }

      int var4 = Integer.parseInt(var1.attributeValue("min"));
      int var5 = Integer.parseInt(var1.attributeValue("max"));
      int var6 = (int)(Double.parseDouble(var1.attributeValue("chance")) * 10000.0);
      if (var2 == RewardType.SWEEP && (ArrayUtils.isEmpty(Config.SPOIL_ITEMS_ID_FOR_RATE) || ArrayUtils.contains(Config.SPOIL_ITEMS_ID_FOR_RATE, var3))) {
         var6 = (int)(var6 * Config.SPOIL_ITEMS_CHANCE_RATE);
      }

      RewardData var7 = new RewardData(var3);
      if (var7.getItem().isHerb()) {
         var7.setChance(var6 * Config.RATE_DROP_HERBS);
      } else {
         var7.setChance(var6);
      }

      var7.setMinDrop(var4);
      var7.setMaxDrop(var5);
      return var7;
   }
}
