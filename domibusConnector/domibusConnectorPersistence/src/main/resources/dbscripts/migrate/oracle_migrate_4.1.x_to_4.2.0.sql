-- *********************************************************************
-- Update Database Script - from domibusConnector 4.1 to 4.2
-- *********************************************************************
-- adds quartz tables
--

-- #################### 1/3 RENAME TABLES ####################
RENAME DOMIBUS_CONNECTOR_MESSAGE TO BKP_DC_MESSAGE;
RENAME DOMIBUS_CONNECTOR_MESSAGE_INFO TO BKP_DC_MESSAGE_INFO;
RENAME DOMIBUS_CONNECTOR_PARTY TO BKP_DC_PARTY;
RENAME DOMIBUS_CONNECTOR_ACTION TO BKP_DC_ACTION;
RENAME DOMIBUS_CONNECTOR_SERVICE TO BKP_DC_SERVICE;
rename DOMIBUS_CONNECTOR_PROPERTY to bkp_dc_property;

-- #################### 2/3 CREATE TABLES AND CONSTRAINTS ####################

CREATE TABLE qrtz_job_details
(
    SCHED_NAME        VARCHAR2(120) NOT NULL,
    JOB_NAME          VARCHAR2(200) NOT NULL,
    JOB_GROUP         VARCHAR2(200) NOT NULL,
    DESCRIPTION       VARCHAR2(250) NULL,
    JOB_CLASS_NAME    VARCHAR2(250) NOT NULL,
    IS_DURABLE        VARCHAR2(1)   NOT NULL,
    IS_NONCONCURRENT  VARCHAR2(1)   NOT NULL,
    IS_UPDATE_DATA    VARCHAR2(1)   NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1)   NOT NULL,
    JOB_DATA          BLOB          NULL,
    CONSTRAINT QRTZ_JOB_DETAILS_PK PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
);
CREATE TABLE qrtz_triggers
(
    SCHED_NAME     VARCHAR2(120) NOT NULL,
    TRIGGER_NAME   VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL,
    JOB_NAME       VARCHAR2(200) NOT NULL,
    JOB_GROUP      VARCHAR2(200) NOT NULL,
    DESCRIPTION    VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13)    NULL,
    PREV_FIRE_TIME NUMBER(13)    NULL,
    PRIORITY       NUMBER(13)    NULL,
    TRIGGER_STATE  VARCHAR2(16)  NOT NULL,
    TRIGGER_TYPE   VARCHAR2(8)   NOT NULL,
    START_TIME     NUMBER(13)    NOT NULL,
    END_TIME       NUMBER(13)    NULL,
    CALENDAR_NAME  VARCHAR2(200) NULL,
    MISFIRE_INSTR  NUMBER(2)     NULL,
    JOB_DATA       BLOB          NULL,
    CONSTRAINT QRTZ_TRIGGERS_PK PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    CONSTRAINT QRTZ_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
);
CREATE TABLE qrtz_simple_triggers
(
    SCHED_NAME      VARCHAR2(120)  NOT NULL,
    TRIGGER_NAME    VARCHAR2(200)  NOT NULL,
    TRIGGER_GROUP   VARCHAR2(200)  NOT NULL,
    REPEAT_COUNT    NUMBER(7)      NOT NULL,
    REPEAT_INTERVAL NUMBER(12)     NOT NULL,
    TIMES_TRIGGERED DECIMAL(10, 0) NOT NULL,
    CONSTRAINT QRTZ_SIMPLE_TRIG_PK PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPLE_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);
CREATE TABLE qrtz_cron_triggers
(
    SCHED_NAME      VARCHAR2(120) NOT NULL,
    TRIGGER_NAME    VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP   VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID    VARCHAR2(80),
    CONSTRAINT QRTZ_CRON_TRIG_PK PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    CONSTRAINT QRTZ_CRON_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);
