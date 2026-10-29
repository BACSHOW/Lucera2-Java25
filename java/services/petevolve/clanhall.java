/*
 * Decompiled with CFR 0.152.
 */
package services.petevolve;

import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class clanhall
extends Functions {
    private static final int bYX = 12311;
    private static final int bYY = 12312;
    private static final int bYZ = 12313;
    private static final int bZa = 12526;
    private static final int bZb = 12527;
    private static final int bZc = 12528;
    private static final int bZd = PetDataTable.L2Pet.HATCHLING_WIND.getControlItemId();
    private static final int bZe = PetDataTable.L2Pet.HATCHLING_STAR.getControlItemId();
    private static final int bZf = PetDataTable.L2Pet.HATCHLING_TWILIGHT.getControlItemId();
    private static final int bZg = PetDataTable.L2Pet.STRIDER_WIND.getControlItemId();
    private static final int bZh = PetDataTable.L2Pet.STRIDER_STAR.getControlItemId();
    private static final int bZi = PetDataTable.L2Pet.STRIDER_TWILIGHT.getControlItemId();

    public void evolve() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        clanhall.show("scripts/services/petevolve/chamberlain.htm", player, npcInstance, new Object[0]);
    }

    public void wstrider(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        boolean bl = Integer.parseInt(stringArray[0]) == 1;
        if (player.getInventory().getCountOf(bl ? bZd : bZg) > 1L) {
            clanhall.show("scripts/services/petevolve/error_3.htm", player, npcInstance, new Object[0]);
            return;
        }
        if (player.getPet() != null) {
            clanhall.show("scripts/services/petevolve/error_4.htm", player, npcInstance, new Object[0]);
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByItemId(bl ? bZd : bZg);
        if (itemInstance == null) {
            clanhall.show("scripts/services/petevolve/no_item.htm", player, npcInstance, new Object[0]);
            return;
        }
        int n = PetDataTable.getSummonId(itemInstance);
        if (n == 0) {
            return;
        }
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        if (npcTemplate == null) {
            return;
        }
        PetInstance petInstance = PetInstance.restore(itemInstance, npcTemplate, player);
        if (n != (bl ? 12311 : 12526)) {
            clanhall.show("scripts/services/petevolve/error_2.htm", player, npcInstance, new Object[0]);
            return;
        }
        if (petInstance.getLevel() < 55) {
            clanhall.show("scripts/services/petevolve/error_lvl_strider.htm", player, npcInstance, new Object[0]);
            return;
        }
        itemInstance.setItemId(bl ? bZg : bZd);
        player.sendItemList(false);
        player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(bl ? bZg : bZd, 1L, 0));
        clanhall.show("scripts/services/petevolve/end_msg_strider.htm", player, npcInstance, new Object[0]);
    }

    public void sstrider(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        boolean bl = Integer.parseInt(stringArray[0]) == 1;
        if (player.getInventory().getCountOf(bl ? bZe : bZh) > 1L) {
            clanhall.show("scripts/services/petevolve/error_3.htm", player, npcInstance, new Object[0]);
            return;
        }
        if (player.getPet() != null) {
            clanhall.show("scripts/services/petevolve/error_4.htm", player, npcInstance, new Object[0]);
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByItemId(bl ? bZe : bZh);
        if (itemInstance == null) {
            clanhall.show("scripts/services/petevolve/no_item.htm", player, npcInstance, new Object[0]);
            return;
        }
        int n = PetDataTable.getSummonId(itemInstance);
        if (n == 0) {
            return;
        }
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        if (npcTemplate == null) {
            return;
        }
        PetInstance petInstance = PetInstance.restore(itemInstance, npcTemplate, player);
        if (n != (bl ? 12312 : 12527)) {
            clanhall.show("scripts/services/petevolve/error_2.htm", player, npcInstance, new Object[0]);
            return;
        }
        if (petInstance.getLevel() < 55) {
            clanhall.show("scripts/services/petevolve/error_lvl_strider.htm", player, npcInstance, new Object[0]);
            return;
        }
        itemInstance.setItemId(bl ? bZh : bZe);
        player.sendItemList(false);
        player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(bl ? bZh : bZe, 1L, 0));
        clanhall.show("scripts/services/petevolve/end_msg_strider.htm", player, npcInstance, new Object[0]);
    }

    public void tstrider(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        boolean bl = Integer.parseInt(stringArray[0]) == 1;
        if (player.getInventory().getCountOf(bl ? bZf : bZi) > 1L) {
            clanhall.show("scripts/services/petevolve/error_3.htm", player, npcInstance, new Object[0]);
            return;
        }
        if (player.getPet() != null) {
            clanhall.show("scripts/services/petevolve/error_4.htm", player, npcInstance, new Object[0]);
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByItemId(bl ? bZf : bZi);
        if (itemInstance == null) {
            clanhall.show("scripts/services/petevolve/no_item.htm", player, npcInstance, new Object[0]);
            return;
        }
        int n = PetDataTable.getSummonId(itemInstance);
        if (n == 0) {
            return;
        }
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        if (npcTemplate == null) {
            return;
        }
        PetInstance petInstance = PetInstance.restore(itemInstance, npcTemplate, player);
        if (n != (bl ? 12313 : 12528)) {
            clanhall.show("scripts/services/petevolve/error_2.htm", player, npcInstance, new Object[0]);
            return;
        }
        if (petInstance.getLevel() < 55) {
            clanhall.show("scripts/services/petevolve/error_lvl_strider.htm", player, npcInstance, new Object[0]);
            return;
        }
        itemInstance.setItemId(bl ? bZi : bZf);
        player.sendItemList(false);
        player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(bl ? bZi : bZf, 1L, 0));
        clanhall.show("scripts/services/petevolve/end_msg_strider.htm", player, npcInstance, new Object[0]);
    }
}

