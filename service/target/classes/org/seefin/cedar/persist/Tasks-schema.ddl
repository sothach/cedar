create table TASK (
  ID          varchar(32)   not null,
  DESCRIPTION varchar(2048) not null,
  CREATE_TIME bigint        not null,
  OWNER_ID    varchar(10)   not null,
  STATE       varchar(9)    not null,
  PRIMARY KEY (ID)
);

create table PARTY (
  ID       varchar(32)  not null,
  USERNAME varchar(256) not null,
  PASSWORD varchar(64)  not null,
  LOCALE   varchar(8)   not null,
  PRIMARY KEY (ID)
);