CREATE TABLE qrtz_simprop_triggers
(
    SCHED_NAME    VARCHAR2(120)  NOT NULL,
    TRIGGER_NAME  VARCHAR2(200)  NOT NULL,
    TRIGGER_GROUP VARCHAR2(200)  NOT NULL,
    STR_PROP_1    VARCHAR2(512)  NULL,
    STR_PROP_2    VARCHAR2(512)  NULL,
    STR_PROP_3    VARCHAR2(512)  NULL,
    INT_PROP_1    DECIMAL(10, 0) NULL,
    INT_PROP_2    DECIMAL(10, 0) NULL,
    LONG_PROP_1   NUMBER(13)     NULL,
    LONG_PROP_2   NUMBER(13)     NULL,
    DEC_PROP_1    NUMERIC(13, 4) NULL,
    DEC_PROP_2    NUMERIC(13, 4) NULL,
    BOOL_PROP_1   VARCHAR2(1)    NULL,
    BOOL_PROP_2   VARCHAR2(1)    NULL,
    CONSTRAINT QRTZ_SIMPROP_TRIG_PK PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPROP_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);
CREATE TABLE qrtz_blob_triggers
(
    SCHED_NAME    VARCHAR2(120) NOT NULL,
    TRIGGER_NAME  VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA     BLOB          NULL,
    CONSTRAINT QRTZ_BLOB_TRIG_PK PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    CONSTRAINT QRTZ_BLOB_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);
CREATE TABLE qrtz_calendars
(
    SCHED_NAME    VARCHAR2(120) NOT NULL,
    CALENDAR_NAME VARCHAR2(200) NOT NULL,
    CALENDAR      BLOB          NOT NULL,
    CONSTRAINT QRTZ_CALENDARS_PK PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
);
CREATE TABLE qrtz_paused_trigger_grps
(
    SCHED_NAME    VARCHAR2(120) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CONSTRAINT QRTZ_PAUSED_TRIG_GRPS_PK PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
);
CREATE TABLE qrtz_fired_triggers
(
    SCHED_NAME        VARCHAR2(120) NOT NULL,
    ENTRY_ID          VARCHAR2(95)  NOT NULL,
    TRIGGER_NAME      VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP     VARCHAR2(200) NOT NULL,
    INSTANCE_NAME     VARCHAR2(200) NOT NULL,
    FIRED_TIME        NUMBER(13)    NOT NULL,
    SCHED_TIME        NUMBER(13)    NOT NULL,
    PRIORITY          NUMBER(13)    NOT NULL,
    STATE             VARCHAR2(16)  NOT NULL,
    JOB_NAME          VARCHAR2(200) NULL,
    JOB_GROUP         VARCHAR2(200) NULL,
    IS_NONCONCURRENT  VARCHAR2(1)   NULL,
    REQUESTS_RECOVERY VARCHAR2(1)   NULL,
    CONSTRAINT QRTZ_FIRED_TRIGGER_PK PRIMARY KEY (SCHED_NAME, ENTRY_ID)
);
CREATE TABLE qrtz_scheduler_state
(
    SCHED_NAME        VARCHAR2(120) NOT NULL,
    INSTANCE_NAME     VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13)    NOT NULL,
    CHECKIN_INTERVAL  NUMBER(13)    NOT NULL,
    CONSTRAINT QRTZ_SCHEDULER_STATE_PK PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
);
CREATE TABLE qrtz_locks
(
    SCHED_NAME VARCHAR2(120) NOT NULL,
    LOCK_NAME  VARCHAR2(40)  NOT NULL,
    CONSTRAINT QRTZ_LOCKS_PK PRIMARY KEY (SCHED_NAME, LOCK_NAME)
);

