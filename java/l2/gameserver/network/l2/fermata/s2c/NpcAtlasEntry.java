package l2.gameserver.network.l2.fermata.s2c;

import java.util.Collections;
import java.util.List;

public class NpcAtlasEntry extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0011;

    public record Location(int x, int y, int z, int spawnCount) {}

    private final int _revision;
    private final int _npcId;
    private final int _level;
    private final int _flags;
    private final int _hp;
    private final int _mp;
    private final int _pAtk;
    private final int _pDef;
    private final int _mAtk;
    private final int _mDef;
    private final int _exp;
    private final int _sp;
    private final int _respawnSeconds;
    private final List<Location> _locations;

    public NpcAtlasEntry(int revision, int npcId, int level, int flags, int hp, int mp,
                         int pAtk, int pDef, int mAtk, int mDef, int exp, int sp,
                         int respawnSeconds, List<Location> locations) {
        super(SUB_OPCODE);
        this._revision = revision;
        this._npcId = npcId;
        this._level = level;
        this._flags = flags;
        this._hp = hp;
        this._mp = mp;
        this._pAtk = pAtk;
        this._pDef = pDef;
        this._mAtk = mAtk;
        this._mDef = mDef;
        this._exp = exp;
        this._sp = sp;
        this._respawnSeconds = respawnSeconds;
        this._locations = locations == null ? Collections.emptyList() : locations;
    }

    @Override
    protected void writePayload() {
        writeD(_revision);
        writeD(_npcId);
        writeC(_level);
        writeC(_flags);
        writeD(_hp);
        writeD(_mp);
        writeD(_pAtk);
        writeD(_pDef);
        writeD(_mAtk);
        writeD(_mDef);
        writeD(_exp);
        writeD(_sp);
        writeD(_respawnSeconds);
        writeH(_locations.size());
        for (Location loc : _locations) {
            writeD(loc.x());
            writeD(loc.y());
            writeD(loc.z());
            writeH(loc.spawnCount());
            writeH(0); // reserved
        }
    }
}
