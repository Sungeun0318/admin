<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="detail-actions">
    <a class="button button-ghost" href="/admin/admins">목록으로</a>
  </section>

  <section class="panel">
    <h2>${mode == 'edit' ? '관리자 수정' : '관리자 생성'}</h2>
    <form method="post" action="${formAction}">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

      <div class="detail-grid">
        <div>
          <div class="form-field">
            <label for="username">관리자 아이디</label>
            <input
              id="username"
              name="username"
              value="${form.username}"
              ${mode == 'edit' ? 'readonly' : ''}
              required
              placeholder="admin-id"
            >
          </div>
          <div class="form-field">
            <label for="displayName">표시 이름</label>
            <input id="displayName" name="displayName" value="${form.displayName}" required placeholder="운영 관리자">
          </div>
          <div class="form-field">
            <label for="password">비밀번호</label>
            <input
              id="password"
              name="password"
              type="password"
              ${mode == 'create' ? 'required' : ''}
              placeholder="${mode == 'edit' ? '변경할 때만 입력' : '6자 이상'}"
            >
          </div>
        </div>

        <div>
          <div class="form-field">
            <label for="role">권한</label>
            <select id="role" class="select-input" name="role">
              <option value="SUPER_ADMIN" ${form.role == 'SUPER_ADMIN' ? 'selected' : ''}>최고 관리자</option>
              <option value="OPERATOR" ${form.role == 'OPERATOR' ? 'selected' : ''}>운영자</option>
              <option value="VIEWER" ${form.role == 'VIEWER' ? 'selected' : ''}>조회 전용</option>
            </select>
          </div>
          <div class="form-field">
            <label for="status">상태</label>
            <select id="status" class="select-input" name="status">
              <option value="ACTIVE" ${form.status == 'ACTIVE' ? 'selected' : ''}>활성</option>
              <option value="DISABLED" ${form.status == 'DISABLED' ? 'selected' : ''}>비활성</option>
            </select>
          </div>
        </div>
      </div>

      <div class="action-row">
        <button class="button button-primary" type="submit">${mode == 'edit' ? '수정 저장' : '관리자 생성'}</button>
        <a class="button button-ghost" href="/admin/admins">취소</a>
      </div>
    </form>
  </section>
<%@ include file="../layout/footer.jsp" %>
