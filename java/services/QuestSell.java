package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestSell extends Functions implements ScriptFile {
   private static final Logger LOG = LoggerFactory.getLogger(QuestSell.class);
   private static final String bXG = "scripts/services";
   private static final String bXH = "-h scripts_services.QuestSell:";
   private static final int bXI = 5;

   private static Map<Set<Quest>, List<Pair<Integer, Long>>> K(String var0) {
      return c(var0, false);
   }

   private static boolean b(Player var0, NpcInstance var1) {
      return var0 != null && var1 != null
         ? !var0.isActionsDisabled() && (Config.ALLOW_TALK_WHILE_SITTING || !var0.isSitting()) && var1.isInActingRange(var0)
         : false;
   }

   private static Map<Set<Quest>, List<Pair<Integer, Long>>> c(String var0, boolean var1) {
      LinkedHashMap var2 = new LinkedHashMap();
      StringTokenizer var3 = new StringTokenizer(var0, ";");

      label62:
      while (var3.hasMoreTokens()) {
         String var4 = var3.nextToken().trim();
         if (!var4.isEmpty()) {
            int var5 = var4.indexOf(58);
            if (var5 > 0) {
               String var6 = var4.substring(0, var5).trim();
               String var7 = var4.substring(var5 + 1).trim();
               StringTokenizer var8 = new StringTokenizer(var6, ",");
               LinkedHashSet var9 = new LinkedHashSet();

               while (var8.hasMoreTokens()) {
                  String var10 = var8.nextToken();
                  Quest var11 = QuestManager.getQuest2(var10);
                  if (var11 == null) {
                     if (var1) {
                        LOG.warn("QuestSellService: Can't get quest \"" + var10 + "\" in \"" + var4 + "\"");
                     }
                     continue label62;
                  }

                  var9.add(var11);
               }

               Set var16 = Collections.unmodifiableSet(var9);
               StringTokenizer var17 = new StringTokenizer(var7, ",");
               ArrayList var18 = new ArrayList();

               while (var17.hasMoreTokens()) {
                  String var12 = var17.nextToken().trim();
                  int var13 = var12.indexOf(45);
                  if (var13 <= 0) {
                     if (var1) {
                        LOG.warn("QuestSellService: Can't get price of \"" + var4 + "\"");
                     }
                     continue label62;
                  }

                  Integer var14 = Integer.parseInt(var12.substring(0, var13).trim());
                  Long var15 = Long.parseLong(var12.substring(var13 + 1).trim());
                  if (ItemHolder.getInstance().getTemplate(var14) == null) {
                     if (var1) {
                        LOG.warn("QuestSellService: Can't get item \"" + var14 + "\" of \"" + var4 + "\"");
                     }
                     continue label62;
                  }

                  var18.add(Pair.of(var14, var15));
               }

               if (var2.containsKey(var16)) {
                  if (var1) {
                     LOG.warn("QuestSellService: Quests already defined \"" + var6 + "\"");
                  }
               } else {
                  var2.put(var16, Collections.unmodifiableList(var18));
               }
            } else if (var1) {
               LOG.warn("QuestSellService: Can't process quest sell list entry \"" + var4 + "\"");
            }
         }
      }

      return var2;
   }

   private static List<String> b(Player var0, Collection<Quest> var1) {
      ArrayList var2 = new ArrayList();

      for (Quest var4 : var1) {
         String var5 = null;
         QuestState var6 = var0.getQuestState(var4);
         if (var6 != null) {
            switch (var6.getState()) {
               case 1:
               case 2:
                  var5 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.questInfoHave");
                  break;
               case 3:
                  var5 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.questInfoDone");
                  break;
               default:
                  var5 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.questInfo");
            }
         } else {
            var5 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.questInfo");
         }

         var5 = var5.replace("%quest_name%", var4.getDescr(var0));
         var5 = var5.replace("%quest_id%", String.valueOf(var4.getQuestIntId()));
         var2.add(var5);
      }

      return var2;
   }

   private static String a(Player var0, Pair<Integer, Long> var1) {
      int var2 = (Integer)var1.getKey();
      long var3 = (Long)var1.getValue();
      ItemTemplate var5 = ItemHolder.getInstance().getTemplate(var2);
      String var6 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.requiredPriceItemInfo");
      var6 = var6.replace("%item_id%", String.valueOf(var5.getItemId()));
      var6 = var6.replace("%item_name%", var5.getName());
      return var6.replace("%item_amount%", String.valueOf(var3));
   }

   private static List<String> c(Player var0, Collection<Pair<Integer, Long>> var1) {
      ArrayList var2 = new ArrayList();

      for (Pair var4 : var1) {
         var2.add(a(var0, var4));
      }

      return var2;
   }

   private static String a(Player var0, int var1, Pair<Set<Quest>, List<Pair<Integer, Long>>> var2) {
      String var3 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.questSellInfo");
      Set var4 = (Set)var2.getKey();
      List var5 = (List)var2.getValue();
      var3 = var3.replace("%quests_list%", String.join("<br1>", b(var0, var4)));
      var3 = var3.replace("%price_list%", String.join("<br1>", c(var0, var5)));
      return var3.replace("%bypass%", "-h scripts_services.QuestSell:buyQuestsListByIdx " + var1);
   }

   private static String a(Player var0, String var1, int var2, String var3) {
      String var4 = StringHolder.getInstance().getNotNull(var0, "scripts.services.QuestSell.paging");
      var4 = var4.replace(
         "%prev_button%",
         var1 != null
            ? "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">"
               .replace("%prev_bypass%", var1)
            : ""
      );
      var4 = var4.replace("%curr_page%", Integer.toString(var2 + 1));
      return var4.replace(
         "%next_button%",
         var3 != null
            ? "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">"
               .replace("%next_bypass%", var3)
            : ""
      );
   }

   private static boolean l(int var0, int var1) {
      return (var1 + 1) * 5 < var0;
   }

   private static String a(Player var0, int var1, Object[] var2, String var3) {
      String var4 = "-h scripts_services.QuestSell:" + var3 + " %d";
      return a(var0, var1 > 0 ? String.format(var4, var1 - 1).trim() : null, var1, l(var2.length, var1) ? String.format(var4, var1 + 1).trim() : null);
   }

   private static boolean d(Player var0, Collection<Quest> var1) {
      for (Quest var3 : var1) {
         if (var3.isVisible()) {
            QuestState var4 = var0.getQuestState(var3);
            if (var4 == null || var4.getState() != 3) {
               return true;
            }
         }
      }

      return false;
   }

   private static Pair<Set<Quest>, List<Pair<Integer, Long>>>[] a(Player var0, Map<Set<Quest>, List<Pair<Integer, Long>>> var1) {
      ArrayList var2 = new ArrayList();

      for (Entry var4 : var1.entrySet()) {
         if (d(var0, (Collection<Quest>)var4.getKey())) {
            var2.add(Pair.of(var4.getKey(), var4.getValue()));
         }
      }

      return (Pair<Set<Quest>, List<Pair<Integer, Long>>>[])var2.toArray(new Pair[var2.size()]);
   }

   private static Pair<Set<Quest>, List<Pair<Integer, Long>>>[] aB(Player var0) {
      return a(var0, K(Config.QUEST_SELL_QUEST_PRICES));
   }

   private static void a(Player var0, NpcInstance var1, int var2) {
      Pair[] var3 = aB(var0);
      StringBuilder var4 = new StringBuilder();
      NpcHtmlMessage var5 = new NpcHtmlMessage(var0, var1);
      var5.setFile("scripts/services/quests_sell_list.htm");
      int var6 = 5 * var2;

      for (int var7 = var6 + 5; var6 < var7 && var6 < var3.length; var6++) {
         Pair var8 = var3[var6];
         var4.append(a(var0, var6, var8));
      }

      var5.replace("%list%", var4.toString());
      var5.replace("%paging%", a(var0, var2, var3, "listAvailableQuestsForSell"));
      var0.sendPacket(var5);
   }

   public void listAvailableQuestsForSell() {
      Player var1 = this.getSelf();
      NpcInstance var2 = this.getNpc();
      if (Config.QUEST_SELL_ENABLE && b(var1, var2)) {
         a(var1, var2, 0);
      } else {
         var1.sendMessage(new CustomMessage("common.Disabled", var1));
      }
   }

   public void listAvailableQuestsForSell(String[] var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (Config.QUEST_SELL_ENABLE && b(var2, var3)) {
         a(var2, var3, Integer.parseInt(var1[0]));
      } else {
         var2.sendMessage(new CustomMessage("common.Disabled", var2));
      }
   }

   private static void a(Player var0, Collection<Quest> var1, Collection<Pair<Integer, Long>> var2) {
      for (Pair var4 : var2) {
         if (ItemFunctions.getItemCount(var0, (Integer)var4.getKey()) < (Long)var4.getValue()) {
            var0.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return;
         }
      }

      for (Pair var7 : var2) {
         if (ItemFunctions.removeItem(var0, (Integer)var7.getKey(), (Long)var7.getValue(), true) < (Long)var7.getValue()) {
            var0.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return;
         }
      }

      for (Quest var8 : var1) {
         var0.setQuestState(var8.newQuestState(var0, 3));
         Log.service("QuestSell", var0, "bought a quest " + var8.getName());
      }
   }

   public void buyQuestsListByIdx(String[] var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (Config.QUEST_SELL_ENABLE && b(var2, var3)) {
         Pair[] var4 = aB(var2);
         int var5 = Integer.parseInt(var1[0]);
         Pair var6 = var4[var5];
         a(var2, (Collection<Quest>)var6.getLeft(), (Collection<Pair<Integer, Long>>)var6.getRight());
         this.listAvailableQuestsForSell();
      } else {
         var2.sendMessage(new CustomMessage("common.Disabled", var2));
      }
   }

   @Override
   public void onLoad() {
      LOG.info("QuestSellService: Loading ...");
      c(Config.QUEST_SELL_QUEST_PRICES, true);
   }

   @Override
   public void onReload() {
   }

   @Override
   public void onShutdown() {
   }
}
