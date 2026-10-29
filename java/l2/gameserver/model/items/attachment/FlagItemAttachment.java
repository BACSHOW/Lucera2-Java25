/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.attachment;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.attachment.PickableAttachment;

public interface FlagItemAttachment
extends PickableAttachment {
    public void onLogout(Player var1);

    public void onDeath(Player var1, Creature var2);

    public void onEnterPeace(Player var1);

    public boolean canAttack(Player var1);

    public boolean canCast(Player var1, Skill var2);
}

