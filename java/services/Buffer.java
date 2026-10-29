package services;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.ItemHandler;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import npc.model.NpcBufferInstance;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.napile.primitive.maps.CIntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Buffer extends Functions implements ScriptFile {
   private static final Logger LOG = LoggerFactory.getLogger(Buffer.class);
   private static final String zP = "Buffer";
   private static final String bWf = ";,";
   private static final String bWg = "BuffProfRec";
   private static final String bWh = "BuffProf-";
   private static final CIntObjectMap<Buffer.BuffTemplate> bWi = new CHashIntObjectMap();
   private static final int bWj = -2;
   private static final long bWk = 100L;
   private static final int bWl = -3;
   private static final long bWm = 50L;
   private static final int bWn = -4;
   private static final long bWo = 50L;
   private static final int bWp = 5;
   private static final int bWq = -100;
   private static final int bWr = -1000;
   private static final int bWs = -2000;
   private static final SAXReader aK = new SAXReader(false);
   private static final File bWt = new File(Config.DATAPACK_ROOT, "data/buff_templates.xml");
   private static Buffer.BuffItemHandler bWu;
   private static int[] bWv;
   public static final int SHARED_REUSE_GROUP = -99999;

   private static boolean au(Player var0) {
      if (var0.isSharedGroupDisabled(-99999)) {
         var0.sendPacket(new SystemMessage(48).addString("Buffer"));
         return false;
      } else {
         TimeStamp var1 = new TimeStamp(-99999, System.currentTimeMillis() + Config.ALT_NPC_BUFFER_REUSE_DELAY, Config.ALT_NPC_BUFFER_REUSE_DELAY);
         var0.addSharedGroupReuse(-99999, var1);
         return true;
      }
   }

   private static void d(int var0, Player var1) {
      switch (var0) {
         case -100:
            var1.setVar("BuffProfRec", "", -1L);
            break;
         case -4:
            if (var1.isInCombat()) {
               var1.sendPacket(new SystemMessage(113).addString("Buffer"));
               return;
            }

            if (!var1.reduceAdena(50L, true)) {
               var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
               return;
            }

            if (au(var1)) {
               var1.getEffectList().stopAllNegateEffects();
               if (var1.getPet() != null) {
                  var1.getPet().getEffectList().stopAllNegateEffects();
               }
            }
            break;
         case -3:
            if (var1.isInCombat() || var1.isOlyParticipant()) {
               var1.sendPacket(new SystemMessage(113).addString("Buffer"));
               return;
            }

            if (!var1.reduceAdena(50L, true)) {
               var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
               return;
            }

            if (au(var1)) {
               var1.setCurrentHpMp(var1.getMaxHp(), var1.getMaxMp());
               var1.setCurrentCp(var1.getMaxCp());
               var1.sendStatusUpdate(true, false, 9, 11, 33, 10, 12, 34);
               if (var1.getPet() != null) {
                  Summon var5 = var1.getPet();
                  var5.setCurrentHpMp(var5.getMaxHp(), var5.getMaxMp(), false);
               }
            }
            break;
         case -2:
            if (var1.isInCombat()) {
               var1.sendPacket(new SystemMessage(113).addString("Buffer"));
               return;
            }

            if (!var1.reduceAdena(100L, true)) {
               var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
               return;
            }

            if (var1.getPet() != null) {
               var1.getPet().getEffectList().stopAllEffects();
            }

            var1.getEffectList().stopAllEffects();
            var1.sendPacket(new SkillCoolTime(var1));
            break;
         default:
            if (var0 > 0) {
               if (au(var1)) {
                  Buffer.BuffTemplate var2 = (Buffer.BuffTemplate)bWi.get(var0);
                  if (var2 != null) {
                     var2.apply(var1);
                     r(var1, var0);
                  } else {
                     LOG.warn("Buffer: Unknown menuId \"" + var0 + "\" used.");
                  }
               }
            } else if (var0 <= -2000 && var0 > -2005) {
               int var4 = Math.abs(-2000 - var0);
               q(var1, var4);
            } else if (var0 <= -1000 && var0 > -1005) {
               int var3 = Math.abs(-1000 - var0);
               if (au(var1)) {
                  p(var1, var3);
               }
            }
      }
   }

   private static void p(final Player var0, int var1) {
      if (var1 <= 5) {
         String var2 = var0.getVar(String.format("%s%d", "BuffProf-", var1));
         if (var2 != null && !var2.trim().isEmpty()) {
            StringTokenizer var3 = new StringTokenizer(var2.trim(), ";,", false);
            final LinkedList var4 = new LinkedList();

            while (var3.hasMoreTokens()) {
               String var5 = var3.nextToken();
               if (!var5.isEmpty()) {
                  try {
                     int var6 = Integer.parseInt(var5);
                     Buffer.BuffTemplate var7 = (Buffer.BuffTemplate)bWi.get(var6);
                     if (var7 != null) {
                        var4.add(var7);
                     }
                  } catch (NumberFormatException var8) {
                     LOG.error("Buffer: Can't apply profile \"" + var1 + "\"", var8);
                  }
               }
            }

            ThreadPoolManager.getInstance().execute(new RunnableImpl() {
               @Override
               public void runImpl() throws Exception {
                  for (Object __raw_var2x_196 : (Iterable<?>)(Object)(var4)) {
                     Buffer.BuffTemplate var2x = (Buffer.BuffTemplate)__raw_var2x_196;
                     if (!var2x.applySync(var0)) {
                        return;
                     }
                  }
               }
            });
         } else {
            var0.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.EmptyProfile", var0));
         }
      }
   }

   private static void q(Player var0, int var1) {
      String var2 = var0.getVar("BuffProfRec");
      if (var1 <= 5 && var2 != null) {
         var0.setVar(String.format("%s%d", "BuffProf-", var1), var2, -1L);
         var0.unsetVar("BuffProfRec");
         var0.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.ProfileSaved", var0));
      } else {
         var0.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.CantSaveProfile", var0));
      }
   }

   private static void r(Player var0, int var1) {
      String var2 = var0.getVar("BuffProfRec");
      if (var2 != null) {
         if (var2.trim().isEmpty()) {
            var0.setVar("BuffProfRec", String.format("%d", var1), -1L);
         } else {
            var2 = var2 + String.format(";%d", var1);
            if (var2.length() > 250) {
               var0.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.LimitProfile", var0));
               var0.unsetVar("BuffProf-");
            } else {
               var0.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.BuffAdded", var0));
               var0.setVar("BuffProfRec", var2.trim(), -1L);
            }
         }
      }
   }

   private static boolean av(Player var0) {
      for (int var1 = 0; var1 < bWv.length; var1++) {
         if (Functions.getItemCount(var0, bWv[var1]) > 0L) {
            return true;
         }
      }

      return false;
   }

   private static boolean b(Player var0, NpcInstance var1) {
      if (var1 == null) {
         return false;
      } else {
         return !(var1 instanceof NpcBufferInstance)
            ? false
            : !var0.isActionsDisabled() && (Config.ALLOW_TALK_WHILE_SITTING || !var0.isSitting()) && var1.isInActingRange(var0);
      }
   }

   public void act(String[] var1) {
      Player var2 = this.getSelf();
      if (var2 != null && var1 != null && var1.length != 0) {
         NpcInstance var3 = null;
         if (!av(var2)) {
            var3 = this.getNpc();
            if (!b(var2, var3)) {
               var2.sendActionFailed();
               return;
            }
         }

         if (var2.isInCombat()) {
            var2.sendActionFailed();
         } else {
            Map var4 = D(var1[0]);
            String var5 = (String)var4.get("ask");
            String var6 = (String)var4.get("reply");
            if (var5 != null && !var5.isEmpty()) {
               try {
                  int var7 = Integer.parseInt(var5);
                  d(var7, var2);
               } catch (Exception var8) {
                  LOG.error("Buffer: Can't apply buff of menuId = \"" + var5 + "\"", var8);
               }
            }

            if (var6 != null && !var6.isEmpty()) {
               showPage(var2, var6, var3);
            } else {
               var2.sendActionFailed();
            }
         }
      }
   }

   public static void showPage(Player var0, String var1, NpcInstance var2) {
      if (var1 != null && !var1.contains("..")) {
         NpcHtmlMessage var3 = new NpcHtmlMessage(var0, var2);
         String var4 = "mods/buffer/" + var1 + ".htm";
         if (var0.hasBonus() && Config.ALT_NPC_BUFFER_PREMIUM_HTML_PREFIX) {
            var4 = "mods/buffer/" + var1 + "-pa.htm";
         } else if (Config.ALT_NPC_BUFFER_PREMIUM_ITEM_IDS != null && Config.ALT_NPC_BUFFER_PREMIUM_ITEM_IDS.length > 0) {
            for (int var8 : Config.ALT_NPC_BUFFER_PREMIUM_ITEM_IDS) {
               if (getItemCount(var0, var8) > 0L) {
                  var4 = "mods/buffer/" + var1 + "-pa.htm";
                  break;
               }
            }
         }

         var3.setFile(var4);
         var0.sendPacket(var3);
      }
   }

   public void actBBS(String[] var1) {
      Player var2 = this.getSelf();
      if (var2 != null && var1 != null && var1.length != 0) {
         if (Config.BBS_BUFFER_PEACE_ONLY && !var2.isInZonePeace()) {
            String var8 = HtmCache.getInstance().getNotNull("scripts/services/community/pages/locked.htm", var2);
            var8 = var8.replace("%name%", var2.getName());
            ShowBoard.separateAndSend(var8, var2);
         } else if (var2.isInCombat()) {
            var2.sendActionFailed();
         } else {
            Map var3 = D(var1[0]);
            String var4 = (String)var3.get("ask");
            String var5 = (String)var3.get("reply");
            if (var4 != null && !var4.isEmpty()) {
               try {
                  int var6 = Integer.parseInt(var4);
                  d(var6, var2);
               } catch (Exception var7) {
                  LOG.error("Buffer: Can't apply buff of menuId = \"" + var4 + "\"", var7);
               }
            }

            if (var5 != null && !var5.isEmpty()) {
               showBBSPage(var2, var5);
            } else {
               var2.sendActionFailed();
            }
         }
      }
   }

   public static void showBBSPage(Player var0, String var1) {
      if (var1 != null && !var1.contains("..")) {
         String var2 = HtmCache.getInstance().getNotNull("mods/buffer/community_buffer/" + var1 + ".htm", var0);
         if (var0.isGM()) {
            Functions.sendDebugMessage(var0, "HTML: mods/buffer/community_buffer/" + var1 + ".htm");
         }

         ShowBoard.separateAndSend(var2, var0);
      }
   }

   @Override
   public void onLoad() {
      dU();
      dT();
   }

   @Override
   public void onReload() {
      dU();
      dT();
   }

   @Override
   public void onShutdown() {
      bWi.clear();
   }

   private static void dT() {
      String var0 = ServerVariables.getString("BuffItemIds", "3433");
      StringTokenizer var1 = new StringTokenizer(var0, ";,", false);
      ArrayList var2 = new ArrayList();

      while (var1.hasMoreTokens()) {
         int var3 = Integer.parseInt(var1.nextToken());
         var2.add(var3);
      }

      if (bWu != null) {
         ItemHandler.getInstance().unregisterItemHandler(bWu);
      }

      bWv = new int[var2.size()];

      for (int var4 = 0; var4 < var2.size(); var4++) {
         bWv[var4] = (Integer)var2.get(var4);
      }

      bWu = new Buffer.BuffItemHandler();
      ItemHandler.getInstance().registerItemHandler(bWu);
      LOG.info("Buffer: Loaded " + bWv.length + " buff item(s).");
   }

   private static void dU() {
      CHashIntObjectMap var0 = new CHashIntObjectMap();

      try {
         Document var1 = aK.read(bWt);
         Element var2 = var1.getRootElement();
         Iterator var3 = var1.getRootElement().elementIterator();

         while (var3.hasNext()) {
            Element var4 = (Element)var3.next();
            if ("template".equalsIgnoreCase(var4.getName())) {
               int var5 = Integer.parseInt(var4.attributeValue("menuId"));
               Buffer.BuffTarget var6 = Buffer.BuffTarget.valueOf(var4.attributeValue("target").toUpperCase());
               int var7 = Integer.parseInt(var4.attributeValue("minLevel", "0"));
               int var8 = Integer.parseInt(var4.attributeValue("maxLevel", String.valueOf(Config.ALT_MAX_LEVEL)));
               ArrayList var9 = new ArrayList();
               boolean var10 = false;
               ArrayList var11 = new ArrayList();
               Iterator var12 = var4.elementIterator();

               while (var12.hasNext()) {
                  Element var13 = (Element)var12.next();
                  if ("consume".equalsIgnoreCase(var13.getName())) {
                     var10 = Boolean.parseBoolean(var13.attributeValue("anyFirst", "false"));
                     Iterator var25 = var13.elementIterator();

                     while (var25.hasNext()) {
                        Element var26 = (Element)var25.next();
                        if ("item".equalsIgnoreCase(var26.getName())) {
                           Element var27 = var26;
                           int var28 = Integer.parseInt(var27.attributeValue("id"));
                           long var29 = Long.parseLong(var27.attributeValue("amount"));
                           int var20 = Integer.parseInt(var27.attributeValue("from_level", "0"));
                           boolean var21 = Boolean.parseBoolean(var27.attributeValue("is_premium_required", "false"));
                           ItemTemplate var22 = ItemHolder.getInstance().getTemplate(var28);
                           var9.add(new Buffer.BuffTemplateConsume(var22, var29, var20, var21));
                        }
                     }
                  } else if ("produce".equalsIgnoreCase(var13.getName())) {
                     Iterator var14 = var13.elementIterator();

                     while (var14.hasNext()) {
                        Element var15 = (Element)var14.next();
                        if ("skill".equalsIgnoreCase(var15.getName())) {
                           Element var16 = var15;
                           int var17 = Integer.parseInt(var16.attributeValue("id"));
                           int var18 = Integer.parseInt(var16.attributeValue("level"));
                           Skill var19 = SkillTable.getInstance().getInfo(var17, var18);
                           var11.add(var19);
                        }
                     }
                  }
               }

               Buffer.BuffTemplate var24 = new Buffer.BuffTemplate(var11, var6, var9, var10, var7, var8);
               var0.put(var5, var24);
            }
         }

         bWi.clear();
         bWi.putAll(var0);
         LOG.info("Buffer: Loaded " + var0.size() + " buff template(s).");
      } catch (Exception var23) {
         LOG.error(var23.getMessage(), var23);
      }
   }

   private static Map<String, String> D(String var0) {
      TreeMap var1 = new TreeMap();
      char[] var2 = var0.toCharArray();
      StringBuilder var3 = new StringBuilder();
      String var4 = null;
      Object var5 = null;

      for (int var6 = 0; var6 < var2.length; var6++) {
         char var7 = var2[var6];
         if (var7 == '=' && var4 == null) {
            var4 = var3.toString();
            var3.setLength(0);
         } else if (var7 == '&') {
            var5 = var3.toString();
            var1.put(var4, var5);
            var3.setLength(0);
            var4 = null;
            var5 = null;
         } else {
            var3.append(var7);
         }
      }

      if (var4 != null) {
         var5 = var3.toString();
         var1.put(var4, var5);
      }

      return var1;
   }

   private static List<Skill> E(String var0) {
      LinkedList var1 = new LinkedList();
      StringTokenizer var2 = new StringTokenizer(var0, ";,", false);

      while (var2.hasMoreTokens()) {
         String var3 = var2.nextToken();
         int var4 = var3.indexOf(45);
         if (var4 > 0) {
            try {
               String var5 = var3.substring(0, var4);
               String var6 = var3.substring(var4 + 1);
               int var7 = Integer.parseInt(var5);
               int var8 = Integer.parseInt(var6);
               Skill var9 = SkillTable.getInstance().getInfo(var7, var8);
               if (var9 == null) {
                  LOG.error("Buffer: Buff template \"" + var3 + "\" skill " + var7 + "-" + var8 + " undefined!");
               } else {
                  var1.add(var9);
               }
            } catch (NumberFormatException var10) {
               LOG.error("Buffer: Can't parse buff template \"" + var3 + "\"", var10);
            }
         } else {
            LOG.error("Buffer: Can't parse buff template \"" + var3 + "\"");
         }
      }

      return var1;
   }

   private static class BuffItemHandler implements IItemHandler {
      private BuffItemHandler() {
      }

      @Override
      public boolean useItem(Playable var1, ItemInstance var2, boolean var3) {
         try {
            Player var4 = var1.getPlayer();
            if (var4 != null) {
               String var5 = "item_-" + var2.getItemId();
               if (var4.hasBonus() && Config.ALT_NPC_BUFFER_PREMIUM_HTML_PREFIX) {
                  var5 = "item-pa-" + var2.getItemId();
               } else if (Config.ALT_NPC_BUFFER_PREMIUM_ITEM_IDS != null && Config.ALT_NPC_BUFFER_PREMIUM_ITEM_IDS.length > 0) {
                  for (int var9 : Config.ALT_NPC_BUFFER_PREMIUM_ITEM_IDS) {
                     if (Functions.getItemCount(var4, var9) > 0L) {
                        var5 = "item-pa-" + var2.getItemId();
                        break;
                     }
                  }
               }

               Buffer.showPage(var1.getPlayer(), var5, null);
               return true;
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         return false;
      }

      @Override
      public void dropItem(Player var1, ItemInstance var2, long var3, Location var5) {
         if (!var1.getInventory().destroyItem(var2, var3)) {
            var1.sendActionFailed();
         } else {
            Log.LogItem(var1, ItemLog.Delete, var2);
            var1.disableDrop(1000);
            var1.sendChanges();
         }
      }

      @Override
      public boolean pickupItem(Playable var1, ItemInstance var2) {
         return false;
      }

      @Override
      public int[] getItemIds() {
         return Buffer.bWv;
      }
   }

   public enum BuffTarget {
      BUFF_PLAYER,
      BUFF_PET;
   }

   private static class BuffTemplate {
      private final List<Skill> bWx;
      private final Buffer.BuffTarget bWy;
      private final List<Buffer.BuffTemplateConsume> bWz;
      private final boolean bWA;
      private final int p;
      private final int s;

      protected BuffTemplate(List<Skill> var1, Buffer.BuffTarget var2, List<Buffer.BuffTemplateConsume> var3, boolean var4, int var5, int var6) {
         this.bWx = var1;
         this.bWy = var2;
         this.bWz = var3;
         this.bWA = var4;
         this.p = var5;
         this.s = var6;
      }

      private void m(Creature var1) {
         if (var1 != null) {
            boolean var2 = false;
            var1.block();
            Player player = var1.getPlayer();
            GameClient.PacketBatchScope batchScope = player != null ? player.openPacketBatch() : null;
            Creature.BulkBuffScope bulkScope = var1.openBulkBuffScope();
            try {
               for (Skill var4 : this.bWx) {
                  for (Effect var6 : var1.getEffectList().getAllEffects()) {
                     if (var6.getSkill().getId() == var4.getId()) {
                        var6.exit();
                        var2 = true;
                     }
                  }

                  if (var2) {
                     var1.sendPacket(new SystemMessage(92).addSkillName(var4.getDisplayId(), var4.getDisplayLevel()));
                  }

                  if (Config.ALT_NPC_BUFFER_EFFECT_TIME > 0L) {
                     var4.getEffects(var1, var1, false, false, Config.ALT_NPC_BUFFER_EFFECT_TIME, 1.0, false);
                  } else {
                     var4.getEffects(var1, var1, false, false);
                  }
               }
            } finally {
               if (bulkScope != null) {
                  bulkScope.close();
               }
               if (batchScope != null) {
                  batchScope.close();
               }
               var1.unblock();
            }
         }
      }

      private Creature n(Creature var1) {
         switch (this.bWy) {
            case BUFF_PLAYER:
               return var1.getPlayer();
            case BUFF_PET:
               return var1.getPet();
            default:
               return null;
         }
      }

      private boolean aw(Player var1) {
         if (!this.bWA) {
            for (Buffer.BuffTemplateConsume var3 : this.bWz) {
               if (var3.getFromLevel() <= 0 || var1.getLevel() >= var3.getFromLevel()) {
                  if (var3.isBonusRequired() && !var1.hasBonus()) {
                     return false;
                  }

                  if (!var3.mayConsume(var1)) {
                     return false;
                  }
               }
            }
         }

         for (Buffer.BuffTemplateConsume var5 : this.bWz) {
            if (var5.getFromLevel() <= 0 || var1.getLevel() >= var5.getFromLevel()) {
               if (var5.isBonusRequired() && !var1.hasBonus()) {
                  return false;
               }

               if (!this.bWA) {
                  if (!var5.consume(var1)) {
                     return false;
                  }
               } else if (var5.consume(var1)) {
                  return true;
               }
            }
         }

         return !this.bWA;
      }

      public boolean canApply(Player var1) {
         if (var1.isInZone(ZoneType.SIEGE) || var1.isInZone(ZoneType.water) || var1.isInDuel()) {
            return false;
         } else if (var1.isOlyParticipant()) {
            return false;
         } else if (var1.getLevel() < this.p) {
            var1.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.minLevel", var1).addNumber(this.p));
            return false;
         } else if (var1.getLevel() > this.s) {
            var1.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.maxLevel", var1).addNumber(this.s));
            return false;
         } else {
            return true;
         }
      }

      public boolean apply(final Player var1) {
         if (!this.canApply(var1)) {
            var1.sendPacket(new SystemMessage(113).addString("Buffer"));
            return false;
         } else {
            ThreadPoolManager.getInstance().execute(new RunnableImpl() {
               @Override
               public void runImpl() throws Exception {
                  Creature var1x = BuffTemplate.this.n(var1);
                  if (var1x == null) {
                     var1.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
                  } else if (!BuffTemplate.this.aw(var1)) {
                     var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                  } else {
                     BuffTemplate.this.m(var1x);
                  }
               }
            });
            return true;
         }
      }

      public boolean applySync(Player var1) {
         synchronized (var1) {
            if (!this.canApply(var1)) {
               var1.sendPacket(new SystemMessage(113).addString("Buffer"));
               return false;
            }

            Creature var3 = this.n(var1);
            if (var3 == null) {
               var1.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET);
               return false;
            }

            if (!this.aw(var1)) {
               return false;
            }

            this.m(var3);
            return true;
         }
      }
   }

   private static class BuffTemplateConsume {
      private final ItemTemplate KP;
      private final long aav;
      private final int bWB;
      private final boolean bWC;

      public BuffTemplateConsume(ItemTemplate var1, long var2, int var4, boolean var5) {
         this.KP = var1;
         this.aav = var2;
         this.bWB = var4;
         this.bWC = var5;
      }

      public ItemTemplate getItem() {
         return this.KP;
      }

      public long getAmount() {
         return this.aav;
      }

      public int getFromLevel() {
         return this.bWB;
      }

      public boolean isBonusRequired() {
         return this.bWC;
      }

      public boolean mayConsume(Player var1) {
         long var2 = var1.getInventory().getCountOf(this.getItem().getItemId());
         if (var2 < this.getAmount()) {
            if (this.getItem().isAdena()) {
               var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
               var1.sendMessage(new CustomMessage("scripts.npc.model.L2NpcBufferInstance.RequiresS1S2", var1, this.getItem().getName(), this.getAmount()));
            }

            return false;
         } else {
            return true;
         }
      }

      public boolean consume(Player var1) {
         return this.getAmount() == 0L
            ? var1.getInventory().getCountOf(this.getItem().getItemId()) > 0L
            : ItemFunctions.removeItem(var1, this.getItem().getItemId(), this.getAmount(), true, false) >= this.getAmount();
      }
   }
}
