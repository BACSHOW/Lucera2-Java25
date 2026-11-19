/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2;

import java.nio.channels.SocketChannel;
import l2.authserver.LoginGuard;
import l2.authserver.LoginMetrics;
import l2.authserver.ThreadPoolManager;
import l2.authserver.network.l2.s2c.Init;
import l2.commons.net.nio.impl.IAcceptFilter;
import l2.commons.net.nio.impl.IClientFactory;
import l2.commons.net.nio.impl.IMMOExecutor;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.threading.RunnableImpl;

public class SelectorHelper
implements IAcceptFilter,
IClientFactory<L2LoginClient>,
IMMOExecutor<L2LoginClient> {
    @Override
    public void execute(Runnable runnable) {
        ThreadPoolManager.getInstance().execute(runnable);
    }

    @Override
    public L2LoginClient create(MMOConnection<L2LoginClient> mMOConnection) {
        final L2LoginClient l2LoginClient = new L2LoginClient(mMOConnection);
        l2LoginClient.sendPacket(new Init(l2LoginClient));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() {
                l2LoginClient.closeNow(false);
            }
        }, 60000L);
        return l2LoginClient;
    }

    @Override
    public boolean accept(SocketChannel socketChannel) {
        String ip = socketChannel.socket().getInetAddress().getHostAddress();
        // Abrindo conexoes demais: o LoginGuard bane e recusa. O IP ja banido continua sendo aceito
        // (dentro desse limite) so para receber a mensagem de bloqueio no RequestAuthLogin.
        if (!LoginGuard.getInstance().onConnection(ip)) {
            LoginMetrics.CONNECTIONS_REFUSED.increment();
            return false;
        }
        LoginMetrics.CONNECTIONS_ACCEPTED.increment();
        return true;
    }
}

