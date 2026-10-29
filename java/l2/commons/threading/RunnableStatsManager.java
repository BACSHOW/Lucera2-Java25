/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.threading;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class RunnableStatsManager {
    private static final RunnableStatsManager sb = new RunnableStatsManager();
    private final Map<Class<?>, ClassStat> sc = new HashMap();
    private final Lock lock = new ReentrantLock();

    public static final RunnableStatsManager getInstance() {
        return sb;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handleStats(Class<?> clazz, long l) {
        try {
            this.lock.lock();
            ClassStat classStat = this.sc.get(clazz);
            if (classStat == null) {
                classStat = new ClassStat(clazz);
            }
            classStat.sd++;
            ClassStat classStat2 = classStat;
            classStat2.se = classStat2.se + l;
            if (classStat.sf > l) {
                classStat.sf = l;
            }
            if (classStat.sg < l) {
                classStat.sg = l;
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private List<ClassStat> aZ() {
        List<ClassStat> list = Collections.emptyList();
        try {
            this.lock.lock();
            list = Arrays.asList(this.sc.values().toArray(new ClassStat[this.sc.size()]));
        }
        finally {
            this.lock.unlock();
        }
        Collections.sort(list, new Comparator<ClassStat>(){

            @Override
            public int compare(ClassStat classStat, ClassStat classStat2) {
                if (classStat.sg < classStat2.sg) {
                    return 1;
                }
                if (classStat.sg == classStat2.sg) {
                    return 0;
                }
                return -1;
            }
        });
        return list;
    }

    public CharSequence getStats() {
        StringBuilder stringBuilder = new StringBuilder();
        List<ClassStat> list = this.aZ();
        for (ClassStat classStat : list) {
            stringBuilder.append(classStat.clazz.getName()).append(":\n");
            stringBuilder.append("\tRun: ............ ").append(classStat.sd).append("\n");
            stringBuilder.append("\tTime: ........... ").append(classStat.se).append("\n");
            stringBuilder.append("\tMin: ............ ").append(classStat.sf).append("\n");
            stringBuilder.append("\tMax: ............ ").append(classStat.sg).append("\n");
            stringBuilder.append("\tAverage: ........ ").append(classStat.se / classStat.sd).append("\n");
        }
        return stringBuilder;
    }

    private class ClassStat {
        private final Class<?> clazz;
        private long sd = 0L;
        private long se = 0L;
        private long sf = Long.MAX_VALUE;
        private long sg = Long.MIN_VALUE;

        private ClassStat(Class<?> clazz) {
            this.clazz = clazz;
            RunnableStatsManager.this.sc.put(clazz, this);
        }
    }
}

