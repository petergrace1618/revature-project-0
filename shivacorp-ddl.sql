-------------------------
-- USERS TABLE DEFINITION
-------------------------

DROP TABLE IF EXISTS shivacorp_schema.transactions;
DROP TABLE IF EXISTS shivacorp_schema.accounts;
DROP TABLE IF EXISTS shivacorp_schema.users;

DROP TYPE IF EXISTS user_type CASCADE;
CREATE TYPE user_type AS ENUM 
	('CUSTOMER', 'EMPLOYEE');

CREATE TABLE shivacorp_schema.users (
	id serial NOT NULL,
	fullname varchar(32) NULL,
	address varchar(64) NULL,
	phone varchar(16) NULL,
	usertype user_type NOT NULL,
	username varchar(32) NOT NULL 
		CONSTRAINT non_empty_username CHECK (username <> ''),
	pwd varchar(16) NOT NULL 
		CONSTRAINT non_empty_password CHECK (pwd <> ''), 
	
	CONSTRAINT users_pk PRIMARY KEY (id)
);

INSERT INTO shivacorp_schema.users
(usertype, username, pwd) VALUES
	('EMPLOYEE', 'admin', 'test'),
	('CUSTOMER', 'buford.clampett', 'derp');

----------------------------
-- ACCOUNTS TABLE DEFINITION
----------------------------

DROP TYPE IF EXISTS status_type CASCADE;
CREATE TYPE status_type AS ENUM 
	('PENDING', 'APPROVED', 'DENIED');

CREATE TABLE shivacorp_schema.accounts (
	acctno serial NOT NULL,
	userid int NOT NULL,
	balance money NOT NULL DEFAULT 0.0,
	status status_type NOT NULL DEFAULT 'PENDING',
	
	CONSTRAINT accounts_pk PRIMARY KEY (acctno),
	CONSTRAINT userid_fk FOREIGN KEY (userid) 
		REFERENCES shivacorp_schema.users (id) 
		ON UPDATE CASCADE ON DELETE CASCADE	
);

ALTER SEQUENCE shivacorp_schema.accounts_acctno_seq 
	RESTART 100001;

INSERT INTO shivacorp_schema.accounts(userid, balance) VALUES (2, 1000);

--------------------------------
-- TRANSACTIONS TABLE DEFINITION
--------------------------------

DROP TYPE IF EXISTS transaction_type CASCADE;
CREATE TYPE transaction_type AS ENUM 
	('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_DEBIT', 'TRANSFER_CREDIT', 'ACCT_APPROVED', 'ACCT_DENIED'); 

CREATE TABLE shivacorp_schema.transactions (
	id serial NOT NULL,
	acctno int NOT NULL, 
	datetime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	transactiontype transaction_type NOT NULL,
	amount money NULL,
	otheracct int NULL,
	
	CONSTRAINT transactions_pk PRIMARY KEY (id),
	CONSTRAINT acctno_fk FOREIGN KEY (acctno) 
		REFERENCES shivacorp_schema.accounts (acctno) 
		ON UPDATE CASCADE ON DELETE CASCADE
);


