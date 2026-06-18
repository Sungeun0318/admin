#!/usr/bin/env python3
"""Send admin daily report email."""

from __future__ import annotations

import html
import os
import smtplib
import sys
from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from pathlib import Path
from typing import Any
from zoneinfo import ZoneInfo

import requests


LOGIN_PATH = "/admin/auth/login"
SPENDING_SUMMARY_PATH = "/admin/ai/insights/spending-summary"
BUDGET_RISK_PATH = "/admin/ai/predictions/budget-risk"
REQUEST_TIMEOUT_SECONDS = 20
KST = ZoneInfo("Asia/Seoul")


class ReportError(RuntimeError):
    pass


def load_local_env() -> None:
    env_path = Path(__file__).resolve().parents[1] / ".env"
    if not env_path.exists():
        return

    for raw_line in env_path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        key = key.strip()
        value = value.strip().strip('"').strip("'")
        os.environ.setdefault(key, value)


def required_env(name: str) -> str:
    value = os.environ.get(name)
    if value is None or value.strip() == "":
        raise ReportError(f"필수 환경변수가 없습니다: {name}")
    return value.strip()


def require_report_env() -> None:
    for name in (
        "BACKEND_BASE_URL",
        "BACKEND_ADMIN_USERNAME",
        "BACKEND_ADMIN_PASSWORD",
        "REPORT_MAIL_FROM",
        "REPORT_MAIL_TO",
        "REPORT_MAIL_PASSWORD",
        "SMTP_HOST",
        "SMTP_PORT",
        "ADMIN_WEB_URL",
    ):
        required_env(name)


def backend_url(path: str) -> str:
    base_url = required_env("BACKEND_BASE_URL").rstrip("/")
    return f"{base_url}{path}"


def unwrap_data(payload: Any, context: str) -> dict[str, Any]:
    if not isinstance(payload, dict):
        raise ReportError(f"{context} 응답이 JSON 객체가 아닙니다.")
    if payload.get("success") is not True:
        message = payload.get("message") or "알 수 없는 오류"
        code = payload.get("code") or "NO_CODE"
        raise ReportError(f"{context} 실패: {code} {message}")
    data = payload.get("data")
    if not isinstance(data, dict):
        raise ReportError(f"{context} 응답 data가 없습니다.")
    return data


def post_json(path: str, body: dict[str, Any], headers: dict[str, str] | None = None) -> dict[str, Any]:
    response = requests.post(
        backend_url(path),
        json=body,
        headers=headers,
        timeout=REQUEST_TIMEOUT_SECONDS,
    )
    if response.status_code == 401:
        raise ReportError("관리자 로그인에 실패했습니다. 계정 또는 비밀번호를 확인하세요.")
    response.raise_for_status()
    return response.json()


def get_json(path: str, token: str, params: dict[str, Any] | None = None) -> dict[str, Any]:
    response = requests.get(
        backend_url(path),
        params=params,
        headers={"Authorization": f"Bearer {token}"},
        timeout=REQUEST_TIMEOUT_SECONDS,
    )
    if response.status_code == 401:
        raise ReportError(f"{path} 호출 인증에 실패했습니다. 관리자 토큰을 확인하세요.")
    response.raise_for_status()
    return response.json()


def login_admin() -> str:
    payload = post_json(
        LOGIN_PATH,
        {
            "username": required_env("BACKEND_ADMIN_USERNAME"),
            "password": required_env("BACKEND_ADMIN_PASSWORD"),
        },
    )
    data = unwrap_data(payload, "관리자 로그인")
    token = data.get("accessToken")
    if not isinstance(token, str) or not token:
        raise ReportError("관리자 로그인 응답에 accessToken이 없습니다.")
    username = data.get("username") or "데이터 없음"
    role = data.get("role") or "데이터 없음"
    print(f"관리자 로그인 성공: username={username}, role={role}")
    return token


def collect_spending_summary(token: str) -> dict[str, Any]:
    payload = get_json(SPENDING_SUMMARY_PATH, token)
    return unwrap_data(payload, "소비 인사이트 수집")


def collect_budget_risk(token: str) -> dict[str, Any]:
    payload = get_json(BUDGET_RISK_PATH, token, {"page": 0, "size": 5})
    return unwrap_data(payload, "예산 위험도 수집")


