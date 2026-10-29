/*
 * Decompiled with CFR 0.152.
 */
package handler.bypass;

import l2.gameserver.handler.bypass.BypassHandler;
import l2.gameserver.handler.bypass.IBypassHandler;
import l2.gameserver.scripts.ScriptFile;

public abstract class ScriptBypassHandler
implements IBypassHandler,
ScriptFile {
    @Override
    public void onLoad() {
        BypassHandler.getInstance().registerBypass(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

