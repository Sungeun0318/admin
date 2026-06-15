<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="toolbar">
    <form class="search-form" method="get" action="/admin/rooms">
      <select class="select-input" name="status">
        <option value="ALL" ${status == 'ALL' ? 'selected' : ''}>전체</option>
        <option value="INVITING" ${status == 'INVITING' ? 'selected' : ''}>초대중</option>
        <option value="BUDGET_INPUT" ${status == 'BUDGET_INPUT' ? 'selected' : ''}>예산 입력중</option>
        <option value="BUDGET_DONE" ${status == 'BUDGET_DONE' ? 'selected' : ''}>예산 확정</option>
        <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>진행중</option>
        <option value="ENDED" ${status == 'ENDED' ? 'selected' : ''}>종료</option>
        <option value="DELETED" ${status == 'DELETED' ? 'selected' : ''}>삭제</option>
      </select>
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="방 이름, 지역, 초대코드 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/rooms">초기화</a>
    </form>
    <span class="admin-badge">총 ${rooms.totalElements}개</span>
  </section>

  <c:choose>
    <c:when test="${empty rooms.content}">
      <div class="empty-state">조건에 맞는 방이 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>방 번호</th>
              <th>방 이름</th>
              <th>지역</th>
              <th>초대 코드</th>
              <th>방장</th>
              <th>상태</th>
              <th>생성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="room" items="${rooms.content}">
              <tr>
                <td>${room.roomNo}</td>
                <td>${room.roomName}</td>
                <td>${room.location}</td>
                <td>${room.roomCode}</td>
                <td>${room.ownerLabel}</td>
                <td><span class="status-pill ${room.statusClass}">${room.statusLabel}</span></td>
                <td>${room.createdAt}</td>
                <td>
                  <a class="button button-ghost" href="/admin/rooms/${room.roomNo}">상세</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="방 목록 페이지">
        <c:if test="${rooms.number > 0}">
          <a class="button button-ghost" href="/admin/rooms?keyword=${keyword}&status=${status}&page=${rooms.number - 1}">이전</a>
        </c:if>
        <span class="pagination-current">${rooms.number + 1} / ${rooms.totalPages}</span>
        <c:if test="${rooms.number + 1 < rooms.totalPages}">
          <a class="button button-ghost" href="/admin/rooms?keyword=${keyword}&status=${status}&page=${rooms.number + 1}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>

<%@ include file="../layout/footer.jsp" %>