def safe_get(source: dict[str, Any] | None, key: str, default: Any = "데이터 없음") -> Any:
    if not isinstance(source, dict):
        return default
    value = source.get(key)
    return default if value is None or value == "" else value


def print_collection_result(spending: dict[str, Any], budget_risk: dict[str, Any]) -> None:
    spending_summary = safe_get(spending, "summary", {})
    risk_summary = safe_get(budget_risk, "summary", {})
    risk_items = budget_risk.get("items") if isinstance(budget_risk.get("items"), list) else []
    top_regions = spending.get("topRegions") if isinstance(spending.get("topRegions"), list) else []
    tag_clicks = spending.get("tagClicks") if isinstance(spending.get("tagClicks"), list) else []

    print("\n=== 관리자 일일 리포트 데이터 수집 결과 ===")
    print(f"전체 방 수: {safe_get(risk_summary, 'totalRoomCount')}")
    print(f"총 지출액: {safe_get(spending_summary, 'totalSpentAmount')}")
    print(f"평균 영수증 금액: {safe_get(spending_summary, 'averageReceiptAmount')}")
    print(f"고위험 방: {safe_get(risk_summary, 'highCount')}")
    print(f"중위험 방: {safe_get(risk_summary, 'mediumCount')}")
    print(f"저위험 방: {safe_get(risk_summary, 'lowCount')}")
    print(f"평균 위험 점수: {safe_get(risk_summary, 'averageRiskScore')}")
    print(f"예산 초과 방 비율: {safe_get(spending_summary, 'budgetOverRoomRate')}")
    print(f"착한가격업소 이용률: {safe_get(spending_summary, 'goodPriceUsageRate')}")

    print("\n--- 고위험 방 TOP 5 ---")
    if not risk_items:
        print("데이터 없음")
    for index, item in enumerate(risk_items[:5], start=1):
        print(
            f"{index}. {safe_get(item, 'roomName')} "
            f"(roomNo={safe_get(item, 'roomNo')}, "
            f"riskScore={safe_get(item, 'riskScore')}, "
            f"predictedUsage={safe_get(item, 'predictedBudgetUsageRate')}%)"
        )

    print("\n--- 지역별 소비 TOP ---")
    if not top_regions:
        print("데이터 없음")
    for item in top_regions[:5]:
        print(f"- {safe_get(item, 'region')}: {safe_get(item, 'spentAmount')}")

    print("\n--- 태그별 추천 클릭 ---")
    if not tag_clicks:
        print("데이터 없음")
    for item in tag_clicks[:5]:
        print(f"- {safe_get(item, 'tag')}: {safe_get(item, 'clickCount')}")


def format_number(value: Any, suffix: str = "") -> str:
    if value is None or value == "" or value == "데이터 없음":
        return "데이터 없음"
    if isinstance(value, bool):
        return str(value)
    if isinstance(value, (int, float)):
        if isinstance(value, float) and not value.is_integer():
            return f"{value:,.1f}{suffix}"
        return f"{int(value):,}{suffix}"
    return f"{value}{suffix}"


def escape(value: Any) -> str:
    if value is None or value == "":
        return "데이터 없음"
    return html.escape(str(value))


def metric_card(label: str, value: Any) -> str:
    return f"""
    <td style="padding:8px;width:25%;">
      <div style="border:1px solid #eadfcb;border-radius:12px;padding:14px;background:#fffaf1;">
        <div style="font-size:12px;color:#8a7c67;font-weight:700;">{escape(label)}</div>
        <div style="margin-top:8px;font-size:22px;color:#2f2922;font-weight:900;">{escape(value)}</div>
      </div>
    </td>
    """


def render_risk_rows(items: list[Any]) -> str:
    if not items:
        return "<tr><td colspan=\"4\" style=\"padding:14px;text-align:center;color:#8a7c67;\">데이터 없음</td></tr>"

    rows = []
    for index, item in enumerate(items[:5], start=1):
        if not isinstance(item, dict):
            continue
        rows.append(
            f"""
            <tr>
              <td style="padding:10px;border-bottom:1px solid #eee3d1;">{index}</td>
              <td style="padding:10px;border-bottom:1px solid #eee3d1;font-weight:800;">{escape(safe_get(item, "roomName"))}</td>
              <td style="padding:10px;border-bottom:1px solid #eee3d1;">{escape(format_number(safe_get(item, "riskScore")))}</td>
              <td style="padding:10px;border-bottom:1px solid #eee3d1;">{escape(format_number(safe_get(item, "predictedBudgetUsageRate"), "%"))}</td>
            </tr>
            """
        )
    return "\n".join(rows) if rows else "<tr><td colspan=\"4\" style=\"padding:14px;text-align:center;color:#8a7c67;\">데이터 없음</td></tr>"


