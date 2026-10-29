/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class CastleFakeTowerInstance
extends NpcInstance {
    public CastleFakeTowerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return false;
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
    }

    @Override
    public void showChatWindow(Player player, String string, Object ... objectArray) {
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }
}

