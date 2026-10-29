/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.gameserver.Config;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import l2.gameserver.network.telnet.commands.TelnetBan;
import l2.gameserver.network.telnet.commands.TelnetConfig;
import l2.gameserver.network.telnet.commands.TelnetDebug;
import l2.gameserver.network.telnet.commands.TelnetItems;
import l2.gameserver.network.telnet.commands.TelnetPerfomance;
import l2.gameserver.network.telnet.commands.TelnetSay;
import l2.gameserver.network.telnet.commands.TelnetServer;
import l2.gameserver.network.telnet.commands.TelnetStatus;
import l2.gameserver.network.telnet.commands.TelnetWorld;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelnetServerHandler
extends SimpleChannelUpstreamHandler
implements TelnetCommandHolder {
    private static final Logger _log = LoggerFactory.getLogger(TelnetServerHandler.class);
    private static final Pattern anZ = Pattern.compile("\"([^\"]*)\"|([^\\s]+)");
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetServerHandler() {
        this.aoa.add(new TelnetCommand("help", new String[]{"h"}){

            @Override
            public String getUsage() {
                return "help [command]";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length == 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Available commands:\n");
                    for (TelnetCommand telnetCommand : TelnetServerHandler.this.aoa) {
                        stringBuilder.append(telnetCommand.getCommand()).append("\n");
                    }
                    return stringBuilder.toString();
                }
                TelnetCommand telnetCommand = TelnetServerHandler.this.x(stringArray[0]);
                if (telnetCommand == null) {
                    return "Unknown command.\n";
                }
                return "usage:\n" + telnetCommand.getUsage() + "\n";
            }
        });
        this.addHandler(new TelnetBan());
        this.addHandler(new TelnetConfig());
        this.addHandler(new TelnetDebug());
        this.addHandler(new TelnetPerfomance());
        this.addHandler(new TelnetSay());
        this.addHandler(new TelnetServer());
        this.addHandler(new TelnetStatus());
        this.addHandler(new TelnetWorld());
        this.addHandler(new TelnetItems());
    }

    public void addHandler(TelnetCommandHolder telnetCommandHolder) {
        for (TelnetCommand telnetCommand : telnetCommandHolder.getCommands()) {
            this.aoa.add(telnetCommand);
        }
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }

    private TelnetCommand x(String string) {
        for (TelnetCommand telnetCommand : this.aoa) {
            if (!telnetCommand.equals(string)) continue;
            return telnetCommand;
        }
        return null;
    }

    private String a(String string, String[] stringArray) {
        TelnetCommand telnetCommand = this.x(string);
        if (telnetCommand == null) {
            return "Unknown command.\n";
        }
        String string2 = telnetCommand.handle(stringArray);
        if (string2 == null) {
            string2 = "usage:\n" + telnetCommand.getUsage() + "\n";
        }
        return string2;
    }

    @Override
    public void channelConnected(ChannelHandlerContext channelHandlerContext, ChannelStateEvent channelStateEvent) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Welcome to L2 GameServer telnet console.\n");
        stringBuilder.append("It is " + new Date() + " now.\n");
        channelStateEvent.getChannel().write(stringBuilder.toString().replaceAll("\n", "\r\n"));
        if (!Config.TELNET_PASSWORD.isEmpty()) {
            channelStateEvent.getChannel().write("Password:");
            channelHandlerContext.setAttachment(Boolean.FALSE);
        } else {
            channelStateEvent.getChannel().write("Type 'help' to see all available commands.\r\n");
            channelHandlerContext.setAttachment(Boolean.TRUE);
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext channelHandlerContext, MessageEvent messageEvent) {
        Object object;
        String string = (String)messageEvent.getMessage();
        String string2 = null;
        boolean bl = false;
        if (Boolean.FALSE.equals(channelHandlerContext.getAttachment())) {
            if (Config.TELNET_PASSWORD.equals(string)) {
                channelHandlerContext.setAttachment(Boolean.TRUE);
                string = "";
            } else {
                string2 = "Wrong password!\n";
                bl = true;
            }
        }
        if (Boolean.TRUE.equals(channelHandlerContext.getAttachment())) {
            if (string.isEmpty()) {
                string2 = "Type 'help' to see all available commands: ";
            } else if (string.toLowerCase().equals("exit")) {
                string2 = "Have a good day!\n";
                bl = true;
            } else {
                object = anZ.matcher(string);
                ((Matcher)object).find();
                String string3 = ((Matcher)object).group();
                ArrayList<String> arrayList = new ArrayList<String>();
                while (((Matcher)object).find()) {
                    String string4 = ((Matcher)object).group(1);
                    if (string4 == null) {
                        string4 = ((Matcher)object).group(0);
                    }
                    arrayList.add(string4);
                }
                string2 = this.a(string3, arrayList.toArray(new String[arrayList.size()]));
            }
        }
        ChannelFuture writeFuture = messageEvent.getChannel().write(string2.replaceAll("\n", "\r\n"));
        if (bl) {
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, ExceptionEvent exceptionEvent) {
        if (exceptionEvent.getCause() instanceof IOException) {
            exceptionEvent.getChannel().close();
        } else {
            _log.error("", exceptionEvent.getCause());
        }
    }
}

