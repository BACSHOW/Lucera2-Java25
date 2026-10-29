package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharInfo extends L2GameServerPacket {
   private static final Logger _log = LoggerFactory.getLogger(CharInfo.class);
   private int[][] aee;
   private int aef;
   private int aeg;
   private int aeh;
   private int aei;
   private int aej;
   private int aek;
   private int ael;
   private int aem;
   private int aen;
   private Location cH;
   private Location Fo;
   private String _name;
   private String _title;
   private int aeo;
   private int CV;
   private int Dd;
   private int aep;
   private int aeq;
   private int aer;
   private int aes;
   private double aet;
   private double aeu;
   private double aev;
   private double aew;
   private int aex;
   private int aey;
   private int aez;
   private int aeA;
   private int aeB;
   private int aeC;
   private int aeD;
   private int aeE;
   private int aeF;
   private int aeG;
   private int class_id;
   private int aeH;
   private int aeI;
   private int aeJ;
   private int aeK;
   private int aeL;
   private int Xh;
   private int aeM;
   private int aeN;
   private int aeO;
   private int aeP;
   private int aeQ;
   private int aeR;
   private int aeS;
   private int aeT;
   private int aeU;
   private int Hm;
   private int aeV;
   private int aeW;
   private int aeX;
   private int aeY;
   private EffectCubic[] aeZ;
   private boolean afa;
   private boolean afb;
   private TeamType _team;
   public static final int[] PAPERDOLL_ORDER = new int[]{0, 6, 7, 8, 9, 10, 11, 12, 13, 7, 15, 16};

   public CharInfo(Player var1) {
      this((Creature)var1);
   }

   public CharInfo(Creature var1) {
      if (var1 == null) {
         System.out.println("CharInfo: cha is null!");
         Thread.dumpStack();
      } else if (!var1.isInvisible()) {
         if (!var1.isDeleted()) {
            Player var2 = var1.getPlayer();
            if (var2 != null) {
               if (this.cH == null) {
                  this.cH = var1.getLoc();
               }

               this.aeo = var1.getObjectId();
               if (var2.getTransformationName() != null || var2.getReflection() == ReflectionManager.GIRAN_HARBOR && var2.getPrivateStoreType() != 0) {
                  this._name = var2.getTransformationName() != null ? var2.getTransformationName() : var2.getName();
                  this._title = var2.getTransformationTitle() != null ? var2.getTransformationTitle() : "";
                  this.aeC = 0;
                  this.aeD = 0;
                  this.aeF = 0;
                  this.aeG = 0;
                  this.aeE = 0;
                  if (var2.isCursedWeaponEquipped()) {
                     this.aeT = CursedWeaponsManager.getInstance().getLevel(var2.getCursedWeaponEquippedId());
                  }
               } else {
                  this._name = var2.getName();
                  if (var2.getPrivateStoreType() != 0) {
                     this._title = "";
                  } else if (!var2.isConnected()) {
                     this._title = var2.getDisconnectedTitle();
                     this.aeV = var2.getDisconnectedTitleColor();
                  } else {
                     this._title = var2.getTitle();
                     this.aeV = var2.getTitleColor();
                  }

                  Clan var3 = var2.getClan();
                  Alliance var4 = var3 == null ? null : var3.getAlliance();
                  this.aeC = var3 == null ? 0 : var3.getClanId();
                  this.aeD = var3 == null ? 0 : var3.getCrestId();
                  this.aeE = var3 == null ? 0 : var3.getCrestLargeId();
                  this.aeF = var4 == null ? 0 : var4.getAllyId();
                  this.aeG = var4 == null ? 0 : var4.getAllyCrestId();
                  this.aeT = 0;
               }

               if (var2.isMounted()) {
                  this.Xh = 0;
                  this.aeU = var2.getMountNpcId() + 1000000;
                  this.aeP = var2.getMountType();
               } else {
                  this.Xh = var2.getEnchantEffect();
                  this.aeU = 0;
                  this.aeP = 0;
               }

               this.aee = new int[17][2];

               for (int var6 : PAPERDOLL_ORDER) {
                  this.aee[var6][0] = var2.getInventory().getPaperdollItemId(var6);
                  this.aee[var6][1] = var2.getInventory().getPaperdollAugmentationId(var6);
               }

               this.aef = var2.getMAtkSpd();
               this.aeg = var2.getPAtkSpd();
               this.aet = var2.getMovementSpeedMultiplier();
               this.aeh = var2.getTemplate().baseRunSpd;
               this.aei = var2.getTemplate().baseWalkSpd;
               this.aek = 0;
               this.ael = 0;
               if (var2.isFlying()) {
                  this.aem = this.aeh;
                  this.aen = this.aei;
               } else {
                  this.aem = 0;
                  this.aen = 0;
               }

               this.aej = var2.getSwimSpeed();
               this.CV = var2.getBaseTemplate().race.ordinal();
               this.Dd = var2.getSex();
               this.aep = var2.getBaseClassId();
               this.aeq = var2.getPvpFlag();
               this.aer = var2.getKarma();
               this.aeu = var2.getAttackSpeedMultiplier();
               this.aev = var2.getColRadius();
               this.aew = var2.getColHeight();
               this.aex = var2.getHairStyle();
               this.aey = var2.getHairColor();
               this.aez = var2.getFace();
               if (this.aeC > 0 && var2.getClan() != null) {
                  this.aeS = var2.getClan().getReputationScore();
               } else {
                  this.aeS = 0;
               }

               this.aeH = var2.isSitting() ? 0 : 1;
               this.aeI = var2.isRunning() ? 1 : 0;
               this.aeJ = var2.isInCombat() ? 1 : 0;
               this.aeK = var2.isAlikeDead() ? 1 : 0;
               this.aeL = var2.isInObserverMode() ? 7 : var2.getPrivateStoreType();
               this.aeZ = var2.getCubics().toArray(new EffectCubic[var2.getCubics().size()]);
               this.aeA = var2.getAbnormalEffect();
               this.aeB = var2.getAbnormalEffect2();
               this.aes = var2.getReceivedRec();
               this.class_id = var2.getClassId().getId();
               this._team = var2.getTeam();
               this.aeM = var2.isNoble() ? 1 : 0;
               this.aeN = !var2.isHero() && (!var2.isGM() || !Config.GM_HERO_AURA) ? 0 : 1;
               this.aeO = var2.isFishing() ? 1 : 0;
               this.Fo = var2.getFishLoc();
               this.Hm = var2.getNameColor();
               this.aeQ = var2.getPledgeClass();
               this.aeR = var2.getPledgeType();
               this.aeW = var2.getTransformation();
               this.aeX = var2.getAgathionId();
               this.afa = var2.getMatchingRoom() != null
                  && var2.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING
                  && var2.getMatchingRoom().getLeader() == var2;
               this.afb = var2.isInFlyingTransform();
            }
         }
      }
   }

   public int getObjectId() { return this.aeo; }

   @Override
   protected final void writeImpl() {
      Player var1 = this.getClient().getActiveChar();
      if (var1 != null) {
         if (this.aeo != 0) {
            if (var1.getObjectId() == this.aeo) {
               _log.error("You cant send CharInfo about his character to active user!!!");
            } else {
               this.writeC(3);
               this.writeD(this.cH.x);
               this.writeD(this.cH.y);
               this.writeD(this.cH.z + Config.CLIENT_Z_SHIFT);
               this.writeD(this.cH.h);
               this.writeD(this.aeo);
               this.writeS(this._name);
               this.writeD(this.CV);
               this.writeD(this.Dd);
               this.writeD(this.aep);

               for (int var5 : PAPERDOLL_ORDER) {
                  this.writeD(this.aee[var5][0]);
               }

               for (int var12 : PAPERDOLL_ORDER) {
                  this.writeD(this.aee[var12][1]);
               }

               this.writeD(this.aeq);
               this.writeD(this.aer);
               this.writeD(this.aef);
               this.writeD(this.aeg);
               this.writeD(this.aeq);
               this.writeD(this.aer);
               this.writeD(this.aeh);
               this.writeD(this.aei);
               this.writeD(this.aej);
               this.writeD(this.aej);
               this.writeD(this.aek);
               this.writeD(this.ael);
               this.writeD(this.aem);
               this.writeD(this.aen);
               this.writeF(this.aet);
               this.writeF(this.aeu);
               this.writeF(this.aev);
               this.writeF(this.aew);
               this.writeD(this.aex);
               this.writeD(this.aey);
               this.writeD(this.aez);
               this.writeS(this._title);
               this.writeD(this.aeC);
               this.writeD(this.aeD);
               this.writeD(this.aeF);
               this.writeD(this.aeG);
               this.writeD(0);
               this.writeC(this.aeH);
               this.writeC(this.aeI);
               this.writeC(this.aeJ);
               this.writeC(this.aeK);
               this.writeC(0);
               this.writeC(this.aeP);
               this.writeC(this.aeL);
               this.writeH(this.aeZ.length);

               for (EffectCubic var13 : this.aeZ) {
                  this.writeH(var13 == null ? 0 : var13.getId());
               }

               this.writeC(this.afa ? 1 : 0);
               this.writeD(this.aeA);
               this.writeC(this.afb ? 2 : 0);
               this.writeH(this.aes);
               this.writeD(this.aeU);
               this.writeD(this.class_id);
               this.writeD(0);
               this.writeC(this.Xh);
               this.writeC(this._team.ordinal());
               this.writeD(this.aeE);
               this.writeC(this.aeM);
               this.writeC(this.aeN);
               this.writeC(this.aeO);
               this.writeD(this.Fo.x);
               this.writeD(this.Fo.y);
               this.writeD(this.Fo.z);
               this.writeD(this.Hm);
               this.writeD(this.cH.h);
               this.writeD(this.aeQ);
               this.writeD(this.aeR);
               this.writeD(this.aeV);
               this.writeD(this.aeT);
               this.writeD(this.aeS);
               this.writeD(this.aeW);
               this.writeD(this.aeX);
               this.writeD(1);
               this.writeD(this.aeB);
            }
         }
      }
   }
}
