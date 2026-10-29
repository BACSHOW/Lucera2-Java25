CREATE TABLE `promocodes_ip_limit` (
  `ip` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  PRIMARY KEY (`ip`,`code`)
);