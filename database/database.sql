CREATE DATABASE  IF NOT EXISTS `xettuyen2026` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `xettuyen2026`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: xettuyen2026
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `xt_bangquydoi`
--

DROP TABLE IF EXISTS `xt_bangquydoi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_bangquydoi` (
  `idqd` int NOT NULL AUTO_INCREMENT,
  `d_phuongthuc` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `d_tohop` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `d_mon` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `d_diema` decimal(6,2) DEFAULT NULL,
  `d_diemb` decimal(6,2) DEFAULT NULL,
  `d_diemc` decimal(6,2) DEFAULT NULL,
  `d_diemd` decimal(6,2) DEFAULT NULL,
  `d_maquydoi` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `d_phanvi` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`idqd`),
  UNIQUE KEY `d_maquydoi_UNIQUE` (`d_maquydoi`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_bangquydoi`
--

LOCK TABLES `xt_bangquydoi` WRITE;
/*!40000 ALTER TABLE `xt_bangquydoi` DISABLE KEYS */;
INSERT INTO `xt_bangquydoi` VALUES (1,'DGNL','A01',NULL,988.00,1018.00,25.25,26.75,'DGNL_A01_2','2'),(2,'DGNL','A01',NULL,984.00,997.00,25.75,26.10,'DGNL_A01_3','3'),(3,'DGNL','A01',NULL,973.00,983.00,25.35,25.65,'DGNL_A01_4','4'),(4,'DGNL','A01',NULL,962.00,972.00,25.05,25.25,'DGNL_A01_5','5'),(5,'DGNL','A01',NULL,954.00,961.00,24.80,25.00,'DGNL_A01_6','6'),(6,'VSAT','','TO',114.50,122.50,7.00,7.75,'VSAT_A01_TO_4','4'),(7,'VSAT','','TO',108.00,114.50,6.60,7.00,'VSAT_A01_TO_5','5'),(8,'VSAT','','TO',102.50,108.00,6.25,6.60,'VSAT_A01_TO_6','6'),(9,'VSAT','','LI',105.00,112.50,8.50,9.00,'VSAT_A01_LI_4','4'),(10,'VSAT','','LI',99.50,105.00,8.00,8.50,'VSAT_A01_LI_5','5'),(11,'VSAT','','LI',94.50,99.50,7.75,8.00,'VSAT_A01_LI_6','6'),(12,'VSAT','','HO',107.50,117.00,8.25,8.75,'VSAT_A01_HO_4','4'),(13,'VSAT','','HO',100.50,107.50,7.75,8.25,'VSAT_A01_HO_5','5'),(14,'VSAT','','HO',94.00,100.50,7.25,7.75,'VSAT_A01_HO_6','6'),(15,'VSAT','','SI',112.50,120.50,7.85,8.34,'VSAT_A01_SI_4','4'),(16,'VSAT','','SI',105.50,112.50,7.50,7.85,'VSAT_A01_SI_5','5'),(17,'VSAT','','SI',100.00,105.50,7.25,7.50,'VSAT_A01_SI_6','6'),(18,'VSAT','','SU',120.50,126.50,9.00,9.25,'VSAT_A01_SU_4','4'),(19,'VSAT','','SU',115.00,120.50,8.50,9.00,'VSAT_A01_SU_5','5'),(20,'VSAT','','SU',110.00,115.00,8.25,8.50,'VSAT_A01_SU_6','6'),(21,'VSAT','','DI',108.50,115.50,9.25,9.75,'VSAT_A01_DI_4','4'),(22,'VSAT','','DI',103.00,108.50,9.00,9.25,'VSAT_A01_DI_5','5'),(23,'VSAT','','DI',98.50,103.00,8.75,9.00,'VSAT_A01_DI_6','6'),(24,'VSAT','','VA',119.50,124.00,8.75,9.00,'VSAT_A01_VA_4','4'),(25,'VSAT','','VA',115.50,119.50,8.50,8.75,'VSAT_A01_VA_5','5'),(26,'VSAT','','VA',112.50,115.50,8.25,8.50,'VSAT_A01_VA_6','6');
/*!40000 ALTER TABLE `xt_bangquydoi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_diemcongxetuyen`
--

DROP TABLE IF EXISTS `xt_diemcongxetuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_diemcongxetuyen` (
  `iddiemcong` int unsigned NOT NULL AUTO_INCREMENT,
  `ts_cccd` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `manganh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '0.00',
  `matohop` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '0.00',
  `phuongthuc` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `diemCC` decimal(6,2) DEFAULT NULL,
  `diemUtxt` decimal(6,2) DEFAULT NULL,
  `diemTong` decimal(6,2) DEFAULT '0.00',
  `ghichu` text COLLATE utf8mb4_unicode_ci,
  `dc_keys` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`iddiemcong`),
  UNIQUE KEY `dc_keys_UNIQUE` (`dc_keys`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_diemcongxetuyen`
--

LOCK TABLES `xt_diemcongxetuyen` WRITE;
/*!40000 ALTER TABLE `xt_diemcongxetuyen` DISABLE KEYS */;
INSERT INTO `xt_diemcongxetuyen` VALUES (1,'001204000001','7480201','A01','PT1',0.50,1.50,2.00,'Chứng chỉ IELTS + Ưu tiên KV','001204000001_7480201_A01_PT1'),(2,'001204000002','7480101','B00','PT2',1.00,NULL,1.00,'Giải HSG Tỉnh','001204000002_7480101_B00_PT2'),(3,'001204000003','7480201','A01','PT1',NULL,2.00,2.00,'Ưu tiên đối tượng chính sách','001204000003_7480201_A01_PT1'),(4,'001204000004','7480103','D01','PT1',0.50,NULL,0.50,'IELTS 6.0','001204000004_7480103_D01_PT1'),(5,'001204000005','7480201','C01','PT1',1.50,1.00,2.50,'IELTS 7.0 + KV1','001204000005_7480201_C01_PT1');
/*!40000 ALTER TABLE `xt_diemcongxetuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_diemthixettuyen`
--

DROP TABLE IF EXISTS `xt_diemthixettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_diemthixettuyen` (
  `iddiemthi` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sobaodanh` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TO` decimal(8,2) DEFAULT '0.00',
  `LI` decimal(8,2) DEFAULT '0.00',
  `HO` decimal(8,2) DEFAULT '0.00',
  `SI` decimal(8,2) DEFAULT '0.00',
  `SU` decimal(8,2) DEFAULT '0.00',
  `DI` decimal(8,2) DEFAULT '0.00',
  `VA` decimal(8,2) DEFAULT '0.00',
  `N1_THI` decimal(8,2) DEFAULT NULL COMMENT 'Điểm thi gốc',
  `N1_CC` decimal(8,2) DEFAULT '0.00' COMMENT 'max(N1_Thi, N1_QD)',
  `CNCN` decimal(8,2) DEFAULT '0.00',
  `CNNN` decimal(8,2) DEFAULT '0.00',
  `TI` decimal(8,2) DEFAULT '0.00',
  `KTPL` decimal(8,2) DEFAULT '0.00',
  `VSAT_TO` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Toán',
  `VSAT_LI` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Lý',
  `VSAT_HO` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Hóa',
  `VSAT_SI` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Sinh',
  `VSAT_SU` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Sử',
  `VSAT_DI` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Địa',
  `VSAT_VA` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Văn',
  `VSAT_N1` decimal(8,2) DEFAULT NULL COMMENT 'VSAT - Tiếng Anh',
  `NL1` decimal(8,2) DEFAULT NULL,
  `NK1` decimal(8,2) DEFAULT NULL,
  `NK2` decimal(8,2) DEFAULT NULL,
  `NK3` decimal(8,2) DEFAULT NULL,
  `NK4` decimal(8,2) DEFAULT NULL,
  `NK5` decimal(8,2) DEFAULT NULL,
  `NK6` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`iddiemthi`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_diemthixettuyen`
--

LOCK TABLES `xt_diemthixettuyen` WRITE;
/*!40000 ALTER TABLE `xt_diemthixettuyen` DISABLE KEYS */;
INSERT INTO `xt_diemthixettuyen` VALUES (1,'001204000001','SGU001',8.50,7.00,6.50,0.00,0.00,0.00,7.50,8.00,9.00,8.00,0.00,7.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'001204000002','SGU002',9.00,8.50,9.00,0.00,0.00,0.00,6.00,7.50,7.50,0.00,0.00,0.00,8.50,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'001204000003','SGU003',7.00,0.00,0.00,0.00,8.00,8.50,8.00,6.00,6.00,0.00,0.00,0.00,7.50,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'001204000004','SGU004',0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,850.00,NULL,NULL,NULL,NULL,NULL,NULL),(5,'001204000005','SGU005',8.00,8.00,8.00,8.00,8.00,8.00,8.00,8.00,8.00,8.00,0.00,8.00,8.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'001204000006','SGU006',7.50,0.00,0.00,0.00,0.00,0.00,7.00,0.00,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,'001204000007','SGU007',6.50,6.00,5.50,5.00,7.00,7.50,6.50,5.00,5.00,8.00,0.00,0.00,7.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'001204000008','SGU008',0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,920.00,NULL,NULL,NULL,NULL,NULL,NULL),(9,'001204000009','SGU009',9.50,9.00,9.50,0.00,0.00,0.00,8.00,9.00,10.00,9.00,0.00,9.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,'001204000010','SGU010',5.00,5.00,5.00,5.00,5.00,5.00,5.00,5.00,5.00,5.00,5.00,5.00,5.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `xt_diemthixettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nganh`
--

DROP TABLE IF EXISTS `xt_nganh`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nganh` (
  `idnganh` int NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `tennganh` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `n_tohopgoc` varchar(3) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_chitieu` int NOT NULL DEFAULT '0',
  `n_diemsan` decimal(10,2) DEFAULT NULL,
  `n_diemtrungtuyen` decimal(10,2) DEFAULT NULL,
  `n_tuyenthang` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_dgnl` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_thpt` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `n_vsat` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `sl_xtt` int DEFAULT NULL,
  `sl_dgnl` int DEFAULT NULL,
  `sl_vsat` int DEFAULT NULL,
  `sl_thpt` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`idnganh`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nganh`
--

LOCK TABLES `xt_nganh` WRITE;
/*!40000 ALTER TABLE `xt_nganh` DISABLE KEYS */;
INSERT INTO `xt_nganh` VALUES (7,'7340101','Quản trị kinh doanh','A01',150,17.50,24.50,'1','1','1','0',15,40,0,'95'),(8,'7310101','Ngành Kinh tế','D01',180,17.00,0.00,'1','0','1','0',10,0,0,'170'),(9,'7140209','Ngành Sư phạm Toán','A00',100,16.50,0.00,'1','1','1','1',10,20,20,'50'),(10,'7220201','Ngôn ngữ Anh','D01',120,18.50,0.00,'1','1','1','0',10,30,0,'80'),(11,'7480201','Công nghệ thông tin','A01',200,18.00,26.00,'1','1','1','1',20,50,30,'100');
/*!40000 ALTER TABLE `xt_nganh` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nganh_tohop`
--

DROP TABLE IF EXISTS `xt_nganh_tohop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nganh_tohop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `matohop` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `th_mon1` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `hsmon1` tinyint DEFAULT NULL,
  `th_mon2` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `hsmon2` tinyint DEFAULT NULL,
  `th_mon3` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `hsmon3` tinyint DEFAULT NULL,
  `tb_keys` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'manganh_matohop',
  `N1` tinyint(1) DEFAULT NULL,
  `TO` tinyint(1) DEFAULT NULL,
  `LI` tinyint(1) DEFAULT NULL,
  `HO` tinyint(1) DEFAULT NULL,
  `SI` tinyint(1) DEFAULT NULL,
  `VA` tinyint(1) DEFAULT NULL,
  `SU` tinyint(1) DEFAULT NULL,
  `DI` tinyint(1) DEFAULT NULL,
  `TI` tinyint(1) DEFAULT NULL,
  `KHAC` tinyint(1) DEFAULT NULL,
  `KTPL` tinyint(1) DEFAULT NULL,
  `dolech` decimal(6,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`tb_keys`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nganh_tohop`
--

LOCK TABLES `xt_nganh_tohop` WRITE;
/*!40000 ALTER TABLE `xt_nganh_tohop` DISABLE KEYS */;
INSERT INTO `xt_nganh_tohop` VALUES (11,'7480201','A01','TO',1,'LI',1,'N1',1,'7480201_A01',0,1,1,1,0,0,0,0,0,0,0,0.00),(12,'7480201','D01','TO',1,'VA',1,'N1',1,'7480201_D01',1,1,0,0,0,1,0,0,0,0,0,0.00),(13,'7340101','A01','TO',1,'LI',1,'N1',1,'7340101_A01',1,1,1,0,0,0,0,0,0,0,0,0.00),(14,'7140209','A00','TO',1,'LI',1,'HO',1,'7140209_A00',0,1,1,1,0,0,0,0,0,0,0,0.00),(15,'7220201','D01','TO',1,'VA',1,'N1',1,'7220201_D01',1,1,0,0,0,1,0,0,0,0,0,0.00);
/*!40000 ALTER TABLE `xt_nganh_tohop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nguyenvongxettuyen`
--

DROP TABLE IF EXISTS `xt_nguyenvongxettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nguyenvongxettuyen` (
  `idnv` int NOT NULL AUTO_INCREMENT,
  `nn_cccd` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nv_manganh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nv_tt` int NOT NULL,
  `diem_thxt` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm môn chính',
  `diem_utqd` decimal(10,5) DEFAULT NULL COMMENT 'Điểm UTQD theo tổ họp sẽ khác nhau.',
  `diem_cong` decimal(6,2) DEFAULT NULL COMMENT 'Tong 3 mon chua tinh mon chinh + diem uu tien\\\\\\\\n',
  `diem_xettuyen` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm ưu tiên',
  `nv_ketqua` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `nv_keys` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `tt_phuongthuc` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tt_thm` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`idnv`),
  UNIQUE KEY `nv_keys_UNIQUE` (`nv_keys`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nguyenvongxettuyen`
--

LOCK TABLES `xt_nguyenvongxettuyen` WRITE;
/*!40000 ALTER TABLE `xt_nguyenvongxettuyen` DISABLE KEYS */;
INSERT INTO `xt_nguyenvongxettuyen` VALUES (24,'001204000001','7480201',1,24.50000,1.00000,0.50,26.00000,'TRÚNG TUYỂN','001204000001_7480201_PT1','PT1','A01'),(27,'001204000001','7340101',2,24.50000,0.00000,0.00,24.50000,'Rớt','001204000001_7340101_PT1','PT1','A01');
/*!40000 ALTER TABLE `xt_nguyenvongxettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_thisinhxettuyen25`
--

DROP TABLE IF EXISTS `xt_thisinhxettuyen25`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_thisinhxettuyen25` (
  `idthisinh` int NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sobaodanh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ho` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `ten` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `ngay_sinh` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dien_thoai` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `gioi_tinh` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `noi_sinh` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `doi_tuong` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `khu_vuc` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`idthisinh`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_thisinhxettuyen25`
--

LOCK TABLES `xt_thisinhxettuyen25` WRITE;
/*!40000 ALTER TABLE `xt_thisinhxettuyen25` DISABLE KEYS */;
/*!40000 ALTER TABLE `xt_thisinhxettuyen25` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_tohop_monthi`
--

DROP TABLE IF EXISTS `xt_tohop_monthi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_tohop_monthi` (
  `idtohop` int NOT NULL AUTO_INCREMENT,
  `matohop` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `mon1` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `mon2` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `mon3` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `tentohop` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`idtohop`),
  UNIQUE KEY `matohop_UNIQUE` (`matohop`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_tohop_monthi`
--

LOCK TABLES `xt_tohop_monthi` WRITE;
/*!40000 ALTER TABLE `xt_tohop_monthi` DISABLE KEYS */;
INSERT INTO `xt_tohop_monthi` VALUES (4,'A01','TO','LI','VA','Toán Vật lý Tiếng Anh'),(5,'B00','TO','HO','SI','Toán Hóa Sinh'),(6,'A00','TO','LI','HO','Toán Lý Hóa'),(8,'D01','TO','VA','N1','Toán Văn Tiếng Anh'),(9,'B01','TO','SI','SU','Toán Sinh Sử');
/*!40000 ALTER TABLE `xt_tohop_monthi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_users`
--

DROP TABLE IF EXISTS `xt_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'user',
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_users`
--

LOCK TABLES `xt_users` WRITE;
/*!40000 ALTER TABLE `xt_users` DISABLE KEYS */;
INSERT INTO `xt_users` VALUES (1,'admin','123456','admin',1),(2,'user1','123456','user',1),(3,'hehe','123','admin',1);
/*!40000 ALTER TABLE `xt_users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-21 13:38:31
