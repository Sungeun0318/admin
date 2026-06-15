<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="../layout/head.jsp" %>
<%@ include file="../layout/sidebar.jsp" %>

<main class="admin-main">
  <%@ include file="../layout/header.jsp" %>

  <section class="detail-actions">
    <a class="button button-ghost" href="/admin/receipts">목록으로</a>
  </section>

  <section class="detail-grid">
    <article class="panel">
      <h2>기본 정보</h2>
      <dl class="detail-list">
        <div>
          <dt>영수증 번호</dt>
          <dd>${receipt.receiptId}</dd>
        </div>
        <div>
          <dt>방</dt>
          <dd>${receipt.roomLabel}</dd>
        </div>
        <div>
          <dt>업로더</dt>
          <dd>${receipt.uploaderLabel}</dd>
        </div>
        <div>
          <dt>유형</dt>
          <dd>${receipt.receiptTypeLabel}</dd>
        </div>
        <div>
          <dt>입력 방식</dt>
          <dd>${receipt.inputMethodLabel}</dd>
        </div>
        <div>
          <dt>OCR 상태</dt>
          <dd>${receipt.ocrStatusLabel}</dd>
        </div>
        <div>
          <dt>발행시간</dt>
          <dd>${receipt.receiptIssuedAt}</dd>
        </div>
        <div>
          <dt>등록일</dt>
          <dd>${receipt.createdAt}</dd>
        </div>
      </dl>
    </article>

    <article class="panel">
      <h2>금액 정보</h2>
      <dl class="detail-list">
        <div>
          <dt>상호명</dt>
          <dd>${receipt.storeName}</dd>
        </div>
        <div>
          <dt>총 금액</dt>
          <dd>${receipt.totalAmount}</dd>
        </div>
        <div>
          <dt>반영 금액</dt>
          <dd>${receipt.amount}</dd>
        </div>
        <div>
          <dt>주소</dt>
          <dd>${receipt.address}</dd>
        </div>
      </dl>
    </article>
  </section>

  <section class="detail-grid">
    <article class="panel">
      <h2>착한가격업소 매칭</h2>
      <dl class="detail-list">
        <div>
          <dt>매칭 여부</dt>
          <dd>${receipt.goodPriceMatchedLabel}</dd>
        </div>
        <div>
          <dt>업소 ID</dt>
          <dd>${receipt.goodPriceStoreId}</dd>
        </div>
        <div>
          <dt>업소명</dt>
          <dd>${receipt.goodPriceStoreName}</dd>
        </div>
        <div>
          <dt>업소 주소</dt>
          <dd>${receipt.goodPriceStoreAddress}</dd>
        </div>
        <div>
          <dt>매칭 점수</dt>
          <dd>${receipt.goodPriceMatchScore}</dd>
        </div>
        <div>
          <dt>매칭 사유</dt>
          <dd>${receipt.goodPriceMatchReason}</dd>
        </div>
        <div>
          <dt>검증일</dt>
          <dd>${receipt.goodPriceVerifiedAt}</dd>
        </div>
      </dl>
    </article>

    <article class="panel">
      <h2>이미지</h2>
      <c:choose>
        <c:when test="${receipt.imageUrl == '-'}">
          <div class="empty-state">등록된 이미지가 없습니다.</div>
        </c:when>
        <c:otherwise>
          <img src="${receipt.imageUrl}" alt="영수증 이미지" style="max-width:100%;border-radius:6px;margin-bottom:12px;">
          <div class="action-row">
            <a class="button button-primary" href="${receipt.imageUrl}" target="_blank" rel="noreferrer">이미지 열기</a>
          </div>
        </c:otherwise>
      </c:choose>
    </article>
  </section>

  <section class="panel">
    <h2>관리 액션</h2>
    <p>삭제 작업은 되돌릴 수 없습니다.</p>
    <form
      class="action-row"
      method="post"
      action="/admin/receipts/delete"
      onsubmit="return confirm('영수증을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.');"
    >
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <input type="hidden" name="receiptId" value="${receipt.receiptId}">
      <button class="button button-danger" type="submit">영수증 삭제</button>
    </form>
  </section>
<%@ include file="../layout/footer.jsp" %>
