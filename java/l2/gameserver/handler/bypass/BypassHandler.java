/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.bypass;

import l2.gameserver.handler.bypass.IBypassHandler;

public class BypassHandler {
    private static final BypassHandler zr = new BypassHandler();

    public static BypassHandler getInstance() {
        return zr;
    }

    public void registerBypass(IBypassHandler iBypassHandler) {
    }
}

