package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.ai.debug.NpcMovementDebugTracer;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public class AdminNpcDebug implements IAdminCommandHandler {
    private static enum Commands {
        admin_track_npc,
        admin_track_ai
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        if (activeChar == null) {
            return false;
        }

        if (wordList.length > 1) {
            if (wordList[1].equalsIgnoreCase("global")) {
                boolean enable = wordList.length > 2 && wordList[2].equalsIgnoreCase("on");
                NpcMovementDebugTracer.GLOBAL_TRACE = enable;
                activeChar.sendMessage("[NPC Tracer] Global Trace: " + (enable ? "ATIVADO (log/npc_movement_debug.txt)" : "DESATIVADO"));
                return true;
            } else if (wordList[1].equalsIgnoreCase("status")) {
                activeChar.sendMessage("[NPC Tracer] Global: " + NpcMovementDebugTracer.GLOBAL_TRACE);
                return true;
            }
        }

        GameObject target = activeChar.getTarget();
        if (target == null || !target.isNpc()) {
            activeChar.sendMessage("Selecione um NPC no target ou use: //track_npc global on|off");
            return false;
        }

        NpcInstance npc = (NpcInstance) target;
        boolean current = NpcMovementDebugTracer.isTracking(npc);
        boolean next = !current;
        NpcMovementDebugTracer.setTrackNpc(npc.getObjectId(), next);

        activeChar.sendMessage(String.format("[NPC Tracer] NPC [%s / ID:%d / ObjId:%d]: %s",
            npc.getName(), npc.getNpcId(), npc.getObjectId(),
            next ? "RASTREAMENTO ATIVADO em log/npc_movement_debug.txt" : "RASTREAMENTO DESATIVADO"));
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }
}
