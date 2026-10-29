/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import org.dom4j.Element;

public class EnchantSkillParser
extends AbstractFileParser<EnchantSkillHolder> {
    private static final EnchantSkillParser xo = new EnchantSkillParser();

    private EnchantSkillParser() {
        super(EnchantSkillHolder.getInstance());
    }

    public static EnchantSkillParser getInstance() {
        return xo;
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/skill_tree/skill_enchant_data.xml");
    }

    @Override
    public String getDTDFileName() {
        return "skill_enchant_data.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator("skill");
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            Iterator<Element> iterator2 = element2.elementIterator("route");
            int n = Integer.parseInt(element2.attributeValue("id"));
            while (iterator2.hasNext()) {
                Element element3 = iterator2.next();
                Iterator<Element> iterator3 = element3.elementIterator("enchant");
                int n2 = Integer.parseInt(element3.attributeValue("id"));
                while (iterator3.hasNext()) {
                    Element element4 = iterator3.next();
                    int n3 = Integer.parseInt(element4.attributeValue("level"));
                    int n4 = Integer.parseInt(element4.attributeValue("skillLvl"));
                    long l = Long.parseLong(element4.attributeValue("exp"));
                    int n5 = Integer.parseInt(element4.attributeValue("sp"));
                    String string = element4.attributeValue("chances");
                    String[] stringArray = string.split("\\s+");
                    int[] nArray = new int[stringArray.length];
                    for (int i = 0; i < stringArray.length; ++i) {
                        nArray[i] = Integer.parseInt(stringArray[i]);
                    }
                    String string2 = element4.attributeValue("neededItemId");
                    int n6 = string2 != null ? Integer.parseInt(string2) : 0;
                    String string3 = element4.attributeValue("neededItemCount");
                    int n7 = string3 != null ? Integer.parseInt(string3) : 0;
                    ((EnchantSkillHolder)this.getHolder()).addEnchantSkill(n, n4, n3, n2, l, n5, nArray, n6, n7);
                }
            }
        }
    }
}

