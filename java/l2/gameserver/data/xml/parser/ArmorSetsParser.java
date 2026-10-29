/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ArmorSetsHolder;
import l2.gameserver.model.ArmorSet;
import org.dom4j.Element;

public final class ArmorSetsParser
extends AbstractFileParser<ArmorSetsHolder> {
    private static final ArmorSetsParser xf = new ArmorSetsParser();

    public static ArmorSetsParser getInstance() {
        return xf;
    }

    private ArmorSetsParser() {
        super(ArmorSetsHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/armor_sets.xml");
    }

    @Override
    public String getDTDFileName() {
        return "armor_sets.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator("set");
        while (iterator.hasNext()) {
            String[] stringArray = null;
            String[] stringArray2 = null;
            String[] stringArray3 = null;
            String[] stringArray4 = null;
            String[] stringArray5 = null;
            String[] stringArray6 = null;
            String[] stringArray7 = null;
            String[] stringArray8 = null;
            String[] stringArray9 = null;
            String[] stringArray10 = null;
            String[] stringArray11 = null;
            Element element2 = iterator.next();
            int n = Integer.parseInt(element2.attributeValue("id"));
            if (element2.attributeValue("chest") != null) {
                stringArray = element2.attributeValue("chest").split(";");
            }
            if (element2.attributeValue("legs") != null) {
                stringArray2 = element2.attributeValue("legs").split(";");
            }
            if (element2.attributeValue("head") != null) {
                stringArray3 = element2.attributeValue("head").split(";");
            }
            if (element2.attributeValue("gloves") != null) {
                stringArray4 = element2.attributeValue("gloves").split(";");
            }
            if (element2.attributeValue("feet") != null) {
                stringArray5 = element2.attributeValue("feet").split(";");
            }
            if (element2.attributeValue("underware") != null) {
                stringArray6 = element2.attributeValue("underware").split(";");
            }
            if (element2.attributeValue("skills") != null) {
                stringArray7 = element2.attributeValue("skills").split(";");
            }
            if (element2.attributeValue("shield") != null) {
                stringArray8 = element2.attributeValue("shield").split(";");
            }
            if (element2.attributeValue("shield_skills") != null) {
                stringArray9 = element2.attributeValue("shield_skills").split(";");
            }
            if (element2.attributeValue("enchant6skills") != null) {
                stringArray10 = element2.attributeValue("enchant6skills").split(";");
            }
            if (element2.attributeValue("enchant10skills") != null) {
                stringArray11 = element2.attributeValue("enchant10skills").split(";");
            }
            ((ArmorSetsHolder)this.getHolder()).addArmorSet(new ArmorSet(n, stringArray, stringArray2, stringArray3, stringArray4, stringArray5, stringArray6, stringArray7, stringArray8, stringArray9, stringArray10, stringArray11));
        }
    }
}

