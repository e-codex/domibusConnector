-- *********************************************************************
-- Update Database Script - FROM domibusConnector 3.5 to 4.0
-- *********************************************************************
-- updates the connector database FROM an 3.5 connector version to 4.0
-- remove all Foreign Keys first before executing this script, otherwise there
-- will be some errors at the end of the script, when the foreign keys are
-- recreated with specific names
--
SET FOREIGN_KEY_CHECKS = 0;

-- 1/5 RENAME tables that need to be recreated
RENAME TABLE
    DOMIBUS_CONNECTOR_SEQ_STORE TO BKP_DC_SEQ_STORE;
RENAME TABLE
    DOMIBUS_CONNECTOR_MSG_ERROR TO BKP_DC_MSG_ERROR;
RENAME TABLE
    DOMIBUS_CONNECTOR_MESSAGE_INFO TO BKP_DC_MSG_INFO;
RENAME TABLE
    DOMIBUS_CONNECTOR_EVIDENCES TO BKP_DC_EVIDENCES;

-- 2/5 CREATE tables, structural changes
CREATE TABLE DOMIBUS_CONNECTOR_EVIDENCE
(
    ID            BIGINT not null primary key,
    MESSAGE_ID    BIGINT not null,
    TYPE          VARCHAR(255),
    EVIDENCE      LONGTEXT,
    DELIVERED_NAT DATETIME,
    DELIVERED_GW  DATETIME,
    UPDATED       DATETIME
);


CREATE TABLE DOMIBUS_CONNECTOR_SEQ_STORE
(
    SEQ_NAME  VARCHAR(255) NOT NULL,
    SEQ_VALUE BIGINT       NOT NULL
);


CREATE TABLE DOMIBUS_CONNECTOR_MSG_ERROR
(
    ID            BIGINT       not null,
    MESSAGE_ID    BIGINT       not null,
    ERROR_MESSAGE VARCHAR(512) not null,
    CREATED       DATETIME     not null,
    ERROR_SOURCE  LONGTEXT,
    DETAILED_TEXT LONGTEXT
);


CREATE TABLE DOMIBUS_CONNECTOR_MSG_CONT
(
    ID           BIGINT NOT NULL,
    MESSAGE_ID   BIGINT NOT NULL,
    CONTENT_TYPE VARCHAR(255),
    CONTENT      LONGBLOB,
    CHECKSUM     LONGTEXT,
    CREATED      DATETIME
);


CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
(
    ID              BIGINT   not null primary key,
    MESSAGE_ID      BIGINT   not null unique,
    FROM_PARTY_ID   VARCHAR(255),
    FROM_PARTY_ROLE VARCHAR(255),
    TO_PARTY_ID     VARCHAR(255),
    TO_PARTY_ROLE   VARCHAR(255),
    ORIGINAL_SENDER VARCHAR(255),
    FINAL_RECIPIENT VARCHAR(255),
    SERVICE         VARCHAR(255),
    ACTION          VARCHAR(255),
    CREATED         DATETIME not null,
    UPDATED         DATETIME not null
);


CREATE TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
(
    ID                   BIGINT             NOT NULL,
    BACKEND_NAME         VARCHAR(255)       NOT NULL,
    BACKEND_KEY_ALIAS    VARCHAR(255)       NOT NULL,
    BACKEND_KEY_PASS     VARCHAR(255),
    BACKEND_SERVICE_TYPE VARCHAR(255),
    BACKEND_ENABLED      BOOLEAN DEFAULT TRUE NULL,
    BACKEND_DEFAULT      BOOLEAN DEFAULT FALSE NULL,
    BACKEND_DESCRIPTION  LONGTEXT,
    BACKEND_PUSH_ADDRESS VARCHAR(512)
);


CREATE TABLE DOMIBUS_CONNECTOR_BACK_2_S
(
    DOMIBUS_CONNECTOR_SERVICE_ID VARCHAR(255) NOT NULL,
    DOMIBUS_CONNECTOR_BACKEND_ID BIGINT       NOT NULL
);


