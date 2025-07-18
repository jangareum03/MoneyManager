-- 회원
INSERT INTO MANAGER.TB_MEMBER
(ID, "TYPE", STATUS, "ROLE", USERNAME, PASSWORD, NAME, NICKNAME, EMAIL, CREATED_AT, DELETED_AT)
VALUES('UAt05001', 'C', 'A', 'ROLE_USER', 'test', '$2a$10$O8pimKqAwGNRL5ym0sKnn.0iBJTnqpaACCPgggoJrTHjvjPy/FXka', '홍길동', '홍길동전', 'test@test.com', TIMESTAMP '2025-05-12 02:36:43.000000', NULL);

INSERT INTO MANAGER.TB_MEMBER_INFO
(ID, GENDER, PROFILE, POINT, CONSECUTIVE_DAYS, IMAGE_LIMIT, LOGIN_AT)
VALUES('UAt05001', 'M', NULL, 0, 0, 1, NULL);

-- 로그
INSERT INTO MANAGER.TB_MEMBER_LOGS
(ID, MEMBER_ID, SUCCESS, "TYPE", ITEM, BEFORE_INFO, AFTER_INFO, FAILURE_REASON, UPDATED_AT)
VALUES(1, 'UAt05001', 'Y', 'CREATE', '회원가입', NULL, NULL, NULL, TIMESTAMP '2025-05-12 02:36:44.000000');


-- 카테고리
INSERT INTO TB_CATEGORY VALUES('010000', NULL, '수입');
INSERT INTO TB_CATEGORY VALUES('020000', NULL, '지출');
INSERT INTO TB_CATEGORY VALUES('010100', '010000', '소득');
INSERT INTO TB_CATEGORY VALUES('010101', '010100', '월급');
INSERT INTO TB_CATEGORY VALUES('010102', '010100', '용돈');
INSERT INTO TB_CATEGORY VALUES('010103', '010100', '알바');
INSERT INTO TB_CATEGORY VALUES('010200', '010000', '저축');
INSERT INTO TB_CATEGORY VALUES('010201', '010200', '예금만기');
INSERT INTO TB_CATEGORY VALUES('010202', '010200', '적금만기');
INSERT INTO TB_CATEGORY VALUES('010300', '010000', '차입');
INSERT INTO TB_CATEGORY VALUES('010301', '010300', '빌린돈');
INSERT INTO TB_CATEGORY VALUES('020100', '020000', '식비');
INSERT INTO TB_CATEGORY VALUES('020101', '020100', '식재료');
INSERT INTO TB_CATEGORY VALUES('020102', '020100', '외식');
INSERT INTO TB_CATEGORY VALUES('020103', '020100', '간식');
INSERT INTO TB_CATEGORY VALUES('020104', '020100', '주류');
INSERT INTO TB_CATEGORY VALUES('020105', '020100', '배달');
INSERT INTO TB_CATEGORY VALUES('020106', '020100', '기타');
INSERT INTO TB_CATEGORY VALUES('020200', '020000', '교통');
INSERT INTO TB_CATEGORY VALUES('020201', '020200', '버스비');
INSERT INTO TB_CATEGORY VALUES('020202', '020200', '택시비');
INSERT INTO TB_CATEGORY VALUES('020203', '020200', '지하철');
INSERT INTO TB_CATEGORY VALUES('020204', '020200', '기타');
INSERT INTO TB_CATEGORY VALUES('020300', '020000', '문화생활');
INSERT INTO TB_CATEGORY VALUES('020301', '020300', '영화');
INSERT INTO TB_CATEGORY VALUES('020302', '020300', '뮤지컬');
INSERT INTO TB_CATEGORY VALUES('020303', '020300', '전시회');
INSERT INTO TB_CATEGORY VALUES('020304', '020300', '도서');
INSERT INTO TB_CATEGORY VALUES('020305', '020300', '스포츠');
INSERT INTO TB_CATEGORY VALUES('020306', '020300', '여행');
INSERT INTO TB_CATEGORY VALUES('020307', '020300', '기타');
INSERT INTO TB_CATEGORY VALUES('020400', '020000', '미용·패선');
INSERT INTO TB_CATEGORY VALUES('020401', '020400', '쇼핑');
INSERT INTO TB_CATEGORY VALUES('020402', '020400', '미용실');
INSERT INTO TB_CATEGORY VALUES('020403', '020400', '화장품');
INSERT INTO TB_CATEGORY VALUES('020404', '020400', '기타');
INSERT INTO TB_CATEGORY VALUES('020500', '020000', '교육');
INSERT INTO TB_CATEGORY VALUES('020501', '020500', '학비');
INSERT INTO TB_CATEGORY VALUES('020502', '020500', '학원비');
INSERT INTO TB_CATEGORY VALUES('020503', '020500', '교과서·참고서');
INSERT INTO TB_CATEGORY VALUES('020504', '020500', '인터넷강의');
INSERT INTO TB_CATEGORY VALUES('020505', '020500', '기타');
INSERT INTO TB_CATEGORY VALUES('020600', '020000', '주거');
INSERT INTO TB_CATEGORY VALUES('020601', '020600', '월세·전세');
INSERT INTO TB_CATEGORY VALUES('020602', '020600', '관리비');
INSERT INTO TB_CATEGORY VALUES('020603', '020600', '수리비');
INSERT INTO TB_CATEGORY VALUES('020604', '020600', '가구·집기');
INSERT INTO TB_CATEGORY VALUES('020605', '020600', '기타');
INSERT INTO TB_CATEGORY VALUES('020700', '020000', '통신');
INSERT INTO TB_CATEGORY VALUES('020701', '020700', '인터넷');
INSERT INTO TB_CATEGORY VALUES('020702', '020700', '휴대폰');
INSERT INTO TB_CATEGORY VALUES('020703', '020700', 'TV');
INSERT INTO TB_CATEGORY VALUES('020704', '020700', 'OTT');
INSERT INTO TB_CATEGORY VALUES('020705', '020700', '기타');
INSERT INTO TB_CATEGORY VALUES('020800', '020000', '의료');
INSERT INTO TB_CATEGORY VALUES('020801', '020800', '병원');
INSERT INTO TB_CATEGORY VALUES('020802', '020800', '약국');
INSERT INTO TB_CATEGORY VALUES('020803', '020800', '건강식품');
INSERT INTO TB_CATEGORY VALUES('020804', '020800', '기타');
INSERT INTO TB_CATEGORY VALUES('020900', '020000', '저축');
INSERT INTO TB_CATEGORY VALUES('020901', '020900', '예금');
INSERT INTO TB_CATEGORY VALUES('020902', '020900', '적금');
INSERT INTO TB_CATEGORY VALUES('020903', '020900', '보험');
INSERT INTO TB_CATEGORY VALUES('020904', '020900', '기타');



