package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.utils.Location;

public class UserInfo extends L2GameServerPacket {
   private boolean ajR = false;
   private boolean anv;
   private int aeh;
   private int aei;
   private int akm;
   private int akn;
   private int aek;
   private int ael;
   private int aem;
   private int aen;
   private int alV;
   private double aja;
   private double ajb;
   private double aev;
   private double aew;
   private int[] aee;
   private Location cH;
   private Location Fo;
   private int afs;
   private int anw;
   private int CV;
   private int sex;
   private int aep;
   private int level;
   private int curCp;
   private int maxCp;
   private int Xh;
   private int anx;
   private long _exp;
   private int curHp;
   private int maxHp;
   private int curMp;
   private int maxMp;
   private int aiE;
   private int aiF;
   private int aiG;
   private int aes;
   private int afh;
   private int afi;
   private int afk;
   private int afg;
   private int afl;
   private int afj;
   private int _sp;
   private int any;
   private int anz;
   private int aiH;
   private int aiI;
   private int aiJ;
   private int aiK;
   private int aiL;
   private int aiM;
   private int aiN;
   private int aiO;
   private int aiP;
   private int aeq;
   private int aer;
   private int aex;
   private int aey;
   private int aez;
   private int aiQ;
   private int aeC;
   private int aeD;
   private int aeF;
   private int aeG;
   private int aeE;
   private int aeL;
   private int anA;
   private int aiV;
   private int aiW;
   private int class_id;
   private int anB;
   private int aeA;
   private int aeB;
   private int aiS;
   private int aiT;
   private int aeU;
   private int aeT;
   private int aiU;
   private int aiY;
   private int aiZ;
   private int aeR;
   private int aiR;
   private int anC;
   private int aeP;
   private String _name;
   private String title;
   private EffectCubic[] aeZ;
   private boolean akg;
   private TeamType _team;

