/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.versioning;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.StringCharacterIterator;
import java.util.Locale;

public final class Locator {
    private Locator() {
    }

    public static File getClassSource(Class<?> clazz) {
        String string = clazz.getName().replace('.', '/') + ".class";
        return Locator.getResourceSource(clazz.getClassLoader(), string);
    }

    public static File getResourceSource(ClassLoader classLoader, String string) {
        if (classLoader == null) {
            classLoader = Locator.class.getClassLoader();
        }
        URL uRL = null;
        uRL = classLoader == null ? ClassLoader.getSystemResource(string) : classLoader.getResource(string);
        if (uRL != null) {
            String string2 = uRL.toString();
            if (string2.startsWith("jar:file:")) {
                int n = string2.indexOf("!");
                String string3 = string2.substring(4, n);
                return new File(Locator.fromURI(string3));
            }
            if (string2.startsWith("file:")) {
                int n = string2.indexOf(string);
                String string4 = string2.substring(0, n);
                return new File(Locator.fromURI(string4));
            }
        }
        return null;
    }

    public static String fromURI(String string) {
        String string2;
        int n;
        URL uRL = null;
        try {
            uRL = new URL(string);
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        if (uRL == null || !"file".equals(uRL.getProtocol())) {
            throw new IllegalArgumentException("Can only handle valid file: URIs");
        }
        StringBuilder stringBuilder = new StringBuilder(uRL.getHost());
        if (stringBuilder.length() > 0) {
            stringBuilder.insert(0, File.separatorChar).insert(0, File.separatorChar);
        }
        stringBuilder.append((n = (string2 = uRL.getFile()).indexOf(63)) < 0 ? string2 : string2.substring(0, n));
        string = stringBuilder.toString().replace('/', File.separatorChar);
        if (File.pathSeparatorChar == ';' && string.startsWith("\\") && string.length() > 2 && Character.isLetter(string.charAt(1)) && string.lastIndexOf(58) > -1) {
            string = string.substring(1);
        }
        String string3 = Locator.h(string);
        return string3;
    }

    private static String h(String string) {
        if (string.indexOf(37) == -1) {
            return string;
        }
        StringBuilder stringBuilder = new StringBuilder();
        StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(string);
        char c = stringCharacterIterator.first();
        while (c != '\uffff') {
            if (c == '%') {
                char c2 = stringCharacterIterator.next();
                if (c2 != '\uffff') {
                    int n = Character.digit(c2, 16);
                    char c3 = stringCharacterIterator.next();
                    if (c3 != '\uffff') {
                        int n2 = Character.digit(c3, 16);
                        stringBuilder.append((char)((n << 4) + n2));
                    }
                }
            } else {
                stringBuilder.append(c);
            }
            c = stringCharacterIterator.next();
        }
        String string2 = stringBuilder.toString();
        return string2;
    }

    public static File getToolsJar() {
        File file;
        boolean bl = false;
        try {
            Class.forName("com.sun.tools.javac.Main");
            bl = true;
        }
        catch (Exception exception) {
            try {
                Class.forName("sun.tools.javac.Main");
                bl = true;
            }
            catch (Exception exception2) {
                // empty catch block
            }
        }
        if (bl) {
            return null;
        }
        String string = System.getProperty("java.home");
        if (string.toLowerCase(Locale.US).endsWith("jre")) {
            string = string.substring(0, string.length() - 4);
        }
        if (!(file = new File(string + "/lib/tools.jar")).exists()) {
            System.out.println("Unable to locate tools.jar. Expected to find it in " + file.getPath());
            return null;
        }
        return file;
    }

    public static URL[] getLocationURLs(File file) throws MalformedURLException {
        return Locator.getLocationURLs(file, new String[]{".jar"});
    }

    public static URL[] getLocationURLs(File file, final String[] stringArray) throws MalformedURLException {
        URL[] uRLArray = new URL[]{};
        if (!file.exists()) {
            return uRLArray;
        }
        if (!file.isDirectory()) {
            uRLArray = new URL[1];
            String string = file.getPath();
            for (int i = 0; i < stringArray.length; ++i) {
                if (!string.toLowerCase().endsWith(stringArray[i])) continue;
                uRLArray[0] = file.toURI().toURL();
                break;
            }
            return uRLArray;
        }
        File[] fileArray = file.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String string) {
                for (int i = 0; i < stringArray.length; ++i) {
                    if (!string.toLowerCase().endsWith(stringArray[i])) continue;
                    return true;
                }
                return false;
            }
        });
        uRLArray = new URL[fileArray.length];
        for (int i = 0; i < fileArray.length; ++i) {
            uRLArray[i] = fileArray[i].toURI().toURL();
        }
        return uRLArray;
    }
}

