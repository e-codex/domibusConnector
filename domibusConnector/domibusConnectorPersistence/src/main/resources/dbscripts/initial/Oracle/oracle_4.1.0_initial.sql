-- INITIAL DATABASE SCRIPT FOR domibusConnector 4.1.x
--


CREATE TABLE DOMIBUS_CONNECTOR_SEQ_STORE
(
    SEQ_NAME  VARCHAR2(255 CHAR) NOT NULL,
    SEQ_VALUE NUMBER(10)         NOT NULL
);
ALTER TABLE DOMIBUS_CONNECTOR_SEQ_STORE
    ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_SEQ_STORE PRIMARY KEY (SEQ_NAME);

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_MESSAGES.ID', 1000);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_EVIDENCES.ID', 1000);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_MESSAGE_INFO.ID', 1000);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_MSG_ERROR.ID', 1000);
-- because of the default users (see below): ADMIN and USER the sequence starts with 1000 + 2
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_USER.ID', 1002);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_USER_PWD.ID', 1002);
INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_PROPERTY.ID', 1000);

CREATE TABLE DOMIBUS_CONNECTOR_SERVICE
(
    SERVICE      VARCHAR2(255 CHAR) NOT NULL,
    SERVICE_TYPE VARCHAR2(255 CHAR) NOT NULL
);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE
    ADD CONSTRAINT PK_DC_SERVICE PRIMARY KEY (SERVICE);

CREATE TABLE DOMIBUS_CONNECTOR_PARTY
(
    PARTY_ID      VARCHAR2(255 CHAR) NOT NULL,
    ROLE          VARCHAR2(255 CHAR) NOT NULL,
    PARTY_ID_TYPE VARCHAR2(512 CHAR)
);
ALTER TABLE DOMIBUS_CONNECTOR_PARTY
    ADD CONSTRAINT PK_DC_PARTY PRIMARY KEY (PARTY_ID, ROLE);

CREATE TABLE DOMIBUS_CONNECTOR_MSG_ERROR
(
    ID            DECIMAL(10, 0)      NOT NULL,
    MESSAGE_ID    DECIMAL(10, 0)      NOT NULL,
    ERROR_MESSAGE VARCHAR2(2048 CHAR) NOT NULL,
    DETAILED_TEXT CLOB,
    ERROR_SOURCE  CLOB,
    CREATED       TIMESTAMP           NOT NULL
);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR
    ADD CONSTRAINT PK_DC_MSG_ERROR PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_MSG_CONT
(
    ID           DECIMAL(10, 0) NOT NULL,
    MESSAGE_ID   DECIMAL(10, 0),
    CONTENT_TYPE VARCHAR2(255 CHAR),
    CONTENT      BLOB,
    CHECKSUM     CLOB,
    CREATED      TIMESTAMP
);
ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT
    ADD CONSTRAINT PK_DC_MSG_CONT PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_BIGDATA
(
    ID          VARCHAR2(255 CHAR) NOT NULL,
    CHECKSUM    CLOB,
    CREATED     TIMESTAMP,
    MESSAGE_ID  DECIMAL(10, 0),
    LAST_ACCESS TIMESTAMP,
    NAME        CLOB,
    CONTENT     BLOB,
    MIMETYPE    VARCHAR2(255 CHAR)
);
ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA
    ADD PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
