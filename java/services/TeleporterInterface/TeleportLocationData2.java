package services.TeleporterInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.helpers.XmlReaderHelper;
import l2.gameserver.Config;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ServiceLog;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Locais e categorias do teleporte da interface (data/teleportLocationsinterface.xml).
 * O mesmo arquivo serve o servidor (destino, preco, nobre) e o cliente: {@link #getClientPayloads()}
 * monta a lista que a TeleportWnd recebe, entao uma zona nova nao exige recompilar a interface.
 */
public class TeleportLocationData2 {
    private static final Logger LOG = LoggerFactory.getLogger(TeleportLocationData2.class);
    private static final String FILE = "data/teleportLocationsinterface.xml";
    /** Prefixo reconhecido pelo TutorialViewerWnd. */
    public static final String PACKET_PREFIX = "TELE_ZONES|";
    /** Tamanho maximo do conteudo de cada pacote (o cliente junta as partes). */
    private static final int MAX_CHUNK = 1500;

    /** Categoria de uma aba da janela. */
    public static final class Category {
        public final String type;
        public final int id;
        public final String name;
        public final String icon;
        public final String match;

        Category(String type, int id, String name, String icon, String match) {
            this.type = type;
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.match = match;
        }
    }

    private volatile Map<String, TeleLocation2> _teleports = Collections.emptyMap();
    private volatile List<Category> _categories = Collections.emptyList();
    private volatile List<String> _payloads = Collections.emptyList();
    private volatile int _version;

    protected TeleportLocationData2() {
    }

    /**
     * Carrega na primeira vez que alguem precisa da lista. A carga no boot continua sendo feita pelo
     * reload() do Interface.onLoad; assim o arquivo e lido uma vez so.
     */
    private void ensureLoaded() {
        if (this._version == 0) {
            synchronized (this) {
                if (this._version == 0) {
                    this.load();
                }
            }
        }
    }

    public void reload() {
        this.load();
    }

    protected synchronized void load() {
        File file = new File(Config.DATAPACK_ROOT, FILE);
        if (!file.exists()) {
            LOG.warn("Interface teleport locations: file not found " + file.getPath());
            return;
        }
        Map<String, TeleLocation2> teleports = new LinkedHashMap<>();
        List<Category> categories = new ArrayList<>();
        try {
            SAXReader reader = XmlReaderHelper.createNonValidatingReader();
            Document doc = reader.read(file);
            for (Iterator<Element> it = doc.getRootElement().elementIterator(); it.hasNext(); ) {
                Element element = it.next();
                StatsSet set = new StatsSet();
                for (Iterator<Attribute> attrs = element.attributeIterator(); attrs.hasNext(); ) {
                    Attribute attr = attrs.next();
                    set.set(attr.getName(), attr.getValue());
                }
                if ("teleport".equalsIgnoreCase(element.getName())) {
                    TeleLocation2 loc = new TeleLocation2(set);
                    if (teleports.put(loc.getId(), loc) != null) {
                        LOG.warn("Interface teleport: id " + loc.getId() + " repetido, vale o ultimo.");
                    }
                } else if ("category".equalsIgnoreCase(element.getName())) {
                    categories.add(new Category(set.getString("type", "main"), set.getInteger("id"),
                            set.getString("name", ""), set.getString("icon", "hunt"), set.getString("match", "")));
                }
            }
        } catch (Exception e) {
            LOG.warn("Error loading " + file.getPath() + " (mantida a lista anterior)", e);
            return;
        }
        this._teleports = teleports;
        this._categories = categories;
        this._version++;
        this._payloads = buildPayloads(this._version, categories, teleports);
        ServiceLog.section("TELEPORT")
                .add("Locais: " + teleports.size() + " em " + categories.size() + " categorias ("
                        + this._payloads.size() + " pacotes para o cliente)")
                .print();
    }

    /** Remove os separadores do protocolo do texto. */
    private static String clean(String s) {
        if (s == null || s.isEmpty()) {
            return "-";
        }
        String r = s.replace('~', ' ').replace('#', ' ').replace('|', '/').trim();
        return r.isEmpty() ? "-" : r;
    }

    /**
     * Formato: TELE_ZONES|versao|parte|total|registro#registro#...
     * Categoria: C~type~id~icon~name~match     Zona: Z~id~type~cat~price~noble~x~y~name
     * Nenhum campo fica vazio ("-" no lugar), para o Split do cliente nunca deslocar campos.
     */
    private static List<String> buildPayloads(int version, List<Category> categories, Map<String, TeleLocation2> teleports) {
        List<String> records = new ArrayList<>(categories.size() + teleports.size());
        for (Category c : categories) {
            records.add("C~" + clean(c.type) + "~" + c.id + "~" + clean(c.icon) + "~" + clean(c.name) + "~" + clean(c.match));
        }
        for (TeleLocation2 t : teleports.values()) {
            records.add("Z~" + clean(t.getId()) + "~" + clean(t.getType()) + "~" + t.getCategory() + "~" + t.getPrice() + "~"
                    + (t.isNoble() ? 1 : 0) + "~" + t.getX() + "~" + t.getY() + "~" + clean(t.getName()));
        }
        List<String> bodies = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String r : records) {
            if (sb.length() > 0 && sb.length() + 1 + r.length() > MAX_CHUNK) {
                bodies.add(sb.toString());
                sb.setLength(0);
            }
            if (sb.length() > 0) {
                sb.append('#');
            }
            sb.append(r);
        }
        if (sb.length() > 0 || bodies.isEmpty()) {
            bodies.add(sb.toString());
        }
        List<String> out = new ArrayList<>(bodies.size());
        for (int i = 0; i < bodies.size(); i++) {
            out.add(PACKET_PREFIX + version + "|" + (i + 1) + "|" + bodies.size() + "|" + bodies.get(i));
        }
        return Collections.unmodifiableList(out);
    }

    public TeleLocation2 getTeleportLocation(String id) {
        this.ensureLoaded();
        return this._teleports.get(id);
    }

    public List<Category> getCategories() {
        this.ensureLoaded();
        return this._categories;
    }

    public int size() {
        this.ensureLoaded();
        return this._teleports.size();
    }

    /** Pacotes prontos para enviar ao cliente, na ordem. */
    public List<String> getClientPayloads() {
        this.ensureLoaded();
        return this._payloads;
    }

    public static TeleportLocationData2 getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final TeleportLocationData2 INSTANCE = new TeleportLocationData2();
    }
}