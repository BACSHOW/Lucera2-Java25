/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.lang;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public final class StatsUtils {
    private static final MemoryMXBean qq = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean qr = ManagementFactory.getThreadMXBean();

    public static long getMemUsed() {
        return qq.getHeapMemoryUsage().getUsed();
    }

    public static String getMemUsedMb() {
        return StatsUtils.getMemUsed() / 0x100000L + " Mb";
    }

    public static long getMemMax() {
        return qq.getHeapMemoryUsage().getMax();
    }

    public static String getMemMaxMb() {
        return StatsUtils.getMemMax() / 0x100000L + " Mb";
    }

    public static long getMemFree() {
        MemoryUsage memoryUsage = qq.getHeapMemoryUsage();
        return memoryUsage.getMax() - memoryUsage.getUsed();
    }

    public static String getMemFreeMb() {
        return StatsUtils.getMemFree() / 0x100000L + " Mb";
    }

    public static CharSequence getMemUsage() {
        double d = (double)qq.getHeapMemoryUsage().getMax() / 1024.0;
        double d2 = (double)qq.getHeapMemoryUsage().getCommitted() / 1024.0;
        double d3 = (double)qq.getHeapMemoryUsage().getUsed() / 1024.0;
        double d4 = d - d2;
        double d5 = d2 - d3;
        double d6 = d - d3;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AllowedMemory: ........... ").append((int)d).append(" KB").append("\n");
        stringBuilder.append("     Allocated: .......... ").append((int)d2).append(" KB (").append((double)Math.round(d2 / d * 1000000.0) / 10000.0).append("%)").append("\n");
        stringBuilder.append("     Non-Allocated: ...... ").append((int)d4).append(" KB (").append((double)Math.round(d4 / d * 1000000.0) / 10000.0).append("%)").append("\n");
        stringBuilder.append("AllocatedMemory: ......... ").append((int)d2).append(" KB").append("\n");
        stringBuilder.append("     Used: ............... ").append((int)d3).append(" KB (").append((double)Math.round(d3 / d * 1000000.0) / 10000.0).append("%)").append("\n");
        stringBuilder.append("     Unused (cached): .... ").append((int)d5).append(" KB (").append((double)Math.round(d5 / d * 1000000.0) / 10000.0).append("%)").append("\n");
        stringBuilder.append("UseableMemory: ........... ").append((int)d6).append(" KB (").append((double)Math.round(d6 / d * 1000000.0) / 10000.0).append("%)").append("\n");
        return stringBuilder;
    }

    public static CharSequence getThreadStats() {
        StringBuilder stringBuilder = new StringBuilder();
        int n = qr.getThreadCount();
        int n2 = qr.getThreadCount();
        int n3 = n - n2;
        int n4 = qr.getPeakThreadCount();
        long l = qr.getTotalStartedThreadCount();
        stringBuilder.append("Live: .................... ").append(n).append(" threads").append("\n");
        stringBuilder.append("     Non-Daemon: ......... ").append(n3).append(" threads").append("\n");
        stringBuilder.append("     Daemon: ............. ").append(n2).append(" threads").append("\n");
        stringBuilder.append("Peak: .................... ").append(n4).append(" threads").append("\n");
        stringBuilder.append("Total started: ........... ").append(l).append(" threads").append("\n");
        stringBuilder.append("=================================================").append("\n");
        return stringBuilder;
    }

    public static CharSequence getThreadStats(boolean bl, boolean bl2, boolean bl3) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ThreadInfo threadInfo : qr.dumpAllThreads(bl, bl2)) {
            stringBuilder.append("Thread #").append(threadInfo.getThreadId()).append(" (").append(threadInfo.getThreadName()).append(")").append("\n");
            stringBuilder.append("=================================================\n");
            stringBuilder.append("\tgetThreadState: ...... ").append((Object)threadInfo.getThreadState()).append("\n");
            for (MonitorInfo monitorInfo : threadInfo.getLockedMonitors()) {
                stringBuilder.append("\tLocked monitor: ....... ").append(monitorInfo).append("\n");
                stringBuilder.append("\t\t[").append(monitorInfo.getLockedStackDepth()).append(".]: at ").append(monitorInfo.getLockedStackFrame()).append("\n");
            }
            for (LockInfo lockInfo : threadInfo.getLockedSynchronizers()) {
                stringBuilder.append("\tLocked synchronizer: ...").append(lockInfo).append("\n");
            }
            if (bl3) {
                stringBuilder.append("\tgetStackTace: ..........\n");
                for (StackTraceElement stackTraceElement : threadInfo.getStackTrace()) {
                    stringBuilder.append("\t\tat ").append(stackTraceElement).append("\n");
                }
            }
            stringBuilder.append("=================================================\n");
        }
        return stringBuilder;
    }

    public static CharSequence getGCStats() {
        StringBuilder stringBuilder = new StringBuilder();
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            stringBuilder.append("GarbageCollector (").append(garbageCollectorMXBean.getName()).append(")\n");
            stringBuilder.append("=================================================\n");
            stringBuilder.append("getCollectionCount: ..... ").append(garbageCollectorMXBean.getCollectionCount()).append("\n");
            stringBuilder.append("getCollectionTime: ...... ").append(garbageCollectorMXBean.getCollectionTime()).append(" ms").append("\n");
            stringBuilder.append("=================================================\n");
        }
        return stringBuilder;
    }
}