(
    ID                   DECIMAL(10, 0) NOT NULL,
    MESSAGE_ID           DECIMAL(10, 0) NOT NULL,
    CONNECTOR_MESSAGE_ID VARCHAR2(255 CHAR),
    FROM_PARTY_ID        VARCHAR2(255 CHAR),
    FROM_PARTY_ROLE      VARCHAR2(255 CHAR),
    TO_PARTY_ID          VARCHAR2(255 CHAR),
    TO_PARTY_ROLE        VARCHAR2(255 CHAR),
    ORIGINAL_SENDER      VARCHAR2(255 CHAR),
    FINAL_RECIPIENT      VARCHAR2(255 CHAR),
    SERVICE              VARCHAR2(255 CHAR),
    ACTION               VARCHAR2(255 CHAR),
    CREATED              TIMESTAMP,
    UPDATED              TIMESTAMP
);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT PK_DC_MSG_INFO PRIMARY KEY (ID);

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE
(
    ID                   DECIMAL(10, 0) NOT NULL,
    EBMS_MESSAGE_ID      VARCHAR2(255 CHAR),
    BACKEND_MESSAGE_ID   VARCHAR2(255 CHAR),
    BACKEND_NAME         VARCHAR2(255 CHAR),
    CONNECTOR_MESSAGE_ID VARCHAR2(255 CHAR),
    CONVERSATION_ID      VARCHAR2(255 CHAR),
    DIRECTION            VARCHAR2(10),
    HASH_VALUE           CLOB,
    CONFIRMED            TIMESTAMP,
    REJECTED             TIMESTAMP,
    DELIVERED_BACKEND    TIMESTAMP,
    DELIVERED_GW         TIMESTAMP,
    UPDATED              TIMESTAMP,
    CREATED              TIMESTAMP
);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT PK_DC_MESSAGE PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT UN_DC_MSG_ID_01 UNIQUE (CONNECTOR_MESSAGE_ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT UN_DC_MSG_EBMS_MSG UNIQUE (EBMS_MESSAGE_ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT UN_DC_MSG_NAT_MSG_01 UNIQUE (BACKEND_MESSAGE_ID);

CREATE TABLE DOMIBUS_CONNECTOR_EVIDENCE
(
    ID                   DECIMAL(10, 0) NOT NULL,
    MESSAGE_ID           DECIMAL(10, 0) NOT NULL,
    CONNECTOR_MESSAGE_ID VARCHAR(255),
    TYPE                 VARCHAR2(255 CHAR),
    EVIDENCE             CLOB,
    DELIVERED_NAT        TIMESTAMP,
    DELIVERED_GW         TIMESTAMP,
    UPDATED              TIMESTAMP
);
ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE
    ADD CONSTRAINT PK_DC_EVIDENCE PRIMARY KEY (ID);
CREATE INDEX "IXFK_DC_EV_01" ON "DOMIBUS_CONNECTOR_EVIDENCE" ("CONNECTOR_MESSAGE_ID");

CREATE TABLE DOMIBUS_CONNECTOR_CONT_TYPE
(
    MESSAGE_CONTENT_TYPE VARCHAR2(255 CHAR) NOT NULL
);
ALTER TABLE DOMIBUS_CONNECTOR_CONT_TYPE
    ADD CONSTRAINT PK_DC_CONT_TYPE PRIMARY KEY (MESSAGE_CONTENT_TYPE);

CREATE TABLE DOMIBUS_CONNECTOR_ACTION
(
    ACTION       VARCHAR2(255 CHAR) NOT NULL,
    PDF_REQUIRED DECIMAL(1, 0)      NOT NULL
);
ALTER TABLE DOMIBUS_CONNECTOR_ACTION
    ADD CONSTRAINT PK_DC_ACTION PRIMARY KEY (ACTION);

CREATE TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
(
    ID                   DECIMAL(10, 0)     NOT NULL,
    BACKEND_NAME         VARCHAR2(255 CHAR) NOT NULL,
    BACKEND_KEY_ALIAS    VARCHAR2(255 CHAR) NOT NULL,
    BACKEND_KEY_PASS     VARCHAR2(255 CHAR),
    BACKEND_SERVICE_TYPE VARCHAR2(255 CHAR),
    BACKEND_ENABLED      NUMBER(1) DEFAULT 1,
    BACKEND_DEFAULT      NUMBER(1) DEFAULT 0,
    BACKEND_DESCRIPTION  CLOB,
    BACKEND_PUSH_ADDRESS CLOB
);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
    ADD CONSTRAINT PK_DC_BACK_INFO PRIMARY KEY (ID);

ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
    ADD CONSTRAINT UN_DC_BACK_01 UNIQUE (BACKEND_NAME);
ALTER TABLE DOMIBUS_CONNECTOR_BACKEND_INFO
    ADD CONSTRAINT UN_DC_BACK_02 UNIQUE (BACKEND_KEY_ALIAS);

CREATE TABLE DOMIBUS_CONNECTOR_BACK_2_S
(
    DOMIBUS_CONNECTOR_SERVICE_ID VARCHAR2(255 CHAR) NOT NULL,
    DOMIBUS_CONNECTOR_BACKEND_ID DECIMAL(10, 0)     NOT NULL
);
ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S
    ADD CONSTRAINT PK_DC_BACK2S PRIMARY KEY (DOMIBUS_CONNECTOR_SERVICE_ID, DOMIBUS_CONNECTOR_BACKEND_ID);

ALTER TABLE DOMIBUS_CONNECTOR_MSG_ERROR
    ADD CONSTRAINT FK_DC_MSG_ERROR_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

ALTER TABLE DOMIBUS_CONNECTOR_MSG_CONT
    ADD CONSTRAINT FK_DC_CON_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_01 FOREIGN KEY (ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION (ACTION);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_02 FOREIGN KEY (SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_03 FOREIGN KEY (FROM_PARTY_ID, FROM_PARTY_ROLE) REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_04 FOREIGN KEY (TO_PARTY_ID, TO_PARTY_ROLE) REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DC_MSG_INFO_I FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

ALTER TABLE DOMIBUS_CONNECTOR_EVIDENCE
    ADD CONSTRAINT FK_DC_EVIDENCES_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S
    ADD CONSTRAINT FK_DC_BACK2S_01 FOREIGN KEY (DOMIBUS_CONNECTOR_BACKEND_ID) REFERENCES DOMIBUS_CONNECTOR_BACKEND_INFO (ID);
ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S
    ADD CONSTRAINT FK_DC_BACK2S_02 FOREIGN KEY (DOMIBUS_CONNECTOR_SERVICE_ID) REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE);

ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA
    ADD CONSTRAINT FK_DC_BIGDATA_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

CREATE TABLE "DOMIBUS_CONNECTOR_PROPERTY"
(
    ID               DECIMAL(10, 0) NOT NULL,
    "PROPERTY_NAME"  VARCHAR2(512)  NOT NULL,
    "PROPERTY_VALUE" VARCHAR2(1024) NULL
)
;
ALTER TABLE "DOMIBUS_CONNECTOR_PROPERTY"
    ADD CONSTRAINT PK_DC_PROPERTY
        PRIMARY KEY ("PROPERTY_NAME")
            USING INDEX
;
/

-- USER:

CREATE TABLE "DOMIBUS_CONNECTOR_USER"
(
    "ID"                     NUMBER(10)          NOT NULL,
    "USERNAME"               VARCHAR2(50)        NOT NULL,
    "ROLE"                   VARCHAR2(50)        NOT NULL,
    "LOCKED"                 NUMBER(1) DEFAULT 0 NOT NULL,
    "NUMBER_OF_GRACE_LOGINS" NUMBER(2) DEFAULT 5 NOT NULL,
    "GRACE_LOGINS_USED"      NUMBER(2) DEFAULT 0 NOT NULL,
    "CREATED"                TIMESTAMP           NOT NULL
)
;

CREATE TABLE "DOMIBUS_CONNECTOR_USER_PWD"
(
    "ID"          NUMBER(10)          NOT NULL,
    "USER_ID"     NUMBER(10)          NOT NULL,
    "PASSWORD"    VARCHAR2(1024)      NOT NULL,
    "SALT"        VARCHAR2(512)       NOT NULL,
    "CURRENT_PWD" NUMBER(1) DEFAULT 0 NOT NULL,
    "INITIAL_PWD" NUMBER(1) DEFAULT 0 NOT NULL,
    "CREATED"     TIMESTAMP           NOT NULL
)
;

/* Create Primary Keys, Indexes, Uniques, Checks, Triggers */

ALTER TABLE DOMIBUS_CONNECTOR_USER
    ADD CONSTRAINT PK_DC_USER
        PRIMARY KEY (ID)
;

ALTER TABLE "DOMIBUS_CONNECTOR_USER_PWD"
    ADD CONSTRAINT PK_DC_USER_PWD
        PRIMARY KEY ("ID")
;

CREATE INDEX "IXFK_DC_USER_PWD_01"
    ON "DOMIBUS_CONNECTOR_USER_PWD" ("USER_ID")
;

/* Create Foreign Key Constraints */

ALTER TABLE "DOMIBUS_CONNECTOR_USER_PWD"
    ADD CONSTRAINT "FK_DC_USER_PWD_01"
        FOREIGN KEY ("USER_ID") REFERENCES "DOMIBUS_CONNECTOR_USER" ("ID")
;

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED,
                                    CREATED)
VALUES (1000, 'admin', 'ADMIN', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED)
VALUES (1000, 1000,
        '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306',
        '5b424031616564356639', 1, 1, current_timestamp);

INSERT INTO DOMIBUS_CONNECTOR_USER (ID, USERNAME, ROLE, LOCKED, NUMBER_OF_GRACE_LOGINS, GRACE_LOGINS_USED,
                                    CREATED)
VALUES (1001, 'user', 'USER', 0, 5, 0, current_timestamp);
INSERT INTO DOMIBUS_CONNECTOR_USER_PWD (ID, USER_ID, PASSWORD, SALT, CURRENT_PWD, INITIAL_PWD, CREATED)
VALUES (1001, 1001,
        '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306',
        '5b424031616564356639', 1, 1, current_timestamp);

-- the seq store is already updated, see top of this file

-- END USER

CREATE TABLE DC_DB_VERSION
(
    TAG VARCHAR(255) PRIMARY KEY
);
INSERT INTO DC_DB_VERSION (TAG)
VALUES ('V4.1.5');