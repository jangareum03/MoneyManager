DROP TABLE tb_prefix;
DROP TABLE inquiry_answer;
DROP TABLE inquiry_question;
DROP TABLE notice;
DROP TABLE admin;

DROP TABLE ledger_image;
DROP TABLE ledger;
DROP TABLE ledger_category;

DROP TABLE member_attendance;
DROP TABLE member_point_log;
DROP TABLE member_log;
DROP TABLE login_log;
DROP TABLE member_token;
DROP TABLE member_info;
DROP TABLE member;


-- 회원 기본정보
CREATE TABLE member (
    id                          VARCHAR2(8)             PRIMARY KEY,
    type                      CHAR(1)                      NOT NULL,
    status                   CHAR(1)                      DEFAULT 'A'                           NOT NULL,
    role                      VARCHAR2(15)            DEFAULT 'ROLE_USER'          NOT NULL,
    username             VARCHAR2(15)           NOT NULL,
    password              VARCHAR2(1000)      NOT NULL,
    name                    VARCHAR2(15)           NOT NULL,
    birthdate             CHAR(8),
    nickname             VARCHAR2(30)          NOT NULL,
    email                    VARCHAR2(100)         NOT NULL,
    created_at          TIMESTAMP               DEFAULT SYSDATE               NOT NULL,
    deleted_at          TIMESTAMP,

    CONSTRAINT CK_member_type            CHECK (type IN ('C', 'K', 'G', 'N')),
    CONSTRAINT CK_member_status         CHECK (status IN ('A', 'L', 'D'))
);

-- 회원 상세정보
CREATE TABLE member_info (
    id                                  VARCHAR2(8),
    gender                         CHAR(1)                      DEFAULT 'N'             NOT NULL,
    profile                         VARCHAR2(1000),
    point                           NUMBER                      DEFAULT 0               NOT NULL,
    consecutive_days      NUMBER                      DEFAULT 0               NOT NULL,
    image_limit                 NUMBER(1)                 DEFAULT 1                 NOT NULL,
    login_at                      TIMESTAMP,
    failure_count             NUMBER(1)                 DEFAULT 0               NOT NULL,

    CONSTRAINT  FK_memberInfo_member                 FOREIGN KEY(id)    REFERENCES member(id)    ON DELETE CASCADE,
    CONSTRAINT CK_memberInfo_gender                  CHECK(gender IN ('N', 'M', 'F'))
);

-- 회원 출석정보
CREATE TABLE member_attendance (
                                   id                                   NUMBER                     PRIMARY KEY,
                                   member_id                    VARCHAR2(8),
                                   attendance_date         DATE                          DEFAULT SYSDATE           NOT NULL,

                                   CONSTRAINT FK_attendance_member     FOREIGN KEY(member_id) REFERENCES member(id)     ON DELETE CASCADE
);

-- 회원 토큰
CREATE TABLE member_token (
                              member_id                       VARCHAR2(8)                 PRIMARY KEY,
                              access_token                  VARCHAR2(1000),
                              refresh_token                VARCHAR2(1000),
                              access_expire_at            TIMESTAMP,
                              refresh_expire_at          TIMESTAMP,
                              last_issued_at                TIMESTAMP                     DEFAULT SYSDATE             NOT NULL,
                              created_at                      TIMESTAMP                    DEFAULT SYSDATE             NOT NULL,
                              updated_at                     TIMESTAMP                    DEFAULT SYSDATE             NOT NULL,

                              CONSTRAINT  FK_member_token      FOREIGN KEY(member_id)  REFERENCES  member(id)       ON DELETE CASCADE
);

 -- 카테고리
 CREATE TABLE ledger_category (
     code                        VARCHAR2(6)         PRIMARY KEY,
     parent_code          VARCHAR2(6),
     name                       VARCHAR2(30)     NOT NULL
 );

-- 가계부
CREATE TABLE ledger (
    id                                    NUMBER                     PRIMARY KEY,
    code                                VARCHAR2(26)         UNIQUE                         NOT NULL,
    member_id                       VARCHAR2(8)          NOT NULL,
    category_id                     VARCHAR2(6)         NOT NULL,
    fix                                    CHAR(1)                       DEFAULT 'N'                 NOT NULL,
    fix_cycle                         CHAR(1),
    transaction_date           DATE             NOT NULL,
    memo                               VARCHAR2(500),
    amount                             NUMBER                      NOT NULL,
    payment_type                CHAR(4)                      DEFAULT 'NONE'           NOT NULL,
    place_name                  VARCHAR2(100),
    road_address               VARCHAR2(300),
    detail_address             VARCHAR2(300),
    created_at                  TIMESTAMP                   DEFAULT SYSDATE         NOT NULL,
    updated_at                 TIMESTAMP,

    CONSTRAINT  FK_ledger_member                 FOREIGN KEY(member_id)    REFERENCES member(id)                        ON DELETE CASCADE,
    CONSTRAINT  FK_ledger_category               FOREIGN KEY(category_id)  REFERENCES ledger_category(code)     ON DELETE CASCADE,
    CONSTRAINT  CK_ledger_fix                        CHECK( fix IN ('N', 'Y') ),
    CONSTRAINT  CK_ledger_fixCycle               CHECK( fix_cycle IN('Y', 'M', 'W') ),
    CONSTRAINT  CK_ledger_paymentType      CHECK(payment_type IN ('NONE', 'CASH', 'CARD', 'BANK'))
);

-- 가계부 이미지
CREATE TABLE ledger_image (
    id                          NUMBER                  PRIMARY KEY,
    ledger_id             NUMBER                  NOT NULL,
    image_path         VARCHAR2(500)       NOT NULL,
    sort_order          NUMBER(1)                NOT NULL,
    created_at          TIMESTAMP             DEFAULT SYSDATE       NOT NULL,
    updated_at          TIMESTAMP,

    CONSTRAINT FK_ledgerImage_ledgerId     FOREIGN KEY(ledger_id)      REFERENCES  ledger(id)                 ON DELETE CASCADE,
    CONSTRAINT UK_ledgerImage_order         UNIQUE(ledger_id, sort_order)
);

-------------------------[ ▼ 시퀀스 ]-------------------------
DROP SEQUENCE login_log_seq;
CREATE SEQUENCE login_log_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE member_log_seq;
CREATE SEQUENCE member_log_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE member_attendance_seq;
CREATE SEQUENCE member_attendance_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE member_point_log_seq;
CREATE SEQUENCE member_point_log_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE ledger_seq;
CREATE SEQUENCE ledger_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE ledger_image_seq;
CREATE SEQUENCE ledger_image_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE question_seq;
CREATE SEQUENCE question_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;

DROP SEQUENCE inquiry_answer_seq;
CREATE SEQUENCE inquiry_answer_seq
    INCREMENT BY 1
    START WITH 1
    NOCYCLE;