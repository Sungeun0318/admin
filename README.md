# Beggar Admin

거지 우정 수호대 관리자용 Spring Boot + JSP 프로젝트다.

## 현재 상태

- 독립 관리자 앱으로 `admin/` repo에 남아있다.
- 기본 포트는 8081이다.
- JSP view와 관리자 전용 service/repository/controller 구조를 가진다.
- 다만 2026-06-13 기준으로 관리자 기능을 `backend` 안으로 통합하는 작업도 진행 중이다.
- `backend/main`에는 `controller/admin`, `service/admin`, `dto/admin`, `repository/admin` 코드가 머지되어 컴파일까지 통과했다.

즉 현재는 아래 두 구조가 공존한다.

| 위치 | 상태 |
|---|---|
| `admin/` | 독립 JSP 관리자 앱 |
| `backend/src/main/java/com/beggar/api/*/admin` | 백엔드 통합 관리자 코드 |

최종 운영 구조를 둘 중 하나로 정리해야 한다.

## 실행

```bash
cd admin
cp application-example.properties application-local.properties
SPRING_CONFIG_LOCATION=optional:file:./application-local.properties \
API_EXTERNAL_SERVER_URL=http://localhost:8080 \
BACKEND_ADMIN_USERNAME=admin \
BACKEND_ADMIN_PASSWORD=admin1234 \
DB_PASSWORD=로컬DB비밀번호 \
./gradlew bootRun
```

`BACKEND_ADMIN_USERNAME`, `BACKEND_ADMIN_PASSWORD`는 JSP 로그인 계정이 아니라
백엔드 `/admin/auth/login` 호출용 서비스 계정이다. 운영 환경에서는 반드시 환경 변수나 배포 설정으로 주입한다.

`application-local.properties`는 git에 올리지 않는 로컬 설정 파일이다.
필요하면 `application-example.properties`를 참고해서 로컬 파일을 만들면 된다.

기본 주소:

```text
http://localhost:8081
```

## 주요 기능

- 관리자 로그인/로그아웃
- 대시보드
- 회원 목록/상세
- 방 목록/상세
- 방 종료/삭제 처리
- 커뮤니티 게시글 관리
- 댓글 관리
- 채팅 관리
- 영수증 목록/상세/삭제
- 운영 로그 조회

## 주요 경로

| Path | 설명 |
|---|---|
| `/login` | 관리자 로그인 |
| `/`, `/admin` | 대시보드 |
| `/admin/users` | 회원 목록 |
| `/admin/users/{userNo}` | 회원 상세 |
| `/admin/rooms` | 방 목록 |
| `/admin/rooms/{roomNo}` | 방 상세 |
| `/admin/community/posts` | 게시글 목록 |
| `/admin/community/posts/{postId}` | 게시글 상세 |
| `/admin/community/comments` | 댓글 목록 |
| `/admin/chats` | 채팅 목록 |
| `/admin/receipts` | 영수증 목록 |
| `/admin/receipts/{receiptId}` | 영수증 상세 |
| `/admin/logs` | 운영 로그 목록 |
| `/admin/logs/{logId}` | 운영 로그 상세 |

## 구조

```text
admin/
├── src/main/java/com/beggar/admin/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── security/
│   └── service/
├── src/main/resources/
│   └── static/css/admin.css
├── application-example.properties
├── application-local.properties
└── src/main/webapp/WEB-INF/views/
    ├── auth/
    ├── chats/
    ├── community/
    ├── layout/
    ├── logs/
    ├── receipts/
    ├── rooms/
    └── users/
```

## 검증

```bash
./gradlew build
```

## AWS Elastic Beanstalk 배포

GitHub Actions는 `aws-admin` 브랜치에 push 되거나 수동 실행(`workflow_dispatch`)하면
`build/libs/admin-app.jar`를 Elastic Beanstalk에 배포한다.

GitHub repository secrets:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION=ap-northeast-2
ADMIN_EB_APPLICATION_NAME
ADMIN_EB_ENVIRONMENT_NAME
```

Elastic Beanstalk 환경 변수:

```text
SPRING_PROFILES_ACTIVE=aws
SERVER_PORT=5000
DB_HOST=RDS-ENDPOINT
DB_PORT=3306
DB_NAME=beggar
DB_USERNAME=...
DB_PASSWORD=...
API_EXTERNAL_SERVER_URL=https://백엔드주소
BACKEND_ADMIN_USERNAME=admin
BACKEND_ADMIN_PASSWORD=admin1234
```

`BACKEND_ADMIN_PASSWORD`는 백엔드 `admin_accounts` 테이블의 서비스 계정 비밀번호다.
JSP 화면 로그인은 관리자 웹 DB의 `admin_account` 테이블을 사용하므로, 운영 DB에도 계정 seed가 필요하다.

## 주의점

- 관리자 계정은 DB의 `admin_account` 테이블 기준이다.
- `backend` 통합 관리자 코드와 이 독립 앱의 기능이 중복된다.
- 통합 완료 후에는 배포/운영 경로, 인증 방식, view 사용 여부를 정해야 한다.
