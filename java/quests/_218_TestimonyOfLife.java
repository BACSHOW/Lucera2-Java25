/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _218_TestimonyOfLife
extends Quest
implements ScriptFile {
    private static final int aNQ = 3140;
    private static final int aNR = 3141;
    private static final int aNS = 3142;
    private static final int aNT = 3143;
    private static final int aNU = 3144;
    private static final int aNV = 3145;
    private static final int aNW = 3146;
    private static final int aNX = 3147;
    private static final int aNY = 3148;
    private static final int aNZ = 3149;
    private static final int aOa = 3150;
    private static final int aOb = 3151;
    private static final int aOc = 3152;
    private static final int aOd = 3153;
    private static final int aOe = 3154;
    private static final int aOf = 3155;
    private static final int aOg = 3156;
    private static final int aOh = 3157;
    private static final int aOi = 3158;
    private static final int aOj = 3159;
    private static final int aOk = 3160;
    private static final int aOl = 3161;
    private static final int aOm = 3162;
    private static final int aOn = 3163;
    private static final int aOo = 3164;
    private static final int aOp = 3165;
    private static final int aOq = 3166;
    private static final int aOr = 3167;
    private static final int aOs = 3168;
    private static final int aOt = 3169;
    private static final int aOu = 3170;
    private static final int aOv = 3171;
    private static final int aOw = 3026;
    private static final int aKb = 104591;
    private static final int aKc = 11250;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _218_TestimonyOfLife() {
        super(0);
        this.addStartNpc(30460);
        this.addTalkId(30154);
        this.addTalkId(30300);
        this.addTalkId(30371);
        this.addTalkId(30375);
        this.addTalkId(30419);
        this.addTalkId(30460);
        this.addTalkId(30655);
        this.addKillId(20145);
        this.addKillId(20176);
        this.addKillId(20233);
        this.addKillId(27077);
        this.addKillId(20550);
        this.addKillId(20581);
        this.addKillId(20582);
        this.addKillId(20082);
        this.addKillId(20084);
        this.addKillId(20086);
        this.addKillId(20087);
        this.addKillId(20088);
        this.addQuestItem(3142, 3141, 3160, 3144, 3143, 3155, 3150, 3148, 3157, 3159, 3145, 3149, 3146, 3151, 3154, 3152, 3153, 3147, 3166, 3167, 3168, 3169, 3170, 3171, 3156, 3158);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            string2 = "30460-04.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(3141, 1L);
        } else if (string.equalsIgnoreCase("30154_1")) {
            string2 = "30154-02.htm";
        } else if (string.equalsIgnoreCase("30154_2")) {
            string2 = "30154-03.htm";
        } else if (string.equalsIgnoreCase("30154_3")) {
            string2 = "30154-04.htm";
        } else if (string.equalsIgnoreCase("30154_4")) {
            string2 = "30154-05.htm";
        } else if (string.equalsIgnoreCase("30154_5")) {
            string2 = "30154-06.htm";
        } else if (string.equalsIgnoreCase("30154_6")) {
            string2 = "30154-07.htm";
            questState.takeItems(3141, 1L);
            questState.giveItems(3144, 1L);
            questState.giveItems(3143, 1L);
        } else if (string.equalsIgnoreCase("30371_1")) {
            string2 = "30371-02.htm";
        } else if (string.equalsIgnoreCase("30371_2")) {
            string2 = "30371-03.htm";
            questState.takeItems(3143, 1L);
            questState.giveItems(3145, 1L);
        } else if (string.equalsIgnoreCase("30371_3")) {
            if (questState.getPlayer().getLevel() < 38) {
                string2 = "30371-10.htm";
                questState.takeItems(3155, 1L);
                questState.giveItems(3148, 1L);
            } else {
                string2 = "30371-11.htm";
                questState.takeItems(3155, 1L);
                questState.giveItems(3147, 1L);
            }
        } else if (string.equalsIgnoreCase("30300_1")) {
            string2 = "30300-02.htm";
        } else if (string.equalsIgnoreCase("30300_2")) {
            string2 = "30300-03.htm";
        } else if (string.equalsIgnoreCase("30300_3")) {
            string2 = "30300-04.htm";
        } else if (string.equalsIgnoreCase("30300_4")) {
            string2 = "30300-05.htm";
        } else if (string.equalsIgnoreCase("30300_5")) {
            string2 = "30300-06.htm";
            questState.takeItems(3145, 1L);
            questState.giveItems(3149, 1L);
        } else if (string.equalsIgnoreCase("30300_6")) {
            string2 = "30300-09.htm";
        } else if (string.equalsIgnoreCase("30300_7")) {
            string2 = "30300-10.htm";
            questState.takeItems(3161, -1L);
            questState.takeItems(3162, -1L);
            questState.takeItems(3163, -1L);
            questState.takeItems(3149, 1L);
            questState.giveItems(3150, 1L);
        } else if (string.equalsIgnoreCase("30419_1")) {
            string2 = "30419-02.htm";
        } else if (string.equalsIgnoreCase("30419_2")) {
            string2 = "30419-03.htm";
        } else if (string.equalsIgnoreCase("30419_3")) {
            string2 = "30419-04.htm";
            questState.takeItems(3146, 1L);
            questState.giveItems(3151, 1L);
            questState.giveItems(3152, 1L);
        } else if (string.equalsIgnoreCase("30375_1")) {
            string2 = "30375-02.htm";
            questState.takeItems(3152, 1L);
            questState.giveItems(3153, 1L);
        } else if (string.equalsIgnoreCase("30655_1")) {
            string2 = "30655-02.htm";
            questState.takeItems(3147, 1L);
            questState.giveItems(3156, 1L);
        }
        return string2;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(3140) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n2 == 1) {
            questState.setState(2);
            questState.setCond(0);
            questState.set("id", "0");
        }
        if (n == 30460 && n3 == 0) {
            if (n3 >= 15) {
                string = "30460-03.htm";
                questState.exitCurrentQuest(true);
                return string;
            }
            if (questState.getPlayer().getRace() != Race.elf) {
                return "30460-01.htm";
            }
            if (questState.getPlayer().getLevel() < 37) {
                string = "30460-02.htm";
                questState.exitCurrentQuest(true);
                return string;
            }
            string = "30460-03.htm";
            questState.setCond(1);
            return string;
        }
        if (n == 30460 && n3 == 1 && questState.getQuestItemsCount(3141) == 1L) {
            return "30460-05.htm";
        }
        if (n == 30460 && n3 == 1 && questState.getQuestItemsCount(3144) == 1L) {
            return "30460-06.htm";
        }
        if (n == 30460 && n3 == 1 && questState.getQuestItemsCount(3142) == 1L) {
            string = "30460-07.htm";
            questState.takeItems(3142, -1L);
            questState.giveItems(3140, 1L);
            if (!questState.getPlayer().getVarB("prof2.2")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(104591L, 11250L);
                questState.getPlayer().setVar("prof2.2", "1", -1L);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.unset("cond");
            questState.exitCurrentQuest(false);
            return string;
        }
        if (n == 30154 && n3 == 1 && questState.getQuestItemsCount(3141) == 1L) {
            return "30154-01.htm";
        }
        if (n == 30154 && n3 == 1 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3160) == 0L) {
            return "30154-08.htm";
        }
        if (n == 30154 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3160) > 0L) {
            string = "30154-09.htm";
            questState.takeItems(3160, 1L);
            questState.takeItems(3144, 1L);
            questState.giveItems(3142, 1L);
            return string;
        }
        if (n == 30154 && n3 == 1 && questState.getQuestItemsCount(3142) == 1L) {
            return "30154-10.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3143) > 0L) {
            return "30371-01.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3145) > 0L) {
            return "30371-04.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3149) > 0L) {
            return "30371-05.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3150) > 0L) {
            string = "30371-06.htm";
            questState.takeItems(3150, 1L);
            questState.giveItems(3146, 1L);
            return string;
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3146) > 0L) {
            return "30371-07.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3151) > 0L) {
            return "30371-08.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3155) > 0L) {
            return "30371-09.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3148) > 0L) {
            if (questState.getPlayer().getLevel() < 38) {
                return "30371-12.htm";
            }
            string = "30371-13.htm";
            questState.takeItems(3148, 1L);
            questState.giveItems(3147, 1L);
            return string;
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3147) > 0L) {
            return "30371-14.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3156) > 0L) {
            return "30371-15.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3026) > 0L && questState.getQuestItemsCount(3157) > 0L) {
            string = "30371-16.htm";
            questState.takeItems(3157, 1L);
            questState.giveItems(3158, 1L);
            return string;
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3026) > 0L && questState.getQuestItemsCount(3158) > 0L) {
            return "30371-17.htm";
        }
        if (n == 30371 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3159) > 0L) {
            string = "30371-18.htm";
            questState.takeItems(3159, 1L);
            questState.giveItems(3160, 1L);
            return string;
        }
        if (n == 30371 && n3 == 1) {
            if (questState.getQuestItemsCount(3142) > 0L) return "30371-19.htm";
        }
        if (questState.getQuestItemsCount(3160) > 0L && questState.getQuestItemsCount(3144) == 1L) {
            return "30371-19.htm";
        }
        if (n == 30300 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3145) > 0L) {
            return "30300-01.htm";
        }
        if (n == 30300 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3149) > 0L) {
            if (questState.getQuestItemsCount(3161) < 10L) return "30300-07.htm";
            if (questState.getQuestItemsCount(3162) < 20L) return "30300-07.htm";
            if (questState.getQuestItemsCount(3163) < 20L) return "30300-07.htm";
            return "30300-08.htm";
        }
        if (n == 30300 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3150) > 0L) {
            return "30300-11.htm";
        }
        if (n == 30300 && n3 == 1 && questState.getQuestItemsCount(3145) == 0L && questState.getQuestItemsCount(3149) == 0L && questState.getQuestItemsCount(3150) == 0L && questState.getQuestItemsCount(3144) == 1L) {
            return "30300-12.htm";
        }
        if (n == 30419 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3146) > 0L) {
            return "30419-01.htm";
        }
        if (n == 30419 && n3 == 1 && (questState.getQuestItemsCount(3152) > 0L || questState.getQuestItemsCount(3153) > 0L) && questState.getQuestItemsCount(3144) == 1L) {
            return "30419-05.htm";
        }
        if (n == 30419 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3154) > 0L) {
            string = "30419-06.htm";
            questState.takeItems(3151, 1L);
            questState.takeItems(3154, 1L);
            questState.giveItems(3155, 1L);
            return string;
        }
        if (n == 30419 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3155) > 0L) {
            return "30419-07.htm";
        }
        if (n == 30419 && n3 == 1 && questState.getQuestItemsCount(3146) == 0L && questState.getQuestItemsCount(3151) == 0L && questState.getQuestItemsCount(3154) == 0L && questState.getQuestItemsCount(3155) == 0L && questState.getQuestItemsCount(3144) == 1L) {
            return "30419-08.htm";
        }
        if (n == 30375 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3152) > 0L) {
            return "30375-01.htm";
        }
        if (n == 30375 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3153) > 0L) {
            if (questState.getQuestItemsCount(3164) < 20L) return "30375-03.htm";
            if (questState.getQuestItemsCount(3165) < 20L) return "30375-03.htm";
            string = "30375-04.htm";
            questState.takeItems(3164, questState.getQuestItemsCount(3164));
            questState.takeItems(3165, questState.getQuestItemsCount(3165));
            questState.takeItems(3153, 1L);
            questState.giveItems(3154, 1L);
            return string;
        }
        if (n == 30375 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3154) > 0L) {
            return "30375-05.htm";
        }
        if (n == 30375 && n3 == 1 && questState.getQuestItemsCount(3152) == 0L && questState.getQuestItemsCount(3153) == 0L && questState.getQuestItemsCount(3154) == 0L && questState.getQuestItemsCount(3144) == 1L) {
            return "30375-06.htm";
        }
        if (n == 30655 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3147) > 0L) {
            return "30655-01.htm";
        }
        if (n == 30655 && n3 == 1 && questState.getQuestItemsCount(3144) > 0L && questState.getQuestItemsCount(3156) > 0L) {
            if (questState.getQuestItemsCount(3166) <= 0L) return "30655-03.htm";
            if (questState.getQuestItemsCount(3167) <= 0L) return "30655-03.htm";
            if (questState.getQuestItemsCount(3168) <= 0L) return "30655-03.htm";
            if (questState.getQuestItemsCount(3169) <= 0L) return "30655-03.htm";
            if (questState.getQuestItemsCount(3170) <= 0L) return "30655-03.htm";
            if (questState.getQuestItemsCount(3171) <= 0L) return "30655-03.htm";
            string = "30655-04.htm";
            questState.takeItems(3166, 1L);
            questState.takeItems(3167, 1L);
            questState.takeItems(3168, 1L);
            questState.takeItems(3169, 1L);
            questState.takeItems(3170, 1L);
            questState.takeItems(3171, 1L);
            questState.takeItems(3156, 1L);
            questState.giveItems(3157, 1L);
            questState.giveItems(3026, 1L);
            return string;
        }
        if (n == 30655 && n3 == 1 && questState.getQuestItemsCount(3026) > 0L && questState.getQuestItemsCount(3157) > 0L) {
            return "30655-05.htm";
        }
        if (n == 30655 && n3 == 1) {
            if (questState.getQuestItemsCount(3158) > 0L) return "30655-06.htm";
        }
        if (questState.getQuestItemsCount(3160) > 0L) return "30655-06.htm";
        if (questState.getQuestItemsCount(3142) <= 0L) return string;
        if (questState.getQuestItemsCount(3144) != 1L) return string;
        return "30655-06.htm";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20550) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3149) == 1L && questState.getQuestItemsCount(3161) < 10L && Rnd.chance(50)) {
                questState.giveItems(3161, 1L);
                if (questState.getQuestItemsCount(3161) < 10L) {
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        } else if (n == 20176) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3149) == 1L && questState.getQuestItemsCount(3163) < 20L && Rnd.chance(50)) {
                questState.giveItems(3163, 1L);
                if (questState.getQuestItemsCount(3163) < 20L) {
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        } else if (n == 20082 || n == 20084 || n == 20086) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3149) == 1L && questState.getQuestItemsCount(3162) < 20L && Rnd.chance(80)) {
                questState.giveItems(3162, 1L);
                if (questState.getQuestItemsCount(3162) < 20L) {
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        } else if (n == 20087 || n == 20088) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3149) == 1L && questState.getQuestItemsCount(3162) < 20L && Rnd.chance(50)) {
                questState.giveItems(3162, 1L);
                if (questState.getQuestItemsCount(3162) < 20L) {
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        } else if (n == 20233) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3153) == 1L && questState.getQuestItemsCount(3164) < 20L && Rnd.chance(50)) {
                questState.giveItems(3164, 1L);
                if (questState.getQuestItemsCount(3164) < 20L) {
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        } else if (n == 20145) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getQuestItemsCount(3153) == 1L && questState.getQuestItemsCount(3165) < 20L && Rnd.chance(50)) {
                questState.giveItems(3165, 1L);
                if (questState.getQuestItemsCount(3165) < 20L) {
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        } else if (n == 27077) {
            if (n2 > 0 && questState.getQuestItemsCount(3144) == 1L && questState.getItemEquipped(7) == 3026 && questState.getQuestItemsCount(3158) == 1L && questState.getQuestItemsCount(3159) == 0L && questState.getQuestItemsCount(3026) > 0L) {
                questState.takeItems(3158, 1L);
                questState.takeItems(3026, 1L);
                questState.giveItems(3159, 1L);
            }
        } else if (n == 20581 || n == 20582) {
            questState.set("id", "0");
            if (n2 > 0 && questState.getQuestItemsCount(3156) == 1L && Rnd.chance(50)) {
                if (questState.getQuestItemsCount(3166) == 0L) {
                    questState.giveItems(3166, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (questState.getQuestItemsCount(3167) == 0L) {
                    questState.giveItems(3167, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (questState.getQuestItemsCount(3168) == 0L) {
                    questState.giveItems(3168, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (questState.getQuestItemsCount(3169) == 0L) {
                    questState.giveItems(3169, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (questState.getQuestItemsCount(3170) == 0L) {
                    questState.giveItems(3170, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (questState.getQuestItemsCount(3171) == 0L) {
                    questState.giveItems(3171, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        }
        return null;
    }
}

