package l2.gameserver.network.l2.fermata.s2c;

import java.util.Collections;
import java.util.List;

public class EquipmentPreview extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0012;

    public static final short RESULT_OK = 0;
    public static final short RESULT_UNKNOWN_ITEM = 1;
    public static final short RESULT_NOT_EQUIPPABLE = 2;
    public static final short RESULT_INVALID_SLOT = 3;
    public static final short RESULT_THROTTLED = 4;

    public record StatDiff(int statId, int currentVal, int projectedVal) {}

    private final int _objectId;
    private final int _targetSlot;
    private final short _replyResult;
    private final byte _gradeFlags;
    private final byte _sectionFlags;
    private final List<StatDiff> _statDiffs;
    private final int[] _replacedItemObjectIds;
    private final List<int[]> _skills;

    public EquipmentPreview(int objectId, int targetSlot, short replyResult,
                            byte gradeFlags, byte sectionFlags, List<StatDiff> statDiffs,
                            int[] replacedItemObjectIds, List<int[]> skills) {
        super(SUB_OPCODE);
        this._objectId = objectId;
        this._targetSlot = targetSlot & 0xFF;
        this._replyResult = replyResult;
        this._gradeFlags = gradeFlags;
        this._sectionFlags = sectionFlags;
        this._statDiffs = statDiffs == null ? Collections.emptyList() : statDiffs;
        this._replacedItemObjectIds = replacedItemObjectIds == null ? new int[0] : replacedItemObjectIds;
        this._skills = skills == null ? Collections.emptyList() : skills;
    }

    public static EquipmentPreview failure(int objectId, int targetSlot, short result) {
        return new EquipmentPreview(objectId, targetSlot, result, (byte) 0, (byte) 0, null, null, null);
    }

    @Override
    protected void writePayload() {
        writeD(_objectId);
        writeC(_targetSlot);
        writeH(_replyResult);
        writeC(_gradeFlags);
        writeC(_sectionFlags);

        writeH(_statDiffs.size());
        for (StatDiff diff : _statDiffs) {
            writeC(diff.statId());
            writeD(diff.currentVal());
            writeD(diff.projectedVal());
        }

        writeC(_replacedItemObjectIds.length);
        for (int replacedId : _replacedItemObjectIds) {
            writeD(replacedId);
        }

        writeC(_skills.size());
        for (int[] sk : _skills) {
            writeD(sk[0]);
            writeC(sk.length > 1 ? sk[1] : 1);
        }
    }
}
