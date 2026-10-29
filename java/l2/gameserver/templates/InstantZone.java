package l2.gameserver.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.util.Rnd;
import l2.gameserver.dao.InstanceReuseDAO;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Territory;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;

public class InstantZone {
   private static final String asq = "hwidLimit";
   private final int _id;
   private final String _name;
   private final SchedulingPattern asr;
   private final int ass;
   private final int ast;
   private final int p;
   private final int s;
   private final int asu;
   private final int asv;
   private final boolean asw;
   private final int _timer;
   private final List<Location> asx;
   private final Location asy;
   private final int asz;
   private final int asA;
   private final IntObjectMap<InstantZone.DoorInfo> _doors;
   private final Map<String, InstantZone.ZoneInfo> _zones;
   private final Map<String, InstantZone.SpawnInfo2> wX;
   private final List<InstantZone.SpawnInfo> asB;
   private final int asC;
   private final int asD;
   private final int asE;
   private final int asF;
   private final boolean asG;
   private final int asH;
   private final int asI;
   private final int asJ;
   private final boolean asK;
   private final StatsSet asL;
   private final InstantZoneEntryType asM;
   private final boolean asN;

   private InstantZone(
      int var1,
      String var2,
      SchedulingPattern var3,
      int var4,
      int var5,
      boolean var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      boolean var12,
      List<Location> var13,
      Location var14,
      int var15,
      int var16,
      IntObjectMap<InstantZone.DoorInfo> var17,
      Map<String, InstantZone.ZoneInfo> var18,
      Map<String, InstantZone.SpawnInfo2> var19,
      List<InstantZone.SpawnInfo> var20,
      int var21,
      int var22,
      int var23,
      int var24,
      boolean var25,
      int var26,
      int var27,
      int var28,
      boolean var29,
      StatsSet var30
   ) {
      this._id = var1;
      this._name = var2;
      this.asr = var3;
      this.ass = var4;
      this.ast = var5;
      this.asN = var6;
      this.p = var7;
      this.s = var8;
      this.asx = var13;
      this.asy = var14;
      this.asu = var9;
      this.asv = var10;
      this.asw = var12;
      this._timer = var11;
      this.asz = var15;
      this.asA = var16;
      this._doors = var17;
      this._zones = var18;
      this.asB = var20;
      this.wX = var19;
      this.asC = var21;
      this.asD = var22;
      this.asE = var23;
      this.asF = var24;
      this.asG = var25;
      this.asH = var26;
      this.asI = var27;
      this.asJ = var28;
      this.asK = var29;
      this.asL = var30;
      if (this.getMinParty() == 1) {
         this.asM = InstantZoneEntryType.SOLO;
      } else if (this.getMinParty() > 1 && this.getMaxParty() <= 9) {
         this.asM = InstantZoneEntryType.PARTY;
      } else {
         if (this.getMaxParty() <= 9) {
            throw new IllegalArgumentException("Invalid type?: " + this._name);
         }

         this.asM = InstantZoneEntryType.COMMAND_CHANNEL;
      }
   }

   public int getId() {
      return this._id;
   }

   public String getName() {
      return this._name;
   }

   public SchedulingPattern getResetReuse() {
      return this.asr;
   }

   public boolean isDispelBuffs() {
      return this.asN;
   }

   public int getTimelimit() {
      return this.ast;
   }

   public int getMinLevel() {
      return this.p;
   }

   public int getMaxLevel() {
      return this.s;
   }

   public int getMinParty() {
      return this.asu;
   }

   public int getMaxParty() {
      return this.asv;
   }

   public int getTimerOnCollapse() {
      return this._timer;
   }

   public boolean isCollapseOnPartyDismiss() {
      return this.asw;
   }

   public Location getTeleportCoord() {
      return this.asx.size() == 1 ? this.asx.get(0) : this.asx.get(Rnd.get(this.asx.size()));
   }

   public Location getReturnCoords() {
      return this.asy;
   }

   public int getMapX() {
      return this.asz;
   }

   public int getMapY() {
      return this.asA;
   }

   public List<InstantZone.SpawnInfo> getSpawnsInfo() {
      return this.asB;
   }

   public int getSharedReuseGroup() {
      return this.ass;
   }

   public int getCollapseIfEmpty() {
      return this.asC;
   }

   public int getRemovedItemId() {
      return this.asE;
   }

   public int getRemovedItemCount() {
      return this.asF;
   }

   public boolean getRemovedItemNecessity() {
      return this.asG;
   }

   public int getGiveItemId() {
      return this.asH;
   }

