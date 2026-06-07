# Beggar Admin

관리자 전용 Spring Boot + JSP 서비스다.
사용자 서비스와 분리해서 8081 포트에서 실행한다.

## 실행

```bash
DB_PASSWORD=로컬DB비밀번호 ./gradlew bootRun
```

기본 주소:

```text
http://localhost:8081
```

## 구현 기능

- 관리자 로그인/로그아웃
- 대시보드
- 회원 조회
- 방 조회, 상세, 종료, 삭제 처리
- 커뮤니티 게시글 관리
- 댓글 관리
- 채팅 관리
- 영수증 관리
- 착한가격업소 관리
- 관리자 계정 관리
- 운영 로그 조회

## 검증

관리자 계정:

```text
DB의 admin_account 테이블에 등록된 계정을 사용한다.
```

빌드:

```bash
./gradlew build
```
