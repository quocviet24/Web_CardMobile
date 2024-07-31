-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: localhost    Database: digitalgrocery
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

drop database IF EXISTS DigitalGrocery;

-- Create the DigitalGrocery database
CREATE DATABASE DigitalGrocery;

-- Use the DigitalGrocery database
USE DigitalGrocery;
set time_zone = '+07:00';
--
-- Table structure for table `card_types`
--

DROP TABLE IF EXISTS `card_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `publisher_id` int NOT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `in_stock` int NOT NULL,
  `sold_quantity` int NOT NULL,
  `is_deleted` tinyint(1) NOT NULL,
  `deleted_date` datetime(6) DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `created_by` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `publisher_id` (`publisher_id`),
  CONSTRAINT `card_types_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_types`
--

LOCK TABLES `card_types` WRITE;
/*!40000 ALTER TABLE `card_types` DISABLE KEYS */;
INSERT INTO `card_types` VALUES (1,1,10000.00,0,4,1,'2024-07-27 18:32:06.465000',0,'2024-01-01 00:00:00.000000',2),(2,1,20000.00,0,4,1,'2024-07-27 18:40:13.445000',0,'2024-01-01 00:00:00.000000',2),(3,1,50000.00,4,0,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(4,2,100000.00,4,2,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(5,2,200000.00,4,0,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(6,2,500000.00,3,0,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(7,2,10000.00,2,4,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(8,3,20000.00,0,3,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(9,3,50000.00,4,0,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(10,3,100000.00,4,0,0,NULL,NULL,'2024-01-01 00:00:00.000000',2),(11,1,100000.00,0,0,0,NULL,NULL,'2024-07-26 17:00:00.000000',2),(12,1,200000.00,0,0,0,NULL,NULL,'2024-07-26 17:00:00.000000',2),(13,1,500000.00,0,0,0,NULL,NULL,'2024-07-26 17:00:00.000000',2),(14,2,50000.00,0,0,0,NULL,NULL,'2024-07-26 17:00:00.000000',2),(15,3,10000.00,0,0,0,NULL,NULL,'2024-07-26 17:00:00.000000',2);
/*!40000 ALTER TABLE `card_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cards`
--

DROP TABLE IF EXISTS `cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cards` (
  `id` int NOT NULL AUTO_INCREMENT,
  `card_type_id` int NOT NULL,
  `seri_number` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `card_number` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `expiry_date` date NOT NULL,
  `is_deleted` tinyint(1) NOT NULL,
  `deleted_date` date DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  `created_date` date NOT NULL,
  `created_by` int NOT NULL,
  `last_updated` date DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `card_type_id` (`card_type_id`),
  CONSTRAINT `cards_ibfk_1` FOREIGN KEY (`card_type_id`) REFERENCES `card_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cards`
--

LOCK TABLES `cards` WRITE;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` VALUES (1,1,'SER12345','CARD12545','2025-12-31',1,NULL,NULL,'2024-01-01',1,NULL,NULL),(2,2,'SER12346','CARD12245','2025-12-31',1,NULL,NULL,'2024-01-01',1,NULL,NULL),(3,3,'SER12347','CARD11345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(4,4,'SER02345','CARD14345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(5,5,'SER12335','CARD12345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(6,6,'SER12005','CARD17345','2025-12-31',1,'2024-07-28',6,'2024-01-01',1,NULL,NULL),(7,7,'SER10015','CARD02345','2025-12-31',1,'2024-07-27',2,'2024-01-01',1,NULL,NULL),(8,8,'SER12305','CARD82345','2025-12-31',1,'2024-07-28',8,'2024-01-01',1,NULL,NULL),(9,9,'SER00325','CARD62345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(10,10,'SER12145','CARD12545','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(11,1,'SER10395','CARD12375','2025-12-31',1,'2024-07-27',7,'2024-01-01',1,NULL,NULL),(12,2,'SER01375','CARD12385','2025-12-31',1,NULL,NULL,'2024-01-01',1,NULL,NULL),(13,3,'SER16345','CARD12325','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(14,4,'SER14345','CARD12305','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(15,5,'SER11345','CARD32345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(16,6,'SER10345','CARD52345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(17,7,'SER21345','CARD23345','2025-12-31',1,'2024-07-27',2,'2024-01-01',1,NULL,NULL),(18,8,'SER32345','CARD34345','2025-12-31',1,'2024-07-28',8,'2024-01-01',1,NULL,NULL),(19,9,'SER52345','CARD56345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(20,10,'SER72345','CARD60345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(21,1,'BER10045','CARD000545','2025-12-31',1,'2024-07-28',8,'2024-01-01',1,NULL,NULL),(22,2,'BER01375','CARD012385','2025-12-31',1,'2024-07-27',2,'2024-01-01',1,NULL,NULL),(23,3,'BER16345','CARD012325','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(24,4,'BER14345','CARD012305','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(25,5,'BER11345','CARD032345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(26,6,'BER10345','CARD052345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(27,7,'BER21345','CARD023345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(28,8,'BER32345','CARD034345','2025-12-31',1,'2024-07-28',6,'2024-01-01',1,NULL,NULL),(29,9,'BER52345','CARD056345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(30,10,'BER72345','CARD060345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(31,1,'VER10045','CARD100545','2025-12-31',1,'2024-07-28',8,'2024-01-01',1,NULL,NULL),(32,2,'VER01375','CARD112385','2025-12-31',1,'2024-07-28',8,'2024-01-01',1,NULL,NULL),(33,3,'VER16345','CARD112325','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(34,4,'VER14345','CARD112305','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(35,5,'VER11345','CARD132345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(36,6,'VER10345','CARD152345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(37,7,'VER21345','CARD123345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(38,8,'VER32345','CARD134345','2025-12-31',1,'2024-07-28',6,'2024-01-01',1,NULL,NULL),(39,9,'VER52345','CARD156345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL),(40,10,'VER72345','CARD160345','2025-12-31',0,NULL,NULL,'2024-01-01',1,NULL,NULL);
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `card_type_id` int NOT NULL,
  `quantity` int NOT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `card_type_id` (`card_type_id`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`card_type_id`) REFERENCES `card_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (2,1,2,2,20000.00),(3,2,2,1,20000.00),(4,2,7,2,30000.00),(6,2,5,3,600000.00),(7,7,3,4,200000.00);
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `card_type_id` int NOT NULL,
  `publisher_name` varchar(50) DEFAULT NULL,
  `unit_price` double NOT NULL,
  `card_id` int DEFAULT NULL,
  `seri_number` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `card_number` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `card_type_id` (`card_type_id`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`card_type_id`) REFERENCES `card_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
INSERT INTO `order_details` VALUES (1,1,1,'Viettel',10000,1,'SER12345','CARD12545','2025-12-31 00:00:00.000000'),(2,2,2,'Viettel',20000,2,'SER12346','CARD12245','2025-12-31 00:00:00.000000'),(3,3,2,'Viettel',20000,12,'SER01375','CARD12385','2025-12-31 00:00:00.000000'),(4,4,4,'MobiFone',100000,0,'MOB02391','CARD12345','2025-12-31 00:00:00.000000'),(5,5,4,'MobiFone',100000,-1,'MOB12391','CARD12345','2025-12-31 00:00:00.000000'),(6,6,7,'MobiFone',10000,-2,'MOB02392','CARD12345','2025-12-31 00:00:00.000000'),(7,6,7,'MobiFone',10000,-3,'MOB02393','CARD12345','2025-12-31 00:00:00.000000'),(8,7,1,'Viettel',10000,NULL,NULL,NULL,NULL),(9,8,8,'Vinaphone',20000,NULL,NULL,NULL,NULL),(10,8,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(11,9,8,'Vinaphone',20000,NULL,NULL,NULL,NULL),(12,10,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(13,11,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(14,11,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(15,11,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(16,11,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(17,11,9,'Vinaphone',50000,NULL,NULL,NULL,NULL),(18,12,1,'Viettel',10000,NULL,NULL,NULL,NULL),(19,12,1,'Viettel',10000,NULL,NULL,NULL,NULL),(20,12,2,'Viettel',20000,NULL,NULL,NULL,NULL),(21,12,2,'Viettel',20000,NULL,NULL,NULL,NULL),(22,13,6,'MobileFone',500000,NULL,NULL,NULL,NULL),(23,14,4,'MobileFone',100000,NULL,NULL,NULL,NULL),(24,15,5,'MobileFone',200000,NULL,NULL,NULL,NULL),(25,16,2,'Viettel',20000,NULL,NULL,NULL,NULL),(26,16,2,'Viettel',20000,NULL,NULL,NULL,NULL),(27,17,8,'VinaPhone',20000,NULL,NULL,NULL,NULL),(28,18,10,'VinaPhone',100000,NULL,NULL,NULL,NULL),(29,19,2,'Viettel',20000,22,'BER01375','CARD012385','2025-12-30 17:00:00.000000'),(30,19,7,'MobileFone',10000,7,'SER10015','CARD02345','2025-12-30 17:00:00.000000'),(31,20,7,'MobileFone',10000,17,'SER21345','CARD23345','2025-12-30 17:00:00.000000'),(32,21,3,'Viettel',50000,NULL,NULL,NULL,NULL),(33,21,3,'Viettel',50000,NULL,NULL,NULL,NULL),(34,21,3,'Viettel',50000,NULL,NULL,NULL,NULL),(35,21,3,'Viettel',50000,NULL,NULL,NULL,NULL),(36,22,1,'Viettel',10000,11,'SER10395','CARD12375','2025-12-30 17:00:00.000000'),(37,23,1,'Viettel',10000,21,'BER10045','CARD000545','2025-12-30 17:00:00.000000'),(38,23,8,'VinaPhone',20000,8,'SER12305','CARD82345','2025-12-30 17:00:00.000000'),(39,24,1,'Viettel',10000,31,'VER10045','CARD100545','2025-12-30 17:00:00.000000'),(40,24,8,'VinaPhone',20000,18,'SER32345','CARD34345','2025-12-30 17:00:00.000000'),(41,25,2,'Viettel',20000,32,'VER01375','CARD112385','2025-12-30 17:00:00.000000'),(42,26,2,'Viettel',20000,NULL,NULL,NULL,NULL),(43,27,8,'VinaPhone',20000,28,'BER32345','CARD034345','2025-12-30 17:00:00.000000');
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_pendings`
--

DROP TABLE IF EXISTS `order_pendings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_pendings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  `card_type_id` int DEFAULT NULL,
  `order_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj18yhx10lqbyn7ev1bimkq3kl` (`card_type_id`),
  KEY `FK7nl1d58i7cdmrlajlna49sse` (`order_id`),
  CONSTRAINT `FK7nl1d58i7cdmrlajlna49sse` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKj18yhx10lqbyn7ev1bimkq3kl` FOREIGN KEY (`card_type_id`) REFERENCES `card_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_pendings`
--

LOCK TABLES `order_pendings` WRITE;
/*!40000 ALTER TABLE `order_pendings` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_pendings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `total_money` decimal(10,2) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `note` varchar(150) DEFAULT NULL,
  `order_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,10000.00,'Completed',NULL,'2024-05-13 00:00:00'),(2,3,20000.00,'Completed',NULL,'2024-07-14 00:00:00'),(3,4,20000.00,'Completed',NULL,'2024-07-15 00:00:00'),(4,2,100000.00,'Completed',NULL,'2024-06-12 00:00:00'),(5,1,100000.00,'Completed',NULL,'2024-06-23 00:00:00'),(6,2,20000.00,'Completed',NULL,'2024-06-23 00:00:00'),(7,3,10000.00,'Pending',NULL,'2024-07-15 00:00:00'),(8,4,70000.00,'Pending',NULL,'2024-07-16 00:00:00'),(9,5,20000.00,'Error',NULL,'2024-07-16 00:00:00'),(10,2,50000.00,'Reject',NULL,'2024-07-14 00:00:00'),(11,4,250000.00,'Pending',NULL,'2024-07-16 00:00:00'),(12,1,60000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-26 16:34:45'),(13,1,500000.00,'Pending',NULL,'2024-07-26 16:40:55'),(14,1,100000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-26 16:45:38'),(15,1,200000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-26 16:49:55'),(16,2,40000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-27 15:31:25'),(17,2,20000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-27 15:31:48'),(18,2,100000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-27 15:33:42'),(19,2,30000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 15:56:41'),(20,2,10000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 16:00:43'),(21,7,200000.00,'Cancel','Bạn đã hủy thanh toán đơn hàng này','2024-07-27 16:32:09'),(22,7,10000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 16:33:50'),(23,8,30000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 18:21:16'),(24,8,30000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 18:30:53'),(25,8,20000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 18:39:40'),(26,1,20000.00,'Error','Đơn hàng này chứa mặt hàng ngừng hoạt động, vui lòng liên hệ với admin để được hỗ trợ','2024-07-27 18:39:42'),(27,6,20000.00,'Completed','Đơn hàng thanh toán thành công','2024-07-27 18:48:01');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `publishers`
--

DROP TABLE IF EXISTS `publishers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publishers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `image` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `is_deleted` tinyint(1) NOT NULL,
  `deleted_date` datetime(6) DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `publishers`
--

LOCK TABLES `publishers` WRITE;
/*!40000 ALTER TABLE `publishers` DISABLE KEYS */;
INSERT INTO `publishers` VALUES (1,'Viettel','viettel.jpg',0,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(2,'MobileFone','mobilephone.jpg',0,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(3,'VinaPhone','vinaphone.jpg',0,NULL,NULL,'2023-05-19 00:00:00.000000',1,NULL,NULL);
/*!40000 ALTER TABLE `publishers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `queue_transaction`
--

DROP TABLE IF EXISTS `queue_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `queue_transaction` (
  `order_id` int NOT NULL,
  `order_transaction` int NOT NULL,
  `user_id` int NOT NULL,
  KEY `order_id` (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `queue_transaction_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `queue_transaction_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `queue_transaction`
--

LOCK TABLES `queue_transaction` WRITE;
/*!40000 ALTER TABLE `queue_transaction` DISABLE KEYS */;
INSERT INTO `queue_transaction` VALUES (13,55443,1);
/*!40000 ALTER TABLE `queue_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'admin'),(2,'customer');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tokens`
--

DROP TABLE IF EXISTS `tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tokens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `expiry_date_time` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `token_type` enum('PASSWORD_RESET','EMAIL_CHANGE','REGISTRATION') DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tokens`
--

LOCK TABLES `tokens` WRITE;
/*!40000 ALTER TABLE `tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` varchar(50) NOT NULL,
  `amount` double DEFAULT NULL,
  `transaction_date` datetime DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `order_id` int DEFAULT NULL,
  `account_name` varchar(100) DEFAULT NULL,
  `account_number` varchar(50) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `bank_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES ('38697bf5f87d4a35848a44b3214b3a5e',30000,'2024-07-27 15:57:29','PAID',19,'null','970422XXXX1958',2,'Q0002h5ilg  CASSO1565 4 2dbsl8SjTXWCZ1OjVxD9ujwC Thanh toan don hang','97042292'),('39e9d28a07de4c3690e93faff2087e76',10000,'2024-07-27 16:01:26','PAID',20,'VND-TGTT-NGUYEN TRAM ANHVIETNAM','19038310194016',2,'Q0002h5ip8  CASSO1565 4 s58VYPPQTRGBvC31X1qkHA7c Thanh toan don hang FT24211402402800 Trace 934291','888899'),('60a71a93ea8a41178e78fe7416601b5c',20000,'2024-07-27 18:40:05','PAID',25,'VND-TGTT-NGUYEN TRAM ANHVIETNAM','19038310194016',8,'Q0002h5omm  CASSO1565 4 bpFlHAhI7mxjlSP3QdM9irbH Thanh toan don hang FT24211240235000 Trace 089618','888899'),('6f565fb15222422caff2887b669efaa9',20000,'2024-07-27 18:40:13','PAID',26,'null','970422XXXX1958',1,'Q0002h5omo  CASSO1565 4 I0zizwTLv2fpn9VmgvNtFiTi Thanh toan don hang','97042292'),('7e9788b4f6554abba803421a50ab1422',20000,'2024-07-27 18:48:26','PAID',27,'HOANG DANG QUOC VIET412 NTMK','9021527927481',6,'Q0002h5onq  CASSO1565 4 WyDKcO31RQzAoaqnmLIbDouA Thanh toan don hang Trace 607075','963388'),('a0c2709676014a699dfe43c78b2fe55e',30000,'2024-07-27 18:22:00','PAID',23,'VND-TGTT-NGUYEN TRAM ANHVIETNAM','19038310194016',8,'Q0002h5npk  CASSO1565 4 iAaW2q3pVdLsofY6TZT4rRsh Thanh toan don hang FT24211564968834 Trace 079940','888899'),('bc487dae0e3b4a00a894ab199f36cb9c',10000,'2024-07-27 16:34:32','PAID',22,'null','970422XXXX7420',7,'Q0002h5ko6  CASSO1565 4 OI64cvp8Mxo6d7PlV2nlbH26 Thanh toan don hang','97042292');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` char(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `first_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `last_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `role_id` int NOT NULL,
  `is_deleted` tinyint(1) NOT NULL,
  `is_verified` tinyint(1) NOT NULL,
  `deleted_date` datetime(6) DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'dung2002ss','25d55ad283aa400af464c76d713c07ad','dung2002ss@gmail.com',NULL,'Dung','Le',2,0,1,'2024-07-26 17:00:00.000000',2,'2024-05-19 00:00:00.000000',1,'2024-07-26 17:00:00.000000',2),(2,'swp391_g1','25d55ad283aa400af464c76d713c07ad','nghiemngoc2912@gmail.com',NULL,'Admin',NULL,1,0,1,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(3,'nghiemngoc291204','25d55ad283aa400af464c76d713c07ad','nghiemngoc291204@gmail.com',NULL,'Ngoc','Nghiem',2,0,1,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(4,'ngocnthhe186888','25d55ad283aa400af464c76d713c07ad','ngocnthhe186888@fpt.edu.vn',NULL,'Ngoc','Nghiem',1,0,1,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(5,'ntramanh1204','25d55ad283aa400af464c76d713c07ad','ntramanh1204@gmail.com',NULL,'Anh','Nguyen',2,0,1,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(6,'anhnthe182498','25d55ad283aa400af464c76d713c07ad','anhnthe182498@fpt.edu.vn',NULL,'Anh','Nguyen',1,0,1,NULL,NULL,'2024-05-19 00:00:00.000000',1,NULL,NULL),(7,'Đàm  Thị Phương Thảo','9d80012e9ec62976acc723e10a8d5209','dtpt666666@gmail.com','0982392660','Đàm ','Thảo',2,0,1,NULL,NULL,'2024-07-26 17:00:00.000000',NULL,NULL,NULL),(8,'Quoc','a741ca84-5641-41b7-b411-fc6577db3fce','nishikatakagi24@gmail.com',NULL,'Quoc Viet',NULL,2,0,1,NULL,NULL,'2024-07-27 17:00:00.000000',NULL,NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-28  2:37:46
