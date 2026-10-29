package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.residence.Residence;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

public final class ResidenceHolder extends AbstractHolder {
   private static ResidenceHolder wK = new ResidenceHolder();
   private IntObjectMap<Residence> wL = new TreeIntObjectMap();
   private Map<Class, List<Residence>> wM = new HashMap<>(4);

   public static ResidenceHolder getInstance() {
      return wK;
   }

   private ResidenceHolder() {
   }

   public void addResidence(Residence var1) {
      this.wL.put(var1.getId(), var1);
   }

   public <R extends Residence> R getResidence(int var1) {
      return (R)this.wL.get(var1);
   }

   public <R extends Residence> R getResidence(Class<R> var1, int var2) {
      Residence var3 = this.getResidence(var2);
      return (R)(var3 != null && var3.getClass() == var1 ? var3 : null);
   }

   public <R extends Residence> List<R> getResidenceList(Class<R> var1) {
      return (List<R>)this.wM.get(var1);
   }

   public Collection<Residence> getResidences() {
      return this.wL.values();
   }

   public <R extends Residence> R getResidenceByObject(Class<? extends Residence> var1, GameObject var2) {
      return (R)this.getResidenceByCoord((Class)var1, var2.getX(), var2.getY(), var2.getZ(), var2.getReflection());
   }

   public <R extends Residence> R getResidenceByCoord(Class<R> var1, int var2, int var3, int var4, Reflection var5) {
      for (Residence var8 : var1 == null ? this.getResidences() : this.getResidenceList(var1)) {
         if (var8.checkIfInZone(var2, var3, var4, var5)) {
            return (R)var8;
         }
      }

      return null;
   }

   public <R extends Residence> R findNearestResidence(Class<R> var1, int var2, int var3, int var4, Reflection var5, int var6) {
      Residence var7 = this.getResidenceByCoord(var1, var2, var3, var4, var5);
      if (var7 == null) {
         double var8 = var6;

         for (Residence var13 : this.getResidenceList(var1)) {
            double var10 = var13.getZone().findDistanceToZone(var2, var3, var4, false);
            if (var8 > var10) {
               var8 = var10;
               var7 = var13;
            }
         }
      }

      return (R)var7;
   }

   public void callInit() {
      for (Residence var2 : this.getResidences()) {
         var2.init();
      }
   }

   private void bo() {
      for (Residence var2 : this.wL.values()) {
         List var3 = this.wM.get(var2.getClass());
         if (var3 == null) {
            this.wM.put(var2.getClass(), var3 = new ArrayList());
         }

         var3.add(var2);
      }
   }

   @Override
   public void log() {
      this.bo();
      this.info("total size: " + this.wL.size());

      for (Entry var2 : this.wM.entrySet()) {
         this.info(" - load " + ((List)var2.getValue()).size() + " " + ((Class)var2.getKey()).getSimpleName().toLowerCase() + "(s).");
      }
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public void clear() {
      this.wL.clear();
      this.wM.clear();
   }
}
