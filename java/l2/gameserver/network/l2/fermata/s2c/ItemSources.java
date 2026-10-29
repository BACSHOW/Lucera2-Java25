package l2.gameserver.network.l2.fermata.s2c;

import java.util.Collections;
import java.util.List;

public class ItemSources extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0010;

    public record DropSource(int npcId, int group, int chance, int min, int max) {}
    public record ShopSource(int npcId, int price) {}

    private final int _revision;
    private final int _itemId;
    private final List<DropSource> _drops;
    private final List<ShopSource> _shops;
    private final List<Integer> _recipeIds;

    public ItemSources(int revision, int itemId, List<DropSource> drops, List<ShopSource> shops, List<Integer> recipeIds) {
        super(SUB_OPCODE);
        this._revision = revision;
        this._itemId = itemId;
        this._drops = drops == null ? Collections.emptyList() : drops;
        this._shops = shops == null ? Collections.emptyList() : shops;
        this._recipeIds = recipeIds == null ? Collections.emptyList() : recipeIds;
    }

    @Override
    protected void writePayload() {
        writeD(_revision);
        writeD(_itemId);
        writeH(_drops.size());
        for (DropSource d : _drops) {
            writeD(d.npcId());
            writeC(d.group());
            writeD(d.chance());
            writeD(d.min());
            writeD(d.max());
        }
        writeH(_shops.size());
        for (ShopSource s : _shops) {
            writeD(s.npcId());
            writeD(s.price());
        }
        writeH(_recipeIds.size());
        for (int rId : _recipeIds) {
            writeD(rId);
        }
    }
}
