-- phpMyAdmin SQL Dump
-- version 4.1.7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Lug 13, 2014 alle 23:42
-- Versione del server: 5.1.71-community-log
-- PHP Version: 5.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `my_whoareyou`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `Account`
--

CREATE TABLE IF NOT EXISTS `Account` (
  `identificativo` varchar(20) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(10) NOT NULL,
  `numero` varchar(12) NOT NULL,
  `email` varchar(30) NOT NULL,
  `citta` varchar(20) NOT NULL,
  `immagine` blob NOT NULL,
  `data_nascita` varchar(10) NOT NULL,
  `sesso` varchar(1) NOT NULL,
  `dati_condivisi` varchar(5) NOT NULL,
  `posizione_condivisa` tinyint(1) NOT NULL,
  PRIMARY KEY (`identificativo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `Amicizia`
--

CREATE TABLE IF NOT EXISTS `Amicizia` (
  `id_richiedente` varchar(20) NOT NULL,
  `id_ricevente` varchar(20) NOT NULL,
  `statoAmicizia` int(1) NOT NULL,
  `conversazione` tinyint(1) NOT NULL,
  PRIMARY KEY (`id_richiedente`,`id_ricevente`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `Coordinate`
--

CREATE TABLE IF NOT EXISTS `Coordinate` (
  `latitudine` varchar(10) NOT NULL,
  `longitudine` varchar(10) NOT NULL,
  PRIMARY KEY (`latitudine`,`longitudine`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `Messaggio_Bacheca`
--

CREATE TABLE IF NOT EXISTS `Messaggio_Bacheca` (
  `Account_identificativo` varchar(20) NOT NULL,
  `data_ora` datetime NOT NULL,
  `oggetto` varchar(250) NOT NULL,
  `Coord_latitudine` varchar(10) NOT NULL,
  `Coord_longitudine` varchar(10) NOT NULL,
  PRIMARY KEY (`Account_identificativo`,`data_ora`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `Messaggio_Privato`
--

CREATE TABLE IF NOT EXISTS `Messaggio_Privato` (
  `mittente` varchar(20) NOT NULL,
  `destinatario` varchar(20) NOT NULL,
  `data_ora` datetime NOT NULL,
  `oggetto` varchar(250) NOT NULL,
  `Coord_latitudine` varchar(10) NOT NULL,
  `Coord_longitudine` varchar(10) NOT NULL,
  `statoMessaggio` tinyint(1) NOT NULL,
  `statoEliminazione` int(1) NOT NULL,
  PRIMARY KEY (`mittente`,`destinatario`,`data_ora`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `Registrazione_Posizione`
--

CREATE TABLE IF NOT EXISTS `Registrazione_Posizione` (
  `Account_identificativo` varchar(20) NOT NULL,
  `data_ora` datetime NOT NULL,
  `Coord_latitudine` varchar(10) NOT NULL,
  `Coord_longitudine` varchar(10) NOT NULL,
  PRIMARY KEY (`Account_identificativo`,`data_ora`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
