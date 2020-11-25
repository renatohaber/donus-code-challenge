create TABLE tab_account (
	id uuid NOT NULL,
	name varchar(255) NOT NULL,
	tax_id varchar(11) NOT NULL,
	balance numeric(5, 2) DEFAULT 0.00,
	dt_creation timestamp with time zone NOT NULL,
    dt_last_update timestamp with time zone NOT NULL,
	CONSTRAINT tab_account_pk PRIMARY KEY (id)
) with (
  OIDS=FALSE
);

create TABLE tab_transaction (
	id uuid NOT NULL,
    account_id uuid NOT NULL,
    type character varying(32) NOT NULL,
    amount numeric(5, 2) NOT NULL,
    dt_creation timestamp with time zone NOT NULL,
    dt_last_update timestamp with time zone NOT NULL,
	CONSTRAINT tab_transaction_pk PRIMARY KEY (id)
) with (
  OIDS=FALSE
);

alter table tab_transaction add CONSTRAINT tab_transaction_fk0 FOREIGN KEY (account_id) REFERENCES tab_account(id);
