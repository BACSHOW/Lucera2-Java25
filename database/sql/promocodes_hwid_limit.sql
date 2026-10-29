CREATE TABLE `promocodes_hwid_limit` (
  `hwid` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  PRIMARY KEY (`hwid`,`code`)
);