create index idx_qrtz_j_req_recovery on qrtz_job_details (SCHED_NAME, REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on qrtz_job_details (SCHED_NAME, JOB_GROUP);

create index idx_qrtz_t_j on qrtz_triggers (SCHED_NAME, JOB_NAME, JOB_GROUP);
create index idx_qrtz_t_jg on qrtz_triggers (SCHED_NAME, JOB_GROUP);
create index idx_qrtz_t_c on qrtz_triggers (SCHED_NAME, CALENDAR_NAME);
create index idx_qrtz_t_g on qrtz_triggers (SCHED_NAME, TRIGGER_GROUP);
create index idx_qrtz_t_state on qrtz_triggers (SCHED_NAME, TRIGGER_STATE);
create index idx_qrtz_t_n_state on qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on qrtz_triggers (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on qrtz_triggers (SCHED_NAME, NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on qrtz_triggers (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP,
                                                             TRIGGER_STATE);

create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers (SCHED_NAME, INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on qrtz_fired_triggers (SCHED_NAME, JOB_NAME, JOB_GROUP);
create index idx_qrtz_ft_jg on qrtz_fired_triggers (SCHED_NAME, JOB_GROUP);
create index idx_qrtz_ft_t_g on qrtz_fired_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
create index idx_qrtz_ft_tg on qrtz_fired_triggers (SCHED_NAME, TRIGGER_GROUP);

----- DOMIBUS_CONNECTOR_MESSAGE ------

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE
(
    ID                   DECIMAL(10, 0) NOT NULL,
    EBMS_MESSAGE_ID      VARCHAR2(255 CHAR),
    BACKEND_MESSAGE_ID   VARCHAR2(255 CHAR),
    BACKEND_NAME         VARCHAR2(255 CHAR),
    CONNECTOR_MESSAGE_ID VARCHAR2(255 CHAR),
    CONVERSATION_ID      VARCHAR2(255 CHAR),
    HASH_VALUE           CLOB,
    CONFIRMED            TIMESTAMP,
    REJECTED             TIMESTAMP,
    DELIVERED_BACKEND    TIMESTAMP,
    DELIVERED_GW         TIMESTAMP,
    UPDATED              TIMESTAMP,
    CREATED              TIMESTAMP,
    DIRECTION_SOURCE     VARCHAR2(20),
    DIRECTION_TARGET     VARCHAR2(20),
    GATEWAY_NAME         VARCHAR2(255)
);

----- DOMIBUS_CONNECTOR_MESSAGE_INFO ------

CREATE TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
(
    ID               DECIMAL(10, 0) NOT NULL,
    MESSAGE_ID       DECIMAL(10, 0) NOT NULL,
    FK_FROM_PARTY_ID DECIMAL(10, 0),
    FK_TO_PARTY_ID   DECIMAL(10, 0),
    ORIGINAL_SENDER  VARCHAR2(2048 CHAR),
    FINAL_RECIPIENT  VARCHAR2(2048 CHAR),
    FK_SERVICE       DECIMAL(10, 0),
    FK_ACTION        DECIMAL(10, 0),
    CREATED          TIMESTAMP,
    UPDATED          TIMESTAMP
);

CREATE TABLE DOMIBUS_CONNECTOR_PARTY
(
    ID            DECIMAL(10, 0) NOT NULL,
    FK_PMODE_SET  DECIMAL(10, 0) NOT NULL,
    IDENTIFIER    VARCHAR2(255),
    PARTY_ID      VARCHAR2(255)  NOT NULL,
    ROLE          VARCHAR2(255)  NOT NULL,
    PARTY_ID_TYPE VARCHAR2(512)  NOT NULL
);

CREATE TABLE DOMIBUS_CONNECTOR_ACTION
(
    ID           DECIMAL(10, 0) NOT NULL,
    FK_PMODE_SET DECIMAL(10, 0) NOT NULL,
    ACTION       VARCHAR2(255)  NOT NULL,
    PDF_REQUIRED DECIMAL(1, 0)  NOT NULL
);

CREATE TABLE DOMIBUS_CONNECTOR_SERVICE
(
    ID           DECIMAL(10, 0) NOT NULL,
    FK_PMODE_SET DECIMAL(10, 0) NOT NULL,
    SERVICE      VARCHAR2(255)  NOT NULL,
    SERVICE_TYPE VARCHAR2(255)
);

CREATE TABLE DOMIBUS_CONNECTOR_PROPERTY
(
    ID             DECIMAL(10, 0) NOT NULL PRIMARY KEY,
    PROPERTY_NAME  VARCHAR2(2048) NOT NULL,
    PROPERTY_VALUE VARCHAR2(2048) NULL
);

create table DC_LINK_CONFIGURATION
(
    ID          DECIMAL(10, 0) not null,
    CONFIG_NAME VARCHAR2(255)  not null
        constraint UNQ_DC_LINK_CONFIG_NMAE unique,
    LINK_IMPL   VARCHAR2(255),
    constraint PK_DC_LINK_CONFIGURATION primary key (ID)
);

create table DC_LINK_CONFIG_PROPERTY
(
    DC_LINK_CONFIGURATION_ID DECIMAL(10, 0) not null,
    PROPERTY_NAME            VARCHAR2(255)  not null,
    PROPERTY_VALUE           CLOB
);

create table DC_MESSAGE_LANE
(
    ID          DECIMAL(10, 0) not null,
    NAME        VARCHAR2(255)  not null,
    DESCRIPTION CLOB
);

create table DC_MESSAGE_LANE_PROPERTY
(
    DC_MESSAGE_LANE_ID DECIMAL(10, 0) not null,
    PROPERTY_NAME      VARCHAR2(255)  not null,
    PROPERTY_VALUE     CLOB
);

create table DC_LINK_PARTNER
(
    ID             DECIMAL(10, 0) not null,
    NAME           VARCHAR2(255)  not null,
    DESCRIPTION    CLOB,
    ENABLED        DECIMAL(1, 0),
    LINK_CONFIG_ID DECIMAL(10, 0),
    LINK_TYPE      VARCHAR2(20),
    LINK_MODE      VARCHAR2(20)
);

create table DC_LINK_PARTNER_PROPERTY
(
    DC_LINK_PARTNER_ID DECIMAL(10, 0) not null,
    PROPERTY_NAME      VARCHAR2(255)  not null,
    PROPERTY_VALUE     CLOB
);

create table DC_TRANSPORT_STEP
(
    ID                          DECIMAL(10, 0) not null,
    MESSAGE_ID                  DECIMAL(10, 0) not null,
    LINK_PARTNER_NAME           VARCHAR2(255)  not null,
    ATTEMPT                     INT            not null,
    TRANSPORT_ID                VARCHAR2(255),
    TRANSPORT_SYSTEM_MESSAGE_ID VARCHAR2(255),
    REMOTE_MESSAGE_ID           VARCHAR2(255),
    CREATED                     TIMESTAMP
);

create table DC_TRANSPORT_STEP_STATUS
(
    TRANSPORT_STEP_ID DECIMAL(10, 0) not null,
    STATE             VARCHAR2(40)   not null,
    CREATED           TIMESTAMP,
    TEXT              CLOB
);

create table DC_MSGCNT_DETSIG
(
    ID             DECIMAL(10, 0) not null,
    SIGNATURE      CLOB,
    SIGNATURE_NAME VARCHAR2(255),
    SIGNATURE_TYPE VARCHAR2(255)
);

CREATE TABLE DC_PMODE_SET
(
    ID              DECIMAL(10, 0),
    FK_MESSAGE_LANE DECIMAL(10, 0),
    CREATED         TIMESTAMP,
    DESCRIPTION     CLOB,
    ACTIVE          DECIMAL(1, 0) DEFAULT 0 NOT NULL
);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_MESSAGE PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT UNIQUE_CONNECTOR_MESSAGE_ID UNIQUE (CONNECTOR_MESSAGE_ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE
    ADD CONSTRAINT UQ_DOMIBUS_CONNE_NAT_MESSAGE_ UNIQUE (BACKEND_MESSAGE_ID);

alter table DC_PMODE_SET
    add CONSTRAINT PK_DC_PMODE_SET PRIMARY KEY (ID);

ALTER TABLE DOMIBUS_CONNECTOR_PARTY
    ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_PARTY PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_PARTY
    ADD CONSTRAINT FK_PARTY_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET (ID);

ALTER TABLE DOMIBUS_CONNECTOR_SERVICE
    ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_SERVICE PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE
    ADD CONSTRAINT FK_SERVICE_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET (ID);

ALTER TABLE DOMIBUS_CONNECTOR_ACTION
    ADD CONSTRAINT PK_DOMIBUS_CONNECTOR_ACTION PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_ACTION
    ADD CONSTRAINT FK_ACTION_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET (ID);

ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT PK_CONNECTOR_MESSAGE_INFO PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_MSGINFO_FROM_PARTY FOREIGN KEY (FK_FROM_PARTY_ID) REFERENCES DOMIBUS_CONNECTOR_PARTY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_MSGINFO_TO_PARTY FOREIGN KEY (FK_TO_PARTY_ID) REFERENCES DOMIBUS_CONNECTOR_PARTY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_MSGINFO_SERVICE FOREIGN KEY (FK_SERVICE) REFERENCES DOMIBUS_CONNECTOR_SERVICE (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_MSGINFO_ACTION FOREIGN KEY (FK_ACTION) REFERENCES DOMIBUS_CONNECTOR_ACTION (ID);
ALTER TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO
    ADD CONSTRAINT FK_DOMIBUS_CONNECTOR_MESSAGE_I FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD STORAGE_PROVIDER_NAME VARCHAR2(255);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD STORAGE_REFERENCE_ID VARCHAR2(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD DIGEST VARCHAR2(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD PAYLOAD_NAME VARCHAR2(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD PAYLOAD_IDENTIFIER VARCHAR2(512);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD PAYLOAD_DESCRIPTION CLOB;
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD PAYLOAD_MIMETYPE VARCHAR2(255);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD PAYLOAD_SIZE DECIMAL(10, 0);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD DETACHED_SIGNATURE_ID DECIMAL(10, 0);
alter table DOMIBUS_CONNECTOR_MSG_CONT
    ADD DELETED TIMESTAMP;

ALTER TABLE DOMIBUS_CONNECTOR_BACK_2_S
    DROP CONSTRAINT FK_DC_BACK2S_02;

alter table DC_LINK_CONFIG_PROPERTY
    add constraint PK_DC_LINK_CONFIG_PROPERTY
        primary key (DC_LINK_CONFIGURATION_ID, PROPERTY_NAME);
alter table DC_LINK_CONFIG_PROPERTY
    add constraint FK_LINKPROPERTY_LINKCONFIG
        foreign key (DC_LINK_CONFIGURATION_ID) references DC_LINK_CONFIGURATION (ID);

alter table DC_MESSAGE_LANE
    add constraint PK_DC_MESSAGE_LANE_ID primary key (ID);
alter table DC_MESSAGE_LANE
    add constraint UNQ_DC_MESSAGE_LANE unique (NAME);

alter table DC_MESSAGE_LANE_PROPERTY
    add constraint PK_DC_MESSAGE_LANE_PROPERTY
        primary key (DC_MESSAGE_LANE_ID, PROPERTY_NAME);
alter table DC_MESSAGE_LANE_PROPERTY
    add constraint FK_MSGLANEPROPERTY_MSGLANE
        foreign key (DC_MESSAGE_LANE_ID) references DC_MESSAGE_LANE (ID);

alter table DC_LINK_PARTNER
    add constraint PK_DC_LINK_PARTNER
        primary key (ID);
alter table DC_LINK_PARTNER
    add constraint UNQ_LINK_INFO_NAME unique (NAME);
alter table DC_LINK_PARTNER
    add constraint FK_LINKINFO_LINKCONFIG
        foreign key (LINK_CONFIG_ID) references DC_LINK_CONFIGURATION (ID);

alter table DC_LINK_PARTNER_PROPERTY
    add constraint PK_DC_LINK_PARTNER_PROPERTY
        primary key (DC_LINK_PARTNER_ID, PROPERTY_NAME);
alter table DC_LINK_PARTNER_PROPERTY
    add constraint FK_LINKPROPERTY_LINKPARTNER
        foreign key (DC_LINK_PARTNER_ID) references DC_LINK_PARTNER (ID);

alter table DC_TRANSPORT_STEP
    add constraint PK_DC_TRANSPORT_STEP
        primary key (ID);
alter table DC_TRANSPORT_STEP
    add constraint FK_MESSAGESTEP_MESSAGE
        foreign key (MESSAGE_ID) references DOMIBUS_CONNECTOR_MESSAGE (ID);

alter table DC_TRANSPORT_STEP_STATUS
    add constraint PK_DC_TRANSPORT_STEP_STATUS primary key (TRANSPORT_STEP_ID, STATE);
alter table DC_TRANSPORT_STEP_STATUS
    add constraint FK_trst_trstst
        foreign key (TRANSPORT_STEP_ID) references DC_TRANSPORT_STEP (ID);

alter table DC_MSGCNT_DETSIG
    add constraint PK_DETACHED_SIGNATURE primary key (ID);

-- #################### 3/3 COPY / CREATE DATA ####################

INSERT INTO DOMIBUS_CONNECTOR_MESSAGE
select ID,
       EBMS_MESSAGE_ID,
       BACKEND_MESSAGE_ID,
       BACKEND_NAME,
       CONNECTOR_MESSAGE_ID,
       CONVERSATION_ID,
       HASH_VALUE,
       CONFIRMED,
       REJECTED,
       DELIVERED_BACKEND,
       DELIVERED_GW,
       UPDATED,
       CREATED,
       (CASE DIRECTION
            WHEN 'GW_TO_NAT' THEN 'GATEWAY'
            WHEN 'NAT_TO_GW' THEN 'BACKEND'
            ELSE null END) as DIRECTION_SOURCE,
       (CASE DIRECTION
            WHEN 'GW_TO_NAT' THEN 'BACKEND'
            WHEN 'NAT_TO_GW' THEN 'GATEWAY'
            ELSE null END) as DIRECTION_TARGET,
       null                as GATEWAY_NAME

from BKP_DC_MESSAGE M;

insert into DOMIBUS_CONNECTOR_MESSAGE_INFO
select B.ID,
       B.MESSAGE_ID,
       CASE
           when FROM_PARTY_ID is not null and FROM_PARTY_ROLE is not null
               then (select id
                     from DOMIBUS_CONNECTOR_PARTY FP
                     where FP.PARTY_ID = FROM_PARTY_ID
                       and FP.ROLE = FROM_PARTY_ROLE)
           else 1
           end
           as FK_FROM_PARTY_ID,
       CASE
           when TO_PARTY_ID is not null and TO_PARTY_ROLE is not null
               then (select id
                     from DOMIBUS_CONNECTOR_PARTY FP
                     where FP.PARTY_ID = TO_PARTY_ID
                       and FP.ROLE = TO_PARTY_ROLE)
           else 1
           end
           as FK_TO_PARTY_ID,
       ORIGINAL_SENDER,
       FINAL_RECIPIENT,
       CASE
           when B.SERVICE is not null
               then (select id from DOMIBUS_CONNECTOR_SERVICE S where S.SERVICE = B.SERVICE)
           else 1
           end
           as FK_SERVICE,
       CASE
           when B.ACTION is not null
               then (select id from DOMIBUS_CONNECTOR_ACTION A where A.ACTION = B.ACTION)
           else 1
           end
           as FK_ACTION,
       CREATED,
       UPDATED
from BKP_DC_MESSAGE_INFO B;
-- needs to happen here
drop table BKP_DC_MESSAGE_INFO;

-- DC_MESSAGE_LANE

INSERT INTO DC_MESSAGE_LANE (ID, NAME, DESCRIPTION)
VALUES (1, 'default_message_lane', 'default message lane');

-- DC_PMODE_SET

INSERT INTO DC_PMODE_SET (ID, FK_MESSAGE_LANE, CREATED, DESCRIPTION, ACTIVE)
VALUES (1,
        null,
        sysdate,
        'initial set created by migration script',
        1);

----- DOMIBUS_CONNECTOR_PARTY ------

CREATE SEQUENCE DC_PARTY_SEQ INCREMENT BY 1 START WITH 1000;

-- delete old default value row
delete
from BKP_DC_PARTY
where PARTY_ID = 'n.a.';

-- todo delete cont from here
insert into DOMIBUS_CONNECTOR_PARTY
values (1, 1, null, 'n.a.', 'n.a.', 'n.a.');

insert into DOMIBUS_CONNECTOR_PARTY
select DC_PARTY_SEQ.nextval,
       1,
       null as IDENTIFIER,
       PARTY_ID,
       ROLE,
       PARTY_ID_TYPE
from BKP_DC_PARTY;

drop table BKP_DC_PARTY;

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_PARTY.ID', DC_PARTY_SEQ.nextval);

DROP SEQUENCE DC_PARTY_SEQ;

----- DOMIBUS_CONNECTOR_ACTION ------

CREATE SEQUENCE DC_ACTION_SEQ INCREMENT BY 1 START WITH 1000;

-- delete old default value row
delete
from BKP_DC_ACTION
where ACTION = 'n.a.';

insert into DOMIBUS_CONNECTOR_ACTION
values (1, 1, 'n.a.', 0);

insert into DOMIBUS_CONNECTOR_ACTION
select DC_ACTION_SEQ.nextval,
       1,
       ACTION,
       PDF_REQUIRED
from BKP_DC_ACTION;

drop table BKP_DC_ACTION;

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_ACTION.ID', DC_ACTION_SEQ.nextval);

DROP SEQUENCE DC_ACTION_SEQ;

----- DOMIBUS_CONNECTOR_SERVICE ------

CREATE SEQUENCE DC_SERVICE_SEQ INCREMENT BY 1 START WITH 1000;

-- delete old default value row
delete
from BKP_DC_SERVICE
where SERVICE = 'n.a.';

insert into DOMIBUS_CONNECTOR_SERVICE
values (1, 1, 'n.a.', 'n.a.');

insert into DOMIBUS_CONNECTOR_SERVICE
select DC_SERVICE_SEQ.nextval,
       1,
       SERVICE,
       SERVICE_TYPE
from BKP_DC_SERVICE;

drop table BKP_DC_SERVICE;

INSERT INTO DOMIBUS_CONNECTOR_SEQ_STORE
VALUES ('DOMIBUS_CONNECTOR_SERVICE.ID', DC_SERVICE_SEQ.nextval);

DROP SEQUENCE DC_SERVICE_SEQ;
-- todo delete
-- select distinct FK_FROM_PARTY_ID, mi.ID, mi.MESSAGE_ID
-- from DOMIBUS_CONNECTOR_MESSAGE_INFO mi
-- where FK_FROM_PARTY_ID not in
--       (select id from DOMIBUS_CONNECTOR_PARTY where FK_FROM_PARTY_ID = DOMIBUS_CONNECTOR_PARTY.ID);
-- DOMIBUS_CONNECTOR_PROPERTY

CREATE SEQUENCE DC_PROPERTY_SEQ INCREMENT BY 1 START WITH 1000;

insert into DOMIBUS_CONNECTOR_PROPERTY
select DC_PROPERTY_SEQ.nextval as ID, PROPERTY_NAME, PROPERTY_VALUE
from bkp_dc_property;

drop table BKP_DC_PROPERTY;

UPDATE DOMIBUS_CONNECTOR_SEQ_STORE
set SEQ_VALUE=DC_PROPERTY_SEQ.nextval
where SEQ_NAME='DOMIBUS_CONNECTOR_PROPERTY.ID';

drop sequence DC_PROPERTY_SEQ;

-- TODO [2021-04-09 12:03:38] [72000][2449] ORA-02449: unique/primary keys in table referenced by foreign keys
drop table BKP_DC_MESSAGE;

--- Update version
INSERT INTO DC_DB_VERSION (TAG)
VALUES ('V4.2.8');
