<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <div class="detail-actions">
    <a class="button button-ghost" href="/admin/rooms">목록으로</a>
  </div>

  <c:if test="${not empty message}">
    <div class="alert alert-success">${message}</div>
  </c:if>

  <section class="detail-grid">
    <article class="panel">
      <div class="panel-header">
        <h2>기본 정보</h2>
        <span class="status-pill ${room.statusClass}">${room.statusLabel}</span>
      </div>
      <dl class="detail-list">
        <div>
          <dt>방 번호</dt>
          <dd>${room.roomNo}</dd>
        </div>
        <div>
          <dt>방 이름</dt>
          <dd>${room.roomName}</dd>
        </div>
        <div>
          <dt>초대 코드</dt>
          <dd>${room.roomCode}</dd>
        </div>
        <div>
          <dt>방장</dt>
          <dd>${room.ownerLabel}</dd>
        </div>
        <div>
          <dt>지역</dt>
          <dd>${room.location}</dd>
        </div>
        <div>
          <dt>최대 인원</dt>
          <dd>${room.maxMemberCount}</dd>
        </div>
        <div>
          <dt>생성일</dt>
          <dd>${room.createdAt}</dd>
        </div>
        <div>
          <dt>종료일</dt>
          <dd>${room.endedAt}</dd>
        </div>
        <div>
          <dt>삭제일</dt>
          <dd>${room.deletedAt}</dd>
        </div>
      </dl>
    </article>

    <article class="panel">
      <h2>운영 요약</h2>
      <div class="summary-grid">
        <div class="summary-card">
          <p class="stat-label">전체 참여자</p>
          <p class="stat-value">${room.memberCount}</p>
        </div>
        <div class="summary-card">
          <p class="stat-label">활성 참여자</p>
          <p class="stat-value">${room.activeMemberCount}</p>
        </div>
        <div class="summary-card">
          <p class="stat-label">영수증 수</p>
          <p class="stat-value">${room.receiptCount}</p>
        </div>
        <div class="summary-card">
          <p class="stat-label">영수증 합계</p>
          <p class="stat-value small-stat">${room.receiptAmount}</p>
        </div>
      </div>
    </article>
  </section>

  <section class="detail-grid">
    <article class="panel">
      <h2>예산 결과</h2>
      <dl class="detail-list">
        <div>
          <dt>1인 최저 예산</dt>
          <dd>${room.minBudgetPerPerson}</dd>
        </div>
        <div>
          <dt>총 예산</dt>
          <dd>${room.totalBudget}</dd>
        </div>
        <div>
          <dt>확정일</dt>
          <dd>${room.budgetConfirmedAt}</dd>
        </div>
      </dl>
    </article>

    <article class="panel">
      <h2>관리 액션</h2>
      <p>
        종료는 더 이상 진행 중인 방으로 집계하지 않는 처리야.
        삭제는 실제 행을 지우지 않고 삭제 상태로 숨기는 소프트 삭제야.
      </p>
      <div class="action-row">
        <form
          method="post"
          action="/admin/rooms/${room.roomNo}/end"
          onsubmit="return confirm('이 방을 종료 처리할까?');"
        >
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <button class="button button-ghost" type="submit" ${room.canEnd ? '' : 'disabled'}>방 종료</button>
        </form>
        <form
          method="post"
          action="/admin/rooms/${room.roomNo}/delete"
          onsubmit="return confirm('이 방을 삭제 처리할까? 실제 데이터는 보존돼.');"
        >
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <button class="button button-danger" type="submit" ${room.canDelete ? '' : 'disabled'}>방 삭제</button>
        </form>
      </div>
    </article>
  </section>
<%@ include file="../layout/footer.jsp" %>
