/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import l2.gameserver.model.entity.events.GlobalEvent;

public interface InitableObject
extends Serializable {
    public void initObject(GlobalEvent var1);
}

