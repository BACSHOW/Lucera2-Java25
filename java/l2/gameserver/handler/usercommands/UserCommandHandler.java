/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands;

import gnu.trove.TIntObjectHashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.handler.usercommands.impl.ClanPenalty;
import l2.gameserver.handler.usercommands.impl.ClanWarsList;
import l2.gameserver.handler.usercommands.impl.CommandChannel;
import l2.gameserver.handler.usercommands.impl.Escape;
import l2.gameserver.handler.usercommands.impl.InstanceZone;
import l2.gameserver.handler.usercommands.impl.LocCommand;
import l2.gameserver.handler.usercommands.impl.OlympiadStat;
import l2.gameserver.handler.usercommands.impl.PartyInfo;
import l2.gameserver.handler.usercommands.impl.Time;

public class UserCommandHandler
extends AbstractHolder {
    private static final UserCommandHandler zw = new UserCommandHandler();
    private TIntObjectHashMap<IUserCommandHandler> zx = new TIntObjectHashMap();

    public static UserCommandHandler getInstance() {
        return zw;
    }

    private UserCommandHandler() {
        this.registerUserCommandHandler(new ClanWarsList());
        this.registerUserCommandHandler(new ClanPenalty());
        this.registerUserCommandHandler(new CommandChannel());
        this.registerUserCommandHandler(new Escape());
        this.registerUserCommandHandler(new LocCommand());
        this.registerUserCommandHandler(new OlympiadStat());
        this.registerUserCommandHandler(new PartyInfo());
        this.registerUserCommandHandler(new InstanceZone());
        this.registerUserCommandHandler(new Time());
    }

    public void registerUserCommandHandler(IUserCommandHandler iUserCommandHandler) {
        int[] nArray;
        for (int n : nArray = iUserCommandHandler.getUserCommandList()) {
            this.zx.put(n, iUserCommandHandler);
        }
    }

    public IUserCommandHandler getUserCommandHandler(int n) {
        return this.zx.get(n);
    }

    @Override
    public int size() {
        return this.zx.size();
    }

    @Override
    public void clear() {
        this.zx.clear();
    }
}

