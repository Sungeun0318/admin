# 관리자 일일 운영 리포트 자동 발송

`send_admin_daily_report.py`는 관리자 운영 지표를 백엔드 Admin API에서 수집하고 HTML 이메일 리포트로 발송하는 스크립트다.

## 실행 흐름

```text
관리자 로그인
  -> 소비 인사이트 API 호출
  -> 예산 위험도 API 호출
  -> HTML 리포트 생성
  -> SMTP 이메일 발송
```

## 필요한 환경변수

로컬에서는 admin repo 루트의 `.env` 파일 또는 shell 환경변수로 주입한다. `.env` 파일은 `.gitignore`에 포함되어 있으므로 커밋하지 않는다.

```env
BACKEND_BASE_URL=https://example.com
BACKEND_ADMIN_USERNAME=admin
BACKEND_ADMIN_PASSWORD=admin-password

REPORT_MAIL_FROM=sender@example.com
REPORT_MAIL_TO=receiver1@example.com,receiver2@example.com
REPORT_MAIL_PASSWORD=mail-app-password
SMTP_USERNAME=sender@example.com
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587

ADMIN_WEB_URL=https://admin.example.com
```

## GitHub Secrets 목록

GitHub Actions schedule 실행을 위해 repository secrets에 아래 값을 등록한다.

```text
BACKEND_BASE_URL
BACKEND_ADMIN_USERNAME
BACKEND_ADMIN_PASSWORD
REPORT_MAIL_FROM
REPORT_MAIL_TO
REPORT_MAIL_PASSWORD
SMTP_USERNAME
SMTP_HOST
SMTP_PORT
ADMIN_WEB_URL
```

`SMTP_USERNAME`은 선택값이다. 비워두면 `REPORT_MAIL_FROM`으로 SMTP 로그인한다. 메일 서비스가 발신 주소와 로그인 ID를 다르게 요구하면 `SMTP_USERNAME`에 로그인용 계정을 넣는다.

## 로컬 실행

```bash
python3 scripts/send_admin_daily_report.py
```

성공하면 콘솔에 수집 결과가 출력되고, `REPORT_MAIL_TO`에 지정된 수신자에게 HTML 이메일이 발송된다.

## GitHub Actions 실행

Workflow 파일:

```text
.github/workflows/admin-daily-report.yml
```

자동 실행:

```text
매일 00:00 UTC
한국 시간 매일 09:00
```

주의:

- GitHub Actions schedule은 GitHub runner 상황에 따라 몇 분 이상 지연될 수 있다.
- `workflow_dispatch`가 켜져 있으므로 GitHub Actions 화면에서 수동 실행할 수 있다.
- PR 트리거는 사용하지 않는다.

## 보안 주의사항

- 관리자 비밀번호, SMTP 앱 비밀번호, 토큰을 코드나 로그에 출력하지 않는다.
- Gmail을 사용하는 경우 일반 계정 비밀번호가 아니라 앱 비밀번호를 사용한다.
- `REPORT_MAIL_TO`는 콤마로 여러 수신자를 지정할 수 있다.

## 실패 시 확인할 것

- `BACKEND_BASE_URL`이 백엔드 API 서버를 가리키는지 확인한다.
- `/admin/auth/login`에 사용할 관리자 계정이 운영 DB에 존재하는지 확인한다.
- GitHub Secrets 이름이 workflow의 env 이름과 정확히 일치하는지 확인한다.
- SMTP 앱 비밀번호와 보안 설정을 확인한다.
- `REPORT_MAIL_FROM`과 SMTP 로그인 계정이 다르면 `SMTP_USERNAME`을 별도로 등록한다.
