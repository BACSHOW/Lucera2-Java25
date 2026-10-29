/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;

public class RequestPetUseItem
extends L2GameClientPacket {
    private int CS;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        player.setActive();
        PetInstance petInstance = (PetInstance)player.getPet();
        if (petInstance == null) {
            return;
        }
        ItemInstance itemInstance = petInstance.getInventory().getItemByObjectId(this.CS);
        if (itemInstance == null || itemInstance.getCount() < 1L) {
            return;
        }
        if (player.isAlikeDead() || petInstance.isDead() || petInstance.isOutOfControl()) {
            player.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(itemInstance.getItemId()));
            return;
        }
        if (petInstance.tryFeedItem(itemInstance)) {
            return;
        }
        if (ArrayUtils.contains(Config.ALT_ALLOWED_PET_POTIONS, itemInstance.getItemId())) {
            Skill[] skillArray = itemInstance.getTemplate().getAttachedSkills();
            if (skillArray.length > 0) {
                for (Skill skill : skillArray) {
                    Creature creature = skill.getAimingTarget(petInstance, petInstance.getTarget());
                    if (!skill.checkCondition(petInstance, creature, false, false, true)) continue;
                    petInstance.getAI().Cast(skill, creature, false, false);
                }
            }
            return;
        }
        SystemMessage systemMessage = ItemFunctions.checkIfCanEquip(petInstance, itemInstance);
        if (systemMessage == null) {
            if (itemInstance.isEquipped()) {
                petInstance.getInventory().unEquipItem(itemInstance);
            } else {
                petInstance.getInventory().equipItem(itemInstance);
            }
            petInstance.broadcastCharInfo();
            return;
        }
        player.sendPacket((IStaticPacket)systemMessage);
    }
}

