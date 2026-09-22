# 돈매니저(Money Mananger)

| 항목 | 내용 |
|------|------|
| 참여인원 | 개인 |
| 개발기간 | 2022.10 - 2023.03 |
| 리팩토링 기간 | 2026.03 - 현재 |
<br>

## 프로젝트 소개
해당 프로젝트는 개인의 돈 흐름을 구글 차트를 활용하여 수입과 지출 데이털르 시각화하고,
사용자가 쉽게 돈 관리릃 할 수 있도록 지원하는 웹 서비스입니다.
<br><br>


## 주요 기능
- 회원가입(이메일 인증 적용)
- 로그인 / 로그아웃
- 가계부 등록, 수정, 검색, 삭제
- 문의사항 등록, 수정, 조회, 삭제
<br>

## 개선한 점
| 기존 | 변경 |
|------|------|
| 세션 기반 인증 | JWT 기반 사용자 인증 |
| 계층 패키지 구조 | 도메인 패키지 구조 |
| 테스트 코드 없음 | 테스트 코드 추가 |
<br>

## 사용 기술
| 구분 | 기술 |
|------|------|
| Language | Java 11 |
| Framework | Spring Boot 2.7 |
| DB | Oracle Database 19c |
| Build Tool | Gradle |
| Application Server | Apache Tomcat 9 |
<br>


## 프로젝트 구조
<p align="center">
  <img width="282" height="800" alt="image" src="https://github.com/user-attachments/assets/4480a1da-ed45-462e-9371-fe6f570a431d" />
</p>
<br>


## ERD
<p align="center">
  <img width="1338" height="1140" alt="Image" src="https://github.com/user-attachments/assets/6dd7333e-6772-4d2c-9703-78eecb693722" />  
</p>
<br>


## 기술문서
- [회원 관련 기능 문서(Notion)](https://www.notion.so/15e0b05cea748155be03d131613b0005?source=copy_link)
- [가계부 관련 기능 문서(Notion)](https://www.notion.so/1e90b05cea7480a7b705fd1f135c4ccc?source=copy_link)
- [서브 서비스 관련 기능 문서(Notion)](https://www.notion.so/1e90b05cea7480e58db6ef5cdf87eb66?source=copy_link)
<br>


## 이슈 관리(JIRA)
### 전체 목록
<p align="center">
  <img width="1038" height="656" alt="image" src="https://github.com/user-attachments/assets/af05ee40-75e7-4977-b345-7d2f392131d9" />
</p>
<br>

### 설계 상세
<p align="center">
  <img width="483" height="435" alt="image" src="https://github.com/user-attachments/assets/d448fc81-8fd7-4060-bbef-2fe7babcab11" />
</p>

<p align="center">
  <img width="878" height="839" alt="image" src="https://github.com/user-attachments/assets/62574bb4-d006-45ee-8e17-d5772213534e" />
</p>
<br>

### 기능별 상세
<p align="center">
  <img width="752" height="654" alt="image" src="https://github.com/user-attachments/assets/8dd4b748-be0c-4369-a580-51f2aa3262e3" />
</p>
<p align="center">
  <img width="730" height="373" alt="image" src="https://github.com/user-attachments/assets/8be3fd6a-3fc9-4b47-bf6c-5f0b7162c989" />
</p>
<br>


## 추가 기능(예정)
- [ ] 가계부 통계 기준일 변경
- [ ] 목표 소비 설정 
