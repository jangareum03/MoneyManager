---- 회원
INSERT INTO member(id, type, status, role, username, password, name, birthdate, nickname, email)
VALUES('UCh11001', 'C', 'A', 'ROLE_USER', 'hong123', 'gil12hong@@', '홍길동', '19800302', '홍길동전', 'hong12@test.com');

INSERT INTO member(id, type, status, role, username, password, name, birthdate, nickname, email, created_at)
VALUES('UCa12001', 'C', 'A', 'ROLE_USER', 'alice', 'pass123!', '김아리', '19920115', '앨리', 'alice@example.com', '2025-12-02 10:07:00');

INSERT INTO member(id, type, status, role, username, password, name, birthdate, nickname, email, created_at)
VALUES('UKb11001', 'K', 'A', 'ROLE_USER', 'bobkakao', 'kakaoPass!', '백반', '19930520', '맛있는 백반집', 'bobkakao@example.com', '2022-11-12 13:21:34');

INSERT INTO member(id, type, status, role, username, password, name, birthdate, nickname, email, created_at)
VALUES('UNn12001', 'N', 'L', 'ROLE_USER', 'nancynaver', 'naver4453@', '리쌍', '19900830', '개리', 'gary@example.com', '2025-12-01 09:43:21');

INSERT INTO member(id, type, status, role, username, password, name, birthdate, nickname, email, created_at)
VALUES('UCc06001', 'C', 'D', 'ROLE_USER', 'cherry', 'cherry123!', '한체리', '19970722', '카드캡터 체리', 'cherry@example.com', '2024-06-05 14:02:04');


-- 회원상세
INSERT INTO member_info(id) VALUES('UCh11001');
INSERT INTO member_info(id, gender, point, consecutive_days, login_at) VALUES('UCa12001', 'F', 10, 1, '2025-11-30 19:12:54');
INSERT INTO member_info(id, gender, point, consecutive_days, login_at) VALUES('UKb11001', 'M', 62000, 120, '2025-12-01 02:32:45');
INSERT INTO member_info(id, consecutive_days, login_at) VALUES('UNn12001', 2, '2025-12-02 08:12:24');

-- 카테고리
INSERT INTO ledger_category VALUES('010000', NULL, '수입');
INSERT INTO ledger_category VALUES('020000', NULL, '지출');
INSERT INTO ledger_category VALUES('010100', '010000', '소득');
INSERT INTO ledger_category VALUES('010101', '010100', '월급');
INSERT INTO ledger_category VALUES('010102', '010100', '용돈');
INSERT INTO ledger_category VALUES('010103', '010100', '알바');
INSERT INTO ledger_category VALUES('010200', '010000', '저축');
INSERT INTO ledger_category VALUES('010201', '010200', '예금만기');
INSERT INTO ledger_category VALUES('010202', '010200', '적금만기');
INSERT INTO ledger_category VALUES('010300', '010000', '차입');
INSERT INTO ledger_category VALUES('010301', '010300', '빌린돈');
INSERT INTO ledger_category VALUES('020100', '020000', '식비');
INSERT INTO ledger_category VALUES('020101', '020100', '식재료');
INSERT INTO ledger_category VALUES('020102', '020100', '외식');
INSERT INTO ledger_category VALUES('020103', '020100', '간식');
INSERT INTO ledger_category VALUES('020104', '020100', '주류');
INSERT INTO ledger_category VALUES('020105', '020100', '배달');
INSERT INTO ledger_category VALUES('020106', '020100', '기타');
INSERT INTO ledger_category VALUES('020200', '020000', '교통');
INSERT INTO ledger_category VALUES('020201', '020200', '버스비');
INSERT INTO ledger_category VALUES('020202', '020200', '택시비');
INSERT INTO ledger_category VALUES('020203', '020200', '지하철');
INSERT INTO ledger_category VALUES('020204', '020200', '기타');
INSERT INTO ledger_category VALUES('020300', '020000', '문화생활');
INSERT INTO ledger_category VALUES('020301', '020300', '영화');
INSERT INTO ledger_category VALUES('020302', '020300', '뮤지컬');
INSERT INTO ledger_category VALUES('020303', '020300', '전시회');
INSERT INTO ledger_category VALUES('020304', '020300', '도서');
INSERT INTO ledger_category VALUES('020305', '020300', '스포츠');
INSERT INTO ledger_category VALUES('020306', '020300', '여행');
INSERT INTO ledger_category VALUES('020307', '020300', '기타');
INSERT INTO ledger_category VALUES('020400', '020000', '미용·패선');
INSERT INTO ledger_category VALUES('020401', '020400', '쇼핑');
INSERT INTO ledger_category VALUES('020402', '020400', '미용실');
INSERT INTO ledger_category VALUES('020403', '020400', '화장품');
INSERT INTO ledger_category VALUES('020404', '020400', '기타');
INSERT INTO ledger_category VALUES('020500', '020000', '교육');
INSERT INTO ledger_category VALUES('020501', '020500', '학비');
INSERT INTO ledger_category VALUES('020502', '020500', '학원비');
INSERT INTO ledger_category VALUES('020503', '020500', '교과서·참고서');
INSERT INTO ledger_category VALUES('020504', '020500', '인터넷강의');
INSERT INTO ledger_category VALUES('020505', '020500', '기타');
INSERT INTO ledger_category VALUES('020600', '020000', '주거');
INSERT INTO ledger_category VALUES('020601', '020600', '월세·전세');
INSERT INTO ledger_category VALUES('020602', '020600', '관리비');
INSERT INTO ledger_category VALUES('020603', '020600', '수리비');
INSERT INTO ledger_category VALUES('020604', '020600', '가구·집기');
INSERT INTO ledger_category VALUES('020605', '020600', '기타');
INSERT INTO ledger_category VALUES('020700', '020000', '통신');
INSERT INTO ledger_category VALUES('020701', '020700', '인터넷');
INSERT INTO ledger_category VALUES('020702', '020700', '휴대폰');
INSERT INTO ledger_category VALUES('020703', '020700', 'TV');
INSERT INTO ledger_category VALUES('020704', '020700', 'OTT');
INSERT INTO ledger_category VALUES('020705', '020700', '기타');
INSERT INTO ledger_category VALUES('020800', '020000', '의료');
INSERT INTO ledger_category VALUES('020801', '020800', '병원');
INSERT INTO ledger_category VALUES('020802', '020800', '약국');
INSERT INTO ledger_category VALUES('020803', '020800', '건강식품');
INSERT INTO ledger_category VALUES('020804', '020800', '기타');
INSERT INTO ledger_category VALUES('020900', '020000', '저축');
INSERT INTO ledger_category VALUES('020901', '020900', '예금');
INSERT INTO ledger_category VALUES('020902', '020900', '적금');
INSERT INTO ledger_category VALUES('020903', '020900', '보험');
INSERT INTO ledger_category VALUES('020904', '020900', '기타');

