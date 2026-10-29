package l2.gameserver.network.l2.fermata.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.fermata.FermataItemSnapshot;

public class LinkedChat extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x000E;
    public static final int MAX_MESSAGE_LENGTH = 100;
    public static final int MAX_ITEMS = 4;
    public static final int MAX_SKILLS = 4;

    private final int _senderObjectId;
    private final String _senderName;
    private final int _chatType;
    private final String _message;
    private final List<FermataItemSnapshot> _snapshots;
    private final List<int[]> _skillData;

    public LinkedChat(int senderObjectId, String senderName, int chatType, String message,
                      List<ItemInstance> items, List<Skill> skills) {
        super(SUB_OPCODE);
        this._senderObjectId = senderObjectId;
        this._senderName = senderName == null ? "" : senderName;
        this._chatType = chatType;
        this._message = message == null ? "" : message;
        this._snapshots = new ArrayList<>();
        if (items != null) {
            int count = Math.min(MAX_ITEMS, items.size());
            for (int i = 0; i < count; i++) {
                this._snapshots.add(new FermataItemSnapshot(items.get(i)));
            }
        }
        this._skillData = new ArrayList<>();
        if (skills != null) {
            int count = Math.min(MAX_SKILLS, skills.size());
            for (int i = 0; i < count; i++) {
                Skill s = skills.get(i);
                this._skillData.add(new int[]{s.getId(), s.getLevel(), 0});
            }
        }
    }

    public static LinkedChat fromDirect(int senderObjectId, String senderName, int chatType, String message,
                                      List<FermataItemSnapshot> snapshots, List<int[]> skillData) {
        LinkedChat packet = new LinkedChat(senderObjectId, senderName, chatType, message, null, null);
        if (snapshots != null) {
            int count = Math.min(MAX_ITEMS, snapshots.size());
            for (int i = 0; i < count; i++) {
                packet._snapshots.add(snapshots.get(i));
            }
        }
        if (skillData != null) {
            int count = Math.min(MAX_SKILLS, skillData.size());
            for (int i = 0; i < count; i++) {
                packet._skillData.add(skillData.get(i));
            }
        }
        return packet;
    }

    @Override
    protected void writePayload() {
        writeD(_senderObjectId);
        writeS(_senderName);
        writeD(_chatType);

        int itemCount = Math.min(MAX_ITEMS, _snapshots.size());
        writeH(itemCount);
        ByteBuffer scratch = ByteBuffer.allocate(FermataItemSnapshot.SNAPSHOT_SIZE);
        for (int i = 0; i < itemCount; i++) {
            scratch.clear();
            _snapshots.get(i).writeTo(scratch);
            scratch.flip();
            writeB(scratch.array());
        }

        int skillCount = Math.min(MAX_SKILLS, _skillData.size());
        writeH(skillCount);
        for (int i = 0; i < skillCount; i++) {
            int[] s = _skillData.get(i);
            writeD(s[0]);
            writeD(s[1]);
            writeD(s.length > 2 ? s[2] : 0);
        }

        writeS(_message);
    }
}
