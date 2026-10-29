CREATE TABLE IF NOT EXISTS  `accounts_bonuses` (
  `account` VARCHAR(45) NOT NULL,
  `expireTime` INT(11) NOT NULL,
  `rateXp` DOUBLE NOT NULL,
  `rateSp` DOUBLE NOT NULL,
  `rateRaidXp` DOUBLE NOT NULL,
  `rateRaidSp` DOUBLE NOT NULL,
  `questRewardRate` DOUBLE NOT NULL,
  `questRewardAdenaRate` DOUBLE NOT NULL,
  `questDropRate` DOUBLE NOT NULL,
  `dropAdena` DOUBLE NOT NULL,
  `dropItems` DOUBLE NOT NULL,
  `dropRaidItems` DOUBLE NOT NULL,
  `dropSpoil` DOUBLE NOT NULL,
  `enchantItemBonus` DOUBLE DEFAULT '1.0',
  `hwidsLimit` INT NULL DEFAULT '1',
  PRIMARY KEY (`account`)
);