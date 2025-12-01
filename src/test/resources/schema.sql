-------------------------[ ▼ 테이블 삭제 ]----------------------
DROP TABLE tb_prefix;
DROP TABLE inquiry_answer;
DROP TABLE inquiry_question;
DROP TABLE notice;
DROP TABLE admin;

DROP TABLE ledger;
DROP TABLE ledger_category;

DROP TABLE member_attendance;
DROP TABLE member_point_log;
DROP TABLE member_log;
DROP TABLE login_log;
DROP TABLE member_token;
DROP TABLE member_info;
DROP TABLE member;


-------------------------[ ▼ 테이블 ]-------------------------
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

    CONSTRAINT  FK_memberInfo_member                 FOREIGN KEY(id)    REFERENCES member(id)    ON DELETE CASCADE,
    CONSTRAINT CK_memberInfo_gender                  CHECK(gender IN ('N', 'M', 'F'))
);

-- 로그인 로그
CREATE TABLE login_log (
    id                              NUMBER                      NOT NULL                    PRIMARY KEY,
    member_id               VARCHAR2(8),
    username                 VARCHAR2(15)            NOT NULL,
    login_type               VARCHAR2(15),
    success                     CHAR(1),
    browser                    VARCHAR2(8)            NOT NULL,
    ip                              CHAR(15)                    NOT NULL,
    failure_reason         VARCHAR2(100),
    access_at                 TIMESTAMP              DEFAULT SYSDATE        NOT NULL,

    CONSTRAINT  FK_loginLogs_member      FOREIGN KEY(member_id)  REFERENCES member(id)    ON DELETE  CASCADE,
    CONSTRAINT  CK_loginLogs_type           CHECK(login_type IN ('LOGIN', 'LOGOUT')),
    CONSTRAINT  CK_loginLogs_success      CHECK(success IN ('Y', 'N'))
);

-- 수정내역
CREATE TABLE member_log (
    id                              NUMBER                       PRIMARY KEY,
    member_id               VARCHAR2(8),
    success                     CHAR(1)                        NOT NULL,
    type                          VARCHAR2(15)             NOT NULL,
    item                          VARCHAR2(30),
    before_info             VARCHAR2(100),
    after_info                VARCHAR2(200),
    failure_reason         VARCHAR2(200),
    updated_at              TIMESTAMP               DEFAULT SYSDATE             NOT NULL,

    CONSTRAINT  FK_memberLogs_member        FOREIGN KEY(member_id)  REFERENCES member(id)    ON DELETE   CASCADE,
    CONSTRAINT  CK_memberLogs_success        CHECK(success IN ('Y', 'N')),
    CONSTRAINT  CK_memberLogs_type             CHECK(type IN ('CREATE', 'UPDATE', 'DELETE'))
);

-- 회원 출석정보
CREATE TABLE member_attendance (
    id                                   NUMBER                     PRIMARY KEY,
    member_id                    VARCHAR2(8),
    attendance_date         DATE                          DEFAULT SYSDATE           NOT NULL,

    CONSTRAINT FK_attendance_member     FOREIGN KEY(member_id) REFERENCES member(id)     ON DELETE CASCADE
);

-- 회원 포인트 내역
CREATE TABLE member_point_log (
    id                              NUMBER                      PRIMARY KEY,
    member_id               VARCHAR2(8),
    type                         VARCHAR2(10),
    points                      NUMBER                      DEFAULT 0                   NOT NULL,
    reason                     VARCHAR2(200),
    balance_points      NUMBER                      DEFAULT 0                    NOT NULL,
    used_at                  TIMESTAMP                DEFAULT SYSDATE       NOT NULL,

    CONSTRAINT  FK_pointLogs_member     FOREIGN KEY(member_id)  REFERENCES member(id)    ON DELETE CASCADE,
    CONSTRAINT  CK_pointLogs_type          CHECK( type IN ('EARM', 'USE') )
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
    id                                      VARCHAR2(26)                      PRIMARY KEY,
    number                             NUMBER                                UNIQUE,
    member_id                       VARCHAR2(8),
    category_id                     VARCHAR2(6),
    fix                                    CHAR(1)                       DEFAULT 'N'                 NOT NULL,
    fix_cycle                         CHAR(1),
    transaction_date           VARCHAR2(8)             NOT NULL,
    memo                               VARCHAR2(500),
    price                               NUMBER                      NOT NULL,
    payment_type                CHAR(4)                      DEFAULT 'NONE'           NOT NULL,
    image1                            VARCHAR2(255),
    image2                           VARCHAR2(255),
    image3                           VARCHAR2(255),
    place_name                  VARCHAR2(100),
    road_address               VARCHAR2(300),
    address                         VARCHAR2(300),
    created_at                  TIMESTAMP                   DEFAULT SYSDATE         NOT NULL,
    updated_at                 TIMESTAMP,

    CONSTRAINT  FK_ledger_member                 FOREIGN KEY(member_id)    REFERENCES member(id)                        ON DELETE CASCADE,
    CONSTRAINT  FK_ledger_category               FOREIGN KEY(category_id)  REFERENCES ledger_category(code)     ON DELETE CASCADE,
    CONSTRAINT  CK_ledger_fix                        CHECK( fix IN ('N', 'Y') ),
    CONSTRAINT  CK_ledger_fixCycle               CHECK( fix_cycle IN('Y', 'M', 'W') ),
    CONSTRAINT  CK_ledger_paymentType      CHECK(payment_type IN ('NONE', 'CASH', 'CARD', 'BANK'))
);

