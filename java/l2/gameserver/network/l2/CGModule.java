package l2.gameserver.network.l2;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;
import l2.commons.logging.LoggerObject;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.gs2as.ChangeAccessLevelMulti;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import org.apache.commons.lang3.tuple.Pair;

public abstract class CGModule<CGCtx extends CGModule.CGContext, CGKey> extends LoggerObject {
   public static CGModule getInstance() {
      return CGModule.CGMHelper.ZF;
   }

   public abstract String getName();

   public abstract CGModule init(String var1);

   public abstract void done();

   public abstract boolean isActive();

   public abstract CGCtx initClient(GameClient var1);

   public abstract void doneClient(CGCtx var1);

   public abstract GameCrypt<CGCtx, CGKey> initCrypt(CGCtx var1);

   public final boolean checkPacket(GameClient var1, ByteBuffer var2) {
      return this.checkPacket((CGCtx)var1.getCGMContext(), var2);
   }

   public final L2GameClientPacket handlePacket(GameClient var1, int var2) {
      return this.handlePacket((CGCtx)var1.getCGMContext(), var2, null);
   }

   public final L2GameClientPacket handlePacket(GameClient var1, int var2, Integer var3) {
      return this.handlePacket((CGCtx)var1.getCGMContext(), var2, var3);
   }

   public abstract L2GameClientPacket handlePacket(CGCtx var1, int var2, Integer var3);

   public boolean checkPacket(CGCtx var1, ByteBuffer var2) {
      return true;
   }

   public abstract String getHWIDText(CGCtx var1);

   public final void addHWIDBan(GameClient var1, String var2, String var3) {
      LinkedHashSet var4 = new LinkedHashSet();
      if (var1 != null && var1.isConnected()) {
         if (var2 != null && !var2.isEmpty()) {
            for (Player var10 : GameObjectsStorage.getAllPlayersForIterate()) {
               GameClient var11;
               if (var10 != null
                  && (var11 = var10.getNetConnection()) != null
                  && var11.isConnected()
                  && var11.getHwid() != null
                  && !var11.getHwid().isEmpty()
                  && var2.equals(var11.getHwid())
                  && var11.getLogin() != null
                  && !var11.getLogin().isEmpty()) {
                  var4.add(var11.getLogin());
               }
            }
         }

         if (!var4.isEmpty()) {
            AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevelMulti(var4, -100, 0));
         }

         String var9 = var1.getLogin();
         this.addHWIDBan((CGCtx)var1.getCGMContext(), var2, var1.getIpAddr(), var9, var3);
      } else {
         for (Player var6 : GameObjectsStorage.getAllPlayersForIterate()) {
            GameClient var7;
            if (var6 != null && (var7 = var6.getNetConnection()) != null && var7.isConnected() && var2.equals(var7.getHwid())) {
               this.addHWIDBan(var7, var2, var3);
               return;
            }
         }

         this.warn("No client to ban by HWID \"" + var2 + "\"");
      }
   }

   public abstract void addHWIDBan(CGCtx var1, String var2, String var3, String var4, String var5);

   public static class CGContext {
      private GameClient ZE;

      public CGContext(GameClient var1) {
         this.ZE = var1;
      }

      public GameClient getGameClient() {
         return this.ZE;
      }

      public void setGameClient(GameClient var1) {
         this.ZE = var1;
      }
   }

   public static class CGDefaultModule extends CGModule<CGModule.CGContext, byte[]> {
      @Override
      public String getName() {
         return "None";
      }

      @Override
      public CGModule init(String var1) {
         this.info("Initializing default CGModule.");
         return this;
      }

      @Override
      public void done() {
      }

      @Override
      public boolean isActive() {
         return false;
      }

      @Override
      public CGModule.CGContext initClient(GameClient var1) {
         return new CGModule.CGContext(var1);
      }

      @Override
      public void doneClient(CGModule.CGContext var1) {
         var1.setGameClient(null);
      }

      @Override
      public GameCrypt<CGModule.CGContext, byte[]> initCrypt(CGModule.CGContext var1) {
         return new GameCrypt<CGModule.CGContext, byte[]>() {};
      }

      @Override
      public L2GameClientPacket handlePacket(CGModule.CGContext var1, int var2, Integer var3) {
         return null;
      }

      @Override
      public String getHWIDText(CGModule.CGContext var1) {
         return null;
      }

      @Override
      public void addHWIDBan(CGModule.CGContext var1, String var2, String var3, String var4, String var5) {
      }
   }

   protected static class CGMHelper {
      private static final CGModule ZF = dn();

      private static CGModule dn() {
         return getModuleAndInit(getHelperConfig(Config.ALT_CG_MODULE));
      }

      public static CGModule getModuleAndInit(Pair<String, String> var0) {
         CGModule var1 = getModule(var0);
         if (var1 != null) {
            return var1.init((String)var0.getRight());
         } else {
            throw new IllegalStateException("CGModule \"" + Config.ALT_CG_MODULE + "\" not found");
         }
      }

      public static CGModule getModule(Pair<String, String> var0) {
         for (CGModule var2 : getAllHelpers()) {
            if (((String)var0.getKey()).equalsIgnoreCase(var2.getName())) {
               return var2;
            }
         }

         return null;
      }

      public static Pair<String, String> getHelperConfig(String var0) {
         if (var0 == null || var0.isEmpty()) {
            return Pair.of("None", null);
         }
         int var1 = var0.indexOf(40);
         int var2 = var0.lastIndexOf(41);
         return var1 >= 0 && var2 > var1 ? Pair.of(var0.substring(0, var1), var0.substring(var1 + 1, var2).trim()) : Pair.of(var0, null);
      }

      public static Set<CGModule> getAllHelpers() {
         ServiceLoader var0 = ServiceLoader.load(CGModule.class);
         HashSet var1 = new HashSet();
         var1.add(new CGModule.CGDefaultModule());

         for (Object __raw_var3_200 : (Iterable<?>)(Object)(var0)) {
            CGModule var3 = (CGModule)__raw_var3_200;
            if (var3 != null) {
               var1.add(var3);
            }
         }

         return var1;
      }
   }
}
