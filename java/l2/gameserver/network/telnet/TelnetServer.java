/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import l2.gameserver.Config;
import l2.gameserver.network.telnet.TelnetPipelineFactory;
import l2.gameserver.network.telnet.TelnetServerHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class TelnetServer {
    public TelnetServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newFixedThreadPool(1), Executors.newFixedThreadPool(1), 1));
        TelnetServerHandler telnetServerHandler = new TelnetServerHandler();
        serverBootstrap.setPipelineFactory(new TelnetPipelineFactory(telnetServerHandler));
        serverBootstrap.bind(new InetSocketAddress(Config.TELNET_HOSTNAME.equals("*") ? null : Config.TELNET_HOSTNAME, Config.TELNET_PORT));
    }
}