   public int getGiveItemCount() {
      return this.asI;
   }

   public int getRequiredQuestId() {
      return this.asJ;
   }

   public boolean getSetReuseUponEntry() {
      return this.asK;
   }

   public int getMaxChannels() {
      return this.asD;
   }

   public InstantZoneEntryType getEntryType() {
      return this.asM;
   }

   public IntObjectMap<InstantZone.DoorInfo> getDoors() {
      return this._doors;
   }

   public Map<String, InstantZone.ZoneInfo> getZones() {
      return this._zones;
   }

   public List<Location> getTeleportCoords() {
      return this.asx;
   }

   public Map<String, InstantZone.SpawnInfo2> getSpawns() {
      return this.wX;
   }

   public StatsSet getAddParams() {
      return this.asL;
   }

   public boolean isHwidLimited() {
      return this.getAddParams().isSet("hwidLimit") && this.getAddParams().getBool("hwidLimit");
   }

   public int getMinutesToNextEntrance(Player var1) {
      SchedulingPattern var2 = InstantZoneHolder.getInstance().getResetReuseById(this.getId());
      if (var2 == null) {
         return 0;
      }

      Long var3 = null;
      if (InstantZoneHolder.getInstance().getSharedReuseInstanceIds(this.getId()) != null
         && !InstantZoneHolder.getInstance().getSharedReuseInstanceIds(this.getId()).isEmpty()) {
         ArrayList var4 = new ArrayList();

         for (int var6 : InstantZoneHolder.getInstance().getSharedReuseInstanceIds(this.getId())) {
            if (var1.getInstanceReuse(var6) != null) {
               var4.add(var1.getInstanceReuse(var6));
            }
         }

         if (!var4.isEmpty()) {
            Collections.sort(var4);
            var3 = (Long)var4.get(var4.size() - 1);
         }
      } else {
         var3 = var1.getInstanceReuse(this.getId());
      }

      String var7;
      if (this.isHwidLimited() && var1.getNetConnection() != null && (var7 = var1.getNetConnection().getHwid()) != null && !StringUtils.isBlank(var7)) {
         Long var8 = InstanceReuseDAO.getInstance().getHwidReuse(this.getId(), var7);
         if (var8 > 0L) {
            if (var3 == null) {
               var3 = var8;
            } else if (var8 > var3) {
               var3 = var8;
            }
         }
      }

      return var3 == null ? 0 : (int)Math.max((var2.next(var3) - System.currentTimeMillis()) / 60000L, 0L);
   }

   public static class Builder implements org.apache.commons.lang3.builder.Builder<InstantZone> {
      private int asO;
      private String name;
      private SchedulingPattern asP = new SchedulingPattern("30 6 * * *");
      private int asQ = -1;
      private int asR = 60;
      private int asS = -1;
      private int asT = -1;
      private boolean asU = false;
      private boolean asV = true;
      private int asW;
      private int asX;
      private int asY;
      private int count;
      private int asZ = 0;
      private int ata = 0;
      private int atb = 0;
      private InstantZone.SpawnInfo atc = null;
      private int atd = 0;
      private int ate = 0;
      private int atf = 0;
      private int atg = 0;
      private int ath = 0;
      private int ati = 20;
      private boolean atj = false;
      private boolean atk = true;
      private StatsSet atl = new StatsSet();
      private int atm = 0;
      private int maxLevel = 0;
      private int atn = 1;
      private int ato = 9;
      private List<Location> atp = new ArrayList<>();
      private Location atq;
      private List<InstantZone.SpawnInfo> spawns = new ArrayList<>();
      private IntObjectMap<InstantZone.DoorInfo> atr = Containers.emptyIntObjectMap();
      private Map<String, InstantZone.ZoneInfo> ats = Collections.emptyMap();
      private Map<String, InstantZone.SpawnInfo2> att = Collections.emptyMap();

      public int getInstanceId() {
         return this.asO;
      }

      public InstantZone.Builder setInstanceId(int var1) {
         this.asO = var1;
         return this;
      }

      public String getName() {
         return this.name;
      }

      public InstantZone.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public SchedulingPattern getResetReuse() {
         return this.asP;
      }

      public InstantZone.Builder setResetReuse(SchedulingPattern var1) {
         this.asP = var1;
         return this;
      }

      public int getTimelimit() {
         return this.asQ;
      }

      public InstantZone.Builder setTimelimit(int var1) {
         this.asQ = var1;
         return this;
      }

      public int getTimer() {
         return this.asR;
      }

