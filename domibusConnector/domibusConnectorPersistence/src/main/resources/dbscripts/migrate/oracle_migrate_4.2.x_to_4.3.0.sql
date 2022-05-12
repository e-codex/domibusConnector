-- *********************************************************************
-- Update Database Script - from domibusConnector 4.2 to 4.3
-- *********************************************************************
-- change message timestamps in DOMIBUS_CONNECTOR_EVIDENCE and DOMIBUS_CONNECTOR_MESSAGE
-- remove not null constraint on service_type in DOMIBUS_CONNECTOR_SERVICE
--

-- #################### 1/6 RENAME tables that need to be recreated ####################

rename DOMIBUS_CONNECTOR_SERVICE to BKP_DC_SERVICE;

-- #################### 2/6 CREATE tables, structural changes ####################

-- delayed to 4.4
-- alter table DOMIBUS_CONNECTOR_ACTION drop column PDF_REQUIRED;

alter table DC_TRANSPORT_STEP add           TRANSPORT_MESSAGE CLOB;
alter table DC_TRANSPORT_STEP add FINAL_STATE_REACHED TIMESTAMP;

alter table DOMIBUS_CONNECTOR_MSG_CONT
    add CONNECTOR_MESSAGE_ID VARCHAR2(512 CHAR);

CREATE TABLE DOMIBUS_CONNECTOR_SERVICE
(
    ID           DECIMAL(10, 0) NOT NULL,
    FK_PMODE_SET DECIMAL(10, 0) NOT NULL,
    SERVICE      VARCHAR2(255)  NOT NULL,
    SERVICE_TYPE VARCHAR2(512)
);

alter table DOMIBUS_CONNECTOR_EVIDENCE modify ("UPDATED" not null);

alter table DOMIBUS_CONNECTOR_MESSAGE modify ("CREATED" not null);
alter table DOMIBUS_CONNECTOR_MESSAGE modify ("UPDATED" not null);

alter table DOMIBUS_CONNECTOR_MESSAGE_INFO modify ("CREATED" not null);
alter table DOMIBUS_CONNECTOR_MESSAGE_INFO modify ("UPDATED" not null);

alter table DOMIBUS_CONNECTOR_MSG_CONT modify ("MESSAGE_ID" not null);
alter table DOMIBUS_CONNECTOR_MSG_CONT modify ("CREATED" not null);

alter table DOMIBUS_CONNECTOR_BIGDATA modify ("CREATED" not null);

alter table DC_PMODE_SET modify ("CREATED" not null);
alter table DC_PMODE_SET modify ("ACTIVE" not null);

-- FK_MESSAGESTEP_MESSAGE is very likely already deleted, because the connector does not work, if it still exists.
-- To prevent errors I wrapped the deletion in a psql try catch
-- DECLARE
--     DC_TRANSPORT_STEP       VARCHAR2(250):= 'DC_TRANSPORT_STEP';
--     FK_MESSAGESTEP_MESSAGE           VARCHAR2(250):= 'FK_MESSAGESTEP_MESSAGE';
-- BEGIN
--     EXECUTE IMMEDIATE 'alter table ' || DC_TRANSPORT_STEP || ' drop constraint ' || FK_MESSAGESTEP_MESSAGE;
-- EXCEPTION
--     WHEN OTHERS THEN
--         -- "ORA-02443: Cannot drop constraint  - nonexistent constraint"
--         -- exceptions are ignored, anything else is raised
--         IF SQLCODE != -2443 THEN
--             RAISE;
--         END IF;
-- END;

-- #################### 3/6 TRANSFER & UPDATE data ####################

