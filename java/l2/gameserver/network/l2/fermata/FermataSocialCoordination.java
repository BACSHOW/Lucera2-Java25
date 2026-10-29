package l2.gameserver.network.l2.fermata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2.commons.util.RateTracker;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

/**
 * Camada de coordenação e rate-limit dos pacotes sociais do protocolo Fermata (chat
 * com links, pings de party, beacons de coordenação). Centraliza os limites para que
 * {@link l2.gameserver.network.l2.fermata.c2s.RequestFermataItemLinkChat},
 * {@link l2.gameserver.network.l2.fermata.c2s.RequestFermataLinkedChat},
 * {@link l2.gameserver.network.l2.fermata.c2s.RequestFermataPartyPing} e
 * {@link l2.gameserver.network.l2.fermata.c2s.RequestFermataCoordination}
 * consultem os mesmos trackers e não permitam abuso coordenado de sub-opcodes.
 *
 * <p>Os limites são por jogador (chave = {@code objectId}). A limpeza é feita por GC
 * quando o jogador sai: a entrada vira unreachable junto com a referência interna do
 * {@link Player}, e o rate tracker é barato (1 long atômico + limite imutável).</p>
 */
public final class FermataSocialCoordination {

    /** 4 mensagens com links / 5s — equivalente ao limite do chat tradicional. */
    public static final int ITEM_LINK_LIMIT = 4;
    public static final int ITEM_LINK_WINDOW_MS = 5_000;

    /** 8 links (itens + skills) / 5s — inclui links múltiplos na mesma mensagem. */
    public static final int LINKED_CHAT_LIMIT = 8;
    public static final int LINKED_CHAT_WINDOW_MS = 5_000;

    /** 6 pings de party / 3s — protege contra spam de marcadores. */
    public static final int PARTY_PING_LIMIT = 6;
    public static final int PARTY_PING_WINDOW_MS = 3_000;

    /** 3 atualizações de coordenação / 2s — beacon novo a cada 2s é mais que suficiente. */
    public static final int COORDINATION_LIMIT = 3;
    public static final int COORDINATION_WINDOW_MS = 2_000;

    /** Limite rígido: não permitir link de itens com augmentation legendary. */
    public static final int LEGENDARY_AUGMENT_THRESHOLD = 300;

    private static final Map<Integer, RateTracker> ITEM_LINK_TRACKERS = new ConcurrentHashMap<>();
    private static final Map<Integer, RateTracker> LINKED_CHAT_TRACKERS = new ConcurrentHashMap<>();
    private static final Map<Integer, RateTracker> PARTY_PING_TRACKERS = new ConcurrentHashMap<>();
    private static final Map<Integer, RateTracker> COORDINATION_TRACKERS = new ConcurrentHashMap<>();

    private FermataSocialCoordination() {
    }

    public static boolean allowItemLinkChat(int objectId) {
        return tracker(objectId, ITEM_LINK_TRACKERS, ITEM_LINK_LIMIT, ITEM_LINK_WINDOW_MS).tryPass();
    }

    public static boolean allowItemLinkChat(Player player) {
        return allowItemLinkChat(player.getObjectId());
    }

    public static boolean allowLinkedChat(int objectId) {
        return tracker(objectId, LINKED_CHAT_TRACKERS, LINKED_CHAT_LIMIT, LINKED_CHAT_WINDOW_MS).tryPass();
    }

    public static boolean allowLinkedChat(Player player) {
        return allowLinkedChat(player.getObjectId());
    }

    public static boolean allowPartyPing(int objectId) {
        return tracker(objectId, PARTY_PING_TRACKERS, PARTY_PING_LIMIT, PARTY_PING_WINDOW_MS).tryPass();
    }

    public static boolean allowPartyPing(Player player) {
        return allowPartyPing(player.getObjectId());
    }

    public static boolean allowCoordination(int objectId) {
        return tracker(objectId, COORDINATION_TRACKERS, COORDINATION_LIMIT, COORDINATION_WINDOW_MS).tryPass();
    }

    public static boolean allowCoordination(Player player) {
        return allowCoordination(player.getObjectId());
    }

    /**
     * Política conservadora: itens equipados legendary / epics não devem ser linkados
     * publicamente por padrão para reduzir surface area de engenharia social.
     */
    public static boolean allowEquippedLink(Player player, ItemInstance item) {
        if (item == null) return false;
        int aug1 = item.getVariationStat1();
        int aug2 = item.getVariationStat2();
        return aug1 < LEGENDARY_AUGMENT_THRESHOLD && aug2 < LEGENDARY_AUGMENT_THRESHOLD;
    }

    /**
     * Chamado pelo ciclo de vida do jogador (logout / troca de char) para liberar
     * memória dos trackers de rate-limit. Idempotente.
     */
    public static void clearPlayer(int objectId) {
        ITEM_LINK_TRACKERS.remove(objectId);
        LINKED_CHAT_TRACKERS.remove(objectId);
        PARTY_PING_TRACKERS.remove(objectId);
        COORDINATION_TRACKERS.remove(objectId);
    }

    private static RateTracker tracker(int objectId,
                                       Map<Integer, RateTracker> map,
                                       int limit,
                                       int windowMs) {
        return map.computeIfAbsent(objectId, k -> new RateTracker(limit, windowMs));
    }
}