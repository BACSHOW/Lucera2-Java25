/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.StaticObjectHolder;
import l2.gameserver.templates.StaticObjectTemplate;
import l2.gameserver.templates.StatsSet;
import org.dom4j.Element;

public final class StaticObjectParser
extends AbstractFileParser<StaticObjectHolder> {
    private static StaticObjectParser xE = new StaticObjectParser();

    public static StaticObjectParser getInstance() {
        return xE;
    }

    private StaticObjectParser() {
        super(StaticObjectHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/staticobjects.xml");
    }

    @Override
    public String getDTDFileName() {
        return "staticobjects.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            StatsSet statsSet = new StatsSet();
            statsSet.set("uid", element2.attributeValue("id"));
            statsSet.set("stype", element2.attributeValue("stype"));
            statsSet.set("path", element2.attributeValue("path"));
            statsSet.set("map_x", element2.attributeValue("map_x"));
            statsSet.set("map_y", element2.attributeValue("map_y"));
            statsSet.set("name", element2.attributeValue("name"));
            statsSet.set("x", element2.attributeValue("x"));
            statsSet.set("y", element2.attributeValue("y"));
            statsSet.set("z", element2.attributeValue("z"));
            statsSet.set("spawn", element2.attributeValue("spawn"));
            ((StaticObjectHolder)this.getHolder()).addTemplate(new StaticObjectTemplate(statsSet));
        }
    }
}

