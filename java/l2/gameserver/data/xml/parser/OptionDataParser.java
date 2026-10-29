/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.data.xml.parser.StatParser;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.OptionDataTemplate;
import org.dom4j.Element;

public final class OptionDataParser
extends StatParser<OptionDataHolder> {
    private static final OptionDataParser xw = new OptionDataParser();

    public static OptionDataParser getInstance() {
        return xw;
    }

    protected OptionDataParser() {
        super(OptionDataHolder.getInstance());
    }

    @Override
    public File getXMLDir() {
        return new File(Config.DATAPACK_ROOT, "data/optiondata");
    }

    @Override
    public boolean isIgnored(File file) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "optiondata.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            OptionDataTemplate optionDataTemplate = new OptionDataTemplate(Integer.parseInt(element2.attributeValue("id")));
            Iterator<Element> iterator2 = element2.elementIterator();
            while (iterator2.hasNext()) {
                Object object;
                int n;
                int n2;
                Element element3 = iterator2.next();
                String string = element3.getName();
                if (string.equalsIgnoreCase("for")) {
                    this.parseFor(element3, optionDataTemplate);
                    continue;
                }
                if (string.equalsIgnoreCase("trigger")) {
                    n2 = this.parseNumber(element3.attributeValue("id")).intValue();
                    n = this.parseNumber(element3.attributeValue("level")).intValue();
                    object = TriggerType.valueOf(element3.attributeValue("type"));
                    double d = this.parseNumber(element3.attributeValue("chance")).doubleValue();
                    TriggerInfo triggerInfo = new TriggerInfo(n2, n, (TriggerType)((Object)object), d);
                    optionDataTemplate.addTrigger(triggerInfo);
                    continue;
                }
                if (!string.equalsIgnoreCase("skill")) continue;
                n2 = Integer.parseInt(element3.attributeValue("id"));
                n = Integer.parseInt(element3.attributeValue("level"));
                object = SkillTable.getInstance().getInfo(n2, n);
                if (object != null) {
                    optionDataTemplate.addSkill((Skill)object);
                    continue;
                }
                this.info("Skill not found(" + n2 + "," + n + ") for option data:" + optionDataTemplate.getId() + "; file:" + this.getCurrentFileName());
            }
            ((OptionDataHolder)this.getHolder()).addTemplate(optionDataTemplate);
        }
    }

    @Override
    protected Object getTableValue(String string) {
        return null;
    }
}

