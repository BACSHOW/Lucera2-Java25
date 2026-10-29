/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.scripts;

import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.Strings;

public final class Events {
    public static boolean onAction(Player player, GameObject gameObject, boolean bl) {
        if (bl) {
            if (player.getVarB("noShift")) {
                return false;
            }
            Scripts.ScriptClassAndMethod scriptClassAndMethod = Scripts.onActionShift.get(gameObject.getL2ClassShortName());
            if (scriptClassAndMethod == null && gameObject.isNpc()) {
                scriptClassAndMethod = Scripts.onActionShift.get("NpcInstance");
            }
            if (scriptClassAndMethod == null && gameObject.isPet()) {
                scriptClassAndMethod = Scripts.onActionShift.get("PetInstance");
            }
            if (scriptClassAndMethod == null && gameObject.isSummon()) {
                scriptClassAndMethod = Scripts.onActionShift.get("SummonInstance");
            }
            if (scriptClassAndMethod == null) {
                return false;
            }
            return Strings.parseBoolean(Scripts.getInstance().callScripts(player, scriptClassAndMethod.className, scriptClassAndMethod.methodName, new Object[]{player, gameObject}));
        }
        Scripts.ScriptClassAndMethod scriptClassAndMethod = Scripts.onAction.get(gameObject.getL2ClassShortName());
        if (scriptClassAndMethod == null && gameObject.isDoor()) {
            scriptClassAndMethod = Scripts.onAction.get("DoorInstance");
        }
        if (scriptClassAndMethod == null) {
            return false;
        }
        return Strings.parseBoolean(Scripts.getInstance().callScripts(player, scriptClassAndMethod.className, scriptClassAndMethod.methodName, new Object[]{player, gameObject}));
    }
}