-- 가계부 테이블
INSERT INTO ledger(num, id, member_id, category_id, transaction_date, amount)
                        VALUES(ledger_seq.NEXTVAL, '01ARZ3NDEKTSV4RRFFQ69G5FAV','UCh11001', '020101', '20251101', 15000);

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, amount)
                        VALUES(ledger_seq.NEXTVAL, '01H5HZ8X9E7EY2XKZCW2FQX16B','UCh11001', '010101', '20251130', 2500000);

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, amount, payment_type, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01HJF8V8W3KDRJDW86XQRZPD96','UCa12001', '020902', '20251001', 20000000, 'BANK', '2025-10-15 13:12:29' );

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, memo, amount, payment_type, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D1','UCa12001', '020102', '20251020', '점심으로 오랜만에 초밥을~', 12000, 'CASH', '2025-10-20 11:49:11' );

INSERT INTO ledger(num, id, member_id, category_id, fix, fix_cycle, transaction_date, amount, payment_type, place_name, road_address, created_at, updated_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D2', 'UCa12001', '020601', 'Y', 'M', '20251105', 500000, 'BANK', '서울 아파트', '서울시 송파구 잠실동 456-78', '2025-11-05 09:21:24', '2025-11-05 09:32:29' );

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, memo, amount, place_name, road_address, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D3','UCa12001', '020301', '20251108', '주토피아2', 15000, 'CGV 강남', '서울시 강남구 테헤란로 321', '2025-11-08 19:24:37' );

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, memo, amount, place_name, road_address, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D4','UCa12001', '020103', '20251117', '핫 초코', 8000, '스타벅스', '서울시 마포구 상암동 12-34', '2025-11-17 14:45:02' );

INSERT INTO ledger(num, id, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D5','UKb11001', '020903', 'Y', 'Y', '20251105', '실손 보험', 120000, '2025-11-05 07:22:16' );

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, memo, amount, place_name, road_address, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D6', 'UKb11001', '020105', '20251123', '도시락', 9500, '한솥', '경기도 성남시 분당구 돌마로 79 썬프라자 1층', '2025-11-23 22:17:54' );

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, memo, amount, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D7','UKb11001', '010101', '20251202', '12월 월급', 2500000, '2025-12-02 07:20:38' );

INSERT INTO ledger(num, id, member_id, category_id, transaction_date, memo, amount, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1E0','UNn12001', '020103', '20251011', 'CU 신상 간식 get!!!', 5500, '2025-10-11 15:39:21' );

INSERT INTO ledger(num, id, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, created_at)
                        VALUES(ledger_seq.NEXTVAL, '01F8Z6YQJ3G5Z7K1V2A9B0C1D8','UNn12001', '020704', 'Y', 'M', '20251201', '넷플릭스', 9900, 'CARD', '2025-12-01 13:56:20' );