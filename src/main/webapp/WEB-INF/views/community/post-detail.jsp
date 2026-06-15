<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <div class="detail-actions">
    <a class="button button-ghost" href="/admin/community/posts">목록으로</a>
  </div>

  <section class="detail-grid">
    <article class="panel">
      <div class="panel-header">
        <h2>게시글 본문</h2>
        <span class="status-pill status-muted">${post.tag}</span>
      </div>
      <dl class="detail-list">
        <div>
          <dt>게시글 번호</dt>
          <dd>${post.postId}</dd>
        </div>
        <div>
          <dt>제목</dt>
          <dd>${post.title}</dd>
        </div>
        <div>
          <dt>작성자</dt>
          <dd>${post.authorLabel}</dd>
        </div>
        <div>
          <dt>작성일</dt>
          <dd>${post.createdAt}</dd>
        </div>
      </dl>
      <div class="content-box">${post.content}</div>
    </article>

    <article class="panel">
      <h2>관리 액션</h2>
      <p>
        현재 게시글 status 컬럼이 없어서 삭제는 물리 삭제로 처리됩니다.
        삭제하면 연결된 댓글도 함께 삭제됩니다.
      </p>
      <form
        method="post"
        action="/admin/community/posts/${post.postId}/delete"
        onsubmit="return confirm('게시글과 댓글을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.');"
      >
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <button class="button button-danger" type="submit">게시글 삭제</button>
      </form>
    </article>
  </section>

  <section class="panel">
    <div class="panel-header">
      <h2>댓글 ${post.comments.size()}</h2>
    </div>
    <c:choose>
      <c:when test="${empty post.comments}">
        <div class="mini-empty">댓글이 없습니다.</div>
      </c:when>
      <c:otherwise>
        <div class="comment-list">
          <c:forEach var="comment" items="${post.comments}">
            <article class="comment-item">
              <div class="comment-meta">
                <strong>${comment.authorLabel}</strong>
                <div class="comment-actions">
                  <span>${comment.createdAt}</span>
                  <form
                    method="post"
                    action="/admin/community/comments/delete"
                    onsubmit="return confirm('댓글을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.');"
                  >
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <input type="hidden" name="commentId" value="${comment.commentId}">
                    <input type="hidden" name="postId" value="${post.postId}">
                    <button class="button button-danger mini-button" type="submit">삭제</button>
                  </form>
                </div>
              </div>
              <p>${comment.content}</p>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
<%@ include file="../layout/footer.jsp" %>
