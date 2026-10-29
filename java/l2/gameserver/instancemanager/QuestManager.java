/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.model.quest.Quest;

public class QuestManager {
    public static final int TUTORIAL_QUEST = 255;
    private static Map<String, Quest> AT = new ConcurrentHashMap<String, Quest>();
    private static Map<Integer, Quest> AU = new ConcurrentHashMap<Integer, Quest>();

    public static Quest getQuest(String string) {
        return AT.get(string);
    }

    public static Quest getQuest(Class<?> clazz) {
        return QuestManager.getQuest(clazz.getSimpleName());
    }

    public static Quest getQuest(int n) {
        return AU.get(n);
    }

    public static Quest getQuest2(String string) {
        if (AT.containsKey(string)) {
            return AT.get(string);
        }
        try {
            int n = Integer.valueOf(string);
            return AU.get(n);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static void addQuest(Quest quest) {
        AT.put(quest.getName(), quest);
        AU.put(quest.getQuestIntId(), quest);
    }

    public static Collection<Quest> getQuests() {
        return AT.values();
    }
}

