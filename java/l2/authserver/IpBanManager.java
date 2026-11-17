/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.authserver.Config;
import l2.authserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpBanManager {
    private static final Logger _log = LoggerFactory.getLogger(IpBanManager.class);
    private static final IpBanManager ni = new IpBanManager();
    private final Map<String, IpSession> nj = new HashMap<String, IpSession>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();

    public static final IpBanManager getInstance() {
        return ni;
    }

    private IpBanManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                IpBanManager.this.writeLock.lock();
                try {
                    Iterator iterator = IpBanManager.this.nj.values().iterator();
                    while (iterator.hasNext()) {
                        IpSession ipSession = (IpSession)iterator.next();
                        if (ipSession.lastTry >= l - Config.LOGIN_TRY_TIMEOUT) continue;
                        iterator.remove();
                    }
                }
                finally {
                    IpBanManager.this.writeLock.unlock();
                }
            }
        }, 1000L, 1000L);
    }

    /** Zera as tentativas do IP (usado ao desbanir pela janela). */
    public void reset(String string) {
        this.writeLock.lock();
        try {
            this.nj.remove(string);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /** Quantas tentativas erradas ainda faltam para o ban (Integer.MAX_VALUE para IP confiavel). */
    public int remainingTries(String string) {
        if (!Config.AUTO_BAN || LoginGuard.getInstance().isTrusted(string)) {
            return Integer.MAX_VALUE;
        }
        this.readLock.lock();
        try {
            IpSession ipSession = this.nj.get(string);
            return Config.LOGIN_TRY_BEFORE_BAN - (ipSession == null ? 0 : ipSession.tryCount);
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean tryLogin(String string, boolean bl) {
        if (!Config.AUTO_BAN || LoginGuard.getInstance().isTrusted(string)) {
            return true;
        }
        this.writeLock.lock();
        try {
            long l;
            IpSession ipSession = this.nj.get(string);
            if (ipSession == null) {
                ipSession = new IpSession();
                this.nj.put(string, ipSession);
            }
            if ((l = System.currentTimeMillis()) - ipSession.lastTry < Config.LOGIN_TRY_TIMEOUT) {
                bl = false;
            }
            if (bl) {
                if (ipSession.tryCount > 0) {
                    --ipSession.tryCount;
                }
            } else if (ipSession.tryCount < Config.LOGIN_TRY_BEFORE_BAN) {
                ++ipSession.tryCount;
            }
            ipSession.lastTry = l;
            if (ipSession.tryCount >= Config.LOGIN_TRY_BEFORE_BAN) {
                // ban progressivo (IpBanSteps) pelo LoginGuard; as tentativas recomecam do zero
                LoginGuard.getInstance().ban(string, "senhas erradas");
                ipSession.tryCount = 0;
                boolean bl2 = false;
                return bl2;
            }
            boolean bl3 = true;
            return bl3;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    private class IpSession {
        public int tryCount;
        public long lastTry;

        private IpSession() {
        }
    }
}

