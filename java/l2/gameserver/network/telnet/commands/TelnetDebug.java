/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet.commands;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import org.apache.commons.io.FileUtils;

public class TelnetDebug
implements TelnetCommandHolder {
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetDebug() {
        this.aoa.add(new TelnetCommand("dumpnpc", new String[]{"dnpc"}){

            @Override
            public String getUsage() {
                return "dumpnpc";
            }

            @Override
            public String handle(String[] stringArray) {
                List list;
                StringBuilder stringBuilder = new StringBuilder();
                int n = 0;
                int n2 = 0;
                int n3 = 0;
                TIntObjectHashMap<ArrayList<NpcInstance>> tIntObjectHashMap = new TIntObjectHashMap<ArrayList<NpcInstance>>();
                for (GameObject gameObject : GameObjectsStorage.getAllObjects()) {
                    if (!gameObject.isCreature() || !gameObject.isNpc()) continue;
                    NpcInstance npcInstance = (NpcInstance)gameObject;
                    int n4 = npcInstance.getNpcId();
                    list = (ArrayList<NpcInstance>)tIntObjectHashMap.get(n4);
                    if (list == null) {
                        list = new ArrayList<NpcInstance>();
                        tIntObjectHashMap.put(n4, (ArrayList<NpcInstance>)list);
                    }
                    list.add(npcInstance);
                    if (list.size() > n3) {
                        n2 = n4;
                        n3 = list.size();
                    }
                    ++n;
                }
                stringBuilder.append("Total NPCs: ").append(n).append("\n");
                stringBuilder.append("Maximum NPC ID: ").append(n2).append(" count : ").append(n3).append("\n");
                TIntObjectIterator tIntObjectIterator = tIntObjectHashMap.iterator();
                while (tIntObjectIterator.hasNext()) {
                    tIntObjectIterator.advance();
                    int n5 = tIntObjectIterator.key();
                    list = (List)tIntObjectIterator.value();
                    stringBuilder.append("=== ID: ").append(n5).append(" ").append(" Count: ").append(list.size()).append(" ===").append("\n");
                    for (Object __raw_npcInstance_67 : (Iterable<?>)(Object)(list)) {
                       NpcInstance npcInstance = (NpcInstance)__raw_npcInstance_67;
                        try {
                            stringBuilder.append("AI: ");
                            if (npcInstance.hasAI()) {
                                stringBuilder.append(npcInstance.getAI().getClass().getName());
                            } else {
                                stringBuilder.append("none");
                            }
                            stringBuilder.append(", ");
                            if (npcInstance.getReflectionId() > 0) {
                                stringBuilder.append("ref: ").append(npcInstance.getReflectionId());
                                stringBuilder.append(" - ").append(npcInstance.getReflection().getName());
                            }
                            stringBuilder.append("loc: ").append(npcInstance.getLoc());
                            stringBuilder.append(", ");
                            stringBuilder.append("spawned: ");
                            stringBuilder.append(npcInstance.isVisible());
                            stringBuilder.append("\n");
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                try {
                    new File("stats").mkdir();
                    FileUtils.writeStringToFile(new File("stats/NpcStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), stringBuilder.toString());
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
                return "NPC stats saved.\n";
            }
        });
        this.aoa.add(new TelnetCommand("asrestart"){

            @Override
            public String getUsage() {
                return "asrestart";
            }

            @Override
            public String handle(String[] stringArray) {
                AuthServerCommunication.getInstance().restart();
                return "Restarted.\n";
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }
}

