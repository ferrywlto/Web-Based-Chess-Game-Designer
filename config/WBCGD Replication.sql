-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.27-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema wbcgd
--

CREATE DATABASE IF NOT EXISTS wbcgd;
USE wbcgd;

--
-- Definition of table `chess`
--

DROP TABLE IF EXISTS `chess`;
CREATE TABLE `chess` (
  `UID` varchar(8) NOT NULL COMMENT 'user id',
  `name` varchar(45) NOT NULL COMMENT 'chess name',
  `description` varchar(45) NOT NULL COMMENT 'chess description',
  `location` varchar(45) NOT NULL COMMENT 'chess file location'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `chess`
--

/*!40000 ALTER TABLE `chess` DISABLE KEYS */;
INSERT INTO `chess` (`UID`,`name`,`description`,`location`) VALUES 
 ('frog','first chess','my first chess','frog_chess.xml'),
 ('subai','subai1','first','real.xml'),
 ('frog','sec chess','sec chess f','frog_chess2.xml'),
 ('frog','3rd Chess','3 desc','frog_chess3.xml'),
 ('subai','subai2','second','real2.xml'),
 ('ferry','basic','basic chess test 1','real.xml'),
 ('ferry','extend','extend chess test','real2.xml');
/*!40000 ALTER TABLE `chess` ENABLE KEYS */;


--
-- Definition of table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `UID` varchar(8) NOT NULL COMMENT 'User identification field',
  `UPass` varchar(8) NOT NULL COMMENT 'User password field',
  PRIMARY KEY  (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table to store user name and password';

--
-- Dumping data for table `user`
--

/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`UID`,`UPass`) VALUES 
 ('ferry','subai'),
 ('frog','meimei'),
 ('subai','nana');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
