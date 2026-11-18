package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.Config;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.ConnectionQualityMonitor;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.netty.NettyGameConnection;

/**
 * {@code .ping}: mostra o ping do jogador, ha quanto tempo foi medido e se a conexao esta boa, instavel ou ruim.
 * Ligado por AltAllowPingCommand (altsettings.properties).
 */
public class Ping implements IVoicedCommandHandler {
    private static final String[] COMMANDS = new String[]{"ping"};

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        if (!Config.ALT_ALLOW_PING_COMMAND || !command.equals("ping")) {
            return false;
        }
        GameClient client = player.getNetConnection();
        if (client == null) {
            return false;
        }
        int ping = client.getLivePing();
        if (ping < 0) {
            player.sendMessage("Ping: medindo... tente de novo em alguns segundos.");
            return true;
        }
        long ageSec = Math.max(0L, client.getPingAgeMs() / 1000L);
        player.sendMessage("Ping: " + ping + " ms (medido ha " + ageSec + " s) - conexao " + ConnectionQualityMonitor.quality(client).label() + ".");
        NettyGameConnection conn = client.getNettyConnection();
        if (conn != null && conn.isLagging()) {
            player.sendMessage("Fila de envio: " + conn.pendingWriteBytes() / 1024L + " KB esperando para chegar ate voce.");
        }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}
