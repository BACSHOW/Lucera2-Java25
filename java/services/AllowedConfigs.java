package services;

import java.io.File;
import java.io.IOException;
import l2.commons.configuration.ExProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllowedConfigs {
    protected static final Logger _log = LoggerFactory.getLogger(AllowedConfigs.class.getName());
    private static final String CONFIG_FILE = "config/custom/l2jreturn.properties";
    public static int[] ALLOWED_MULTISELLS = new int[0];

    /** Lê o AllowedMultisells; devolve quantos multisells ficaram liberados (vai para o bloco INTERFACE do console). */
    public static int init() {
        ExProperties config = initProperties(CONFIG_FILE);
        ALLOWED_MULTISELLS = config.getProperty("AllowedMultisells", new int[0]);
        return ALLOWED_MULTISELLS.length;
    }

    public static ExProperties initProperties(String filename) {
        ExProperties result = new ExProperties();

        try {
            result.load(new File(filename));
        } catch (IOException e) {
            _log.warn("Allowed Multisells: Error loading \"" + filename + "\" config.");
        }

        return result;
    }
}
