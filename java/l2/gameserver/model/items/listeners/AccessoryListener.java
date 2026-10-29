/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.listeners;

import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;

public final class AccessoryListener
implements OnEquipListener {
    private static final AccessoryListener XP = new AccessoryListener();

    public static AccessoryListener getInstance() {
        return XP;
    }

    @Override
    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable()) {
            return;
        }
        Player player = (Player)playable;
        if (itemInstance.getBodyPart() == 0x200000 && itemInstance.getTemplate().getAttachedSkills().length > 0) {
            int n2 = player.getAgathionId();
            int n3 = player.getTransformationTemplate();
            for (Skill skill : itemInstance.getTemplate().getAttachedSkills()) {
                if (n2 > 0 && skill.getNpcId() == n2) {
                    player.setAgathion(0);
                }
                if (skill.getNpcId() != n3 || skill.getSkillType() != Skill.SkillType.TRANSFORMATION) continue;
                player.setTransformation(0);
            }
        }
        if (itemInstance.isAccessory() || itemInstance.getTemplate().isTalisman() || itemInstance.getTemplate().isBracelet()) {
            player.sendUserInfo(true);
        } else {
            player.broadcastCharInfo();
        }
    }

    @Override
    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable()) {
            return;
        }
        Player player = (Player)playable;
        if (itemInstance.isAccessory() || itemInstance.getTemplate().isTalisman() || itemInstance.getTemplate().isBracelet()) {
            player.sendUserInfo(true);
            player.sendItemList(false);
        } else {
            player.broadcastCharInfo();
        }
    }
}

