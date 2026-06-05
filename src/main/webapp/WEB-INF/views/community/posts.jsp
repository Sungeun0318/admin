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
    <form class="search-form" method="get" action="/admin/community/posts">
      <select class="select-input" name="tag">
        <option value="" ${empty tag ? 'selected' : ''}>전체 태그</option>
        <option value="절약팁" ${tag == '절약팁' ? 'selected' : ''}>절약팁</option>
        <option value="질문" ${tag == '질문' ? 'selected' : ''}>질문</option>
        <option value="같이해요" ${tag == '같이해요' ? 'selected' : ''}>같이해요</option>
      </select>
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="제목 또는 내용 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/community/posts">초기화</a>
    </form>
    <span class="admin-badge">총 ${posts.totalElements}개</span>
  </section>

  <c:choose>
    <c:when test="${empty posts.content}">
      <div class="empty-state">조건에 맞는 게시글이 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>게시글 번호</th>
              <th>제목</th>
              <th>태그</th>
              <th>작성자</th>
              <th>댓글</th>
              <th>작성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="post" items="${posts.content}">
              <tr>
                <td>${post.postId}</td>
                <td>${post.title}</td>
                <td><span class="status-pill status-muted">${post.tag}</span></td>
                <td>${post.authorLabel}</td>
                <td>${post.commentCount}</td>
                <td>${post.createdAt}</td>
                <td>
                  <a class="button button-ghost" href="/admin/community/posts/${post.postId}">상세</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="게시글 목록 페이지">
        <c:if test="${posts.number > 0}">
          <a class="button button-ghost" href="/admin/community/posts?keyword=${keyword}&tag=${tag}&page=${posts.number - 1}">이전</a>
        </c:if>
        <span class="pagination-current">${posts.number + 1} / ${posts.totalPages}</span>
        <c:if test="${posts.number + 1 < posts.totalPages}">
          <a class="button button-ghost" href="/admin/community/posts?keyword=${keyword}&tag=${tag}&page=${posts.number + 1}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>
<%@ include file="../layout/footer.jsp" %>
