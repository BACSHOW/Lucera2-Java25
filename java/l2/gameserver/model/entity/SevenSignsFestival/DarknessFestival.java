/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.SevenSignsFestival;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.SevenSignsFestival.FestivalSpawn;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.instances.FestivalMonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarknessFestival
extends Reflection {
    private static final Logger _log = LoggerFactory.getLogger(DarknessFestival.class);
    public static final int FESTIVAL_LENGTH = 1080000;
    public static final int FESTIVAL_FIRST_SPAWN = 60000;
    public static final int FESTIVAL_SECOND_SPAWN = 540000;
    public static final int FESTIVAL_CHEST_SPAWN = 900000;
    private FestivalSpawn PV;
    private FestivalSpawn PW;
    private int PX = 0;
    private boolean PY = false;
    private final int PZ;
    private final int Qa;
    private Future<?> Qb;

    public DarknessFestival(Party party, int n, int n2) {
        this.onCreate();
        this.setName("Darkness Festival");
        this.setParty(party);
        this.PZ = n2;
        this.Qa = n;
        this.startCollapseTimer(1140000L);
        if (n == 2) {
            this.PV = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_WITCH_SPAWNS[this.PZ]);
            this.PW = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_PLAYER_SPAWNS[this.PZ]);
        } else {
            this.PV = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_WITCH_SPAWNS[this.PZ]);
            this.PW = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_PLAYER_SPAWNS[this.PZ]);
        }
        party.setReflection(this);
        this.setReturnLoc(party.getPartyLeader().getLoc());
        for (Player eventOwner : party.getPartyMembers()) {
            eventOwner.setVar("backCoords", eventOwner.getLoc().toXYZString(), -1L);
            eventOwner.getEffectList().stopAllEffects();
            eventOwner.teleToLocation(Location.findPointToStay(this.PW.loc, 20, 100, this.getGeoIndex()), this);
        }
        this.be();
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(this.PV.npcId);
        try {
            SimpleSpawner exception = new SimpleSpawner(npcTemplate);
            exception.setLoc(this.PV.loc);
            exception.setReflection(this);
            this.addSpawn(exception);
            exception.doSpawn(true);
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        this.t("The festival will begin in 1 minute.");
    }

    private void be() {
        switch (this.PX) {
            case 0: {
                this.PX = 60000;
                this.Qb = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        DarknessFestival.this.spawnFestivalMonsters(60, 0);
                        DarknessFestival.this.t("Go!");
                        DarknessFestival.this.be();
                    }
                }, 60000L);
                break;
            }
            case 60000: {
                this.PX = 540000;
                this.Qb = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        DarknessFestival.this.spawnFestivalMonsters(60, 2);
                        DarknessFestival.this.t("Next wave arrived!");
                        DarknessFestival.this.be();
                    }
                }, 480000L);
                break;
            }
            case 540000: {
                this.PX = 900000;
                this.Qb = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        DarknessFestival.this.spawnFestivalMonsters(60, 3);
                        DarknessFestival.this.t("The chests have spawned! Be quick, the festival will end soon.");
                    }
                }, 360000L);
            }
        }
    }

    public void spawnFestivalMonsters(int n, int n2) {
        int[][] nArray = null;
        switch (n2) {
            case 0: 
            case 1: {
                nArray = this.Qa == 2 ? FestivalSpawn.FESTIVAL_DAWN_PRIMARY_SPAWNS[this.PZ] : FestivalSpawn.FESTIVAL_DUSK_PRIMARY_SPAWNS[this.PZ];
                break;
            }
            case 2: {
                nArray = this.Qa == 2 ? FestivalSpawn.FESTIVAL_DAWN_SECONDARY_SPAWNS[this.PZ] : FestivalSpawn.FESTIVAL_DUSK_SECONDARY_SPAWNS[this.PZ];
                break;
            }
            case 3: {
                int[][] nArray2 = nArray = this.Qa == 2 ? FestivalSpawn.FESTIVAL_DAWN_CHEST_SPAWNS[this.PZ] : FestivalSpawn.FESTIVAL_DUSK_CHEST_SPAWNS[this.PZ];
            }
        }
        if (nArray != null) {
            for (int[] nArray3 : nArray) {
                FestivalSpawn festivalSpawn = new FestivalSpawn(nArray3);
                NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(festivalSpawn.npcId);
                SimpleSpawner simpleSpawner = new SimpleSpawner(npcTemplate);
                simpleSpawner.setReflection(this);
                simpleSpawner.setLoc(festivalSpawn.loc);
                simpleSpawner.setHeading(Rnd.get(65536));
                simpleSpawner.setAmount(1);
                simpleSpawner.setRespawnDelay(n);
                simpleSpawner.startRespawn();
                FestivalMonsterInstance festivalMonsterInstance = (FestivalMonsterInstance)simpleSpawner.doSpawn(true);
                if (n2 == 1) {
                    festivalMonsterInstance.setOfferingBonus(2);
                } else if (n2 == 3) {
                    festivalMonsterInstance.setOfferingBonus(5);
                }
                this.addSpawn(simpleSpawner);
            }
        }
    }

    public boolean increaseChallenge() {
        if (this.PY) {
            return false;
        }
        this.PY = true;
        this.spawnFestivalMonsters(60, 1);
        return true;
    }

    @Override
    public void collapse() {
        if (this.isCollapseStarted()) {
            return;
        }
        if (this.Qb != null) {
            this.Qb.cancel(false);
            this.Qb = null;
        }
        if (SevenSigns.getInstance().getCurrentPeriod() == 1 && this.getParty() != null) {
            long l;
            Player player = this.getParty().getPartyLeader();
            ItemInstance itemInstance = player.getInventory().getItemByItemId(5901);
            long l2 = l = itemInstance == null ? 0L : itemInstance.getCount();
            if (player.getInventory().destroyItem(itemInstance)) {
                long l3 = l * 1L;
                boolean bl = SevenSignsFestival.getInstance().setFinalScore(this.getParty(), this.Qa, this.PZ, l3);
                player.sendPacket((IStaticPacket)new SystemMessage(1267).addNumber(l3));
                this.u("l2p.gameserver.model.entity.SevenSignsFestival.Ended");
                if (bl) {
                    this.t("Your score is highest!");
                }
            } else {
                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2FestivalGuideInstance.BloodOfferings", player, new Object[0]));
            }
        }
        super.collapse();
    }

    private void t(String string) {
        for (Player player : this.getPlayers()) {
            player.sendMessage(string);
        }
    }

    private void u(String string) {
        for (Player player : this.getPlayers()) {
            player.sendMessage(new CustomMessage(string, player, new Object[0]));
        }
    }

    public void partyMemberExited() {
        if (this.getParty() == null || this.getParty().getMemberCount() <= 1) {
            this.collapse();
        }
    }

    @Override
    public boolean canChampions() {
        return true;
    }

    @Override
    public boolean isAutolootForced() {
        return true;
    }
}