update DOMIBUS_CONNECTOR_SEQ_STORE
set SEQ_NAME='DOMIBUS_CONNECTOR_MESSAGE.ID'
where not exists
    (select SEQ_NAME
     from DOMIBUS_CONNECTOR_SEQ_STORE
     where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGE.ID') and
    exists
        (select SEQ_NAME
         from DOMIBUS_CONNECTOR_SEQ_STORE
         where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGES.ID')
;

update DOMIBUS_CONNECTOR_SEQ_STORE
set SEQ_NAME='DOMIBUS_CONNECTOR_EVIDENCE.ID'
where not exists
    (select SEQ_NAME
     from DOMIBUS_CONNECTOR_SEQ_STORE
     where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCE.ID') and
    exists
        (select SEQ_NAME
         from DOMIBUS_CONNECTOR_SEQ_STORE
         where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID')
;

delete from DOMIBUS_CONNECTOR_SEQ_STORE
where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGE.ID' AND
    EXISTS(select SEQ_NAME
           from DOMIBUS_CONNECTOR_SEQ_STORE
           where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGES.ID') AND
    EXISTS(select SEQ_NAME
           from DOMIBUS_CONNECTOR_SEQ_STORE
           where SEQ_NAME = 'DOMIBUS_CONNECTOR_MESSAGE.ID')
;

delete from DOMIBUS_CONNECTOR_SEQ_STORE
where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID' AND
    EXISTS(select SEQ_NAME
           from DOMIBUS_CONNECTOR_SEQ_STORE
           where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCES.ID') AND
    EXISTS(select SEQ_NAME
           from DOMIBUS_CONNECTOR_SEQ_STORE
           where SEQ_NAME = 'DOMIBUS_CONNECTOR_EVIDENCE.ID')
;

insert into DOMIBUS_CONNECTOR_SERVICE select * from BKP_DC_SERVICE;

-- update timestamps

update (select e.type, e.delivered_gw, e.updated, m.direction_source, m.direction_target, m.confirmed
        from DOMIBUS_CONNECTOR_EVIDENCE e
                 join DOMIBUS_CONNECTOR_MESSAGE m on m.id = e.MESSAGE_ID) x
set x.DELIVERED_GW=x.updated
where x.DELIVERED_GW is null
  and x.TYPE in ('DELIVERY', 'RETRIVAL', 'RELAY_REMMD_ACCEPTANCE')
  and x.updated > to_date('01.06.2020', 'dd.mm.yyyy')
  and x.DIRECTION_SOURCE = 'GATEWAY'
  and x.DIRECTION_TARGET = 'BACKEND'
  and x.confirmed is not null;


update (select e.type, e.delivered_nat, e.updated, m.direction_source, m.direction_target, m.confirmed
        from DOMIBUS_CONNECTOR_EVIDENCE e
                 join DOMIBUS_CONNECTOR_MESSAGE m on m.id = e.MESSAGE_ID) x
set x.DELIVERED_NAT=x.updated
where x.DELIVERED_NAT is null
  and x.TYPE in ('DELIVERY', 'RETRIVAL', 'RELAY_REMMD_ACCEPTANCE')
  and x.updated > to_date('01.06.2020', 'dd.mm.yyyy')
  and x.DIRECTION_SOURCE = 'BACKEND'
  and x.DIRECTION_TARGET = 'GATEWAY'
  and x.confirmed is not null;

-- #################### 4/6 DELETE temporary tables, frees fk names ####################

drop table BKP_DC_SERVICE cascade constraints;

-- #################### 5/6 ADD the constraints ####################

ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT PK_DC_SERVICE PRIMARY KEY (ID);
ALTER TABLE DOMIBUS_CONNECTOR_SERVICE ADD CONSTRAINT FK_SERVICE_PMODE_SET_ID FOREIGN KEY (FK_PMODE_SET) REFERENCES DC_PMODE_SET(ID);
ALTER TABLE DOMIBUS_CONNECTOR_BIGDATA ADD CONSTRAINT FK_DC_BIGDATA_MESSAGE_01 FOREIGN KEY (MESSAGE_ID) REFERENCES DOMIBUS_CONNECTOR_MESSAGE (ID);

-- #################### 6/6 UPDATE Version ####################

update DC_DB_VERSION set TAG='V4.3';