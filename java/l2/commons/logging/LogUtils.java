/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
    private LogUtils() {
    }

    public static String dumpStack() {
        return LogUtils.dumpStack(new Throwable());
    }

    public static String dumpStack(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        return stringWriter.toString();
    }
}

