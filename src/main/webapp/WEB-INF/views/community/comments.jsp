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
    <form class="search-form" method="get" action="/admin/community/comments">
      <input
        class="select-input"
        type="number"
        name="postId"
        value="${postId}"
        placeholder="게시글 번호"
        min="1"
      >
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="댓글 내용 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/community/comments">초기화</a>
    </form>
    <span class="admin-badge">총 ${comments.totalElements}개</span>
  </section>

  <c:choose>
    <c:when test="${empty comments.content}">
      <div class="empty-state">조건에 맞는 댓글이 없어.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>댓글 번호</th>
              <th>게시글</th>
              <th>작성자</th>
              <th>내용</th>
              <th>작성일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="comment" items="${comments.content}">
              <tr>
                <td>${comment.commentId}</td>
                <td>
                  <a class="table-link" href="/admin/community/posts/${comment.postId}">
                    #${comment.postId} ${comment.postTitle}
                  </a>
                </td>
                <td>${comment.authorLabel}</td>
                <td class="table-content">${comment.content}</td>
                <td>${comment.createdAt}</td>
                <td>
                  <form
                    method="post"
                    action="/admin/community/comments/delete"
                    onsubmit="return confirm('댓글을 삭제할까? 이 작업은 되돌릴 수 없어.');"
                  >
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <input type="hidden" name="commentId" value="${comment.commentId}">
                    <input type="hidden" name="keyword" value="${keyword}">
                    <input type="hidden" name="postId" value="${postId}">
                    <button class="button button-danger" type="submit">삭제</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="댓글 목록 페이지">
        <c:if test="${comments.number > 0}">
          <a class="button button-ghost" href="/admin/community/comments?keyword=${keyword}&postId=${postId}&page=${comments.number - 1}">이전</a>
        </c:if>
        <span class="pagination-current">${comments.number + 1} / ${comments.totalPages}</span>
        <c:if test="${comments.number + 1 < comments.totalPages}">
          <a class="button button-ghost" href="/admin/community/comments?keyword=${keyword}&postId=${postId}&page=${comments.number + 1}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>
<%@ include file="../layout/footer.jsp" %>
