DROP TABLE IF EXISTS `altrec_skills`;
CREATE TABLE IF NOT EXISTS `altrec_skills` (
    `bot_id` INT NOT NULL,
    `skill_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`skill_level` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
    CONSTRAINT `pk_altrec_skills` PRIMARY KEY (`bot_id`, `skill_id`),
    KEY `k_bot_id` (`bot_id`)
) ENGINE=MyISAM;
