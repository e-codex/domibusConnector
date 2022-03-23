CREATE TABLE  "DC_DB_VERSION"
(
	"TAG" VARCHAR2(255) NOT NULL
)
;

CREATE TABLE  "DC_KEYSTORE"
(
	"ID" NUMBER(10) NOT NULL,
	"UUID" VARCHAR2(255 CHAR) NOT NULL,
	"KEYSTORE" BLOB NOT NULL,
	"PASSWORD" VARCHAR2(1024 BYTE),
	"UPLOADED" TIMESTAMP NOT NULL,
	"DESCRIPTION" VARCHAR2(512 CHAR),
	"TYPE" VARCHAR2(50 CHAR)
)
;

CREATE TABLE  "DC_LINK_CONFIG_PROPERTY"
(
	"DC_LINK_CONFIGURATION_ID" NUMBER(10) NOT NULL,
	"PROPERTY_NAME" VARCHAR2(255) NOT NULL,
	"PROPERTY_VALUE" CLOB
)
;

CREATE TABLE  "DC_LINK_CONFIGURATION"
(
	"ID" NUMBER(10) NOT NULL,
	"CONFIG_NAME" VARCHAR2(255) NOT NULL,
	"LINK_IMPL" VARCHAR2(255)
)
;

CREATE TABLE  "DC_LINK_PARTNER"
(
	"ID" NUMBER(10) NOT NULL,
	"NAME" VARCHAR2(50) NOT NULL,
	"DESCRIPTION" CLOB,
	"ENABLED" NUMBER(1),
	"LINK_CONFIG_ID" NUMBER(10),
	"LINK_TYPE" VARCHAR2(20),
	"LINK_MODE" VARCHAR2(20)
)
;

CREATE TABLE  "DC_LINK_PARTNER_PROPERTY"
(
	"DC_LINK_PARTNER_ID" NUMBER(10) NOT NULL,
	"PROPERTY_NAME" VARCHAR2(255) NOT NULL,
	"PROPERTY_VALUE" CLOB
)
;

CREATE TABLE  "DC_MESSAGE_LANE"
(
	"ID" NUMBER(10) NOT NULL,
	"NAME" VARCHAR2(255) NOT NULL,
	"DESCRIPTION" CLOB
)
;

CREATE TABLE  "DC_MESSAGE_LANE_PROPERTY"
(
	"DC_MESSAGE_LANE_ID" NUMBER(10) NOT NULL,
	"PROPERTY_NAME" VARCHAR2(255) NOT NULL,
	"PROPERTY_VALUE" CLOB
)
;

CREATE TABLE  "DC_MSGCNT_DETSIG"
(
	"ID" NUMBER(10) NOT NULL,
	"SIGNATURE" CLOB,
	"SIGNATURE_NAME" VARCHAR2(255),
	"SIGNATURE_TYPE" VARCHAR2(255)
)
;

CREATE TABLE  "DC_PMODE_SET"
(
	"ID" NUMBER(10) NOT NULL,
	"FK_MESSAGE_LANE" NUMBER(10),
	"CREATED" TIMESTAMP NOT NULL,
	"DESCRIPTION" CLOB,
	"ACTIVE" NUMBER(1) NOT NULL,
	"PMODES" BLOB,
	"FK_CONNECTORSTORE" NUMBER(10)
)
;

CREATE TABLE  "DC_TRANSPORT_STEP"
(
	"ID" NUMBER(10) NOT NULL,
	"CONNECTOR_MESSAGE_ID" VARCHAR2(255) NOT NULL,
	"LINK_PARTNER_NAME" VARCHAR2(255) NOT NULL,
	"ATTEMPT" NUMBER(2) NOT NULL,
	"TRANSPORT_ID" VARCHAR2(255),
	"TRANSPORT_SYSTEM_MESSAGE_ID" VARCHAR2(255),
	"REMOTE_MESSAGE_ID" VARCHAR2(255),
	"CREATED" TIMESTAMP,
	"TRANSPORTED_MESSAGE" CLOB,
	"FINAL_STATE_REACHED" TIMESTAMP
)
;

