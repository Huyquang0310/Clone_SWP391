<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hộp thư của tôi | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student-inbox.css">
</head>
<body class="support-page student-inbox-page">
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<a class="skip-link" href="#inbox-content">Đi đến nội dung chính</a>
<main class="inbox-page" id="inbox-content">
    <header class="activity-hero inbox-hero">
        <div class="hero-copy activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> HỖ TRỢ SINH VIÊN</span>
            <h1>Hộp thư của tôi</h1>
            <p>Trao đổi riêng với ban tổ chức hoặc nhân viên hỗ trợ.</p>
        </div>
        <nav class="hero-nav" aria-label="Điều hướng hộp thư">
            <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
            <a href="${pageContext.request.contextPath}/student-qa">Hỏi &amp; Đáp</a>
            <a class="active" href="${pageContext.request.contextPath}/student-inbox" aria-current="page">Hộp thư</a>
        </nav>
    </header>

    <section class="inbox-shell" aria-labelledby="inbox-title">
        <div class="shell-heading">
            <div>
                <h2 id="inbox-title">Tin nhắn hỗ trợ</h2>
                <p>Chọn người nhận để bắt đầu hoặc tiếp tục cuộc trò chuyện.</p>
            </div>
            <span class="staff-count">
                <c:choose>
                    <c:when test="${empty staffList}">0 người nhận</c:when>
                    <c:otherwise><c:out value="${staffList.size()}"/> người nhận</c:otherwise>
                </c:choose>
            </span>
        </div>

        <div class="inbox-layout">
            <aside class="staff-sidebar" aria-labelledby="recipient-title">
                <div class="panel-heading">
                    <span class="panel-mark" aria-hidden="true">01</span>
                    <div><h3 id="recipient-title">Chọn người nhận</h3><p>Nhân viên đang hỗ trợ</p></div>
                </div>
                <div class="staff-list">
                    <c:choose>
                        <c:when test="${empty staffList}">
                            <div class="compact-empty"><strong>Chưa có nhân viên</strong><span>Danh sách người nhận hiện đang trống.</span></div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="staff" items="${staffList}">
                                <a href="${pageContext.request.contextPath}/student-inbox?action=chat&amp;staffId=${staff.id}"
                                   class="staff-item ${selectedStaff.id == staff.id ? 'active' : ''}">
                                    <span class="staff-avatar" aria-hidden="true">NV</span>
                                    <span class="staff-info"><strong><c:out value="${staff.name}"/></strong><small><c:out value="${staff.code}"/></small></span>
                                    <span class="arrow" aria-hidden="true"><svg class="ui-icon"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-right"/></svg></span>
                                </a>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </aside>

            <section class="messages-panel" aria-labelledby="recent-title">
                <c:choose>
                    <c:when test="${not empty selectedStaff}">
                        <div class="panel-heading conversation-heading">
                            <span class="msg-avatar" aria-hidden="true">NV</span>
                            <div>
                                <h3 id="recent-title"><c:out value="${selectedStaff.name}"/></h3>
                                <p>Nhân viên hỗ trợ · <c:out value="${selectedStaff.code}"/></p>
                            </div>
                            <a class="close-chat" href="${pageContext.request.contextPath}/student-inbox" aria-label="Đóng cuộc trò chuyện">×</a>
                        </div>
                        <div class="inline-chat">
                            <div class="chat-messages" id="messagesArea">
                                <c:choose>
                                    <c:when test="${empty messages}">
                                        <div class="empty-panel chat-empty">
                                            <span class="empty-symbol" aria-hidden="true"><svg class="ui-icon"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#send"/></svg></span>
                                            <strong>Hãy gửi tin nhắn đầu tiên</strong>
                                            <p>Nội dung trao đổi chỉ hiển thị với bạn và nhân viên hỗ trợ.</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="msg" items="${messages}">
                                            <c:set var="isMine" value="${msg.senderType == 'STUDENT'}"/>
                                            <div class="chat-row ${isMine ? 'mine' : 'theirs'}">
                                                <div class="chat-bubble ${isMine ? 'mine' : 'theirs'}">
                                                    <c:out value="${msg.content}"/>
                                                    <time>${dt:format(msg.sentAt)}</time>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="chat-composer">
                                <form method="post" action="${pageContext.request.contextPath}/student-inbox" id="inlineChatForm">
                                    <input type="hidden" name="action" value="send">
                                    <input type="hidden" name="staffId" value="${selectedStaff.id}">
                                    <label class="sr-only" for="messageContent">Nội dung tin nhắn</label>
                                    <textarea id="messageContent" name="content" maxlength="4000" required
                                              placeholder="Nhập tin nhắn riêng tư của bạn..."
                                              rows="1" onkeydown="submitOnEnter(event)"></textarea>
                                    <button type="submit" aria-label="Gửi tin nhắn">Gửi</button>
                                </form>
                                <small>Nhấn Enter để gửi · Shift+Enter để xuống dòng</small>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="panel-heading">
                            <span class="panel-mark" aria-hidden="true">02</span>
                            <div><h3 id="recent-title">Tin nhắn gần đây</h3><p>Các trao đổi mới nhất của bạn</p></div>
                        </div>
                        <c:choose>
                            <c:when test="${empty recentMessages}">
                                <div class="empty-panel">
                                    <span class="empty-symbol" aria-hidden="true"><svg class="ui-icon"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#send"/></svg></span>
                                    <strong>Bạn chưa gửi tin nhắn nào</strong>
                                    <p>Chọn một nhân viên ở danh sách bên trái để bắt đầu trao đổi.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="recent-list">
                                    <c:forEach var="msg" items="${recentMessages}">
                                        <a class="recent-item" href="${pageContext.request.contextPath}/student-inbox?action=chat&amp;staffId=${msg.staffId}">
                                            <span class="msg-avatar" aria-hidden="true">NV</span>
                                            <span class="msg-info">
                                                <span class="msg-to">Gửi tới <strong><c:out value="${msg.staffName}"/></strong> · <c:out value="${msg.staffCode}"/></span>
                                                <span class="msg-content"><c:out value="${msg.content}"/></span>
                                            </span>
                                            <time class="msg-date">${dt:format(msg.sentAt)}</time>
                                        </a>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </section>
</main>
<script>
    const messagesArea = document.getElementById('messagesArea');
    if (messagesArea) messagesArea.scrollTop = messagesArea.scrollHeight;

    function submitOnEnter(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            event.currentTarget.form.requestSubmit();
        }
    }
</script>
        </div>
    </div>
</div>
</body>
</html>
