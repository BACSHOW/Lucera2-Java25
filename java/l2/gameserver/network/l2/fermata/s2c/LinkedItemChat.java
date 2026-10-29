package l2.gameserver.network.l2.fermata.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.fermata.FermataItemSnapshot;

public class LinkedItemChat extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0003;
    public static final int MAX_MESSAGE_LENGTH = 100;
    public static final int MAX_LINKED_ITEMS = 4;

    private final int _senderObjectId;
    private final String _senderName;
    private final int _chatType;
    private final String _message;
    private final List<FermataItemSnapshot> _snapshots;

    public LinkedItemChat(int senderObjectId, String senderName, int chatType, String message, List<ItemInstance> items) {
        super(SUB_OPCODE);
        this._senderObjectId = senderObjectId;
        this._senderName = senderName == null ? "" : senderName;
        this._chatType = chatType;
        this._message = message == null ? "" : message;
        this._snapshots = new ArrayList<>();
        if (items != null) {
            int count = Math.min(MAX_LINKED_ITEMS, items.size());
            for (int i = 0; i < count; i++) {
                this._snapshots.add(new FermataItemSnapshot(items.get(i)));
            }
        }
    }

    public static LinkedItemChat fromSnapshots(int senderObjectId, String senderName, int chatType, String message, List<FermataItemSnapshot> snapshots) {
        LinkedItemChat packet = new LinkedItemChat(senderObjectId, senderName, chatType, message, null);
        if (snapshots != null) {
            int count = Math.min(MAX_LINKED_ITEMS, snapshots.size());
            for (int i = 0; i < count; i++) {
                packet._snapshots.add(snapshots.get(i));
            }
        }
        return packet;
    }

    @Override
    protected void writePayload() {
        writeD(_senderObjectId);
        writeS(_senderName);
        writeD(_chatType);

        int count = Math.min(MAX_LINKED_ITEMS, _snapshots.size());
        writeH(count);

        ByteBuffer scratch = ByteBuffer.allocate(FermataItemSnapshot.SNAPSHOT_SIZE);
        for (int i = 0; i < count; i++) {
            scratch.clear();
            _snapshots.get(i).writeTo(scratch);
            scratch.flip();
            writeB(scratch.array());
        }

        writeS(_message);
    }
}
