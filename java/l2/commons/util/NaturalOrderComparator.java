/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util;

import java.io.File;
import java.util.Comparator;

public class NaturalOrderComparator
implements Comparator {
    public static final Comparator<String> STRING_COMPARATOR = new Comparator<String>(){

        @Override
        public int compare(String string, String string2) {
            return NaturalOrderComparator.d(string, string2);
        }
    };
    public static final Comparator<File> FILE_NAME_COMPARATOR = new Comparator<File>(){

        @Override
        public int compare(File file, File file2) {
            return NaturalOrderComparator.d(file.getName(), file2.getName());
        }
    };
    public static final Comparator<File> ABSOLUTE_FILE_NAME_COMPARATOR = new Comparator<File>(){

        @Override
        public int compare(File file, File file2) {
            return NaturalOrderComparator.d(file.getAbsolutePath(), file2.getAbsolutePath());
        }
    };

    private static int c(String string, String string2) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (true) {
            char c = NaturalOrderComparator.d(string, n2);
            char c2 = NaturalOrderComparator.d(string2, n3);
            if (!Character.isDigit(c) && !Character.isDigit(c2)) {
                return n;
            }
            if (!Character.isDigit(c)) {
                return -1;
            }
            if (!Character.isDigit(c2)) {
                return 1;
            }
            if (c < c2) {
                if (n == 0) {
                    n = -1;
                }
            } else if (c > c2) {
                if (n == 0) {
                    n = 1;
                }
            } else if (c == '\u0000' && c2 == '\u0000') {
                return n;
            }
            ++n2;
            ++n3;
        }
    }

    public int compare(Object object, Object object2) {
        String string = object.toString();
        String string2 = object2.toString();
        return NaturalOrderComparator.d(string, string2);
    }

    private static int d(String string, String string2) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while (true) {
            int n5;
            n4 = 0;
            n3 = 0;
            char c = NaturalOrderComparator.d(string, n);
            char c2 = NaturalOrderComparator.d(string2, n2);
            while (Character.isSpaceChar(c) || c == '0') {
                n3 = c == '0' ? ++n3 : 0;
                c = NaturalOrderComparator.d(string, ++n);
            }
            while (Character.isSpaceChar(c2) || c2 == '0') {
                n4 = c2 == '0' ? ++n4 : 0;
                c2 = NaturalOrderComparator.d(string2, ++n2);
            }
            if (Character.isDigit(c) && Character.isDigit(c2) && (n5 = NaturalOrderComparator.c(string.substring(n), string2.substring(n2))) != 0) {
                return n5;
            }
            if (c == '\u0000' && c2 == '\u0000') {
                return n3 - n4;
            }
            if (c < c2) {
                return -1;
            }
            if (c > c2) {
                return 1;
            }
            ++n;
            ++n2;
        }
    }

    private static char d(String string, int n) {
        if (n >= string.length()) {
            return '\u0000';
        }
        return string.charAt(n);
    }
}

