package l2.commons.net.nio.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HaProxySelectorThread<T extends MMOClient> extends SelectorThread<T> {
   private static final Logger _log = LoggerFactory.getLogger(HaProxySelectorThread.class);
   private static final int qw = 16;
   private static final byte[] qx = new byte[]{13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10};
   private final byte[] qy = new byte[12];
   private final byte[] qz = new byte[12];
   private final byte[] qA = new byte[36];

   public HaProxySelectorThread(SelectorConfig var1, IPacketHandler<T> var2, IMMOExecutor<T> var3, IClientFactory<T> var4, IAcceptFilter var5) throws IOException {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public void openServerSocket(InetAddress var1, int var2) throws IOException {
      ServerSocketChannel var3 = ServerSocketChannel.open();
      var3.configureBlocking(false);
      var3.socket().bind(var1 == null ? new InetSocketAddress(var2) : new InetSocketAddress(var1, var2));
      var3.register(this.getSelector(), var3.validOps());
      this.setName("HaProxySelectorThread:" + var3.socket().getLocalPort());
   }

   @Override
   protected void acceptConnection(SelectionKey var1) {
      ServerSocketChannel var2 = (ServerSocketChannel)var1.channel();

      SocketChannel var3;
      try {
         while ((var3 = var2.accept()) != null) {
            var3.configureBlocking(false);
            SelectionKey var4 = var3.register(this.getSelector(), 1);
            Socket var5 = var3.socket();
            HaProxyMMOConnection var6 = new HaProxyMMOConnection<>(this, var5, var4);
            var6.setIpAddr("?.?.?.?");
            var4.attach(var6);
            this._connections.add(var6);
         }
      } catch (IOException var7) {
         _log.error("Error in " + this.getName(), var7);
      }
   }

   @Override
   protected void finishConnection(SelectionKey var1) {
      try {
         ((SocketChannel)var1.channel()).finishConnect();
      } catch (IOException var5) {
         HaProxyMMOConnection var3 = (HaProxyMMOConnection)var1.attachment();
         if (var3.isHaProxyInitiated()) {
            MMOClient var4 = var3.getClient();
            var4.getConnection().onForcedDisconnection();
         } else {
            var3.onForcedDisconnection();
         }

         this.closeConnectionImpl(var3);
      }
   }

   @Override
   protected void readPacket(SelectionKey var1) {
      HaProxyMMOConnection var2 = (HaProxyMMOConnection)var1.attachment();
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
               if (var2.isHaProxyInitiated()) {
                  while (this.tryReadPacket2(var1, var2, var3)) {
                  }
               } else if (this.tryInitHaProxyHeader(var1, var2, var3) && var2.isHaProxyInitiated()) {
                  while (this.tryReadPacket2(var1, var2, var3)) {
                  }
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

   private boolean a(byte[] var1) {
      return Arrays.equals(var1, qx);
   }

   private String a(int var1, byte[] var2) {
      if (var1 == 17) {
         return String.format("%d.%d.%d.%d", 255 & var2[0], 255 & var2[1], 255 & var2[2], 255 & var2[3]);
      } else {
         return var1 == 33
            ? String.format(
               "%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x",
               255 & var2[0],
               255 & var2[1],
               255 & var2[2],
               255 & var2[3],
               255 & var2[4],
               255 & var2[5],
               255 & var2[6],
               255 & var2[7],
               255 & var2[8],
               255 & var2[9],
               255 & var2[10],
               255 & var2[11],
               255 & var2[12],
               255 & var2[13],
               255 & var2[14],
               255 & var2[15]
            )
            : null;
      }
   }

   public static final String formatHex(byte[] var0) {
      StringBuilder var1 = new StringBuilder();

      for (byte var2 = 0; var2 < var0.length; var2 += 16) {
         var1.append(String.format("%04x: ", Integer.valueOf(var2)));

         for (int var3 = 0; var3 < 16; var3++) {
            int var4 = var2 + var3;
            if (var4 < var0.length) {
               var1.append(String.format("%02x ", var0[var4]));
            } else {
               var1.append("   ");
            }
         }

         var1.append(": ");

         for (int var6 = 0; var6 < 16; var6++) {
            int var7 = var2 + var6;
            if (var7 < var0.length) {
               byte var5 = var0[var7];
               if (var5 > 31 && var5 < 128) {
                  var1.append(String.format("%c", var5));
               } else {
                  var1.append('.');
               }
            } else {
               var1.append(' ');
            }
         }

         var1.append('\n');
      }

      return var1.toString();
   }

   protected boolean tryInitHaProxyHeader(SelectionKey var1, HaProxyMMOConnection<T> var2, ByteBuffer var3) {
      if (var2.isClosed()) {
         return false;
      }

      int var4 = var3.position();
      if (var3.remaining() >= 16) {
         var3.get(this.qy);
         if (!this.a(this.qy)) {
            Log.network("Incorrect HAProxy sign. Closing connection.");
            this.closeConnectionImpl(var2);
            return false;
         }

         int var5 = var3.get() & 255;
         int var6 = 15 & var5 >> 4;
         if (var6 != 2) {
            Log.network("Incorrect HAProxy version. Expected v2, got " + var6 + ". Closing connection.");
            this.closeConnectionImpl(var2);
            return false;
         }

         int var7 = 15 & var5;
         if (var7 == 0) {
            this.closeConnectionImpl(var2);
            return false;
         }

         if (var7 != 1) {
            Log.network("Unknown HAProxy command: " + var7 + ". Closing connection.");
            this.closeConnectionImpl(var2);
            return false;
         }

         int var8 = var3.get() & 255;
         byte[] var9 = null;
         if (var8 == 17) {
            var9 = this.qz;
         } else if (var8 == 33) {
            var9 = this.qA;
         }

         if (var9 == null) {
            Log.network("Incorrect HAProxy protocol family: " + var8 + "! Closing connection.");
            this.closeConnectionImpl(var2);
            return false;
         }

         int var10 = (var3.get() & 255) << 8;
         var10 |= var3.get() & 255;
         if (var10 <= 0 || var10 != var9.length) {
            Log.network("Incorrect HAProxy packet size : " + var10 + "! Closing connection.");
            this.closeConnectionImpl(var2);
            return false;
         }

         if (var10 <= var3.remaining()) {
            var3.get(var9);
            String var11 = this.a(var8, var9);
            if (var11 == null) {
               Log.network("Incorrect HAProxy source addr! Closing connection.");
               this.closeConnectionImpl(var2);
               return false;
            }

            ((Buffer)var3).position(var4 + 16 + var10);
            var2.setIpAddr(var11);
            var2.setHaProxyInitiated(true);
            T var12 = this.getClientFactory().create(var2);
            var12.setConnection(var2);
            var2.setClient(var12);
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
            "Read buffer exhausted for HAProxy client. Try to adjust buffer size, current : "
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

   @Override
   protected void closeConnectionImpl(MMOConnection<T> var1) {
      try {
         var1.onDisconnection();
      } finally {
         try {
            var1.close();
         } catch (IOException var24) {
         } finally {
            var1.releaseBuffers();
            var1.clearQueues();
            MMOClient var8 = var1.getClient();
            if (var8 != null) {
               var8.setConnection(null);
            }

            var1.getSelectionKey().attach(null);
            var1.getSelectionKey().cancel();
            this._connections.remove(var1);
            stats.decreseOpenedConnections();
         }
      }
   }
}