      public InstantZone.Builder setTimer(int var1) {
         this.asR = var1;
         return this;
      }

      public int getMapx() {
         return this.asS;
      }

      public InstantZone.Builder setMapx(int var1) {
         this.asS = var1;
         return this;
      }

      public int getMapy() {
         return this.asT;
      }

      public InstantZone.Builder setMapy(int var1) {
         this.asT = var1;
         return this;
      }

      public IntObjectMap<InstantZone.DoorInfo> getDoors() {
         return this.atr;
      }

      public InstantZone.Builder setDoors(IntObjectMap<InstantZone.DoorInfo> var1) {
         this.atr = var1;
         return this;
      }

      public Map<String, InstantZone.ZoneInfo> getZones() {
         return this.ats;
      }

      public InstantZone.Builder setZones(Map<String, InstantZone.ZoneInfo> var1) {
         this.ats = var1;
         return this;
      }

      public Map<String, InstantZone.SpawnInfo2> getSpawns2() {
         return this.att;
      }

      public InstantZone.Builder setSpawns2(Map<String, InstantZone.SpawnInfo2> var1) {
         this.att = var1;
         return this;
      }

      public List<InstantZone.SpawnInfo> getSpawns() {
         return this.spawns;
      }

      public InstantZone.Builder setSpawns(List<InstantZone.SpawnInfo> var1) {
         this.spawns = var1;
         return this;
      }

      public boolean isDispelBuffs() {
         return this.asU;
      }

      public InstantZone.Builder setDispelBuffs(boolean var1) {
         this.asU = var1;
         return this;
      }

      public boolean isOnPartyDismiss() {
         return this.asV;
      }

      public InstantZone.Builder setOnPartyDismiss(boolean var1) {
         this.asV = var1;
         return this;
      }

      public List<Location> getTeleportLocs() {
         return this.atp;
      }

      public InstantZone.Builder setTeleportLocs(List<Location> var1) {
         this.atp = var1;
         return this;
      }

      public InstantZone.Builder addTeleportLocs(List<Location> var1) {
         this.atp.addAll(var1);
         return this;
      }

      public int getMobId() {
         return this.asW;
      }

      public InstantZone.Builder setMobId(int var1) {
         this.asW = var1;
         return this;
      }

      public int getRespawn() {
         return this.asX;
      }

      public InstantZone.Builder setRespawn(int var1) {
         this.asX = var1;
         return this;
      }

      public int getRespawnRnd() {
         return this.asY;
      }

      public InstantZone.Builder setRespawnRnd(int var1) {
         this.asY = var1;
         return this;
      }

      public int getCount() {
         return this.count;
      }

      public InstantZone.Builder setCount(int var1) {
         this.count = var1;
         return this;
      }

      public int getSharedReuseGroup() {
         return this.asZ;
      }

      public InstantZone.Builder setSharedReuseGroup(int var1) {
         this.asZ = var1;
         return this;
      }

      public int getCollapseIfEmpty() {
         return this.ata;
      }

      public InstantZone.Builder setCollapseIfEmpty(int var1) {
         this.ata = var1;
         return this;
      }

      public int getSpawnType() {
         return this.atb;
      }

      public InstantZone.Builder setSpawnType(int var1) {
         this.atb = var1;
         return this;
      }

      public InstantZone.SpawnInfo getSpawnDat() {
         return this.atc;
      }

      public InstantZone.Builder setSpawnDat(InstantZone.SpawnInfo var1) {
         this.atc = var1;
         return this;
      }

      public int getRemovedItemId() {
         return this.atd;
      }

      public InstantZone.Builder setRemovedItemId(int var1) {
         this.atd = var1;
         return this;
      }

      public int getRemovedItemCount() {
         return this.ate;
      }

      public InstantZone.Builder setRemovedItemCount(int var1) {
         this.ate = var1;
         return this;
      }

      public int getGiveItemId() {
         return this.atf;
      }

      public InstantZone.Builder setGiveItemId(int var1) {
         this.atf = var1;
         return this;
      }

      public int getGivedItemCount() {
         return this.atg;
      }

      public InstantZone.Builder setGivedItemCount(int var1) {
         this.atg = var1;
         return this;
      }

      public int getRequiredQuestId() {
         return this.ath;
      }

      public InstantZone.Builder setRequiredQuestId(int var1) {
         this.ath = var1;
         return this;
      }

      public int getMaxChannels() {
         return this.ati;
      }

      public InstantZone.Builder setMaxChannels(int var1) {
         this.ati = var1;
         return this;
      }

