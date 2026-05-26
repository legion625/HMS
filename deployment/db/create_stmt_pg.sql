CREATE TABLE consumption (
    uid varchar NOT NULL,
    type_index integer,
    direction_index integer,
    amount integer,
    description varchar,
    payment_type_index integer,
    date varchar,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE payment (
    uid varchar NOT NULL,
    consumption_uid varchar NOT NULL,
    date varchar,
    amount integer,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE mbr_entity
(
    uid character varying NOT NULL,
    alias character varying,
    type_idx smallint,
    birth_date bigint,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE mbr_guloo_stamp
(
    uid character varying NOT NULL,
    stamp_date bigint,
    desp character varying,
    remark character varying,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE mbr_guloo_stamp_entity_conj
(
    uid character varying NOT NULL,
    stamp_uid character varying,
    entity_uid character varying,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE mbr_guloo_stamp_cate
(
    uid character varying NOT NULL,
    name character varying,
    color character varying,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);


CREATE TABLE mbr_guloo_stamp_cate_conj
(
    uid character varying NOT NULL,
    stamp_uid character varying,
    cate_uid character varying,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);


CREATE TABLE sys_attr (
    uid character varying NOT NULL,
    type_idx smallint,
    attr_key character varying,
    attr_value character varying,
    object_create_time bigint,
    object_update_time bigint,
    PRIMARY KEY (uid)
);

CREATE TABLE system_seq (
    item_id character varying NOT NULL,
    current_num bigint,
    last_num bigint,
    max_num bigint,
    PRIMARY KEY (item_id)
);
