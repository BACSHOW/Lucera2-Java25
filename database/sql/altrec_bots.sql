CREATE TABLE IF NOT EXISTS `altrec_bots` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `face` TINYINT(3) unsigned DEFAULT NULL,
    `hairStyle` TINYINT(3) unsigned DEFAULT NULL,
    `hairColor` TINYINT(3) unsigned DEFAULT NULL,
    `sex` TINYINT(1) DEFAULT NULL,
    `x` MEDIUMINT(9) DEFAULT NULL,
    `y` MEDIUMINT(9) DEFAULT NULL,
    `z` MEDIUMINT(9) DEFAULT NULL,
    `is_noble` BOOLEAN NOT NULL DEFAULT '0',
    CONSTRAINT `pk_altrec_bots` PRIMARY KEY (`id`)
) ENGINE=MyISAM;