      public boolean isRemovedItemNecessity() {
         return this.atj;
      }

      public InstantZone.Builder setRemovedItemNecessity(boolean var1) {
         this.atj = var1;
         return this;
      }

      public boolean isSetReuseUponEntry() {
         return this.atk;
      }

      public InstantZone.Builder setSetReuseUponEntry(boolean var1) {
         this.atk = var1;
         return this;
      }

      public StatsSet getParams() {
         return this.atl;
      }

      public InstantZone.Builder setParams(StatsSet var1) {
         this.atl = var1;
         return this;
      }

      public int getMinLevel() {
         return this.atm;
      }

      public InstantZone.Builder setMinLevel(int var1) {
         this.atm = var1;
         return this;
      }

      public int getMaxLevel() {
         return this.maxLevel;
      }

      public InstantZone.Builder setMaxLevel(int var1) {
         this.maxLevel = var1;
         return this;
      }

      public int getMinParty() {
         return this.atn;
      }

      public InstantZone.Builder setMinParty(int var1) {
         this.atn = var1;
         return this;
      }

      public int getMaxParty() {
         return this.ato;
      }

      public InstantZone.Builder setMaxParty(int var1) {
         this.ato = var1;
         return this;
      }

      public Location getRet() {
         return this.atq;
      }

      public InstantZone.Builder setRet(Location var1) {
         this.atq = var1;
         return this;
      }

      public InstantZone build() {
         return new InstantZone(
            this.asO,
            this.name,
            this.asP,
            this.asZ,
            this.asQ,
            this.asU,
            this.atm,
            this.maxLevel,
            this.atn,
            this.ato,
            this.asR,
            this.asV,
            this.atp,
            this.atq,
            this.asS,
            this.asT,
            this.atr,
            this.ats,
            this.att,
            this.spawns,
            this.ata,
            this.ati,
            this.atd,
            this.ate,
            this.atj,
            this.atf,
            this.atg,
            this.ath,
            this.atk,
            this.atl
         );
      }
   }

   public static class DoorInfo {
      private final DoorTemplate atu;
      private final boolean arO;
      private final boolean atv;

      public DoorInfo(DoorTemplate var1, boolean var2, boolean var3) {
         this.atu = var1;
         this.arO = var2;
         this.atv = var3;
      }

      public DoorTemplate getTemplate() {
         return this.atu;
      }

      public boolean isOpened() {
         return this.arO;
      }

      public boolean isInvul() {
         return this.atv;
      }
   }

   @Deprecated
   public static class SpawnInfo {
      private final int atw;
      private final int _npcId;
      private final int _count;
      private final int atx;
      private final int aty;
      private final List<Location> atz;
      private final Territory Au;

      public SpawnInfo(int var1, int var2, int var3, int var4, int var5, Territory var6) {
         this(var1, var2, var3, var4, var5, null, var6);
      }

      public SpawnInfo(int var1, int var2, int var3, int var4, int var5, List<Location> var6) {
         this(var1, var2, var3, var4, var5, var6, null);
      }

      public SpawnInfo(int var1, int var2, int var3, int var4, int var5, List<Location> var6, Territory var7) {
         this.atw = var1;
         this._npcId = var2;
         this._count = var3;
         this.atx = var4;
         this.aty = var5;
         this.atz = var6;
         this.Au = var7;
      }

      public int getSpawnType() {
         return this.atw;
      }

      public int getNpcId() {
         return this._npcId;
      }

      public int getCount() {
         return this._count;
      }

      public int getRespawnDelay() {
         return this.atx;
      }

      public int getRespawnRnd() {
         return this.aty;
      }

      public List<Location> getCoords() {
         return this.atz;
      }

      public Territory getLoc() {
         return this.Au;
      }
   }

   public static class SpawnInfo2 {
      private final List<SpawnTemplate> atA;
      private final boolean cs;

      public SpawnInfo2(List<SpawnTemplate> var1, boolean var2) {
         this.atA = var1;
         this.cs = var2;
      }

      public List<SpawnTemplate> getTemplates() {
         return this.atA;
      }

      public boolean isSpawned() {
         return this.cs;
      }
   }

   public static class ZoneInfo {
      private final ZoneTemplate Mg;
      private final boolean _active;

      public ZoneInfo(ZoneTemplate var1, boolean var2) {
         this.Mg = var1;
         this._active = var2;
      }

      public ZoneTemplate getTemplate() {
         return this.Mg;
      }

      public boolean isActive() {
         return this._active;
      }
   }
}
