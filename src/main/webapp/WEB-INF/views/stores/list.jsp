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
    <form class="search-form" method="get" action="/admin/recommendations/stores">
      <select class="select-input" name="visible">
        <option value="ALL" ${visible == 'ALL' ? 'selected' : ''}>전체</option>
        <option value="VISIBLE" ${visible == 'VISIBLE' ? 'selected' : ''}>노출</option>
        <option value="HIDDEN" ${visible == 'HIDDEN' ? 'selected' : ''}>비노출</option>
      </select>
      <input
        class="select-input"
        type="search"
        name="category"
        value="${category}"
        placeholder="카테고리"
      >
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="업소명, 메뉴, 주소 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/recommendations/stores">초기화</a>
    </form>
    <div class="admin-header-actions">
      <span class="admin-badge">총 ${stores.totalElements}개</span>
      <a class="button button-primary" href="/admin/recommendations/stores/new">업소 추가</a>
    </div>
  </section>

  <c:choose>
    <c:when test="${empty stores.content}">
      <div class="empty-state">조건에 맞는 착한가격업소가 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>번호</th>
              <th>업소명</th>
              <th>카테고리</th>
              <th>대표 메뉴</th>
              <th>가격</th>
              <th>주소</th>
              <th>노출</th>
              <th>수정일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="store" items="${stores.content}">
              <tr>
                <td>${store.id}</td>
                <td>${store.name}</td>
                <td>${store.category}</td>
                <td>${store.itemName}</td>
                <td>${store.price}</td>
                <td class="table-content">${store.address}</td>
                <td><span class="status-pill ${store.visibleClass}">${store.visibleLabel}</span></td>
                <td>${store.updatedAt}</td>
                <td>
                  <div class="comment-actions">
                    <a class="button button-ghost" href="/admin/recommendations/stores/${store.id}/edit">수정</a>
                    <form method="post" action="/admin/recommendations/stores/${store.id}/toggle-visible">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                      <button class="button button-ghost" type="submit">노출 변경</button>
                    </form>
                    <form
                      method="post"
                      action="/admin/recommendations/stores/${store.id}/delete"
                      onsubmit="return confirm('착한가격업소를 삭제할까? 추천 후보에서 제거돼.');"
                    >
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                      <button class="button button-danger" type="submit">삭제</button>
                    </form>
                  </div>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="착한가격업소 목록 페이지">
        <c:url var="prevUrl" value="/admin/recommendations/stores">
          <c:param name="keyword" value="${keyword}" />
          <c:param name="category" value="${category}" />
          <c:param name="visible" value="${visible}" />
          <c:param name="page" value="${stores.number - 1}" />
        </c:url>
        <c:url var="nextUrl" value="/admin/recommendations/stores">
          <c:param name="keyword" value="${keyword}" />
          <c:param name="category" value="${category}" />
          <c:param name="visible" value="${visible}" />
          <c:param name="page" value="${stores.number + 1}" />
        </c:url>
        <c:if test="${stores.number > 0}">
          <a class="button button-ghost" href="${prevUrl}">이전</a>
        </c:if>
        <span class="pagination-current">${stores.number + 1} / ${stores.totalPages}</span>
        <c:if test="${stores.number + 1 < stores.totalPages}">
          <a class="button button-ghost" href="${nextUrl}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>

  <section class="panel">
    <h2>추천 노출 기준</h2>
    <p>
      노출 상태인 업소만 추천 후보로 사용하도록 백엔드 추천 API와 연결하면 돼.
      지금 화면은 관리자 수동 관리 테이블을 만들고 운영자가 후보를 관리하는 단계야.
    </p>
  </section>
<%@ include file="../layout/footer.jsp" %>
