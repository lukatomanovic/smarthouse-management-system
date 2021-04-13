CREATE DATABASE  IF NOT EXISTS `smarthouse` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smarthouse`;
-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: smarthouse
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `id` int NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `period` int DEFAULT NULL,
  `status` varchar(10) NOT NULL DEFAULT 'disabled',
  `songid` int DEFAULT NULL,
  `userid` int NOT NULL,
  `repetitioncount` int DEFAULT '0',
  `totalrepetitionnumber` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `songid_FK_alarm_idx` (`songid`),
  KEY `userid_FK_alarm_idx` (`userid`),
  CONSTRAINT `songid_FK_alarm` FOREIGN KEY (`songid`) REFERENCES `songs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userid_FK_alarm` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm`
--

LOCK TABLES `alarm` WRITE;
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
INSERT INTO `alarm` VALUES (5,'2021-02-25 01:18:00',0,'enabled',NULL,1,0,0),(6,'2021-02-24 20:54:00',0,'enabled',NULL,1,0,0),(7,'2021-02-25 03:19:00',0,'enabled',NULL,1,0,0),(8,'2021-03-01 00:27:00',15,'enabled',NULL,1,0,5),(9,'2021-03-29 00:27:00',15,'enabled',NULL,1,0,5),(10,'2021-02-27 10:00:00',10,'enabled',12,1,0,1),(11,'2021-02-28 13:00:00',10,'enabled',14,1,0,1),(13,'2021-03-15 10:13:00',0,'enabled',15,1,0,0),(15,'2021-02-26 03:00:00',0,'disabled',NULL,1,0,0),(16,'2021-02-26 10:00:00',0,'enabled',16,1,0,0),(17,'2021-02-26 10:47:00',0,'enabled',NULL,1,0,0);
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `planner`
--

DROP TABLE IF EXISTS `planner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `planner` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` int NOT NULL,
  `location` varchar(100) DEFAULT NULL,
  `time` datetime NOT NULL,
  `duration` int NOT NULL,
  `alarmid` int DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userid_FK_planner_idx` (`userid`),
  KEY `alarmid_FK_planner_idx` (`alarmid`),
  CONSTRAINT `alarmid_FK_planner` FOREIGN KEY (`alarmid`) REFERENCES `alarm` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userid_FK_planner` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `planner`
--

LOCK TABLES `planner` WRITE;
/*!40000 ALTER TABLE `planner` DISABLE KEYS */;
INSERT INTO `planner` VALUES (5,1,'Beograd','2021-02-25 02:25:00',45,5,'obaveza'),(6,1,'Novi Sad','2021-02-24 22:00:00',45,6,'obaveza2'),(7,1,'Novi Sad','2021-02-25 04:25:00',45,7,'obaveza3'),(10,1,'Beograd','2021-02-26 12:00:00',120,17,'odbrana IS1');
/*!40000 ALTER TABLE `planner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `songid` int NOT NULL,
  `userid` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userid_FK_idx` (`userid`),
  KEY `songid_FK_idx` (`songid`),
  CONSTRAINT `songid_FK` FOREIGN KEY (`songid`) REFERENCES `songs` (`id`),
  CONSTRAINT `userid_FK` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (1,9,1),(2,9,1),(3,9,1),(4,9,1),(5,9,1),(6,12,1),(7,13,1),(8,6,1),(9,9,1),(10,14,1),(11,6,1),(12,16,1);
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `songs`
--

DROP TABLE IF EXISTS `songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `songs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `url_UNIQUE` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `songs`
--

LOCK TABLES `songs` WRITE;
/*!40000 ALTER TABLE `songs` DISABLE KEYS */;
INSERT INTO `songs` VALUES (1,'usne boje vina','https://www.youtube.com/watch?v=cI1T5cQZ5qo'),(2,'Zeljko Samardzic - Udala se moja crna draga','https://www.youtube.com/watch?v=IDFxDgCTzOM'),(3,'Dado Glišić - Sto bespuća','https://www.youtube.com/watch?v=bJH-ur8Iswc'),(4,'Zeljko Vasic - Zena kao ti - (Audio 2004) HD','https://www.youtube.com/watch?v=ieQ3LJYGK-g'),(5,'BOBAN RAJOVIĆ - INTERVENTNA (OFFICIAL VIDEO)','https://www.youtube.com/watch?v=HEwT7CRTkAU'),(6,'Mile Kitic - Zasto bas ti - (Audio 2002)','https://www.youtube.com/watch?v=hTCRK5E0WWg'),(7,'YU DANCE HITOVI 90s','https://www.youtube.com/watch?v=NgLGOwR-Dr4'),(8,'Vlado Georgiev feat Niggor - Tropski bar - (Official Video)','https://www.youtube.com/watch?v=AWD416NTNKE'),(9,'Boban Rajović - Lijepa žena (Official Video)','https://www.youtube.com/watch?v=X2kyMqWPaRc'),(11,'Luis Fonsi - Despacito ft. Daddy Yankee','https://www.youtube.com/watch?v=kJQP7kiw5Fk'),(12,'Halid Beslic - Hej lijepa zeno - (Audio 1986)','https://www.youtube.com/watch?v=ry2qKtg_25A'),(13,'Dzej i Mina - Slavija - (Tv Pink)','https://www.youtube.com/watch?v=sIpECHUtVtI'),(14,'Halid Beslic - Romanija (Official Video 2015)','https://www.youtube.com/watch?v=OPK8tu7TOY4'),(15,'Baja Mali Knindza - Ptico moja bijeli labude (BN Music)','https://www.youtube.com/watch?v=vpCH8H0Mr0U'),(16,'Neda Ukraden - Zora je (OFFICIAL VIDEO)','https://www.youtube.com/watch?v=8tHCKUbOW1Y');
/*!40000 ALTER TABLE `songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `address` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'lukat','lukat','Beograd'),(3,'perap','perap','Nis');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-26  7:04:16
