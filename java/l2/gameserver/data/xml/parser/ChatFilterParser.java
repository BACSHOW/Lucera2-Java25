/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import gnu.trove.TIntArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.model.chat.ChatFilters;
import l2.gameserver.model.chat.chatfilter.ChatFilter;
import l2.gameserver.model.chat.chatfilter.ChatFilterMatcher;
import l2.gameserver.model.chat.chatfilter.matcher.MatchChatChannels;
import l2.gameserver.model.chat.chatfilter.matcher.MatchChatLimit;
import l2.gameserver.model.chat.chatfilter.matcher.MatchFloodLimit;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalAnd;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalNot;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalOr;
import l2.gameserver.model.chat.chatfilter.matcher.MatchLogicalXor;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMaps;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinJobLevel;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinLevel;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinLiveTime;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinOnlineTime;
import l2.gameserver.model.chat.chatfilter.matcher.MatchMinPvP;
import l2.gameserver.model.chat.chatfilter.matcher.MatchPremiumState;
import l2.gameserver.model.chat.chatfilter.matcher.MatchRecipientLimit;
import l2.gameserver.model.chat.chatfilter.matcher.MatchWords;
import l2.gameserver.network.l2.components.ChatType;
import org.dom4j.Element;

public class ChatFilterParser
extends AbstractFileParser<ChatFilters> {
    private static ChatFilterParser xi = new ChatFilterParser();

    public static ChatFilterParser getInstance() {
        return xi;
    }

    protected ChatFilterParser() {
        super(ChatFilters.getinstance());
    }

    protected List<ChatFilterMatcher> parseMatchers(Element element) throws Exception {
        ArrayList<ChatFilterMatcher> arrayList = new ArrayList<ChatFilterMatcher>();
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            if (element2.getName().equals("Channels")) {
                ArrayList<ChatType> arrayList2 = new ArrayList<ChatType>();
                StringTokenizer stringTokenizer = new StringTokenizer(element2.getText(), ",");
                while (stringTokenizer.hasMoreTokens()) {
                    arrayList2.add(ChatType.valueOf(stringTokenizer.nextToken()));
                }
                arrayList.add(new MatchChatChannels(arrayList2.toArray(new ChatType[arrayList2.size()])));
                continue;
            }
            if (element2.getName().equals("Maps")) {
                TIntArrayList tIntArrayList = new TIntArrayList();
                StringTokenizer stringTokenizer = new StringTokenizer(element2.getText(), ",");
                while (stringTokenizer.hasMoreTokens()) {
                    String[] stringArray = stringTokenizer.nextToken().split("_");
                    tIntArrayList.add(Integer.parseInt(stringArray[0]));
                    tIntArrayList.add(Integer.parseInt(stringArray[1]));
                }
                arrayList.add(new MatchMaps(tIntArrayList.toNativeArray()));
                continue;
            }
            if (element2.getName().equals("Words")) {
                ArrayList<String> arrayList3 = new ArrayList<String>();
                StringTokenizer stringTokenizer = new StringTokenizer(element2.getText());
                while (stringTokenizer.hasMoreTokens()) {
                    arrayList3.add(stringTokenizer.nextToken());
                }
                arrayList.add(new MatchWords(arrayList3.toArray(new String[arrayList3.size()])));
                continue;
            }
            if (element2.getName().equals("ExcludePremium")) {
                arrayList.add(new MatchPremiumState(Boolean.parseBoolean(element2.getText())));
                continue;
            }
            if (element2.getName().equals("Level")) {
                arrayList.add(new MatchMinLevel(Integer.parseInt(element2.getText())));
                continue;
            }
            if (element2.getName().equals("PvP_count")) {
                arrayList.add(new MatchMinPvP(Integer.parseInt(element2.getText())));
                continue;
            }
            if (element2.getName().equals("JobLevel")) {
                arrayList.add(new MatchMinJobLevel(Integer.parseInt(element2.getText())));
                continue;
            }
            if (element2.getName().equals("OnlineTime")) {
                arrayList.add(new MatchMinOnlineTime(Integer.parseInt(element2.getText())));
                continue;
            }
            if (element2.getName().equals("LiveTime")) {
                arrayList.add(new MatchMinLiveTime(Integer.parseInt(element2.getText())));
                continue;
            }
            if (element2.getName().endsWith("Limit")) {
                int n = 0;
                int n2 = 0;
                int n3 = 0;
                Iterator<Element> iterator2 = element2.elementIterator();
                while (iterator2.hasNext()) {
                    Element element3 = iterator2.next();
                    if (element3.getName().equals("Count")) {
                        n = Integer.parseInt(element3.getText());
                        continue;
                    }
                    if (element3.getName().equals("Time")) {
                        n2 = Integer.parseInt(element3.getText());
                        continue;
                    }
                    if (!element3.getName().equals("Burst")) continue;
                    n3 = Integer.parseInt(element3.getText());
                }
                if (n < 1) {
                    throw new IllegalArgumentException("Limit Count < 1!");
                }
                if (n2 < 1) {
                    throw new IllegalArgumentException("Limit Time  < 1!");
                }
                if (n3 < 1) {
                    throw new IllegalArgumentException("Limit Burst < 1!");
                }
                if (element2.getName().equals("Limit")) {
                    arrayList.add(new MatchChatLimit(n, n2, n3));
                    continue;
                }
                if (element2.getName().equals("FloodLimit")) {
                    arrayList.add(new MatchFloodLimit(n, n2, n3));
                    continue;
                }
                if (!element2.getName().equals("RecipientLimit")) continue;
                arrayList.add(new MatchRecipientLimit(n, n2, n3));
                continue;
            }
            if (element2.getName().equals("Or")) {
                List<ChatFilterMatcher> list = this.parseMatchers(element2);
                arrayList.add(new MatchLogicalOr(list.toArray(new ChatFilterMatcher[list.size()])));
                continue;
            }
            if (element2.getName().equals("And")) {
                List<ChatFilterMatcher> list = this.parseMatchers(element2);
                arrayList.add(new MatchLogicalAnd(list.toArray(new ChatFilterMatcher[list.size()])));
                continue;
            }
            if (element2.getName().equals("Not")) {
                List<ChatFilterMatcher> list = this.parseMatchers(element2);
                if (list.size() == 1) {
                    arrayList.add(new MatchLogicalNot(list.get(0)));
                    continue;
                }
                arrayList.add(new MatchLogicalNot(new MatchLogicalAnd(list.toArray(new ChatFilterMatcher[list.size()]))));
                continue;
            }
            if (!element2.getName().equals("Xor")) continue;
            List<ChatFilterMatcher> list = this.parseMatchers(element2);
            arrayList.add(new MatchLogicalXor(list.toArray(new ChatFilterMatcher[list.size()])));
        }
        return arrayList;
    }

    @Override
    protected void readData(Element element) throws Exception {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            int n = 0;
            String string = null;
            Element element2 = iterator.next();
            Iterator<Element> actionIterator = element2.elementIterator();
            while (actionIterator.hasNext()) {
                Element element3 = actionIterator.next();
                if (element3.getName().equals("Action")) {
                    String string2 = element3.getText();
                    if (string2.equals("BanChat")) {
                        n = 1;
                        continue;
                    }
                    if (string2.equals("WarnMsg")) {
                        n = 2;
                        continue;
                    }
                    if (string2.equals("ReplaceMsg")) {
                        n = 3;
                        continue;
                    }
                    if (!string2.equals("RedirectMsg")) continue;
                    n = 4;
                    continue;
                }
                if (element3.getName().equals("BanTime")) {
                    string = String.valueOf(Integer.parseInt(element3.getText()));
                    continue;
                }
                if (element3.getName().equals("RedirectChannel")) {
                    string = ChatType.valueOf(element3.getText()).toString();
                    continue;
                }
                if (element3.getName().equals("ReplaceMsg")) {
                    string = element3.getText();
                    continue;
                }
                if (!element3.getName().equals("WarnMsg")) continue;
                string = element3.getText();
            }
            List<ChatFilterMatcher> matchers = this.parseMatchers(element2);
            if (matchers.isEmpty()) {
                throw new IllegalArgumentException("No matchers defined for a filter!");
            }
            ChatFilterMatcher chatFilterMatcher = matchers.size() == 1 ? matchers.get(0) : new MatchLogicalAnd(matchers.toArray(new ChatFilterMatcher[matchers.size()]));
            ((ChatFilters)this.getHolder()).add(new ChatFilter(chatFilterMatcher, n, string));
        }
    }

    @Override
    public File getXMLFile() {
        return new File("config/chatfilters.xml");
    }

    @Override
    public String getDTDFileName() {
        return "chatfilters.dtd";
    }
}

