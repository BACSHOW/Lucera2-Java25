package l2.gameserver.model.entity.oly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParticipantPool {
   private static final Logger _log = LoggerFactory.getLogger(ParticipantPool.class);
   private static ParticipantPool Sz;
   private Map<CompetitionType, ArrayList<ParticipantPool.EntryRec>> SA;

   public static final ParticipantPool getInstance() {
      if (Sz == null) {
         Sz = new ParticipantPool();
      }

      return Sz;
   }

   private ParticipantPool() {
   }

   public void AllocatePools() {
      this.SA = new HashMap<>();

      for (CompetitionType var4 : CompetitionType.values()) {
         this.SA.put(var4, new ArrayList<>());
      }

      _log.info("OlyParticipantPool: Allocated " + this.SA.size() + " particiant pools.");
   }

   public void FreePools() {
      if (this.SA != null) {
         this.SA.clear();
      }

      _log.info("OlyParticipantPool: pools cleared.");
   }

   public boolean isEnough(CompetitionType var1, int var2, boolean var3) {
      switch (var1) {
         case CLASS_FREE:
            return this.SA.get(var1).size() >= Config.OLY_MIN_CF_START;
         case TEAM_CLASS_FREE:
            return this.SA.get(var1).size() >= Config.OLY_MIN_TB_START;
         case CLASS_INDIVIDUAL:
            int var8 = 0;

            for (ParticipantPool.EntryRec var10 : this.SA.get(var1)) {
               if (var10.cls_id == var2) {
                  var8++;
               }
            }

            return var8 >= Config.OLY_MIN_CB_START;
         case CLASS_TYPE_INDIVIDUAL:
            int var4 = 0;

            for (ParticipantPool.EntryRec var6 : this.SA.get(var1)) {
               ClassId var7 = ClassId.getClassById(var6.cls_id);
               if (var7 != null && var7.isMage() == var3) {
                  var4++;
               }
            }

            return var4 >= Config.OLY_MIN_CB_START;
         default:
            return false;
      }
   }

   public int getNearestIndex(CompetitionType var1, int var2, int var3, boolean var4) {
      ArrayList var5 = this.SA.get(var1);
      ParticipantPool.EntryRec var6 = (ParticipantPool.EntryRec)var5.get(var2);
      if (var6 == null) {
         return -1;
      }

      int var7 = Integer.MAX_VALUE;
      int var8 = Integer.MIN_VALUE;

      for (int var9 = 0; var9 < var2; var9++) {
         ParticipantPool.EntryRec var10 = (ParticipantPool.EntryRec)var5.get(var9);
         if (var10 != null && (var1 != CompetitionType.CLASS_INDIVIDUAL || var3 <= 0 || var3 == var10.cls_id)) {
            if (var1 == CompetitionType.CLASS_TYPE_INDIVIDUAL) {
               ClassId var11 = ClassId.getClassById(var10.cls_id);
               if (var11 == null || var11.isMage() != var4) {
                  continue;
               }
            }

            int var14 = Math.abs(var6.average - var10.average);
            if (var14 < var7) {
               var8 = var9;
               var7 = var14;
            }
         }
      }

      for (int var12 = var2 + 1; var12 < var5.size(); var12++) {
         ParticipantPool.EntryRec var13 = (ParticipantPool.EntryRec)var5.get(var12);
         if (var13 != null && (var1 != CompetitionType.CLASS_INDIVIDUAL || var3 <= 0 || var3 == var13.cls_id)) {
            if (var1 == CompetitionType.CLASS_TYPE_INDIVIDUAL) {
               ClassId var15 = ClassId.getClassById(var13.cls_id);
               if (var15 == null || var15.isMage() != var4) {
                  continue;
               }
            }

            int var16 = Math.abs(var6.average - var13.average);
            if (var16 < var7) {
               var8 = var12;
               var7 = var16;
            }
         }
      }

      return var8;
   }

   public void createEntry(CompetitionType var1, Player[] var2) {
      if (var2 != null && var2.length != 0) {
         synchronized ((ArrayList)this.SA.get(var1)) {
            this.SA.get(var1).add(new ParticipantPool.EntryRec(var2));
         }
      }
   }

   public Player[][] retrieveEntrys(CompetitionType var1, int var2, boolean var3) {
      this.b(var1);
      ArrayList var4 = this.SA.get(var1);
      int var5 = -1;
      long var6 = Long.MIN_VALUE;
      int var8 = -1;
      Player[][] var9 = (Player[][])null;
      synchronized (var4) {
         for (int var11 = 0; var11 < var4.size(); var11++) {
            ParticipantPool.EntryRec var12 = (ParticipantPool.EntryRec)var4.get(var11);
            if (var12 != null && (var1 != CompetitionType.CLASS_INDIVIDUAL || var2 <= 0 || var2 == var12.cls_id)) {
               if (var1 == CompetitionType.CLASS_TYPE_INDIVIDUAL) {
                  ClassId var13 = ClassId.getClassById(var12.cls_id);
                  if (var13 == null || var13.isMage() != var3) {
                     continue;
                  }
               }

               if (var12.reg_time > var6) {
                  var5 = var11;
                  var6 = var12.reg_time;
               }
            }
         }

         if (var5 < 0) {
            return (Player[][])null;
         }

         var8 = this.getNearestIndex(var1, var5, var2, var3);
         if (var8 < 0) {
            return (Player[][])null;
         }

         var9 = new Player[][]{
            Util.GetPlayersFromStoredIds(((ParticipantPool.EntryRec)var4.remove(var5)).sids),
            Util.GetPlayersFromStoredIds(((ParticipantPool.EntryRec)var4.remove(var8)).sids)
         };
         var4.trimToSize();
         return var9;
      }
   }

   public boolean removeEntryByPlayer(CompetitionType var1, Player var2) {
      long var3 = var2.getStoredId();
      ArrayList var5 = this.SA.get(var1);
      synchronized (var5) {
         for (int var7 = 0; var7 < var5.size(); var7++) {
            ParticipantPool.EntryRec var8 = (ParticipantPool.EntryRec)var5.get(var7);
            if (var8 != null) {
               for (long var12 : var8.sids) {
                  if (var12 == var3) {
                     var5.remove(var7);
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public CompetitionType getCompTypeOf(Player var1) {
      long var2 = var1.getStoredId();

      for (Entry var5 : this.SA.entrySet()) {
         ArrayList var6 = (ArrayList)var5.getValue();

         for (int var7 = 0; var7 < var6.size(); var7++) {
            ParticipantPool.EntryRec var8 = (ParticipantPool.EntryRec)var6.get(var7);
            if (var8 != null) {
               for (long var12 : var8.sids) {
                  if (var12 == var2) {
                     return (CompetitionType)var5.getKey();
                  }
               }
            }
         }
      }

      return null;
   }

   public boolean isRegistred(Player var1) {
      if (!OlyController.getInstance().isRegAllowed()) {
         return false;
      }

      for (CompetitionType var3 : this.SA.keySet()) {
         if (this.isRegistred(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean isHWIDRegistred(String var1) {
      if (!OlyController.getInstance().isRegAllowed()) {
         return false;
      }

      LinkedList var2 = new LinkedList();

      for (Entry var4 : this.SA.entrySet()) {
         ArrayList var5 = (ArrayList)var4.getValue();
         synchronized (var5) {
            var2.addAll(var5);
         }
      }

      for (Object __raw_var10_252 : (Iterable<?>)(Object)(var2)) {
         ParticipantPool.EntryRec var10 = (ParticipantPool.EntryRec)__raw_var10_252;
         for (int var11 = 0; var11 < var10.sids.length; var11++) {
            Player var6 = GameObjectsStorage.getAsPlayer(var10.sids[var11]);
            if (var6 != null
               && var6.getNetConnection() != null
               && var6.getNetConnection().getHwid() != null
               && var1.equalsIgnoreCase(var6.getNetConnection().getHwid())) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isIPRegistred(String var1) {
      if (OlyController.getInstance().isRegAllowed() && this.SA != null) {
         LinkedList var2 = new LinkedList();

         for (Entry var4 : this.SA.entrySet()) {
            ArrayList var5 = (ArrayList)var4.getValue();
            synchronized (var5) {
               var2.addAll(var5);
            }
         }

         for (Object __raw_var10_278 : (Iterable<?>)(Object)(var2)) {
            ParticipantPool.EntryRec var10 = (ParticipantPool.EntryRec)__raw_var10_278;
            for (int var11 = 0; var11 < var10.sids.length; var11++) {
               Player var6 = GameObjectsStorage.getAsPlayer(var10.sids[var11]);
               if (var6 != null
                  && var6.getNetConnection() != null
                  && var6.getNetConnection().getIpAddr() != null
                  && var6.getNetConnection().getIpAddr() != "?.?.?.?"
                  && var1.equalsIgnoreCase(var6.getNetConnection().getIpAddr())) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean isRegistred(CompetitionType var1, Player var2) {
      if (!OlyController.getInstance().isRegAllowed()) {
         return false;
      }

      long var3 = var2.getStoredId();

      for (ParticipantPool.EntryRec var7 : this.SA.get(var1)) {
         if (var7 != null) {
            for (long var11 : var7.sids) {
               if (var11 == var3) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void broadcastToEntrys(CompetitionType var1, L2GameServerPacket var2, int var3) {
      for (ParticipantPool.EntryRec var6 : this.SA.get(var1)) {
         if (var6 != null) {
            for (long var10 : var6.sids) {
               Player var12 = GameObjectsStorage.getAsPlayer(var10);
               if (var12 != null && (var3 <= 0 || var12.getClassId().getId() == var3)) {
                  var12.sendPacket(var2);
               }
            }
         }
      }
   }

   private void b(CompetitionType var1) {
      ArrayList var2 = this.SA.get(var1);
      synchronized (var2) {
         ArrayList var4 = new ArrayList();

         for (int var5 = 0; var5 < var2.size(); var5++) {
            if (!this.a((ParticipantPool.EntryRec)var2.get(var5))) {
               var4.add(var5);
            }
         }

         for (Object __raw_var6_341 : (Iterable<?>)(Object)(var4)) {
            int var6 = ((Number)__raw_var6_341).intValue();
            var2.remove(var6);
         }
      }
   }

   public void onLogout(Player var1) {
      if (OlyController.getInstance().isRegAllowed()) {
         CompetitionType var2 = getInstance().getCompTypeOf(var1);
         if (var2 != null) {
            this.removeEntryByPlayer(var2, var1);
         }
      }
   }

   private boolean a(ParticipantPool.EntryRec var1) {
      return true;
   }

   public int getParticipantCount() {
      int var1 = 0;

      for (Entry var3 : this.SA.entrySet()) {
         ArrayList var4 = (ArrayList)var3.getValue();

         for (int var5 = 0; var5 < var4.size(); var5++) {
            ParticipantPool.EntryRec var6 = (ParticipantPool.EntryRec)var4.get(var5);
            if (var6 != null) {
               var1 += var6.sids.length;
            }
         }
      }

      return var1;
   }

   private class EntryRec {
      long[] sids;
      int average;
      long reg_time;
      int cls_id;

      public EntryRec(Player[] var2) {
         this.sids = new long[var2.length];
         this.cls_id = var2[0].getClassId().getId();
         int var3 = 0;

         for (int var4 = 0; var4 < var2.length; var4++) {
            this.sids[var4] = var2[var4].getStoredId();
            var3 += Math.max(0, NoblesController.getInstance().getPointsOf(var2[var4].getObjectId()));
            OlyController.getInstance().incPartCount();
         }

         this.average = var3 / var2.length;
         this.reg_time = System.currentTimeMillis();
      }
   }
}
