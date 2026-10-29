/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SoulCrystalHolder;
import l2.gameserver.templates.SoulCrystal;
import org.dom4j.Element;

public final class SoulCrystalParser
extends AbstractFileParser<SoulCrystalHolder> {
    private static final SoulCrystalParser xB = new SoulCrystalParser();

    public static SoulCrystalParser getInstance() {
        return xB;
    }

    private SoulCrystalParser() {
        super(SoulCrystalHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/soul_crystals.xml");
    }

    @Override
    public String getDTDFileName() {
        return "soul_crystals.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator("crystal");
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            int n = Integer.parseInt(element2.attributeValue("item_id"));
            int n2 = Integer.parseInt(element2.attributeValue("level"));
            int n3 = Integer.parseInt(element2.attributeValue("next_item_id"));
            int n4 = element2.attributeValue("cursed_next_item_id") == null ? 0 : Integer.parseInt(element2.attributeValue("cursed_next_item_id"));
            ((SoulCrystalHolder)this.getHolder()).addCrystal(new SoulCrystal(n, n2, n3, n4));
        }
    }
}

