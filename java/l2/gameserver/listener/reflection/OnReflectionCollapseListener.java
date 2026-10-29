/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.reflection;

import l2.commons.listener.Listener;
import l2.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener
extends Listener<Reflection> {
    public void onReflectionCollapse(Reflection var1);
}