-- 관리자
INSERT INTO TB_ADMIN VALUES('Aa050001','ROLE_ADMIN', 'admin', '$2a$10$HSZLDVcpfSaVIFn7AeXQMeGlbFBdkpICdap4K0GeNDoxxXzk/9xJ6', '이자바', '운영자', '', 'java@test.com', SYSDATE, 0 );


-- 공지사항
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NE'), 'Aa050001', 'E', 'ACTIVE', '5월엔 포인트가 2배로 UP!! UP!!', '안녕하세요. 5월 한달동안 출석체크를 진행하시면 포인트가 2배로 적립됩니다. 기존 적립된 포인트 5P에서 10P로 적립되며 한달동안 출석을 모두 완료한 회원분들은 추가적으로 1000P를 더 드립니다.', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NE'), 'Aa050001', 'E', 'ACTIVE', '여름맞이 할인 행사', '여름을 맞아 특별 할인 이벤트를 진행합니다! 7월 25일부터 30일까지 모든 상품 20% 할인! 많이 참여해주세요! 🎁.', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NN'), 'Aa050001', 'N', 'ACTIVE', '개인정보처리방침 변경 사항 안내', '개인정보 처리방침이 아래와 같이 변경되었습니다. 변경내용 "결제 및 환불" 목적의 수집 항목 추가 (휴대전화번호)¶¶문의사항은 우측의 Q&A 또는 고객센터(0000-0000)로 연락해주세요.¶감사합니다.', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NN'), 'Aa050001', 'N', 'ACTIVE', '시스템 업데이트 완료', '시스템 업데이트가 정상적으로 완료되었습니다. 모든 기능을 정상적으로 이용하실 수 있습니다. 문제가 있으면 우측의 Q&A 또는 고객센터(0000-0000)로 연락해주세요.', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NN'), 'Aa050001', 'N', 'ACTIVE', '새로운 기능 추가 안내', '이번 업데이트로 새로운 검색 기능이 추가되었습니다.  이제 파일로 한번에 가계부를 등록할 수 있습니다!! 참고파일에 있는 파일로만 가능합니다.', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NN'), 'Aa050001', 'N', 'ACTIVE', '새로운 고객센터 운영 시간', '고객센터 운영 시간이 변경되었습니다! 4월 26일부터는 오전 9시부터 오후 6시까지 운영됩니다. 불편하지 않도록 노력하겠습니다. 감사합니다.', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NN'), 'Aa050001', 'N', 'ACTIVE', '전화 상담 서비스 종료', '전화 상담 서비스가 종료되었습니다. 앞으로는 온라인 채팅과 이메일로만 문의가 가능합니다.. 더 나은 서비스를 제공하기 위해 노력하겠습니다! 💻', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NN'), 'Aa050001', 'N', 'ACTIVE', '서비스 만족도 조사', '저희 서비스를 이용해주셔서 감사합니다! 서비스 개선을 위해 간단한 설문에 참여해주시면 감사하겠습니다. 설문에 응답해주시면 추첨을 통해 10,000포인트를 드려요! 🎁', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NP'), 'Aa050001', 'P', 'ACTIVE', '서버 증설 작업에 따른 서비스 이용 일시 중단 안내', '관리 체계 정비 및 서버 안정화를 위한 서버 증설 작업을 실시합니다. 작업 시간 동안에는 모든 서비스 이용이 전면 중단되오니 이용에 참고 부탁드립니다. 점검일시 => 20XX년 X월 X일(월) 09:00 ~ 12:00 (3시간)', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NP'), 'Aa050001', 'P', 'ACTIVE', '서버 점검 안내', '안녕하세요! 서버 점검이 진행됩니다.¶4월 24일 오후 2시부터 4시까지 약 2시간 동안 서비스가 일시 중단될 예정이니 참고해주세요. 불편을 드려 죄송합니다.🙏 점검일시 => 20XX년 4월 24일(수) 14:00 ~ 16:00 (2시간)', SYSDATE );
INSERT INTO TB_NOTICE(ID, ADMIN_ID, TYPE, STATUS, TITLE, CONTENT, CREATED_DATE) VALUES( GET_NOTICE_ID('notice', 'NP'), 'Aa050001', 'P', 'ACTIVE', '서비스 장애 발생', '현재 서비스에 일시적인 장애가 발생했습니다.¶고객님께 불편을 드려 죄송합니다.  최대한 빨리 복구 작업을 진행 중입니다. 🙇', SYSDATE );


-- 질문
INSERT INTO MANAGER.TB_QA_QUESTION(ID, MEMBER_ID, TITLE, CONTENT, "OPEN", CREATED_DATE, UPDATED_DATE, ANSWER) VALUES(1, 'UAt05001', '첫 게시글', '안뇽하세요. 반갑습니다. 대탈출 2를 보고 있어요!!', 'Y', TIMESTAMP '2025-06-20 22:00:57.000000', NULL, 'N');

-- 답변
INSERT INTO TB_QA_ANSWER(id, question_id, admin_id, content) VALUES( seq_answer.NEXTVAL, 1, 'Aa050001', '안녕하세요! 고객님! 우선 답변이 늦어 죄송합니다.');


-- 테이블 PREFIX
INSERT INTO TB_SEQ_PREFIX (TABLE_NAME,PREFIX,LAST_NUMBER) VALUES ('notice','NN',0);
INSERT INTO TB_SEQ_PREFIX (TABLE_NAME,PREFIX,LAST_NUMBER) VALUES ('notice','NE',0);
INSERT INTO TB_SEQ_PREFIX (TABLE_NAME,PREFIX,LAST_NUMBER) VALUES ('notice','NP',0);

