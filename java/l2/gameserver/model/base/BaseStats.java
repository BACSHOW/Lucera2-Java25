/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public enum BaseStats {
    STR{

        @Override
        public final int getStat(Creature creature) {
            return creature == null ? 1 : creature.getSTR();
        }

        @Override
        public final double calcBonus(Creature creature) {
            return creature == null ? 1.0 : NT[creature.getSTR()];
        }

        @Override
        public final double calcChanceMod(Creature creature) {
            return Math.min(2.0 - Math.sqrt(this.calcBonus(creature)), 1.0);
        }
    }
    ,
    INT{

        @Override
        public final int getStat(Creature creature) {
            return creature == null ? 1 : creature.getINT();
        }

        @Override
        public final double calcBonus(Creature creature) {
            return creature == null ? 1.0 : NU[creature.getINT()];
        }
    }
    ,
    DEX{

        @Override
        public final int getStat(Creature creature) {
            return creature == null ? 1 : creature.getDEX();
        }

        @Override
        public final double calcBonus(Creature creature) {
            return creature == null ? 1.0 : NV[creature.getDEX()];
        }
    }
    ,
    WIT{

        @Override
        public final int getStat(Creature creature) {
            return creature == null ? 1 : creature.getWIT();
        }

        @Override
        public final double calcBonus(Creature creature) {
            return creature == null ? 1.0 : NW[creature.getWIT()];
        }
    }
    ,
    CON{

        @Override
        public final int getStat(Creature creature) {
            return creature == null ? 1 : creature.getCON();
        }

        @Override
        public final double calcBonus(Creature creature) {
            return creature == null ? 1.0 : NX[creature.getCON()];
        }
    }
    ,
    MEN{

        @Override
        public final int getStat(Creature creature) {
            return creature == null ? 1 : creature.getMEN();
        }

        @Override
        public final double calcBonus(Creature creature) {
            return creature == null ? 1.0 : NY[creature.getMEN()];
        }
    }
    ,
    NONE;

    public static final BaseStats[] VALUES;
    protected static final Logger _log;
    private static final int NS = 100;
    private static final double[] NT;
    private static final double[] NU;
    private static final double[] NV;
    private static final double[] NW;
    private static final double[] NX;
    private static final double[] NY;

    public int getStat(Creature creature) {
        return 1;
    }

    public double calcBonus(Creature creature) {
        return 1.0;
    }

    public double calcChanceMod(Creature creature) {
        return 2.0 - Math.sqrt(this.calcBonus(creature));
    }

    public static final BaseStats valueOfXml(String string) {
        string = string.intern();
        for (BaseStats baseStats : VALUES) {
            if (!baseStats.toString().equalsIgnoreCase(string)) continue;
            if (baseStats == NONE) {
                return null;
            }
            return baseStats;
        }
        throw new NoSuchElementException("Unknown name '" + string + "' for enum BaseStats");
    }

    static {
        VALUES = BaseStats.values();
        _log = LoggerFactory.getLogger(BaseStats.class);
        NT = new double[100];
        NU = new double[100];
        NV = new double[100];
        NW = new double[100];
        NX = new double[100];
        NY = new double[100];
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setIgnoringComments(true);
        File file = new File(Config.DATAPACK_ROOT, "data/attribute_bonus.xml");
        Document document = null;
        try {
            document = documentBuilderFactory.newDocumentBuilder().parse(file);
        }
        catch (SAXException sAXException) {
            _log.error("", sAXException);
        }
        catch (IOException iOException) {
            _log.error("", iOException);
        }
        catch (ParserConfigurationException parserConfigurationException) {
            _log.error("", parserConfigurationException);
        }
        if (document != null) {
            for (Node node = document.getFirstChild(); node != null; node = node.getNextSibling()) {
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    double d;
                    String string;
                    Node node3;
                    if (node2.getNodeName().equalsIgnoreCase("str_bonus")) {
                        for (node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            string = node3.getNodeName();
                            if (!string.equalsIgnoreCase("set")) continue;
                            int n = Integer.valueOf(node3.getAttributes().getNamedItem("attribute").getNodeValue());
                            d = Integer.valueOf(node3.getAttributes().getNamedItem("val").getNodeValue()).intValue();
                            BaseStats.NT[n] = (100.0 + d) / 100.0;
                        }
                    }
                    if (node2.getNodeName().equalsIgnoreCase("int_bonus")) {
                        for (node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            string = node3.getNodeName();
                            if (!string.equalsIgnoreCase("set")) continue;
                            int n = Integer.valueOf(node3.getAttributes().getNamedItem("attribute").getNodeValue());
                            d = Integer.valueOf(node3.getAttributes().getNamedItem("val").getNodeValue()).intValue();
                            BaseStats.NU[n] = (100.0 + d) / 100.0;
                        }
                    }
                    if (node2.getNodeName().equalsIgnoreCase("con_bonus")) {
                        for (node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            string = node3.getNodeName();
                            if (!string.equalsIgnoreCase("set")) continue;
                            int n = Integer.valueOf(node3.getAttributes().getNamedItem("attribute").getNodeValue());
                            d = Integer.valueOf(node3.getAttributes().getNamedItem("val").getNodeValue()).intValue();
                            BaseStats.NX[n] = (100.0 + d) / 100.0;
                        }
                    }
                    if (node2.getNodeName().equalsIgnoreCase("men_bonus")) {
                        for (node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            string = node3.getNodeName();
                            if (!string.equalsIgnoreCase("set")) continue;
                            int n = Integer.valueOf(node3.getAttributes().getNamedItem("attribute").getNodeValue());
                            d = Integer.valueOf(node3.getAttributes().getNamedItem("val").getNodeValue()).intValue();
                            BaseStats.NY[n] = (100.0 + d) / 100.0;
                        }
                    }
                    if (node2.getNodeName().equalsIgnoreCase("dex_bonus")) {
                        for (node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            string = node3.getNodeName();
                            if (!string.equalsIgnoreCase("set")) continue;
                            int n = Integer.valueOf(node3.getAttributes().getNamedItem("attribute").getNodeValue());
                            d = Integer.valueOf(node3.getAttributes().getNamedItem("val").getNodeValue()).intValue();
                            BaseStats.NV[n] = (100.0 + d) / 100.0;
                        }
                    }
                    if (!node2.getNodeName().equalsIgnoreCase("wit_bonus")) continue;
                    for (node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                        string = node3.getNodeName();
                        if (!string.equalsIgnoreCase("set")) continue;
                        int n = Integer.valueOf(node3.getAttributes().getNamedItem("attribute").getNodeValue());
                        d = Integer.valueOf(node3.getAttributes().getNamedItem("val").getNodeValue()).intValue();
                        BaseStats.NW[n] = (100.0 + d) / 100.0;
                    }
                }
            }
        }
    }
}

