<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>관리자 로그인 | 거지 관리자</title>
  <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
  <main class="login-page">
    <section class="login-card">
      <h1 class="login-title">관리자 로그인</h1>
      <p class="login-description">
        Beggar 관리자 콘솔에 접근하려면 관리자 계정으로 로그인해야 해.
      </p>

      <% if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger">아이디나 비밀번호를 확인해줘.</div>
      <% } %>
      <% if (request.getParameter("logout") != null) { %>
        <div class="alert alert-success">로그아웃됐어.</div>
      <% } %>

      <form method="post" action="/login">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

        <div class="form-field">
          <label for="username">아이디</label>
          <input id="username" name="username" type="text" autocomplete="username" required autofocus>
        </div>

        <div class="form-field">
          <label for="password">비밀번호</label>
          <input id="password" name="password" type="password" autocomplete="current-password" required>
        </div>

        <button class="primary-action" type="submit">로그인</button>
      </form>
    </section>
  </main>
</body>
</html>
