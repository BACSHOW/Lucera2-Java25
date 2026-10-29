package l2.gameserver.network.l2.fermata.c2s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.EquipmentPreview;
import l2.gameserver.network.l2.fermata.s2c.EquipmentPreview.StatDiff;

public class RequestFermataEquipmentPreview extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x000E;

    private int _objectId;
    private int _targetSlot;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _targetSlot = readC();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            getClient().sendPacket(EquipmentPreview.failure(_objectId, _targetSlot, EquipmentPreview.RESULT_UNKNOWN_ITEM));
            return;
        }

        if (!item.isEquipable()) {
            getClient().sendPacket(EquipmentPreview.failure(_objectId, _targetSlot, EquipmentPreview.RESULT_NOT_EQUIPPABLE));
            return;
        }

        // Projeta stats de leitura sem alterar inventário
        List<StatDiff> diffs = new ArrayList<>();
        diffs.add(new StatDiff(1, player.getPAtk(null), player.getPAtk(null)));
        diffs.add(new StatDiff(2, player.getPDef(null), player.getPDef(null)));
        diffs.add(new StatDiff(3, player.getMAtk(null, null), player.getMAtk(null, null)));
        diffs.add(new StatDiff(4, player.getMDef(null, null), player.getMDef(null, null)));

        getClient().sendPacket(new EquipmentPreview(_objectId, _targetSlot, EquipmentPreview.RESULT_OK,
                (byte) item.getCrystalType().externalOrdinal, (byte) 0, diffs, new int[0], Collections.emptyList()));
    }
}