def render_simple_list(items: list[Any], label_key: str, value_key: str, suffix: str = "") -> str:
    if not items:
        return "<li>데이터 없음</li>"
    rows = []
    for item in items[:5]:
        if not isinstance(item, dict):
            continue
        rows.append(
            f"<li><strong>{escape(safe_get(item, label_key))}</strong>: "
            f"{escape(format_number(safe_get(item, value_key), suffix))}</li>"
        )
    return "\n".join(rows) if rows else "<li>데이터 없음</li>"


def build_email_subject(now: datetime) -> str:
    return f"[거지 우정 수호대] 일일 운영 리포트 - {now:%Y.%m.%d}"


def build_report_html(spending: dict[str, Any], budget_risk: dict[str, Any], now: datetime) -> str:
    spending_summary = safe_get(spending, "summary", {})
    risk_summary = safe_get(budget_risk, "summary", {})
    risk_items = budget_risk.get("items") if isinstance(budget_risk.get("items"), list) else []
    top_regions = spending.get("topRegions") if isinstance(spending.get("topRegions"), list) else []
    tag_clicks = spending.get("tagClicks") if isinstance(spending.get("tagClicks"), list) else []
    admin_web_url = required_env("ADMIN_WEB_URL")

    total_rooms = format_number(safe_get(risk_summary, "totalRoomCount"), "개")
    total_spent = format_number(safe_get(spending_summary, "totalSpentAmount"), "원")
    average_receipt = format_number(safe_get(spending_summary, "averageReceiptAmount"), "원")
    average_risk_score = format_number(safe_get(risk_summary, "averageRiskScore"))

    return f"""<!doctype html>
<html lang="ko">
<body style="margin:0;padding:0;background:#f5f0e8;font-family:Arial,'Apple SD Gothic Neo','Malgun Gothic',sans-serif;color:#2f2922;">
  <div style="max-width:760px;margin:0 auto;padding:28px 18px;">
    <div style="background:#ffffff;border-radius:18px;padding:28px;border:1px solid #eadfcb;">
      <div style="font-size:13px;color:#a18f75;font-weight:800;">{now:%Y.%m.%d} 운영 리포트</div>
      <h1 style="margin:8px 0 6px;font-size:28px;line-height:1.25;color:#2f2922;">거지 우정 수호대 일일 운영 리포트</h1>
      <p style="margin:0 0 22px;color:#6f6252;font-size:14px;">관리자 주요 운영 지표를 자동 수집한 결과입니다.</p>

      <h2 style="font-size:18px;margin:22px 0 10px;">1. 운영 요약</h2>
      <table role="presentation" style="width:100%;border-collapse:collapse;"><tr>
        {metric_card("전체 방 수", total_rooms)}
        {metric_card("총 지출액", total_spent)}
        {metric_card("평균 영수증", average_receipt)}
        {metric_card("평균 위험 점수", average_risk_score)}
      </tr></table>

      <h2 style="font-size:18px;margin:26px 0 10px;">2. 예산 위험도</h2>
      <table role="presentation" style="width:100%;border-collapse:collapse;"><tr>
        {metric_card("고위험 방", format_number(safe_get(risk_summary, "highCount"), "개"))}
        {metric_card("중위험 방", format_number(safe_get(risk_summary, "mediumCount"), "개"))}
        {metric_card("저위험 방", format_number(safe_get(risk_summary, "lowCount"), "개"))}
        {metric_card("고위험 비율", format_number(safe_get(risk_summary, "highRate"), "%"))}
      </tr></table>

      <h2 style="font-size:18px;margin:26px 0 10px;">3. 고위험 방 TOP 5</h2>
      <table style="width:100%;border-collapse:collapse;border:1px solid #eadfcb;border-radius:12px;overflow:hidden;">
        <thead>
          <tr style="background:#fff2d1;text-align:left;">
            <th style="padding:10px;">순위</th>
            <th style="padding:10px;">방 이름</th>
            <th style="padding:10px;">위험 점수</th>
            <th style="padding:10px;">예상 사용률</th>
          </tr>
        </thead>
        <tbody>{render_risk_rows(risk_items)}</tbody>
      </table>

      <h2 style="font-size:18px;margin:26px 0 10px;">4. 소비 인사이트</h2>
      <ul style="line-height:1.8;margin:0;padding-left:20px;">
        <li>예산 초과 방 비율: <strong>{escape(format_number(safe_get(spending_summary, "budgetOverRoomRate"), "%"))}</strong></li>
        <li>착한가격업소 이용률: <strong>{escape(format_number(safe_get(spending_summary, "goodPriceUsageRate"), "%"))}</strong></li>
      </ul>

      <table role="presentation" style="width:100%;border-collapse:collapse;margin-top:12px;"><tr>
        <td style="width:50%;vertical-align:top;padding-right:10px;">
          <div style="border:1px solid #eadfcb;border-radius:12px;padding:14px;">
            <h3 style="font-size:15px;margin:0 0 8px;">지역별 소비 TOP</h3>
            <ul style="line-height:1.8;margin:0;padding-left:18px;">{render_simple_list(top_regions, "region", "spentAmount", "원")}</ul>
          </div>
        </td>
        <td style="width:50%;vertical-align:top;padding-left:10px;">
          <div style="border:1px solid #eadfcb;border-radius:12px;padding:14px;">
            <h3 style="font-size:15px;margin:0 0 8px;">태그별 추천 클릭</h3>
            <ul style="line-height:1.8;margin:0;padding-left:18px;">{render_simple_list(tag_clicks, "tag", "clickCount", "회")}</ul>
          </div>
        </td>
      </tr></table>

      <div style="margin-top:26px;padding:16px;border-radius:12px;background:#2f2922;text-align:center;">
        <a href="{html.escape(admin_web_url)}" style="color:#ffffff;text-decoration:none;font-weight:900;">관리자 페이지에서 자세히 보기</a>
      </div>
    </div>
  </div>
</body>
</html>"""


