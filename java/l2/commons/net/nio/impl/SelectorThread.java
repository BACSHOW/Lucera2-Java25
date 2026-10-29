package l2.commons.net.nio.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectorThread<T extends MMOClient> extends Thread {
   private static final Logger _log = LoggerFactory.getLogger(SelectorThread.class);
   private final Selector rh = Selector.open();
   private final IPacketHandler<T> ri;
   private final IMMOExecutor<T> qV;
   private final IClientFactory<T> rj;
   private IAcceptFilter rk;
   private boolean rl;
   protected final SelectorConfig _sc;
   private final int rm;
   private ByteBuffer rn;
   protected final ByteBuffer WRITE_BUFFER;
   protected final ByteBuffer READ_BUFFER;
   private T ro;
   protected final List<MMOConnection<T>> _connections;
   private final Queue<ByteBuffer> rp;
   private static final List<SelectorThread> rq = new ArrayList<>();
   protected static SelectorStats stats = new SelectorStats();
   public static long MAX_CONNECTIONS = Long.MAX_VALUE;

   public SelectorThread(SelectorConfig var1, IPacketHandler<T> var2, IMMOExecutor<T> var3, IClientFactory<T> var4, IAcceptFilter var5) throws IOException {
      synchronized (rq) {
         rq.add(this);
      }

      this._sc = var1;
      this.rk = var5;
      this.ri = var2;
      this.rj = var4;
      this.qV = var3;
      this.rp = new ArrayDeque<>(this._sc.HELPER_BUFFER_COUNT);
      this._connections = new CopyOnWriteArrayList<>();
      this.rn = ByteBuffer.wrap(new byte[this._sc.WRITE_BUFFER_SIZE]).order(this._sc.BYTE_ORDER);
      this.WRITE_BUFFER = ByteBuffer.wrap(new byte[this._sc.WRITE_BUFFER_SIZE]).order(this._sc.BYTE_ORDER);
      this.READ_BUFFER = ByteBuffer.wrap(new byte[this._sc.READ_BUFFER_SIZE]).order(this._sc.BYTE_ORDER);
      this.rm = Math.max(this._sc.READ_BUFFER_SIZE, this._sc.WRITE_BUFFER_SIZE);

      for (int var9 = 0; var9 < this._sc.HELPER_BUFFER_COUNT; var9++) {
         this.rp.add(ByteBuffer.wrap(new byte[this.rm]).order(this._sc.BYTE_ORDER));
      }
   }

   public void openServerSocket(InetAddress var1, int var2) throws IOException {
      ServerSocketChannel var3 = ServerSocketChannel.open();
      var3.configureBlocking(false);
      var3.socket().bind(var1 == null ? new InetSocketAddress(var2) : new InetSocketAddress(var1, var2));
      var3.register(this.getSelector(), var3.validOps());
      this.setName("SelectorThread:" + var3.socket().getLocalPort());
   }

   protected ByteBuffer getPooledBuffer() {
      return this.rp.isEmpty() ? ByteBuffer.wrap(new byte[this.rm]).order(this._sc.BYTE_ORDER) : this.rp.poll();
   }

   protected void recycleBuffer(ByteBuffer var1) {
      if (this.rp.size() < this._sc.HELPER_BUFFER_COUNT) {
         ((Buffer)var1).clear();
         this.rp.add(var1);
      }
   }

   protected void freeBuffer(ByteBuffer var1, MMOConnection<T> var2) {
      if (var1 == this.READ_BUFFER) {
         ((Buffer)this.READ_BUFFER).clear();
      } else {
         var2.setReadBuffer(null);
         this.recycleBuffer(var1);
      }
   }

   @Override
   public void run() {
      int var1 = 0;
      Set var2 = null;
      Iterator var3 = null;
      Object var4 = null;
      SelectionKey var5 = null;
      Object var6 = null;
      long var7 = 0L;

      while (true) {
         try {
            if (this.isShuttingDown()) {
               this.closeSelectorThread();
               return;
            }

            var7 = System.currentTimeMillis();

            for (MMOConnection var20 : this._connections) {
               if (!var20.isPengingClose() || var20.isPendingWrite() && var7 - var20.getPendingCloseTime() < 10000L) {
                  if (var20.isPendingWrite() && var7 - var20.getPendingWriteTime() >= this._sc.INTEREST_DELAY) {
                     var20.enableWriteInterest();
                  }
               } else {
                  this.closeConnectionImpl(var20);
               }
            }

            var1 = this.getSelector().selectNow();
            if (var1 > 0) {
               var2 = this.getSelector().selectedKeys();
               var3 = var2.iterator();

               while (var3.hasNext()) {
                  var5 = (SelectionKey)var3.next();
                  var3.remove();
                  if (var5.isValid()) {
                     try {
                        if (var5.isAcceptable()) {
                           this.acceptConnection(var5);
                        } else if (var5.isConnectable()) {
                           this.finishConnection(var5);
                        } else {
                           if (var5.isReadable()) {
                              this.readPacket(var5);
                           }

                           if (var5.isValid() && var5.isWritable()) {
                              this.writePacket(var5);
                           }
                        }
                     } catch (CancelledKeyException var13) {
                     }
                  }
               }
            }

            try {
               Thread.sleep(this._sc.SLEEP_TIME);
            } catch (InterruptedException var12) {
            }
         } catch (IOException var14) {
            _log.error("Error in " + this.getName(), var14);

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var11) {
            }
         }
      }
   }

   protected void finishConnection(SelectionKey var1) {
      try {
         ((SocketChannel)var1.channel()).finishConnect();
      } catch (IOException var5) {
         MMOConnection var3 = (MMOConnection)var1.attachment();
         MMOClient var4 = var3.getClient();
         var4.getConnection().onForcedDisconnection();
         this.closeConnectionImpl(var4.getConnection());
      }
   }

   protected void acceptConnection(SelectionKey var1) {
      ServerSocketChannel var2 = (ServerSocketChannel)var1.channel();

      SocketChannel var3;
      try {
         while ((var3 = var2.accept()) != null) {
            if (MAX_CONNECTIONS <= stats.getCurrentConnections() || this.getAcceptFilter() != null && !this.getAcceptFilter().accept(var3)) {
               var3.close();
            } else {
               var3.configureBlocking(false);
               SelectionKey var4 = var3.register(this.getSelector(), 1);
               Socket var5 = var3.socket();
               MMOConnection var6 = new MMOConnection<>(this, var5, var4);
               var6.setIpAddr(var5.getInetAddress().getHostAddress());
               MMOClient var7 = this.getClientFactory().create(var6);
               var7.setConnection(var6);
               var6.setClient(var7);
               var4.attach(var6);
               this._connections.add(var6);
               stats.increaseOpenedConnections();
            }
         }
      } catch (IOException var8) {
         _log.error("Error in " + this.getName(), var8);
      }
   }

   protected void readPacket(SelectionKey var1) {
      MMOConnection var2 = (MMOConnection)var1.attachment();
      if (!var2.isClosed()) {
         int var4 = -2;
         ByteBuffer var3;
         if ((var3 = var2.getReadBuffer()) == null) {
            var3 = this.READ_BUFFER;
         }

         if (var3.position() == var3.limit()) {
            Log.network(
               "Read buffer exhausted for client : "
                  + var2.getClient()
                  + ", try to adjust buffer size, current : "
                  + var3.capacity()
                  + ", primary : "
                  + (var3 == this.READ_BUFFER)
                  + ". Closing connection."
            );
            this.closeConnectionImpl(var2);
         } else {
            try {
               var4 = var2.getReadableByteChannel().read(var3);
            } catch (IOException var6) {
            }

            if (var4 > 0) {
               ((Buffer)var3).flip();
               stats.increaseIncomingBytes(var4);
               int var5 = 0;

               while (this.tryReadPacket2(var1, var2, var3)) {
                  var5++;
               }
            } else if (var4 == 0) {
               this.closeConnectionImpl(var2);
            } else if (var4 == -1) {
               this.closeConnectionImpl(var2);
            } else {
               var2.onForcedDisconnection();
               this.closeConnectionImpl(var2);
            }
         }

         if (var3 == this.READ_BUFFER) {
            ((Buffer)var3).clear();
         }
      }
   }

   protected boolean tryReadPacket2(SelectionKey var1, MMOConnection<T> var2, ByteBuffer var3) {
      if (var2.isClosed()) {
         return false;
      }

      int var4 = var3.position();
      if (var3.remaining() > this._sc.HEADER_SIZE) {
         int var5 = var3.getShort() & '\uffff';
         if (var5 <= this._sc.HEADER_SIZE || var5 > this._sc.PACKET_SIZE) {
            Log.network("Incorrect packet size : " + var5 + "! Client : " + var2.getClient() + ". Closing connection.");
            this.closeConnectionImpl(var2);
            return false;
         }

         var5 -= this._sc.HEADER_SIZE;
         if (var5 <= var3.remaining()) {
            stats.increaseIncomingPacketsCount();
            this.parseClientPacket(this.getPacketHandler(), var3, var5, var2);
            ((Buffer)var3).position(var4 + var5 + this._sc.HEADER_SIZE);
            if (!var3.hasRemaining()) {
               this.freeBuffer(var3, var2);
               return false;
            }

            return true;
         }

         ((Buffer)var3).position(var4);
      }

      if (var4 == var3.capacity()) {
         Log.network(
            "Read buffer exhausted for client : "
               + var2.getClient()
               + ", try to adjust buffer size, current : "
               + var3.capacity()
               + ", primary : "
               + (var3 == this.READ_BUFFER)
               + "."
         );
      }

      if (var3 == this.READ_BUFFER) {
         this.allocateReadBuffer(var2);
      } else {
         var3.compact();
      }

      return false;
   }

   protected void allocateReadBuffer(MMOConnection<T> var1) {
      var1.setReadBuffer(this.getPooledBuffer().put(this.READ_BUFFER));
      ((Buffer)this.READ_BUFFER).clear();
   }

   protected boolean parseClientPacket(IPacketHandler<T> var1, ByteBuffer var2, int var3, MMOConnection<T> var4) {
      MMOClient var5 = var4.getClient();
      int var6 = var2.position();
      var5.decrypt(var2, var3);
      ((Buffer)var2).position(var6);
      if (var2.hasRemaining()) {
         int var7 = var2.limit();
         ((Buffer)var2).limit(var6 + var3);
         ReceivablePacket var8 = var1.handlePacket(var2, (T)var5);
         if (var8 != null) {
            var8.setByteBuffer(var2);
            var8.setClient(var5);
            if (var8.read()) {
               var4.recvPacket(var8);
            }

            var8.setByteBuffer(null);
         }

         ((Buffer)var2).limit(var7);
      }

      return true;
   }

   protected void writePacket(SelectionKey var1) {
      MMOConnection var2 = (MMOConnection)var1.attachment();
      this.prepareWriteBuffer(var2);
      ((Buffer)this.rn).flip();
      int var3 = this.rn.remaining();
      int var4 = -1;

      try {
         var4 = var2.getWritableChannel().write(this.rn);
      } catch (IOException var6) {
      }

      if (var4 >= 0) {
         stats.increaseOutgoingBytes(var4);
         if (var4 != var3) {
            var2.createWriteBuffer(this.rn);
         }

         if (!var2.getSendQueue().isEmpty() || var2.hasPendingWriteBuffer()) {
            var2.scheduleWriteInterest();
         }
      } else {
         var2.onForcedDisconnection();
         this.closeConnectionImpl(var2);
      }
   }

   protected T getWriteClient() {
      return this.ro;
   }

   protected ByteBuffer getWriteBuffer() {
      return this.WRITE_BUFFER;
   }

   protected void prepareWriteBuffer(MMOConnection<T> var1) {
      this.ro = (T)var1.getClient();
      ((Buffer)this.rn).clear();
      if (var1.hasPendingWriteBuffer()) {
         var1.movePendingWriteBufferTo(this.rn);
      }

      if (this.rn.hasRemaining() && !var1.hasPendingWriteBuffer()) {
         Queue var3 = var1.getSendQueue();

         for (int var2 = 0; var2 < this._sc.MAX_SEND_PER_PASS; var2++) {
            SendablePacket var4;
            synchronized (var1) {
               if ((var4 = (SendablePacket)var3.poll()) == null) {
                  break;
               }
            }

            try {
               stats.increaseOutgoingPacketsCount();
               this.putPacketIntoWriteBuffer(var4, true);
               ((Buffer)this.WRITE_BUFFER).flip();
               if (this.rn.remaining() < this.WRITE_BUFFER.limit()) {
                  var1.createWriteBuffer(this.WRITE_BUFFER);
                  break;
               }

               this.rn.put(this.WRITE_BUFFER);
            } catch (Exception var7) {
               _log.error("Error in " + this.getName(), var7);
               break;
            }
         }
      }

      ((Buffer)this.WRITE_BUFFER).clear();
      this.ro = null;
   }

   protected final void putPacketIntoWriteBuffer(SendablePacket<T> var1, boolean var2) {
      ((Buffer)this.WRITE_BUFFER).clear();
      int var3 = this.WRITE_BUFFER.position();
      ((Buffer)this.WRITE_BUFFER).position(var3 + this._sc.HEADER_SIZE);
      var1.write();
      int var4 = this.WRITE_BUFFER.position() - var3 - this._sc.HEADER_SIZE;
      if (var4 == 0) {
         ((Buffer)this.WRITE_BUFFER).position(var3);
      } else {
         ((Buffer)this.WRITE_BUFFER).position(var3 + this._sc.HEADER_SIZE);
         if (var2) {
            this.ro.encrypt(this.WRITE_BUFFER, var4);
            var4 = this.WRITE_BUFFER.position() - var3 - this._sc.HEADER_SIZE;
         }

         ((Buffer)this.WRITE_BUFFER).position(var3);
         this.WRITE_BUFFER.putShort((short)((this._sc.HEADER_SIZE + var4) & 0xFFFF));
         ((Buffer)this.WRITE_BUFFER).position(var3 + this._sc.HEADER_SIZE + var4);
      }
   }

   protected SelectorConfig getConfig() {
      return this._sc;
   }

   protected Selector getSelector() {
      return this.rh;
   }

   protected IMMOExecutor<T> getExecutor() {
      return this.qV;
   }

   protected IPacketHandler<T> getPacketHandler() {
      return this.ri;
   }

   protected IClientFactory<T> getClientFactory() {
      return this.rj;
   }

   public void setAcceptFilter(IAcceptFilter var1) {
      this.rk = var1;
   }

   protected IAcceptFilter getAcceptFilter() {
      return this.rk;
   }

   protected void closeConnectionImpl(MMOConnection<T> var1) {
      try {
         var1.onDisconnection();
      } finally {
         try {
            var1.close();
         } catch (IOException var22) {
         } finally {
            var1.releaseBuffers();
            var1.clearQueues();
            var1.getClient().setConnection(null);
            var1.getSelectionKey().attach(null);
            var1.getSelectionKey().cancel();
            this._connections.remove(var1);
            stats.decreseOpenedConnections();
         }
      }
   }

   public void shutdown() {
      this.rl = true;
   }

   public boolean isShuttingDown() {
      return this.rl;
   }

   protected void closeAllChannels() {
      for (SelectionKey var3 : this.getSelector().keys()) {
         try {
            var3.channel().close();
         } catch (IOException var5) {
         }
      }
   }

   protected void closeSelectorThread() {
      this.closeAllChannels();

      try {
         this.getSelector().close();
      } catch (IOException var2) {
      }
   }

   public static CharSequence getStats() {
      StringBuilder var0 = new StringBuilder();
      var0.append("selectorThreadCount: .... ").append(rq.size()).append("\n");
      var0.append("=================================================\n");
      var0.append("getTotalConnections: .... ").append(stats.getTotalConnections()).append("\n");
      var0.append("getCurrentConnections: .. ").append(stats.getCurrentConnections()).append("\n");
      var0.append("getMaximumConnections: .. ").append(stats.getMaximumConnections()).append("\n");
      var0.append("getIncomingBytesTotal: .. ").append(stats.getIncomingBytesTotal()).append("\n");
      var0.append("getOutgoingBytesTotal: .. ").append(stats.getOutgoingBytesTotal()).append("\n");
      var0.append("getIncomingPacketsTotal:  ").append(stats.getIncomingPacketsTotal()).append("\n");
      var0.append("getOutgoingPacketsTotal:  ").append(stats.getOutgoingPacketsTotal()).append("\n");
      var0.append("getMaxBytesPerRead: ..... ").append(stats.getMaxBytesPerRead()).append("\n");
      var0.append("getMaxBytesPerWrite: .... ").append(stats.getMaxBytesPerWrite()).append("\n");
      var0.append("=================================================\n");
      return var0;
   }
}
