package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.netty.NettyGameConnection;

/**
 * Teste de conexao lenta em localhost: limita a velocidade de envio para um jogador.
 *
 * <ul>
 * <li>{@code //netslow <jogador|me> <KB/s>} limita (ex.: 5 = 5 KB/s);</li>
 * <li>{@code //netslow <jogador|me> 0} tira o limite;</li>
 * <li>{@code //netslow} mostra a fila de envio de quem esta online (e quem esta limitado).</li>
 * </ul>
 *
 * <p>So para testes: o limite some quando o jogador desconecta.</p>
 */
public class AdminNetSlow implements IAdminCommandHandler {
    private static enum Commands {
        admin_netslow
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        if (activeChar == null) {
            return false;
        }
        if (wordList.length < 2) {
            return this.status(activeChar);
        }
        Player target = wordList[1].equalsIgnoreCase("me") ? activeChar : GameObjectsStorage.getPlayer(wordList[1]);
        if (target == null) {
            activeChar.sendMessage("[netslow] Jogador " + wordList[1] + " nao esta online.");
            return false;
        }
        NettyGameConnection conn = connection(target);
        if (conn == null) {
            activeChar.sendMessage("[netslow] " + target.getName() + " nao usa a rede Netty (UseNetty = false ou loja offline).");
            return false;
        }
        if (wordList.length < 3) {
            int cur = conn.getThrottleKbps();
            activeChar.sendMessage("[netslow] " + target.getName() + ": " + (cur > 0 ? cur + " KB/s" : "sem limite")
                    + ", fila de envio " + conn.pendingWriteBytes() / 1024L + " KB" + (conn.isWritable() ? "" : " (CHEIA)") + ".");
            activeChar.sendMessage("[netslow] Uso: //netslow <jogador|me> <KB/s>  (0 tira o limite)");
            return true;
        }
        int kbps;
        try {
            kbps = Integer.parseInt(wordList[2]);
        } catch (NumberFormatException e) {
            activeChar.sendMessage("[netslow] Valor invalido: " + wordList[2] + ". Use um numero de KB/s (0 tira o limite).");
            return false;
        }
        if (kbps < 0 || kbps > 100_000) {
            activeChar.sendMessage("[netslow] Use de 0 a 100000 KB/s.");
            return false;
        }
        conn.setThrottleKbps(kbps);
        activeChar.sendMessage("[netslow] " + target.getName() + ": " + (kbps > 0 ? "envio limitado a " + kbps + " KB/s." : "limite removido."));
        return true;
    }

    private boolean status(Player activeChar) {
        long[] q = NettyGameConnection.queueStats();
        activeChar.sendMessage("[netslow] Conexoes cheias: " + q[0] + " | maior fila: " + q[1] / 1024L + " KB | limitadas: " + q[2]);
        for (Player p : GameObjectsStorage.getAllPlayersForIterate()) {
            NettyGameConnection conn = connection(p);
            if (conn == null) {
                continue;
            }
            int kbps = conn.getThrottleKbps();
            long pendingKb = conn.pendingWriteBytes() / 1024L;
            if (kbps > 0 || pendingKb > 0 || !conn.isWritable()) {
                activeChar.sendMessage("  " + p.getName() + ": " + (kbps > 0 ? kbps + " KB/s" : "sem limite") + ", fila " + pendingKb + " KB"
                        + (conn.isWritable() ? "" : " (CHEIA)"));
            }
        }
        activeChar.sendMessage("[netslow] Uso: //netslow <jogador|me> <KB/s>  (0 tira o limite)");
        return true;
    }

    private static NettyGameConnection connection(Player p) {
        GameClient client = p.getNetConnection();
        return client == null ? null : client.getNettyConnection();
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }
}