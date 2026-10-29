package l2.gameserver.network.l2.fermata.c2s;

import java.util.ArrayList;
import java.util.List;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.fermata.FermataSocialCoordination;
import l2.gameserver.network.l2.fermata.s2c.LinkedItemChat;

/**
 * Sub-opcode 0x0002 do protocolo Fermata 0xFD: chat normal contendo até 4 links de
 * itens do inventário do remetente.
 *
 * <p>Layout C2S:</p>
 * <pre>
 *   int   chatType (0=global, 1=shout, 2=tell, 3=party, 4=clan, 5=trade, 6=alliance)
 *   short itemCount (0..4)
 *   short[4] itemObjectIds        // objectId do inventário do jogador
 *   String message (UTF-16LE + duplo \0)
 * </pre>
 *
 * <p>Antes do broadcast, cada {@code objectId} precisa ser resolvido contra o inventário
 * do {@link Player} ativo. Itens que não pertencem ao jogador (ou estão no chão / em outro
 * inventário) são descartados silenciosamente para não vazar dados privados.</p>
 */
public class RequestFermataItemLinkChat extends L2GameClientPacket {

    public static final int SUB_OPCODE = 0x0002;
    public static final int MAX_LINKED_ITEMS = 4;

    private int _chatType;
    private int[] _itemObjectIds = new int[MAX_LINKED_ITEMS];
    private String _message;

    @Override
    protected void readImpl() {
        _chatType = readD();
        int itemCount = readH();
        if (itemCount < 0 || itemCount > MAX_LINKED_ITEMS) {
            // Pacote mal-formado: absorve sem falhar.
            _itemObjectIds = new int[0];
            _message = readS();
            return;
        }
        for (int i = 0; i < itemCount; i++) {
            _itemObjectIds[i] = readD();
        }
        _message = readS();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (!FermataSocialCoordination.allowItemLinkChat(activeChar)) {
            // Rate limit excedido: silenciosamente descarta o pacote.
            return;
        }

        // Validação de propriedade do inventário.
        List<ItemInstance> resolved = new ArrayList<>(MAX_LINKED_ITEMS);
        for (int objectId : _itemObjectIds) {
            if (objectId == 0) continue;
            ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
            if (item == null) continue;
            if (item.getOwnerId() != activeChar.getObjectId()) continue;
            if (item.isEquipped() && !FermataSocialCoordination.allowEquippedLink(activeChar, item)) {
                continue;
            }
            resolved.add(item);
        }

        if (_message == null) {
            _message = "";
        }
        // Limite rígido de tamanho para evitar abuso do chat.
        if (_message.length() > LinkedItemChat.MAX_MESSAGE_LENGTH) {
            _message = _message.substring(0, LinkedItemChat.MAX_MESSAGE_LENGTH);
        }

        LinkedItemChat broadcast = new LinkedItemChat(
            activeChar.getObjectId(),
            activeChar.getName(),
            _chatType,
            _message,
            resolved
        );
        activeChar.broadcastPacket(broadcast);
    }

    protected Player getActiveChar() {
        GameClient client = getClient();
        return client == null ? null : client.getActiveChar();
    }
}