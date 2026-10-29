/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.time.cron;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import l2.commons.time.cron.NextTime;
import l2.commons.util.Rnd;

public class SchedulingPattern
implements NextTime {
    private static final int sy = 0;
    private static final int sz = 59;
    private static final int sA = 0;
    private static final int sB = 23;
    private static final int sC = 1;
    private static final int sD = 31;
    private static final int sE = 1;
    private static final int sF = 12;
    private static final int sG = 0;
    private static final int sH = 7;
    private static final ValueParser sI = new MinuteValueParser();
    private static final ValueParser sJ = new HourValueParser();
    private static final ValueParser sK = new DayOfMonthValueParser();
    private static final ValueParser sL = new MonthValueParser();
    private static final ValueParser sM = new DayOfWeekValueParser();
    private String sN;
    protected List<ValueMatcher> minuteMatchers = new ArrayList<ValueMatcher>();
    protected List<ValueMatcher> hourMatchers = new ArrayList<ValueMatcher>();
    protected List<ValueMatcher> dayOfMonthMatchers = new ArrayList<ValueMatcher>();
    protected List<ValueMatcher> monthMatchers = new ArrayList<ValueMatcher>();
    protected List<ValueMatcher> dayOfWeekMatchers = new ArrayList<ValueMatcher>();
    protected int matcherSize = 0;
    protected Map<Integer, Integer> hourAdder = new TreeMap<Integer, Integer>();
    protected Map<Integer, Integer> hourAdderRnd = new TreeMap<Integer, Integer>();
    protected Map<Integer, Integer> dayOfYearAdder = new TreeMap<Integer, Integer>();
    protected Map<Integer, Integer> minuteAdderRnd = new TreeMap<Integer, Integer>();
    protected Map<Integer, Integer> weekOfYearAdder = new TreeMap<Integer, Integer>();

    public static boolean validate(String string) {
        try {
            new SchedulingPattern(string);
        }
        catch (InvalidPatternException invalidPatternException) {
            return false;
        }
        return true;
    }

    public SchedulingPattern(String string) throws InvalidPatternException {
        this.sN = string;
        StringTokenizer stringTokenizer = new StringTokenizer(string, "|");
        if (stringTokenizer.countTokens() < 1) {
            throw new InvalidPatternException("invalid pattern: \"" + string + "\"");
        }
        while (stringTokenizer.hasMoreTokens()) {
            int n;
            String[] stringArray;
            String string2;
            String string3 = stringTokenizer.nextToken();
            StringTokenizer stringTokenizer2 = new StringTokenizer(string3, " \t");
            int n2 = stringTokenizer2.countTokens();
            if (n2 < 5 || n2 > 6) {
                throw new InvalidPatternException("invalid pattern: \"" + string3 + "\"");
            }
            try {
                string2 = stringTokenizer2.nextToken();
                stringArray = string2.split(":");
                if (stringArray.length > 1) {
                    for (n = 0; n < stringArray.length - 1; ++n) {
                        if (stringArray[n].length() <= 1) continue;
                        if (stringArray[n].startsWith("~")) {
                            this.minuteAdderRnd.put(this.matcherSize, Integer.parseInt(stringArray[n].substring(1)));
                            continue;
                        }
                        throw new InvalidPatternException("Unknown hour modifier \"" + stringArray[n] + "\"");
                    }
                    string2 = stringArray[stringArray.length - 1];
                }
                this.minuteMatchers.add(this.a(string2, sI));
            }
            catch (Exception exception) {
                throw new InvalidPatternException("invalid pattern \"" + string3 + "\". Error parsing minutes field: " + exception.getMessage() + ".");
            }
            try {
                string2 = stringTokenizer2.nextToken();
                stringArray = string2.split(":");
                if (stringArray.length > 1) {
                    for (n = 0; n < stringArray.length - 1; ++n) {
                        if (stringArray[n].length() <= 1) continue;
                        if (stringArray[n].startsWith("+")) {
                            this.hourAdder.put(this.matcherSize, Integer.parseInt(stringArray[n].substring(1)));
                            continue;
                        }
                        if (stringArray[n].startsWith("~")) {
                            this.hourAdderRnd.put(this.matcherSize, Integer.parseInt(stringArray[n].substring(1)));
                            continue;
                        }
                        throw new InvalidPatternException("Unknown hour modifier \"" + stringArray[n] + "\"");
                    }
                    string2 = stringArray[stringArray.length - 1];
                }
                this.hourMatchers.add(this.a(string2, sJ));
            }
            catch (Exception exception) {
                throw new InvalidPatternException("invalid pattern \"" + string3 + "\". Error parsing hours field: " + exception.getMessage() + ".");
            }
            try {
                string2 = stringTokenizer2.nextToken();
                stringArray = string2.split(":");
                if (stringArray.length > 1) {
                    for (n = 0; n < stringArray.length - 1; ++n) {
                        if (stringArray[n].length() <= 1) continue;
                        if (stringArray[n].startsWith("+")) {
                            this.dayOfYearAdder.put(this.matcherSize, Integer.parseInt(stringArray[n].substring(1)));
                            continue;
                        }
                        throw new InvalidPatternException("Unknown day modifier \"" + stringArray[n] + "\"");
                    }
                    string2 = stringArray[stringArray.length - 1];
                }
                this.dayOfMonthMatchers.add(this.a(string2, sK));
            }
            catch (Exception exception) {
                throw new InvalidPatternException("invalid pattern \"" + string3 + "\". Error parsing days of month field: " + exception.getMessage() + ".");
            }
            try {
                this.monthMatchers.add(this.a(stringTokenizer2.nextToken(), sL));
            }
            catch (Exception exception) {
                throw new InvalidPatternException("invalid pattern \"" + string3 + "\". Error parsing months field: " + exception.getMessage() + ".");
            }
            try {
                this.dayOfWeekMatchers.add(this.a(stringTokenizer2.nextToken(), sM));
            }
            catch (Exception exception) {
                throw new InvalidPatternException("invalid pattern \"" + string3 + "\". Error parsing days of week field: " + exception.getMessage() + ".");
            }
            if (stringTokenizer2.hasMoreTokens()) {
                try {
                    string2 = stringTokenizer2.nextToken();
                    if (string2.charAt(0) != '+') {
                        throw new InvalidPatternException("Unknown week of year addition in pattern \"" + string3 + "\".");
                    }
                    string2 = string2.substring(1);
                    this.weekOfYearAdder.put(this.matcherSize, Integer.parseInt(string2));
                }
                catch (Exception exception) {
                    throw new InvalidPatternException("invalid pattern \"" + string3 + "\". Error parsing week of year addition: " + exception + ".");
                }
            }
            ++this.matcherSize;
        }
    }

    private ValueMatcher a(String string, ValueParser valueParser) throws Exception {
        if (string.length() == 1 && string.equals("*")) {
            return new AlwaysTrueValueMatcher();
        }
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        while (stringTokenizer.hasMoreTokens()) {
            List<Integer> list;
            String string2 = stringTokenizer.nextToken();
            try {
                list = this.b(string2, valueParser);
            }
            catch (Exception exception) {
                throw new Exception("invalid field \"" + string + "\", invalid element \"" + string2 + "\", " + exception.getMessage());
            }
            for (Integer n : list) {
                if (arrayList.contains(n)) continue;
                arrayList.add(n);
            }
        }
        if (arrayList.size() == 0) {
            throw new Exception("invalid field \"" + string + "\"");
        }
        if (valueParser == sK) {
            return new DayOfMonthValueMatcher(arrayList);
        }
        return new IntArrayValueMatcher(arrayList);
    }

    private List<Integer> b(String string, ValueParser valueParser) throws Exception {
        List<Integer> list;
        StringTokenizer stringTokenizer = new StringTokenizer(string, "/");
        int n = stringTokenizer.countTokens();
        if (n < 1 || n > 2) {
            throw new Exception("syntax error");
        }
        try {
            list = this.c(stringTokenizer.nextToken(), valueParser);
        }
        catch (Exception exception) {
            throw new Exception("invalid range, " + exception.getMessage());
        }
        if (n == 2) {
            int n2;
            String string2 = stringTokenizer.nextToken();
            try {
                n2 = Integer.parseInt(string2);
            }
            catch (NumberFormatException numberFormatException) {
                throw new Exception("invalid divisor \"" + string2 + "\"");
            }
            if (n2 < 1) {
                throw new Exception("non positive divisor \"" + n2 + "\"");
            }
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int i = 0; i < list.size(); i += n2) {
                arrayList.add(list.get(i));
            }
            return arrayList;
        }
        return list;
    }

    private List<Integer> c(String string, ValueParser valueParser) throws Exception {
        int n;
        int n2;
        if (string.equals("*")) {
            int n3 = valueParser.getMinValue();
            int n4 = valueParser.getMaxValue();
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int i = n3; i <= n4; ++i) {
                arrayList.add(Integer.valueOf(i));
            }
            return arrayList;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, "-");
        int n5 = stringTokenizer.countTokens();
        if (n5 < 1 || n5 > 2) {
            throw new Exception("syntax error");
        }
        String string2 = stringTokenizer.nextToken();
        try {
            n2 = valueParser.parse(string2);
        }
        catch (Exception exception) {
            throw new Exception("invalid value \"" + string2 + "\", " + exception.getMessage());
        }
        if (n5 == 1) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            arrayList.add(Integer.valueOf(n2));
            return arrayList;
        }
        String string3 = stringTokenizer.nextToken();
        try {
            n = valueParser.parse(string3);
        }
        catch (Exception exception) {
            throw new Exception("invalid value \"" + string3 + "\", " + exception.getMessage());
        }
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        if (n2 < n) {
            for (int i = n2; i <= n; ++i) {
                arrayList.add(Integer.valueOf(i));
            }
        } else if (n2 > n) {
            int n6;
            int n7 = valueParser.getMinValue();
            int n8 = valueParser.getMaxValue();
            for (n6 = n2; n6 <= n8; ++n6) {
                arrayList.add(Integer.valueOf(n6));
            }
            for (n6 = n7; n6 <= n; ++n6) {
                arrayList.add(Integer.valueOf(n6));
            }
        } else {
            arrayList.add(Integer.valueOf(n2));
        }
        return arrayList;
    }

    public boolean match(TimeZone timeZone, long l) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.setTimeInMillis(l);
        gregorianCalendar.set(13, 0);
        gregorianCalendar.set(14, 0);
        for (int i = 0; i < this.matcherSize; ++i) {
            if (this.weekOfYearAdder.containsKey(i)) {
                gregorianCalendar.add(3, -this.weekOfYearAdder.get(i).intValue());
            }
            if (this.dayOfYearAdder.containsKey(i)) {
                gregorianCalendar.add(6, -this.dayOfYearAdder.get(i).intValue());
            }
            if (this.hourAdder.containsKey(i)) {
                gregorianCalendar.add(10, -this.hourAdder.get(i).intValue());
            }
            int n = gregorianCalendar.get(12);
            int n2 = gregorianCalendar.get(11);
            int n3 = gregorianCalendar.get(5);
            int n4 = gregorianCalendar.get(2) + 1;
            int n5 = gregorianCalendar.get(7) - 1;
            int n6 = gregorianCalendar.get(1);
            ValueMatcher valueMatcher = this.minuteMatchers.get(i);
            ValueMatcher valueMatcher2 = this.hourMatchers.get(i);
            ValueMatcher valueMatcher3 = this.dayOfMonthMatchers.get(i);
            ValueMatcher valueMatcher4 = this.monthMatchers.get(i);
            ValueMatcher valueMatcher5 = this.dayOfWeekMatchers.get(i);
            boolean matches = valueMatcher.match(n) && valueMatcher2.match(n2)
                    && (valueMatcher3 instanceof DayOfMonthValueMatcher
                    ? ((DayOfMonthValueMatcher)valueMatcher3).match(n3, n4, gregorianCalendar.isLeapYear(n6))
                    : valueMatcher3.match(n3))
                    && valueMatcher4.match(n4) && valueMatcher5.match(n5);
            if (!matches) continue;
            return true;
        }
        return false;
    }

    public boolean match(long l) {
        return this.match(TimeZone.getDefault(), l);
    }

    public long next(TimeZone timeZone, long l) {
        long l2 = -1L;
        for (int i = 0; i < this.matcherSize; ++i) {
            long l3 = -1L;
            GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
            gregorianCalendar.setTimeInMillis(l);
            gregorianCalendar.set(13, 0);
            gregorianCalendar.set(14, 0);
            if (this.weekOfYearAdder.containsKey(i)) {
                gregorianCalendar.add(3, this.weekOfYearAdder.get(i));
            }
            if (this.dayOfYearAdder.containsKey(i)) {
                gregorianCalendar.add(6, this.dayOfYearAdder.get(i));
            }
            if (this.hourAdder.containsKey(i)) {
                gregorianCalendar.add(10, this.hourAdder.get(i));
            }
            ValueMatcher valueMatcher = this.minuteMatchers.get(i);
            ValueMatcher valueMatcher2 = this.hourMatchers.get(i);
            ValueMatcher valueMatcher3 = this.dayOfMonthMatchers.get(i);
            ValueMatcher valueMatcher4 = this.monthMatchers.get(i);
            ValueMatcher valueMatcher5 = this.dayOfWeekMatchers.get(i);
            block1: while (true) {
                int n = gregorianCalendar.get(1);
                boolean bl = gregorianCalendar.isLeapYear(n);
                for (int j = gregorianCalendar.get(2) + 1; j <= 12; ++j) {
                    if (valueMatcher4.match(j)) {
                        gregorianCalendar.set(2, j - 1);
                        int n2 = DayOfMonthValueMatcher.getLastDayOfMonth(j, bl);
                        for (int k = gregorianCalendar.get(5); k <= n2; ++k) {
                            if (valueMatcher3 instanceof DayOfMonthValueMatcher ? ((DayOfMonthValueMatcher)valueMatcher3).match(k, j, bl) : valueMatcher3.match(k)) {
                                gregorianCalendar.set(5, k);
                                int n3 = gregorianCalendar.get(7) - 1;
                                if (valueMatcher5.match(n3)) {
                                    for (int i2 = gregorianCalendar.get(11); i2 <= 23; ++i2) {
                                        if (valueMatcher2.match(i2)) {
                                            gregorianCalendar.set(11, i2);
                                            for (int i3 = gregorianCalendar.get(12); i3 <= 59; ++i3) {
                                                if (!valueMatcher.match(i3)) continue;
                                                gregorianCalendar.set(12, i3);
                                                long l4 = gregorianCalendar.getTimeInMillis();
                                                if (l4 < l) continue;
                                                if (l3 != -1L && l4 >= l3) break block1;
                                                l3 = l4;
                                                if (this.hourAdderRnd.containsKey(i)) {
                                                    l3 += (long)(Rnd.get(this.hourAdderRnd.get(i)) * 60 * 60) * 1000L;
                                                }
                                                if (!this.minuteAdderRnd.containsKey(i)) break block1;
                                                l3 += (long)(Rnd.get(this.minuteAdderRnd.get(i)) * 60) * 1000L;
                                                break block1;
                                            }
                                        }
                                        gregorianCalendar.set(12, 0);
                                    }
                                }
                            }
                            gregorianCalendar.set(11, 0);
                            gregorianCalendar.set(12, 0);
                        }
                    }
                    gregorianCalendar.set(5, 1);
                    gregorianCalendar.set(11, 0);
                    gregorianCalendar.set(12, 0);
                }
                gregorianCalendar.set(2, 0);
                gregorianCalendar.set(11, 0);
                gregorianCalendar.set(12, 0);
                gregorianCalendar.roll(1, true);
            }
            if (l3 <= l || l2 != -1L && l3 >= l2) continue;
            l2 = l3;
        }
        return l2;
    }

    @Override
    public long next(long l) {
        return this.next(TimeZone.getDefault(), l);
    }

    public String toString() {
        return this.sN;
    }

    private static int a(String string, String[] stringArray, int n) throws Exception {
        for (int i = 0; i < stringArray.length; ++i) {
            if (!stringArray[i].equalsIgnoreCase(string)) continue;
            return n + i;
        }
        throw new Exception("invalid alias \"" + string + "\"");
    }

    private static class DayOfMonthValueMatcher
    extends IntArrayValueMatcher {
        private static final int[] sO = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        public DayOfMonthValueMatcher(List<Integer> list) {
            super(list);
        }

        public boolean match(int n, int n2, boolean bl) {
            return super.match(n) || n > 27 && this.match(32) && DayOfMonthValueMatcher.isLastDayOfMonth(n, n2, bl);
        }

        public static int getLastDayOfMonth(int n, boolean bl) {
            if (bl && n == 2) {
                return 29;
            }
            return sO[n - 1];
        }

        public static boolean isLastDayOfMonth(int n, int n2, boolean bl) {
            return n == DayOfMonthValueMatcher.getLastDayOfMonth(n2, bl);
        }
    }

    private static class IntArrayValueMatcher
    implements ValueMatcher {
        private int[] values;

        public IntArrayValueMatcher(List<Integer> list) {
            int n = list.size();
            this.values = new int[n];
            for (int i = 0; i < n; ++i) {
                try {
                    this.values[i] = list.get(i);
                    continue;
                }
                catch (Exception exception) {
                    throw new IllegalArgumentException(exception.getMessage());
                }
            }
        }

        @Override
        public boolean match(int n) {
            for (int i = 0; i < this.values.length; ++i) {
                if (this.values[i] != n) continue;
                return true;
            }
            return false;
        }
    }

    private static class AlwaysTrueValueMatcher
    implements ValueMatcher {
        private AlwaysTrueValueMatcher() {
        }

        @Override
        public boolean match(int n) {
            return true;
        }
    }

    private static interface ValueMatcher {
        public boolean match(int var1);
    }

    private static class DayOfWeekValueParser
    extends SimpleValueParser {
        private static String[] sP = new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

        public DayOfWeekValueParser() {
            super(0, 7);
        }

        @Override
        public int parse(String string) throws Exception {
            try {
                return super.parse(string) % 7;
            }
            catch (Exception exception) {
                return SchedulingPattern.a(string, DayOfWeekValueParser.sP, 0);
            }
        }
    }

    private static class MonthValueParser
    extends SimpleValueParser {
        private static String[] sP = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

        public MonthValueParser() {
            super(1, 12);
        }

        @Override
        public int parse(String string) throws Exception {
            try {
                return super.parse(string);
            }
            catch (Exception exception) {
                return SchedulingPattern.a(string, MonthValueParser.sP, 1);
            }
        }
    }

    private static class DayOfMonthValueParser
    extends SimpleValueParser {
        public DayOfMonthValueParser() {
            super(1, 31);
        }

        @Override
        public int parse(String string) throws Exception {
            if (string.equalsIgnoreCase("L")) {
                return 32;
            }
            return super.parse(string);
        }
    }

    private static class HourValueParser
    extends SimpleValueParser {
        public HourValueParser() {
            super(0, 23);
        }
    }

    private static class MinuteValueParser
    extends SimpleValueParser {
        public MinuteValueParser() {
            super(0, 59);
        }
    }

    private static class SimpleValueParser
    implements ValueParser {
        protected int minValue;
        protected int maxValue;

        public SimpleValueParser(int n, int n2) {
            this.minValue = n;
            this.maxValue = n2;
        }

        @Override
        public int parse(String string) throws Exception {
            int n;
            try {
                n = Integer.parseInt(string);
            }
            catch (NumberFormatException numberFormatException) {
                throw new Exception("invalid integer value");
            }
            if (n < this.minValue || n > this.maxValue) {
                throw new Exception("value out of range");
            }
            return n;
        }

        @Override
        public int getMinValue() {
            return this.minValue;
        }

        @Override
        public int getMaxValue() {
            return this.maxValue;
        }
    }

    private static interface ValueParser {
        public int parse(String var1) throws Exception;

        public int getMinValue();

        public int getMaxValue();
    }

    public class InvalidPatternException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InvalidPatternException() {
        }

        InvalidPatternException(String string) {
            super(string);
        }
    }
}
