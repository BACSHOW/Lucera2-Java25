/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.attachment;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.attachment.ItemAttachment;

public interface PickableAttachment
extends ItemAttachment {
    public boolean canPickUp(Player var1);

    public void pickUp(Player var1);
}

