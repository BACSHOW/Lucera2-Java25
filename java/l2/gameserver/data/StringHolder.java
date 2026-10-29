/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Language;

public final class StringHolder
extends AbstractHolder {
    private static final StringHolder vs = new StringHolder();
    private Map<Language, Map<String, String>> vt = new HashMap<Language, Map<String, String>>();

    public static StringHolder getInstance() {
        return vs;
    }

    private StringHolder() {
    }

    public String getNullable(Player player, String string) {
        Language language = player == null ? Language.ENGLISH : player.getLanguage();
        return this.a(language, string);
    }

    public String getNotNull(Player player, String string) {
        Language language = player == null ? Language.ENGLISH : player.getLanguage();
        String string2 = this.a(language, string);
        if (string2 == null && player != null) {
            string2 = "Not find string: " + string + "; for lang: " + (Object)((Object)language);
            this.vt.get((Object)language).put(string, string2);
        }
        return string2;
    }

    public String getNotNull(Language language, String string) {
        String string2 = this.a(language, string);
        if (string2 == null && language != null) {
            string2 = "Not find string: " + string + "; for lang: " + (Object)((Object)language);
            this.vt.get((Object)language).put(string, string2);
        }
        return string2;
    }

    private String a(Language language, String string) {
        Map<String, String> map = this.vt.get((Object)language);
        return map.get(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        for (Language language : Language.VALUES) {
            this.vt.put(language, new HashMap());
            File file = new File(Config.DATAPACK_ROOT, "data/string/strings_" + language.getShortName() + ".properties");
            if (!file.exists()) {
                this.warn("Not find file: " + file.getAbsolutePath());
                continue;
            }
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new LineNumberReader(new FileReader(file));
                String string = null;
                while ((string = ((LineNumberReader)bufferedReader).readLine()) != null) {
                    if (string.startsWith("#")) continue;
                    StringTokenizer stringTokenizer = new StringTokenizer(string, "=");
                    if (stringTokenizer.countTokens() < 2) {
                        this.error("Error on line: " + string + "; file: " + file.getName());
                        continue;
                    }
                    String string2 = stringTokenizer.nextToken();
                    String string3 = stringTokenizer.nextToken();
                    while (stringTokenizer.hasMoreTokens()) {
                        string3 = string3 + "=" + stringTokenizer.nextToken();
                    }
                    Map<String, String> map = this.vt.get((Object)language);
                    map.put(string2, string3);
                }
            }
            catch (Exception exception) {
                this.error("Exception: " + exception, exception);
            }
            finally {
                try {
                    bufferedReader.close();
                }
                catch (Exception exception) {}
            }
        }
        this.log();
    }

    public void reload() {
        this.clear();
        this.load();
    }

    @Override
    public void log() {
        for (Map.Entry<Language, Map<String, String>> entry : this.vt.entrySet()) {
            this.info("load strings: " + entry.getValue().size() + " for lang: " + (Object)((Object)entry.getKey()));
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        this.vt.clear();
    }
}

