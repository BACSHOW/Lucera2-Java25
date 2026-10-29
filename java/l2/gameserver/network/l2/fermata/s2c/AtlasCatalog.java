package l2.gameserver.network.l2.fermata.s2c;

import java.util.Collections;
import java.util.List;

public class AtlasCatalog extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x000F;

    public record CatalogRow(int npcId, int level, int flags, int spawnCount, int x, int y, int z, int respawnSeconds) {}

    private final int _revision;
    private final int _monsterTotal;
    private final int _page;
    private final int _pageCount;
    private final List<CatalogRow> _rows;

    public AtlasCatalog(int revision, int monsterTotal, int page, int pageCount, List<CatalogRow> rows) {
        super(SUB_OPCODE);
        this._revision = revision;
        this._monsterTotal = monsterTotal;
        this._page = page;
        this._pageCount = pageCount;
        this._rows = rows == null ? Collections.emptyList() : rows;
    }

    @Override
    protected void writePayload() {
        writeD(_revision);
        writeD(_monsterTotal);
        writeH(_page);
        writeH(_pageCount);
        writeH(_rows.size());
        for (CatalogRow r : _rows) {
            writeD(r.npcId());
            writeC(r.level());
            writeC(r.flags());
            writeH(r.spawnCount());
            writeD(r.x());
            writeD(r.y());
            writeD(r.z());
            writeD(r.respawnSeconds());
        }
    }
}
