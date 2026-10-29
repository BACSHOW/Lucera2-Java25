/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.crypt;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHash {
    private static final Logger _log = LoggerFactory.getLogger(PasswordHash.class);
    private final String name;

    public PasswordHash(String string) {
        this.name = string;
    }

    public boolean compare(String string, String string2) {
        try {
            return this.encrypt(string).equalsIgnoreCase(string2);
        }
        catch (Exception exception) {
            _log.error(this.name + ": encryption error!", exception);
            return false;
        }
    }

    public String encrypt(String string) throws Exception {
        AbstractChecksum abstractChecksum = JacksumAPI.getChecksumInstance(this.name);
        abstractChecksum.setEncoding("BASE64");
        abstractChecksum.update(string.getBytes());
        return abstractChecksum.format("#CHECKSUM");
    }
}

