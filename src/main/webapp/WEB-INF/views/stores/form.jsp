<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="detail-actions">
    <a class="button button-ghost" href="/admin/recommendations/stores">목록으로</a>
  </section>

  <section class="panel">
    <h2>${mode == 'edit' ? '업소 수정' : '업소 추가'}</h2>
    <form
      method="post"
      action="${formAction}"
    >
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

      <div class="detail-grid">
        <div>
          <div class="form-field">
            <label for="storeId">외부 업소 ID</label>
            <input id="storeId" name="storeId" value="${form.storeId}" placeholder="공공데이터 ID 또는 내부 코드">
          </div>
          <div class="form-field">
            <label for="name">업소명</label>
            <input id="name" name="name" value="${form.name}" required placeholder="예: 명학분식">
          </div>
          <div class="form-field">
            <label for="category">카테고리</label>
            <input id="category" name="category" value="${form.category}" placeholder="예: 한식">
          </div>
          <div class="form-field">
            <label for="itemName">대표 메뉴</label>
            <input id="itemName" name="itemName" value="${form.itemName}" placeholder="예: 김치찌개">
          </div>
          <div class="form-field">
            <label for="price">가격</label>
            <input id="price" name="price" type="number" min="0" value="${form.price}" placeholder="예: 7000">
          </div>
        </div>

        <div>
          <div class="form-field">
            <label for="address">주소</label>
            <input id="address" name="address" value="${form.address}" required placeholder="예: 경기 안양시 ...">
          </div>
          <div class="form-field">
            <label for="lat">위도</label>
            <input id="lat" name="lat" type="number" step="0.0000001" value="${form.lat}" placeholder="37.0000000">
          </div>
          <div class="form-field">
            <label for="lng">경도</label>
            <input id="lng" name="lng" type="number" step="0.0000001" value="${form.lng}" placeholder="127.0000000">
          </div>
          <div class="form-field">
            <label for="phoneNumber">전화번호</label>
            <input id="phoneNumber" name="phoneNumber" value="${form.phoneNumber}" placeholder="031-000-0000">
          </div>
          <div class="form-field">
            <label for="visible">추천 노출</label>
            <select id="visible" class="select-input" name="visible">
              <option value="true" ${form.visible ? 'selected' : ''}>노출</option>
              <option value="false" ${!form.visible ? 'selected' : ''}>비노출</option>
            </select>
          </div>
        </div>
      </div>

      <div class="action-row">
        <button class="button button-primary" type="submit">${mode == 'edit' ? '수정 저장' : '업소 추가'}</button>
        <a class="button button-ghost" href="/admin/recommendations/stores">취소</a>
      </div>
    </form>
  </section>
<%@ include file="../layout/footer.jsp" %>
