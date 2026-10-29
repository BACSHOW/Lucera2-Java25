/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.StringHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.Language;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecondPasswordAuth {
    private static final Logger LOG = LoggerFactory.getLogger(SecondPasswordAuth.class);
    private final String pm;
    private String aab;
    private int aac;
    private long aad;
    private SecondPasswordAuthUI aae;

    public SecondPasswordAuth(String string) {
        this.pm = string;
        this.aab = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String cfr_renamed_0() {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection;
        block5: {
            if (this.aab != null) {
                return this.aab;
            }
            connection = null;
            preparedStatement = null;
            resultSet = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("SELECT `password`, `tryLine`, `blockEndTime` FROM `second_auth` WHERE `login` = ?");
                preparedStatement.setString(1, this.pm);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) break block5;
                this.aab = resultSet.getString("password");
                this.aac = Math.min(Config.SECOND_AUTH_MAX_TRYS, resultSet.getInt("tryLine"));
                this.aad = resultSet.getLong("blockEndTime");
            }
            catch (SQLException sQLException) {
                try {
                    LOG.warn("Database error on retreiving second password for login '" + this.pm + "' :", sQLException);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return this.aab;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void store() {
        if (this.aab == null) {
            return;
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO `second_auth`(`login`, `password`, `tryLine`, `blockEndTime`) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, this.pm);
            preparedStatement.setString(2, this.aab);
            preparedStatement.setInt(3, this.getTrysCount());
            preparedStatement.setLong(4, this.aad);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                LOG.warn("Database error on storing second password for login '" + this.pm + "' :", sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public boolean isSecondPasswordSet() {
        return this.cfr_renamed_0() != null;
    }

    public boolean isBlocked() {
        if (this.aad == 0L) {
            return false;
        }
        if (this.aad * 1000L < System.currentTimeMillis()) {
            this.aad = 0L;
            this.aac = 0;
            this.store();
            return false;
        }
        return true;
    }

    public int getBlockTimeLeft() {
        return (int)Math.max(0L, this.aad - System.currentTimeMillis() / 1000L);
    }

    public int getTrysCount() {
        return Math.min(Config.SECOND_AUTH_MAX_TRYS, this.aac);
    }

    public boolean isValidSecondPassword(String string) {
        if (string == null && this.cfr_renamed_0() == null) {
            return true;
        }
        if (string.equalsIgnoreCase(this.cfr_renamed_0())) {
            this.aad = 0L;
            this.aac = 0;
            this.store();
            return true;
        }
        ++this.aac;
        if (this.aac >= Config.SECOND_AUTH_MAX_TRYS) {
            this.aad = System.currentTimeMillis() / 1000L + Config.SECOND_AUTH_BLOCK_TIME;
            this.aac = Config.SECOND_AUTH_MAX_TRYS;
        }
        this.store();
        return false;
    }

    public boolean changePassword(String string, String string2) {
        if (!this.isValidSecondPassword(string)) {
            return false;
        }
        this.aab = string2;
        this.store();
        return true;
    }

    public SecondPasswordAuthUI getUI() {
        return this.aae;
    }

    public void setUI(SecondPasswordAuthUI secondPasswordAuthUI) {
        this.aae = secondPasswordAuthUI;
    }

    public static class SecondPasswordAuthUI {
        private static final Language aaf;
        private static final Random aag;
        private SecondPasswordAuthUIType aah;
        private SPAUIPINInputData[] aai;
        private SPAUIPINInputData aaj;
        private SecondPasswordAuthUIResult aak;
        private ArrayList<Integer> aal = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        private Runnable aam;

        public SecondPasswordAuthUI(SecondPasswordAuthUIType secondPasswordAuthUIType) {
            Collections.shuffle(this.aal, aag);
            this.aah = secondPasswordAuthUIType;
            switch (this.aah) {
                case CREATE: {
                    this.aai = new SPAUIPINInputData[2];
                    this.aai[0] = new SPAUIPINInputData(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.CREATE.PIN"), 0);
                    this.aai[1] = new SPAUIPINInputData(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.CREATE.PINConfirm"), 1);
                    this.aaj = this.aai[0];
                    break;
                }
                case VERIFY: {
                    this.aai = new SPAUIPINInputData[1];
                    this.aai[0] = new SPAUIPINInputData(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.VERIFY.PIN"), 0);
                    this.aaj = this.aai[0];
                    break;
                }
                case CHANGE: {
                    this.aai = new SPAUIPINInputData[3];
                    this.aai[0] = new SPAUIPINInputData(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.CHANGE.PINOld"), 0);
                    this.aai[1] = new SPAUIPINInputData(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.CHANGE.PINNew"), 1);
                    this.aai[2] = new SPAUIPINInputData(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.CHANGE.PINNewConfirm"), 2);
                    this.aaj = this.aai[0];
                }
            }
            this.aak = null;
        }

        public void setRunOnVerify(Runnable runnable) {
            this.aam = runnable;
        }

        private void a(GameClient gameClient, String string) {
            if (string.equals("cnl")) {
                return;
            }
            if (string.startsWith("af")) {
                int n = string.charAt(2) - 48;
                this.aaj = this.aai[n];
            } else if (string.startsWith("np")) {
                if (this.aaj != null) {
                    if (string.equals("npc")) {
                        this.aaj.clear();
                    } else if (string.equals("npb")) {
                        this.aaj.back();
                    } else {
                        int n = string.charAt(2) - 48;
                        if (n >= 0 && n <= 9 && this.aaj.getLen() < 8) {
                            this.aaj.add(n);
                        }
                    }
                }
            } else {
                if (string.equals("hlp")) {
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
                    npcHtmlMessage.setFile("spahelp.htm");
                    gameClient.sendPacket((L2GameServerPacket)npcHtmlMessage);
                    return;
                }
                if (string.equals("hlb")) {
                    gameClient.getSecondPasswordAuth().getUI().handle(gameClient, "");
                    return;
                }
                if (string.equals("cgh")) {
                    SecondPasswordAuthUI secondPasswordAuthUI = new SecondPasswordAuthUI(SecondPasswordAuthUIType.CHANGE);
                    secondPasswordAuthUI.setRunOnVerify(this.aam);
                    gameClient.getSecondPasswordAuth().setUI(secondPasswordAuthUI);
                    secondPasswordAuthUI.handle(gameClient, "");
                    return;
                }
                if (string.equals("okk")) {
                    SecondPasswordAuth secondPasswordAuth = gameClient.getSecondPasswordAuth();
                    if (secondPasswordAuth == null) {
                        return;
                    }
                    switch (this.aah) {
                        case CREATE: {
                            if (this.aai[0].getLen() < Config.SECOND_AUTH_MIN_LENG || this.aai[1].getLen() < Config.SECOND_AUTH_MIN_LENG) {
                                this.aak = SecondPasswordAuthUIResult.TOO_SHORT;
                                break;
                            }
                            if (this.aai[0].getLen() > Config.SECOND_AUTH_MAX_LENG || this.aai[1].getLen() > Config.SECOND_AUTH_MAX_LENG) {
                                this.aak = SecondPasswordAuthUIResult.TOO_LONG;
                                break;
                            }
                            if (!this.aai[0].toString().equals(this.aai[1].toString())) {
                                this.aak = SecondPasswordAuthUIResult.NOT_MATCH;
                                break;
                            }
                            if (!this.aai[0].isStrongPin()) {
                                this.aak = SecondPasswordAuthUIResult.TOO_SIMPLE;
                                break;
                            }
                            if (secondPasswordAuth.isSecondPasswordSet()) {
                                this.aak = SecondPasswordAuthUIResult.ERROR;
                                break;
                            }
                            String string2 = this.aai[0].toString();
                            secondPasswordAuth.changePassword(null, string2);
                            SecondPasswordAuthUI secondPasswordAuthUI = new SecondPasswordAuthUI(SecondPasswordAuthUIType.VERIFY);
                            secondPasswordAuthUI.setRunOnVerify(this.aam);
                            gameClient.getSecondPasswordAuth().setUI(secondPasswordAuthUI);
                            secondPasswordAuthUI.handle(gameClient, "");
                            return;
                        }
                        case CHANGE: {
                            if (!secondPasswordAuth.isSecondPasswordSet()) {
                                this.aak = SecondPasswordAuthUIResult.ERROR;
                                break;
                            }
                            if (this.aai[0].getLen() < Config.SECOND_AUTH_MIN_LENG || this.aai[1].getLen() < Config.SECOND_AUTH_MIN_LENG || this.aai[2].getLen() < Config.SECOND_AUTH_MIN_LENG) {
                                this.aak = SecondPasswordAuthUIResult.TOO_SHORT;
                                break;
                            }
                            if (this.aai[0].getLen() > Config.SECOND_AUTH_MAX_LENG || this.aai[1].getLen() > Config.SECOND_AUTH_MAX_LENG || this.aai[2].getLen() > Config.SECOND_AUTH_MAX_LENG) {
                                this.aak = SecondPasswordAuthUIResult.TOO_LONG;
                                break;
                            }
                            String string3 = this.aai[0].toString();
                            if (!this.aai[1].toString().equals(this.aai[2].toString())) {
                                this.aak = SecondPasswordAuthUIResult.NOT_MATCH;
                                break;
                            }
                            if (!this.aai[1].isStrongPin()) {
                                this.aak = SecondPasswordAuthUIResult.TOO_SIMPLE;
                                break;
                            }
                            String string4 = this.aai[1].toString();
                            if (!secondPasswordAuth.isBlocked() && secondPasswordAuth.changePassword(string3, string4)) {
                                SecondPasswordAuthUI secondPasswordAuthUI = new SecondPasswordAuthUI(SecondPasswordAuthUIType.VERIFY);
                                secondPasswordAuthUI.setRunOnVerify(this.aam);
                                gameClient.getSecondPasswordAuth().setUI(secondPasswordAuthUI);
                                secondPasswordAuthUI.handle(gameClient, "");
                                return;
                            }
                            if (secondPasswordAuth.isBlocked()) {
                                this.aak = SecondPasswordAuthUIResult.BLOCK_HOMEPAGE;
                                String string5 = StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Result.BLOCK_HOMEPAGE");
                                string5 = string5.replace("%tryCnt%", Integer.toString(secondPasswordAuth.getTrysCount()));
                                string5 = string5.replace("%time%", Util.formatTime(secondPasswordAuth.getBlockTimeLeft()));
                                gameClient.close((L2GameServerPacket)new ConfirmDlg(SystemMsg.S1, -1).addString(string5));
                            } else {
                                this.aak = SecondPasswordAuthUIResult.FAIL_VERIFY;
                                this.a(gameClient);
                            }
                            return;
                        }
                        case VERIFY: {
                            if (!secondPasswordAuth.isSecondPasswordSet()) {
                                this.aak = SecondPasswordAuthUIResult.ERROR;
                                break;
                            }
                            String string6 = this.aai[0].toString();
                            if (!secondPasswordAuth.isBlocked() && secondPasswordAuth.isValidSecondPassword(string6)) {
                                if (this.aam != null) {
                                    gameClient.setSecondPasswordAuthed(true);
                                    ThreadPoolManager.getInstance().execute(this.aam);
                                }
                                return;
                            }
                            if (secondPasswordAuth.isBlocked()) {
                                this.aak = SecondPasswordAuthUIResult.BLOCK_HOMEPAGE;
                                String string7 = StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Result.BLOCK_HOMEPAGE");
                                string7 = string7.replace("%tryCnt%", Integer.toString(secondPasswordAuth.getTrysCount()));
                                string7 = string7.replace("%time%", Util.formatTime(secondPasswordAuth.getBlockTimeLeft()));
                                gameClient.close((L2GameServerPacket)new ConfirmDlg(SystemMsg.S1, -1).addString(string7));
                            } else {
                                this.aak = SecondPasswordAuthUIResult.FAIL_VERIFY;
                                this.a(gameClient);
                            }
                            return;
                        }
                    }
                }
            }
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
            npcHtmlMessage.setHtml(this.dr());
            gameClient.sendPacket((L2GameServerPacket)npcHtmlMessage);
        }

        public void handle(GameClient gameClient, String string) {
            if (string != null) {
                try {
                    this.a(gameClient, string);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        public boolean verify(GameClient gameClient, Runnable runnable) {
            SecondPasswordAuth secondPasswordAuth = gameClient.getSecondPasswordAuth();
            if (secondPasswordAuth == null) {
                return false;
            }
            this.setRunOnVerify(runnable);
            if (secondPasswordAuth.isBlocked()) {
                this.aak = SecondPasswordAuthUIResult.BLOCK_HOMEPAGE;
                String string = StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Result.BLOCK_HOMEPAGE");
                string = string.replace("%tryCnt%", Integer.toString(secondPasswordAuth.getTrysCount()));
                string = string.replace("%time%", Util.formatTime(secondPasswordAuth.getBlockTimeLeft()));
                gameClient.close((L2GameServerPacket)new ConfirmDlg(SystemMsg.S1, -1).addString(string));
                return false;
            }
            this.handle(gameClient, "");
            return true;
        }

        private void a(GameClient gameClient) {
            SecondPasswordAuth secondPasswordAuth = gameClient.getSecondPasswordAuth();
            if (secondPasswordAuth == null) {
                return;
            }
            this.aak = SecondPasswordAuthUIResult.FAIL_VERIFY;
            String string = StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Result.FAIL_VERIFY");
            string = string.replace("%tryCnt%", Integer.toString(Config.SECOND_AUTH_MAX_TRYS - secondPasswordAuth.getTrysCount()));
            gameClient.close((L2GameServerPacket)new ConfirmDlg(SystemMsg.S1, -1).addString(string));
        }

        private String getTitle() {
            return StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth." + this.aah.name() + ".Title");
        }

        private String dp() {
            return StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth." + this.aah.name() + ".Description");
        }

        private String getNote() {
            if (this.aak == null) {
                return StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Note");
            }
            return StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Result." + this.aak.name());
        }

        private String dq() {
            return StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.InputDescription");
        }

        private String dr() {
            StringBuilder stringBuilder = new StringBuilder(8192);
            stringBuilder.append("<html>");
            stringBuilder.append("<head><title>").append(this.getTitle()).append("</title></head>");
            stringBuilder.append("<body><center>");
            stringBuilder.append("<table width=270 border=0 cellspacing=0 cellpadding=0>");
            stringBuilder.append("<br>");
            stringBuilder.append("<tr><td fixwidth=270 align=center>");
            this.c(stringBuilder);
            stringBuilder.append("</td></tr>");
            stringBuilder.append("<tr><td fixwidth=270 align=center>");
            stringBuilder.append("<img src=\"L2UI.SquareBlank\" width=270 height=10>");
            this.e(stringBuilder);
            stringBuilder.append("</td></tr>");
            stringBuilder.append("<tr><td align=center>");
            stringBuilder.append("<img src=\"L2UI.SquareBlank\" width=270 height=10>");
            this.f(stringBuilder);
            stringBuilder.append("</td></tr>");
            stringBuilder.append("</table>");
            stringBuilder.append("</center></body>");
            stringBuilder.append("</html> ");
            return stringBuilder.toString();
        }

        private void c(StringBuilder stringBuilder) {
            stringBuilder.append("<table width=260 height=250 border=0 cellspacing=5 cellpadding=0 bgcolor=000000>");
            stringBuilder.append("<tr><td valign=TOP height=80>").append(this.dp()).append("</td></tr>");
            stringBuilder.append("<tr><td align=CENTER>").append(this.dq()).append("</td></tr>");
            stringBuilder.append("<tr><td valign=TOP>");
            this.d(stringBuilder);
            stringBuilder.append("<br>");
            stringBuilder.append("</td></tr>");
            stringBuilder.append("<tr><td valign=TOP align=center height=100>");
            stringBuilder.append("<img src=\"L2UI.SquareGray\" width=250 height=1><br>");
            this.formatNumPad(stringBuilder);
            stringBuilder.append("<br>");
            stringBuilder.append("</td></tr>");
            stringBuilder.append("</table>");
        }

        private void d(StringBuilder stringBuilder) {
            stringBuilder.append("<table width=250 height=60 border=0 cellspacing=5 cellpadding=0>");
            for (SPAUIPINInputData sPAUIPINInputData : this.aai) {
                sPAUIPINInputData.formatPINInput(stringBuilder, this.aaj == sPAUIPINInputData);
            }
            stringBuilder.append("</table>");
        }

        private void e(StringBuilder stringBuilder) {
            stringBuilder.append("<table height=20 border=0 cellspacing=0 cellpadding=0 bgcolor=\"000000\">").append("<tr><td align=center valign=center fixwidth=264>").append(this.getNote()).append("</td></tr></table>");
        }

        private void f(StringBuilder stringBuilder) {
            stringBuilder.append("<table width=260 border=0 cellspacing=0 cellpadding=0><tr>");
            stringBuilder.append("<td fixwidth=50>");
            if (this.aah.isCanChange()) {
                stringBuilder.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Change")).append("\" action=\"bypass -h spa_cgh\">");
            }
            stringBuilder.append("</td>");
            stringBuilder.append("<td width=60>&nbsp;</td>");
            stringBuilder.append("<td>");
            stringBuilder.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Help")).append("\" action=\"bypass -h spa_hlp\">");
            stringBuilder.append("</td>");
            stringBuilder.append("<td>");
            stringBuilder.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.OK")).append("\" action=\"bypass -h spa_okk\">");
            stringBuilder.append("</td>");
            stringBuilder.append("<td>");
            stringBuilder.append("<button width=47 height=21 fore=\"L2UI_CH3.smallbutton1\" back=\"L2UI_CH3.smallbutton1_down\" value=\"").append(StringHolder.getInstance().getNotNull(aaf, "l2.gameserver.network.l2.SecondPasswordAuth.Cancel")).append("\" action=\"bypass -h spa_cnl\">");
            stringBuilder.append("</td>");
            stringBuilder.append("</tr></table>");
        }

        public void formatNumPad(StringBuilder stringBuilder) {
            int n;
            stringBuilder.append("<table width=90 border=0 cellspacing=0 cellpadding=0>");
            for (int i = 0; i < 3; ++i) {
                stringBuilder.append("<tr>");
                for (int j = 0; j < 3; ++j) {
                    int n2 = i * 3 + j;
                    n = this.aal.get(n2);
                    stringBuilder.append("<td>");
                    stringBuilder.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_").append(n).append("\" back=\"L2UI_CH3.calculate2_").append(n).append("_down\" value=\"\" action=\"bypass spa_np").append(n).append("\">");
                    stringBuilder.append("</td>");
                }
                stringBuilder.append("</tr>");
            }
            stringBuilder.append("<tr><td>");
            n = this.aal.get(9);
            stringBuilder.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_c\" back=\"L2UI_CH3.calculate2_c_down\" action=\"bypass spa_npc\">");
            stringBuilder.append("</td><td>");
            stringBuilder.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_").append(n).append("\" back=\"L2UI_CH3.calculate2_").append(n).append("_down\" value=\"\" action=\"bypass spa_np").append(n).append("\">");
            stringBuilder.append("</td><td>");
            stringBuilder.append("<button width=35 height=24 fore=\"L2UI_CH3.calculate2_bs\" back=\"L2UI_CH3.calculate2_bs_down\" action=\"bypass spa_npb\">");
            stringBuilder.append("</td></tr>");
            stringBuilder.append("</table>");
        }

        static {
            aag = new Random();
            Language language = Language.ENGLISH;
            for (Language language2 : Language.VALUES) {
                if (!language2.getShortName().equals(Config.DEFAULT_LANG)) continue;
                language = language2;
            }
            aaf = language;
        }

        private static class SPAUIPINInputData {
            private final Stack<Integer> aan = new Stack();
            private final String aao;
            private final int aap;

            public SPAUIPINInputData(String string, int n) {
                this.aao = string;
                this.aap = n;
            }

            public String getLabel() {
                return this.aao;
            }

            public int getInputFieldIdx() {
                return this.aap;
            }

            public void clear() {
                this.aan.clear();
            }

            public void back() {
                if (!this.aan.isEmpty()) {
                    this.aan.pop();
                }
            }

            public void add(int n) {
                this.aan.add(n);
            }

            public boolean isEmpty() {
                return this.aan.isEmpty();
            }

            public boolean isStrongPin() {
                return !this.isEmpty();
            }

            public int getLen() {
                return this.aan.size();
            }

            private void a(StringBuilder stringBuilder, boolean bl, int n, String string) {
                int n2 = 8 * Math.min(8, n);
                int n3 = bl ? 1 : 0;
                int n4 = 65 - (n2 + n3);
                String string2 = bl ? "L2UI_CH3.inputbox02_over" : "L2UI_CH3.M_inputbox02";
                String string3 = bl ? "L2UI_CH3.inputbox04_over" : "L2UI_CH3.M_inputbox04";
                String string4 = bl ? "L2UI_CH3.radar_tutorial1" : "L2UI_CH3.radar_tutorial2";
                stringBuilder.append("<table width=67 height=12 border=0 cellspacing=0 cellpadding=0>");
                stringBuilder.append("<tr><td>");
                stringBuilder.append("<img src=\"").append(string2).append("\" width=67 height=1>");
                stringBuilder.append("</td></tr>");
                stringBuilder.append("<tr><td>");
                stringBuilder.append("<table width=67 height=12 border=0 cellspacing=0 cellpadding=0><tr>");
                stringBuilder.append("<td><img src=\"").append(string3).append("\" width=1 height=12></td>");
                stringBuilder.append("<td>");
                stringBuilder.append("<img src=\"L2UI.SquareBlank\" width=65 height=4>");
                stringBuilder.append("<table border=0 cellspacing=0 cellpadding=0><tr>");
                if (n2 > 0) {
                    stringBuilder.append("<td fixwidth=").append(n2).append(">");
                    stringBuilder.append("<button width=").append(n2).append(" height=8 ").append("fore=\"").append(string4).append("\" back=\"").append(string4).append("\" value=\" \"");
                    if (string != null) {
                        stringBuilder.append(" action=\"bypass spa_").append(string).append("\"");
                    }
                    stringBuilder.append(">");
                    stringBuilder.append("</td>");
                }
                if (n3 > 0) {
                    stringBuilder.append("<td valign=TOP><img src=\"L2UI.SquareWhite\" width=1 height=8></td>");
                }
                if (n4 > 0) {
                    stringBuilder.append("<td FIXWIDTH=").append(n4).append(">");
                    stringBuilder.append("<button width=").append(n4).append(" height=8 ").append("fore=\"L2UI.SquareBlank\" back=\"L2UI.SquareBlank\" value=\" \"");
                    if (string != null) {
                        stringBuilder.append(" action=\"bypass spa_").append(string).append("\"");
                    }
                    stringBuilder.append(">");
                    stringBuilder.append("</td>");
                }
                stringBuilder.append("</tr></table>");
                stringBuilder.append("</td>");
                stringBuilder.append("<td><img src=\"").append(string3).append("\" width=1 height=12></td>");
                stringBuilder.append("</tr></table>");
                stringBuilder.append("</td></tr>");
                stringBuilder.append("<tr><td>");
                stringBuilder.append("<img src=\"").append(string2).append("\" width=67 height=1>");
                stringBuilder.append("</td></tr>");
                stringBuilder.append("</table>");
            }

            public void formatPINInput(StringBuilder stringBuilder, boolean bl) {
                stringBuilder.append("<tr>");
                stringBuilder.append("<td align=right valign=TOP fixwidth=100>");
                if (!bl) {
                    stringBuilder.append("<font color=\"a2a0a2\">").append(this.getLabel()).append("</font>");
                } else {
                    stringBuilder.append(this.getLabel());
                }
                stringBuilder.append("</td>");
                stringBuilder.append("<td align=left>");
                this.a(stringBuilder, bl, this.getLen(), String.format("af%d", this.getInputFieldIdx()));
                stringBuilder.append("</td>");
                stringBuilder.append("</tr>");
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder(8);
                for (Integer n : this.aan) {
                    stringBuilder.append((char)(48 + Math.min(9, Math.max(n, 0))));
                }
                return stringBuilder.toString();
            }
        }

        public static enum SecondPasswordAuthUIType {
            CREATE(false),
            VERIFY(true),
            CHANGE(false);

            private final boolean aar;

            private SecondPasswordAuthUIType(boolean bl) {
                this.aar = bl;
            }

            public boolean isCanChange() {
                return this.aar;
            }
        }

        public static enum SecondPasswordAuthUIResult {
            TOO_SHORT,
            TOO_LONG,
            NOT_MATCH,
            TOO_SIMPLE,
            FAIL_VERIFY,
            BLOCK_HOMEPAGE,
            ERROR;

        }
    }
}

