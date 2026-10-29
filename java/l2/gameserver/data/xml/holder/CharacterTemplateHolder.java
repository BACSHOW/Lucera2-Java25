/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.templates.PlayerTemplate;

public class CharacterTemplateHolder
extends AbstractHolder {
    private static final CharacterTemplateHolder vJ = new CharacterTemplateHolder();
    private final Map<ClassId, PlayerTemplate> vK = new HashMap<ClassId, PlayerTemplate>();
    private final Map<ClassId, PlayerTemplate> vL = new HashMap<ClassId, PlayerTemplate>();

    public static final CharacterTemplateHolder getInstance() {
        return vJ;
    }

    public void addTemplate(PlayerTemplate playerTemplate, boolean bl) {
        if (bl) {
            this.vK.put(playerTemplate.classId, playerTemplate);
        } else {
            this.vL.put(playerTemplate.classId, playerTemplate);
        }
    }

    public PlayerTemplate getTemplate(ClassId classId, boolean bl) {
        return bl ? this.vK.get((Object)classId) : this.vL.get((Object)classId);
    }

    @Override
    public int size() {
        return this.vK.size() + this.vL.size();
    }

    @Override
    public void clear() {
        this.vK.clear();
        this.vL.clear();
    }
}

