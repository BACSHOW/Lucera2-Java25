/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.accounts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.authserver.ThreadPoolManager;
import l2.authserver.accounts.Account;
import l2.authserver.network.l2.SessionKey;
import l2.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger _log = LoggerFactory.getLogger(SessionManager.class);
    private static final SessionManager nB = new SessionManager();
    private final Map<SessionKey, Session> nC = new HashMap<SessionKey, Session>();
    private final Lock lock = new ReentrantLock();

    public static final SessionManager getInstance() {
        return nB;
    }

    private SessionManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void runImpl() {
                SessionManager.this.lock.lock();
                try {
                    long l = System.currentTimeMillis();
                    Iterator iterator = SessionManager.this.nC.values().iterator();
                    while (iterator.hasNext()) {
                        Session session = (Session)iterator.next();
                        if (session.getExpireTime() >= l) continue;
                        iterator.remove();
                    }
                }
                finally {
                    SessionManager.this.lock.unlock();
                }
            }
        }, 30000L, 30000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Session openSession(Account account) {
        this.lock.lock();
        try {
            Session session = new Session(account);
            this.nC.put(session.getSessionKey(), session);
            Session session2 = session;
            return session2;
        }
        finally {
            this.lock.unlock();
        }
    }

    public Session closeSession(SessionKey sessionKey) {
        this.lock.lock();
        try {
            Session session = this.nC.remove(sessionKey);
            return session;
        }
        finally {
            this.lock.unlock();
        }
    }

    public Session getSessionByName(String string) {
        for (Session session : this.nC.values()) {
            if (!session.nD.getLogin().equalsIgnoreCase(string)) continue;
            return session;
        }
        return null;
    }

    public final class Session {
        private final Account nD;
        private final SessionKey nE;
        private final long nF;

        private Session(Account account) {
            this.nD = account;
            this.nE = SessionKey.create();
            this.nF = System.currentTimeMillis() + 60000L;
        }

        public SessionKey getSessionKey() {
            return this.nE;
        }

        public Account getAccount() {
            return this.nD;
        }

        public long getExpireTime() {
            return this.nF;
        }
    }
}

