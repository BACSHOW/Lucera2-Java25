/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.MonsterRace;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.DeleteObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MonRaceInfo;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class RaceManagerInstance
extends NpcInstance {
    public static final int LANES = 8;
    public static final int WINDOW_START = 0;
    private static List<Race> VA;
    private static Set<RaceManagerInstance> VB;
    private static int VC;
    private static final long SECOND = 1000L;
    private static final long MINUTE = 60000L;
    private static int VD;
    private static final int VE = 0;
    private static final int WAITING = 1;
    private static final int VF = 2;
    private static final int VG = 3;
    private static int state;
    protected static final int[][] codes;
    private static boolean VH;
    protected static MonRaceInfo packet;
    protected static int[] cost;

    public RaceManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        if (VH) {
            VH = false;
            VC = ServerVariables.getInt("monster_race", 1);
            VA = new ArrayList<Race>();
            VB = new CopyOnWriteArraySet<RaceManagerInstance>();
            ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
            threadPoolManager.scheduleAtFixedRate(new Announcement(816), 0L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(817), 30000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(816), 60000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(817), 90000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(818), 120000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(818), 180000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(818), 240000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(818), 300000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(819), 360000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(819), 420000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(820), 420000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(820), 480000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(821), 510000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(822), 530000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(823), 535000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(823), 536000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(823), 537000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(823), 538000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(823), 539000L, 600000L);
            threadPoolManager.scheduleAtFixedRate(new Announcement(824), 540000L, 600000L);
        }
        VB.add(this);
    }

    public void removeKnownPlayer(Player player) {
        for (int i = 0; i < 8; ++i) {
            player.sendPacket((IStaticPacket)new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
        }
    }

    public void makeAnnouncement(int n) {
        SystemMessage systemMessage = new SystemMessage(n);
        switch (n) {
            case 816: 
            case 817: {
                if (state != 0) {
                    state = 0;
                    this.db();
                }
                systemMessage.addNumber(VC);
                break;
            }
            case 818: 
            case 820: 
            case 823: {
                systemMessage.addNumber(VD);
                systemMessage.addNumber(VC);
                --VD;
                break;
            }
            case 819: {
                systemMessage.addNumber(VC);
                state = 1;
                VD = 2;
                break;
            }
            case 822: 
            case 825: {
                systemMessage.addNumber(VC);
                VD = 5;
                break;
            }
            case 826: {
                state = 3;
                systemMessage.addNumber(MonsterRace.getInstance().getFirstPlace() + 1);
                systemMessage.addNumber(MonsterRace.getInstance().getSecondPlace() + 1);
            }
        }
        this.broadcast(systemMessage);
        if (n == 824) {
            state = 2;
            this.db();
            VD = 5;
        }
    }

    protected void broadcast(L2GameServerPacket l2GameServerPacket) {
        for (RaceManagerInstance raceManagerInstance : VB) {
            if (raceManagerInstance.isDead()) continue;
            raceManagerInstance.broadcastPacketToOthers(l2GameServerPacket);
        }
    }

    public void sendMonsterInfo() {
        this.broadcast(packet);
    }

    private void db() {
        MonsterRace monsterRace = MonsterRace.getInstance();
        if (state == 2) {
            PlaySound playSound = new PlaySound("S_Race");
            this.broadcast(playSound);
            PlaySound playSound2 = new PlaySound(PlaySound.Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559));
            this.broadcast(playSound2);
            packet = new MonRaceInfo(codes[1][0], codes[1][1], monsterRace.getMonsters(), monsterRace.getSpeeds());
            this.sendMonsterInfo();
            ThreadPoolManager.getInstance().schedule(new RunRace(), 5000L);
        } else {
            monsterRace.newRace();
            monsterRace.newSpeeds();
            packet = new MonRaceInfo(codes[0][0], codes[0][1], monsterRace.getMonsters(), monsterRace.getSpeeds());
            this.sendMonsterInfo();
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!RaceManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("BuyTicket") && state != 0) {
            player.sendPacket((IStaticPacket)Msg.MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
            string = "Chat 0";
        }
        if (string.startsWith("ShowOdds") && state == 0) {
            player.sendPacket((IStaticPacket)Msg.MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
            string = "Chat 0";
        }
        if (string.startsWith("BuyTicket")) {
            int n = Integer.parseInt(string.substring(10));
            if (n == 0) {
                player.setRace(0, 0);
                player.setRace(1, 0);
            }
            if (n == 10 && player.getRace(0) == 0 || n == 20 && player.getRace(0) == 0 && player.getRace(1) == 0) {
                n = 0;
            }
            this.showBuyTicket(player, n);
        } else if (string.equals("ShowOdds")) {
            this.showOdds(player);
        } else if (string.equals("ShowInfo")) {
            this.showMonsterInfo(player);
        } else if (!string.equals("calculateWin") && !string.equals("viewHistory")) {
            super.onBypassFeedback(player, string);
        }
    }

    public void showOdds(Player player) {
        if (state == 0) {
            return;
        }
        int n = this.getTemplate().npcId;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        String string = this.getHtmlPath(n, 5, player);
        npcHtmlMessage.setFile(string);
        for (int i = 0; i < 8; ++i) {
            int n2 = i + 1;
            String string2 = "Mob" + n2;
            npcHtmlMessage.replace(string2, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
        }
        npcHtmlMessage.replace("1race", String.valueOf(VC));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        player.sendActionFailed();
    }

    public void showMonsterInfo(Player player) {
        int n = this.getTemplate().npcId;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        String string = this.getHtmlPath(n, 6, player);
        npcHtmlMessage.setFile(string);
        for (int i = 0; i < 8; ++i) {
            int n2 = i + 1;
            String string2 = "Mob" + n2;
            npcHtmlMessage.replace(string2, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
        }
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        player.sendActionFailed();
    }

    public void showBuyTicket(Player player, int n) {
        if (state != 0) {
            return;
        }
        int n2 = this.getTemplate().npcId;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        if (n < 10) {
            String string;
            String string2 = this.getHtmlPath(n2, 2, player);
            npcHtmlMessage.setFile(string2);
            for (int i = 0; i < 8; ++i) {
                int n3 = i + 1;
                string = "Mob" + n3;
                npcHtmlMessage.replace(string, MonsterRace.getInstance().getMonsters()[i].getTemplate().name);
            }
            string = "No1";
            if (n == 0) {
                npcHtmlMessage.replace(string, "");
            } else {
                npcHtmlMessage.replace(string, "" + n);
                player.setRace(0, n);
            }
        } else if (n < 20) {
            if (player.getRace(0) == 0) {
                return;
            }
            String string = this.getHtmlPath(n2, 3, player);
            npcHtmlMessage.setFile(string);
            npcHtmlMessage.replace("0place", "" + player.getRace(0));
            String string3 = "Mob1";
            String string4 = MonsterRace.getInstance().getMonsters()[player.getRace((int)0) - 1].getTemplate().name;
            npcHtmlMessage.replace(string3, string4);
            string3 = "0adena";
            if (n == 10) {
                npcHtmlMessage.replace(string3, "");
            } else {
                npcHtmlMessage.replace(string3, "" + cost[n - 11]);
                player.setRace(1, n - 10);
            }
        } else if (n == 20) {
            if (player.getRace(0) == 0 || player.getRace(1) == 0) {
                return;
            }
            String string = this.getHtmlPath(n2, 4, player);
            npcHtmlMessage.setFile(string);
            npcHtmlMessage.replace("0place", "" + player.getRace(0));
            String string5 = "Mob1";
            String string6 = MonsterRace.getInstance().getMonsters()[player.getRace((int)0) - 1].getTemplate().name;
            npcHtmlMessage.replace(string5, string6);
            string5 = "0adena";
            int n4 = cost[player.getRace(1) - 1];
            npcHtmlMessage.replace(string5, "" + n4);
            string5 = "0tax";
            int n5 = 0;
            npcHtmlMessage.replace(string5, "" + n5);
            string5 = "0total";
            int n6 = n4 + n5;
            npcHtmlMessage.replace(string5, "" + n6);
        } else {
            if (player.getRace(0) == 0 || player.getRace(1) == 0) {
                return;
            }
            if (player.getAdena() < (long)cost[player.getRace(1) - 1]) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            int n7 = player.getRace(0);
            int n8 = player.getRace(1);
            player.setRace(0, 0);
            player.setRace(1, 0);
            player.reduceAdena(cost[n8 - 1], true);
            SystemMessage systemMessage = new SystemMessage(371);
            systemMessage.addNumber(VC);
            systemMessage.addItemName(4443);
            player.sendPacket((IStaticPacket)systemMessage);
            ItemInstance itemInstance = ItemFunctions.createItem(4443);
            itemInstance.setEnchantLevel(VC);
            itemInstance.setBlessed(n7);
            itemInstance.setDamaged(cost[n8 - 1] / 100);
            player.getInventory().addItem(itemInstance);
            return;
        }
        npcHtmlMessage.replace("1race", String.valueOf(VC));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        player.sendActionFailed();
    }

    public MonRaceInfo getPacket() {
        return packet;
    }

    static {
        VC = 1;
        VD = 5;
        state = 3;
        codes = new int[][]{{-1, 0}, {0, 15322}, {13765, -1}};
        VH = true;
        cost = new int[]{100, 500, 1000, 5000, 10000, 20000, 50000, 100000};
    }

    class RunEnd
    extends RunnableImpl {
        RunEnd() {
        }

        @Override
        public void runImpl() throws Exception {
            RaceManagerInstance.this.makeAnnouncement(826);
            RaceManagerInstance.this.makeAnnouncement(825);
            VC++;
            ServerVariables.set("monster_race", VC);
            for (int i = 0; i < 8; ++i) {
                RaceManagerInstance.this.broadcast(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));
            }
        }
    }

    class RunRace
    extends RunnableImpl {
        RunRace() {
        }

        @Override
        public void runImpl() throws Exception {
            packet = new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
            RaceManagerInstance.this.sendMonsterInfo();
            ThreadPoolManager.getInstance().schedule(new RunEnd(), 30000L);
        }
    }

    public class Race {
        private Info[] VI;

        public Race(Info[] infoArray) {
            this.VI = infoArray;
        }

        public Info getLaneInfo(int n) {
            return this.VI[n];
        }

        public class Info {
            private int id;
            private int VJ;
            private int VK;
            private int VL;

            public Info(int n, int n2, int n3, int n4) {
                this.id = n;
                this.VJ = n2;
                this.VK = n3;
                this.VL = n4;
            }

            public int getId() {
                return this.id;
            }

            public int getOdds() {
                return this.VK;
            }

            public int getPayout() {
                return this.VL;
            }

            public int getPlace() {
                return this.VJ;
            }
        }
    }

    class Announcement
    extends RunnableImpl {
        private int type;

        public Announcement(int n) {
            this.type = n;
        }

        @Override
        public void runImpl() throws Exception {
            RaceManagerInstance.this.makeAnnouncement(this.type);
        }
    }
}

