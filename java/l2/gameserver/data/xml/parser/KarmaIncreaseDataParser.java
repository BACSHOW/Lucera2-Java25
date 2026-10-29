/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.KarmaIncreaseDataHolder;
import org.dom4j.Element;

public final class KarmaIncreaseDataParser
extends AbstractFileParser<KarmaIncreaseDataHolder> {
    private static final KarmaIncreaseDataParser xu = new KarmaIncreaseDataParser();

    public static KarmaIncreaseDataParser getInstance() {
        return xu;
    }

    private KarmaIncreaseDataParser() {
        super(KarmaIncreaseDataHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/stats/player/karma_increase.xml");
    }

    @Override
    public String getDTDFileName() {
        return "karma_increase.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            int n = Integer.parseInt(element2.attributeValue("level"));
            double d = Double.parseDouble(element2.attributeValue("value"));
            ((KarmaIncreaseDataHolder)this.getHolder()).addData(n, d);
        }
    }
}

