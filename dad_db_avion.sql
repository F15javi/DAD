-- MySQL dump 10.13  Distrib 8.0.28, for macos11 (x86_64)
--
-- Host: localhost    Database: dad_db_avion
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `airport`
--

DROP TABLE IF EXISTS `airport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airport` (
  `id_Airport` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  PRIMARY KEY (`id_Airport`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airport`
--

LOCK TABLES `airport` WRITE;
/*!40000 ALTER TABLE `airport` DISABLE KEYS */;
/*!40000 ALTER TABLE `airport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fly`
--

DROP TABLE IF EXISTS `fly`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fly` (
  `id_Fly` int NOT NULL AUTO_INCREMENT,
  `id_AirportDest` int NOT NULL,
  `id_AirportOrig` int NOT NULL,
  `plate` varchar(20) NOT NULL,
  `time_Dep` bigint NOT NULL,
  `time_Arr` bigint NOT NULL,
  PRIMARY KEY (`id_Fly`),
  KEY `id_AirportDest_idx` (`id_AirportDest`),
  KEY `id_AirportOrig_idx` (`id_AirportOrig`),
  CONSTRAINT `id_AirportDest` FOREIGN KEY (`id_AirportDest`) REFERENCES `airport` (`id_Airport`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `id_AirportOrig` FOREIGN KEY (`id_AirportOrig`) REFERENCES `airport` (`id_Airport`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fly`
--

LOCK TABLES `fly` WRITE;
/*!40000 ALTER TABLE `fly` DISABLE KEYS */;
/*!40000 ALTER TABLE `fly` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gps`
--

DROP TABLE IF EXISTS `gps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gps` (
  `id_Gps` int NOT NULL AUTO_INCREMENT,
  `id_Fly` int NOT NULL,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  `dir` double NOT NULL,
  `vel` double NOT NULL,
  `alt` double NOT NULL,
  `time` bigint NOT NULL,
  PRIMARY KEY (`id_Gps`),
  UNIQUE KEY `id_Gps_UNIQUE` (`id_Gps`),
  KEY `id_Fly_idx` (`id_Fly`),
  CONSTRAINT `id_Fly` FOREIGN KEY (`id_Fly`) REFERENCES `fly` (`id_Fly`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gps`
--

LOCK TABLES `gps` WRITE;
/*!40000 ALTER TABLE `gps` DISABLE KEYS */;
/*!40000 ALTER TABLE `gps` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-31 18:23:42
