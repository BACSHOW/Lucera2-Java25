/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.ArrayList;
import java.util.List;
import l2.commons.util.Rnd;

public class Bingo {
    protected static final String template = "%msg%<br><br>%choices%<br><br>%board%";
    protected static final String template_final = "%msg%<br><br>%board%";
    protected static final String template_board = "For your information, below is your current selection.<br><table border=\"1\" border color=\"white\" width=100><tr><td align=\"center\">%cell1%</td><td align=\"center\">%cell2%</td><td align=\"center\">%cell3%</td></tr><tr><td align=\"center\">%cell4%</td><td align=\"center\">%cell5%</td><td align=\"center\">%cell6%</td></tr><tr><td align=\"center\">%cell7%</td><td align=\"center\">%cell8%</td><td align=\"center\">%cell9%</td></tr></table>";
    protected static final String msg_again = "You have already selected that number. Choose your %choicenum% number again.";
    protected static final String msg_begin = "I've arranged 9 numbers on the panel.<br>Now, select your %choicenum% number.";
    protected static final String msg_next = "Now, choose your %choicenum% number.";
    protected static final String msg_0lines = "You are spectacularly unlucky! The red-colored numbers on the panel below are the ones you chose. As you can see, they didn't create even a single line. Did you know that it is harder not to create a single line than creating all 3 lines?";
    protected static final String msg_3lines = "You've created 3 lines! The red colored numbers on the bingo panel below are the numbers you chose. Congratulations!";
    protected static final String msg_lose = "Hmm... You didn't make 3 lines. Why don't you try again? The red-colored numbers on the panel are the ones you chose.";
    protected static final String[] nums = new String[]{"first", "second", "third", "fourth", "fifth", "final"};
    protected int lines;
    private final String azn;
    private final List<Integer> azo = new ArrayList<Integer>();
    private final List<Integer> azp = new ArrayList<Integer>();

    public Bingo(String string) {
        this.azn = string;
        while (this.azo.size() < 9) {
            int n = Rnd.get(1, 9);
            if (this.azo.contains(n)) continue;
            this.azo.add(n);
        }
    }

    public String Select(String string) {
        try {
            return this.Select(Integer.valueOf(string));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public String Select(int n) {
        if (n < 1 || n > 9) {
            return null;
        }
        if (this.azp.contains(n)) {
            return this.getDialog(msg_again);
        }
        this.azp.add(n);
        if (this.azp.size() == 6) {
            return this.getFinal();
        }
        return this.getDialog("");
    }

    protected String getBoard() {
        if (this.azp.size() == 0) {
            return "";
        }
        String string = template_board;
        for (int i = 1; i <= 9; ++i) {
            String string2 = "%cell" + String.valueOf(i) + "%";
            int n = this.azo.get(i - 1);
            string = this.azp.contains(n) ? string.replaceFirst(string2, "<font color=\"" + (this.azp.size() == 6 ? "ff0000" : "ffff00") + "\">" + String.valueOf(n) + "</font>") : string.replaceFirst(string2, "?");
        }
        return string;
    }

    public String getDialog(String string) {
        String string2 = template;
        string2 = this.azp.size() == 0 ? string2.replaceFirst("%msg%", msg_begin) : string2.replaceFirst("%msg%", string.equalsIgnoreCase("") ? msg_next : string);
        string2 = string2.replaceFirst("%choicenum%", nums[this.azp.size()]);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= 9; ++i) {
            if (this.azp.contains(i)) continue;
            stringBuilder.append(this.azn.replaceAll("%n%", String.valueOf(i)));
        }
        string2 = string2.replaceFirst("%choices%", stringBuilder.toString());
        string2 = string2.replaceFirst("%board%", this.getBoard());
        return string2;
    }

    protected String getFinal() {
        String string = template_final.replaceFirst("%board%", this.getBoard());
        this.calcLines();
        string = this.lines == 3 ? string.replaceFirst("%msg%", msg_3lines) : (this.lines == 0 ? string.replaceFirst("%msg%", msg_0lines) : string.replaceFirst("%msg%", msg_lose));
        return string;
    }

    public int calcLines() {
        this.lines = 0;
        this.lines += this.checkLine(0, 1, 2) ? 1 : 0;
        this.lines += this.checkLine(3, 4, 5) ? 1 : 0;
        this.lines += this.checkLine(6, 7, 8) ? 1 : 0;
        this.lines += this.checkLine(0, 3, 6) ? 1 : 0;
        this.lines += this.checkLine(1, 4, 7) ? 1 : 0;
        this.lines += this.checkLine(2, 5, 8) ? 1 : 0;
        this.lines += this.checkLine(0, 4, 8) ? 1 : 0;
        this.lines += this.checkLine(2, 4, 6) ? 1 : 0;
        return this.lines;
    }

    public boolean checkLine(int n, int n2, int n3) {
        return this.azp.contains(this.azo.get(n)) && this.azp.contains(this.azo.get(n2)) && this.azp.contains(this.azo.get(n3));
    }
}

