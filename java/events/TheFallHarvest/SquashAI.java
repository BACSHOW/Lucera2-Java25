/*
 * Decompiled with CFR 0.152.
 */
package events.TheFallHarvest;

import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Log;
import npc.model.SquashInstance;

public class SquashAI
extends Fighter {
    public static final int Young_Squash = 12774;
    public static final int High_Quality_Squash = 12775;
    public static final int Low_Quality_Squash = 12776;
    public static final int Large_Young_Squash = 12777;
    public static final int High_Quality_Large_Squash = 12778;
    public static final int Low_Quality_Large_Squash = 12779;
    public static final int King_Squash = 13016;
    public static final int Emperor_Squash = 13017;
    public static final int Squash_Level_up = 4513;
    public static final int Squash_Poisoned = 4514;
    private static final String[] iw = new String[]{"scripts.events.TheFallHarvest.SquashAI.textOnSpawn.0", "scripts.events.TheFallHarvest.SquashAI.textOnSpawn.1", "scripts.events.TheFallHarvest.SquashAI.textOnSpawn.2"};
    private static final String[] ix = new String[]{"Bites rat-a-tat... to change... body...!", "Ha ha, grew up! Completely on all!", "Cannot to aim all? Had a look all to flow out...", "Is that also calculated hit? Look for person which has the strength!", "Don't waste your time!", "Ha, this sound is really pleasant to hear?", "I eat your attack to grow!", "Time to hit again! Come again!", "Only useful music can open big pumpkin... It can not be opened with weapon!"};
    private static final String[] iy = new String[]{"heh heh,looks well hit!", "yo yo? Your skill is mediocre?", "Time to hit again! Come again!", "I eat your attack to grow!", "Make an effort... to get down like this, I walked...", "What is this kind of degree to want to open me? Really is indulges in fantasy!", "Good fighting method. Evidently flies away the fly also can overcome.", "Strives to excel strength oh! But waste your time..."};
    private static final String[] iz = new String[]{"The lovely pumpkin young fruit start to glisten when taken to the threshing ground! From now on will be able to grow healthy and strong!", "Oh, Haven't seen for a long time?", "Suddenly, thought as soon as to see my beautiful appearance?", "Well! This is something! Is the nectar?", "Refuels! Drink 5 bottles to be able to grow into the big pumpkin oh!"};
    private static final String[] iA = new String[]{"If I drink nectar, I can grow up faster!", "Come, believe me, sprinkle a nectar! I can certainly turn the big pumpkin!!!", "Take nectar to come, pumpkin nectar!"};
    private static final String[] iB = new String[]{"Wish the big pumpkin!", "completely became the recreation area! Really good!", "Guessed I am mature or am rotten?", "Nectar is just the best! Ha! Ha! Ha!"};
    private static final String[] iC = new String[]{"oh! Randomly missed! Too quickly sprinkles the nectar?", "If I die like this, you only could get young pumpkin...", "Cultivate a bit faster! The good speech becomes the big pumpkin, the young pumpkin is not good!", "The such small pumpkin you all must eat? Bring the nectar, I can be bigger!"};
    private static final String[] iD = new String[]{"Young pumpkin wishing! Has how already grown up?", "Already grew up! Quickly sneaked off...", "Graciousness, is very good. Come again to see, now felt more and more well"};
    private static final String[] iE = new String[]{"Hey! Was not there! Here is! Here! Not because I can not properly care? Small!", "Wow, stops? Like this got down to have to thank", "Hungry for a nectar oh...", "Do you want the big pumpkin? But I like young pumpkin..."};
    private static final String[] iF = new String[]{"Big pumpkin wishing! Ask, to sober!", "Rumble rumble... it's really tasty! Hasn't it?", "Cultivating me just to eat? Good, is casual your... not to give the manna on the suicide!"};
    private static final String[] iG = new String[]{"Isn't it the water you add? What flavor?", "Master, rescue my... I don't have the nectar flavor, I must die..."};
    private static final String[] iH = new String[]{"is very good, does extremely well! Knew what next step should make?", "If you catch me, I give you 10 million adena!!! Agree?"};
    private static final String[] iI = new String[]{"Hungry for a nectar oh...", "If I drink nectar, I can grow up faster!"};
    private int _npcId = this.getActor().getNpcId();
    private int iJ;
    private int iK;
    private long iL;
    private long iM;
    private ScheduledFuture<?> iN;
    private static int iO = 3000;

    public SquashAI(NpcInstance npcInstance) {
        super(npcInstance);
        Functions.npcSayInRangeCustomMessage((NpcInstance)this.getActor(), 128, iw[Rnd.get(iw.length)], new Object[0]);
        this.iM = System.currentTimeMillis() + 120000L;
    }

    @Override
    protected boolean thinkActive() {
        if (System.currentTimeMillis() > this.iM) {
            SquashInstance squashInstance;
            this.iM = Long.MAX_VALUE;
            if (this.iN != null) {
                this.iN.cancel(false);
                this.iN = null;
            }
            if ((squashInstance = this.getActor()) != null) {
                squashInstance.deleteMe();
            }
        }
        return false;
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature creature) {
        SquashInstance squashInstance = this.getActor();
        if (squashInstance == null || skill.getId() != 2005) {
            return;
        }
        switch (this.iK) {
            case 0: {
                ++this.iK;
                this.iL = System.currentTimeMillis();
                if (Rnd.chance(50)) {
                    ++this.iJ;
                    Functions.npcSayInRange(squashInstance, iz[Rnd.get(iz.length)], 128);
                    squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4513, 1, iO, 0L));
                    break;
                }
                Functions.npcSayInRange(squashInstance, iA[Rnd.get(iA.length)], 128);
                squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4514, 1, iO, 0L));
                break;
            }
            case 1: {
                if (System.currentTimeMillis() - this.iL < (long)iO) {
                    Functions.npcSayInRange(squashInstance, iy[Rnd.get(iy.length)], 128);
                    return;
                }
                ++this.iK;
                this.iL = System.currentTimeMillis();
                if (Rnd.chance(50)) {
                    ++this.iJ;
                    Functions.npcSayInRange(squashInstance, iB[Rnd.get(iB.length)], 128);
                    squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4513, 1, iO, 0L));
                    break;
                }
                Functions.npcSayInRange(squashInstance, iC[Rnd.get(iC.length)], 128);
                squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4514, 1, iO, 0L));
                break;
            }
            case 2: {
                if (System.currentTimeMillis() - this.iL < (long)iO) {
                    Functions.npcSayInRange(squashInstance, iy[Rnd.get(iy.length)], 128);
                    return;
                }
                ++this.iK;
                this.iL = System.currentTimeMillis();
                if (Rnd.chance(50)) {
                    ++this.iJ;
                    Functions.npcSayInRange(squashInstance, iD[Rnd.get(iD.length)], 128);
                    squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4513, 1, iO, 0L));
                    break;
                }
                Functions.npcSayInRange(squashInstance, iE[Rnd.get(iE.length)], 128);
                squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4514, 1, iO, 0L));
                break;
            }
            case 3: {
                if (System.currentTimeMillis() - this.iL < (long)iO) {
                    Functions.npcSayInRange(squashInstance, iy[Rnd.get(iy.length)], 128);
                    return;
                }
                ++this.iK;
                this.iL = System.currentTimeMillis();
                if (Rnd.chance(50)) {
                    ++this.iJ;
                    Functions.npcSayInRange(squashInstance, iF[Rnd.get(iF.length)], 128);
                    squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4513, 1, iO, 0L));
                    break;
                }
                Functions.npcSayInRange(squashInstance, iG[Rnd.get(iG.length)], 128);
                squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4514, 1, iO, 0L));
                break;
            }
            case 4: {
                if (System.currentTimeMillis() - this.iL < (long)iO) {
                    Functions.npcSayInRange(squashInstance, iy[Rnd.get(iy.length)], 128);
                    return;
                }
                ++this.iK;
                this.iL = System.currentTimeMillis();
                if (Rnd.chance(50)) {
                    ++this.iJ;
                    Functions.npcSayInRange(squashInstance, iH[Rnd.get(iH.length)], 128);
                    squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4513, 1, iO, 0L));
                } else {
                    Functions.npcSayInRange(squashInstance, iI[Rnd.get(iI.length)], 128);
                    squashInstance.broadcastPacket(new MagicSkillUse(squashInstance, squashInstance, 4514, 1, iO, 0L));
                }
                if (this._npcId == 12774) {
                    this._npcId = this.iJ < 3 ? 12776 : (this.iJ == 5 ? 13016 : 12775);
                } else if (this._npcId == 12777) {
                    this._npcId = this.iJ < 3 ? 12779 : (this.iJ == 5 ? 13017 : 12778);
                }
                this.iN = ThreadPoolManager.getInstance().schedule(new PolimorphTask(), iO);
            }
        }
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        SquashInstance squashInstance = this.getActor();
        if (squashInstance != null && Rnd.chance(5)) {
            Functions.npcSayInRange(squashInstance, ix[Rnd.get(ix.length)], 128);
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.iK = -1;
        SquashInstance squashInstance = this.getActor();
        if (squashInstance == null) {
            return;
        }
        switch (this._npcId) {
            case 12776: {
                Functions.npcSayInRange(squashInstance, "The pampkin opens!!!", 128);
                Functions.npcSayInRange(squashInstance, "ya yo! Opens! Good thing many...", 128);
                break;
            }
            case 12775: {
                Functions.npcSayInRange(squashInstance, "The pampkin opens!!!", 128);
                Functions.npcSayInRange(squashInstance, "ya yo! Opens! Good thing many...", 128);
                break;
            }
            case 13016: {
                Functions.npcSayInRange(squashInstance, "The pampkin opens!!!", 128);
                Functions.npcSayInRange(squashInstance, "ya yo! Opens! Good thing many...", 128);
                break;
            }
            case 12779: {
                Functions.npcSayInRange(squashInstance, "The pampkin opens!!!", 128);
                Functions.npcSayInRange(squashInstance, "ya yo! Opens! Good thing many...", 128);
                break;
            }
            case 12778: {
                Functions.npcSayInRange(squashInstance, "The pampkin opens!!!", 128);
                Functions.npcSayInRange(squashInstance, "ya yo! Opens! Good thing many...", 128);
                break;
            }
            case 13017: {
                Functions.npcSayInRange(squashInstance, "The pampkin opens!!!", 128);
                Functions.npcSayInRange(squashInstance, "ya yo! Opens! Good thing many...", 128);
                break;
            }
            default: {
                Functions.npcSayInRange(squashInstance, "Ouch, if I had died like this, you could obtain nothing!", 128);
                Functions.npcSayInRange(squashInstance, "The news about my death shouldn't spread, oh!", 100);
            }
        }
        super.onEvtDead(squashInstance);
        if (this.iN != null) {
            this.iN.cancel(false);
            this.iN = null;
            Log.add("TheFallHarvest :: Player " + squashInstance.getSpawner().getName() + " tried to use cheat (SquashAI clone): killed " + squashInstance + " after polymorfing started", "illegal-actions");
        }
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    public SquashInstance getActor() {
        return (SquashInstance)super.getActor();
    }

    public class PolimorphTask
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            SquashInstance squashInstance = SquashAI.this.getActor();
            if (squashInstance == null) {
                return;
            }
            SimpleSpawner simpleSpawner = null;
            try {
                simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(SquashAI.this._npcId));
                simpleSpawner.setLoc(squashInstance.getLoc());
                NpcInstance npcInstance = simpleSpawner.doSpawn(true);
                npcInstance.setAI(new SquashAI(npcInstance));
                ((SquashInstance)npcInstance).setSpawner(squashInstance.getSpawner());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            SquashAI.this.iM = Long.MAX_VALUE;
            squashInstance.deleteMe();
        }
    }
}

