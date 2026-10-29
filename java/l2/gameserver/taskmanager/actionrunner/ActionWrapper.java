/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.actionrunner;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.taskmanager.actionrunner.ActionRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActionWrapper
extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(ActionWrapper.class);
    private final String _name;
    private Future<?> arC;

    public ActionWrapper(String string) {
        this._name = string;
    }

    public void schedule(long l) {
        this.arC = ThreadPoolManager.getInstance().schedule(this, l);
    }

    public void cancel() {
        if (this.arC != null) {
            this.arC.cancel(true);
            this.arC = null;
        }
    }

    public abstract void runImpl0() throws Exception;

    @Override
    public void runImpl() {
        try {
            this.runImpl0();
        }
        catch (Exception exception) {
            _log.info("ActionWrapper: Exception: " + exception + "; name: " + this._name, exception);
        }
        finally {
            ActionRunner.getInstance().remove(this._name, this);
            this.arC = null;
        }
    }

    public String getName() {
        return this._name;
    }
}

