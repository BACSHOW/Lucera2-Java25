/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.ArrayList;
import java.util.List;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class _421_LittleWingAdventures
extends Quest
implements ScriptFile {
    private static int aLs = 30610;
    private static int bBv = 30747;
    private static int bCo = 27185;
    private static int bCp = 27186;
    private static int bCq = 27187;
    private static int bCr = 27188;
    private static int bCs = 27189;
    private static int bBM = PetDataTable.L2Pet.HATCHLING_WIND.getControlItemId();
    private static int bCt = PetDataTable.L2Pet.HATCHLING_STAR.getControlItemId();
    private static int bBN = PetDataTable.L2Pet.HATCHLING_TWILIGHT.getControlItemId();
    private static int bCu = PetDataTable.L2Pet.STRIDER_WIND.getControlItemId();
    private static int bCv = PetDataTable.L2Pet.STRIDER_STAR.getControlItemId();
    private static int bCw = PetDataTable.L2Pet.STRIDER_TWILIGHT.getControlItemId();
    private static int bCx = 4325;
    private static int bCy = 110;

    public _421_LittleWingAdventures() {
        super(0);
        this.addStartNpc(aLs);
        this.addTalkId(bBv);
        this.addKillId(bCo);
        this.addKillId(bCp);
        this.addKillId(bCq);
        this.addKillId(bCr);
        this.addAttackId(bCo);
        this.addAttackId(bCp);
        this.addAttackId(bCq);
        this.addAttackId(bCr);
        this.addQuestItem(bCx);
    }

    private static ItemInstance k(QuestState questState) {
        ArrayList<ItemInstance> arrayList = new ArrayList<ItemInstance>();
        for (ItemInstance itemInstance : questState.getPlayer().getInventory().getItems()) {
            if (itemInstance == null || itemInstance.getItemId() != bBM && itemInstance.getItemId() != bCt && itemInstance.getItemId() != bBN) continue;
            arrayList.add(itemInstance);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        if (arrayList.size() == 1) {
            return (ItemInstance)arrayList.get(0);
        }
        if (questState.getState() == 1) {
            return null;
        }
        int n = questState.getInt("dragonflute");
        for (ItemInstance itemInstance : arrayList) {
            if (itemInstance.getObjectId() != n) continue;
            return itemInstance;
        }
        return null;
    }

    private static boolean a(QuestState questState, boolean bl) {
        ItemInstance itemInstance;
        Summon summon = questState.getPlayer().getPet();
        if (summon == null) {
            return false;
        }
        if (bl) {
            int n = questState.getInt("dragonflute");
            if (n == 0) {
                return false;
            }
            if (summon.getControlItemObjId() != n) {
                return false;
            }
        }
        if ((itemInstance = _421_LittleWingAdventures.k(questState)) == null) {
            return false;
        }
        return PetDataTable.getControlItemId(summon.getNpcId()) == itemInstance.getItemId();
    }

    private static boolean g(QuestState questState, int n) {
        return questState.getInt(String.valueOf(n)) == 1000000;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        ItemInstance itemInstance = _421_LittleWingAdventures.k(questState);
        int n2 = questState.getInt("dragonflute");
        int n3 = questState.getCond();
        if (string.equalsIgnoreCase("30610_05.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if ((string.equalsIgnoreCase("30747_03.htm") || string.equalsIgnoreCase("30747_04.htm")) && n == 2 && n3 == 1) {
            if (itemInstance == null) {
                return "noquest";
            }
            if (itemInstance.getObjectId() != n2) {
                if (Rnd.chance(10)) {
                    questState.takeItems(itemInstance.getItemId(), 1L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                }
                return "30747_00.htm";
            }
            if (!_421_LittleWingAdventures.a(questState, false)) {
                return string.equalsIgnoreCase("30747_04.htm") ? "30747_04a.htm" : "30747_02.htm";
            }
            if (string.equalsIgnoreCase("30747_04.htm")) {
                questState.setCond(2);
                questState.takeItems(bCx, -1L);
                questState.giveItems(bCx, 4L);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getCond();
        ItemInstance itemInstance = _421_LittleWingAdventures.k(questState);
        int n4 = questState.getInt("dragonflute");
        if (n == 1) {
            if (n2 != aLs) {
                return "noquest";
            }
            if (questState.getPlayer().getLevel() < 45) {
                questState.exitCurrentQuest(true);
                return "30610_01.htm";
            }
            if (itemInstance == null) {
                questState.exitCurrentQuest(true);
                return "30610_02.htm";
            }
            if (itemInstance.getEnchantLevel() < 55) {
                questState.exitCurrentQuest(true);
                return "30610_03.htm";
            }
            questState.setCond(0);
            questState.set("dragonflute", String.valueOf(itemInstance.getObjectId()));
            return "30610_04.htm";
        }
        if (n != 2) {
            return "noquest";
        }
        if (n2 == aLs) {
            if (itemInstance == null) {
                return "30610_02.htm";
            }
            return itemInstance.getObjectId() == n4 ? "30610_07.htm" : "30610_06.htm";
        }
        if (n2 == bBv) {
            if (questState.getQuestItemsCount(bCu) + questState.getQuestItemsCount(bCv) + questState.getQuestItemsCount(bCw) > 0L) {
                return "30747_00b.htm";
            }
            if (itemInstance == null) {
                return "noquest";
            }
            if (n3 == 1) {
                return "30747_01.htm";
            }
            if (n3 == 2) {
                if (!_421_LittleWingAdventures.a(questState, false)) {
                    return "30747_09.htm";
                }
                if (questState.getQuestItemsCount(bCx) == 0L) {
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    return "30747_11.htm";
                }
                return "30747_10.htm";
            }
            if (n3 == 3) {
                if (itemInstance.getObjectId() != n4) {
                    return "30747_00a.htm";
                }
                if (questState.getQuestItemsCount(bCx) > 0L) {
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    return "30747_11.htm";
                }
                if (!(_421_LittleWingAdventures.g(questState, bCo) && _421_LittleWingAdventures.g(questState, bCp) && _421_LittleWingAdventures.g(questState, bCq) && _421_LittleWingAdventures.g(questState, bCr))) {
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    return "30747_11.htm";
                }
                if (questState.getInt("welldone") == 0) {
                    if (!_421_LittleWingAdventures.a(questState, false)) {
                        return "30747_09.htm";
                    }
                    questState.set("welldone", "1");
                    return "30747_12.htm";
                }
                if (_421_LittleWingAdventures.a(questState, false) || questState.getPlayer().getPet() != null) {
                    return "30747_13a.htm";
                }
                itemInstance.setItemId(bCu + itemInstance.getItemId() - bBM);
                questState.getPlayer().sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                return "30747_13.htm";
            }
        }
        return "noquest";
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2 || questState.getCond() != 2 || !_421_LittleWingAdventures.a(questState, true) || questState.getQuestItemsCount(bCx) == 0L) {
            return null;
        }
        String string = String.valueOf(npcInstance.getNpcId());
        Integer n = questState.getInt(string);
        if (_421_LittleWingAdventures.g(questState, npcInstance.getNpcId())) {
            return null;
        }
        if (n > bCy) {
            questState.set(string, "1000000");
            Functions.npcSay(npcInstance, "Give me the leaf!");
            questState.takeItems(bCx, 1L);
            if (_421_LittleWingAdventures.g(questState, bCo) && _421_LittleWingAdventures.g(questState, bCp) && _421_LittleWingAdventures.g(questState, bCq) && _421_LittleWingAdventures.g(questState, bCr)) {
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        } else {
            questState.set(string, String.valueOf(n + 1));
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        ThreadPoolManager.getInstance().schedule(new GuardiansSpawner(npcInstance, questState, Rnd.get(15, 20)), 1000L);
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public class GuardiansSpawner
    extends RunnableImpl {
        private SimpleSpawner bCz = null;
        private String bCA;
        private String bCB = null;
        private List<String> bCC = null;
        private int bCD = 0;

        public GuardiansSpawner(NpcInstance npcInstance, QuestState questState, int n) {
            NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(bCs);
            if (npcTemplate == null) {
                return;
            }
            try {
                this.bCz = new SimpleSpawner(npcTemplate);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            for (int i = 0; i < n; ++i) {
                this.bCz.setLoc(Location.findPointToStay(npcInstance, 50, 200));
                this.bCz.setHeading(Rnd.get(0, 65535));
                this.bCz.setAmount(1);
                this.bCz.doSpawn(true);
                this.bCA = questState.getPlayer().getName();
                if (questState.getPlayer().getPet() != null) {
                    this.bCB = questState.getPlayer().getPet().getName();
                }
                if (questState.getPlayer().getParty() == null) continue;
                this.bCC = new ArrayList<String>();
                for (Player player : questState.getPlayer().getParty().getPartyMembers()) {
                    if (player.equals(questState.getPlayer())) continue;
                    this.bCC.add(player.getName());
                }
            }
            this.bCz.stopRespawn();
            this.dP();
        }

        private void a(Playable playable, int n) {
            if (playable == null) {
                return;
            }
            for (NpcInstance npcInstance : this.bCz.getAllSpawned()) {
                npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, playable, n);
            }
        }

        private void dP() {
            Player player = World.getPlayer(this.bCA);
            if (player != null) {
                if (this.bCB != null && player.getPet() != null && player.getPet().getName().equalsIgnoreCase(this.bCB)) {
                    this.a(player.getPet(), 10);
                }
                this.a(player, 2);
            }
            if (this.bCC != null) {
                for (String string : this.bCC) {
                    this.a(World.getPlayer(string), 1);
                }
            }
        }

        @Override
        public void runImpl() throws Exception {
            if (this.bCz == null) {
                return;
            }
            ++this.bCD;
            if (this.bCD < 600) {
                this.dP();
                ThreadPoolManager.getInstance().schedule(this, 1000L);
                return;
            }
            this.bCz.deleteAll();
        }
    }
}

