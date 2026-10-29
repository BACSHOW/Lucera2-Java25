package l2.authserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.authserver.database.L2DatabaseFactory;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerManager {
   private static final Logger LOG = LoggerFactory.getLogger(GameServerManager.class);
   private static final GameServerManager nb = new GameServerManager();
   private final Map<Integer, GameServer> nc = new TreeMap<>();
   private final Map<Integer, List<ProxyServer>> nd = new TreeMap<>();
   private final Map<Integer, ProxyServer> ne = new TreeMap<>();
   private final ReadWriteLock nf = new ReentrantReadWriteLock();
   private final Lock ng = this.nf.readLock();
   private final Lock nh = this.nf.writeLock();

   public static final GameServerManager getInstance() {
      return nb;
   }

   public GameServerManager() {
      this.aL();
      LOG.info("Loaded " + this.nc.size() + " registered GameServer(s).");
      this.aM();
      LOG.info("Loaded " + this.ne.size() + " proxy server(s).");
   }

   private void aL() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = L2DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `server_id` FROM `gameservers`");
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("server_id");

            for (Config.ProxyServerConfig var8 : Config.PROXY_SERVERS_CONFIGS) {
               if (var8.getProxyId() == var4) {
                  LOG.warn("Server with id " + var4 + " collides with proxy server.");
               }
            }

            GameServer var14 = new GameServer(var4);
            this.nc.put(var4, var14);
         }
      } catch (Exception var12) {
         LOG.error("", var12);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   private void aM() {
      for (Config.ProxyServerConfig var4 : Config.PROXY_SERVERS_CONFIGS) {
         if (this.nc.containsKey(var4.getProxyId())) {
            LOG.warn("Won't load collided proxy with id " + var4.getProxyId() + ".");
         } else {
            ProxyServer var5 = new ProxyServer(var4.getOrigServerId(), var4.getProxyId());

            try {
               InetAddress var6 = InetAddress.getByName(var4.getPorxyHost());
               var5.setProxyAddr(var6);
            } catch (UnknownHostException var7) {
               LOG.error("Can't load proxy", var7);
               continue;
            }

            var5.setProxyPort(var4.getProxyPort());
            var5.setHideMain(var4.isHideMain());
            var5.setMinAccessLevel(var4.getMinAccessLevel());
            List var8 = this.nd.get(var5.getOrigServerId());
            if (var8 == null) {
               this.nd.put(var5.getOrigServerId(), var8 = new LinkedList());
            }

            var8.add(var5);
            this.ne.put(var5.getProxyServerId(), var5);
         }
      }
   }

   public List<ProxyServer> getProxyServersList(int var1) {
      List var2 = this.nd.get(var1);
      return var2 != null ? var2 : Collections.emptyList();
   }

   public ProxyServer getProxyServerById(int var1) {
      return this.ne.get(var1);
   }

   public GameServer[] getGameServers() {
      this.ng.lock();

      try {
         return this.nc.values().toArray(new GameServer[this.nc.size()]);
      } finally {
         this.ng.unlock();
      }
   }

   public GameServer getGameServerById(int var1) {
      this.ng.lock();

      try {
         return this.nc.get(var1);
      } finally {
         this.ng.unlock();
      }
   }

   public boolean registerGameServer(GameServer var1) {
      if (!Config.ACCEPT_NEW_GAMESERVER) {
         return false;
      }

      this.nh.lock();

      try {
         int var2 = 1;

         while (var2++ < 127) {
            GameServer var3 = this.nc.get(var2);
            if (this.ne.containsKey(var2) || var3 == null) {
               this.nc.put(var2, var1);
               var1.setId(var2);
               return true;
            }
         }
      } finally {
         this.nh.unlock();
      }

      return false;
   }

   public boolean registerGameServer(int var1, GameServer var2) {
      this.nh.lock();

      try {
         GameServer var3 = this.nc.get(var1);
         if (!Config.ACCEPT_NEW_GAMESERVER && var3 == null) {
            return false;
         }

         if (var3 == null || !var3.isAuthed()) {
            this.nc.put(var1, var2);
            var2.setId(var1);
            return true;
         }
      } finally {
         this.nh.unlock();
      }

      return false;
   }
}
