CREATE DATABASE  IF NOT EXISTS `pirates`;
USE `pirates`;
--
-- Table structure for table `game_mode`
--

--DROP TABLE IF EXISTS `game_mode`;
CREATE TABLE IF NOT EXISTS `game_mode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

LOCK TABLES `game_mode` WRITE;
INSERT INTO `game_mode` VALUES (1,'MODE_DM');
INSERT INTO `game_mode` VALUES (2,'MODE_TEAMDM');
INSERT INTO `game_mode` VALUES (3,'MODE_KOTH');
INSERT INTO `game_mode` VALUES (4,'MODE_CTF');
UNLOCK TABLES;

--
-- Table structure for table `game`
--

--DROP TABLE IF EXISTS `game`;
CREATE TABLE IF NOT EXISTS `game` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `max_players` varchar(11) NOT NULL,
  `game_mode_id` int(11) NOT NULL,
  `map_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`game_mode_id`) REFERENCES `game_mode` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`map_id`) REFERENCES `map` (`id`) ON DELETE CASCADE ON UPDATE CASCADE 
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Table structure for table `map`
--

--DROP TABLE IF EXISTS `map`;
CREATE TABLE IF NOT EXISTS `map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `size` int(11) DEFAULT 32,
  `island` int(11) NOT NULL,
  `crate` int(11) NOT NULL,
  `bottle` int(11) NOT NULL,
  `booty` int(11) NOT NULL,
  `tree` int(11) NOT NULL
  PRIMARY KEY (`id`),
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Table structure for table `role`
--

--DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

LOCK TABLES `role` WRITE;
INSERT INTO `role` VALUES (1,'ROLE_USER');
INSERT INTO `role` VALUES (2,'ROLE_ADMIN');
UNLOCK TABLES;

--
-- Table structure for table `user`
-- ALTER TABLE user ADD COLUMN score INT(11) DEFAULT 0 NOT NULL;

--DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `score` int(11) NOT NULL,
  `kill` int(11) NOT NULL,
  `death` int(11) NOT NULL,
  `ship_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`ship_id`) REFERENCES `ship` (`id`) ON DELETE CASCADE ON UPDATE CASCADE 
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Table structure for table `ship`
--

--DROP TABLE IF EXISTS `ship`;
CREATE TABLE IF NOT EXISTS `ship` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `hp` int(11) NOT NULL,
  `crew` int(11) NOT NULL,
  `wood` int(11) NOT NULL,
  `sail` int(11) NOT NULL,  
  PRIMARY KEY (`id`),
  FOREIGN KEY (`map_id`) REFERENCES `map` (`id`) ON DELETE CASCADE ON UPDATE CASCADE 
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Table structure for table `user_role`
--

--DROP TABLE IF EXISTS `user_role`;
CREATE TABLE IF NOT EXISTS `user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_role_roleid_idx` (`role_id`),
  CONSTRAINT `fk_user_role_roleid` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_userid` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
