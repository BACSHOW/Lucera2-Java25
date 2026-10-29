DROP TABLE IF EXISTS `altrec_actions`;
CREATE TABLE IF NOT EXISTS `altrec_actions` (
    `bot_id` INT NOT NULL,
    `ord` INT NOT NULL,
    `action_type` VARCHAR(32) CHARACTER SET utf8 NOT NULL,
    `duration` INT NOT NULL DEFAULT -1,
    `body` TINYTEXT CHARACTER SET utf8 NOT NULL,
    CONSTRAINT `pk_altrec_actions` PRIMARY KEY (`bot_id`, `ord`),
    KEY `k_bot_id` (`bot_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;