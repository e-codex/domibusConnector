SET FOREIGN_KEY_CHECKS=0 
;

/* Drop Tables */

DROP TABLE IF EXISTS `DOMIBUS_WEBADMIN_PROPERTY` CASCADE
;

DROP TABLE IF EXISTS `DOMIBUS_WEBADMIN_USER` CASCADE
;

/* Create Tables */

CREATE TABLE `DOMIBUS_CONNECTOR_PROPERTY`
(
	`PROPERTY_NAME` VARCHAR(255) NOT NULL,
	`PROPERTY_VALUE` TEXT NULL,
	CONSTRAINT `PK_DOMIBUS_CONNECTOR_PROPERTIES` PRIMARY KEY (`PROPERTY_NAME` ASC)
)

;

CREATE TABLE `DOMIBUS_CONNECTOR_USER`
(
	`ID` DECIMAL(10,0) NOT NULL,
	`USERNAME` VARCHAR(50) NOT NULL,
	`ROLE` VARCHAR(50) NOT NULL,
	`LOCKED` BIT NOT NULL DEFAULT 0,
	`NUMBER_OF_GRACE_LOGINS` BIGINT NOT NULL DEFAULT 5,
	`GRACE_LOGINS_USED` BIGINT NOT NULL DEFAULT 0,
	`CREATED` DATETIME NULL,
	CONSTRAINT `PK_DOMIBUS_CONNECTOR_USER` PRIMARY KEY (`ID` ASC)
)

;

CREATE TABLE `DOMIBUS_CONNECTOR_USER_PWD`
(
	`ID` DECIMAL(10,0) NOT NULL,
	`USER_ID` DECIMAL(10,0) NOT NULL,
	`PASSWORD` TEXT NOT NULL,
	`SALT` TEXT NOT NULL,
	`CURRENT_PWD` BIT NOT NULL DEFAULT 0,
	`INITIAL_PWD` BIT NOT NULL DEFAULT 0,
	`CREATED` DATETIME NULL,
	CONSTRAINT `PK_DOMIBUS_CONNECTOR_USER_PWD` PRIMARY KEY (`ID` ASC)
)
;

ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE
 ADD COLUMN CONNECTOR_MESSAGE_ID VARCHAR(255)
 ;

CREATE INDEX IXFK_DOMIBUS_CONN_EV01
 ON  DOMIBUS_CONNECTOR_EVIDENCE (CONNECTOR_MESSAGE_ID)
;

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE `DOMIBUS_CONNECTOR_USER_PWD` 
 ADD INDEX `IXFK_DOMIBUS_CONNECTOR_USER_PWD_DOMIBUS_CONNECTOR_USER` (`USER_ID` ASC)
;

/* Create Foreign Key Constraints */

ALTER TABLE `DOMIBUS_CONNECTOR_USER_PWD` 
 ADD CONSTRAINT `FK_DOMIBUS_CONNECTOR_USER_PWD_DOMIBUS_CONNECTOR_USER`
	FOREIGN KEY (`USER_ID`) REFERENCES `DOMIBUS_CONNECTOR_USER` (`ID`) ON DELETE Restrict ON UPDATE Restrict
;

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED, CREATED) VALUES (1, 'admin', 'ADMIN', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED) VALUES (1, 1, '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306', '5b424031616564356639', 1, 1, current_timestamp);

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED, CREATED) VALUES (2, 'user', 'USER', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED) VALUES (2, 2, '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306', '5b424031616564356639', 1, 1, current_timestamp);

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_USER.ID', 3);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_USER_PWD.ID', 3);

SET FOREIGN_KEY_CHECKS=1 
;