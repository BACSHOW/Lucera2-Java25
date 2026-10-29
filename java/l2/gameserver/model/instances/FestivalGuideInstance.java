/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.Calendar;
import l2.gameserver.Config;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;

public final class FestivalGuideInstance
extends NpcInstance {
    protected int _festivalType;
    protected int _festivalOracle;

    public FestivalGuideInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        switch (this.getNpcId()) {
            case 31127: 
            case 31132: {
                this._festivalType = 0;
                this._festivalOracle = 2;
                break;
            }
            case 31128: 
            case 31133: {
                this._festivalType = 1;
                this._festivalOracle = 2;
                break;
            }
            case 31129: 
            case 31134: {
                this._festivalType = 2;
                this._festivalOracle = 2;
                break;
            }
            case 31130: 
            case 31135: {
                this._festivalType = 3;
                this._festivalOracle = 2;
                break;
            }
            case 31131: 
            case 31136: {
                this._festivalType = 4;
                this._festivalOracle = 2;
                break;
            }
            case 31137: 
            case 31142: {
                this._festivalType = 0;
                this._festivalOracle = 1;
                break;
            }
            case 31138: 
            case 31143: {
                this._festivalType = 1;
                this._festivalOracle = 1;
                break;
            }
            case 31139: 
            case 31144: {
                this._festivalType = 2;
                this._festivalOracle = 1;
                break;
            }
            case 31140: 
            case 31145: {
                this._festivalType = 3;
                this._festivalOracle = 1;
                break;
            }
            case 31141: 
            case 31146: {
                this._festivalType = 4;
                this._festivalOracle = 1;
            }
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        block34: {
            block35: {
                block33: {
                    if (!FestivalGuideInstance.canBypassCheck(player, this)) {
                        return;
                    }
                    if (SevenSigns.getInstance().getPlayerCabal(player) == 0) {
                        player.sendMessage(new CustomMessage("FestivalGuide.YouMustBeParticipant", player, new Object[0]));
                        return;
                    }
                    if (!string.startsWith("FestivalDesc")) break block33;
                    int n = Integer.parseInt(string.substring(13));
                    this.a(player, n, null, true);
                    break block34;
                }
                if (!string.startsWith("Festival")) break block35;
                Party party = player.getParty();
                int n = Integer.parseInt(string.substring(9, 10));
                switch (n) {
                    case 1: {
                        this.a(player, 1, null, false);
                        break;
                    }
                    case 2: {
                        if (SevenSigns.getInstance().getCurrentPeriod() != 1) {
                            this.a(player, 2, "a", false);
                            return;
                        }
                        if (SevenSignsFestival.getInstance().isFestivalInitialized()) {
                            player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2FestivalGuideInstance.InProgress", player, new Object[0]));
                            return;
                        }
                        if (party == null || party.getMemberCount() < Config.FESTIVAL_MIN_PARTY_SIZE) {
                            this.a(player, 2, "b", false);
                            return;
                        }
                        if (!party.isLeader(player)) {
                            this.a(player, 2, "c", false);
                            return;
                        }
                        int n2 = SevenSignsFestival.getMaxLevelForFestival(this._festivalType);
                        for (Player player2 : party.getPartyMembers()) {
                            if (player2.getLevel() > n2) {
                                this.a(player, 2, "d", false);
                                return;
                            }
                            if (SevenSigns.getInstance().getPlayerCabal(player2) != 0) continue;
                            this.a(player, 2, "g", false);
                            return;
                        }
                        if (player.isFestivalParticipant()) {
                            this.a(player, 2, "f", false);
                            return;
                        }
                        int n3 = Integer.parseInt(string.substring(11));
                        long l = (long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, n3) * Config.FESTIVAL_RATE_PRICE);
                        if (!player.getInventory().destroyItemByItemId(n3, l)) {
                            player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2FestivalGuideInstance.NotEnoughSSType", player, new Object[0]));
                            return;
                        }
                        player.sendPacket((IStaticPacket)SystemMessage2.removeItems(n3, l));
                        SevenSignsFestival.getInstance().addAccumulatedBonus(this._festivalType, n3, l);
                        new DarknessFestival(player.getParty(), SevenSigns.getInstance().getPlayerCabal(player), this._festivalType);
                        this.a(player, 2, "e", false);
                        break;
                    }
                    case 4: {
                        Object object;
                        StringBuilder stringBuilder = new StringBuilder("<html><body>Festival Guide:<br>These are the top scores of the week, for the ");
                        StatsSet statsSet = SevenSignsFestival.getInstance().getHighestScoreData(2, this._festivalType);
                        StatsSet statsSet2 = SevenSignsFestival.getInstance().getHighestScoreData(1, this._festivalType);
                        StatsSet statsSet3 = SevenSignsFestival.getInstance().getOverallHighestScoreData(this._festivalType);
                        int n4 = statsSet.getInteger("score");
                        int n5 = statsSet2.getInteger("score");
                        int n6 = 0;
                        if (statsSet3 != null) {
                            n6 = statsSet3.getInteger("score");
                        }
                        stringBuilder.append(SevenSignsFestival.getFestivalName(this._festivalType) + " festival.<br>");
                        if (n4 > 0) {
                            stringBuilder.append("Dawn: " + this.w(statsSet.getString("date")) + ". Score " + n4 + "<br>" + statsSet.getString("names").replaceAll(",", ", ") + "<br>");
                        } else {
                            stringBuilder.append("Dawn: No record exists. Score 0<br>");
                        }
                        if (n5 > 0) {
                            stringBuilder.append("Dusk: " + this.w(statsSet2.getString("date")) + ". Score " + n5 + "<br>" + statsSet2.getString("names").replaceAll(",", ", ") + "<br>");
                        } else {
                            stringBuilder.append("Dusk: No record exists. Score 0<br>");
                        }
                        if (n6 > 0 && statsSet3 != null) {
                            object = "Children of Dusk";
                            if (statsSet3.getInteger("cabal") == 2) {
                                object = "Children of Dawn";
                            }
                            stringBuilder.append("Consecutive top scores: " + this.w(statsSet3.getString("date")) + ". Score " + n6 + "<br>Affilated side: " + (String)object + "<br>" + statsSet3.getString("names").replaceAll(",", ", ") + "<br>");
                        } else {
                            stringBuilder.append("Consecutive top scores: No record exists. Score 0<br>");
                        }
                        stringBuilder.append("<a action=\"bypass -h npc_" + this.getObjectId() + "_Chat 0\">Go back.</a></body></html>");
                        object = new NpcHtmlMessage(player, this);
                        ((NpcHtmlMessage)object).setHtml(stringBuilder.toString());
                        player.sendPacket((IStaticPacket)object);
                        break;
                    }
                    case 8: {
                        if (party == null) {
                            return;
                        }
                        if (!party.isLeader(player)) {
                            this.a(player, 8, "a", false);
                            break;
                        }
                        Reflection reflection = this.getReflection();
                        if (reflection instanceof DarknessFestival) {
                            if (((DarknessFestival)reflection).increaseChallenge()) {
                                this.a(player, 8, "b", false);
                                break;
                            }
                            this.a(player, 8, "c", false);
                            break;
                        }
                        break block34;
                    }
                    case 9: {
                        if (party == null) {
                            return;
                        }
                        Reflection reflection = this.getReflection();
                        if (!(reflection instanceof DarknessFestival)) {
                            return;
                        }
                        if (party.isLeader(player)) {
                            ((DarknessFestival)reflection).collapse();
                            break;
                        }
                        if (party.getMemberCount() > Config.FESTIVAL_MIN_PARTY_SIZE) {
                            player.leaveParty();
                            break;
                        }
                        player.sendMessage(new CustomMessage("FestivalGuide.OnlyPartyLeader", player, new Object[0]));
                        break;
                    }
                    default: {
                        this.a(player, n, null, false);
                        break;
                    }
                }
                break block34;
            }
            super.onBypassFeedback(player, string);
        }
    }

    private void a(Player player, int n, String string, boolean bl) {
        String string2 = "seven_signs/festival/";
        string2 = string2 + (bl ? "desc_" : "festival_");
        string2 = string2 + (string != null ? n + string + ".htm" : n + ".htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(string2);
        npcHtmlMessage.replace("%festivalType%", SevenSignsFestival.getFestivalName(this._festivalType));
        npcHtmlMessage.replace("%min%", String.valueOf(Config.FESTIVAL_MIN_PARTY_SIZE));
        if (n == 1) {
            npcHtmlMessage.replace("%price1%", String.valueOf((long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, 6362) * Config.FESTIVAL_RATE_PRICE)));
            npcHtmlMessage.replace("%price2%", String.valueOf((long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, 6361) * Config.FESTIVAL_RATE_PRICE)));
            npcHtmlMessage.replace("%price3%", String.valueOf((long)Math.floor((double)SevenSignsFestival.getStoneCount(this._festivalType, 6360) * Config.FESTIVAL_RATE_PRICE)));
        }
        if (n == 5) {
            npcHtmlMessage.replace("%statsTable%", this.cU());
        }
        if (n == 6) {
            npcHtmlMessage.replace("%bonusTable%", this.cV());
        }
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        player.sendActionFailed();
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        String string = "seven_signs/";
        switch (this.getNpcId()) {
            case 31127: 
            case 31128: 
            case 31129: 
            case 31130: 
            case 31131: {
                string = string + "festival/dawn_guide.htm";
                break;
            }
            case 31137: 
            case 31138: 
            case 31139: 
            case 31140: 
            case 31141: {
                string = string + "festival/dusk_guide.htm";
                break;
            }
            case 31132: 
            case 31133: 
            case 31134: 
            case 31135: 
            case 31136: 
            case 31142: 
            case 31143: 
            case 31144: 
            case 31145: 
            case 31146: {
                string = string + "festival/festival_witch.htm";
                break;
            }
            default: {
                string = this.getHtmlPath(this.getNpcId(), n, player);
            }
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, string, n));
    }

    private String cU() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; ++i) {
            long l = SevenSignsFestival.getInstance().getHighestScore(2, i);
            long l2 = SevenSignsFestival.getInstance().getHighestScore(1, i);
            String string = SevenSignsFestival.getFestivalName(i);
            String string2 = "Children of Dusk";
            if (l > l2) {
                string2 = "Children of Dawn";
            } else if (l == l2) {
                string2 = "None";
            }
            stringBuilder.append("<tr><td width=\"100\" align=\"center\">" + string + "</td><td align=\"center\" width=\"35\">" + l2 + "</td><td align=\"center\" width=\"35\">" + l + "</td><td align=\"center\" width=\"130\">" + string2 + "</td></tr>");
        }
        return stringBuilder.toString();
    }

    private String cV() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; ++i) {
            long l = SevenSignsFestival.getInstance().getAccumulatedBonus(i);
            String string = SevenSignsFestival.getFestivalName(i);
            stringBuilder.append("<tr><td align=\"center\" width=\"150\">" + string + "</td><td align=\"center\" width=\"150\">" + l + "</td></tr>");
        }
        return stringBuilder.toString();
    }

    private String w(String string) {
        long l = Long.valueOf(string);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        return calendar.get(1) + "/" + calendar.get(2) + "/" + calendar.get(5);
    }
}

