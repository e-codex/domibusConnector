SET FOREIGN_KEY_CHECKS=0;



--DROP INDEX UQ_DOMIBUS_CONNE_EBMS_MESSAGE ON DOMIBUS_CONNECTOR_MESSAGE;
--DROP INDEX UQ_DOMIBUS_CONNE_NAT_MESSAGE_ ON DOMIBUS_CONNECTOR_MESSAGE;


ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD COLUMN DIRECTION_SOURCE VARCHAR(20);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD COLUMN DIRECTION_TARGET VARCHAR(20);

UPDATE DOMIBUS_CONNECTOR_MESSAGE SET DIRECTION_TARGET='GATEWAY' WHERE DIRECTION = 'NAT_TO_GW' OR DIRECTION='CON_TO_GW';
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET DIRECTION_TARGET='BACKEND' WHERE DIRECTION = 'GW_TO_NAT' OR DIRECTION='CON_TO_NAT';
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET DIRECTION_SOURCE='GATEWAY' WHERE DIRECTION = 'GW_TO_NAT';
UPDATE DOMIBUS_CONNECTOR_MESSAGE SET DIRECTION_SOURCE='BACKEND' WHERE DIRECTION = 'NAT_TO_GW';

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE DROP COLUMN DIRECTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UQ_DC_MSG_EBMSID_DIRECTION_TARGET UNIQUE (EBMS_MESSAGE_ID,DIRECTION_TARGET);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD CONSTRAINT UQ_DC_MSG_NATID_DIRECTION_TARGET UNIQUE (BACKEND_MESSAGE_ID,DIRECTION_TARGET);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE ADD COLUMN GATEWAY_NAME VARCHAR(255);

create table DC_LINK_CONFIGURATION (
	ID DECIMAL(10, 0) not null,
	CONFIG_NAME VARCHAR(255) not null,
	LINK_IMPL VARCHAR(255),
	constraint PK_DC_LINK_CONFIGURATION primary key (ID),
	constraint UNQ_DC_LINK_CONFIG_NMAE unique (CONFIG_NAME)
);

create table DC_LINK_CONFIG_PROPERTY (
	DC_LINK_CONFIGURATION_ID DECIMAL(10, 0) not null,
	PROPERTY_NAME VARCHAR(255) not null,
	PROPERTY_VALUE LONGTEXT,
	constraint PK_DC_LINK_CONFIG_PROPERTY primary key (DC_LINK_CONFIGURATION_ID, PROPERTY_NAME),
	constraint FK_LINKPROPERTY_LINKCONFIG foreign key (DC_LINK_CONFIGURATION_ID) references DC_LINK_CONFIGURATION (ID)
);

 create table DC_MESSAGE_LANE
(
	ID DECIMAL(10, 0) not null,
	NAME VARCHAR(255) not null ,
	DESCRIPTION LONGTEXT,
	constraint PK_DC_MESSAGE_LANE_ID primary key (ID),
	constraint UNQ_DC_MESSAGE_LANE UNIQUE (NAME)
);

create table DC_MESSAGE_LANE_PROPERTY
(
	DC_MESSAGE_LANE_ID DECIMAL(10, 0) not null,
	PROPERTY_NAME VARCHAR(255) not null,
	PROPERTY_VALUE LONGTEXT,
	constraint PK_DC_MESSAGE_LANE_PROPERTY primary key (DC_MESSAGE_LANE_ID, PROPERTY_NAME),
	constraint FK_MSGLANEPROPERTY_MSGLANE foreign key (DC_MESSAGE_LANE_ID) references DC_MESSAGE_LANE (ID)
);
INSERT INTO DC_MESSAGE_LANE (ID, NAME, DESCRIPTION) VALUES (1,'default_message_lane','default message lane');


create table DC_LINK_PARTNER
(
	ID DECIMAL(10, 0) not null,
	NAME VARCHAR(255) not null,
	DESCRIPTION LONGTEXT,
	ENABLED BOOLEAN,
	LINK_CONFIG_ID DECIMAL(10, 0),
	LINK_TYPE VARCHAR(20),
	LINK_MODE VARCHAR(20),
	constraint UNQ_LINK_INFO_NAME unique(NAME),
	constraint PK_DC_LINK_PARTNER primary key (ID),
	constraint FK_LINKINFO_LINKCONFIG foreign key (LINK_CONFIG_ID) references DC_LINK_CONFIGURATION (ID)
);

