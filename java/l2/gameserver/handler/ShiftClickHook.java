package l2.gameserver.handler;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;

/**
 * Ponto para um servico (script) tratar o Shift + clique em um objeto antes da janela original do jogo.
 * O Action chama {@link #handle}; se o servico tratar, a janela original nao abre.
 *
 * <p>Usado pelo Monster Data (services.MobInformer), que registra o handler no onLoad e tira no onShutdown.
 * Sem handler registrado, o Shift + clique segue o caminho original (AllowShiftClick / AltShowDroplist).</p>
 */
public final class ShiftClickHook {
    @FunctionalInterface
    public interface Handler {
        /** true = tratou (a janela original nao abre). */
        boolean handle(Player player, GameObject object);
    }

    private static volatile Handler handler;

    private ShiftClickHook() {
    }

    public static void set(Handler h) {
        handler = h;
    }

    public static void clear(Handler h) {
        if (handler == h) {
            handler = null;
        }
    }

    public static boolean handle(Player player, GameObject object) {
        Handler h = handler;
        return h != null && h.handle(player, object);
    }
}
