/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.IOUtils;

public class ExProperties
extends Properties {
    private static final long serialVersionUID = 1L;
    public static final String defaultDelimiter = "[\\s,;]+";

    public void load(String string) throws IOException {
        this.load(new File(string));
    }

    public void load(File file) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            this.load(fileInputStream);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(fileInputStream);
            throw throwable;
        }
        IOUtils.closeQuietly(fileInputStream);
    }

    public static boolean parseBoolean(String string) {
        switch (string.length()) {
            case 1: {
                char c = string.charAt(0);
                if (c == 'y' || c == 'Y' || c == '1') {
                    return true;
                }
                if (c != 'n' && c != 'N' && c != '0') break;
                return false;
            }
            case 2: {
                char c = string.charAt(0);
                char c2 = string.charAt(1);
                if (!(c != 'o' && c != 'O' || c2 != 'n' && c2 != 'N')) {
                    return true;
                }
                if (c != 'n' && c != 'N' || c2 != 'o' && c2 != 'O') break;
                return false;
            }
            case 3: {
                char c = string.charAt(0);
                char c3 = string.charAt(1);
                char c4 = string.charAt(2);
                if (!(c != 'y' && c != 'Y' || c3 != 'e' && c3 != 'E' || c4 != 's' && c4 != 'S')) {
                    return true;
                }
                if (c != 'o' && c != 'O' || c3 != 'f' && c3 != 'F' || c4 != 'f' && c4 != 'F') break;
                return false;
            }
            case 4: {
                char c = string.charAt(0);
                char c5 = string.charAt(1);
                char c6 = string.charAt(2);
                char c7 = string.charAt(3);
                if (c != 't' && c != 'T' || c5 != 'r' && c5 != 'R' || c6 != 'u' && c6 != 'U' || c7 != 'e' && c7 != 'E') break;
                return true;
            }
            case 5: {
                char c = string.charAt(0);
                char c8 = string.charAt(1);
                char c9 = string.charAt(2);
                char c10 = string.charAt(3);
                char c11 = string.charAt(4);
                if (c != 'f' && c != 'F' || c8 != 'a' && c8 != 'A' || c9 != 'l' && c9 != 'L' || c10 != 's' && c10 != 'S' || c11 != 'e' && c11 != 'E') break;
                return false;
            }
        }
        throw new IllegalArgumentException("For input string: \"" + string + "\"");
    }

    public boolean getProperty(String string, boolean bl) {
        boolean bl2 = bl;
        String string2 = super.getProperty(string, null);
        if (string2 != null) {
            bl2 = ExProperties.parseBoolean(string2);
        }
        return bl2;
    }

    public int getProperty(String string, int n) {
        int n2 = n;
        String string2 = super.getProperty(string, null);
        if (string2 != null) {
            n2 = Integer.parseInt(string2);
        }
        return n2;
    }

    public long getProperty(String string, long l) {
        long l2 = l;
        String string2 = super.getProperty(string, null);
        if (string2 != null) {
            l2 = Long.parseLong(string2);
        }
        return l2;
    }

    public double getProperty(String string, double d) {
        double d2 = d;
        String string2 = super.getProperty(string, null);
        if (string2 != null) {
            d2 = Double.parseDouble(string2);
        }
        return d2;
    }

    public String[] getProperty(String string, String[] stringArray) {
        return this.getProperty(string, stringArray, defaultDelimiter);
    }

    public String[] getProperty(String string, String[] stringArray, String string2) {
        String[] stringArray2 = stringArray;
        String string3 = super.getProperty(string, null);
        if (string3 != null) {
            stringArray2 = string3.split(string2);
        }
        return stringArray2;
    }

    public boolean[] getProperty(String string, boolean[] blArray) {
        return this.getProperty(string, blArray, defaultDelimiter);
    }

    public boolean[] getProperty(String string, boolean[] blArray, String string2) {
        boolean[] blArray2 = blArray;
        String string3 = super.getProperty(string, null);
        if (string3 != null) {
            String[] stringArray = string3.split(string2);
            blArray2 = new boolean[stringArray.length];
            for (int i = 0; i < blArray2.length; ++i) {
                blArray2[i] = ExProperties.parseBoolean(stringArray[i]);
            }
        }
        return blArray2;
    }

    public int[] getProperty(String string, int[] nArray) {
        return this.getProperty(string, nArray, defaultDelimiter);
    }

    public int[] getProperty(String string, int[] nArray, String string2) {
        int[] nArray2 = nArray;
        String string3 = super.getProperty(string, null);
        if (string3 != null) {
            if (!string3.isEmpty()) {
                String[] stringArray = string3.split(string2);
                nArray2 = new int[stringArray.length];
                for (int i = 0; i < nArray2.length; ++i) {
                    nArray2[i] = Integer.parseInt(stringArray[i]);
                }
            } else {
                nArray2 = new int[]{};
            }
        }
        return nArray2;
    }

    public long[] getProperty(String string, long[] lArray) {
        return this.getProperty(string, lArray, defaultDelimiter);
    }

    public long[] getProperty(String string, long[] lArray, String string2) {
        long[] lArray2 = lArray;
        String string3 = super.getProperty(string, null);
        if (string3 != null) {
            if (!string3.isEmpty()) {
                String[] stringArray = string3.split(string2);
                lArray2 = new long[stringArray.length];
                for (int i = 0; i < lArray2.length; ++i) {
                    lArray2[i] = Long.parseLong(stringArray[i]);
                }
            } else {
                lArray2 = new long[]{};
            }
        }
        return lArray2;
    }

    public double[] getProperty(String string, double[] dArray) {
        return this.getProperty(string, dArray, defaultDelimiter);
    }

    public double[] getProperty(String string, double[] dArray, String string2) {
        double[] dArray2 = dArray;
        String string3 = super.getProperty(string, null);
        if (string3 != null) {
            String[] stringArray = string3.split(string2);
            dArray2 = new double[stringArray.length];
            for (int i = 0; i < dArray2.length; ++i) {
                dArray2[i] = Double.parseDouble(stringArray[i]);
            }
        }
        return dArray2;
    }

    public float[] getProperty(String string, float[] fArray) {
        return this.getProperty(string, fArray, defaultDelimiter);
    }

    public float[] getProperty(String string, float[] fArray, String string2) {
        float[] fArray2 = fArray;
        String string3 = super.getProperty(string, null);
        if (string3 != null) {
            if (!string3.isEmpty()) {
                String[] stringArray = string3.split(string2);
                fArray2 = new float[stringArray.length];
                for (int i = 0; i < fArray2.length; ++i) {
                    fArray2[i] = Float.parseFloat(stringArray[i]);
                }
            } else {
                fArray2 = new float[]{};
            }
        }
        return fArray2;
    }
}

