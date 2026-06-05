<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <div class="detail-actions">
    <a class="button button-ghost" href="/admin/users">목록으로</a>
  </div>

  <section class="detail-grid">
    <article class="panel">
      <div class="panel-header">
        <h2>기본 정보</h2>
        <span class="status-pill status-active">${user.role}</span>
      </div>
      <dl class="detail-list">
        <div>
          <dt>회원 번호</dt>
          <dd>${user.userNo}</dd>
        </div>
        <div>
          <dt>이름</dt>
          <dd>${user.userName}</dd>
        </div>
        <div>
          <dt>이메일</dt>
          <dd>${user.email}</dd>
        </div>
        <div>
          <dt>성별</dt>
          <dd>${user.genderLabel}</dd>
        </div>
        <div>
          <dt>나이대</dt>
          <dd>${user.ageRange}</dd>
        </div>
        <div>
          <dt>가입일</dt>
          <dd>${user.createdAt}</dd>
        </div>
      </dl>
    </article>

    <article class="panel">
      <h2>활동 요약</h2>
      <div class="summary-grid">
        <div class="summary-card">
          <p class="stat-label">방장 방</p>
          <p class="stat-value">${user.ownedRoomCount}</p>
        </div>
        <div class="summary-card">
          <p class="stat-label">참여 방</p>
          <p class="stat-value">${user.joinedRoomCount}</p>
        </div>
        <div class="summary-card">
          <p class="stat-label">작성 글</p>
          <p class="stat-value">${user.postCount}</p>
        </div>
        <div class="summary-card">
          <p class="stat-label">작성 댓글</p>
          <p class="stat-value">${user.commentCount}</p>
        </div>
      </div>
    </article>
  </section>

  <section class="panel">
    <h2>상태 관리</h2>
    <p>
      현재 사용자 DB에는 회원 status 컬럼이 없어서 비활성화 처리는 보류 상태야.
      다음 DB 변경 단계에서 status/deleted_at 컬럼을 추가하면 이 영역에 액션을 연결하면 돼.
    </p>
  </section>
<%@ include file="../layout/footer.jsp" %>
