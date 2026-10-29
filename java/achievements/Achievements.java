package achievements;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.scripts.Scripts;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Achievements implements ScriptFile {
   private static final Achievements aJ = new Achievements();
   private static final Logger LOG = LoggerFactory.getLogger(Achievements.class);
   private static final SAXReader aK = new SAXReader(false);
   private static final Achievements.AchVoicedCommandHandler aL = new Achievements.AchVoicedCommandHandler();
   private static final File aM = new File(Config.DATAPACK_ROOT, "data/achievements.xml");
   private boolean aN = false;
   private String[] aO = ArrayUtils.EMPTY_STRING_ARRAY;
   private Map<AchievementMetricType, List<AchievementInfo>> aP = Collections.emptyMap();
   private Map<AchievementInfo.AchievementInfoCategory, List<AchievementInfo>> aQ = Collections.emptyMap();
   private List<AchievementInfo.AchievementInfoCategory> aR = Collections.emptyList();
   private List<AchievementInfo> aS = Collections.emptyList();

   public static Achievements getInstance() {
      return aJ;
   }

   public boolean isEnabled() {
      return this.aN;
   }

   private String[] d() {
      return this.aO;
   }

   public List<AchievementInfo> getAchievementInfosByMetric(AchievementMetricType var1) {
      return this.aP.get(var1);
   }

   public List<AchievementInfo> getAchievementInfosByCategory(AchievementInfo.AchievementInfoCategory var1) {
      return this.aQ.get(var1);
   }

   public AchievementInfo getAchievementInfoById(int var1) {
      for (AchievementInfo var3 : this.aS) {
         if (var3.getId() == var1) {
            return var3;
         }
      }

      return null;
   }

   public List<AchievementInfo.AchievementInfoCategory> getCategories() {
      return this.aR;
   }

   public void parse() {
      try {
         Document var1 = aK.read(aM);
         HashMap var2 = new HashMap();
         HashMap var3 = new HashMap();
         TreeMap var4 = new TreeMap();
         LinkedList var5 = new LinkedList();
         ArrayList var6 = new ArrayList();
         Element var7 = var1.getRootElement();
         this.aN = Boolean.parseBoolean(var7.attributeValue("enabled", "false"));
         String var8 = var7.attributeValue("voice_commands");
         this.aO = var8 != null && !var8.trim().isEmpty() ? var8.split("[^\\w\\d_]+") : ArrayUtils.EMPTY_STRING_ARRAY;
         if (this.aN) {
            Iterator var9 = var1.getRootElement().elementIterator();

            while (var9.hasNext()) {
               Element var10 = (Element)var9.next();
               if ("category".equalsIgnoreCase(var10.getName())) {
                  String var39 = var10.attributeValue("name");
                  AchievementInfo.AchievementInfoCategory var40 = new AchievementInfo.AchievementInfoCategory(var39, var10.attributeValue("title_address"));
                  var4.put(var39, var40);
                  var5.add(var40);
               } else if ("achievement".equalsIgnoreCase(var10.getName())) {
                  String var11 = var10.attributeValue("name_address");
                  String var12 = var10.attributeValue("category");
                  AchievementMetricType var13 = AchievementMetricType.valueOf(var10.attributeValue("type"));
                  String var14 = var10.attributeValue("expire_cron");
                  long var15 = Long.parseLong(var10.attributeValue("metric_stage_notify_delay", String.valueOf(0L)));
                  AchievementInfo var17 = new AchievementInfo(
                     Integer.parseInt(var10.attributeValue("id")), var13, var15, var11, var14 != null ? new SchedulingPattern(var14) : null
                  );
                  var17.setCategory((AchievementInfo.AchievementInfoCategory)var4.get(var12));
                  String var18 = var10.attributeValue("icon", "Icon.NOIMAGE");
                  var17.setIcon(var18);
                  int var19 = 0;
                  Iterator var20 = var10.elementIterator();

                  while (var20.hasNext()) {
                     Element var21 = (Element)var20.next();
                     if ("conds".equalsIgnoreCase(var21.getName())) {
                        Iterator var43 = var21.elementIterator();

                        while (var43.hasNext()) {
                           Element var44 = (Element)var43.next();
                           if ("cond".equalsIgnoreCase(var44.getName())) {
                              AchievementCondition var45 = AchievementCondition.makeCond(var44.attributeValue("name"), var44.attributeValue("value"));
                              if (var45 == null) {
                                 throw new RuntimeException("Unknown condition \"" + var44.getName() + " of achievement " + var11 + "(" + var17.getId() + ")");
                              }

                              var17.addCond(var45);
                           }
                        }
                     } else if ("stage".equalsIgnoreCase(var21.getName())) {
                        int var22 = var19 = Integer.parseInt(var21.attributeValue("level", String.valueOf(var19 + 1)));
                        String var23 = var21.attributeValue("desc_address");
                        int var24 = Integer.parseInt(var21.attributeValue("value"));
                        boolean var25 = Boolean.parseBoolean(var21.attributeValue("reset_metric", String.valueOf(Boolean.TRUE)));
                        if (var22 - var17.getMaxLevel() > 1) {
                           LOG.warn("Inconsistent level \"" + var22 + "\" of achievement \"" + var11 + "\"(" + var17.getId() + ")");
                        }

                        AchievementInfo.AchievementInfoLevel var26 = var17.addLevel(var22, var24, var23, var25);
                        Iterator var27 = var21.elementIterator();

                        while (var27.hasNext()) {
                           Element var28 = (Element)var27.next();
                           if ("rewards".equals(var28.getName())) {
                              Iterator var46 = var28.elementIterator();

                              while (var46.hasNext()) {
                                 Element var47 = (Element)var46.next();
                                 if ("reward".equalsIgnoreCase(var47.getName())) {
                                    int var48 = Integer.parseInt(var47.attributeValue("item_id"));
                                    long var32 = Long.parseLong(var47.attributeValue("min"));
                                    long var34 = Long.parseLong(var47.attributeValue("max"));
                                    int var36 = (int)(Double.parseDouble(var47.attributeValue("chance")) * 10000.0);
                                    RewardData var37 = new RewardData(var48);
                                    var37.setChance(var36);
                                    var37.setMinDrop(var32);
                                    var37.setMaxDrop(var34);
                                    var26.addRewardData(var37);
                                 }
                              }
                           } else if ("conds".equalsIgnoreCase(var28.getName())) {
                              Iterator var29 = var28.elementIterator();

                              while (var29.hasNext()) {
                                 Element var30 = (Element)var29.next();
                                 if ("cond".equalsIgnoreCase(var30.getName())) {
                                    AchievementCondition var31 = AchievementCondition.makeCond(var30.attributeValue("name"), var30.attributeValue("value"));
                                    if (var31 == null) {
                                       throw new RuntimeException(
                                          "Unknown condition \"" + var30.getName() + " of achievement " + var11 + "(" + var17.getId() + ")"
                                       );
                                    }

                                    var26.addCond(var31);
                                 }
                              }
                           }
                        }
                     }
                  }

                  List var41 = (List)var2.get(var17.getMetricType());
                  if (var41 == null) {
                     var2.put(var17.getMetricType(), var41 = new ArrayList());
                  }

                  var41.add(var17);
                  List var42 = (List)var3.get(var17.getCategory());
                  if (var42 == null) {
                     var3.put(var17.getCategory(), var42 = new ArrayList());
                  }

                  var42.add(var17);
                  var6.add(var17);
               }
            }
         }

         this.aR = var5;
         this.aP = var2;
         this.aQ = var3;
         this.aS = var6;
         LOG.info("Achievements: Loaded " + this.aS.size() + " achievement(s) for " + this.aR.size() + " category(ies).");
      } catch (Exception var38) {
         LOG.warn("Can't parse achievements", var38);
      }
   }

   private static void e() {
      try (Connection var0 = DatabaseFactory.getInstance().getConnection()) {
         GameServer.getInstance().getDbmsStructureSynchronizer(var0).synchronize(new String[]{"ex_achievements"});
      } catch (Exception var13) {
         throw new RuntimeException(var13);
      }
   }

   @Override
   public void onLoad() {
      getInstance().parse();
      if (getInstance().isEnabled()) {
         e();
         AchievementMetricListeners.getInstance().init();
         if (aL.getVoicedCommandList().length > 0) {
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(aL);
         }
      }
   }

   @Override
   public void onReload() {
      this.onShutdown();
      this.onLoad();
   }

   @Override
   public void onShutdown() {
      if (getInstance().isEnabled()) {
         AchievementMetricListeners.getInstance().done();
      }
   }

   private static class AchVoicedCommandHandler implements IVoicedCommandHandler {
      private AchVoicedCommandHandler() {
      }

      @Override
      public boolean useVoicedCommand(String var1, Player var2, String var3) {
         if (!Achievements.getInstance().isEnabled()) {
            return false;
         }

         for (String var7 : Achievements.getInstance().d()) {
            if (!var7.isEmpty() && var7.equalsIgnoreCase(var1)) {
               Scripts.getInstance().callScripts(var2, AchievementUI.class.getName(), "achievements");
               return true;
            }
         }

         return false;
      }

      @Override
      public String[] getVoicedCommandList() {
         return Achievements.getInstance().isEnabled() ? Achievements.getInstance().d() : ArrayUtils.EMPTY_STRING_ARRAY;
      }
   }
}
