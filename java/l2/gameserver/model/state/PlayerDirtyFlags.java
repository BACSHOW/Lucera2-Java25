package l2.gameserver.model.state;

/**
 * Máscara de bits para dirty checking granular em memória.
 * Permite persistir cirurgicamente apenas os atributos do jogador que realmente sofreram mutação.
 */
public final class PlayerDirtyFlags {
    public static final int DIRTY_NONE        = 0;
    public static final int DIRTY_COORDINATES = 1 << 0; // x, y, z, heading
    public static final int DIRTY_VITALS      = 1 << 1; // hp, mp, cp
    public static final int DIRTY_STATS       = 1 << 2; // exp, sp, level
    public static final int DIRTY_PVP_PK      = 1 << 3; // pvpkills, pkkills, karma
    public static final int DIRTY_CLAN        = 1 << 4; // clanid, pledge_type, rank
    public static final int DIRTY_TIME        = 1 << 5; // onlinetime, lastAccess
    public static final int DIRTY_SETTINGS    = 1 << 6; // title, name, face, hair

    public static final int DIRTY_ALL         = 0x7F; // Todas as flags ativas

    private PlayerDirtyFlags() {}
}
