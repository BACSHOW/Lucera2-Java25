package l2.gameserver.network.l2.fermata.s2c;

import java.util.Collections;
import java.util.List;

public class NpcDrops extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0007;

    public record DropItem(int itemId, int min, int max, int chance) {}
    public record DropGroup(int kind, int categoryChance, int rolls, List<DropItem> items) {}

    private final int _npcId;
    private final List<DropGroup> _groups;

    public NpcDrops(int npcId, List<DropGroup> groups) {
        super(SUB_OPCODE);
        this._npcId = npcId;
        this._groups = groups == null ? Collections.emptyList() : groups;
    }

    @Override
    protected void writePayload() {
        writeD(_npcId);
        writeH(_groups.size());
        for (DropGroup g : _groups) {
            writeC(g.kind());
            writeD(g.categoryChance());
            writeC(g.rolls());
            List<DropItem> items = g.items() == null ? Collections.emptyList() : g.items();
            writeH(items.size());
            for (DropItem item : items) {
                writeD(item.itemId());
                writeD(item.min());
                writeD(item.max());
                writeD(item.chance());
            }
        }
    }
}
