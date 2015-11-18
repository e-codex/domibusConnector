DROP TABLE DOMIBUS_CONNECTOR_EVIDENCES;
DROP TABLE DOMIBUS_CONNECTOR_MESSAGE_INFO;
DROP TABLE DOMIBUS_CONNECTOR_MSG_ERROR;
DROP TABLE DOMIBUS_CONNECTOR_MESSAGES;
DROP TABLE DOMIBUS_CONNECTOR_SEQ_STORE;
DROP TABLE DOMIBUS_CONNECTOR_PARTY;
DROP TABLE DOMIBUS_CONNECTOR_ACTION;
DROP TABLE DOMIBUS_CONNECTOR_SERVICE;

delete from dcon_qrtz_fired_triggers;
delete from dcon_qrtz_simple_triggers;
delete from dcon_qrtz_simprop_triggers;
delete from dcon_qrtz_cron_triggers;
delete from dcon_qrtz_blob_triggers;
delete from dcon_qrtz_triggers;
delete from dcon_qrtz_job_details;
delete from dcon_qrtz_calendars;
delete from dcon_qrtz_paused_trigger_grps;
delete from dcon_qrtz_locks;
delete from dcon_qrtz_scheduler_state;

drop table dcon_qrtz_calendars;
drop table dcon_qrtz_fired_triggers;
drop table dcon_qrtz_blob_triggers;
drop table dcon_qrtz_cron_triggers;
drop table dcon_qrtz_simple_triggers;
drop table dcon_qrtz_simprop_triggers;
drop table dcon_qrtz_triggers;
drop table dcon_qrtz_job_details;
drop table dcon_qrtz_paused_trigger_grps;
drop table dcon_qrtz_locks;
drop table dcon_qrtz_scheduler_state;