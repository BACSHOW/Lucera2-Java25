/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.util.NaturalOrderComparator;
import l2.gameserver.Config;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.DocumentSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillsEngine {
    private static final Logger _log = LoggerFactory.getLogger(SkillsEngine.class);
    private static final SkillsEngine aop = new SkillsEngine();

    public static SkillsEngine getInstance() {
        return aop;
    }

    private SkillsEngine() {
    }

    public List<Skill> loadSkills(File file) {
        if (file == null) {
            _log.warn("SkillsEngine: File not found!");
            return null;
        }
        _log.debug("Loading skills from " + file.getName() + " ...");
        DocumentSkill documentSkill = new DocumentSkill(file);
        documentSkill.parse();
        return documentSkill.getSkills();
    }

    public Map<Integer, Map<Integer, Skill>> loadAllSkills() {
        File file = new File(Config.DATAPACK_ROOT, "data/stats/skills");
        if (!file.exists()) {
            _log.info("Dir " + file.getAbsolutePath() + " not exists");
            return Collections.emptyMap();
        }
        File[] fileArray = file.listFiles(new FileFilter(){

            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".xml");
            }
        });
        Arrays.sort(fileArray, NaturalOrderComparator.FILE_NAME_COMPARATOR);
        HashMap<Integer, Map<Integer, Skill>> hashMap = new HashMap<Integer, Map<Integer, Skill>>();
        int n = 0;
        int n2 = 0;
        for (File file2 : fileArray) {
            List<Skill> list = this.loadSkills(file2);
            if (list == null) continue;
            for (Skill skill : list) {
                int n3 = skill.getId();
                int n4 = skill.getLevel();
                HashMap<Integer, Skill> hashMap2 = (HashMap<Integer, Skill>)hashMap.get(n3);
                if (hashMap2 == null) {
                    hashMap2 = new HashMap<Integer, Skill>();
                    hashMap.put(n3, hashMap2);
                }
                hashMap2.put(n4, skill);
                if (skill.getId() > n) {
                    n = skill.getId();
                }
                if (skill.getLevel() <= n2) continue;
                n2 = skill.getLevel();
            }
        }
        _log.info("SkillsEngine: Loaded " + hashMap.size() + " skill templates from XML files. Max id: " + n + ", max level: " + n2);
        return hashMap;
    }
}

