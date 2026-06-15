<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <c:choose>
    <c:when test="${empty budgetRiskSummary}">
      <div class="empty-state">예산 위험도 데이터를 불러오지 못했어.</div>
    </c:when>
    <c:otherwise>
      <section class="stats-grid risk-stats" aria-label="예산 위험도 요약">
        <article class="stat-card accent-stat">
          <p class="stat-label">고위험 방</p>
          <p class="stat-value"><fmt:formatNumber value="${budgetRiskSummary.highCount}" pattern="#,###" />개</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">중위험 방</p>
          <p class="stat-value"><fmt:formatNumber value="${budgetRiskSummary.mediumCount}" pattern="#,###" />개</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">저위험 방</p>
          <p class="stat-value"><fmt:formatNumber value="${budgetRiskSummary.lowCount}" pattern="#,###" />개</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">평균 위험 점수</p>
          <p class="stat-value">${budgetRiskSummary.averageRiskScore}</p>
        </article>
      </section>

      <section class="dashboard-panels risk-panels" aria-label="예산 위험도 차트">
        <article class="panel">
          <div class="panel-header">
            <h2>위험도 분포</h2>
            <span class="admin-badge">모델 ${budgetRiskModelVersion}</span>
          </div>
          <div class="risk-bars">
            <div class="risk-bar-row">
              <div class="risk-bar-label">
                <strong>HIGH</strong>
                <span>${budgetRiskSummary.highRate}%</span>
              </div>
              <div class="risk-bar-track">
                <div class="risk-bar-fill risk-high" style="width: ${budgetRiskSummary.highRate}%;"></div>
              </div>
            </div>
            <div class="risk-bar-row">
              <div class="risk-bar-label">
                <strong>MEDIUM</strong>
                <span>${budgetRiskSummary.mediumRate}%</span>
              </div>
              <div class="risk-bar-track">
                <div class="risk-bar-fill risk-medium" style="width: ${budgetRiskSummary.mediumRate}%;"></div>
              </div>
            </div>
            <div class="risk-bar-row">
              <div class="risk-bar-label">
                <strong>LOW</strong>
                <span>${budgetRiskSummary.lowRate}%</span>
              </div>
              <div class="risk-bar-track">
                <div class="risk-bar-fill risk-low" style="width: ${budgetRiskSummary.lowRate}%;"></div>
              </div>
            </div>
          </div>
        </article>

        <article class="panel">
          <div class="panel-header">
            <h2>예측 대상</h2>
            <span class="admin-badge">전체 방</span>
          </div>
          <div class="risk-total">
            <strong><fmt:formatNumber value="${budgetRiskSummary.totalRoomCount}" pattern="#,###" />개</strong>
            <p>방별 예산, 지출 영수증, 착한가격업소 이용 여부를 기반으로 위험도를 계산했어.</p>
          </div>
        </article>
      </section>

      <section class="panel">
        <div class="panel-header">
          <h2>위험 방 목록</h2>
          <span class="admin-badge">위험 점수순</span>
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
                        <span class="status-pill ${risk.riskLevel == 'HIGH' ? 'status-danger' : risk.riskLevel == 'MEDIUM' ? 'status-warning' : 'status-active'}">
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
            <nav class="pagination" aria-label="예산 위험도 페이지">
              <c:choose>
                <c:when test="${riskHasJumpPrevious}">
                  <a class="button button-ghost" href="/admin/budget-risk?page=${riskJumpPreviousPage}&size=${riskSize}">-5</a>
                </c:when>
                <c:otherwise>
                  <button class="button button-ghost" disabled>-5</button>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${riskHasPrevious}">
                  <a class="button button-ghost" href="/admin/budget-risk?page=${riskPage - 1}&size=${riskSize}">이전</a>
                </c:when>
                <c:otherwise>
                  <button class="button button-ghost" disabled>이전</button>
                </c:otherwise>
              </c:choose>
              <c:forEach var="pageNo" items="${riskPageNumbers}">
                <c:choose>
                  <c:when test="${pageNo == riskPage}">
                    <span class="button button-page-active" aria-current="page">${pageNo + 1}</span>
                  </c:when>
                  <c:otherwise>
                    <a class="button button-ghost" href="/admin/budget-risk?page=${pageNo}&size=${riskSize}">${pageNo + 1}</a>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
              <span class="pagination-current">
                ${riskPage + 1} / ${riskTotalPages == 0 ? 1 : riskTotalPages}
                · 총 <fmt:formatNumber value="${riskTotalItems}" pattern="#,###" />개
              </span>
              <c:choose>
                <c:when test="${riskHasNext}">
                  <a class="button button-ghost" href="/admin/budget-risk?page=${riskPage + 1}&size=${riskSize}">다음</a>
                </c:when>
                <c:otherwise>
                  <button class="button button-ghost" disabled>다음</button>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${riskHasJumpNext}">
                  <a class="button button-ghost" href="/admin/budget-risk?page=${riskJumpNextPage}&size=${riskSize}">+5</a>
                </c:when>
                <c:otherwise>
                  <button class="button button-ghost" disabled>+5</button>
                </c:otherwise>
              </c:choose>
            </nav>
          </c:otherwise>
        </c:choose>
      </section>
    </c:otherwise>
  </c:choose>

<%@ include file="../layout/footer.jsp" %>
