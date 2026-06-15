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
    <form class="search-form" method="get" action="/admin/receipts">
      <input
        class="select-input"
        type="number"
        name="roomNo"
        value="${roomNo}"
        placeholder="방 번호"
        min="1"
      >
      <input
        class="select-input"
        type="number"
        name="roomMemberId"
        value="${roomMemberId}"
        placeholder="멤버 번호"
        min="1"
      >
      <input class="select-input" type="date" name="fromDate" value="${fromDate}">
      <input class="select-input" type="date" name="toDate" value="${toDate}">
      <input
        class="search-input"
        type="search"
        name="keyword"
        value="${keyword}"
        placeholder="상호명, 주소, 착한가격업소 검색"
      >
      <button class="button button-primary" type="submit">검색</button>
      <a class="button button-ghost" href="/admin/receipts">초기화</a>
    </form>
    <span class="admin-badge">총 ${receipts.totalElements}개</span>
  </section>

  <c:choose>
    <c:when test="${empty receipts.content}">
      <div class="empty-state">조건에 맞는 영수증이 없습니다.</div>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>영수증 번호</th>
              <th>방</th>
              <th>업로더</th>
              <th>상호명</th>
              <th>유형</th>
              <th>입력</th>
              <th>OCR</th>
              <th>금액</th>
              <th>착한가격</th>
              <th>발행시간</th>
              <th>등록일</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="receipt" items="${receipts.content}">
              <tr>
                <td>${receipt.receiptId}</td>
                <td>${receipt.roomLabel}</td>
                <td>${receipt.uploaderLabel}</td>
                <td class="table-content">${receipt.storeName}</td>
                <td>${receipt.receiptTypeLabel}</td>
                <td>${receipt.inputMethodLabel}</td>
                <td>${receipt.ocrStatusLabel}</td>
                <td>${receipt.amount}</td>
                <td>${receipt.goodPriceLabel}</td>
                <td>${receipt.receiptIssuedAt}</td>
                <td>${receipt.createdAt}</td>
                <td>
                  <div class="comment-actions">
                    <a class="button button-ghost" href="/admin/receipts/${receipt.receiptId}">상세</a>
                    <form
                      method="post"
                      action="/admin/receipts/delete"
                      onsubmit="return confirm('영수증을 삭제하시겠습니까? 방 지출 합계에 영향이 있을 수 있습니다.');"
                    >
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                      <input type="hidden" name="receiptId" value="${receipt.receiptId}">
                      <input type="hidden" name="keyword" value="${keyword}">
                      <input type="hidden" name="roomNo" value="${roomNo}">
                      <input type="hidden" name="roomMemberId" value="${roomMemberId}">
                      <input type="hidden" name="fromDate" value="${fromDate}">
                      <input type="hidden" name="toDate" value="${toDate}">
                      <button class="button button-danger" type="submit">삭제</button>
                    </form>
                  </div>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <nav class="pagination" aria-label="영수증 목록 페이지">
        <c:url var="prevUrl" value="/admin/receipts">
          <c:param name="keyword" value="${keyword}" />
          <c:param name="roomNo" value="${roomNo}" />
          <c:param name="roomMemberId" value="${roomMemberId}" />
          <c:param name="fromDate" value="${fromDate}" />
          <c:param name="toDate" value="${toDate}" />
          <c:param name="page" value="${receipts.number - 1}" />
        </c:url>
        <c:url var="nextUrl" value="/admin/receipts">
          <c:param name="keyword" value="${keyword}" />
          <c:param name="roomNo" value="${roomNo}" />
          <c:param name="roomMemberId" value="${roomMemberId}" />
          <c:param name="fromDate" value="${fromDate}" />
          <c:param name="toDate" value="${toDate}" />
          <c:param name="page" value="${receipts.number + 1}" />
        </c:url>
        <c:if test="${receipts.number > 0}">
          <a class="button button-ghost" href="${prevUrl}">이전</a>
        </c:if>
        <span class="pagination-current">${receipts.number + 1} / ${receipts.totalPages}</span>
        <c:if test="${receipts.number + 1 < receipts.totalPages}">
          <a class="button button-ghost" href="${nextUrl}">다음</a>
        </c:if>
      </nav>
    </c:otherwise>
  </c:choose>

<%@ include file="../layout/footer.jsp" %>
