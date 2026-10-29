/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggerObject {
    protected final Logger _log = LoggerFactory.getLogger(this.getClass());

    public void error(String string, Exception exception) {
        this._log.error(this.getClass().getSimpleName() + ": " + string, exception);
    }

    public void error(String string) {
        this._log.error(this.getClass().getSimpleName() + ": " + string);
    }

    public void warn(String string, Exception exception) {
        this._log.warn(this.getClass().getSimpleName() + ": " + string, exception);
    }

    public void warn(String string) {
        this._log.warn(this.getClass().getSimpleName() + ": " + string);
    }

    public void info(String string, Exception exception) {
        this._log.info(this.getClass().getSimpleName() + ": " + string, exception);
    }

    public void info(String string) {
        this._log.info(this.getClass().getSimpleName() + ": " + string);
    }

    public void debug(String string) {
        this._log.debug(this.getClass().getSimpleName() + ": " + string);
    }

    public void debug(String string, Exception exception) {
        this._log.debug(this.getClass().getSimpleName() + ": " + string, exception);
    }
}
