/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver;

import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.database.L2DatabaseFactory;

public class AccountManager {
    private static final void printUsage() {
        System.out.println("Usage: ");
        System.out.println(" -c <name> <password>\t Creates account <name> with password <password>");
        System.out.println(" -p <name> <password>\t Update <name>'s password to <password>");
    }

    public static void main(String ... stringArray) throws Throwable {
        Config.load();
        L2DatabaseFactory.getInstance().getConnection().close();
        Config.initCrypt();
        if (stringArray.length > 2 && "-c".equalsIgnoreCase(stringArray[0])) {
            String string = stringArray[1].trim();
            String string2 = stringArray[2].trim();
            System.out.println("Creating account \"" + string + "\" with password \"" + string2 + "\"");
            Account account = new Account(string);
            account.restore();
            if (account.getPasswordHash() != null) {
                System.err.println("Account \"" + string + "\" already exists");
                System.exit(-1);
                return;
            }
            account.setPasswordHash(Config.DEFAULT_CRYPT.encrypt(string2));
            account.setAccessLevel(0);
            account.save();
            System.out.println("Account \"" + account.getLogin() + "\" created.");
            System.exit(0);
        } else if (stringArray.length > 2 && "-p".equalsIgnoreCase(stringArray[0])) {
            String string = stringArray[1].trim();
            String string3 = stringArray[2].trim();
            System.out.println("Set account \"" + string + "\" password to \"" + string3 + "\"");
            Account account = new Account(string);
            account.restore();
            if (account.getPasswordHash() == null) {
                System.err.println("Account \"" + string + "\" dose not exists");
                System.exit(-1);
                return;
            }
            account.setPasswordHash(Config.DEFAULT_CRYPT.encrypt(string3));
            account.setAccessLevel(0);
            account.update();
            System.out.println("Account \"" + account.getLogin() + "\" password set to \"" + string3 + "\".");
            System.exit(0);
        } else {
            AccountManager.printUsage();
            System.exit(0);
        }
    }
}

