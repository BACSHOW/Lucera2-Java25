/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.OptionDataTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class OptionDataHolder
extends AbstractHolder {
    private static final OptionDataHolder wB = new OptionDataHolder();
    private IntObjectMap<OptionDataTemplate> wC = new HashIntObjectMap<OptionDataTemplate>();

    public static OptionDataHolder getInstance() {
        return wB;
    }

    public void addTemplate(OptionDataTemplate optionDataTemplate) {
        this.wC.put(optionDataTemplate.getId(), optionDataTemplate);
    }

    public OptionDataTemplate getTemplate(int n) {
        return this.wC.get(n);
    }

    @Override
    public int size() {
        return this.wC.size();
    }

    @Override
    public void clear() {
        this.wC.clear();
    }
}

