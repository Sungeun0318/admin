<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout/head.jsp" %>
<%@ include file="layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="layout/header.jsp" %>

      <section class="toolbar">
        <div></div>
        <a class="button button-primary" href="/admin/insights">통계 보기</a>
      </section>

      <section class="stats-grid" aria-label="운영 지표">
        <article class="stat-card">
          <p class="stat-label">전체 회원 수</p>
          <p class="stat-value">${stats.totalUsers}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">전체 방 수</p>
          <p class="stat-value">${stats.totalRooms}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">진행 중인 방</p>
          <p class="stat-value">${stats.activeRooms}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">종료된 방</p>
          <p class="stat-value">${stats.endedRooms}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">삭제된 방</p>
          <p class="stat-value">${stats.deletedRooms}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">전체 게시글</p>
          <p class="stat-value">${stats.totalPosts}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">전체 댓글</p>
          <p class="stat-value">${stats.totalComments}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">전체 채팅</p>
          <p class="stat-value">${stats.totalChats}</p>
        </article>
        <article class="stat-card">
          <p class="stat-label">전체 영수증</p>
          <p class="stat-value">${stats.totalReceipts}</p>
        </article>
      </section>

      <section class="dashboard-panels" aria-label="최근 운영 데이터">
        <article class="panel">
          <div class="panel-header">
            <h2>최근 가입 회원</h2>
            <a class="button button-ghost" href="/admin/users">전체 보기</a>
          </div>
          <div class="mini-list">
            <c:choose>
              <c:when test="${empty recentUsers}">
                <div class="mini-empty">최근 가입 회원이 없어.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="item" items="${recentUsers}">
                  <div class="mini-item">
                    <div>
                      <p class="mini-title">${item.title}</p>
                      <p class="mini-subtitle">${item.subtitle}</p>
                    </div>
                    <span class="mini-meta">${item.meta}</span>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </article>

        <article class="panel">
          <div class="panel-header">
            <h2>최근 생성 방</h2>
            <a class="button button-ghost" href="/admin/rooms">전체 보기</a>
          </div>
          <div class="mini-list">
            <c:choose>
              <c:when test="${empty recentRooms}">
                <div class="mini-empty">최근 생성 방이 없어.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="item" items="${recentRooms}">
                  <div class="mini-item">
                    <div>
                      <p class="mini-title">${item.title}</p>
                      <p class="mini-subtitle">${item.subtitle}</p>
                    </div>
                    <span class="mini-meta">${item.meta}</span>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </article>

        <article class="panel">
          <div class="panel-header">
            <h2>최근 게시글</h2>
            <a class="button button-ghost" href="/admin/community/posts">전체 보기</a>
          </div>
          <div class="mini-list">
            <c:choose>
              <c:when test="${empty recentPosts}">
                <div class="mini-empty">최근 게시글이 없어.</div>
              </c:when>
              <c:otherwise>
                <c:forEach var="item" items="${recentPosts}">
                  <div class="mini-item">
                    <div>
                      <p class="mini-title">${item.title}</p>
                      <p class="mini-subtitle">${item.subtitle}</p>
                    </div>
                    <span class="mini-meta">${item.meta}</span>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </article>
      </section>

      <section class="panel">
        <h2>운영 기준</h2>
        <p>
          진행 중인 방은 초대중, 예산 입력중, 예산 확정, ACTIVE 상태인 방이고, 종료된 방은 ENDED,
          삭제된 방은 DELETED 상태로 집계해.
        </p>
      </section>
<%@ include file="layout/footer.jsp" %>
