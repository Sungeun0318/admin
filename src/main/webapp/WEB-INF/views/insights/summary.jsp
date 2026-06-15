<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <c:choose>
    <c:when test="${empty summary}">
      <div class="empty-state">소비 인사이트 데이터를 불러오지 못했어.</div>
    </c:when>
    <c:otherwise>
      <section class="stats-grid insights-stats" aria-label="소비 인사이트 요약">
        <article class="stat-card accent-stat">
          <p class="stat-label">총 지출 금액</p>
          <p class="stat-value">
            <fmt:formatNumber value="${summary.totalSpentAmount}" pattern="#,###" />원
          </p>
        </article>
        <article class="stat-card">
          <p class="stat-label">평균 결제 금액</p>
          <p class="stat-value">
            <fmt:formatNumber value="${summary.averageReceiptAmount}" pattern="#,###" />원
          </p>
        </article>
        <article class="stat-card">
          <p class="stat-label">예산 초과 방 비율</p>
          <p class="stat-value">${summary.budgetOverRoomRate}%</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">착한가격업소 이용률</p>
          <p class="stat-value">${summary.goodPriceUsageRate}%</p>
        </article>
      </section>

      <section class="dashboard-panels insights-panels" aria-label="소비 차트">
        <article class="panel">
          <div class="panel-header">
            <h2>지역별 지출 TOP 5</h2>
            <span class="admin-badge">지출 합계</span>
          </div>
          <div class="insight-list">
            <c:choose>
              <c:when test="${empty topRegions}">
                <div class="mini-empty">지역별 지출 데이터가 없어.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="region" items="${topRegions}">
                  <div class="insight-row">
                    <div>
                      <p class="mini-title">${region.region}</p>
                      <p class="mini-subtitle">영수증 기준 합산</p>
                    </div>
                    <strong><fmt:formatNumber value="${region.spentAmount}" pattern="#,###" />원</strong>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </article>

        <article class="panel">
          <div class="panel-header">
            <h2>태그별 추천 클릭 수</h2>
            <span class="admin-badge">CLICK</span>
          </div>
          <div class="insight-list">
            <c:choose>
              <c:when test="${empty tagClicks}">
                <div class="mini-empty">추천 클릭 데이터가 없어.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="tag" items="${tagClicks}">
                  <div class="insight-row">
                    <div>
                      <p class="mini-title">${tag.tag}</p>
                      <p class="mini-subtitle">추천 상세 클릭</p>
                    </div>
                    <strong><fmt:formatNumber value="${tag.clickCount}" pattern="#,###" />회</strong>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </article>
      </section>

      <section class="panel">
        <div class="panel-header">
          <h2>예산 사용률 높은 방 TOP 10</h2>
          <span class="admin-badge">방별 지출 / 총 예산</span>
        </div>

        <c:choose>
          <c:when test="${empty highBudgetUsageRooms}">
            <div class="mini-empty">예산 사용률 데이터가 없어.</div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap compact-table">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>방 번호</th>
                    <th>방 이름</th>
                    <th>총 예산</th>
                    <th>현재 지출</th>
                    <th>사용률</th>
                    <th>관리</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="room" items="${highBudgetUsageRooms}">
                    <tr>
                      <td>${room.roomNo}</td>
                      <td>${room.roomName}</td>
                      <td><fmt:formatNumber value="${room.totalBudget}" pattern="#,###" />원</td>
                      <td><fmt:formatNumber value="${room.spentAmount}" pattern="#,###" />원</td>
                      <td>
                        <span class="status-pill ${room.usageRate >= 100 ? 'status-danger' : 'status-active'}">
                          ${room.usageRate}%
                        </span>
                      </td>
                      <td>
                        <a class="button button-ghost" href="/admin/rooms/${room.roomNo}">상세</a>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </section>

      <section class="panel">
        <div class="panel-header">
          <h2>예산 초과 위험 예측</h2>
          <span class="admin-badge">모델 ${budgetRiskModelVersion}</span>
        </div>

        <c:choose>
          <c:when test="${empty budgetRiskItems}">
            <div class="mini-empty">예산 위험 예측 데이터가 없어.</div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap compact-table">
              <table class="data-table">
                <thead>
                  <tr>
                    <th>방 번호</th>
                    <th>방 이름</th>
                    <th>위험도</th>
                    <th>위험 점수</th>
                    <th>예상 최종 지출</th>
                    <th>예상 사용률</th>
                    <th>권장 다음 소비</th>
                    <th>사유</th>
                    <th>관리</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="risk" items="${budgetRiskItems}">
                    <tr>
                      <td>${risk.roomNo}</td>
                      <td>${risk.roomName}</td>
                      <td>
                        <span class="status-pill ${risk.riskLevel == 'HIGH' ? 'status-danger' : 'status-active'}">
                          ${risk.riskLevel}
                        </span>
                      </td>
                      <td>${risk.riskScore}</td>
                      <td><fmt:formatNumber value="${risk.predictedFinalSpentAmount}" pattern="#,###" />원</td>
                      <td>${risk.predictedBudgetUsageRate}%</td>
                      <td><fmt:formatNumber value="${risk.recommendedNextSpendLimit}" pattern="#,###" />원</td>
                      <td class="table-content">${risk.reason}</td>
                      <td>
                        <a class="button button-ghost" href="/admin/rooms/${risk.roomNo}">상세</a>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </section>
    </c:otherwise>
  </c:choose>

<%@ include file="../layout/footer.jsp" %>
