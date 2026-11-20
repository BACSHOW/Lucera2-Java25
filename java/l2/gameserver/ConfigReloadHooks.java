package l2.gameserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configs dos scripts (servicos) que recarregam junto com as do nucleo: //reload_config ("Reload All Configs")
 * e o "Salvar e recarregar" da janela do servidor chamam {@link Config#load()}, que no fim roda estes ganchos.
 *
 * <p>Cada servico registra no onLoad, por nome (registrar de novo com o mesmo nome substitui o anterior, entao
 * recarregar o script nao duplica).</p>
 */
public final class ConfigReloadHooks {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigReloadHooks.class);
    private static final Map<String, Runnable> HOOKS = new ConcurrentHashMap<>();

    private ConfigReloadHooks() {
    }

    public static void register(String name, Runnable reload) {
        HOOKS.put(name, reload);
    }

    public static void unregister(String name) {
        HOOKS.remove(name);
    }

    static void runAll() {
        for (Map.Entry<String, Runnable> e : HOOKS.entrySet()) {
            try {
                e.getValue().run();
                LOG.info("Config recarregada: " + e.getKey());
            } catch (RuntimeException ex) {
                LOG.warn("Config: erro ao recarregar " + e.getKey(), ex);
            }
        }
    }
}
