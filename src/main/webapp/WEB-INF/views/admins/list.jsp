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
    <form class="search-form" method="get" action="/admin/admins">
      <select class="select-input" name="status">
        <option value="ALL" ${status == 'ALL' ? 'selected' : ''}>전체</option>
        <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>활성</option>
        <option value="DISABLED" ${status == 'DISABLED' ? 'selected' : ''}>비활성</option>
      </select>
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="아이디, 표시 이름 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/admins">초기화</a>
    </form>
    <div class="admin-header-actions">
      <span class="admin-badge">총 ${admins.totalElements}개</span>
      <a class="button button-primary" href="/admin/admins/new">관리자 생성</a>
    </div>
  </section>

  <c:choose>
    <c:when test="${empty admins.content}">
      <div class="empty-state">조건에 맞는 관리자 계정이 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>번호</th>
              <th>아이디</th>
              <th>표시 이름</th>
              <th>권한</th>
              <th>상태</th>
              <th>생성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="admin" items="${admins.content}">
              <tr>
                <td>${admin.adminId}</td>
                <td>${admin.username}</td>
                <td>${admin.displayName}</td>
                <td>${admin.roleLabel}</td>
                <td><span class="status-pill ${admin.statusClass}">${admin.statusLabel}</span></td>
                <td>${admin.createdAt}</td>
                <td>
                  <div class="comment-actions">
                    <a class="button button-ghost" href="/admin/admins/${admin.adminId}/edit">수정</a>
                    <form
                      method="post"
                      action="/admin/admins/${admin.adminId}/disable"
                      onsubmit="return confirm('관리자 계정을 비활성화할까?');"
                    >
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                      <button class="button button-danger" type="submit">비활성화</button>
                    </form>
                  </div>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="관리자 목록 페이지">
        <c:url var="prevUrl" value="/admin/admins">
          <c:param name="keyword" value="${keyword}" />
          <c:param name="status" value="${status}" />
          <c:param name="page" value="${admins.number - 1}" />
        </c:url>
        <c:url var="nextUrl" value="/admin/admins">
          <c:param name="keyword" value="${keyword}" />
          <c:param name="status" value="${status}" />
          <c:param name="page" value="${admins.number + 1}" />
        </c:url>
        <c:if test="${admins.number > 0}">
          <a class="button button-ghost" href="${prevUrl}">이전</a>
        </c:if>
        <span class="pagination-current">${admins.number + 1} / ${admins.totalPages}</span>
        <c:if test="${admins.number + 1 < admins.totalPages}">
          <a class="button button-ghost" href="${nextUrl}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>

  <section class="panel">
    <h2>권한 안내</h2>
    <p>
      이 화면은 SUPER_ADMIN만 접근할 수 있어.
      OPERATOR는 운영 데이터 관리, VIEWER는 조회 전용 권한으로 확장하면 돼.
    </p>
  </section>
<%@ include file="../layout/footer.jsp" %>