def parse_recipients(raw_value: str) -> list[str]:
    recipients = [item.strip() for item in raw_value.split(",") if item.strip()]
    if not recipients:
        raise ReportError("REPORT_MAIL_TO에 유효한 수신자 이메일이 없습니다.")
    return recipients


def send_email(subject: str, html_body: str) -> None:
    sender = required_env("REPORT_MAIL_FROM")
    smtp_username = os.environ.get("SMTP_USERNAME", sender).strip() or sender
    recipients = parse_recipients(required_env("REPORT_MAIL_TO"))
    password = required_env("REPORT_MAIL_PASSWORD")
    smtp_host = required_env("SMTP_HOST")
    smtp_port_raw = required_env("SMTP_PORT")
    try:
        smtp_port = int(smtp_port_raw)
    except ValueError as exc:
        raise ReportError("SMTP_PORT는 숫자여야 합니다.") from exc

    message = MIMEMultipart("alternative")
    message["Subject"] = subject
    message["From"] = sender
    message["To"] = ", ".join(recipients)
    message.attach(MIMEText("HTML 메일을 지원하는 클라이언트에서 확인해주세요.", "plain", "utf-8"))
    message.attach(MIMEText(html_body, "html", "utf-8"))

    try:
        with smtplib.SMTP(smtp_host, smtp_port, timeout=REQUEST_TIMEOUT_SECONDS) as smtp:
            smtp.starttls()
            smtp.login(smtp_username, password)
            smtp.sendmail(sender, recipients, message.as_string())
    except smtplib.SMTPException as exc:
        raise ReportError(f"메일 발송에 실패했습니다: {exc}") from exc


def main() -> int:
    load_local_env()
    try:
        require_report_env()
        token = login_admin()
        spending = collect_spending_summary(token)
        budget_risk = collect_budget_risk(token)
        print_collection_result(spending, budget_risk)
        now = datetime.now(KST)
        subject = build_email_subject(now)
        html_body = build_report_html(spending, budget_risk, now)
        send_email(subject, html_body)
        print(f"\n메일 발송 성공: {subject}")
        return 0
    except requests.RequestException as exc:
        print(f"API 호출 중 네트워크 오류가 발생했습니다: {exc}", file=sys.stderr)
        return 1
    except ReportError as exc:
        print(str(exc), file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
