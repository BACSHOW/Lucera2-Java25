/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.HitCondBonusHolder;
import l2.gameserver.model.base.HitCondBonusType;
import org.dom4j.Element;

public final class HitCondBonusParser
extends AbstractFileParser<HitCondBonusHolder> {
    private static final HitCondBonusParser xr = new HitCondBonusParser();

    public static HitCondBonusParser getInstance() {
        return xr;
    }

    private HitCondBonusParser() {
        super(HitCondBonusHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/stats/player/hit_cond_bonus.xml");
    }

    @Override
    public String getDTDFileName() {
        return "hit_cond_bonus.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            HitCondBonusType hitCondBonusType = HitCondBonusType.valueOf(element2.attributeValue("type"));
            double d = Double.parseDouble(element2.attributeValue("value"));
            ((HitCondBonusHolder)this.getHolder()).addHitCondBonus(hitCondBonusType, d);
        }
    }
}

