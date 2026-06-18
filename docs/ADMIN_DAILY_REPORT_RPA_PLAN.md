# 관리자 일일 운영 리포트 자동 발송 RPA 기획

## 1. 목표

관리자가 매번 관리자 페이지에 접속하지 않아도, 매일 정해진 시간에 주요 운영 지표를 이메일로 받아볼 수 있게 한다.

이 기능은 사용자 화면 기능이 아니라 관리자 운영 자동화 기능이다. 반복적으로 확인해야 하는 대시보드 지표를 자동 수집하고, HTML 이메일 리포트로 발송하는 것을 목표로 한다.

## 2. 기능 이름

관리자 일일 운영 리포트 자동 발송

## 3. 핵심 가치

- 관리자가 매일 수동으로 대시보드를 확인하는 반복 업무를 줄인다.
- 서비스 상태를 이메일로 빠르게 확인할 수 있다.
- 예산 위험도, 소비 인사이트, 사용자/방 현황을 한 번에 모니터링할 수 있다.
- 이후 고위험 방 임계치 알림, 주간 리포트, 장애 알림으로 확장할 수 있다.

## 4. 전체 흐름

```text
GitHub Actions schedule
        ↓
Python 또는 Java 실행 스크립트 실행
        ↓
백엔드 Admin API 로그인
        ↓
관리자 JWT 발급
        ↓
대시보드 / 소비 인사이트 / 예산 위험도 API 호출
        ↓
HTML 리포트 생성
        ↓
SMTP 이메일 발송
        ↓
실행 결과 로그 확인
```

## 5. 구현 위치 결정

### A안: admin repo 안에 Python 스크립트 추가

예상 위치:

```text
admin/
  scripts/
    send_admin_daily_report.py
```

장점:

- GitHub Actions schedule로 실행하기 쉽다.
- 이메일 생성과 SMTP 발송을 Python으로 간단하게 처리할 수 있다.
- admin repo 안에 있으므로 관리자 자동화 기능이라는 목적이 명확하다.
- Spring Boot 앱 런타임에 영향을 주지 않는다.

단점:

- Java/Spring 코드와는 별도 실행 스크립트가 된다.

### B안: Spring Boot admin 앱 내부에 스케줄러 추가

예상 위치:

```text
src/main/java/com/beggar/admin/report/
```

장점:

- admin 앱 내부 기능으로 통합된다.
- 기존 `backendWebClient`와 설정을 재사용할 수 있다.

단점:

- 애플리케이션이 켜져 있어야 스케줄러가 동작한다.
- SMTP 설정과 스케줄러가 운영 앱에 직접 들어가 배포/운영 영향 범위가 커진다.

### 최종 추천

1차 구현은 A안으로 진행한다.

이유:

- GitHub Actions schedule은 GitHub 내부 러너에서 실행되므로 로컬 PC나 AWS 서버 스케줄러가 필요 없다.
- 실패해도 admin 웹 서비스 자체에는 영향이 없다.
- 시연과 운영 자동화 목적에 가장 간단하고 안전하다.

## 6. 수집할 데이터

### 6.1 대시보드 요약

후보 API:

```text
GET /admin/dashboard
```

수집 지표:

- 전체 회원 수
- 전체 방 수
- 활성 방 수
- 게시글 수 또는 운영 로그 수

실제 API 응답 구조는 구현 전 코드 확인 후 맞춘다.

### 6.2 소비 인사이트

후보 API:

```text
GET /admin/ai/insights/spending-summary
```

수집 지표:

- 총 지출액
- 평균 영수증 금액
- 예산 초과 방 비율
- 착한가격업소 이용률
- 태그별 추천 클릭 수
- 지역별 소비 TOP 데이터

### 6.3 예산 위험도

후보 API:

```text
GET /admin/ai/predictions/budget-risk?page=0&size=5
```

수집 지표:

- 고위험 방 개수
- 중위험 방 개수
- 저위험 방 개수
- 평균 위험 점수
- 예산 초과 위험 TOP 5 방

## 7. 이메일 구성

### 메일 제목

```text
[거지 우정 수호대] 일일 운영 리포트 - yyyy.MM.dd
```

### 메일 본문 섹션

```text
1. 오늘의 운영 요약
2. 예산 위험도 요약
3. 고위험 방 TOP 5
4. 소비 인사이트
5. 태그별 추천 클릭 수
6. 관리자 페이지 바로가기
```

### HTML 예시 구조

