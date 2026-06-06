<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="detail-actions">
    <a class="button button-ghost" href="/admin/logs">목록으로</a>
  </section>

  <section class="panel">
    <h2>로그 정보</h2>
    <dl class="detail-list">
      <div>
        <dt>로그 번호</dt>
        <dd>${log.logId}</dd>
      </div>
      <div>
        <dt>관리자</dt>
        <dd>${log.adminUsername}</dd>
      </div>
      <div>
        <dt>액션</dt>
        <dd>${log.actionLabel}</dd>
      </div>
      <div>
        <dt>대상</dt>
        <dd>${log.targetTypeLabel}</dd>
      </div>
      <div>
        <dt>대상 ID</dt>
        <dd>${log.targetId}</dd>
      </div>
      <div>
        <dt>생성일</dt>
        <dd>${log.createdAt}</dd>
      </div>
    </dl>
  </section>

  <section class="panel">
    <h2>내용</h2>
    <div class="content-box">${log.message}</div>
  </section>
<%@ include file="../layout/footer.jsp" %>
