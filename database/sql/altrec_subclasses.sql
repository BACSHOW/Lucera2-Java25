CREATE TABLE IF NOT EXISTS `altrec_subclasses` (
    `bot_id` INT NOT NULL,
    `class_id` TINYINT UNSIGNED NOT NULL,
	`exp` bigint UNSIGNED NOT NULL DEFAULT '0',
    `active` BOOLEAN NOT NULL DEFAULT '0',
	`is_base` BOOLEAN NOT NULL DEFAULT '0',
    CONSTRAINT `pk_altrec_subclasses` PRIMARY KEY (`bot_id`, `class_id`),
    KEY `k_bot_id` (`bot_id`)
) ENGINE=MyISAM;