```html
<h1>거지 우정 수호대 일일 운영 리포트</h1>

<h2>오늘의 운영 요약</h2>
<ul>
  <li>전체 회원 수: 1,000명</li>
  <li>전체 방 수: 1,001개</li>
  <li>활성 방 수: 430개</li>
</ul>

<h2>예산 위험도</h2>
<ul>
  <li>고위험 방: 515개</li>
  <li>중위험 방: 100개</li>
  <li>저위험 방: 386개</li>
  <li>평균 위험 점수: 55.2</li>
</ul>

<h2>고위험 방 TOP 5</h2>
<table>
  <thead>
    <tr>
      <th>순위</th>
      <th>방 이름</th>
      <th>위험 점수</th>
      <th>예상 사용률</th>
    </tr>
  </thead>
  <tbody>
    ...
  </tbody>
</table>
```

## 8. 필요한 환경변수

코드에 비밀번호를 직접 작성하지 않는다. 로컬 `.env` 또는 GitHub Secrets로 관리한다.

```env
BACKEND_BASE_URL=https://dl8gtxj2pi4v7.cloudfront.net
BACKEND_ADMIN_USERNAME=admin
BACKEND_ADMIN_PASSWORD=admin1234

REPORT_MAIL_FROM=sender@example.com
REPORT_MAIL_TO=admin@example.com
REPORT_MAIL_PASSWORD=app-password
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587

ADMIN_WEB_URL=http://Begmanager-env.eba-wd8acumg.ap-northeast-2.elasticbeanstalk.com
```

주의:

- Gmail을 쓰는 경우 일반 계정 비밀번호가 아니라 앱 비밀번호를 사용한다.
- GitHub Actions에서는 위 값을 repository secrets로 등록한다.
- 수신자가 여러 명이면 `REPORT_MAIL_TO`를 콤마 구분 문자열로 받을 수 있게 설계한다.

## 9. GitHub Actions schedule 설계

예상 파일:

```text
.github/workflows/admin-daily-report.yml
```

예상 실행 조건:

```yaml
on:
  schedule:
    - cron: "0 0 * * *"
  workflow_dispatch:
```

설명:

- GitHub Actions cron은 UTC 기준이다.
- `0 0 * * *`는 한국 시간 오전 9시다.
- `workflow_dispatch`를 추가해 수동 실행도 가능하게 한다.

## 10. 구현 단계

### Step 1. 기획 문서 확정

- 이 문서를 기준으로 Claude 또는 팀원에게 검토받는다.
- 구현 위치를 `admin/scripts/send_admin_daily_report.py`로 확정한다.

### Step 2. 관리자 로그인 함수 구현

처리:

```text
POST {BACKEND_BASE_URL}/admin/auth/login
body: { "username": "...", "password": "..." }
```

결과:

```text
accessToken 추출
```

실패 처리:

- 401이면 관리자 계정 또는 비밀번호 설정 오류로 명확히 출력한다.
- 응답에 accessToken이 없으면 실행을 중단한다.

### Step 3. 데이터 수집 함수 구현

호출 후보:

```text
GET /admin/ai/insights/spending-summary
GET /admin/ai/predictions/budget-risk?page=0&size=5
```

추가로 대시보드 API가 확인되면 포함한다.

실패 처리:

- 특정 API 실패 시 전체 메일 발송을 막을지, 해당 섹션만 "데이터 수집 실패"로 표시할지 결정한다.
- 1차 구현은 핵심 API 실패 시 실행 중단으로 단순화한다.

### Step 4. HTML 템플릿 생성

처리:

- 수집 데이터를 HTML 문자열로 변환한다.
- 데이터가 비어 있으면 "데이터 없음"으로 표시한다.
- 숫자는 천 단위 콤마를 적용한다.

### Step 5. SMTP 메일 발송

처리:

- `smtplib` 사용
- TLS 연결
- HTML MIME 메일 발송

실패 처리:

- SMTP 인증 실패
- 수신자 누락
- 네트워크 오류

### Step 6. 로컬 실행 검증

명령:

```bash
python scripts/send_admin_daily_report.py
```

확인:

- 관리자 로그인 성공
- API 데이터 수집 성공
- 메일 도착 확인
- HTML이 깨지지 않는지 확인

### Step 7. GitHub Actions schedule 추가

처리:

- workflow 파일 추가
- secrets 등록
- 수동 실행으로 1차 검증
- 정상 동작 확인 후 schedule 유지

## 11. 완료 기준

1. 로컬에서 `python scripts/send_admin_daily_report.py` 실행 시 메일 발송 성공
2. 메일 제목에 실행 날짜 표시
3. 메일 본문에 예산 위험도 요약 표시
4. 고위험 방 TOP 5 표시
5. 소비 인사이트 주요 지표 표시
6. 관리자 페이지 링크 표시
7. GitHub Actions `workflow_dispatch` 수동 실행 성공
8. GitHub Actions schedule 설정 완료
9. 비밀번호와 토큰이 코드에 하드코딩되지 않음

## 12. 리스크와 대응

### 12.1 관리자 인증 실패

원인:

- `BACKEND_ADMIN_USERNAME`
- `BACKEND_ADMIN_PASSWORD`
- 백엔드 admin 계정 seed 누락

