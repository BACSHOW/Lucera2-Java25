/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import gnu.trove.TIntHashSet;
import java.util.ArrayList;
import java.util.StringTokenizer;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.scripts.Events;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class OlympiadBufferInstance
extends NpcInstance {
    private TIntHashSet UL = new TIntHashSet();

    public OlympiadBufferInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (Events.onAction(player, this, bl)) {
            player.sendActionFailed();
            return;
        }
        if (this != player.getTarget()) {
            player.setTarget(this);
            MyTargetSelected myTargetSelected = new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel());
            player.sendPacket((IStaticPacket)myTargetSelected);
            player.sendPacket((IStaticPacket)new ValidateLocation(this));
        } else {
            MyTargetSelected myTargetSelected = new MyTargetSelected(this.getObjectId(), player.getLevel() - this.getLevel());
            player.sendPacket((IStaticPacket)myTargetSelected);
            if (!this.isInActingRange(player)) {
                if (!player.getAI().isIntendingInteract(this)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                }
            } else if (this.UL.size() > 4) {
                this.showChatWindow(player, 1, new Object[0]);
            } else {
                this.showChatWindow(player, 0, new Object[0]);
            }
            player.sendActionFailed();
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!OlympiadBufferInstance.canBypassCheck(player, this)) {
            return;
        }
        if (this.UL.size() > 4) {
            this.showChatWindow(player, 1, new Object[0]);
        }
        if (string.startsWith("Buff")) {
            int n = 0;
            int n2 = 0;
            StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
            stringTokenizer.nextToken();
            n = Integer.parseInt(stringTokenizer.nextToken());
            n2 = Integer.parseInt(stringTokenizer.nextToken());
            Skill skill = SkillTable.getInstance().getInfo(n, n2);
            ArrayList<Creature> arrayList = new ArrayList<Creature>();
            arrayList.add(player);
            this.broadcastPacket(new MagicSkillUse(this, player, n, n2, 0, 0L));
            this.callSkill(skill, arrayList, true);
            this.UL.add(n);
            if (this.UL.size() > 4) {
                this.showChatWindow(player, 1, new Object[0]);
            } else {
                this.showChatWindow(player, 0, new Object[0]);
            }
        } else {
            this.showChatWindow(player, 0, new Object[0]);
        }
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? "buffer" : "buffer-" + n2;
        return "oly/" + string + ".htm";
    }
}

