package l2.gameserver.network.l2.c2s;

/**
 * Absorvedor seguro de pacotes estendidos do protocolo Fermata (opcode 0xFD).
 * Consome os bytes restantes com segurança sem disparar desconexão por pacote desconhecido.
 */
public final class FermataDummyPacket extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        if (this._buf.hasRemaining()) {
            this._buf.position(this._buf.limit());
        }
    }

    @Override
    protected void runImpl() {
        // Sem ação, absorvido com sucesso
    }
}