CREATE TABLE  "DC_TRANSPORT_STEP_STATUS"
(
	"TRANSPORT_STEP_ID" NUMBER(10) NOT NULL,
	"STATE" VARCHAR2(40) NOT NULL,
	"CREATED" TIMESTAMP,
	"TEXT" CLOB
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_ACTION"
(
	"ID" NUMBER(10) NOT NULL,
	"FK_PMODE_SET" NUMBER(10) NOT NULL,
	"ACTION" VARCHAR2(255 CHAR) NOT NULL
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_BIGDATA"
(
	"ID" VARCHAR2(255 CHAR) NOT NULL,
	"CHECKSUM" CLOB,
	"CREATED" TIMESTAMP NOT NULL,
	"CONNECTOR_MESSAGE_ID" VARCHAR2(255 CHAR),
	"LAST_ACCESS" TIMESTAMP,
	"NAME" CLOB,
	"CONTENT" BLOB,
	"MIMETYPE" VARCHAR2(255 CHAR)
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_EVIDENCE"
(
	"ID" NUMBER(10) NOT NULL,
	"MESSAGE_ID" NUMBER(10) NOT NULL,
	"CONNECTOR_MESSAGE_ID" VARCHAR2(255),
	"TYPE" VARCHAR2(255 CHAR),
	"EVIDENCE" CLOB,
	"DELIVERED_NAT" TIMESTAMP,
	"DELIVERED_GW" TIMESTAMP,
	"UPDATED" TIMESTAMP NOT NULL
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_MESSAGE"
(
	"ID" NUMBER(10) NOT NULL,
	"EBMS_MESSAGE_ID" VARCHAR2(255 CHAR),
	"BACKEND_MESSAGE_ID" VARCHAR2(255 CHAR),
	"BACKEND_NAME" VARCHAR2(255 CHAR),
	"CONNECTOR_MESSAGE_ID" VARCHAR2(255 CHAR),
	"CONVERSATION_ID" VARCHAR2(255 CHAR),
	"HASH_VALUE" CLOB,
	"CONFIRMED" TIMESTAMP,
	"REJECTED" TIMESTAMP,
	"DELIVERED_BACKEND" TIMESTAMP,
	"DELIVERED_GW" TIMESTAMP,
	"UPDATED" TIMESTAMP NOT NULL,
	"CREATED" TIMESTAMP NOT NULL,
	"DIRECTION_SOURCE" VARCHAR2(20),
	"DIRECTION_TARGET" VARCHAR2(20),
	"GATEWAY_NAME" VARCHAR2(255)
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO"
(
	"ID" NUMBER(10) NOT NULL,
	"MESSAGE_ID" NUMBER(10) NOT NULL,
	"FK_FROM_PARTY_ID" NUMBER(10),
	"FK_TO_PARTY_ID" NUMBER(10),
	"ORIGINAL_SENDER" VARCHAR2(2048 CHAR),
	"FINAL_RECIPIENT" VARCHAR2(2048 CHAR),
	"FK_SERVICE" NUMBER(10),
	"FK_ACTION" NUMBER(10),
	"CREATED" TIMESTAMP NOT NULL,
	"UPDATED" TIMESTAMP NOT NULL
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_MSG_CONT"
(
	"ID" NUMBER(10) NOT NULL,
	"MESSAGE_ID" NUMBER(10) NOT NULL,
	"CONTENT_TYPE" VARCHAR2(255 CHAR),
	"CONTENT" BLOB,
	"CHECKSUM" CLOB,
	"CREATED" TIMESTAMP NOT NULL,
	"STORAGE_PROVIDER_NAME" VARCHAR2(255),
	"STORAGE_REFERENCE_ID" VARCHAR2(512),
	"DIGEST" VARCHAR2(512),
	"PAYLOAD_NAME" VARCHAR2(512),
	"PAYLOAD_IDENTIFIER" VARCHAR2(512),
	"PAYLOAD_DESCRIPTION" CLOB,
	"PAYLOAD_MIMETYPE" VARCHAR2(255),
	"PAYLOAD_SIZE" NUMBER(10),
	"DETACHED_SIGNATURE_ID" NUMBER(10),
	"DELETED" TIMESTAMP,
	"CONNECTOR_MESSAGE_ID" VARCHAR2(512 CHAR)
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_MSG_ERROR"
(
	"ID" NUMBER(10) NOT NULL,
	"MESSAGE_ID" NUMBER(10) NOT NULL,
	"ERROR_MESSAGE" VARCHAR2(2048 CHAR) NOT NULL,
	"DETAILED_TEXT" CLOB,
	"ERROR_SOURCE" CLOB,
	"CREATED" TIMESTAMP NOT NULL
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_PARTY"
(
	"ID" NUMBER(10) NOT NULL,
	"FK_PMODE_SET" NUMBER(10) NOT NULL,
	"IDENTIFIER" VARCHAR2(255),
	"PARTY_ID" VARCHAR2(255 CHAR) NOT NULL,
	"ROLE" VARCHAR2(255 CHAR) NOT NULL,
	"PARTY_ID_TYPE" VARCHAR2(512 CHAR) NOT NULL,
	"ROLE_TYPE" VARCHAR2(50 CHAR)
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_PROPERTY"
(
	"ID" NUMBER(10) NOT NULL,
	"PROPERTY_NAME" VARCHAR2(2048) NOT NULL,
	"PROPERTY_VALUE" VARCHAR2(2048)
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_SEQ_STORE"
(
	"SEQ_NAME" VARCHAR2(255 CHAR) NOT NULL,
	"SEQ_VALUE" NUMBER(10) NOT NULL
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_SERVICE"
(
	"ID" NUMBER(10) NOT NULL,
	"FK_PMODE_SET" NUMBER(10) NOT NULL,
	"SERVICE" VARCHAR2(255 CHAR) NOT NULL,
	"SERVICE_TYPE" VARCHAR2(512 CHAR)
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_USER"
(
	"ID" NUMBER(10) NOT NULL,
	"USERNAME" VARCHAR2(50) NOT NULL,
	"ROLE" VARCHAR2(50) NOT NULL,
	"LOCKED" NUMBER(1) DEFAULT 0 NOT NULL,
	"NUMBER_OF_GRACE_LOGINS" NUMBER(2) DEFAULT 5 NOT NULL,
	"GRACE_LOGINS_USED" NUMBER(2) DEFAULT 0 NOT NULL,
	"CREATED" TIMESTAMP NOT NULL
)
;

CREATE TABLE  "DOMIBUS_CONNECTOR_USER_PWD"
(
	"ID" NUMBER(10) NOT NULL,
	"USER_ID" NUMBER(10) NOT NULL,
	"PASSWORD" VARCHAR2(1024) NOT NULL,
	"SALT" VARCHAR2(512) NOT NULL,
	"CURRENT_PWD" NUMBER(1) DEFAULT 0 NOT NULL,
	"INITIAL_PWD" NUMBER(1) DEFAULT 0 NOT NULL,
	"CREATED" TIMESTAMP NOT NULL
)
;

ALTER TABLE  "DC_DB_VERSION" 
 ADD CONSTRAINT "PK_DC_DB_VERSION"
	PRIMARY KEY ("TAG") 
 USING INDEX
;

ALTER TABLE  "DC_KEYSTORE" 
 ADD CONSTRAINT "PK_DC_KEYSTORE"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_KEYSTORE" 
 ADD CONSTRAINT "UQ_DC_KEYSTORE" UNIQUE ("UUID") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_CONFIG_PROPERTY" 
 ADD CONSTRAINT "PK_DC_LINK_CONF_PROP"
	PRIMARY KEY ("DC_LINK_CONFIGURATION_ID","PROPERTY_NAME") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_CONFIGURATION" 
 ADD CONSTRAINT "PK_DC_LINK_CONFIGURATION"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_CONFIGURATION" 
 ADD CONSTRAINT "UN_DC_LINK_CONF_NAME_01" UNIQUE ("CONFIG_NAME") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_PARTNER" 
 ADD CONSTRAINT "PK_DC_LINK_P"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_PARTNER" 
 ADD CONSTRAINT "UN_DC_LINK_P_01" UNIQUE ("NAME") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_PARTNER_PROPERTY" 
 ADD CONSTRAINT "PK_DC_LINK_P_PROP"
	PRIMARY KEY ("DC_LINK_PARTNER_ID","PROPERTY_NAME") 
 USING INDEX
;

ALTER TABLE  "DC_MESSAGE_LANE" 
 ADD CONSTRAINT "PK_DC_MSG_LANE"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_MESSAGE_LANE" 
 ADD CONSTRAINT "UN_DC_MSG_LANE_01" UNIQUE ("NAME") 
 USING INDEX
;

ALTER TABLE  "DC_MESSAGE_LANE_PROPERTY" 
 ADD CONSTRAINT "PK_DC_MSG_LANE_PROP"
	PRIMARY KEY ("DC_MESSAGE_LANE_ID","PROPERTY_NAME") 
 USING INDEX
;

ALTER TABLE  "DC_MSGCNT_DETSIG" 
 ADD CONSTRAINT "PK_DETSIG"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_PMODE_SET" 
 ADD CONSTRAINT "PK_DC_PMODE_SET"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_TRANSPORT_STEP" 
 ADD CONSTRAINT "PK_DC_TRANSPORT_STEP"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_TRANSPORT_STEP_STATUS" 
 ADD CONSTRAINT "PK_DC_TRANS_STEP_STATUS"
	PRIMARY KEY ("TRANSPORT_STEP_ID","STATE") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_ACTION" 
 ADD CONSTRAINT "PK_DC_ACTION"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_BIGDATA"
 ADD CONSTRAINT "PK_DC_BIGDATA"
	PRIMARY KEY ("ID")
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_EVIDENCE" 
 ADD CONSTRAINT "PK_DC_EVIDENCE"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE" 
 ADD CONSTRAINT "PK_DC_MESSAGE"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE" 
 ADD CONSTRAINT "UQ_DC_MSG_BCK_MSG_ID" UNIQUE ("BACKEND_MESSAGE_ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE" 
 ADD CONSTRAINT "UQ_DC_MSG_CON_MSG_ID" UNIQUE ("CONNECTOR_MESSAGE_ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO" 
 ADD CONSTRAINT "PK_DC_MESSAGE_INFO"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MSG_CONT" 
 ADD CONSTRAINT "PK_DC_MSG_CONT"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MSG_ERROR" 
 ADD CONSTRAINT "PK_DC_MSG_ERROR"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_PARTY" 
 ADD CONSTRAINT "PK_DC_PARTY"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_PROPERTY" 
 ADD CONSTRAINT "PK_DOMIBUS_CONN_03"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_SEQ_STORE" 
 ADD CONSTRAINT "PK_DC_SEQ_STORE"
	PRIMARY KEY ("SEQ_NAME") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_SERVICE" 
 ADD CONSTRAINT "PK_DC_SERVICE"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_USER" 
 ADD CONSTRAINT "PK_DC_USER"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_USER" 
 ADD CONSTRAINT "USERNAME_UNIQUE" UNIQUE ("USERNAME") 
 USING INDEX
;

ALTER TABLE  "DOMIBUS_CONNECTOR_USER_PWD" 
 ADD CONSTRAINT "PK_DC_USER_PWD"
	PRIMARY KEY ("ID") 
 USING INDEX
;

ALTER TABLE  "DC_LINK_CONFIG_PROPERTY" 
 ADD CONSTRAINT "FK_DC_LINK_CONF_PROP_01"
	FOREIGN KEY ("DC_LINK_CONFIGURATION_ID") REFERENCES  "DC_LINK_CONFIGURATION" ("ID")
;

ALTER TABLE  "DC_LINK_PARTNER" 
 ADD CONSTRAINT "FK_DC_LINK_P_01"
	FOREIGN KEY ("LINK_CONFIG_ID") REFERENCES  "DC_LINK_CONFIGURATION" ("ID")
;

ALTER TABLE  "DC_LINK_PARTNER_PROPERTY" 
 ADD CONSTRAINT "FK_DC_LINK_P_PROP_01"
	FOREIGN KEY ("DC_LINK_PARTNER_ID") REFERENCES  "DC_LINK_PARTNER" ("ID")
;

ALTER TABLE  "DC_MESSAGE_LANE_PROPERTY" 
 ADD CONSTRAINT "FK_DC_MSG_LANE_PROP_01"
	FOREIGN KEY ("DC_MESSAGE_LANE_ID") REFERENCES  "DC_MESSAGE_LANE" ("ID")
;

ALTER TABLE  "DC_PMODE_SET" 
 ADD CONSTRAINT "FK_DC_PMODE_SET_01"
	FOREIGN KEY ("FK_MESSAGE_LANE") REFERENCES  "DC_MESSAGE_LANE" ("ID")
;

ALTER TABLE  "DC_PMODE_SET" 
 ADD CONSTRAINT "FK_DC_PMODE_SET_02"
	FOREIGN KEY ("FK_CONNECTORSTORE") REFERENCES  "DC_KEYSTORE" ("ID")
;

ALTER TABLE  "DC_TRANSPORT_STEP_STATUS" 
 ADD CONSTRAINT "FK_DC_TRANS_STEP_STATUS_01"
	FOREIGN KEY ("TRANSPORT_STEP_ID") REFERENCES  "DC_TRANSPORT_STEP" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_ACTION" 
 ADD CONSTRAINT "FK_DC_ACTION_PMODE_SET_01"
	FOREIGN KEY ("FK_PMODE_SET") REFERENCES  "DC_PMODE_SET" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_EVIDENCE" 
 ADD CONSTRAINT "FK_DC_EVIDENCES_01"
	FOREIGN KEY ("MESSAGE_ID") REFERENCES  "DOMIBUS_CONNECTOR_MESSAGE" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO" 
 ADD CONSTRAINT "FK_DC_MSG_INFO_I"
	FOREIGN KEY ("MESSAGE_ID") REFERENCES  "DOMIBUS_CONNECTOR_MESSAGE" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO" 
 ADD CONSTRAINT "FK_DC_MSG_INFO_ACTION"
	FOREIGN KEY ("FK_ACTION") REFERENCES  "DOMIBUS_CONNECTOR_ACTION" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO" 
 ADD CONSTRAINT "FK_DC_MSG_INFO_F_PARTY"
	FOREIGN KEY ("FK_FROM_PARTY_ID") REFERENCES  "DOMIBUS_CONNECTOR_PARTY" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO" 
 ADD CONSTRAINT "FK_DC_MSG_INFO_SERVICE"
	FOREIGN KEY ("FK_SERVICE") REFERENCES  "DOMIBUS_CONNECTOR_SERVICE" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MESSAGE_INFO" 
 ADD CONSTRAINT "FK_DC_MSG_INFO_T_PARTY"
	FOREIGN KEY ("FK_TO_PARTY_ID") REFERENCES  "DOMIBUS_CONNECTOR_PARTY" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MSG_CONT" 
 ADD CONSTRAINT "FK_DC_CON_01"
	FOREIGN KEY ("MESSAGE_ID") REFERENCES  "DOMIBUS_CONNECTOR_MESSAGE" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MSG_CONT" 
 ADD CONSTRAINT "FK_DC_MSG_CONT_02"
	FOREIGN KEY ("DETACHED_SIGNATURE_ID") REFERENCES  "DC_MSGCNT_DETSIG" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_MSG_ERROR" 
 ADD CONSTRAINT "FK_DC_MSG_ERROR_01"
	FOREIGN KEY ("MESSAGE_ID") REFERENCES  "DOMIBUS_CONNECTOR_MESSAGE" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_PARTY" 
 ADD CONSTRAINT "FK_DC_PARTY_PMODE_SET_01"
	FOREIGN KEY ("FK_PMODE_SET") REFERENCES  "DC_PMODE_SET" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_SERVICE" 
 ADD CONSTRAINT "FK_DC_SERVICE_PMODE_SET_01"
	FOREIGN KEY ("FK_PMODE_SET") REFERENCES  "DC_PMODE_SET" ("ID")
;

ALTER TABLE  "DOMIBUS_CONNECTOR_USER_PWD" 
 ADD CONSTRAINT "FK_DC_USER_PWD_01"
	FOREIGN KEY ("USER_ID") REFERENCES  "DOMIBUS_CONNECTOR_USER" ("ID")
;
