/*
 * Decompiled with CFR 0.152.
 */
package quests;

import gnu.trove.TIntIntHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import quests._070_SagaOfThePhoenixKnight;
import quests._071_SagaOfEvasTemplar;
import quests._072_SagaOfTheSwordMuse;
import quests._073_SagaOfTheDuelist;
import quests._074_SagaOfTheDreadnoughts;
import quests._075_SagaOfTheTitan;
import quests._076_SagaOfTheGrandKhavatari;
import quests._077_SagaOfTheDominator;
import quests._078_SagaOfTheDoomcryer;
import quests._079_SagaOfTheAdventurer;
import quests._080_SagaOfTheWindRider;
import quests._081_SagaOfTheGhostHunter;
import quests._082_SagaOfTheSagittarius;
import quests._083_SagaOfTheMoonlightSentinel;
import quests._084_SagaOfTheGhostSentinel;
import quests._085_SagaOfTheCardinal;
import quests._086_SagaOfTheHierophant;
import quests._087_SagaOfEvasSaint;
import quests._088_SagaOfTheArchmage;
import quests._089_SagaOfTheMysticMuse;
import quests._090_SagaOfTheStormScreamer;
import quests._091_SagaOfTheArcanaLord;
import quests._092_SagaOfTheElementalMaster;
import quests._093_SagaOfTheSpectralMaster;
import quests._094_SagaOfTheSoultaker;
import quests._095_SagaOfTheHellKnight;
import quests._096_SagaOfTheSpectralDancer;
import quests._097_SagaOfTheShillienTemplar;
import quests._098_SagaOfTheShillienSaint;
import quests._099_SagaOfTheFortuneSeeker;
import quests._100_SagaOfTheMaestro;

