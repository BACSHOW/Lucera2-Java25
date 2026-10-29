/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Files {
    private static final Logger _log = LoggerFactory.getLogger(Files.class);

    public static void writeFile(String string, String string2) {
        try {
            FileUtils.writeStringToFile(new File(string), string2, "UTF-8");
        }
        catch (IOException iOException) {
            _log.error("Error while saving file : " + string, iOException);
        }
    }

    public static boolean copyFile(String string, String string2) {
        try {
            FileUtils.copyFile(new File(string), new File(string2), false);
            return true;
        }
        catch (IOException iOException) {
            _log.error("Error while copying file : " + string + " to " + string2, iOException);
            return false;
        }
    }
}

