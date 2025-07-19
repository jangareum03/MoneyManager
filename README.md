# 돈매니저(Money Manager)
- 개발기간: 2022.10 - 2023.03
- 리펙토링 기간: 2025.03 - 2025.07
- 기술 스택: Java, String Boot, Oracle, SCSS, JavaScript


## 프로젝트 소개
사용자가 수입과 지출을 작성하고 지출 카테고리별로 통계 그래프를 확인할 수 있는 웹 기반 가계부 서비스입니다.
주/월/년별로 소비 통계 그래프를 제공하며, 기간 별로 수입과 지출 금액의 총계를 알 수 있습니다.
> 2022년에 개인 프로젝트로 개발한 가계부 웹 서비스를 2025년에 수정 및 리팩토링 진행했습니다.<br>
> 당시에는 CRUD기능 중심으로 개발하였으나 이번 리팩토링은 보안, 중복 코드 제거 중점으로 개선했습니다.



## 기술 스택
- IDE : IntelliJ
- 백엔드 : Java 11, Spring Boot 2.7, Spring Security
- 프론트엔드 : HTML, Thymeleaf, SCSS, JavaScript, FETCH
- DB : Oracle
- 보안 : HTTPS(Keystore.p12), CSP

## ERD
<img width="1338" height="1140" alt="Image" src="https://github.com/user-attachments/assets/4415220d-e52c-46ac-aa00-5fc4f0017324" />

## 주요기능
- 회원가입 / 로그인
- 가계부 및 문의사항 작성, 조회, 수정, 삭제
- 지출 카테고리 별로 그래프 조회
- 제목, 작성자, 카테고리 등 분류하여 조회 가능

## 기능별 문서 보기
- [회원가입 기능 문서(Notion)](https://www.notion.so/15e0b05cea748094b757c4d8748d3177?source=copy_link)
- [로그인/로그아웃 기능 문서{Notion}](https://www.notion.so/15e0b05cea748091987cc7f2a8b15846?source=copy_link)
- [회원수정 기능 문서(Notion)](https://www.notion.so/15e0b05cea74807ab876dbb8f8954364?source=copy_link)
- [회원탈퇴 기능 문서(Notion)](https://www.notion.so/15e0b05cea7480f8b032fae6cf0ec950?source=copy_link)
> 문서는 현재 회원가입 기능부터 정리 중이며, 지속적으로 다른 기능도 업데이트될 예정입니다.

## 2025 리팩토링 개선 내용
- 반복 코드 제거 및 파일 기능별로 분류
- HTTPS 설정 및 CSP 보안 정책 적용
- SCSS 기반으로 스타일 적용

## 추가 에정 기능
- 목표 소비 설정
- 한번에 수입과 지출을 파일로 등록 가능
- 테마 추가
- 관리자 페이지 추가
