----------------------- domibusConnector Tables ------------------------------

CREATE TABLE DC_DB_VERSION (TAG VARCHAR(255) PRIMARY KEY);
INSERT INTO DC_DB_VERSION (TAG) VALUES ('V3.5');

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGES (
	ID NUMBER(10) NOT NULL,
	EBMS_MESSAGE_ID VARCHAR2(255) UNIQUE,
	NAT_MESSAGE_ID VARCHAR2(255) UNIQUE,
	CONVERSATION_ID VARCHAR2(255),
	DIRECTION VARCHAR2(10),
	HASH_VALUE VARCHAR2(1000),
	CONFIRMED TIMESTAMP,
	REJECTED TIMESTAMP,
	DELIVERED_NAT TIMESTAMP,
	DELIVERED_GW TIMESTAMP,
	UPDATED TIMESTAMP,
	PRIMARY KEY (ID)
);

CREATE TABLE DOMIBUS_CONNECTOR_EVIDENCES (
	ID  NUMBER(10) NOT NULL,
	MESSAGE_ID  NUMBER(10) NOT NULL,
	TYPE VARCHAR2(255),
	EVIDENCE CLOB,
	DELIVERED_NAT TIMESTAMP,
	DELIVERED_GW TIMESTAMP,
	UPDATED TIMESTAMP,
	PRIMARY KEY (ID),
	CONSTRAINT dc_evidences_fk_message FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGES (ID)
);

CREATE TABLE DOMIBUS_CONNECTOR_SEQ_STORE (
	SEQ_NAME VARCHAR2(255) NOT NULL,
	SEQ_VALUE  NUMBER(10) NOT NULL,
	PRIMARY KEY(SEQ_NAME)
);

CREATE TABLE DOMIBUS_CONNECTOR_PARTY (
	PARTY_ID VARCHAR2(50) NOT NULL,
	ROLE VARCHAR2(255) NOT NULL,
	PARTY_ID_TYPE VARCHAR2(255),
	PRIMARY KEY (PARTY_ID, ROLE)
);

CREATE TABLE DOMIBUS_CONNECTOR_ACTION (
	ACTION VARCHAR2(50) NOT NULL,
	PDF_REQUIRED NUMBER(1,0) DEFAULT 1 NOT NULL,
	PRIMARY KEY (ACTION)
);

