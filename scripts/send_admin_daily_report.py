#!/usr/bin/env python3
"""Collect admin daily report data.

Stage 1 intentionally collects and prints report data only.
Email rendering/sending is added in the next stage.
"""

from __future__ import annotations

import os
import sys
from pathlib import Path
from typing import Any

import requests


LOGIN_PATH = "/admin/auth/login"
SPENDING_SUMMARY_PATH = "/admin/ai/insights/spending-summary"
BUDGET_RISK_PATH = "/admin/ai/predictions/budget-risk"
REQUEST_TIMEOUT_SECONDS = 20


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


def main() -> int:
    load_local_env()
    try:
        token = login_admin()
        spending = collect_spending_summary(token)
        budget_risk = collect_budget_risk(token)
        print_collection_result(spending, budget_risk)
        return 0
    except requests.RequestException as exc:
        print(f"API 호출 중 네트워크 오류가 발생했습니다: {exc}", file=sys.stderr)
        return 1
    except ReportError as exc:
        print(str(exc), file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