-- 관리자
CREATE TABLE admin (
    id                                  VARCHAR2(8)                 NOT NULL                        PRIMARY KEY,
    role                              VARCHAR2(15)                NOT NULL,
    adminname                  VARCHAR2(15)                NOT NULL,
    password                     VARCHAR2(1000)           NOT NULL,
    name                           VARCHAR2(30)               NOT NULL,
    nickname                    VARCHAR2(30)               NOT NULL,
    phone                         VARCHAR2(15),
    email                           VARCHAR2(100)             NOT NULL,
    created_at                TIMESTAMP                    DEFAULT SYSDATE            NOT NULL,
    answer_count           NUMBER                          DEFAULT 0                         NOT NULL
);

-- 공지사항
CREATE TABLE notice (
    id                              VARCHAR2(8)                 NOT NULL                        PRIMARY KEY,
    admin_id                  VARCHAR2(8),
    type                         CHAR(1)                           NOT NULL,
    status                      VARCHAR2(20)               DEFAULT 'ACTIVE'             NOT NULL,
    title                         VARCHAR2(100)              NOT NULL,
    content                   VARCHAR2(1000)           NOT NULL,
    created_date          DATE                              DEFAULT SYSDATE            NOT NULL,
    updated_date         DATE,
    view_count             NUMBER                          DEFAULT 0                        NOT NULL,
    rank                        NUMBER,

    CONSTRAINT      FK_notice_admin             FOREIGN KEY(admin_id)   REFERENCES admin(id)     ON DELETE SET NULL,
    CONSTRAINT      CK_notice_type               CHECK( type IN('N', 'E', 'P') ),
    CONSTRAINT      CK_notice_status            CHECK( status IN ('ACTIVE', 'INACTIVE') )
);

-- 문의사항 질문
CREATE TABLE inquiry_question (
    id                                  NUMBER                              NOT NULL                  PRIMARY KEY,
    member_id                   VARCHAR2(8)                     NOT NULL,
    title                              VARCHAR2(100)                 NOT NULL,
    content                       VARCHAR2(1000)               NOT NULL,
    open                            CHAR(1)                              DEFAULT 'Y'                  NOT NULL,
    answer                        CHAR(1)                               DEFAULT 'N'                 NOT NULL,
    created_date             TIMESTAMP                        DEFAULT SYSDATE       NOT NULL,
    updated_date            TIMESTAMP,

    CONSTRAINT  FK_question_member      FOREIGN KEY(member_id)      REFERENCES member(id)    ON DELETE SET NULL,
    CONSTRAINT  CK_question_open          CHECK( open IN ('Y', 'N') ),
    CONSTRAINT  CK_question_answer      CHECK( answer IN ('Y', 'N') )
);

-- 문의사항 답변
CREATE TABLE inquiry_answer (
    id                                  NUMBER                          PRIMARY KEY,
    question_id                 NUMBER                          NOT NULL,
    admin_id                     VARCHAR2(8)                  NOT NULL,
    content                       VARCHAR2(1000),
    created_date             TIMESTAMP                    DEFAULT SYSDATE           NOT NULL,
    updated_date            TIMESTAMP,

    CONSTRAINT FK_answer_question     FOREIGN KEY(question_id)       REFERENCES inquiry_question(id)       ON DELETE SET NULL,
    CONSTRAINT FK_answer_admin         FOREIGN KEY(admin_id)            REFERENCES admin(id)                         ON DELETE SET NULL
);

-- 테이블 prefix
CREATE TABLE tb_prefix (
    table_name           VARCHAR2(100)          NOT NULL,
    prefix                     VARCHAR2(100)         NOT NULL,
    last_number          NUMBER                      DEFAULT 0              NOT NULL
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