create table DC_LINK_PARTNER_PROPERTY
(
	DC_LINK_PARTNER_ID DECIMAL(10, 0) not null,
	PROPERTY_NAME VARCHAR(255) not null,
	PROPERTY_VALUE LONGTEXT,
	constraint PK_DC_LINK_PARTNER_PROPERTY primary key (DC_LINK_PARTNER_ID, PROPERTY_NAME),
	constraint FK_LINKPROPERTY_LINKPARTNER foreign key (DC_LINK_PARTNER_ID) references DC_LINK_PARTNER (ID)
);


DROP TABLE IF EXISTS DC_TRANSPORT_STEP;
create table DC_TRANSPORT_STEP
(
	ID DECIMAL(10, 0) not null,
	CONNECTOR_MESSAGE_ID VARCHAR(255) not null,
	LINK_PARTNER_NAME VARCHAR(255) not null,
	ATTEMPT INT not null,
	TRANSPORT_ID VARCHAR(255),
	TRANSPORT_SYSTEM_MESSAGE_ID VARCHAR(255),
	REMOTE_MESSAGE_ID VARCHAR(255),
	CREATED TIMESTAMP,
	constraint PK_DC_TRANSPORT_STEP primary key (ID)
);

DROP TABLE IF EXISTS DC_TRANSPORT_STEP_STATUS;
create table DC_TRANSPORT_STEP_STATUS
(
	TRANSPORT_STEP_ID DECIMAL(10, 0) not null,
	STATE VARCHAR(40) not null,
	CREATED TIMESTAMP,
	TEXT LONGTEXT,
	constraint PK_DC_TRANSPORT_STEP_STATUS
		primary key (TRANSPORT_STEP_ID, STATE),
	constraint FK_TRANSPORTSTEPSTATUS_TRANSPORTSTEP
		foreign key (TRANSPORT_STEP_ID) references DC_TRANSPORT_STEP (ID)
);


create table DC_MSGCNT_DETSIG
(
    ID DECIMAL(10,0) not null,
    CONTENT DECIMAL(10,0) not null,
    SIGNATURE TEXT,
    SIGNATURE_NAME varchar(255),
    SIGNATURE_TYPE varchar(255),
        constraint PK_DETACHED_SIGNATURE
        primary key (ID)
);

alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN STORAGE_PROVIDER_NAME VARCHAR(255);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN STORAGE_REFERENCE_ID VARCHAR(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN DIGEST VARCHAR(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN PAYLOAD_NAME VARCHAR(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN PAYLOAD_IDENTIFIER VARCHAR(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN PAYLOAD_DESCRIPTION TEXT;
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN PAYLOAD_MIMETYPE VARCHAR(255);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN PAYLOAD_SIZE DECIMAL(10,0);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN DETACHED_SIGNATURE_ID DECIMAL(10,0);
alter table DOMIBUS_CONNECTOR_MSG_CONT ADD COLUMN DELETED TIMESTAMP;




CREATE TABLE QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

--
-- Migrate p mode set tables
--
CREATE TABLE DC_PMODE_SET (
    ID DECIMAL(10,0),
    FK_MESSAGE_LANE DECIMAL (10,0),
    CREATED TIMESTAMP,
    DESCRIPTION TEXT,
    ACTIVE DECIMAL(1, 0) DEFAULT 0 NOT NULL,
    CONSTRAINT PK_DC_PMODE_SET PRIMARY KEY (ID)
);

INSERT INTO DC_PMODE_SET (ID, FK_MESSAGE_LANE, CREATED, DESCRIPTION) VALUES (1, 1, NOW(), 'initial set created by migration script');

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO DROP FOREIGN KEY FK_MSGI_FROM_PARTY;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO DROP FOREIGN KEY FK_MSGI_TO_PARTY;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO DROP FOREIGN KEY FK_MSGI_SERVICE;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO DROP FOREIGN KEY FK_MSGI_ACTION;

ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S DROP FOREIGN KEY FK_DOMIBUS_CONN_DOMIBUS_CON_02;

-- create technical key

ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD COLUMN IDENTIFIER VARCHAR(255);
ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD COLUMN FK_PMODE_SET DECIMAL(10,0);
ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD COLUMN ID INT NOT NULL UNIQUE AUTO_INCREMENT;
ALTER TABLE DOMIBUS_CONNECTOR_PARTY MODIFY COLUMN ID DECIMAL(10,0) NOT NULL;
ALTER TABLE DOMIBUS_CONNECTOR_PARTY DROP PRIMARY KEY;
ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_PARTY PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_PARTY ADD CONSTRAINT FK_PARTY_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET(ID);

ALTER TABLE DOMIBUS_CONNECTOR_ACTION ADD COLUMN FK_PMODE_SET DECIMAL(10,0);
ALTER TABLE DOMIBUS_CONNECTOR_ACTION ADD COLUMN ID INT NOT NULL UNIQUE AUTO_INCREMENT;
ALTER TABLE DOMIBUS_CONNECTOR_ACTION MODIFY COLUMN ID DECIMAL(10,0) NOT NULL;
ALTER TABLE DOMIBUS_CONNECTOR_ACTION DROP PRIMARY KEY;
ALTER TABLE DOMIBUS_CONNECTOR_ACTION ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_ACTION PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_ACTION ADD CONSTRAINT FK_ACTION_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET(ID);

ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD COLUMN FK_PMODE_SET DECIMAL(10,0);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD COLUMN ID INT NOT NULL UNIQUE AUTO_INCREMENT;
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE MODIFY COLUMN ID DECIMAL(10,0) NOT NULL;
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE DROP PRIMARY KEY;
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_SERVICE PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT FK_SERVICE_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET(ID);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD COLUMN FK_TO_PARTY_ID DECIMAL(10,0);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD COLUMN FK_FROM_PARTY_ID DECIMAL(10,0);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD COLUMN FK_ACTION DECIMAL(10,0);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD COLUMN FK_SERVICE DECIMAL(10,0);


UPDATE DOMIBUS_CONNECTOR_MESSAGE_INFO info
            SET info.FK_TO_PARTY_ID = (SELECT party.id FROM DOMIBUS_CONNECTOR_PARTY party WHERE info.TO_PARTY_ID = party.PARTY_ID AND info.TO_PARTY_ROLE = party.ROLE)
            WHERE exists (SELECT * FROM DOMIBUS_CONNECTOR_PARTY party WHERE info.TO_PARTY_ID = party.PARTY_ID AND info.TO_PARTY_ROLE = party.ROLE);

UPDATE DOMIBUS_CONNECTOR_MESSAGE_INFO info
            SET info.FK_FROM_PARTY_ID = (SELECT party.id FROM DOMIBUS_CONNECTOR_PARTY party WHERE info.FROM_PARTY_ID = party.PARTY_ID AND info.FROM_PARTY_ROLE = party.ROLE)
            WHERE exists (SELECT * FROM DOMIBUS_CONNECTOR_PARTY party WHERE info.FROM_PARTY_ID = party.PARTY_ID AND info.FROM_PARTY_ROLE = party.ROLE);

UPDATE DOMIBUS_CONNECTOR_MESSAGE_INFO info
            SET info.FK_ACTION = (SELECT action.id FROM DOMIBUS_CONNECTOR_ACTION action WHERE info.ACTION = action.ACTION )
            WHERE exists (SELECT * FROM DOMIBUS_CONNECTOR_ACTION action WHERE info.ACTION = action.ACTION );

UPDATE DOMIBUS_CONNECTOR_MESSAGE_INFO info
            SET info.FK_SERVICE = (SELECT service.id FROM DOMIBUS_CONNECTOR_SERVICE service WHERE info.SERVICE = service.SERVICE )
            WHERE exists (SELECT service.id FROM DOMIBUS_CONNECTOR_SERVICE service WHERE info.SERVICE = service.SERVICE );

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGINFO_FROM_PARTY FOREIGN KEY (FK_FROM_PARTY_ID) REFERENCES DOMIBUS_CONNECTOR_PARTY(ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGINFO_TO_PARTY FOREIGN KEY (FK_TO_PARTY_ID) REFERENCES DOMIBUS_CONNECTOR_PARTY(ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGINFO_SERVICE FOREIGN KEY (FK_SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE(ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO ADD CONSTRAINT FK_MSGINFO_ACTION FOREIGN KEY (FK_ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION(ID);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO MODIFY COLUMN ORIGINAL_SENDER VARCHAR(2048);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO MODIFY COLUMN FINAL_RECIPIENT VARCHAR(2048);

SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE IF NOT EXISTS DC_DB_VERSION (TAG VARCHAR(255) PRIMARY KEY);
INSERT INTO DC_DB_VERSION (TAG) VALUES ('V4.2');
