/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.stats.triggers.TriggerInfo;

public class StatTemplate {
    protected FuncTemplate[] _funcTemplates = FuncTemplate.EMPTY_ARRAY;
    protected List<TriggerInfo> _triggerList = Collections.emptyList();

    public List<TriggerInfo> getTriggerList() {
        return this._triggerList;
    }

    public void addTrigger(TriggerInfo triggerInfo) {
        if (this._triggerList.isEmpty()) {
            this._triggerList = new ArrayList<TriggerInfo>(4);
        }
        this._triggerList.add(triggerInfo);
    }

    public void attachFunc(FuncTemplate funcTemplate) {
        this._funcTemplates = ArrayUtils.add(this._funcTemplates, funcTemplate);
    }

    public FuncTemplate[] getAttachedFuncs() {
        return this._funcTemplates;
    }

    public Func[] getStatFuncs(Object object) {
        if (this._funcTemplates.length == 0) {
            return Func.EMPTY_FUNC_ARRAY;
        }
        Func[] funcArray = new Func[this._funcTemplates.length];
        for (int i = 0; i < funcArray.length; ++i) {
            funcArray[i] = this._funcTemplates[i].getFunc(object);
        }
        return funcArray;
    }
}