대응:

- 로그인 실패 메시지를 명확히 출력한다.
- GitHub Secrets 값 확인 절차를 문서화한다.

### 12.2 메일 발송 실패

원인:

- Gmail 앱 비밀번호 미설정
- SMTP 인증 실패
- 보안 정책 차단

대응:

- Gmail 앱 비밀번호 사용
- `SMTP_HOST`, `SMTP_PORT` 환경변수화
- 실패 로그 출력

### 12.3 API 응답 구조 변경

원인:

- 백엔드 admin API 응답 필드 변경

대응:

- `.get()` 기반으로 안전하게 파싱한다.
- 필수 필드 누락 시 "데이터 없음"으로 표시한다.

### 12.4 GitHub Actions 시간대 착각

원인:

- cron이 UTC 기준

대응:

- 한국 오전 9시는 `0 0 * * *`로 설정한다.
- 문서와 workflow 주석에 명시한다.

## 13. 확장 아이디어

### 고위험 방 임계치 알림

```text
고위험 방 수 >= 100
또는 평균 위험 점수 >= 70
```

조건을 만족하면 일일 리포트와 별도로 즉시 알림 메일을 보낸다.

### 주간 리포트

매주 월요일에 지난 7일간의 소비 패턴, 태그 클릭 추이, 위험도 변화량을 발송한다.

### Slack 또는 Discord 연동

이메일 외에 운영 채널로 리포트를 전송한다.

## 14. Claude 검토용 프롬프트

아래 내용을 Claude에게 그대로 보내면 된다.

```text
나는 Spring Boot + JSP 관리자 웹과 Spring Boot 백엔드, FastAPI AI 서버로 구성된 프로젝트를 만들고 있어.
이번에 관리자 운영 자동화 기능으로 "관리자 일일 운영 리포트 자동 발송 RPA"를 추가하려고 해.

현재 프로젝트 구조:
- beggar-admin: Spring Boot + JSP 관리자 웹
- beggar-backend: 사용자/관리자 API 서버
- beggar-ai: FastAPI 기반 AI/통계 서버
- 관리자 웹은 백엔드 /admin API를 WebClient로 호출함
- 백엔드 /admin API는 관리자 JWT 인증이 필요함
- 백엔드 관리자 로그인 API:
  POST /admin/auth/login
  body: { "username": "...", "password": "..." }
  response: { success: true, data: { accessToken, username, role } }

추가하려는 기능:
관리자가 매번 관리자 페이지에 접속하지 않아도 매일 오전 9시에 주요 운영 지표를 이메일로 받아볼 수 있게 한다.

수집하려는 데이터:
1. 예산 위험도
   - 고위험 방 개수
   - 중위험 방 개수
   - 저위험 방 개수
   - 평균 위험 점수
   - 고위험 방 TOP 5
2. 소비 인사이트
   - 총 지출액
   - 평균 영수증 금액
   - 예산 초과 방 비율
   - 착한가격업소 이용률
   - 태그별 추천 클릭 수
3. 가능하면 대시보드 요약
   - 전체 회원 수
   - 전체 방 수
   - 활성 방 수

구현 방향:
- admin repo 안에 Python 스크립트 추가
  scripts/send_admin_daily_report.py
- GitHub Actions schedule로 매일 오전 9시 실행
- workflow_dispatch로 수동 실행도 가능하게 함
- 스크립트는 백엔드 관리자 로그인 API로 accessToken을 받은 뒤, Admin API들을 호출함
- 수집한 데이터를 HTML 이메일로 만들고 SMTP로 발송함
- 비밀번호와 SMTP 정보는 코드에 넣지 않고 GitHub Secrets 또는 env로 주입함

예상 환경변수:
BACKEND_BASE_URL
BACKEND_ADMIN_USERNAME
BACKEND_ADMIN_PASSWORD
REPORT_MAIL_FROM
REPORT_MAIL_TO
REPORT_MAIL_PASSWORD
SMTP_HOST
SMTP_PORT
ADMIN_WEB_URL

검토받고 싶은 내용:
1. 이 기능을 admin repo 안의 Python script + GitHub Actions schedule로 구현하는 방향이 적절한지
2. Spring Boot Scheduler로 구현하는 것과 비교했을 때 장단점은 무엇인지
3. 이메일 리포트에 포함할 지표가 적절한지
4. 보안상 조심해야 할 점은 무엇인지
5. GitHub Actions schedule로 SMTP 메일을 보내는 방식에서 주의할 점은 무엇인지
6. 1차 구현 범위를 너무 크게 잡은 부분은 없는지
7. 실무적으로 더 좋은 구조나 개선점이 있다면 제안해줘

답변은 구현 가능성, 보안, 유지보수, 실무 관점에서 구체적으로 피드백해줘.
```
