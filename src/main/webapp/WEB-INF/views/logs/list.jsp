<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="toolbar">
    <form class="search-form" method="get" action="/admin/logs">
      <input
        class="select-input"
        type="search"
        name="adminUsername"
        value="${adminUsername}"
        placeholder="관리자"
      >
      <select class="select-input" name="action">
        <option value="" ${empty action ? 'selected' : ''}>전체 액션</option>
        <option value="CREATE" ${action == 'CREATE' ? 'selected' : ''}>생성</option>
        <option value="UPDATE" ${action == 'UPDATE' ? 'selected' : ''}>수정</option>
        <option value="DELETE" ${action == 'DELETE' ? 'selected' : ''}>삭제</option>
        <option value="DISABLE" ${action == 'DISABLE' ? 'selected' : ''}>비활성화</option>
        <option value="END" ${action == 'END' ? 'selected' : ''}>종료</option>
        <option value="TOGGLE_VISIBLE" ${action == 'TOGGLE_VISIBLE' ? 'selected' : ''}>노출 변경</option>
      </select>
      <select class="select-input" name="targetType">
        <option value="" ${empty targetType ? 'selected' : ''}>전체 대상</option>
        <option value="ROOM" ${targetType == 'ROOM' ? 'selected' : ''}>방</option>
        <option value="POST" ${targetType == 'POST' ? 'selected' : ''}>게시글</option>
        <option value="COMMENT" ${targetType == 'COMMENT' ? 'selected' : ''}>댓글</option>
        <option value="CHAT" ${targetType == 'CHAT' ? 'selected' : ''}>채팅</option>
        <option value="RECEIPT" ${targetType == 'RECEIPT' ? 'selected' : ''}>영수증</option>
      </select>
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="대상 ID, 메시지 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/logs">초기화</a>
    </form>
    <span class="admin-badge">총 ${logs.totalElements}개</span>
  </section>

  <c:choose>
    <c:when test="${empty logs.content}">
      <div class="empty-state">조건에 맞는 운영 로그가 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>로그 번호</th>
              <th>관리자</th>
              <th>액션</th>
              <th>대상</th>
              <th>대상 ID</th>
              <th>내용</th>
              <th>생성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="log" items="${logs.content}">
              <tr>
                <td>${log.logId}</td>
                <td>${log.adminUsername}</td>
                <td>${log.actionLabel}</td>
                <td>${log.targetTypeLabel}</td>
                <td>${log.targetId}</td>
                <td class="table-content">${log.message}</td>
                <td>${log.createdAt}</td>
                <td>
                  <a class="button button-ghost" href="/admin/logs/${log.logId}">상세</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="운영 로그 목록 페이지">
        <c:url var="prevUrl" value="/admin/logs">
          <c:param name="adminUsername" value="${adminUsername}" />
          <c:param name="action" value="${action}" />
          <c:param name="targetType" value="${targetType}" />
          <c:param name="keyword" value="${keyword}" />
          <c:param name="page" value="${logs.number - 1}" />
        </c:url>
        <c:url var="nextUrl" value="/admin/logs">
          <c:param name="adminUsername" value="${adminUsername}" />
          <c:param name="action" value="${action}" />
          <c:param name="targetType" value="${targetType}" />
          <c:param name="keyword" value="${keyword}" />
          <c:param name="page" value="${logs.number + 1}" />
        </c:url>
        <c:if test="${logs.number > 0}">
          <a class="button button-ghost" href="${prevUrl}">이전</a>
        </c:if>
        <span class="pagination-current">${logs.number + 1} / ${logs.totalPages}</span>
        <c:if test="${logs.number + 1 < logs.totalPages}">
          <a class="button button-ghost" href="${nextUrl}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>

  <section class="panel">
    <h2>운영 로그 기준</h2>
    <p>
      운영 로그는 관리자 변경 액션의 감사 기록이야.
      일반 관리자도 로그를 삭제할 수 없고, 목록과 상세 조회만 제공해.
    </p>
  </section>
<%@ include file="../layout/footer.jsp" %>
