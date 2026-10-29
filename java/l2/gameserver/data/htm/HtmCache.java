/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.htm;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.utils.Language;
import l2.gameserver.utils.Strings;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmCache {
    public static final int DISABLED = 0;
    public static final int LAZY = 1;
    public static final int ENABLED = 2;
    private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);
    private static final HtmCache vu = new HtmCache();
    private Cache[] vv = new Cache[Language.VALUES.length];

    public static HtmCache getInstance() {
        return vu;
    }

    private HtmCache() {
        CacheManager manager = CacheManager.getInstance();
        for (int i = 0; i < this.vv.length; ++i) {
            String cacheName = this.getClass().getName() + "." + Language.VALUES[i].name();
            Cache cache = manager.getCache(cacheName);
            if (cache == null) {
                manager.addCacheIfAbsent(cacheName);
                cache = manager.getCache(cacheName);
            }
            this.vv[i] = cache;
        }
    }

    public void reload() {
        this.clear();
        switch (Config.HTM_CACHE_MODE) {
            case 2: {
                for (Language language : Language.VALUES) {
                    File file = new File(Config.DATAPACK_ROOT, "data/html-" + language.getShortName());
                    if (!file.exists()) {
                        _log.info("HtmCache: Not find html dir for lang: " + (Object)((Object)language));
                        continue;
                    }
                    this.a(language, file, file.getAbsolutePath() + "/");
                }
                for (int i = 0; i < this.vv.length; ++i) {
                    Cache cache = this.vv[i];
                    _log.info(String.format("HtmCache: parsing %d documents; lang: %s.", new Object[]{cache.getSize(), Language.VALUES[i]}));
                }
                break;
            }
            case 1: {
                _log.info("HtmCache: lazy cache mode.");
                break;
            }
            case 0: {
                _log.info("HtmCache: disabled.");
            }
        }
    }

    private void a(Language language, File file, String string) {
        File[] fileArray;
        if (!file.exists()) {
            _log.info("HtmCache: dir not exists: " + file);
            return;
        }
        for (File file2 : fileArray = file.listFiles()) {
            if (file2.isDirectory()) {
                this.a(language, file2, string);
                continue;
            }
            if (!file2.getName().endsWith(".htm")) continue;
            try {
                this.putContent(language, file2, string);
            }
            catch (IOException iOException) {
                _log.info("HtmCache: file error" + iOException, iOException);
            }
        }
    }

    public void putContent(Language language, File file, String string) throws IOException {
        String string2 = FileUtils.readFileToString(file, "UTF-8");
        String string3 = file.getAbsolutePath().substring(string.length()).replace("\\", "/");
        this.vv[language.ordinal()].put(new Element((Serializable)((Object)string3.toLowerCase()), (Serializable)((Object)Strings.bbParse(string2))));
    }

    public String getNotNull(String string, Player player) {
        Language language = player == null ? Language.ENGLISH : player.getLanguage();
        String string2 = this.a(string, language);
        if (StringUtils.isEmpty(string2)) {
            string2 = "Dialog not found: " + string + "; Lang: " + (Object)((Object)language);
        }
        return string2;
    }

    public String getNullable(String string, Player player) {
        Language language = player == null ? Language.ENGLISH : player.getLanguage();
        String string2 = this.a(string, language);
        if (StringUtils.isEmpty(string2)) {
            return null;
        }
        return string2;
    }

    private String a(String string, Language language) {
        if (string == null) {
            return null;
        }
        String string2 = string.toLowerCase();
        String string3 = this.a(language, string2);
        if (string3 == null) {
            switch (Config.HTM_CACHE_MODE) {
                case 2: {
                    break;
                }
                case 1: {
                    string3 = this.c(language, string);
                    if (string3 != null || language == Language.ENGLISH) break;
                    string3 = this.c(Language.ENGLISH, string);
                    break;
                }
                case 0: {
                    string3 = this.b(language, string);
                    if (string3 != null || language == Language.ENGLISH) break;
                    string3 = this.b(Language.ENGLISH, string);
                }
            }
        }
        return string3;
    }

    private String b(Language language, String string) {
        String string2 = null;
        File file = new File(Config.DATAPACK_ROOT, "data/html-" + language.getShortName() + "/" + string);
        if (file.exists()) {
            try {
                string2 = FileUtils.readFileToString(file, "UTF-8");
                string2 = Strings.bbParse(string2);
            }
            catch (IOException iOException) {
                _log.info("HtmCache: File error: " + string + " lang: " + (Object)((Object)language));
            }
        }
        return string2;
    }

    private String c(Language language, String string) {
        String string2 = null;
        File file = new File(Config.DATAPACK_ROOT, "data/html-" + language.getShortName() + "/" + string);
        if (file.exists()) {
            try {
                string2 = FileUtils.readFileToString(file, "UTF-8");
                string2 = Strings.bbParse(string2);
                this.vv[language.ordinal()].put(new Element((Serializable)((Object)string), (Serializable)((Object)string2)));
            }
            catch (IOException iOException) {
                _log.info("HtmCache: File error: " + string + " lang: " + (Object)((Object)language));
            }
        }
        return string2;
    }

    private String a(Language language, String string) {
        Element element = this.vv[language.ordinal()].get((Serializable)((Object)string));
        if (element == null) {
            element = this.vv[Language.ENGLISH.ordinal()].get((Serializable)((Object)string));
        }
        return element == null ? null : (String)element.getObjectValue();
    }

    public void clear() {
        for (int i = 0; i < this.vv.length; ++i) {
            if (this.vv[i] != null) {
                this.vv[i].removeAll();
            }
        }
    }
}

