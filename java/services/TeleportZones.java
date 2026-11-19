package services;

import l2.commons.listener.Listener;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import services.TeleporterInterface.TeleportLocationData2;

/**
 * Envia a lista de teleporte (data/teleportLocationsinterface.xml) para a TeleportWnd.
 *  - ao entrar no jogo;
 *  - scripts_services.TeleportZones:list    o cliente pede (janela aberta sem lista);
 *  - scripts_services.TeleportZones:reload  GM recarrega o XML e reenvia a todos online.
 */
public class TeleportZones extends Functions implements ScriptFile {
    private static final long ENTER_DELAY_MS = 1500L;
    private static final EnterListener ENTER_LISTENER = new EnterListener();

    public static void sendTo(Player player) {
        if (player == null || !player.isConnected()) {
            return;
        }
        for (String payload : TeleportLocationData2.getInstance().getClientPayloads()) {
            player.sendPacket(new TutorialShowHtml(payload));
        }
    }

    /** Reenvia a lista atual a todos os jogadores conectados. Retorna quantos receberam. */
    public static int sendToAll() {
        int sent = 0;
        for (Player p : GameObjectsStorage.getAllPlayersForIterate()) {
            if (p.isConnected()) {
                sendTo(p);
                sent++;
            }
        }
        return sent;
    }

    public void list() {
        sendTo(this.getSelf());
    }

    public void reload() {
        Player gm = this.getSelf();
        if (gm == null || !gm.isGM()) {
            return;
        }
        TeleportLocationData2.getInstance().reload();
        int sent = sendToAll();
        gm.sendMessage("Teleporte: " + TeleportLocationData2.getInstance().size() + " zonas recarregadas, enviadas a " + sent + " jogadores.");
    }

    @Override
    public void onLoad() {
        // A carga do XML fica com o Interface.onLoad (reload); aqui so registra o envio no login.
        CharListenerList.addGlobal(asCreatureListener(ENTER_LISTENER));
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
        CharListenerList.removeGlobal(asCreatureListener(ENTER_LISTENER));
    }

    @SuppressWarnings("unchecked")
    private static Listener<Creature> asCreatureListener(Object listener) {
        return (Listener<Creature>) listener;
    }

    private static final class EnterListener implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(final Player player) {
            ThreadPoolManager.getInstance().schedule(() -> sendTo(player), ENTER_DELAY_MS);
        }
    }
}