package l2.gameserver.network.authcomm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.authcomm.gs2as.AuthRequest;
import l2.gameserver.network.l2.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServerCommunication extends Thread {
   private static final Logger _log = LoggerFactory.getLogger(AuthServerCommunication.class);
   private static final AuthServerCommunication Zv = new AuthServerCommunication();
   private final Map<String, GameClient> Zw = new HashMap<>();
   private final Map<String, GameClient> Zx = new HashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock readLock = this.lock.readLock();
   private final Lock writeLock = this.lock.writeLock();
   private final ByteBuffer readBuffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
   private final ByteBuffer oy = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
   private final Queue<SendablePacket> sendQueue = new ArrayDeque<>();
   private final Lock sendLock = new ReentrantLock();
   private final AtomicBoolean isPengingWrite = new AtomicBoolean();
   private SelectionKey oB;
   private Selector oz;
   private boolean oA;
   private boolean Zy;

   public static final AuthServerCommunication getInstance() {
      return Zv;
   }

   private AuthServerCommunication() {
      try {
         this.oz = Selector.open();
      } catch (IOException var2) {
         _log.error("", var2);
      }
   }

   private void connect() throws IOException {
      _log.info("Connecting to authserver on " + Config.GAME_SERVER_LOGIN_HOST + ":" + Config.GAME_SERVER_LOGIN_PORT);
      SocketChannel var1 = SocketChannel.open();
      var1.configureBlocking(false);
      this.oB = var1.register(this.oz, 8);
      var1.connect(new InetSocketAddress(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT));
   }

   public void sendPacket(SendablePacket var1) {
      if (!this.isShutdown()) {
         this.sendLock.lock();

         boolean var2;
         label40: {
            try {
               this.sendQueue.add(var1);
               var2 = this.enableWriteInterest();
               break label40;
            } catch (CancelledKeyException var7) {
            } finally {
               this.sendLock.unlock();
            }

            return;
         }

         if (var2) {
            this.oz.wakeup();
         }
      }
   }

   private boolean disableWriteInterest() throws CancelledKeyException {
      if (this.isPengingWrite.compareAndSet(true, false)) {
         this.oB.interestOps(this.oB.interestOps() & -5);
         return true;
      } else {
         return false;
      }
   }

   private boolean enableWriteInterest() throws CancelledKeyException {
      if (!this.isPengingWrite.getAndSet(true)) {
         this.oB.interestOps(this.oB.interestOps() | 4);
         return true;
      } else {
         return false;
      }
   }

   protected ByteBuffer getReadBuffer() {
      return this.readBuffer;
   }

   protected ByteBuffer getWriteBuffer() {
      return this.oy;
   }

   @Override
   public void run() {
      while (!this.oA) {
         this.Zy = false;

         try {
            label78:
            while (!this.isShutdown()) {
               this.connect();
               long var5 = System.currentTimeMillis();
               int var7 = this.oz.select(5000L);
               var5 = System.currentTimeMillis() - var5;
               if (var7 == 0 && var5 < 5000L) {
                  for (SelectionKey var16 : this.oz.keys()) {
                     if (var16.isValid()) {
                        SocketChannel var9 = (SocketChannel)var16.channel();
                        if (var9 != null && (var16.interestOps() & 8) != 0) {
                           this.a(var16);
                           break label78;
                        }
                     }
                  }
               } else {
                  Set var1 = this.oz.selectedKeys();
                  if (var1.isEmpty()) {
                     throw new IOException("Connection timeout.");
                  }

                  Iterator var2 = var1.iterator();

                  try {
                     while (var2.hasNext()) {
                        SelectionKey var3 = (SelectionKey)var2.next();
                        var2.remove();
                        int var4 = var3.readyOps();
                        switch (var4) {
                           case 8:
                              this.a(var3);
                              break label78;
                        }
                     }
                  } catch (CancelledKeyException var11) {
                     break;
                  }
               }
            }

            while (!this.isShutdown()) {
               this.oz.select();
               Set var14 = this.oz.selectedKeys();
               Iterator var15 = var14.iterator();

               try {
                  while (var15.hasNext()) {
                     SelectionKey var17 = (SelectionKey)var15.next();
                     var15.remove();
                     int var18 = var17.readyOps();
                     switch (var18) {
                        case 1:
                           this.read(var17);
                        case 2:
                        case 3:
                        default:
                           break;
                        case 4:
                           this.write(var17);
                           break;
                        case 5:
                           this.write(var17);
                           this.read(var17);
                     }
                  }
               } catch (CancelledKeyException var12) {
                  break;
               }
            }
         } catch (IOException var13) {
            _log.error("AuthServer I/O error: " + var13.getMessage());
         }

         this.close();

         try {
            Thread.sleep(5000L);
         } catch (InterruptedException var10) {
         }
      }
   }

   private void read(SelectionKey var1) throws IOException {
      SocketChannel var2 = (SocketChannel)var1.channel();
      ByteBuffer var3 = this.getReadBuffer();
      int var4 = var2.read(var3);
      if (var4 == -1) {
         throw new IOException("End of stream.");
      }

      if (var4 != 0) {
         ((Buffer)var3).flip();

         while (this.a(var1, var3)) {
         }
      }
   }

   private boolean a(SelectionKey var1, ByteBuffer var2) throws IOException {
      int var3 = var2.position();
      if (var2.remaining() > 2) {
         int var4 = var2.getShort() & '\uffff';
         if (var4 <= 2) {
            throw new IOException("Incorrect packet size: <= 2");
         }

         var4 -= 2;
         if (var4 <= var2.remaining()) {
            int var5 = var2.limit();
            ((Buffer)var2).limit(var3 + var4 + 2);
            ReceivablePacket var6 = PacketHandler.handlePacket(var2);
            if (var6 != null && var6.read()) {
               ThreadPoolManager.getInstance().execute(var6);
            }

            ((Buffer)var2).limit(var5);
            ((Buffer)var2).position(var3 + var4 + 2);
            if (!var2.hasRemaining()) {
               ((Buffer)var2).clear();
               return false;
            }

            return true;
         }

         ((Buffer)var2).position(var3);
      }

      var2.compact();
      return false;
   }

   private void write(SelectionKey var1) throws IOException {
      SocketChannel var2 = (SocketChannel)var1.channel();
      ByteBuffer var3 = this.getWriteBuffer();
      this.sendLock.lock();

      boolean var4;
      try {
         int var5 = 0;

         SendablePacket var6;
         while (var5++ < 64 && (var6 = this.sendQueue.poll()) != null) {
            int var7 = var3.position();
            ((Buffer)var3).position(var7 + 2);
            var6.write();
            int var8 = var3.position() - var7 - 2;
            if (var8 == 0) {
               ((Buffer)var3).position(var7);
            } else {
               ((Buffer)var3).position(var7);
               var3.putShort((short)(var8 + 2));
               ((Buffer)var3).position(var7 + var8 + 2);
            }
         }

         var4 = this.sendQueue.isEmpty();
         if (var4) {
            this.disableWriteInterest();
         }
      } finally {
         this.sendLock.unlock();
      }

      ((Buffer)var3).flip();
      var2.write(var3);
      if (var3.remaining() > 0) {
         var3.compact();
         var4 = false;
      } else {
         ((Buffer)var3).clear();
      }

      if (!var4 && this.enableWriteInterest()) {
         this.oz.wakeup();
      }
   }

   private void a(SelectionKey var1) throws IOException {
      SocketChannel var2 = (SocketChannel)var1.channel();
      var2.finishConnect();
      var1.interestOps(var1.interestOps() & -9);
      var1.interestOps(var1.interestOps() | 1);
      this.sendPacket(new AuthRequest());
   }

   private void close() {
      this.Zy = !this.oA;
      this.sendLock.lock();

      try {
         this.sendQueue.clear();
      } finally {
         this.sendLock.unlock();
      }

      ((Buffer)this.readBuffer).clear();
      ((Buffer)this.oy).clear();
      this.isPengingWrite.set(false);

      try {
         if (this.oB != null) {
            this.oB.channel().close();
            this.oB.cancel();
         }
      } catch (IOException var10) {
      }

      this.writeLock.lock();

      try {
         this.Zw.clear();
      } finally {
         this.writeLock.unlock();
      }
   }

   public void shutdown() {
      this.oA = true;
      this.oz.wakeup();
   }

   public boolean isShutdown() {
      return this.oA || this.Zy;
   }

   public void restart() {
      this.Zy = true;
      this.oz.wakeup();
   }

   public GameClient addWaitingClient(GameClient var1) {
      this.writeLock.lock();

      try {
         return this.Zw.put(var1.getLogin(), var1);
      } finally {
         this.writeLock.unlock();
      }
   }

   public GameClient removeWaitingClient(String var1) {
      this.writeLock.lock();

      try {
         return this.Zw.remove(var1);
      } finally {
         this.writeLock.unlock();
      }
   }

   public GameClient addAuthedClient(GameClient var1) {
      this.writeLock.lock();

      try {
         return this.Zx.put(var1.getLogin(), var1);
      } finally {
         this.writeLock.unlock();
      }
   }

   public GameClient removeAuthedClient(String var1) {
      this.writeLock.lock();

      try {
         return this.Zx.remove(var1);
      } finally {
         this.writeLock.unlock();
      }
   }

   public GameClient getAuthedClient(String var1) {
      this.readLock.lock();

      try {
         return this.Zx.get(var1);
      } finally {
         this.readLock.unlock();
      }
   }

   public GameClient removeClient(GameClient var1) {
      this.writeLock.lock();

      try {
         return var1.isAuthed() ? this.Zx.remove(var1.getLogin()) : this.Zw.remove(var1.getSessionKey());
      } finally {
         this.writeLock.unlock();
      }
   }

   public String[] getAccounts() {
      this.readLock.lock();

      try {
         return this.Zx.keySet().toArray(new String[this.Zx.size()]);
      } finally {
         this.readLock.unlock();
      }
   }
}
