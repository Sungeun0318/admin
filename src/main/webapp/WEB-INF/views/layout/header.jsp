<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<header class="admin-header">
  <div>
    <h1 class="admin-title">${pageTitle}</h1>
    <p class="admin-page-description">${pageDescription}</p>
  </div>

  <div class="admin-header-actions">
    <span class="admin-badge">8081 관리자 서버</span>
    <form method="post" action="/logout">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <button class="logout-button" type="submit">로그아웃</button>
    </form>
  </div>
</header>
