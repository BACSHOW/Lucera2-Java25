/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.PromoCodeHolder;
import l2.gameserver.model.PromoCode;
import l2.gameserver.model.promoCode.PromoCodeReward;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class PromoCodeParser
extends AbstractFileParser<PromoCodeHolder> {
    private static final PromoCodeParser xx = new PromoCodeParser();

    public static PromoCodeParser getInstance() {
        return xx;
    }

    public PromoCodeParser() {
        super(PromoCodeHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/promocodes.xml");
    }

    @Override
    public String getDTDFileName() {
        return "promocodes.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        for (Element element2 : element.elements()) {
            String string = element2.attributeValue("name");
            int n = Integer.parseInt(element2.attributeValue("limit", "0"));
            int n2 = Integer.parseInt(element2.attributeValue("minLevel", "0"));
            boolean bl = Boolean.parseBoolean(element2.attributeValue("limitByUser", "false"));
            boolean bl2 = Boolean.parseBoolean(element2.attributeValue("limitByHWID", "false"));
            boolean bl3 = Boolean.parseBoolean(element2.attributeValue("limitByIP", "false"));
            ArrayList<PromoCodeReward> arrayList = new ArrayList<PromoCodeReward>();
            long l = 0L;
            long l2 = 0L;
            for (Element element3 : element2.elements()) {
                Object object;
                Object object2;
                String string2 = element3.getName();
                if (string2.equals("date")) {
                    object2 = element3.attributeValue("from");
                    object = element3.attributeValue("to");
                    if (object2 != null) {
                        l = TimeUtils.parse((String)object2);
                    }
                    if (object == null) continue;
                    l2 = TimeUtils.parse((String)object);
                    continue;
                }
                object2 = Class.forName("l2.gameserver.model.promoCode." + StringUtils.capitalize(string2) + "PromoCodeReward");
                object = ((Class)object2).getConstructor(Element.class);
                arrayList.add((PromoCodeReward)((Constructor)object).newInstance(element3));
            }
            ((PromoCodeHolder)this.getHolder()).addPromoCode(string, new PromoCode(string, n, l, l2, arrayList, bl, bl2, bl3, n2));
        }
    }
}

