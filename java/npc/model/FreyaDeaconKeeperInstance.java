/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import java.lang.ref.SoftReference;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;

public final class FreyaDeaconKeeperInstance
extends NpcInstance {
    private static final int axB = 8057;
    private static final long axC = 10L;
    private static final int axD = 8379;
    private static final long axE = 3L;
    private static final String axk = "freya_deacon001.htm";
    private static final String axF = "freya_deacon002.htm";
    private static final String axG = "[schuttgart13_mb2314_05m1]";
    private static final String[] axH = new String[]{"[schuttgart13_mb2314_01m1]", "[schuttgart13_mb2314_02m1]", "[schuttgart13_mb2314_03m1]", "[schuttgart13_mb2314_04m1]"};
    private static String axI = "[ice_fairy_sirr_epic]";
    private static final Location axJ = new Location(113533, -126159, -3488);
    private static final Location axK = new Location(115792, -125760, -3373);
    private static final int axL = 4479;
    private static final int axM = 1;
    private static final int axN = 0x16116A1;
    private static final int axO = 23140002;
    private static final int axP = 10025;
    private static final int axQ = 10026;
    private static final int axR = 10027;
    private static final int axS = 11038;
    private static final int axT = 11039;
    private static final int axU = 11040;
    private final Listner axV = new Listner();
    private Future<?> axW;
    private SoftReference<Party> axX;
    private int axY;

    public FreyaDeaconKeeperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!FreyaDeaconKeeperInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("request_entrance_freya_castle")) {
            this.af(player);
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    private synchronized void af(Player player) {
        if (this.dG() == null) {
            Party party = player.getParty();
            if (party == null) {
                this.showChatWindow(player, 6, new Object[0]);
                return;
            }
            if (party.getPartyLeader() != player) {
                this.showChatWindow(player, 2, new Object[0]);
                return;
            }
            for (Player player2 : party.getPartyMembers()) {
                if (player2.getInventory().getCountOf(8057) >= 10L) continue;
                this.showChatWindow(player, 3, "<?name?>", player2.getName());
                return;
            }
            if (!player.isQuestContinuationPossible(true)) {
                this.showChatWindow(player, 4, new Object[0]);
                return;
            }
            for (Player player2 : party.getPartyMembers()) {
                Functions.removeItem(player2, 8057, 10L);
            }
            this.c(party);
            this.showChatWindow(player, 5, new Object[0]);
            Functions.npcShoutCustomMessage(this, "scripts.ice_castle.1121005", new Object[0]);
        } else {
            this.showChatWindow(player, 1, new Object[0]);
        }
    }

    private void clear() {
        this.dF();
        if (this.axX != null) {
            this.axX.clear();
            this.axX = null;
        }
        this.axY = 0;
        if (this.axW != null) {
            this.axW.cancel(false);
            this.axW = null;
        }
    }

    private void dD() {
        Zone zone = ReflectionUtils.getZone(axI);
        if (zone != null) {
            for (Player player : zone.getInsidePlayers()) {
                player.teleToLocation(axK);
            }
        }
    }

    private void dE() {
        for (DoorInstance doorInstance : ReflectionUtils.getDoors(0x16116A1, 23140002)) {
            doorInstance.closeMe();
        }
    }

    private void dF() {
        for (DoorInstance doorInstance : ReflectionUtils.getDoors(0x16116A1, 23140002)) {
            doorInstance.openMe();
        }
    }

    private Party dG() {
        if (this.axX == null) {
            return null;
        }
        return this.axX.get();
    }

    private void dH() {
        for (Spawner spawner : SpawnManager.getInstance().getSpawners(axG)) {
            NpcInstance npcInstance = spawner.doSpawn(true);
            spawner.stopRespawn();
            if (!npcInstance.isMonster()) continue;
            npcInstance.addListener(this.axV);
        }
    }

    private void dI() {
        for (Spawner spawner : SpawnManager.getInstance().getSpawners(axG)) {
            for (NpcInstance npcInstance : spawner.getAllSpawned()) {
                if (!npcInstance.isMonster()) continue;
                npcInstance.removeListener(this.axV);
            }
            spawner.deleteAll();
        }
    }

    private void dJ() {
        for (String string : axH) {
            for (Spawner spawner : SpawnManager.getInstance().getSpawners(string)) {
                spawner.init();
                spawner.stopRespawn();
            }
        }
    }

    private void dK() {
        for (String string : axH) {
            for (Spawner spawner : SpawnManager.getInstance().getSpawners(string)) {
                spawner.deleteAll();
            }
        }
    }

    private void a(int n, String string, Object ... objectArray) {
        Party party = this.dG();
        if (party != null && party.getMemberCount() > 0) {
            for (Player player : this.dG()) {
                CustomMessage customMessage = new CustomMessage(string, player, objectArray);
                ExShowScreenMessage exShowScreenMessage = new ExShowScreenMessage(customMessage.toString(), n, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, false);
                player.sendPacket((IStaticPacket)exShowScreenMessage);
            }
        }
    }

    private void c(Party party) {
        this.axX = new SoftReference<Party>(party);
        this.axY = party.getMemberCount();
        this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1000), 120000L);
        this.a(100000, "scripts.ice_castle.1121000", new Object[0]);
    }

    private class IceCastleRunner
    extends RunnableImpl {
        private final int agd;

        private IceCastleRunner(int n) {
            this.agd = n;
        }

        @Override
        public void runImpl() throws Exception {
            switch (this.agd) {
                case 1000: {
                    Party party = FreyaDeaconKeeperInstance.this.dG();
                    boolean bl = true;
                    if (party == null || party.getMemberCount() != FreyaDeaconKeeperInstance.this.axY) {
                        bl = false;
                    }
                    if (bl && party != null) {
                        for (Player player : party.getPartyMembers()) {
                            if (!(FreyaDeaconKeeperInstance.this.getDistance(player) > 512.0)) continue;
                            bl = false;
                        }
                    }
                    if (!bl) {
                        FreyaDeaconKeeperInstance.this.clear();
                        Functions.npcShoutCustomMessage(FreyaDeaconKeeperInstance.this, "scripts.ice_castle.1121007", new Object[0]);
                        return;
                    }
                    FreyaDeaconKeeperInstance.this.dE();
                    FreyaDeaconKeeperInstance.this.dD();
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1001), 5000L);
                    Functions.teleportParty(party, axJ, 64);
                    break;
                }
                case 1001: {
                    Party party = FreyaDeaconKeeperInstance.this.dG();
                    if (party == null || party.getMemberCount() != FreyaDeaconKeeperInstance.this.axY) {
                        FreyaDeaconKeeperInstance.this.clear();
                        return;
                    }
                    FreyaDeaconKeeperInstance.this.a(10000, "scripts.ice_castle.1121001", new Object[0]);
                    FreyaDeaconKeeperInstance.this.dH();
                    FreyaDeaconKeeperInstance.this.dJ();
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1002), 30000L);
                    break;
                }
                case 1002: {
                    Party party = FreyaDeaconKeeperInstance.this.dG();
                    if (party != null) {
                        Skill skill = SkillTable.getInstance().getInfo(4479, 1);
                        for (Player player : party.getPartyMembers()) {
                            skill.getEffects(player, player, false, false);
                        }
                    }
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1003), 300000L);
                    break;
                }
                case 1003: {
                    FreyaDeaconKeeperInstance.this.a(10000, "scripts.ice_castle.1121008", new Object[0]);
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1004), 600000L);
                    break;
                }
                case 1004: {
                    FreyaDeaconKeeperInstance.this.a(10000, "scripts.ice_castle.1121009", new Object[0]);
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1005), 600000L);
                    break;
                }
                case 1005: {
                    FreyaDeaconKeeperInstance.this.a(10000, "scripts.ice_castle.1121002", new Object[0]);
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1006), 540000L);
                    break;
                }
                case 1006: {
                    FreyaDeaconKeeperInstance.this.dK();
                    FreyaDeaconKeeperInstance.this.dI();
                    FreyaDeaconKeeperInstance.this.dF();
                    FreyaDeaconKeeperInstance.this.a(10000, "scripts.ice_castle.1121003", new Object[0]);
                    FreyaDeaconKeeperInstance.this.clear();
                    break;
                }
                case 1007: {
                    FreyaDeaconKeeperInstance.this.dK();
                    FreyaDeaconKeeperInstance.this.dF();
                    FreyaDeaconKeeperInstance.this.a(10000, "scripts.ice_castle.1121004", new Object[0]);
                    FreyaDeaconKeeperInstance.this.axW = ThreadPoolManager.getInstance().schedule(new IceCastleRunner(1008), 30000L);
                    break;
                }
                case 1008: {
                    FreyaDeaconKeeperInstance.this.dI();
                    FreyaDeaconKeeperInstance.this.clear();
                }
            }
        }
    }

    private class Listner
    implements OnDeathListener {
        private Listner() {
        }

        @Override
        public void onDeath(Creature creature, Creature creature2) {
            if (FreyaDeaconKeeperInstance.this.axW != null) {
                FreyaDeaconKeeperInstance.this.axW.cancel(false);
                FreyaDeaconKeeperInstance.this.axW = null;
            }
            ThreadPoolManager.getInstance().execute(new IceCastleRunner(1007));
        }
    }
}

