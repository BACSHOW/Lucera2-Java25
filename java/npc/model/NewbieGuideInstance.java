/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import quests._201_HfighterTutorial;
import quests._202_HmageTutorial;
import quests._203_ElfTutorial;
import quests._204_DelfTutorial;
import quests._205_OrcTutorial;
import quests._206_DwarfTutorial;
import quests._255_Tutorial;

public class NewbieGuideInstance
extends NpcInstance {
    private static final long serialVersionUID = 92437418203893683L;
    private static final int aya = 5789;
    private static final int ayb = 5790;
    private static final int ayc = 8542;

    public NewbieGuideInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        boolean bl;
        QuestState questState = player.getQuestState(_255_Tutorial.class);
        QuestState questState2 = player.getQuestState(_201_HfighterTutorial.class);
        QuestState questState3 = player.getQuestState(_202_HmageTutorial.class);
        QuestState questState4 = player.getQuestState(_203_ElfTutorial.class);
        QuestState questState5 = player.getQuestState(_204_DelfTutorial.class);
        QuestState questState6 = player.getQuestState(_205_OrcTutorial.class);
        QuestState questState7 = player.getQuestState(_206_DwarfTutorial.class);
        boolean bl2 = bl = player.getClassId().getRace() != Race.orc && player.getClassId().isMage();
        if (questState != null && questState.getInt("tutorial_quest_ex") < 5) {
            questState.set("tutorial_quest_ex", "5");
            if (Functions.getItemCount(player, 5789) <= 400L && Functions.getItemCount(player, 5790) <= 200L) {
                if (!bl) {
                    player.sendPacket((IStaticPacket)new PlaySound(PlaySound.Type.VOICE, "tutorial_voice_026", 0, 0, player.getLoc()));
                    Functions.addItem(player, 5789, 200L);
                } else {
                    player.sendPacket((IStaticPacket)new PlaySound(PlaySound.Type.VOICE, "tutorial_voice_027", 0, 0, player.getLoc()));
                    Functions.addItem(player, 5790, 100L);
                }
            }
            Functions.addItem(player, 8542, 12L);
            if (questState2 != null && questState2.isStarted()) {
                questState2.exitCurrentQuest(false);
            } else if (questState3 != null && questState3.isStarted()) {
                questState3.exitCurrentQuest(false);
            } else if (questState4 != null && questState4.isStarted()) {
                questState4.exitCurrentQuest(false);
            } else if (questState5 != null && questState5.isStarted()) {
                questState5.exitCurrentQuest(false);
            } else if (questState6 != null && questState6.isStarted()) {
                questState6.exitCurrentQuest(false);
            } else if (questState7 != null && questState7.isStarted()) {
                questState7.exitCurrentQuest(false);
            }
        }
        switch (this.getNpcId()) {
            case 30598: {
                this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot001.htm", new Object[0]);
                break;
            }
            case 30599: {
                this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios001.htm", new Object[0]);
                break;
            }
            case 30600: {
                this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia001.htm", new Object[0]);
                break;
            }
            case 30601: {
                this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin001.htm", new Object[0]);
                break;
            }
            case 30602: {
                this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai001.htm", new Object[0]);
                break;
            }
            case 31076: {
                this.showChatWindow(player, "newbiehelper/guide_gludin_nina/guide_gludin_nina001.htm", new Object[0]);
                break;
            }
            case 31077: {
                this.showChatWindow(player, "newbiehelper/guide_gludio_euria/guide_gludio_euria001.htm", new Object[0]);
                break;
            }
            case 32327: {
                this.showChatWindow(player, "newbiehelper/blessing_benefector/blessing_benefector001.htm", new Object[0]);
                break;
            }
            default: {
                super.showChatWindow(player, n, objectArray);
            }
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!NewbieGuideInstance.canBypassCheck(player, this)) {
            return;
        }
        if (player == null) {
            return;
        }
        if (string.startsWith("menu_select?ask=-7&")) {
            if (string.endsWith("reply_1")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        if (player.getRace() != Race.human) {
                            this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot003.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() > 20 || player.getClassId().level() != 0) {
                            this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot002.htm", new Object[0]);
                            break;
                        }
                        if (player.getClassId().level() != 0) break;
                        if (!player.isMageClass()) {
                            if (player.getLevel() <= 5) {
                                this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_f05.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 10) {
                                this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_f10.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 15) {
                                this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_f15.htm", new Object[0]);
                                break;
                            }
                            this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_f20.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 7) {
                            this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_m07.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 14) {
                            this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_m14.htm", new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_m20.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        if (player.getRace() != Race.elf) {
                            this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios003.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() > 20 || player.getClassId().level() != 0) {
                            this.showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_elf_roios/guide_elf_roios002.htm", new Object[0]);
                            break;
                        }
                        if (player.getClassId().level() != 0) break;
                        if (!player.isMageClass()) {
                            if (player.getLevel() <= 5) {
                                this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_f05.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 10) {
                                this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_f10.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 15) {
                                this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_f15.htm", new Object[0]);
                                break;
                            }
                            this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_f20.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 7) {
                            this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_m07.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 14) {
                            this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_m14.htm", new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_m20.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        if (player.getRace() != Race.darkelf) {
                            this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia003.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() > 20 || player.getClassId().level() != 0) {
                            this.showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_delf_frankia/guide_delf_frankia002.htm", new Object[0]);
                            break;
                        }
                        if (player.getClassId().level() != 0) break;
                        if (!player.isMageClass()) {
                            if (player.getLevel() <= 5) {
                                this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_f05.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 10) {
                                this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_f10.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 15) {
                                this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_f15.htm", new Object[0]);
                                break;
                            }
                            this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_f20.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 7) {
                            this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_m07.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 14) {
                            this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_m14.htm", new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_m20.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        if (player.getRace() != Race.dwarf) {
                            this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin003.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() > 20 || player.getClassId().level() != 0) {
                            this.showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_dwarf_gullin/guide_dwarf_gullin002.htm", new Object[0]);
                            break;
                        }
                        if (player.getClassId().level() != 0) break;
                        if (player.getLevel() <= 5) {
                            this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_f05.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 10) {
                            this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_f10.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 15) {
                            this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_f15.htm", new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_f20.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        if (player.getRace() != Race.orc) {
                            this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai003.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() > 20 || player.getClassId().level() != 0) {
                            this.showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_orc_tanai/guide_orc_tanai002.htm", new Object[0]);
                            break;
                        }
                        if (player.getClassId().level() != 0) break;
                        if (!player.isMageClass()) {
                            if (player.getLevel() <= 5) {
                                this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_f05.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 10) {
                                this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_f10.htm", new Object[0]);
                                break;
                            }
                            if (player.getLevel() <= 15) {
                                this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_f15.htm", new Object[0]);
                                break;
                            }
                            this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_f20.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 7) {
                            this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_m07.htm", new Object[0]);
                            break;
                        }
                        if (player.getLevel() <= 14) {
                            this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_m14.htm", new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_m20.htm", new Object[0]);
                    }
                }
            }
        } else if (string.startsWith("menu_select?ask=255&")) {
            if (string.endsWith("reply_10")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_11")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04a.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04a.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04a.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04a.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04a.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_12")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04b.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04b.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04b.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04b.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04b.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_13")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04c.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04c.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04c.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04c.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04c.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_14")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04d.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04d.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04d.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04d.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04d.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_15")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04e.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04e.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04e.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04e.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04e.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_16")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04f.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04f.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04f.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04f.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04f.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_17")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04g.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04g.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04g.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04g.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04g.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_18")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04h.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_04h.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_04h.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04h.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04h.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_19")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_04i.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_04i.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_31")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84108, 244604, -3729, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46926, 51511, -2977, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(9670, 15537, -4574, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115072, -178176, -906, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45264, -112512, -235, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_32")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82236, 241573, -3728, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44995, 51706, -2803, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(15120, 15656, -4376, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117847, -182339, -1537, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46576, -117311, -242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_33")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82515, 241221, -3728, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45727, 51721, -2803, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(17306, 13592, -3724, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116617, -184308, -1569, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-47360, -113791, -237, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_34")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82319, 244709, -3727, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42812, 51138, -2996, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(15272, 16310, -4377, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117826, -182576, -1537, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-47360, -113424, -235, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_35")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82659, 244992, -3717, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45487, 46511, -2996, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(6449, 19619, -3694, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116378, -184308, -1571, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45744, -117165, -236, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_36")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86114, 244682, -3727, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47401, 51764, -2996, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-15404, 71131, -3445, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115183, -176728, -791, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46528, -109968, -250, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_37")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86328, 244448, -3724, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42971, 51372, -2996, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(7496, 17388, -4377, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114969, -176752, -790, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45808, -110055, -255, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_38")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86322, 241215, -3727, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47595, 51569, -2996, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(17102, 13002, -3743, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117366, -178725, -1118, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45731, -113844, -237, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_39")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85964, 240947, -3727, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45778, 46534, -2996, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(6532, 19903, -3693, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117378, -178914, -1120, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45728, -113360, -237, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_40")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85026, 242689, -3729, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44476, 47153, -2984, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-15648, 71405, -3451, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116226, -178529, -948, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45952, -114784, -199, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_41")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83789, 240799, -3717, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42700, 50057, -2984, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(7644, 18048, -4377, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116190, -178441, -948, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45952, -114496, -199, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_42")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84204, 240403, -3717, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42766, 50037, -2984, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-1301, 75883, -3566, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116016, -178615, -948, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45863, -112621, -200, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_43")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86385, 243267, -3717, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44683, 46952, -2981, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-1152, 76125, -3566, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116190, -178615, -948, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45864, -112540, -199, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_44")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86733, 242918, -3717, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44667, 46896, -2982, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10580, 17574, -4554, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116103, -178407, -948, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43264, -112532, -220, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_45")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84516, 245449, -3714, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45725, 52105, -2795, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(12009, 15704, -4554, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116103, -178653, -948, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43910, -115518, -194, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_46")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84729, 245001, -3726, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44823, 52414, -2795, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11951, 15661, -4554, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115468, -182446, -1434, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43950, -115457, -194, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_47")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84965, 245222, -3726, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45000, 52101, -2795, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10761, 17970, -4554, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115315, -182155, -1444, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44416, -111486, -222, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_48")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84981, 244764, -3726, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45919, 52414, -2795, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10823, 18013, -4554, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115271, -182692, -1445, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43926, -111794, -222, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_49")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85186, 245001, -3726, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44692, 52261, -2795, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11283, 14226, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115900, -177316, -915, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43109, -113770, -221, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_50")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83326, 242964, -3718, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47780, 49568, -2983, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10447, 14620, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116268, -177524, -914, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43114, -113404, -221, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_51")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83020, 242553, -3718, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47912, 50170, -2983, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11258, 14431, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115741, -181645, -1344, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46768, -113610, -3, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_52")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83175, 243065, -3718, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47868, 50167, -2983, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10344, 14445, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116192, -181072, -1344, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46802, -114011, -112, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_53")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82809, 242751, -3718, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28928, 74248, -3773, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10315, 14293, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115205, -180024, -870, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46247, -113866, -21, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_54")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-81895, 243917, -3721, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(43673, 49683, -3046, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10775, 14190, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114716, -180018, -871, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46808, -113184, -112, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_55")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-81840, 243534, -3721, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45610, 49008, -3059, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11235, 14078, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114832, -179520, -871, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45328, -114736, -237, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_56")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-81512, 243424, -3720, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(50592, 54986, -3376, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11012, 14128, -4242, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115717, -183488, -1483, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30602: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44624, -111873, -238, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_orc_tanai/guide_orc_tanai_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_57")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84436, 242793, -3729, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42978, 49115, -2994, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13380, 17430, -4542, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115618, -183265, -1483, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_58")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-78939, 240305, -3443, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46475, 50495, -3058, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13464, 17751, -4541, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114348, -178537, -813, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_59")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85301, 244587, -3725, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45859, 50827, -3058, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13763, 17501, -4542, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114990, -177294, -854, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_60")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83163, 243560, -3728, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(51210, 82474, -3283, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44225, 79721, -3652, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114426, -178672, -812, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_61")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-97131, 258946, -3622, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30599: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(49262, 53607, -3216, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_elf_roios/guide_elf_roios_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44015, 79683, -3652, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114409, -178415, -812, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_62")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-114685, 222291, -2925, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(25856, 10832, -3724, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117061, -181867, -1413, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_63")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84057, 242832, -3729, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(12328, 14947, -4574, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116164, -184029, -1507, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_64")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-100332, 238019, -3573, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13081, 18444, -4573, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115563, -182923, -1448, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_65")) {
                switch (this.getNpcId()) {
                    case 30598: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82041, 242718, -3725, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30600: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(12311, 17470, -4574, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_delf_frankia/guide_delf_frankia_q0255_05.htm", new Object[0]);
                        break;
                    }
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(112656, -174864, -611, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            } else if (string.endsWith("reply_66")) {
                switch (this.getNpcId()) {
                    case 30601: {
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116852, -183595, -1566, player), 200L);
                        this.showChatWindow(player, "newbiehelper/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm", new Object[0]);
                    }
                }
            }
        } else if (string.startsWith("Teleport")) {
            if (player.getLevel() >= 20) {
                this.showChatWindow(player, "newbiehelper/guide_teleport_over001.htm", new Object[0]);
            } else {
                super.onBypassFeedback(player, string);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    private class RadarTask
    extends RunnableImpl {
        private final int x;
        private final int y;
        private final int z;
        private final Player Nr;

        RadarTask(int n, int n2, int n3, Player player) {
            this.x = n;
            this.y = n2;
            this.z = n3;
            this.Nr = player;
        }

        @Override
        public void runImpl() throws Exception {
            this.Nr.sendPacket((IStaticPacket)new RadarControl(0, 1, this.x, this.y, this.z));
        }
    }
}

