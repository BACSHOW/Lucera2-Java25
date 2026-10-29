/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RunnableImpl
implements Runnable {
    public static final Logger _log = LoggerFactory.getLogger(RunnableImpl.class);

    public abstract void runImpl() throws Exception;

    @Override
    public final void run() {
        try {
            this.runImpl();
        }
        catch (Throwable throwable) {
            _log.error("Exception: RunnableImpl.run(): " + throwable, throwable);
        }
    }
}