   public UserInfo(Player var1) {
      if (var1.getTransformationName() != null) {
         this._name = var1.getTransformationName();
         this.title = var1.getTransformationTitle() != null ? var1.getTransformationTitle() : "";
         this.aeD = 0;
         this.aeG = 0;
         this.aeE = 0;
         this.aeT = CursedWeaponsManager.getInstance().getLevel(var1.getCursedWeaponEquippedId());
      } else {
         this._name = var1.getName();
         Clan var2 = var1.getClan();
         Alliance var3 = var2 == null ? null : var2.getAlliance();
         this.aeC = var2 == null ? 0 : var2.getClanId();
         this.aeD = var2 == null ? 0 : var2.getCrestId();
         this.aeE = var2 == null ? 0 : var2.getCrestLargeId();
         this.aeF = var3 == null ? 0 : var3.getAllyId();
         this.aeG = var3 == null ? 0 : var3.getAllyCrestId();
         this.aeT = 0;
         this.title = var1.getTitle();
      }

      if (var1.getPlayerAccess().GodMode && var1.isInvisible()) {
         this.title = this.title + "(Invisible)";
      }

      if (var1.isPolymorphed()) {
         if (NpcHolder.getInstance().getTemplate(var1.getPolyId()) != null) {
            this.title = this.title + " - " + NpcHolder.getInstance().getTemplate(var1.getPolyId()).name;
         } else {
            this.title = this.title + " - Polymorphed";
         }
      }

      if (var1.isMounted()) {
         this.Xh = 0;
         this.aeU = var1.getMountNpcId() + 1000000;
         this.aeP = var1.getMountType();
      } else {
         this.Xh = var1.getEnchantEffect();
         this.aeU = 0;
         this.aeP = 0;
      }

      this.anx = var1.getPhysicalAttackRange();
      this.aja = var1.getMovementSpeedMultiplier();
      this.aeh = var1.getTemplate().baseRunSpd;
      this.aei = var1.getTemplate().baseWalkSpd;
      this.aek = 0;
      this.ael = 0;
      if (var1.isFlying()) {
         this.aem = this.aeh;
         this.aen = this.aei;
      } else {
         this.aem = 0;
         this.aen = 0;
      }

      this.akm = var1.getSwimSpeed();
      this.akn = var1.getSwimSpeed();
      this.aee = new int[51];

      for (int var5 : Inventory.PAPERDOLL_ORDER) {
         int baseIdx = var5 * 3;
         this.aee[baseIdx] = var1.getInventory().getPaperdollObjectId(var5);
         this.aee[baseIdx + 1] = var1.getInventory().getPaperdollItemId(var5);
         this.aee[baseIdx + 2] = var1.getInventory().getPaperdollAugmentationId(var5);
      }

      this.alV = var1.isClanLeader() ? 64 : 0;

      for (GlobalEvent var9 : var1.getEvents()) {
         this.alV = var9.getUserRelation(var1, this.alV);
      }

      this.cH = var1.getLoc();
      this.afs = var1.getObjectId();
      this.anw = var1.isInBoat() ? var1.getBoat().getObjectId() : 0;
      this.CV = var1.getRace().ordinal();
      this.sex = var1.getSex();
      this.aep = var1.getBaseClassId();
      this.level = var1.getLevel();
      this._exp = var1.getExp();
      this.afh = var1.getSTR();
      this.afk = var1.getDEX();
      this.afi = var1.getCON();
      this.afg = var1.getINT();
      this.afl = var1.getWIT();
      this.afj = var1.getMEN();
      this.curHp = (int)var1.getCurrentHp();
      this.maxHp = var1.getMaxHp();
      this.curMp = (int)var1.getCurrentMp();
      this.maxMp = var1.getMaxMp();
      this.aiE = var1.getCurrentLoad();
      this.aiF = var1.getMaxLoad();
      this._sp = var1.getIntSp();
      this.aiH = var1.getPAtk(null);
      this.aiI = var1.getPAtkSpd();
      this.aiJ = var1.getPDef(null);
      this.aiK = var1.getEvasionRate(null);
      this.aiL = var1.getAccuracy();
      this.aiM = var1.getCriticalHit(null, null);
      this.aiN = var1.getMAtk(null, null);
      this.aiO = var1.getMAtkSpd();
      this.aiP = var1.getMDef(null, null);
      this.aeq = var1.getPvpFlag();
      this.aer = var1.getKarma();
      this.ajb = var1.getAttackSpeedMultiplier();
      this.aev = var1.getColRadius();
      this.aew = var1.getColHeight();
      this.aex = var1.getHairStyle();
      this.aey = var1.getHairColor();
      this.aez = var1.getFace();
      this.aiQ = !var1.isGM() && !var1.getPlayerAccess().CanUseGMCommand ? 0 : 1;
      this.aeC = var1.getClanId();
      this.aeF = var1.getAllyId();
      this.aeL = var1.getPrivateStoreType();
      this.anA = var1.getSkillLevel(248) > 0 ? 1 : 0;
      this.aiV = var1.getPkKills();
      this.aiW = var1.getPvpKills();
      this.aeZ = var1.getCubics().toArray(new EffectCubic[var1.getCubics().size()]);
      this.aeA = var1.getAbnormalEffect();
      this.aeB = var1.getAbnormalEffect2();
      this.any = var1.getClanPrivileges();
      this.aiG = var1.getGivableRec();
      this.aes = var1.getReceivedRec();
      this.anz = var1.getInventoryLimit();
      this.class_id = var1.getClassId().getId();
      this.maxCp = var1.getMaxCp();
      this.curCp = (int)var1.getCurrentCp();
      this._team = var1.getTeam();
      this.aiS = !var1.isNoble() && (!var1.isGM() || !Config.GM_HERO_AURA) ? 0 : 1;
      this.aiT = !var1.isHero() && (!var1.isGM() || !Config.GM_HERO_AURA) ? 0 : 1;
      this.Fo = var1.getFishLoc();
      this.aiU = var1.getNameColor();
      this.aiY = var1.isRunning() ? 1 : 0;
      this.aiZ = var1.getPledgeClass();
      this.aeR = var1.getPledgeType();
      this.aiR = var1.getTitleColor();
      this.anC = var1.getTransformation();
      this.anB = var1.getAgathionId();
      this.anv = var1.getMatchingRoom() != null
         && var1.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING
         && var1.getMatchingRoom().getLeader() == var1;
      this.akg = var1.isInFlyingTransform();
      this.ajR = true;
   }

