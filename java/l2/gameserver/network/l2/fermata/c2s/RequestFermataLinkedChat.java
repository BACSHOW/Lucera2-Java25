package l2.gameserver.network.l2.fermata.c2s;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.fermata.FermataSocialCoordination;
import l2.gameserver.network.l2.fermata.s2c.LinkedChat;

/**
 * Sub-opcode 0x000A do protocolo Fermata 0xFD: chat com links de itens E skills
 * aprendidas pelo remetente.
 *
 * <p>Layout C2S:</p>
 * <pre>
 *   int   chatType
 *   short itemCount    (0..4)
 *   int[itemCount]   itemObjectIds
 *   short skillCount   (0..4)
 *   int[skillCount]   skillIds      // skill id no client
 *   String message
 * </pre>
 *
 * <p>Validação: cada item deve pertencer ao inventário do jogador; cada skill precisa
 * estar em {@code getAllSkills()}. Objetos não validados são descartados em silêncio.</p>
 */
public class RequestFermataLinkedChat extends L2GameClientPacket {

    public static final int SUB_OPCODE = 0x000A;
    public static final int MAX_LINKS = 4;

    private int _chatType;
    private int[] _itemObjectIds = new int[MAX_LINKS];
    private int[] _skillIds = new int[MAX_LINKS];
    private String _message;

    @Override
    protected void readImpl() {
        _chatType = readD();
        int itemCount = readH();
        if (itemCount < 0 || itemCount > MAX_LINKS) {
            readS();
            return;
        }
        for (int i = 0; i < itemCount; i++) {
            _itemObjectIds[i] = readD();
        }
        int skillCount = readH();
        if (skillCount < 0 || skillCount > MAX_LINKS) {
            readS();
            return;
        }
        for (int i = 0; i < skillCount; i++) {
            _skillIds[i] = readD();
        }
        _message = readS();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (!FermataSocialCoordination.allowLinkedChat(activeChar)) {
            return;
        }

        List<ItemInstance> resolvedItems = new ArrayList<>(MAX_LINKS);
        Set<Integer> resolvedSkillIds = new HashSet<>(MAX_LINKS);
        List<Skill> resolvedSkills = new ArrayList<>(MAX_LINKS);

        for (int objectId : _itemObjectIds) {
            if (objectId == 0) continue;
            ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
            if (item == null) continue;
            if (item.getOwnerId() != activeChar.getObjectId()) continue;
            if (item.isEquipped() && !FermataSocialCoordination.allowEquippedLink(activeChar, item)) continue;
            resolvedItems.add(item);
        }

        // Pré-calcula o conjunto de skills conhecidas para lookup O(1).
        Set<Integer> knownSkillIds = new HashSet<>();
        for (Skill s : activeChar.getAllSkills()) {
            if (s != null) {
                knownSkillIds.add(s.getId());
            }
        }
        for (int skillId : _skillIds) {
            if (skillId == 0) continue;
            if (!knownSkillIds.contains(skillId)) continue;
            if (!resolvedSkillIds.add(skillId)) continue;
            Skill skill = activeChar.getKnownSkill(skillId);
            if (skill != null) {
                resolvedSkills.add(skill);
            }
        }

        if (_message == null) {
            _message = "";
        }
        if (_message.length() > LinkedChat.MAX_MESSAGE_LENGTH) {
            _message = _message.substring(0, LinkedChat.MAX_MESSAGE_LENGTH);
        }

        LinkedChat broadcast = new LinkedChat(
            activeChar.getObjectId(),
            activeChar.getName(),
            _chatType,
            _message,
            resolvedItems,
            resolvedSkills
        );
        activeChar.broadcastPacket(broadcast);
    }

    protected Player getActiveChar() {
        GameClient client = getClient();
        return client == null ? null : client.getActiveChar();
    }
}