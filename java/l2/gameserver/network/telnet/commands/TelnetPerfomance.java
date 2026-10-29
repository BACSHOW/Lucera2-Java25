/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet.commands;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import l2.commons.lang.StatsUtils;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.threading.RunnableStatsManager;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.geodata.PathFindBuffers;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import l2.gameserver.taskmanager.AiTaskManager;
import l2.gameserver.taskmanager.EffectTaskManager;
import org.apache.commons.io.FileUtils;

public class TelnetPerfomance
implements TelnetCommandHolder {
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetPerfomance() {
        this.aoa.add(new TelnetCommand("pool", new String[]{"p"}){

            @Override
            public String getUsage() {
                return "pool [dump]";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                if (stringArray.length == 0 || stringArray[0].isEmpty()) {
                    stringBuilder.append(ThreadPoolManager.getInstance().getStats());
                } else if (stringArray[0].equals("dump") || stringArray[0].equals("d")) {
                    try {
                        new File("stats").mkdir();
                        FileUtils.writeStringToFile(new File("stats/RunnableStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), RunnableStatsManager.getInstance().getStats().toString());
                        stringBuilder.append("Runnable stats saved.\n");
                    }
                    catch (IOException iOException) {
                        stringBuilder.append("Exception: " + iOException.getMessage() + "!\n");
                    }
                } else {
                    return null;
                }
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("mem", new String[]{"m"}){

            @Override
            public String getUsage() {
                return "mem";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(StatsUtils.getMemUsage());
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("heap"){

            @Override
            public String getUsage() {
                return "heap [dump] <live>";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                if (stringArray.length == 0 || stringArray[0].isEmpty()) {
                    return null;
                }
                if (stringArray[0].equals("dump") || stringArray[0].equals("d")) {
                    try {
                        boolean bl = stringArray.length == 2 && !stringArray[1].isEmpty() && (stringArray[1].equals("live") || stringArray[1].equals("l"));
                        new File("dumps").mkdir();
                        String string = "dumps/HeapDump" + (bl ? "Live" : "") + "-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".hprof";
                        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                        mBeanServer.invoke(new ObjectName("com.sun.management:type=HotSpotDiagnostic"), "dumpHeap", new Object[]{string, Boolean.valueOf(bl)}, new String[]{String.class.getName(), "boolean"});
                        stringBuilder.append("Heap dumped.\n");
                    }
                    catch (Exception exception) {
                        stringBuilder.append("Exception: " + exception.getMessage() + "!\n");
                    }
                } else {
                    return null;
                }
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("threads", new String[]{"t"}){

            @Override
            public String getUsage() {
                return "threads [dump]";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                if (stringArray.length == 0 || stringArray[0].isEmpty()) {
                    stringBuilder.append(StatsUtils.getThreadStats());
                } else if (stringArray[0].equals("dump") || stringArray[0].equals("d")) {
                    try {
                        new File("stats").mkdir();
                        FileUtils.writeStringToFile(new File("stats/ThreadsDump-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), StatsUtils.getThreadStats(true, true, true).toString());
                        stringBuilder.append("Threads stats saved.\n");
                    }
                    catch (IOException iOException) {
                        stringBuilder.append("Exception: " + iOException.getMessage() + "!\n");
                    }
                } else {
                    return null;
                }
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("gc"){

            @Override
            public String getUsage() {
                return "gc";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(StatsUtils.getGCStats());
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("net", new String[]{"ns"}){

            @Override
            public String getUsage() {
                return "net";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(SelectorThread.getStats());
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("pathfind", new String[]{"pfs"}){

            @Override
            public String getUsage() {
                return "pathfind";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(PathFindBuffers.getStats());
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("aistats", new String[]{"as"}){

            @Override
            public String getUsage() {
                return "aistats";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < Config.AI_TASK_MANAGER_COUNT; ++i) {
                    stringBuilder.append("AiTaskManager #").append(i + 1).append("\n");
                    stringBuilder.append("=================================================\n");
                    stringBuilder.append(AiTaskManager.getInstance().getStats(i));
                    stringBuilder.append("=================================================\n");
                }
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("effectstats", new String[]{"es"}){

            @Override
            public String getUsage() {
                return "effectstats";
            }

            @Override
            public String handle(String[] stringArray) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < Config.EFFECT_TASK_MANAGER_COUNT; ++i) {
                    stringBuilder.append("EffectTaskManager #").append(i + 1).append("\n");
                    stringBuilder.append("=================================================\n");
                    stringBuilder.append(EffectTaskManager.getInstance().getStats(i));
                    stringBuilder.append("=================================================\n");
                }
                return stringBuilder.toString();
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }
}
