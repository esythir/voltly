CREATE SEQUENCE TB_EQUIPMENTS_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
CREATE SEQUENCE TB_SENSORS_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- TABLE: TB_EQUIPMENTS
CREATE TABLE TB_EQUIPMENTS (
    ID              NUMBER(19)   DEFAULT TB_EQUIPMENTS_ID_SEQ.NEXTVAL   PRIMARY KEY,
    USER_ID         NUMBER(19)   NOT NULL,
    NAME            VARCHAR2(120) NOT NULL,
    DESCRIPTION     VARCHAR2(250),
    DAILY_LIMIT_KWH NUMBER(10,3) NOT NULL,
    IS_ACTIVE       NUMBER(1)    DEFAULT 1           NOT NULL,
    CONSTRAINT FK_EQUIPMENTS_USER
        FOREIGN KEY (USER_ID)
        REFERENCES TB_USERS (ID)
);

CREATE INDEX IDX_EQUIPMENTS_USER  ON TB_EQUIPMENTS (USER_ID);

-- TABLE: TB_SENSORS
CREATE TABLE TB_SENSORS (
    ID              NUMBER(19)   DEFAULT TB_SENSORS_ID_SEQ.NEXTVAL  PRIMARY KEY,
    EQUIPMENT_ID    NUMBER(19)   NOT NULL,
    SERIAL_NUMBER   VARCHAR2(100) NOT NULL UNIQUE,
    TYPE            VARCHAR2(80)  NOT NULL,
    CONSTRAINT FK_SENSORS_EQUIPMENT
        FOREIGN KEY (EQUIPMENT_ID)
        REFERENCES TB_EQUIPMENTS (ID)
);

CREATE INDEX IDX_SENSORS_EQUIPMENT ON TB_SENSORS (EQUIPMENT_ID);