CREATE TABLE DOMIBUS_CONNECTOR_SERVICE (
	SERVICE VARCHAR2(50) NOT NULL,
	SERVICE_TYPE VARCHAR2(255) NOT NULL,
	PRIMARY KEY (SERVICE)
);

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO (
	ID  NUMBER(10) NOT NULL,
	MESSAGE_ID  NUMBER(10) UNIQUE NOT NULL,
	FROM_PARTY_ID VARCHAR2(255),
	FROM_PARTY_ROLE VARCHAR2(255),
	TO_PARTY_ID VARCHAR2(255),
	TO_PARTY_ROLE VARCHAR2(255),
	ORIGINAL_SENDER VARCHAR2(255),
	FINAL_RECIPIENT VARCHAR2(255),
	SERVICE VARCHAR2(255),
	ACTION VARCHAR2(255),
	CREATED TIMESTAMP NOT NULL,
	UPDATED TIMESTAMP NOT NULL,
	PRIMARY KEY (ID),
	CONSTRAINT dc_msg_info_fk_message FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGES (ID),
	CONSTRAINT dc_msg_info_fk_from_party FOREIGN KEY (FROM_PARTY_ID, FROM_PARTY_ROLE) REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE),
	CONSTRAINT dc_msg_info_fk_to_party FOREIGN KEY (TO_PARTY_ID, TO_PARTY_ROLE) REFERENCES DOMIBUS_CONNECTOR_PARTY (PARTY_ID, ROLE),
	CONSTRAINT dc_msg_info_fk_service FOREIGN KEY (SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE (SERVICE),
	CONSTRAINT dc_msg_info_fk_action FOREIGN KEY (ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION (ACTION)
);

CREATE TABLE DOMIBUS_CONNECTOR_MSG_ERROR (
	ID  NUMBER(10) NOT NULL,
	MESSAGE_ID  NUMBER(10) NOT NULL,
	ERROR_MESSAGE VARCHAR2(255) NOT NULL,
	DETAILED_TEXT CLOB,
	ERROR_SOURCE VARCHAR2(255),
	CREATED TIMESTAMP NOT NULL,
	PRIMARY KEY (ID),
	CONSTRAINT dc_msg_error_fk_message FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGES (ID)
);




----------------------- Quartz Scheduler Tables ------------------------------

CREATE TABLE dcon_qrtz_job_details
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR2(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT dcon_qrtz_JOB_DETAILS_PK PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE dcon_qrtz_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT dcon_qrtz_TRIGGERS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT dcon_qrtz_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP) 
      REFERENCES dcon_qrtz_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP) 
);
CREATE TABLE dcon_qrtz_simple_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    CONSTRAINT dcon_SIMPLE_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT dcon_SIMPLE_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES dcon_qrtz_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dcon_qrtz_cron_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    CONSTRAINT dcon_qrtz_CRON_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT dcon_qrtz_CRON_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES dcon_qrtz_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dcon_qrtz_simprop_triggers
  (          
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    STR_PROP_1 VARCHAR2(512) NULL,
    STR_PROP_2 VARCHAR2(512) NULL,
    STR_PROP_3 VARCHAR2(512) NULL,
    INT_PROP_1 NUMBER(10) NULL,
    INT_PROP_2 NUMBER(10) NULL,
    LONG_PROP_1 NUMBER(13) NULL,
    LONG_PROP_2 NUMBER(13) NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(1) NULL,
    BOOL_PROP_2 VARCHAR2(1) NULL,
    CONSTRAINT dcon_SIMPROP_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT dcon_SIMPROP_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES dcon_qrtz_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dcon_qrtz_blob_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    CONSTRAINT dcon_qrtz_BLOB_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT dcon_qrtz_BLOB_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES dcon_qrtz_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dcon_qrtz_calendars
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    CONSTRAINT dcon_qrtz_CALENDARS_PK PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);
CREATE TABLE dcon_qrtz_paused_trigger_grps
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    CONSTRAINT dcon_qrtz_PAUSED_TRIG_GRPS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);
CREATE TABLE dcon_qrtz_fired_triggers 
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_NONCONCURRENT VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    CONSTRAINT dcon_qrtz_FIRED_TRIGGER_PK PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);
CREATE TABLE dcon_qrtz_scheduler_state 
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    CONSTRAINT dcon_qrtz_SCHEDULER_STATE_PK PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);
CREATE TABLE dcon_qrtz_locks
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    CONSTRAINT dcon_qrtz_LOCKS_PK PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

create index idx_dcon_qrtz_j_req_recovery on dcon_qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_dcon_qrtz_j_grp on dcon_qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index idx_dcon_qrtz_t_1 on dcon_qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_dcon_qrtz_t_2 on dcon_qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_dcon_qrtz_t_3 on dcon_qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_dcon_qrtz_t_4 on dcon_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_dcon_qrtz_t_5 on dcon_qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_dcon_qrtz_t_6 on dcon_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_dcon_qrtz_t_7 on dcon_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_dcon_qrtz_t_8 on dcon_qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_dcon_qrtz_t_9 on dcon_qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_dcon_qrtz_t_10 on dcon_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_dcon_qrtz_t_11 on dcon_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_dcon_qrtz_t_12 on dcon_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_dcon_qrtz_ft_1 on dcon_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_dcon_qrtz_ft_2 on dcon_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_dcon_qrtz_ft_3 on dcon_qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_dcon_qrtz_ft_4 on dcon_qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_dcon_qrtz_ft_5 on dcon_qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_dcon_qrtz_ft_6 on dcon_qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);


CREATE TABLE DOMIBUS_WEBADMIN_USER (
  USERNAME  VARCHAR2(30)  NOT NULL,
  PASSWORD  VARCHAR2(150)  NOT NULL,
  SALT VARCHAR2(64) NOT NULL,
  ROLE	    VARCHAR2(20) NOT NULL,
  PRIMARY KEY (USERNAME)
);

INSERT INTO DOMIBUS_WEBADMIN_USER (USERNAME, PASSWORD, SALT, ROLE) VALUES ('admin', '2bf5e637d0d82a75ca43e3be85df2c23febffc0cc221f5e010937005df478a19b5eaab59fe7e4e97f6b43ba648c169effd432e19817f386987d058c239236306', '5b424031616564356639', 'admin');

CREATE TABLE DOMIBUS_WEBADMIN_PROPERTIES (
  PROPERTIES_KEY VARCHAR2(30),
  PROPERTIES_VALUE VARCHAR2(100)
);


commit;