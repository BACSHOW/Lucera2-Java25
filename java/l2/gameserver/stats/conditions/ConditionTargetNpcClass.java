package l2.gameserver.stats.conditions;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.stats.Env;

public class ConditionTargetNpcClass extends Condition {
   private final Class<NpcInstance> aqv;

   public ConditionTargetNpcClass(String var1) {
      Object var2 = null;

      try {
         var2 = Class.forName("l2.gameserver.model.instances." + var1 + "Instance");
      } catch (ClassNotFoundException var4) {
         var2 = Scripts.getInstance().getClasses().get("npc.model." + var1 + "Instance");
      }

      if (var2 == null) {
         throw new IllegalArgumentException("Not found type class for type: " + var1 + ".");
      }

      this.aqv = (Class<NpcInstance>)var2;
   }

   @Override
   protected boolean testImpl(Env var1) {
      return var1.target != null && var1.target.getClass() == this.aqv;
   }
}
