DROP TABLE ECODEX_EVIDENCES;
DROP TABLE ECODEX_MESSAGE_INFO;
DROP TABLE ECODEX_MESSAGES;
DROP TABLE ECODEX_SEQ_STORE;
DROP TABLE ECODEX_PARTY;
DROP TABLE ECODEX_ACTION;
DROP TABLE ECODEX_SERVICE;

CREATE TABLE ECODEX_MESSAGES (
	ID BIGINT NOT NULL,
	EBMS_MESSAGE_ID VARCHAR(255) UNIQUE,
	NAT_MESSAGE_ID VARCHAR(255) UNIQUE,
	CONVERSATION_ID VARCHAR(255),
	DIRECTION VARCHAR(10),
	HASH_VALUE VARCHAR(1000),
	CONFIRMED DATETIME,
	REJECTED DATETIME,
	DELIVERED_NAT DATETIME,
	DELIVERED_GW DATETIME,
	UPDATED TIMESTAMP,
	PRIMARY KEY (ID)
);

CREATE TABLE ECODEX_EVIDENCES (
	ID BIGINT NOT NULL,
	MESSAGE_ID BIGINT NOT NULL,
	TYPE VARCHAR(255),
	EVIDENCE TEXT,
	DELIVERED_NAT DATETIME,
	DELIVERED_GW DATETIME,
	UPDATED TIMESTAMP,
	PRIMARY KEY (ID),
	FOREIGN KEY (MESSAGE_ID) REFERENCES ECODEX_MESSAGES (ID)
);

CREATE TABLE ECODEX_SEQ_STORE (
	SEQ_NAME VARCHAR(255) NOT NULL,
	SEQ_VALUE BIGINT NOT NULL,
	PRIMARY KEY(SEQ_NAME)
);

INSERT INTO ECODEX_SEQ_STORE VALUES ('ECODEX_MESSAGES.ID', 0);
INSERT INTO ECODEX_SEQ_STORE VALUES ('ECODEX_EVIDENCES.ID', 0);
INSERT INTO ECODEX_SEQ_STORE VALUES ('ECODEX_MESSAGE_INFO.ID', 0);

CREATE TABLE ECODEX_PARTY (
	PARTY_ID VARCHAR(50) NOT NULL,
	ROLE VARCHAR(50) NOT NULL,
	PARTY_ID_TYPE VARCHAR(50),
	PRIMARY KEY (PARTY_ID, ROLE)
);

INSERT INTO ECODEX_PARTY VALUES ('AT', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('DE', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('EE', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('ES', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('EU', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('IT', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('GR', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('NL', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('PL', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');
INSERT INTO ECODEX_PARTY VALUES ('CZ', 'GW', 'urn:oasis:names:tc:ebcore:partyid-type:iso3166-1');

CREATE TABLE ECODEX_ACTION (
	ECDX_ACTION VARCHAR(50) NOT NULL,
	PDF_REQUIRED SMALLINT NOT NULL DEFAULT 1,
	PRIMARY KEY (ECDX_ACTION)
);

INSERT INTO ECODEX_ACTION VALUES ('Form_A', 1);
INSERT INTO ECODEX_ACTION VALUES ('Form_B', 1);
INSERT INTO ECODEX_ACTION VALUES ('Form_C', 1);
INSERT INTO ECODEX_ACTION VALUES ('Form_D', 1);
INSERT INTO ECODEX_ACTION VALUES ('Form_E', 1);
INSERT INTO ECODEX_ACTION VALUES ('Form_F', 1);
INSERT INTO ECODEX_ACTION VALUES ('Form_G', 1);
INSERT INTO ECODEX_ACTION VALUES ('FreeFormLetter', 1);
INSERT INTO ECODEX_ACTION VALUES ('SubmissionAcceptanceRejection', 0);
INSERT INTO ECODEX_ACTION VALUES ('RelayREMMDAcceptanceRejection', 0);
INSERT INTO ECODEX_ACTION VALUES ('RelayREMMDFailure', 0);
INSERT INTO ECODEX_ACTION VALUES ('DeliveryNonDeliveryToRecipient', 0);
INSERT INTO ECODEX_ACTION VALUES ('RetrievalNonRetrievalToRecipient', 0);

CREATE TABLE ECODEX_SERVICE (
	ECDX_SERVICE VARCHAR(50) NOT NULL,
	PRIMARY KEY (ECDX_SERVICE)
);

INSERT INTO ECODEX_SERVICE VALUES ('EPO');

CREATE TABLE ECODEX_MESSAGE_INFO (
	ID  BIGINT NOT NULL,
	MESSAGE_ID  BIGINT UNIQUE NOT NULL,
	FROM_PARTY_ID VARCHAR(50),
	FROM_PARTY_ROLE VARCHAR(50),
	TO_PARTY_ID VARCHAR(50),
	TO_PARTY_ROLE VARCHAR(50),
	ORIGINAL_SENDER VARCHAR(50),
	FINAL_RECIPIENT VARCHAR(50),
	ECDX_SERVICE VARCHAR(50),
	ECDX_ACTION VARCHAR(50),
	CREATED DATETIME NOT NULL,
	UPDATED DATETIME NOT NULL,
	PRIMARY KEY (ID),
	FOREIGN KEY (MESSAGE_ID) REFERENCES ECODEX_MESSAGES (ID),
	FOREIGN KEY (FROM_PARTY_ID, FROM_PARTY_ROLE) REFERENCES ECODEX_PARTY (PARTY_ID, ROLE),
	FOREIGN KEY (TO_PARTY_ID, TO_PARTY_ROLE) REFERENCES ECODEX_PARTY (PARTY_ID, ROLE),
	FOREIGN KEY (ECDX_SERVICE) REFERENCES ECODEX_SERVICE (ECDX_SERVICE),
	FOREIGN KEY (ECDX_ACTION) REFERENCES ECODEX_ACTION (ECDX_ACTION)
);



commit;