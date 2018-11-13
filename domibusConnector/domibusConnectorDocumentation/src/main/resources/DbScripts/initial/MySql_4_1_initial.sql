--  *********************************************************************
--  Create Database Script
--  *********************************************************************


CREATE TABLE DOMIBUS_CONNECTOR_SEQ_STORE (SEQ_NAME VARCHAR(255) NOT NULL, SEQ_VALUE numeric(10, 0) NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_SEQ_STORE ADD PRIMARY KEY (SEQ_NAME);

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_MESSAGES.ID', 0);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_EVIDENCES.ID', 0);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_MESSAGE_INFO.ID', 0);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_MSG_ERROR.ID', 0);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_USER.ID', 3);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE VALUES ('DOMIBUS_CONNECTOR_USER_PWD.ID', 3);

CREATE TABLE DOMIBUS_CONNECTOR_SERVICE (SERVICE VARCHAR(255) NOT NULL, SERVICE_TYPE VARCHAR(512) NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD PRIMARY KEY (SERVICE);

CREATE TABLE DOMIBUS_CONNECTOR_PARTY (PARTY_ID VARCHAR(255) NOT NULL, ROLE VARCHAR(255) NOT NULL, PARTY_ID_TYPE VARCHAR(512) NULL);
ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD PRIMARY KEY (PARTY_ID, ROLE);

CREATE TABLE DOMIBUS_CONNECTOR_MSG_ERROR (ID DECIMAL(10, 0) NOT NULL, MESSAGE_ID DECIMAL(10, 0) NOT NULL, ERROR_MESSAGE VARCHAR(512) NOT NULL, DETAILED_TEXT LONGTEXT NULL, ERROR_SOURCE LONGTEXT NULL, CREATED datetime NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR ADD PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_MSG_CONT (ID DECIMAL(10, 0) NOT NULL, MESSAGE_ID DECIMAL(10, 0) NOT NULL, CONTENT_TYPE VARCHAR(255) NULL, CONTENT LONGBLOB NULL, CHECKSUM LONGTEXT NULL, CREATED datetime NULL);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT ADD PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_BIGDATA (ID VARCHAR(255) NOT NULL, CHECKSUM LONGTEXT NULL, CREATED datetime NULL, MESSAGE_ID DECIMAL(10, 0) NULL, LAST_ACCESS datetime NULL, NAME LONGTEXT NULL, CONTENT LONGBLOB NULL, MIMETYPE VARCHAR(255) NULL);
ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA ADD PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO (ID DECIMAL(10, 0) NOT NULL, MESSAGE_ID DECIMAL(10, 0) NOT NULL, FROM_PARTY_ID VARCHAR(255) NULL, FROM_PARTY_ROLE VARCHAR(255) NULL, TO_PARTY_ID VARCHAR(255) NULL, TO_PARTY_ROLE VARCHAR(255) NULL, ORIGINAL_SENDER VARCHAR(255) NULL, FINAL_RECIPIENT VARCHAR(255) NULL, SERVICE VARCHAR(255) NULL, ACTION VARCHAR(255) NULL, CREATED datetime NOT NULL, UPDATED datetime NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD PRIMARY KEY (ID);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT MSG_INFO_UNIQ_MSG_ID UNIQUE (MESSAGE_ID);

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE (ID DECIMAL(10, 0) NOT NULL, EBMS_MESSAGE_ID VARCHAR(255) NULL, BACKEND_MESSAGE_ID VARCHAR(255) NULL, BACKEND_NAME VARCHAR(255) NULL, CONNECTOR_MESSAGE_ID VARCHAR(255) NULL, CONVERSATION_ID VARCHAR(255) NULL, DIRECTION VARCHAR(10) NULL, HASH_VALUE LONGTEXT NULL, CONFIRMED datetime NULL, REJECTED datetime NULL, DELIVERED_BACKEND datetime NULL, DELIVERED_GW datetime NULL, UPDATED datetime NULL, CREATED datetime NULL);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UNIQUE_CONNECTOR_MESSAGE_ID UNIQUE (CONNECTOR_MESSAGE_ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UQ_DOMIBUS_CONNE_EBMS_MESSAGE UNIQUE (EBMS_MESSAGE_ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UQ_DOMIBUS_CONNE_NAT_MESSAGE_ UNIQUE (BACKEND_MESSAGE_ID);

CREATE TABLE DOMIBUS_CONNECTOR_EVIDENCE (ID DECIMAL(10, 0) NOT NULL, MESSAGE_ID DECIMAL(10, 0) NOT NULL, CONNECTOR_MESSAGE_ID VARCHAR(255), TYPE VARCHAR(255) NULL, EVIDENCE LONGTEXT NULL, DELIVERED_NAT datetime NULL, DELIVERED_GW datetime NULL, UPDATED datetime NULL);
ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE ADD PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_CONT_TYPE (MESSAGE_CONTENT_TYPE VARCHAR(255) NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_CONT_TYPE ADD PRIMARY KEY (MESSAGE_CONTENT_TYPE);

CREATE TABLE DOMIBUS_CONNECTOR_ACTION (ACTION VARCHAR(255) NOT NULL, PDF_REQUIRED BIT(1) DEFAULT 0 NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_ACTION ADD PRIMARY KEY (ACTION);

CREATE TABLE DOMIBUS_CONNECTOR_BACKEND_INFO (ID DECIMAL(10, 0) NOT NULL, BACKEND_NAME VARCHAR(255) NOT NULL, BACKEND_KEY_ALIAS VARCHAR(255) NOT NULL, BACKEND_KEY_PASS VARCHAR(255) NULL, BACKEND_SERVICE_TYPE VARCHAR(512) NULL, BACKEND_ENABLED BIT(1) DEFAULT 1 NULL, BACKEND_DEFAULT BIT(1) DEFAULT 0 NULL, BACKEND_DESCRIPTION LONGTEXT NULL, BACKEND_PUSH_ADDRESS LONGTEXT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO ADD PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO ADD CONSTRAINT UN_DOMIBUS_CONNECTOR_BACK_01 UNIQUE (BACKEND_NAME);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO ADD CONSTRAINT UN_DOMIBUS_CONNECTOR_BACK_02 UNIQUE (BACKEND_KEY_ALIAS);

CREATE TABLE DOMIBUS_CONNECTOR_BACK_2_S (DOMIBUS_CONNECTOR_SERVICE_ID VARCHAR(255) NOT NULL, DOMIBUS_CONNECTOR_BACKEND_ID DECIMAL(10, 0) NOT NULL);
ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S ADD PRIMARY KEY (DOMIBUS_CONNECTOR_SERVICE_ID, DOMIBUS_CONNECTOR_BACKEND_ID);

ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MSG_ERROR FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_04 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGI_ACTION FOREIGN KEY (ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION (ACTION) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGI_SERVICE FOREIGN KEY (SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE (service) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGI_FROM_PARTY FOREIGN KEY (FROM_PARTY_ID, FROM_PARTY_ROLE) REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGI_TO_PARTY FOREIGN KEY (TO_PARTY_ID, TO_PARTY_ROLE) REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGI_MSGID FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_EVIDENCES FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_01 FOREIGN KEY (DOMIBUS_CONNECTOR_BACKEND_ID) REFERENCES DOMIBUS_CONNECTOR_BACKEND_INFO (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S ADD CONSTRAINT FK_DOMIBUS_CONN_DOMIBUS_CON_02 FOREIGN KEY (DOMIBUS_CONNECTOR_SERVICE_ID) REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_BIGDATA FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE `DOMIBUS_CONNECTOR_PROPERTY`
(
	`PROPERTY_NAME` VARCHAR(255) NOT NULL,
	`PROPERTY_VALUE` TEXT NULL,
	CONSTRAINT `PK_DOMIBUS_CONNECTOR_PROPERTY` PRIMARY KEY (`PROPERTY_NAME` ASC)
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
ALTER TABLE `DOMIBUS_CONNECTOR_USER_PWD` 
 ADD INDEX `IXFK_DOMIBUS_CONNECTOR_USER_PWD_DOMIBUS_CONNECTOR_USER` (`USER_ID` ASC);
ALTER TABLE `DOMIBUS_CONNECTOR_USER_PWD` 
 ADD CONSTRAINT `FK_DOMIBUS_CONNECTOR_USER_PWD_DOMIBUS_CONNECTOR_USER`
	FOREIGN KEY (`USER_ID`) REFERENCES `DOMIBUS_CONNECTOR_USER` (`ID`) ON DELETE Restrict ON UPDATE Restrict
;

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED, CREATED) VALUES (1, 'admin', 'ADMIN', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED) VALUES (1, 1, '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306', '5b424031616564356639', 1, 1, current_timestamp);

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED, CREATED) VALUES (2, 'user', 'USER', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED) VALUES (2, 2, '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306', '5b424031616564356639', 1, 1, current_timestamp);




