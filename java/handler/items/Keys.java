/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import gnu.trove.TIntHashSet;
import handler.items.ScriptItemHandler;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.DoorHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.DoorTemplate;

public class Keys
extends ScriptItemHandler {
    private int[] iv = null;

    public Keys() {
        TIntHashSet tIntHashSet = new TIntHashSet();
        for (DoorTemplate doorTemplate : DoorHolder.getInstance().getDoors().values()) {
            if (doorTemplate == null || doorTemplate.getKey() <= 0) continue;
            tIntHashSet.add(doorTemplate.getKey());
        }
        this.iv = tIntHashSet.toArray();
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = playable.getPlayer();
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isDoor()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        DoorInstance doorInstance = (DoorInstance)gameObject;
        if (doorInstance.isOpen()) {
            player.sendPacket((IStaticPacket)Msg.IT_IS_NOT_LOCKED);
            return false;
        }
        if (doorInstance.getKey() <= 0 || itemInstance.getItemId() != doorInstance.getKey()) {
            player.sendPacket((IStaticPacket)Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }
        if (player.getDistance(doorInstance) > 300.0) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
            return false;
        }
        if (!player.getInventory().destroyItem(itemInstance, 1L)) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }
        player.sendPacket((IStaticPacket)SystemMessage2.removeItems(itemInstance.getItemId(), 1L));
        player.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Unlock.Success", player, new Object[0]));
        doorInstance.openMe(player, true);
        return true;
    }

    @Override
    public int[] getItemIds() {
        return this.iv;
    }
}