   @Override
   protected final void writeImpl() {
      if (this.ajR) {
         this.writeC(4);
         this.writeD(this.cH.x);
         this.writeD(this.cH.y);
         this.writeD(this.cH.z + Config.CLIENT_Z_SHIFT);
         this.writeD(this.cH.h);
         this.writeD(this.afs);
         this.writeS(this._name);
         this.writeD(this.CV);
         this.writeD(this.sex);
         this.writeD(this.aep);
         this.writeD(this.level);
         this.writeQ(this._exp);
         this.writeD(this.afh);
         this.writeD(this.afk);
         this.writeD(this.afi);
         this.writeD(this.afg);
         this.writeD(this.afl);
         this.writeD(this.afj);
         this.writeD(this.maxHp);
         this.writeD(this.curHp);
         this.writeD(this.maxMp);
         this.writeD(this.curMp);
         this.writeD(this._sp);
         this.writeD(this.aiE);
         this.writeD(this.aiF);
         this.writeD(this.anx);

         for (int var4 : Inventory.PAPERDOLL_ORDER) {
            this.writeD(this.aee[var4 * 3]);
         }

         for (int var14 : Inventory.PAPERDOLL_ORDER) {
            this.writeD(this.aee[var14 * 3 + 1]);
         }

         for (int var15 : Inventory.PAPERDOLL_ORDER) {
            this.writeD(this.aee[var15 * 3 + 2]);
         }

         this.writeD(this.aiH);
         this.writeD(this.aiI);
         this.writeD(this.aiJ);
         this.writeD(this.aiK);
         this.writeD(this.aiL);
         this.writeD(this.aiM);
         this.writeD(this.aiN);
         this.writeD(this.aiO);
         this.writeD(this.aiI);
         this.writeD(this.aiP);
         this.writeD(this.aeq);
         this.writeD(this.aer);
         this.writeD(this.aeh);
         this.writeD(this.aei);
         this.writeD(this.akm);
         this.writeD(this.akn);
         this.writeD(this.aek);
         this.writeD(this.ael);
         this.writeD(this.aem);
         this.writeD(this.aen);
         this.writeF(this.aja);
         this.writeF(this.ajb);
         this.writeF(this.aev);
         this.writeF(this.aew);
         this.writeD(this.aex);
         this.writeD(this.aey);
         this.writeD(this.aez);
         this.writeD(this.aiQ);
         this.writeS(this.title);
         this.writeD(this.aeC);
         this.writeD(this.aeD);
         this.writeD(this.aeF);
         this.writeD(this.aeG);
         this.writeD(this.alV);
         this.writeC(this.aeP);
         this.writeC(this.aeL);
         this.writeC(this.anA);
         this.writeD(this.aiV);
         this.writeD(this.aiW);
         this.writeH(this.aeZ.length);

         for (EffectCubic var16 : this.aeZ) {
            this.writeH(var16 == null ? 0 : var16.getId());
         }

         this.writeC(this.anv ? 1 : 0);
         this.writeD(this.aeA);
         this.writeC(this.akg ? 2 : 0);
         this.writeD(this.any);
         this.writeH(this.aiG);
         this.writeH(this.aes);
         this.writeD(this.aeU);
         this.writeH(this.anz);
         this.writeD(this.class_id);
         this.writeD(0);
         this.writeD(this.maxCp);
         this.writeD(this.curCp);
         this.writeC(this.Xh);
         this.writeC(this._team.ordinal());
         this.writeD(this.aeE);
         this.writeC(this.aiS);
         this.writeC(this.aiT);
         this.writeC(0);
         this.writeD(this.Fo.x);
         this.writeD(this.Fo.y);
         this.writeD(this.Fo.z);
         this.writeD(this.aiU);
         this.writeC(this.aiY);
         this.writeD(this.aiZ);
         this.writeD(this.aeR);
         this.writeD(this.aiR);
         this.writeD(this.aeT);
      }
   }
}
