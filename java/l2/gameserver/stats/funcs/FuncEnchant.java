/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.funcs;

import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.templates.item.WeaponTemplate;

public class FuncEnchant
extends Func {
    public FuncEnchant(Stats stats, int n, Object object, double d) {
        super(stats, n, object);
    }

    private int b(ItemInstance itemInstance) {
        if (itemInstance == null) {
            return 0;
        }
        int n = itemInstance.getOwnerId();
        Player player = GameObjectsStorage.getPlayer(n);
        int n2 = itemInstance.getEnchantLevel();
        int n3 = -1;
        if (player != null) {
            if (player.isOlyParticipant()) {
                if (itemInstance.isArmor()) {
                    n3 = Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR;
                } else if (itemInstance.isWeapon()) {
                    n3 = itemInstance.getTemplate().isMageItem() ? Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE : Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS;
                } else if (itemInstance.isAccessory()) {
                    n3 = Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY;
                }
                if (n3 < 0) {
                    return n2;
                }
                return Math.min(n2, n3);
            }
            if (Config.PVP_EVENTS_RESTRICT_ENCHANT && player.getTeam() != TeamType.NONE) {
                if (itemInstance.isArmor()) {
                    n3 = Config.PVP_EVENTS_RESTRICT_ENCHANT_ARMOR_LEVEL;
                } else if (itemInstance.isWeapon()) {
                    n3 = itemInstance.getTemplate().isMageItem() ? Config.PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_MAGE : Config.PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_PHYS;
                } else if (itemInstance.isAccessory()) {
                    n3 = Config.PVP_EVENTS_RESTRICT_ENCHANT_ACCESSORY;
                }
                if (n3 < 0) {
                    return n2;
                }
                return Math.min(n2, n3);
            }
            if (!Config.ENCHANT_LIMIT_ZONE_NAMES.isEmpty()) {
                for (Zone zone : player.getZones()) {
                    if (!Config.ENCHANT_LIMIT_ZONE_NAMES.contains(zone.getName())) continue;
                    if (itemInstance.isArmor()) {
                        n3 = Config.ENCHANT_LIMIT_ZONE_ARMOR_LEVEL;
                    } else if (itemInstance.isWeapon()) {
                        n3 = itemInstance.getTemplate().isMageItem() ? Config.ENCHANT_LIMIT_ZONE_WEAPON_MAGE : Config.ENCHANT_LIMIT_ZONE_WEAPON_PHYS;
                    } else if (itemInstance.isAccessory()) {
                        n3 = Config.ENCHANT_LIMIT_ZONE_ACCESSORY;
                    }
                    if (n3 < 0) {
                        return n2;
                    }
                    return Math.min(n2, n3);
                }
            }
        }
        return n2;
    }

    @Override
    public void calc(Env env) {
        ItemInstance itemInstance = (ItemInstance)this.owner;
        int n = this.b(itemInstance);
        int n2 = Math.max(0, n - 3);
        switch (this.stat) {
            case SHIELD_DEFENCE: 
            case MAGIC_DEFENCE: 
            case POWER_DEFENCE: {
                env.value += (double)(n + n2 * 2);
                return;
            }
            case MAGIC_ATTACK: {
                switch (itemInstance.getTemplate().getCrystalType().cry) {
                    case 1462: {
                        env.value += (double)(4 * (n + n2));
                        break;
                    }
                    case 1461: {
                        env.value += (double)(3 * (n + n2));
                        break;
                    }
                    case 1460: {
                        env.value += (double)(3 * (n + n2));
                        break;
                    }
                    case 1459: {
                        env.value += (double)(3 * (n + n2));
                        break;
                    }
                    case 0: 
                    case 1458: {
                        env.value += (double)(2 * (n + n2));
                    }
                }
                return;
            }
            case POWER_ATTACK: {
                ItemType itemType = itemInstance.getItemType();
                boolean bl = itemType == WeaponTemplate.WeaponType.BOW;
                boolean bl2 = (itemType == WeaponTemplate.WeaponType.DUALFIST || itemType == WeaponTemplate.WeaponType.DUAL || itemType == WeaponTemplate.WeaponType.BIGSWORD || itemType == WeaponTemplate.WeaponType.SWORD) && itemInstance.getTemplate().getBodyPart() == 16384;
                switch (itemInstance.getTemplate().getCrystalType().cry) {
                    case 1462: {
                        if (bl) {
                            env.value += (double)(10 * (n + n2));
                            break;
                        }
                        if (bl2) {
                            env.value += (double)(6 * (n + n2));
                            break;
                        }
                        env.value += (double)(5 * (n + n2));
                        break;
                    }
                    case 1461: {
                        if (bl) {
                            env.value += (double)(8 * (n + n2));
                            break;
                        }
                        if (bl2) {
                            env.value += (double)(5 * (n + n2));
                            break;
                        }
                        env.value += (double)(4 * (n + n2));
                        break;
                    }
                    case 1459: 
                    case 1460: {
                        if (bl) {
                            env.value += (double)(6 * (n + n2));
                            break;
                        }
                        if (bl2) {
                            env.value += (double)(4 * (n + n2));
                            break;
                        }
                        env.value += (double)(3 * (n + n2));
                        break;
                    }
                    case 0: 
                    case 1458: {
                        if (bl) {
                            env.value += (double)(4 * (n + n2));
                            break;
                        }
                        env.value += (double)(2 * (n + n2));
                    }
                }
                return;
            }
            case POWER_ATTACK_SPEED: 
            case MAGIC_ATTACK_SPEED: 
            case MAX_HP: 
            case MAX_MP: 
            case MAX_CP: {
                env.value += 1.5 * (double)(n + n2);
                return;
            }
        }
    }
}

