 create database worklink;

 use worklink;

CREATE TABLE education (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Hide BOOLEAN NOT NULL,
    Qualification VARCHAR(255),
    Specialization VARCHAR(255)
);

CREATE TABLE location (
    id INT AUTO_INCREMENT PRIMARY KEY,
    City VARCHAR(255),
    Country VARCHAR(255),
    State VARCHAR(255),
    Validated BOOLEAN NOT NULL,
    ArrAlias JSON,
    LatLong JSON
);

CREATE TABLE Labour (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(255),
    SecondName VARCHAR(255),
    Number VARCHAR(20),
    CountryCode VARCHAR(10),
    Gender VARCHAR(10),
    Dob DATETIME,
    Education INT,
    Experience DOUBLE,
    PerDayCharge DOUBLE,
    Location JSON,
    AlternateNumber VARCHAR(20),
    AlternateCountryCode VARCHAR(10),
    Photo BLOB,
    Age INT,
    EmailId VARCHAR(255),
    AboutMe TEXT,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdateDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (Education) REFERENCES Education(Id)
 );

CREATE TABLE JobPoster (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(255),
    SecondName VARCHAR(255),
    Number VARCHAR(20),
    CountryCode VARCHAR(10),
    Gender VARCHAR(10),
    Location INT,
    AlternateNumber VARCHAR(20),
    AlternateCountryCode VARCHAR(10),
    Photo BLOB,
    EmailId VARCHAR(255),
    AboutMe TEXT,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdateDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (Location) REFERENCES Location(Id) 
);

CREATE TABLE Jobs(
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255),
    JobPosterId INT,
    Disabled BOOLEAN,
    Description TEXT,
    ExpiryDays INT,
    Expired BOOLEAN,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdateDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (JobPosterId) REFERENCES JobPoster(Id) 
);

CREATE TABLE JobAssignments (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    JobId INT,
    LabourId INT,
    Status VARCHAR(50),
    CostNegotiated DOUBLE,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdateDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (JobId) REFERENCES Jobs(Id),     
    FOREIGN KEY (LabourId) REFERENCES Labour(Id) 
);

CREATE TABLE `session` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `DeviceId` varchar(255) DEFAULT NULL,
  `DeviceType` varchar(255) DEFAULT NULL,
  `expired` tinyint(1) DEFAULT '0',
  `LastActiveTime` datetime DEFAULT NULL,
  `LoginTime` datetime DEFAULT NULL,
  `timeInMilliSeconds` int DEFAULT '0',
  `UserId` int DEFAULT NULL,
  `usertype` varchar(255) DEFAULT NULL,
  `Brand` varchar(255) DEFAULT NULL,
  `Model` varchar(255) DEFAULT NULL,
  `OsVersion` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `VersionName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ;

 CREATE TABLE `messagesintable` (
  `MsgId` int NOT NULL AUTO_INCREMENT,
  `AccountId` varchar(255) DEFAULT NULL,
  `Address` longtext,
  `Attachment` blob,
  `Cost` double DEFAULT NULL,
  `CustomData` varchar(1024) DEFAULT NULL,
  `Date` datetime DEFAULT NULL,
  `event_name` varchar(255) DEFAULT NULL,
  `Message` varchar(8192) DEFAULT NULL,
  `MsgType` int DEFAULT NULL,
  `Provider` varchar(255) DEFAULT NULL,
  `SubAccountId` varchar(255) DEFAULT NULL,
  `Time` time(6) DEFAULT NULL,
  `vendor_id` int DEFAULT NULL,
  `Version` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MsgId`)
);