CREATE TABLE DOMIBUS_CONNECTOR_BIGDATA
(
    ID          VARCHAR(255) NOT NULL PRIMARY KEY,
    CHECKSUM    LONGTEXT,
    CREATED     DATETIME,
    MESSAGE_ID  BIGINT,
    LAST_ACCESS DATETIME,
    NAME        LONGTEXT,
    CONTENT     LONGBLOB,
    MIMETYPE    VARCHAR(255)
);


ALTER TABLE DOMIBUS_CONNECTOR_SERVICE
    MODIFY SERVICE VARCHAR(255);

ALTER TABLE DOMIBUS_CONNECTOR_SERVICE
    MODIFY SERVICE_TYPE VARCHAR(512);

ALTER TABLE DOMIBUS_CONNECTOR_PARTY
    MODIFY PARTY_ID VARCHAR(255);

ALTER TABLE DOMIBUS_CONNECTOR_PARTY
    MODIFY ROLE VARCHAR(255);

ALTER TABLE DOMIBUS_CONNECTOR_PARTY
    MODIFY PARTY_ID_TYPE VARCHAR(512);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGES RENAME TO DOMIBUS_CONNECTOR_MESSAGE;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    MODIFY ID BIGINT;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE RENAME COLUMN NAT_MESSAGE_ID TO BACKEND_MESSAGE_ID;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    MODIFY CONFIRMED DATETIME;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    MODIFY REJECTED DATETIME;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    MODIFY DELIVERED_GW DATETIME;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    MODIFY UPDATED DATETIME;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE RENAME COLUMN DELIVERED_NAT TO DELIVERED_BACKEND;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD BACKEND_NAME VARCHAR(255);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONNECTOR_MESSAGE_ID VARCHAR(255);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CREATED DATETIME;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD HASH_VALUE_TEMP LONGTEXT;
UPDATE DOMIBUS_CONNECTOR_MESSAGE
SET HASH_VALUE_TEMP=HASH_VALUE;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    DROP COLUMN HASH_VALUE;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE RENAME COLUMN HASH_VALUE_TEMP TO HASH_VALUE;

ALTER TABLE DOMIBUS_CONNECTOR_ACTION
    MODIFY ACTION VARCHAR(255);

DROP TABLE IF EXISTS DCON_QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS DCON_QRTZ_CALENDARS;
DROP TABLE IF EXISTS DCON_QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS DCON_QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS DCON_QRTZ_LOCKS;
DROP TABLE IF EXISTS DCON_QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS DCON_QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS DCON_QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS DCON_QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS DCON_QRTZ_TRIGGERS;
DROP TABLE IF EXISTS DCON_QRTZ_JOB_DETAILS;


