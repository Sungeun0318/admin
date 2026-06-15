<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="toolbar">
    <form class="search-form" method="get" action="/admin/users">
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="이름 또는 이메일 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/users">초기화</a>
    </form>
    <span class="admin-badge">총 ${users.totalElements}명</span>
  </section>

  <c:choose>
    <c:when test="${empty users.content}">
      <div class="empty-state">검색 결과가 없습니다.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>회원 번호</th>
              <th>이름</th>
              <th>이메일</th>
              <th>권한</th>
              <th>가입일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="user" items="${users.content}">
              <tr>
                <td>${user.userNo}</td>
                <td>${user.userName}</td>
                <td>${user.email}</td>
                <td><span class="status-pill status-active">${user.role}</span></td>
                <td>${user.createdAt}</td>
                <td>
                  <a class="button button-ghost" href="/admin/users/${user.userNo}">상세</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="회원 목록 페이지">
        <c:if test="${users.number > 0}">
          <a class="button button-ghost" href="/admin/users?keyword=${keyword}&page=${users.number - 1}">이전</a>
        </c:if>
        <span class="pagination-current">${users.number + 1} / ${users.totalPages}</span>
        <c:if test="${users.number + 1 < users.totalPages}">
          <a class="button button-ghost" href="/admin/users?keyword=${keyword}&page=${users.number + 1}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>
<%@ include file="../layout/footer.jsp" %>
