# STEP 15 통합 검증 결과

검증 일시: 2026-06-06 23:05 KST

## 1. 검증 대상

```text
backend   사용자 API 서버, 8080
webfront  React 사용자 프론트, 5173
admin     관리자 JSP 서버, 8081
```

## 2. 빌드 검증

| 프로젝트 | 명령어 | 결과 |
| --- | --- | --- |
| backend | `./gradlew build` | 통과 |
| webfront | `npm run build` | 통과 |
| admin | `./gradlew build` | 통과 |

## 3. 실행 검증

### 3.1 backend

명령어:

```bash
./gradlew bootRun
```

결과:

```text
Tomcat started on port 8080
```

HTTP 확인:

```http
GET http://localhost:8080/rooms/1
```

응답:

```text
HTTP/1.1 200
```

확인 내용:

- 사용자 API 서버가 8080에서 기동됨.
- DB 연결 성공.
- `/rooms/1` API가 JSON 응답을 반환함.

### 3.2 webfront

명령어:

```bash
npm run dev -- --host 127.0.0.1
```

결과:

```text
VITE ready
Local: http://127.0.0.1:5173/
```

HTTP 확인:

```http
GET http://127.0.0.1:5173/
```

응답:

```text
HTTP/1.1 200 OK
```

확인 내용:

- React/Vite 개발 서버가 5173에서 기동됨.
- HTML 엔트리 문서가 정상 반환됨.

### 3.3 admin

명령어:

```bash
./gradlew bootRun
```

결과:

```text
BUILD FAILED
Access denied for user 'root'@'localhost' (using password: NO)
```

원인:

- 현재 셸에 `DB_PASSWORD` 등 DB 접속 환경변수가 설정되어 있지 않음.
- `admin/src/main/resources/application.properties` 기본값이 `root` 계정과 빈 비밀번호를 사용함.
- 로컬 MySQL이 root 무비밀번호 접속을 허용하지 않아 `AdminSchemaInitializer` 실행 단계에서 실패함.

필요 조건:

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=beggar
DB_USERNAME=root
DB_PASSWORD=로컬DB비밀번호
ADMIN_PASSWORD=관리자초기비밀번호
./gradlew bootRun
```

AWS DB로 검증할 경우에는 `DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`를 AWS DB 값으로 설정해야 함.

## 4. 포트 정리

검증 후 실행 중이던 서버를 종료했고, 다음 포트가 비어 있는 것을 확인함.

```text
8080
8081
5173
```

## 5. 결과 요약

통과:

- backend 빌드
- backend 8080 실행
- backend API 응답
- webfront 빌드
- webfront 5173 실행
- webfront HTML 응답
- admin 빌드

보류:

- admin 8081 실행
- 관리자 로그인 화면 검증
- 관리자 조회 화면 검증
- 관리자 변경 사항이 사용자 API에 반영되는지 검증
- 권한 없는 접근 차단 검증

보류 사유:

- admin 실행에 필요한 DB 접속 환경변수가 현재 셸에 없음.

## 6. 다음 검증 방법

DB 환경변수를 설정한 뒤 아래 순서로 재검증한다.

```bash
cd /Users/sungeun/Developer/Beggar/backend
./gradlew bootRun
```

```bash
cd /Users/sungeun/Developer/Beggar/webfront
npm run dev -- --host 127.0.0.1
```

```bash
cd /Users/sungeun/Developer/Beggar/admin
DB_PASSWORD=로컬DB비밀번호 ADMIN_PASSWORD=관리자초기비밀번호 ./gradlew bootRun
```

확인 주소:

```text
사용자 API: http://localhost:8080
사용자 프론트: http://127.0.0.1:5173
관리자 페이지: http://localhost:8081
```

관리자 검증 항목:

- `/login` 접속.
- SUPER_ADMIN 로그인.
- `/admin` 대시보드 조회.
- `/admin/rooms` 방 목록 조회.
- `/admin/receipts` 영수증 목록 조회.
- `/admin/recommendations/stores` 착한가격업소 관리.
- `/admin/admins`는 SUPER_ADMIN만 접근 가능.
- `/admin/logs` 운영 로그 조회.
