DROP TABLE IF EXISTS `altrec_items`;
CREATE TABLE IF NOT EXISTS `altrec_items` (
    `bot_id` INT NOT NULL,
    `item_type` MEDIUMINT UNSIGNED NOT NULL DEFAULT '0',
    `amount` BIGINT NOT NULL DEFAULT '0',
    `is_equipped` BOOLEAN NOT NULL DEFAULT '0',
    `enchant` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
    KEY `k_bot_id` (`bot_id`)
) ENGINE=MyISAM;