package l2.gameserver.network.l2.fermata.s2c;

public class FightLinkChat extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0103;
    public static final byte REVISION = 1;

    private final int _speakerObjectId;
    private final int _channel;
    private final String _speaker;
    private final String _template;
    private final byte[] _referencesPayload;

    public FightLinkChat(int speakerObjectId, int channel, String speaker, String template, byte[] referencesPayload) {
        super(SUB_OPCODE);
        this._speakerObjectId = speakerObjectId;
        this._channel = channel;
        this._speaker = speaker == null ? "" : speaker;
        this._template = template == null ? "" : template;
        this._referencesPayload = referencesPayload == null ? new byte[0] : referencesPayload;
    }

    @Override
    protected void writePayload() {
        writeC(REVISION);
        writeD(_speakerObjectId);
        writeD(_channel);
        writeS(_speaker);
        writeS(_template);
        writeB(_referencesPayload);
    }
}