public abstract class SagasSuperclass
extends Quest {
    protected int id = 0;
    protected int classid = 0;
    protected int prevclass = 0;
    protected int[] NPC = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public int[] Items = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    protected int[] Mob = new int[]{0, 1, 2};
    protected int[] X = new int[]{0, 1, 2};
    protected int[] Y = new int[]{0, 1, 2};
    protected int[] Z = new int[]{0, 1, 2};
    public String[] Text = new String[18];
    protected IntObjectMap<List<QuestSpawnInfo>> _spawnInfos = new CHashIntObjectMap<List<QuestSpawnInfo>>();
    protected TIntIntHashMap _kills = new TIntIntHashMap();
    protected TIntIntHashMap _archons = new TIntIntHashMap();
    protected int[] Archon_Minions = new int[]{21646, 21647, 21648, 21649, 21650, 21651};
    protected int[] Guardian_Angels = new int[]{27214, 27215, 27216};
    protected int[] Archon_Hellisha_Norm = new int[]{18212, 18213, 18214, 18215, 18216, 18217, 18218, 18219};
    protected static final Map<Integer, Pair<Class<?>, ClassId[]>> Quests;

    private void b(QuestState questState, Player player) {
        this._kills.remove(player.getObjectId());
        this._archons.remove(player.getObjectId());
        this._spawnInfos.remove(player.getObjectId());
        questState.addExpAndSp(2586527L, 0L);
        questState.giveItems(57, 5000000L);
        questState.giveItems(6622, 1L, true);
        questState.exitCurrentQuest(true);
        player.setClassId(this.getClassId(player), false, true);
        player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
        player.broadcastPacket(new SocialAction(player.getObjectId(), 7));
        if (!player.isSubClassActive() && player.getBaseClassId() == this.getPrevClass(player)) {
            player.setBaseClass(this.getClassId(player));
        }
        player.broadcastCharInfo();
    }

    public SagasSuperclass(int n) {
        super(n);
    }

    protected void registerNPCs() {
        this.addStartNpc(this.NPC[0]);
        this.addAttackId(this.Mob[2]);
        this.addFirstTalkId(this.NPC[4]);
        for (int n : this.NPC) {
            this.addTalkId(n);
        }
        for (int n : this.Mob) {
            this.addKillId(n);
        }
        for (int n : this.Archon_Minions) {
            this.addKillId(n);
        }
        for (int n : this.Guardian_Angels) {
            this.addKillId(n);
        }
        for (int n : this.Archon_Hellisha_Norm) {
            this.addKillId(n);
        }
        for (int n : this.Items) {
            if (n == 0 || n == 7080 || n == 7081 || n == 6480 || n == 6482) continue;
            this.addQuestItem(n);
        }
    }

    protected int getClassId(Player player) {
        return this.classid;
    }

    protected int getPrevClass(Player player) {
        return this.prevclass;
    }

    protected void Cast(NpcInstance npcInstance, Creature creature, int n, int n2) {
        creature.broadcastPacket(new MagicSkillUse(creature, creature, n, n2, 6000, 1L));
        creature.broadcastPacket(new MagicSkillUse(npcInstance, npcInstance, n, n2, 6000, 1L));
    }

    protected void addSpawn(Player player, NpcInstance npcInstance) {
        List<QuestSpawnInfo> list = this._spawnInfos.get(player.getObjectId());
        if (list == null) {
            list = new ArrayList<QuestSpawnInfo>(4);
            this._spawnInfos.put(player.getObjectId(), list);
        }
        list.add(new QuestSpawnInfo(npcInstance));
    }

    protected NpcInstance findMySpawn(Player player, int n) {
        if (n == 0 || player == null) {
            return null;
        }
        List<QuestSpawnInfo> list = this._spawnInfos.get(player.getObjectId());
        if (list == null) {
            return null;
        }
        for (QuestSpawnInfo questSpawnInfo : list) {
            if (questSpawnInfo.npcId != n) continue;
            return questSpawnInfo.getNpc();
        }
        return null;
    }

    protected void deleteMySpawn(Player player, int n) {
        List<QuestSpawnInfo> list = this._spawnInfos.get(player.getObjectId());
        if (list == null) {
            return;
        }
        Iterator<QuestSpawnInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            QuestSpawnInfo questSpawnInfo = iterator.next();
            if (questSpawnInfo.npcId != n) continue;
            NpcInstance npcInstance = questSpawnInfo.getNpc();
            if (npcInstance != null) {
                npcInstance.deleteMe();
            }
            iterator.remove();
        }
    }

    public void giveHallishaMark(QuestState questState) {
        Player player = questState.getPlayer();
        if (player == null) {
            return;
        }
        Integer n = this._archons.get(player.getObjectId());
        if (n == null) {
            return;
        }
        if (GameObjectsStorage.getNpc(n) != null) {
            return;
        }
        questState.cancelQuestTimer("Archon Hellisha has despawned");
        if (questState.getQuestItemsCount(this.Items[3]) < 700L) {
            questState.giveItems(this.Items[3], 1L);
        } else {
            questState.takeItems(this.Items[3], 20L);
            NpcInstance npcInstance = NpcUtils.spawnSingle(this.Mob[1], questState.getPlayer().getLoc(), 600000L);
            this.addSpawn(questState.getPlayer(), npcInstance);
            this._archons.put(player.getObjectId(), npcInstance.getObjectId());
            questState.startQuestTimer("Archon Hellisha has despawned", 600000L, npcInstance);
            npcInstance.setRunning();
            npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 100000);
            this.AutoChat(npcInstance, this.Text[13].replace("PLAYERNAME", questState.getPlayer().getName()));
        }
    }

    protected QuestState findRightState(Player player, NpcInstance npcInstance) {
        if (player == null || npcInstance == null) {
            return null;
        }
        List<QuestSpawnInfo> list = this._spawnInfos.get(player.getObjectId());
        if (list == null) {
            return null;
        }
        for (QuestSpawnInfo questSpawnInfo : list) {
            NpcInstance npcInstance2 = questSpawnInfo.getNpc();
            if (npcInstance2 != npcInstance) continue;
            return player.getQuestState(this);
        }
        return null;
    }

    public static QuestState findQuest(Player player) {
        QuestState questState = null;
        for (Map.Entry<Integer, Pair<Class<?>, ClassId[]>> entry : Quests.entrySet()) {
            int n = entry.getKey();
            Class<?> clazz = entry.getValue().getLeft();
            ClassId[] classIdArray = entry.getValue().getRight();
            questState = player.getQuestState(clazz);
            if (questState == null) continue;
            for (ClassId classId : classIdArray) {
                if (classId.getParent(player.getSex()) != player.getClassId()) continue;
                return questState;
            }
        }
        return null;
    }

    protected void AutoChat(NpcInstance npcInstance, String string) {
        if (npcInstance != null) {
            Functions.npcSay(npcInstance, string);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = "";
        Player player = questState.getPlayer();
        if (string.equalsIgnoreCase("0-011.htm") || string.equalsIgnoreCase("0-012.htm") || string.equalsIgnoreCase("0-013.htm") || string.equalsIgnoreCase("0-014.htm") || string.equalsIgnoreCase("0-015.htm")) {
            string2 = string;
        } else if (string.equalsIgnoreCase("accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(this.Items[10], 1L);
            string2 = "0-03.htm";
        } else if (string.equalsIgnoreCase("0-1")) {
            if (player.getLevel() < 76) {
                string2 = "0-02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string2 = "0-05.htm";
            }
        } else if (string.equalsIgnoreCase("0-2")) {
            if (player.getLevel() >= 76) {
                string2 = "0-07.htm";
                questState.takeItems(this.Items[10], -1L);
                this.b(questState, player);
            } else {
                questState.takeItems(this.Items[10], -1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(20);
                string2 = "0-08.htm";
            }
        } else if (string.equalsIgnoreCase("1-3")) {
            questState.setCond(3);
            string2 = "1-05.htm";
        } else if (string.equalsIgnoreCase("1-4")) {
            questState.setCond(4);
            questState.takeItems(this.Items[0], 1L);
            if (this.Items[11] != 0) {
                questState.takeItems(this.Items[11], 1L);
            }
            questState.giveItems(this.Items[1], 1L);
            string2 = "1-06.htm";
        } else if (string.equalsIgnoreCase("2-1")) {
            questState.setCond(2);
            string2 = "2-05.htm";
        } else if (string.equalsIgnoreCase("2-2")) {
            questState.setCond(5);
            questState.takeItems(this.Items[1], 1L);
            questState.giveItems(this.Items[4], 1L);
            string2 = "2-06.htm";
        } else if (string.equalsIgnoreCase("3-5")) {
            string2 = "3-07.htm";
        } else if (string.equalsIgnoreCase("3-6")) {
            questState.setCond(11);
            string2 = "3-02.htm";
        } else if (string.equalsIgnoreCase("3-7")) {
            questState.setCond(12);
            string2 = "3-03.htm";
        } else if (string.equalsIgnoreCase("3-8")) {
            questState.setCond(13);
            questState.takeItems(this.Items[2], 1L);
            questState.giveItems(this.Items[7], 1L);
            string2 = "3-08.htm";
        } else if (string.equalsIgnoreCase("4-1")) {
            string2 = "4-010.htm";
        } else if (string.equalsIgnoreCase("4-2")) {
            questState.giveItems(this.Items[9], 1L);
            questState.setCond(18);
            questState.playSound("ItemSound.quest_middle");
            string2 = "4-011.htm";
        } else {
            if (string.equalsIgnoreCase("4-3")) {
                questState.giveItems(this.Items[9], 1L);
                questState.setCond(18);
                questState.set("Quest0", "0");
                questState.playSound("ItemSound.quest_middle");
                NpcInstance npcInstance2 = this.findMySpawn(player, this.NPC[4]);
                if (npcInstance2 != null) {
                    this.AutoChat(npcInstance2, this.Text[13].replace("PLAYERNAME", player.getName()));
                    this.deleteMySpawn(player, this.NPC[4]);
                    questState.cancelQuestTimer("Mob_2 has despawned");
                    questState.cancelQuestTimer("NPC_4 Timer");
                }
                return null;
            }
            if (string.equalsIgnoreCase("5-1")) {
                questState.setCond(6);
                questState.takeItems(this.Items[4], 1L);
                this.Cast(questState.findTemplate(this.NPC[5]), player, 4546, 1);
                questState.playSound("ItemSound.quest_middle");
                string2 = "5-02.htm";
            } else if (string.equalsIgnoreCase("6-1")) {
                questState.setCond(8);
                questState.takeItems(this.Items[5], 1L);
                this.Cast(questState.findTemplate(this.NPC[6]), player, 4546, 1);
                questState.playSound("ItemSound.quest_middle");
                string2 = "6-03.htm";
            } else if (string.equalsIgnoreCase("7-1")) {
                if (this.findMySpawn(player, this.Mob[0]) == null) {
                    NpcInstance npcInstance3 = NpcUtils.spawnSingle(this.Mob[0], new Location(this.X[0], this.Y[0], this.Z[0]), 180000L);
                    this.addSpawn(player, npcInstance3);
                    questState.startQuestTimer("Mob_0 Timer", 500L, npcInstance3);
                    questState.startQuestTimer("Mob_1 has despawned", 120000L, npcInstance3);
                    string2 = "7-02.htm";
                } else {
                    string2 = "7-03.htm";
                }
            } else if (string.equalsIgnoreCase("7-2")) {
                questState.setCond(10);
                questState.takeItems(this.Items[6], 1L);
                this.Cast(questState.findTemplate(this.NPC[7]), player, 4546, 1);
                questState.playSound("ItemSound.quest_middle");
                string2 = "7-06.htm";
            } else if (string.equalsIgnoreCase("8-1")) {
                questState.setCond(14);
                questState.takeItems(this.Items[7], 1L);
                this.Cast(questState.findTemplate(this.NPC[8]), player, 4546, 1);
                questState.playSound("ItemSound.quest_middle");
                string2 = "8-02.htm";
            } else if (string.equalsIgnoreCase("9-1")) {
                questState.setCond(17);
                questState.takeItems(this.Items[8], 1L);
                this.Cast(questState.findTemplate(this.NPC[9]), player, 4546, 1);
                questState.playSound("ItemSound.quest_middle");
                string2 = "9-03.htm";
            } else if (string.equalsIgnoreCase("10-1")) {
                if (questState.getInt("Quest0") == 0 || this.findMySpawn(player, this.NPC[4]) == null) {
                    this.deleteMySpawn(player, this.NPC[4]);
                    this.deleteMySpawn(player, this.Mob[2]);
                    questState.set("Quest0", "1");
                    questState.set("Quest1", "45");
                    NpcInstance npcInstance4 = NpcUtils.spawnSingle(this.NPC[4], new Location(this.X[2], this.Y[2], this.Z[2]), 300000L);
                    NpcInstance npcInstance5 = NpcUtils.spawnSingle(this.Mob[2], new Location(this.X[1], this.Y[1], this.Z[1]), 300000L);
                    this.addSpawn(player, npcInstance5);
                    this.addSpawn(player, npcInstance4);
                    questState.startQuestTimer("Mob_2 Timer", 1000L, npcInstance5);
                    questState.startQuestTimer("Mob_2 despawn", 59000L, npcInstance5);
                    questState.startQuestTimer("NPC_4 Timer", 500L, npcInstance4);
                    questState.startQuestTimer("NPC_4 despawn", 60000L, npcInstance4);
                    string2 = "10-02.htm";
                } else if (questState.getInt("Quest1") == 45) {
                    string2 = "10-03.htm";
                } else if (questState.getInt("Tab") == 1) {
                    NpcInstance npcInstance6 = this.findMySpawn(player, this.NPC[4]);
                    if (npcInstance6 == null || !questState.getPlayer().knowsObject(npcInstance6)) {
                        this.deleteMySpawn(player, this.NPC[4]);
                        npcInstance6 = NpcUtils.spawnSingle(this.NPC[4], new Location(this.X[2], this.Y[2], this.Z[2]), 300000L);
                        this.addSpawn(player, npcInstance6);
                        questState.set("Quest0", "1");
                        questState.set("Quest1", "0");
                        questState.startQuestTimer("NPC_4 despawn", 180000L, npcInstance6);
                    }
                    string2 = "10-04.htm";
                }
            } else if (string.equalsIgnoreCase("10-2")) {
                questState.setCond(19);
                questState.takeItems(this.Items[9], 1L);
                this.Cast(questState.findTemplate(this.NPC[10]), player, 4546, 1);
                questState.playSound("ItemSound.quest_middle");
                string2 = "10-06.htm";
            } else if (string.equalsIgnoreCase("11-9")) {
                questState.setCond(15);
                string2 = "11-03.htm";
            } else {
                if (string.equalsIgnoreCase("Mob_0 Timer")) {
                    this.AutoChat(this.findMySpawn(player, this.Mob[0]), this.Text[0].replace("PLAYERNAME", player.getName()));
                    return null;
                }
                if (string.equalsIgnoreCase("Mob_1 has despawned")) {
                    this.AutoChat(this.findMySpawn(player, this.Mob[0]), this.Text[1].replace("PLAYERNAME", player.getName()));
                    this.deleteMySpawn(player, this.Mob[0]);
                    return null;
                }
                if (string.equalsIgnoreCase("Archon Hellisha has despawned")) {
                    this.AutoChat(npcInstance, this.Text[6].replace("PLAYERNAME", player.getName()));
                    this.deleteMySpawn(player, this.Mob[1]);
                    return null;
                }
                if (string.equalsIgnoreCase("Mob_2 Timer")) {
                    NpcInstance npcInstance7;
                    NpcInstance npcInstance8 = this.findMySpawn(player, this.NPC[4]);
                    if (npcInstance8.knowsObject(npcInstance7 = this.findMySpawn(player, this.Mob[2]))) {
                        npcInstance8.setRunning();
                        npcInstance8.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npcInstance7, null);
                        npcInstance7.setRunning();
                        npcInstance7.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npcInstance8, null);
                        this.AutoChat(npcInstance7, this.Text[14].replace("PLAYERNAME", player.getName()));
                    } else {
                        questState.startQuestTimer("Mob_2 Timer", 1000L, npcInstance);
                    }
                    return null;
                }
                if (string.equalsIgnoreCase("Mob_2 despawn")) {
                    NpcInstance npcInstance9 = this.findMySpawn(player, this.Mob[2]);
                    this.AutoChat(npcInstance9, this.Text[15].replace("PLAYERNAME", player.getName()));
                    questState.set("Quest0", "2");
                    if (npcInstance9 != null) {
                        npcInstance9.reduceCurrentHp(9999999.0, npcInstance9, null, true, true, false, false, false, false, false);
                    }
                    this.deleteMySpawn(player, this.Mob[2]);
                    return null;
                }
                if (string.equalsIgnoreCase("NPC_4 Timer")) {
                    this.AutoChat(this.findMySpawn(player, this.NPC[4]), this.Text[7].replace("PLAYERNAME", player.getName()));
                    questState.startQuestTimer("NPC_4 Timer 2", 1500L, npcInstance);
                    if (questState.getInt("Quest1") == 45) {
                        questState.set("Quest1", "0");
                    }
                    return null;
                }
                if (string.equalsIgnoreCase("NPC_4 Timer 2")) {
                    this.AutoChat(this.findMySpawn(player, this.NPC[4]), this.Text[8].replace("PLAYERNAME", player.getName()));
                    questState.startQuestTimer("NPC_4 Timer 3", 10000L, npcInstance);
                    return null;
                }
                if (string.equalsIgnoreCase("NPC_4 Timer 3")) {
                    if (questState.getInt("Quest0") == 0) {
                        questState.startQuestTimer("NPC_4 Timer 3", 13000L, npcInstance);
                        this.AutoChat(this.findMySpawn(player, this.NPC[4]), this.Text[Rnd.get(9, 10)].replace("PLAYERNAME", player.getName()));
                    }
                    return null;
                }
                if (string.equalsIgnoreCase("NPC_4 despawn")) {
                    questState.set("Quest1", this.str(questState.getInt("Quest1") + 1));
                    NpcInstance npcInstance10 = this.findMySpawn(player, this.NPC[4]);
                    if (questState.getInt("Quest0") == 1 || questState.getInt("Quest0") == 2 || questState.getInt("Quest1") > 3) {
                        questState.set("Quest0", "0");
                        this.AutoChat(npcInstance10, this.Text[Rnd.get(11, 12)].replace("PLAYERNAME", player.getName()));
                        if (npcInstance10 != null) {
                            npcInstance10.reduceCurrentHp(9999999.0, npcInstance10, null, true, true, false, false, false, false, false);
                        }
                        this.deleteMySpawn(player, this.NPC[4]);
                    } else {
                        questState.startQuestTimer("NPC_4 despawn", 1000L, npcInstance);
                    }
                    return null;
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        Player player = questState.getPlayer();
        if (player.getClassId().getId() != this.getPrevClass(player)) {
            questState.exitCurrentQuest(true);
            return string;
        }
        if (n2 == 0) {
            if (n == this.NPC[0]) {
                string = "0-01.htm";
            }
        } else if (n2 == 1) {
            if (n == this.NPC[0]) {
                string = "0-04.htm";
            } else if (n == this.NPC[2]) {
                string = "2-01.htm";
            }
        } else if (n2 == 2) {
            if (n == this.NPC[2]) {
                string = "2-02.htm";
            } else if (n == this.NPC[1]) {
                string = "1-01.htm";
            }
        } else if (n2 == 3) {
            if (n == this.NPC[1]) {
                string = questState.getQuestItemsCount(this.Items[0]) > 0L ? (this.Items[11] == 0 ? "1-03.htm" : (questState.getQuestItemsCount(this.Items[11]) > 0L ? "1-03.htm" : "1-02.htm")) : "1-02.htm";
            } else if (n == 31537) {
                if (questState.getQuestItemsCount(7546) == 0L) {
                    string = "tunatun_q72_01.htm";
                    questState.giveItems(7546, 1L);
                    return null;
                }
                string = "tunatun_q72_02.htm";
            }
        } else if (n2 == 4) {
            if (n == this.NPC[1]) {
                string = "1-04.htm";
            } else if (n == this.NPC[2]) {
                string = "2-03.htm";
            }
        } else if (n2 == 5) {
            if (n == this.NPC[2]) {
                string = "2-04.htm";
            } else if (n == this.NPC[5]) {
                string = "5-01.htm";
            }
        } else if (n2 == 6) {
            if (n == this.NPC[5]) {
                string = "5-03.htm";
            } else if (n == this.NPC[6]) {
                string = "6-01.htm";
            }
        } else if (n2 == 7) {
            if (n == this.NPC[6]) {
                string = "6-02.htm";
            }
        } else if (n2 == 8) {
            if (n == this.NPC[6]) {
                string = "6-04.htm";
            } else if (n == this.NPC[7]) {
                string = "7-01.htm";
            }
        } else if (n2 == 9) {
            if (n == this.NPC[7]) {
                string = "7-05.htm";
            }
        } else if (n2 == 10) {
            if (n == this.NPC[7]) {
                string = "7-07.htm";
            } else if (n == this.NPC[3]) {
                string = "3-01.htm";
            }
        } else if (n2 == 11 || n2 == 12) {
            if (n == this.NPC[3]) {
                string = questState.getQuestItemsCount(this.Items[2]) > 0L ? "3-05.htm" : "3-04.htm";
            }
        } else if (n2 == 13) {
            if (n == this.NPC[3]) {
                string = "3-06.htm";
            } else if (n == this.NPC[8]) {
                string = "8-01.htm";
            }
        } else if (n2 == 14) {
            if (n == this.NPC[8]) {
                string = "8-03.htm";
            } else if (n == this.NPC[11]) {
                string = "11-01.htm";
            }
        } else if (n2 == 15) {
            if (n == this.NPC[11]) {
                string = "11-02.htm";
            } else if (n == this.NPC[9]) {
                string = "9-01.htm";
            }
        } else if (n2 == 16) {
            if (n == this.NPC[9]) {
                string = "9-02.htm";
            }
        } else if (n2 == 17) {
            if (n == this.NPC[9]) {
                string = "9-04.htm";
            } else if (n == this.NPC[10]) {
                string = "10-01.htm";
            }
        } else if (n2 == 18) {
            if (n == this.NPC[10]) {
                string = "10-05.htm";
            }
        } else if (n2 == 19) {
            if (n == this.NPC[10]) {
                string = "10-07.htm";
            }
            if (n == this.NPC[0]) {
                string = "0-06.htm";
            }
        } else if (n2 == 20 && n == this.NPC[0]) {
            if (player.getLevel() >= 76) {
                string = "0-09.htm";
                if (this.getClassId(player) < 131 || this.getClassId(player) > 135) {
                    this.b(questState, player);
                }
            } else {
                string = "0-010.htm";
            }
        }
        return string;
    }

    @Override
    public String onFirstTalk(NpcInstance npcInstance, Player player) {
        String string = "";
        QuestState questState = player.getQuestState(this);
        if (questState == null) {
            return string;
        }
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == this.NPC[4]) {
            if (n2 == 17) {
                QuestState questState2 = this.findRightState(player, npcInstance);
                if (questState2 != null) {
                    if (questState == questState2) {
                        if (questState.getInt("Tab") == 1) {
                            if (questState.getInt("Quest0") == 0) {
                                string = "4-04.htm";
                            } else if (questState.getInt("Quest0") == 1) {
                                string = "4-06.htm";
                            }
                        } else if (questState.getInt("Quest0") == 0) {
                            string = "4-01.htm";
                        } else if (questState.getInt("Quest0") == 1) {
                            string = "4-03.htm";
                        }
                    } else if (questState.getInt("Tab") == 1) {
                        if (questState.getInt("Quest0") == 0) {
                            string = "4-05.htm";
                        } else if (questState.getInt("Quest0") == 1) {
                            string = "4-07.htm";
                        }
                    } else if (questState.getInt("Quest0") == 0) {
                        string = "4-02.htm";
                    }
                }
            } else if (n2 == 18) {
                string = "4-08.htm";
            }
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        QuestState questState2;
        Player player = questState.getPlayer();
        if (questState.getCond() == 17 && npcInstance.getNpcId() == this.Mob[2] && questState == (questState2 = this.findRightState(player, npcInstance))) {
            questState.set("Quest0", this.str(questState.getInt("Quest0") + 1));
            if (questState.getInt("Quest0") == 1) {
                this.AutoChat(npcInstance, this.Text[16].replace("PLAYERNAME", player.getName()));
            }
            if (questState.getInt("Quest0") > 15) {
                questState.set("Quest0", "1");
                this.AutoChat(npcInstance, this.Text[17].replace("PLAYERNAME", player.getName()));
                npcInstance.reduceCurrentHp(9999999.0, npcInstance, null, true, true, false, false, false, false, false);
                this.deleteMySpawn(player, this.Mob[2]);
                questState.cancelQuestTimer("Mob_2 despawn");
                questState.set("Tab", "1");
            }
        }
        return null;
    }

    protected boolean isArchonMinions(int n) {
        for (int n2 : this.Archon_Minions) {
            if (n2 != n) continue;
            return true;
        }
        return false;
    }

    protected boolean isArchonHellishaNorm(int n) {
        for (int n2 : this.Archon_Hellisha_Norm) {
            if (n2 != n) continue;
            return true;
        }
        return false;
    }

    protected boolean isGuardianAngels(int n) {
        for (int n2 : this.Guardian_Angels) {
            if (n2 != n) continue;
            return true;
        }
        return false;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        Player player = questState.getPlayer();
        if (player.getActiveClassId() != this.getPrevClass(player)) {
            return null;
        }
        if (this.isArchonMinions(n)) {
            Party party = player.getParty();
            if (party != null) {
                ArrayList<QuestState> arrayList = new ArrayList<QuestState>();
                for (Player player2 : party.getPartyMembers()) {
                    QuestState questState2 = SagasSuperclass.findQuest(player2);
                    if (questState2 == null || questState2.getCond() != 15 || !(player.getDistance(player2) <= (double)Config.ALT_PARTY_DISTRIBUTION_RANGE)) continue;
                    arrayList.add(questState2);
                }
                if (arrayList.size() > 0) {
                    QuestState questState3 = (QuestState)arrayList.get(Rnd.get(arrayList.size()));
                    ((SagasSuperclass)questState3.getQuest()).giveHallishaMark(questState3);
                }
            } else {
                QuestState questState4 = SagasSuperclass.findQuest(player);
                if (questState4 != null && questState4.getCond() == 15) {
                    ((SagasSuperclass)questState4.getQuest()).giveHallishaMark(questState4);
                }
            }
        } else if (this.isArchonHellishaNorm(n)) {
            Party party = player.getParty();
            if (party != null) {
                this.AutoChat(npcInstance, "Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...");
                for (Player player3 : party.getPartyMembers()) {
                    QuestState questState5 = SagasSuperclass.findQuest(player3);
                    if (questState5 == null || questState5.getCond() != 15 || !(player.getDistance(player3) <= (double)Config.ALT_PARTY_DISTRIBUTION_RANGE)) continue;
                    questState5.giveItems(((SagasSuperclass)questState5.getQuest()).Items[8], 1L);
                    questState5.takeItems(((SagasSuperclass)questState5.getQuest()).Items[3], -1L);
                    questState5.setCond(16);
                    questState5.playSound("ItemSound.quest_middle");
                }
            } else {
                QuestState questState6 = SagasSuperclass.findQuest(player);
                if (questState6 != null && questState6.getCond() == 15) {
                    this.AutoChat(npcInstance, ((SagasSuperclass)questState6.getQuest()).Text[4].replace("PLAYERNAME", questState6.getPlayer().getName()));
                    questState6.giveItems(((SagasSuperclass)questState6.getQuest()).Items[8], 1L);
                    questState6.takeItems(((SagasSuperclass)questState6.getQuest()).Items[3], -1L);
                    questState6.setCond(16);
                    questState6.playSound("ItemSound.quest_middle");
                }
            }
        } else if (this.isGuardianAngels(n)) {
            QuestState questState7 = SagasSuperclass.findQuest(player);
            if (questState7 != null && questState7.getCond() == 6) {
                Integer n2 = this._kills.get(player.getObjectId());
                if (n2 == null) {
                    n2 = 0;
                }
                if (n2 < 9) {
                    this._kills.put(player.getObjectId(), n2 + 1);
                } else {
                    questState7.playSound("ItemSound.quest_middle");
                    questState7.giveItems(((SagasSuperclass)questState7.getQuest()).Items[5], 1L);
                    questState7.setCond(7);
                }
            }
        } else {
            QuestState questState8;
            int n3 = questState.getCond();
            if (n == this.Mob[0] && n3 == 8) {
                QuestState questState9 = this.findRightState(player, npcInstance);
                if (questState9 != null) {
                    if (!player.isInParty() && questState == questState9) {
                        this.AutoChat(npcInstance, this.Text[12].replace("PLAYERNAME", player.getName()));
                        questState.giveItems(this.Items[6], 1L);
                        questState.setCond(9);
                        questState.playSound("ItemSound.quest_middle");
                    }
                    questState.cancelQuestTimer("Mob_1 has despawned");
                    this.deleteMySpawn(questState9.getPlayer(), this.Mob[0]);
                }
            } else if (n == this.Mob[1] && n3 == 15) {
                QuestState questState10 = this.findRightState(player, npcInstance);
                if (questState10 != null) {
                    if (!player.isInParty()) {
                        if (questState == questState10) {
                            this.AutoChat(npcInstance, this.Text[4].replace("PLAYERNAME", player.getName()));
                            questState.giveItems(this.Items[8], 1L);
                            questState.takeItems(this.Items[3], -1L);
                            questState.setCond(16);
                            questState.playSound("ItemSound.quest_middle");
                        } else {
                            this.AutoChat(npcInstance, this.Text[5].replace("PLAYERNAME", player.getName()));
                        }
                    }
                    questState.cancelQuestTimer("Archon Hellisha has despawned");
                    this.deleteMySpawn(questState10.getPlayer(), this.Mob[1]);
                }
            } else if (n == this.Mob[2] && n3 == 17 && questState == (questState8 = this.findRightState(player, npcInstance))) {
                questState.set("Quest0", "1");
                this.AutoChat(npcInstance, this.Text[17].replace("PLAYERNAME", player.getName()));
                npcInstance.reduceCurrentHp(9999999.0, npcInstance, null, true, true, false, false, false, false, false);
                this.deleteMySpawn(player, this.Mob[2]);
                questState.cancelQuestTimer("Mob_2 despawn");
                questState.set("Tab", "1");
            }
        }
        return null;
    }

    static {
        HashMap<Integer, Pair<Class, ClassId[]>> hashMap = new HashMap<Integer, Pair<Class, ClassId[]>>();
        hashMap.put(70, Pair.of(_070_SagaOfThePhoenixKnight.class, new ClassId[]{ClassId.phoenix_knight}));
        hashMap.put(71, Pair.of(_071_SagaOfEvasTemplar.class, new ClassId[]{ClassId.evas_templar}));
        hashMap.put(72, Pair.of(_072_SagaOfTheSwordMuse.class, new ClassId[]{ClassId.sword_muse}));
        hashMap.put(73, Pair.of(_073_SagaOfTheDuelist.class, new ClassId[]{ClassId.duelist}));
        hashMap.put(74, Pair.of(_074_SagaOfTheDreadnoughts.class, new ClassId[]{ClassId.dreadnought}));
        hashMap.put(75, Pair.of(_075_SagaOfTheTitan.class, new ClassId[]{ClassId.titan}));
        hashMap.put(76, Pair.of(_076_SagaOfTheGrandKhavatari.class, new ClassId[]{ClassId.grand_khavatari}));
        hashMap.put(77, Pair.of(_077_SagaOfTheDominator.class, new ClassId[]{ClassId.dominator}));
        hashMap.put(78, Pair.of(_078_SagaOfTheDoomcryer.class, new ClassId[]{ClassId.doomcryer}));
        hashMap.put(79, Pair.of(_079_SagaOfTheAdventurer.class, new ClassId[]{ClassId.adventurer}));
        hashMap.put(80, Pair.of(_080_SagaOfTheWindRider.class, new ClassId[]{ClassId.wind_rider}));
        hashMap.put(81, Pair.of(_081_SagaOfTheGhostHunter.class, new ClassId[]{ClassId.ghost_hunter}));
        hashMap.put(82, Pair.of(_082_SagaOfTheSagittarius.class, new ClassId[]{ClassId.sagittarius}));
        hashMap.put(83, Pair.of(_083_SagaOfTheMoonlightSentinel.class, new ClassId[]{ClassId.moonlight_sentinel}));
        hashMap.put(84, Pair.of(_084_SagaOfTheGhostSentinel.class, new ClassId[]{ClassId.ghost_sentinel}));
        hashMap.put(85, Pair.of(_085_SagaOfTheCardinal.class, new ClassId[]{ClassId.cardinal}));
        hashMap.put(86, Pair.of(_086_SagaOfTheHierophant.class, new ClassId[]{ClassId.hierophant}));
        hashMap.put(87, Pair.of(_087_SagaOfEvasSaint.class, new ClassId[]{ClassId.evas_saint}));
        hashMap.put(88, Pair.of(_088_SagaOfTheArchmage.class, new ClassId[]{ClassId.archmage}));
        hashMap.put(89, Pair.of(_089_SagaOfTheMysticMuse.class, new ClassId[]{ClassId.mystic_muse}));
        hashMap.put(90, Pair.of(_090_SagaOfTheStormScreamer.class, new ClassId[]{ClassId.storm_screamer}));
        hashMap.put(91, Pair.of(_091_SagaOfTheArcanaLord.class, new ClassId[]{ClassId.arcana_lord}));
        hashMap.put(92, Pair.of(_092_SagaOfTheElementalMaster.class, new ClassId[]{ClassId.elemental_master}));
        hashMap.put(93, Pair.of(_093_SagaOfTheSpectralMaster.class, new ClassId[]{ClassId.spectral_master}));
        hashMap.put(94, Pair.of(_094_SagaOfTheSoultaker.class, new ClassId[]{ClassId.soultaker}));
        hashMap.put(95, Pair.of(_095_SagaOfTheHellKnight.class, new ClassId[]{ClassId.hell_knight}));
        hashMap.put(96, Pair.of(_096_SagaOfTheSpectralDancer.class, new ClassId[]{ClassId.spectral_dancer}));
        hashMap.put(97, Pair.of(_097_SagaOfTheShillienTemplar.class, new ClassId[]{ClassId.shillien_templar}));
        hashMap.put(98, Pair.of(_098_SagaOfTheShillienSaint.class, new ClassId[]{ClassId.shillien_saint}));
        hashMap.put(99, Pair.of(_099_SagaOfTheFortuneSeeker.class, new ClassId[]{ClassId.fortune_seeker}));
        hashMap.put(100, Pair.of(_100_SagaOfTheMaestro.class, new ClassId[]{ClassId.maestro}));
        Quests = (Map)Collections.unmodifiableMap(hashMap);
    }

    private class QuestSpawnInfo {
        public final int npcId;
        private HardReference<NpcInstance> arv;

        public QuestSpawnInfo(NpcInstance npcInstance) {
            this.npcId = npcInstance.getNpcId();
            this.arv = npcInstance.getRef();
        }

        public NpcInstance getNpc() {
            return this.arv.get();
        }
    }
}

