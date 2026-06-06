<aside class="admin-sidebar">
  <div class="admin-logo">거지 관리자</div>
  <div class="admin-subtitle">Beggar Admin Console</div>

  <nav class="admin-nav" aria-label="관리자 메뉴">
    <a class="${activeMenu == 'dashboard' ? 'active' : ''}" href="/admin">대시보드</a>
    <a class="${activeMenu == 'users' ? 'active' : ''}" href="/admin/users">회원 관리</a>
    <a class="${activeMenu == 'rooms' ? 'active' : ''}" href="/admin/rooms">방 관리</a>
    <a class="${activeMenu == 'posts' ? 'active' : ''}" href="/admin/community/posts">게시글 관리</a>
    <a class="${activeMenu == 'comments' ? 'active' : ''}" href="/admin/community/comments">댓글 관리</a>
    <a class="${activeMenu == 'chats' ? 'active' : ''}" href="/admin/chats">채팅 관리</a>
    <a class="${activeMenu == 'receipts' ? 'active' : ''}" href="/admin/receipts">영수증 관리</a>
    <a class="${activeMenu == 'stores' ? 'active' : ''}" href="/admin/recommendations/stores">착한가격업소</a>
    <a class="${activeMenu == 'admins' ? 'active' : ''}" href="/admin/admins">관리자 계정</a>
    <a class="${activeMenu == 'logs' ? 'active' : ''}" href="/admin/logs">운영 로그</a>
  </nav>
</aside>
