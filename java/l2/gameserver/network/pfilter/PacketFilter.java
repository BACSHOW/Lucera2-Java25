package l2.gameserver.network.pfilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.net.nio.impl.ReceivablePacket;
import l2.gameserver.Config;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class PacketFilter {
   private static final PacketFilter anM = new PacketFilter(null, Collections.emptyMap(), Collections.emptyMap()) {
      @Override
      public boolean isEnabled() {
         return false;
      }

      @Override
      public boolean checkPacket(int var1) {
         return true;
      }

      @Override
      public boolean checkPacketEx(int var1, int var2) {
         return true;
      }

      @Override
      public boolean checkPacket(ReceivablePacket<GameClient> var1) {
         return true;
      }
   };
   private final Map<Integer, RateLimit> anN;
   private final Map<Class<? extends L2GameClientPacket>, RateLimit> anO;
   private final GameClient anP;

   private PacketFilter(GameClient var1, Map<Integer, RateLimit> var2, Map<Class<? extends L2GameClientPacket>, RateLimit> var3) {
      this.anP = var1;
      this.anN = new HashMap<>(var2);
      this.anO = new HashMap<>(var3);
   }

   static int exOpcode(int var0, int var1) {
      return var0 << 16 | var1 & 65535;
   }

   public static PacketFilter create(GameClient var0) {
      if (!Config.PFILTER_ENABLED) {
         return anM;
      }

      HashMap var1 = new HashMap();
      HashMap var2 = new HashMap();

      for (Entry var4 : PacketFilterHolder.getInstance().getByOpcodeLimits().entrySet()) {
         var1.put(var4.getKey(), new RateLimit((Limit)var4.getValue()));
      }

      for (Entry var6 : PacketFilterHolder.getInstance().getByPacketClassLimits().entrySet()) {
         var2.put(var6.getKey(), new RateLimit((Limit)var6.getValue()));
      }

      return new PacketFilter(var0, var1, var2);
   }

   public boolean isEnabled() {
      return Config.PFILTER_ENABLED;
   }

   private RateLimit W(int var1) {
      return this.anN.get(var1);
   }

   private RateLimit j(int var1, int var2) {
      return this.anN.get(exOpcode(var1, var2));
   }

   private RateLimit f(Class<? extends L2GameClientPacket> var1) {
      return this.anO.get(var1);
   }

   public boolean checkRate(RateLimit var1) {
      return var1 == null || var1.tryPass(this.anP);
   }

   public boolean checkPacket(int var1) {
      return this.checkRate(this.W(var1));
   }

   public boolean checkPacketEx(int var1, int var2) {
      return this.checkRate(this.j(var1, var2));
   }

   public boolean checkPacket(ReceivablePacket<GameClient> var1) {
      return this.checkRate(this.f((Class<? extends L2GameClientPacket>)var1.getClass()));
   }
}
