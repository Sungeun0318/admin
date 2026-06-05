<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <c:if test="${not empty message}">
    <div class="alert alert-success">${message}</div>
  </c:if>

  <section class="toolbar">
    <form class="search-form" method="get" action="/admin/chats">
      <input
        class="select-input"
        type="number"
        name="userNo"
        value="${userNo}"
        placeholder="회원 번호"
        min="1"
      >
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="채팅 내용 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/chats">초기화</a>
    </form>
    <span class="admin-badge">총 ${chats.totalElements}개</span>
  </section>

  <c:choose>
    <c:when test="${empty chats.content}">
      <div class="empty-state">조건에 맞는 채팅이 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>채팅 번호</th>
              <th>작성자</th>
              <th>메시지</th>
              <th>작성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="chat" items="${chats.content}">
              <tr>
                <td>${chat.chatId}</td>
                <td>${chat.authorLabel}</td>
                <td class="table-content">${chat.message}</td>
                <td>${chat.createdAt}</td>
                <td>
                  <form
                    method="post"
                    action="/admin/chats/delete"
                    onsubmit="return confirm('채팅 메시지를 삭제할까? 이 작업은 되돌릴 수 없어.');"
                  >
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <input type="hidden" name="chatId" value="${chat.chatId}">
                    <input type="hidden" name="keyword" value="${keyword}">
                    <input type="hidden" name="userNo" value="${userNo}">
                    <button class="button button-danger" type="submit">삭제</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="채팅 목록 페이지">
        <c:if test="${chats.number > 0}">
          <a class="button button-ghost" href="/admin/chats?keyword=${keyword}&userNo=${userNo}&page=${chats.number - 1}">이전</a>
        </c:if>
        <span class="pagination-current">${chats.number + 1} / ${chats.totalPages}</span>
        <c:if test="${chats.number + 1 < chats.totalPages}">
          <a class="button button-ghost" href="/admin/chats?keyword=${keyword}&userNo=${userNo}&page=${chats.number + 1}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>

  <section class="panel">
    <h2>채팅 관리 안내</h2>
    <p>
      현재 이 화면은 room_free_chats 테이블에 저장된 커뮤니티 전체 채팅을 관리해.
      WebSocket 실시간 관리는 이후 단계에서 별도로 연결하면 돼.
    </p>
  </section>
<%@ include file="../layout/footer.jsp" %>
