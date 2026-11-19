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

    public static void init() {
        ExProperties config = initProperties(CONFIG_FILE);
        ALLOWED_MULTISELLS = config.getProperty("AllowedMultisells", new int[0]);
        _log.info("Allowed Multisells config loaded.");
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
