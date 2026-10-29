package l2.gameserver.templates;

import java.lang.reflect.Constructor;
import l2.commons.geometry.Polygon;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.DoorAI;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoorTemplate extends CharTemplate {
   private static final Logger _log = LoggerFactory.getLogger(DoorTemplate.class);
   public static final Constructor<DoorAI> DEFAULT_AI_CONSTRUCTOR = (Constructor<DoorAI>)CharacterAI.class.getConstructors()[0];
   private final int _id;
   private final String _name;
   private final DoorTemplate.DoorType arL;
   private final boolean arM;
   private final boolean arN;
   private final boolean arO;
   private final boolean arP;
   private final Polygon arQ;
   private final Location cH;
   private final int arR;
   private final int arS;
   private final int arT;
   private final int arU;
   private final int arV;
   private StatsSet arW;
   private Class<DoorAI> arX = DoorAI.class;
   private Constructor<DoorAI> arY = DEFAULT_AI_CONSTRUCTOR;

   public DoorTemplate(StatsSet var1) {
      super(var1);
      this._id = var1.getInteger("uid");
      this._name = var1.getString("name");
      this.arL = var1.getEnum("door_type", DoorTemplate.DoorType.class, DoorTemplate.DoorType.DOOR);
      this.arM = var1.getBool("unlockable", false);
      this.arN = var1.getBool("show_hp", false);
      this.arO = var1.getBool("opened", false);
      this.arP = var1.getBool("targetable", true);
      this.cH = (Location)var1.get("pos");
      this.arQ = (Polygon)var1.get("shape");
      this.arR = var1.getInteger("key", 0);
      this.arS = var1.getInteger("open_time", 0);
      this.arT = var1.getInteger("random_time", 0);
      this.arU = var1.getInteger("close_time", 0);
      this.arV = var1.getInteger("master_door", 0);
      this.arW = (StatsSet)var1.getObject("ai_params", StatsSet.EMPTY);
      this.B(var1.getString("ai", "DoorAI"));
   }

   private void B(String var1) {
      Object var2 = null;

      try {
         var2 = Class.forName("l2.gameserver.ai." + var1);
      } catch (ClassNotFoundException var4) {
         var2 = Scripts.getInstance().getClasses().get("ai.door." + var1);
      }

      if (var2 == null) {
         _log.error("Not found ai class for ai: " + var1 + ". DoorId: " + this._id);
      } else {
         this.arX = (Class<DoorAI>)var2;
         this.arY = (Constructor<DoorAI>)this.arX.getConstructors()[0];
      }

      if (this.arX.isAnnotationPresent(Deprecated.class)) {
         _log.error("Ai type: " + var1 + ", is deprecated. DoorId: " + this._id);
      }
   }

   public CharacterAI getNewAI(DoorInstance var1) {
      try {
         return this.arY.newInstance(var1);
      } catch (Exception var3) {
         _log.error("Unable to create ai of doorId " + this._id, var3);
         return new DoorAI(var1);
      }
   }

   @Override
   public int getNpcId() {
      return this._id;
   }

   public String getName() {
      return this._name;
   }

   public DoorTemplate.DoorType getDoorType() {
      return this.arL;
   }

   public boolean isUnlockable() {
      return this.arM;
   }

   public boolean isHPVisible() {
      return this.arN;
   }

   public Polygon getPolygon() {
      return this.arQ;
   }

   public int getKey() {
      return this.arR;
   }

   public boolean isOpened() {
      return this.arO;
   }

   public Location getLoc() {
      return this.cH;
   }

   public int getOpenTime() {
      return this.arS;
   }

   public int getRandomTime() {
      return this.arT;
   }

   public int getCloseTime() {
      return this.arU;
   }

   public boolean isTargetable() {
      return this.arP;
   }

   public int getMasterDoor() {
      return this.arV;
   }

   public StatsSet getAIParams() {
      return this.arW;
   }

   public enum DoorType {
      DOOR,
      WALL;
   }
}
