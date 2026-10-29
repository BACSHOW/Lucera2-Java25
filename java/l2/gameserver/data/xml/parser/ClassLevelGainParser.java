/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ClassLevelGainHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.templates.StatsSet;
import org.dom4j.Element;

public class ClassLevelGainParser
extends AbstractDirParser<ClassLevelGainHolder> {
    private static final ClassLevelGainParser xj = new ClassLevelGainParser();

    private ClassLevelGainParser() {
        super(ClassLevelGainHolder.getInstance());
    }

    public static ClassLevelGainParser getInstance() {
        return xj;
    }

    @Override
    public File getXMLDir() {
        return new File(Config.DATAPACK_ROOT, "data/stats/player/gain_level/");
    }

    @Override
    public boolean isIgnored(File file) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "gain_level.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            if (!"gain".equals(element2.getName())) continue;
            Element element3 = element2;
            int n = Integer.parseInt(element3.attributeValue("class_id"));
            Iterator<Element> iterator2 = element3.elementIterator();
            while (iterator2.hasNext()) {
                Element element4 = iterator2.next();
                if (!"level".equals(element4.getName())) continue;
                int n2 = Integer.parseInt(element4.attributeValue("val"));
                StatsSet statsSet = new StatsSet();
                statsSet.put("hp", Double.parseDouble(element4.attributeValue("hp")));
                statsSet.put("cp", Double.parseDouble(element4.attributeValue("cp")));
                statsSet.put("mp", Double.parseDouble(element4.attributeValue("mp")));
                ((ClassLevelGainHolder)this.getHolder()).addLevelGain(ClassId.getClassById(n), n2, statsSet);
            }
        }
    }
}

