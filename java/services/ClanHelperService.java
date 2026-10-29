/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanHelperService
extends Functions
implements ScriptFile {
    private static final Logger log = LoggerFactory.getLogger(ClanHelperService.class);
    private static final String bWH = "@clanhelper.used";
    private static final List<Skill> bWI = Arrays.asList(SkillTable.getInstance().getInfo(370, 3), SkillTable.getInstance().getInfo(373, 3), SkillTable.getInstance().getInfo(379, 3), SkillTable.getInstance().getInfo(391, 1), SkillTable.getInstance().getInfo(371, 3), SkillTable.getInstance().getInfo(374, 3), SkillTable.getInstance().getInfo(376, 3), SkillTable.getInstance().getInfo(377, 3), SkillTable.getInstance().getInfo(383, 3), SkillTable.getInstance().getInfo(380, 3), SkillTable.getInstance().getInfo(382, 3), SkillTable.getInstance().getInfo(384, 3), SkillTable.getInstance().getInfo(385, 3), SkillTable.getInstance().getInfo(386, 3), SkillTable.getInstance().getInfo(387, 3), SkillTable.getInstance().getInfo(388, 3), SkillTable.getInstance().getInfo(390, 3), SkillTable.getInstance().getInfo(372, 3), SkillTable.getInstance().getInfo(375, 3), SkillTable.getInstance().getInfo(378, 3), SkillTable.getInstance().getInfo(381, 3), SkillTable.getInstance().getInfo(389, 3));
    static boolean SERVICES_CLANHELPER_ENABLED = true;
    static String SERVICES_CLANHELPER_CONFIG = "1=2:2;2=2:3,[-200-10000,57-100500];3=2:4,[-200-20000]";
    private static boolean bWJ = false;
    private static Map<Integer, ClanHelperEntry> bWK = Collections.emptyMap();

    @Override
    public void onLoad() {
        if (Config.SERVICES_CLANHELPER_ENABLED) {
            StringTokenizer stringTokenizer = new StringTokenizer(Config.SERVICES_CLANHELPER_CONFIG, ";");
            LinkedHashMap<Integer, ClanHelperEntry> linkedHashMap = new LinkedHashMap<Integer, ClanHelperEntry>();
            while (stringTokenizer.hasMoreTokens()) {
                String string = stringTokenizer.nextToken();
                int n = string.indexOf(61);
                int n2 = Integer.parseInt(string.substring(0, n));
                linkedHashMap.put(n2, ClanHelperEntry.parse(string.substring(n + 1)));
            }
            bWJ = true;
            bWK = linkedHashMap;
            log.info("ClanHelperService: Loaded {} entry(s).", (Object)linkedHashMap.size());
        } else {
            bWJ = false;
            bWK = Collections.emptyMap();
            log.info("ClanHelperService: Disabled");
        }
    }

    private boolean a(Clan clan, int n) {
        String string = CharacterVariablesDAO.getInstance().getVar(clan.getClanId(), bWH, "clan-var");
        if (string == null || string.isEmpty()) {
            return false;
        }
        for (String string2 : string.split(";")) {
            if (string2.isEmpty() || n != Integer.parseInt(string2)) continue;
            return true;
        }
        return false;
    }

    private void b(Clan clan, int n) {
        String string = StringUtils.defaultString(CharacterVariablesDAO.getInstance().getVar(clan.getClanId(), bWH, "clan-var"), "");
        CharacterVariablesDAO.getInstance().setVar(clan.getClanId(), bWH, "clan-var", String.format("%s;%d", string, n), -1L);
    }

    public void get_clan_help(String[] stringArray) {
        Player player = this.getSelf();
        if (player == null || !ClanHelperService.CheckPlayerConditions(player)) {
            return;
        }
        if (stringArray == null || stringArray.length < 1) {
            return;
        }
        int n = Integer.parseInt(stringArray[0]);
        if (!bWK.containsKey(n)) {
            return;
        }
        ClanHelperEntry clanHelperEntry = bWK.get(n);
        if (!bWJ || !Config.SERVICES_CLANHELPER_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_A_CLAN_MEMBER);
            return;
        }
        if (clan.getLeaderId() != player.getObjectId()) {
            player.sendPacket((IStaticPacket)Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }
        if (this.a(clan, n)) {
            player.sendMessage(new CustomMessage("services.ClanHelper.Already", player, new Object[0]));
            return;
        }
        HashMap<String, Player> hashMap = new HashMap<String, Player>();
        List<Player> list = clan.getOnlineMembers(0);
        for (Player object : list) {
            GameClient gameClient;
            if (!object.isConnected() || object.isLogoutStarted() || (gameClient = object.getNetConnection()) == null || gameClient.getHwid() == null || gameClient.getHwid().isEmpty()) continue;
            hashMap.put(gameClient.getHwid(), object);
        }
        if (hashMap.size() < clanHelperEntry.getRequiredClanSize()) {
            player.sendMessage(new CustomMessage("services.ClanHelper.ClanToSmall", player, new Object[0]));
            return;
        }
        this.b(clan, n);
        boolean bl = false;
        if (clanHelperEntry.getRewardLevel() > 0 && clanHelperEntry.getRewardLevel() >= clan.getLevel()) {
            clan.setLevel(clanHelperEntry.getRewardLevel());
            bl = true;
        }
        if (Config.SERVICES_CLANHELPER_ADD_FULL_CLAN_SKILLS) {
            for (Skill skill : bWI) {
                clan.addSkill(skill, true);
                clan.broadcastToOnlineMembers(new L2GameServerPacket[]{new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill)});
                bl = true;
            }
        }
        for (Pair<Integer, Long> pair : clanHelperEntry.getRewardItems()) {
            if (pair.getKey() < 0) {
                switch (pair.getKey()) {
                    case -200: {
                        int n2 = pair.getValue().intValue();
                        clan.incReputation(n2, false, "ClanHelperService");
                        player.sendPacket((IStaticPacket)new SystemMessage(1777).addNumber(n2));
                        bl = true;
                        break;
                    }
                    case -100: {
                        for (Player player2 : hashMap.values()) {
                            player2.addPcBangPoints(pair.getValue().intValue(), false);
                        }
                        break;
                    }
                }
                continue;
            }
            for (Player player2 : hashMap.values()) {
                ClanHelperService.addItem(player2, pair.getKey(), pair.getValue());
            }
        }
        if (bl) {
            clan.updateClanInDB();
            clan.broadcastClanStatus(true, true, true);
        }
        player.sendMessage(new CustomMessage("services.ClanHelper.Success", player, new Object[0]));
        Log.service("ClanHelperService", player, "received a clan reward  for Clan " + player.getClan().getName());
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
    }

    private static class ClanHelperEntry {
        private final int bWL;
        private final int bWM;
        private final List<Pair<Integer, Long>> bWN;

        private ClanHelperEntry(int n, int n2, List<Pair<Integer, Long>> list) {
            this.bWL = n;
            this.bWM = n2;
            this.bWN = list;
        }

        public ClanHelperEntry(int n, int n2) {
            this.bWL = n;
            this.bWM = n2;
            this.bWN = Collections.emptyList();
        }

        public int getRequiredClanSize() {
            return this.bWL;
        }

        public int getRewardLevel() {
            return this.bWM;
        }

        public List<Pair<Integer, Long>> getRewardItems() {
            return this.bWN;
        }

        public static ClanHelperEntry parse(String string) {
            int n = string.indexOf(58);
            if (n <= 0) {
                throw new RuntimeException("Can't parse requirements of \"" + string + "\"");
            }
            String string2 = string.substring(0, n).trim();
            String string3 = string.substring(n + 1).trim();
            int n2 = string2.indexOf(44);
            int n3 = Integer.parseInt(n2 > 0 ? string2.substring(0, n2).trim() : string2);
            int n4 = string3.indexOf(44, 0);
            if (n4 <= 0) {
                int n5 = Integer.parseInt(string3);
                return new ClanHelperEntry(n3, n5);
            }
            int n6 = Integer.parseInt(string3.substring(0, n4));
            String string4 = string3.substring(n4 + 1).trim();
            if (string4.isEmpty()) {
                return new ClanHelperEntry(n3, n6);
            }
            ArrayList<Pair<Integer, Long>> arrayList = new ArrayList<Pair<Integer, Long>>();
            if (!string4.startsWith(",")) {
                if (!string4.startsWith("[") || !string4.endsWith("]")) {
                    throw new RuntimeException("Can't parse reward of \"" + string + "\"");
                }
                string4 = string4.substring(1, string4.length() - 1).trim();
                for (String string5 : string4.split(",")) {
                    if ((string5 = string5.trim()).isEmpty()) continue;
                    int n7 = string5.indexOf(45, 1);
                    arrayList.add(Pair.of(Integer.parseInt(string5.substring(0, n7).trim()), Long.parseLong(string5.substring(n7 + 1).trim())));
                }
            }
            return new ClanHelperEntry(n3, n6, arrayList);
        }
    }
}