-- 3/5 TRANSFER data

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_MESSAGE.ID',
        (SELECT SEQ_VALUE FROM BKP_DC_SEQ_STORE WHERE SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGES.ID'));

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_EVIDENCE.ID',
        (SELECT SEQ_VALUE FROM BKP_DC_SEQ_STORE WHERE SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID'));

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
SELECT *
FROM BKP_DC_SEQ_STORE
WHERE SEQ_NAME in ('DOMIBUS_CONNECTOR_MESSAGE_INFO.ID', 'DOMIBUS_CONNECTOR_MSG_ERROR.ID');

insert into DOMIBUS_CONNECTOR_SERVICE
values ('n.a.', 'n.a.');

insert into DOMIBUS_CONNECTOR_PARTY
values ('n.a.', 'n.a.', 'n.a.');

INSERT INTO DOMIBUS_CONNECTOR_MSG_ERROR
SELECT ID, MESSAGE_ID, ERROR_MESSAGE, CREATED, ERROR_SOURCE, DETAILED_TEXT
FROM BKP_DC_MSG_ERROR;

insert into DOMIBUS_CONNECTOR_MESSAGE_INFO
select B.ID,
       B.MESSAGE_ID,
       CASE
           when FROM_PARTY_ID is not null and FROM_PARTY_ROLE is not null
               then (select PARTY_ID
                     from DOMIBUS_CONNECTOR_PARTY FP
                     where FP.PARTY_ID = FROM_PARTY_ID
                       and FP.ROLE = FROM_PARTY_ROLE)
           else 'n.a.'
           end
           as FROM_PARTY_ID,
       CASE
           when FROM_PARTY_ID is not null and FROM_PARTY_ROLE is not null
               then (select ROLE
                     from DOMIBUS_CONNECTOR_PARTY FP
                     where FP.PARTY_ID = FROM_PARTY_ID
                       and FP.ROLE = FROM_PARTY_ROLE)
           else 'n.a.'
           end
           as FROM_PARTY_ROLE,
       CASE
           when TO_PARTY_ID is not null and TO_PARTY_ROLE is not null
               then (select PARTY_ID
                     from DOMIBUS_CONNECTOR_PARTY FP
                     where FP.PARTY_ID = TO_PARTY_ID
                       and FP.ROLE = TO_PARTY_ROLE)
           else 'n.a.'
           end
           as TO_PARTY_ID,
       CASE
           when TO_PARTY_ID is not null and TO_PARTY_ROLE is not null
               then (select ROLE
                     from DOMIBUS_CONNECTOR_PARTY FP
                     where FP.PARTY_ID = TO_PARTY_ID
                       and FP.ROLE = TO_PARTY_ROLE)
           else 'n.a.'
           end
           as TO_PARTY_ROLE,
       ORIGINAL_SENDER,
       FINAL_RECIPIENT,
       CASE
           when B.SERVICE is not null
               then (select SERVICE from DOMIBUS_CONNECTOR_SERVICE S where S.SERVICE = B.SERVICE)
           else 'n.a.'
           end
           as FK_SERVICE,
       CASE
           when B.ACTION is not null
               then (select ACTION from DOMIBUS_CONNECTOR_ACTION A where A.ACTION = B.ACTION)
           else 'n.a.'
           end
           as FK_ACTION,
       CREATED,
       UPDATED
from BKP_DC_MSG_INFO B;

UPDATE DOMIBUS_CONNECTOR_MESSAGE
# SET CONNECTOR_MESSAGE_ID='_migrate_' || SYS_GUID()
SET CONNECTOR_MESSAGE_ID=CONCAT('_migrate_', UUID())
where CONNECTOR_MESSAGE_ID is null;

INSERT INTO DOMIBUS_CONNECTOR_EVIDENCE
SELECT *
FROM BKP_DC_EVIDENCES;

insert into DOMIBUS_CONNECTOR_ACTION
values ('n.a.', 0);

-- 4/5 DELETE temporary tables
DROP TABLE BKP_DC_SEQ_STORE;
DROP TABLE BKP_DC_MSG_ERROR;
DROP TABLE BKP_DC_MSG_INFO;
DROP TABLE BKP_DC_EVIDENCES;

-- 5/5 ADD the constraints
ALTER TABLE DOMIBUS_CONNECTOR_SEQ_STORE
    ADD CONSTRAINT PK_DC_SEQ_STORE PRIMARY KEY (SEQ_NAME);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR
    ADD CONSTRAINT PK_DC_MSG_ERROR PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR
    ADD CONSTRAINT FK_DC_MSG_ERROR_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT
    ADD CONSTRAINT PK_DC_MSG_CONT PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT
    ADD CONSTRAINT FK_DC_CON_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_01 foreign key (ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION (ACTION) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_02 foreign key (SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_03 foreign key (FROM_PARTY_ID, FROM_PARTY_ROLE) references DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_04 foreign key (TO_PARTY_ID, TO_PARTY_ROLE) references DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_I foreign key (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
    ADD CONSTRAINT PK_DC_BACK_01 PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
    ADD CONSTRAINT UN_DC_BACK_01 UNIQUE (BACKEND_NAME);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
    ADD CONSTRAINT UN_DC_BACK_02 UNIQUE (BACKEND_KEY_ALIAS);
ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S
    ADD CONSTRAINT FK_DC_BACK2S_01 FOREIGN KEY (DOMIBUS_CONNECTOR_BACKEND_ID) REFERENCES DOMIBUS_CONNECTOR_BACKEND_INFO (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S
    ADD CONSTRAINT FK_DC_BACK2S_02 FOREIGN KEY (DOMIBUS_CONNECTOR_SERVICE_ID) REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA
    ADD CONSTRAINT FK_DC_BIGDATA_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE
    ADD CONSTRAINT FK_DC_EVIDENCES_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

SET FOREIGN_KEY_CHECKS = 1;