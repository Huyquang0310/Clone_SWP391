<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat với ${student.name} - SWP391 G1</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg: #0f1117; --surface: #1a1d27; --surface2: #22263a;
            --border: #2e3348; --accent: #245b96; --accent2: #17385f;
            --text: #e2e8f0; --text-muted: #94a3b8; --radius: 12px;
        }

        body { font-family: 'Inter', sans-serif; background: var(--bg); color: var(--text); height: 100vh; display: flex; flex-direction: column; }

        .header {
            background: var(--surface); border-bottom: 1px solid var(--border);
            padding: 0 2rem; display: flex; align-items: center;
            justify-content: space-between; height: 64px; flex-shrink: 0;
        }

        .header-left { display: flex; align-items: center; gap: 0.875rem; }

        .btn-back {
            color: var(--text-muted); text-decoration: none; font-size: 0.875rem;
            padding: 6px 12px; border-radius: 8px; border: 1px solid var(--border);
            background: var(--surface2); transition: all 0.2s;
        }
        .btn-back:hover { color: var(--text); border-color: var(--accent); }

        .student-avatar {
            width: 40px; height: 40px; border-radius: 50%;
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            display: flex; align-items: center; justify-content: center;
            font-weight: 700; color: #fff;
        }

        .contact-info .contact-name { font-weight: 700; font-size: 0.95rem; }
        .contact-info .contact-sub { font-size: 0.75rem; color: var(--text-muted); }

        .chat-wrapper { flex: 1; display: flex; flex-direction: column; max-width: 800px; width: 100%; margin: 0 auto; padding: 0 1rem; overflow: hidden; }

        .messages-area { flex: 1; overflow-y: auto; padding: 1.5rem 0; display: flex; flex-direction: column; gap: 0.875rem; }

        .msg-row { display: flex; align-items: flex-end; gap: 0.5rem; }
        .msg-row.me { flex-direction: row-reverse; }

        .msg-avatar {
            width: 30px; height: 30px; border-radius: 50%; flex-shrink: 0;
            display: flex; align-items: center; justify-content: center;
            font-size: 0.75rem; font-weight: 700; color: #fff;
        }
        .msg-avatar.student-av { background: linear-gradient(135deg, var(--accent), var(--accent2)); }
        .msg-avatar.staff-av { background: linear-gradient(135deg, #10b981, #059669); }

        .bubble {
            max-width: 65%; padding: 0.875rem 1.125rem; border-radius: 18px;
            font-size: 0.875rem; line-height: 1.6;
        }
        .bubble.me { background: linear-gradient(135deg, #10b981, #059669); color: #fff; border-bottom-right-radius: 4px; }
        .bubble.other { background: var(--surface2); color: var(--text); border: 1px solid var(--border); border-bottom-left-radius: 4px; }

        .bubble-time { font-size: 0.7rem; opacity: 0.7; margin-top: 4px; }
        .bubble.me .bubble-time { text-align: right; }

        .empty-chat { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--text-muted); gap: 0.75rem; }
        .empty-icon { font-size: 3rem; }

        .input-area { background: var(--surface); border-top: 1px solid var(--border); padding: 1rem 0 1.5rem; }
        .input-form { display: flex; gap: 0.75rem; align-items: flex-end; }

        textarea {
            flex: 1; padding: 0.875rem 1rem; background: var(--surface2);
            border: 1px solid var(--border); border-radius: 12px;
            color: var(--text); font-family: 'Inter', sans-serif;
            font-size: 0.875rem; resize: none; min-height: 48px; max-height: 140px;
            line-height: 1.5; transition: border-color 0.2s;
        }
        textarea:focus { outline: none; border-color: #10b981; }
        textarea::placeholder { color: var(--text-muted); }

        .btn-send {
            width: 48px; height: 48px; border-radius: 12px; flex-shrink: 0;
            background: linear-gradient(135deg, #10b981, #059669);
            border: none; cursor: pointer; font-size: 1.25rem;
            transition: all 0.2s; display: flex; align-items: center; justify-content: center;
        }
        .btn-send:hover { opacity: 0.9; transform: scale(1.05); }

        .char-note { font-size: 0.75rem; color: var(--text-muted); margin-top: 6px; }

        /* Student info pill */
        .student-info-pill {
            background: var(--surface2); border: 1px solid var(--border);
            border-radius: 20px; padding: 4px 14px;
            font-size: 0.75rem; color: var(--text-muted);
        }
    </style>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/support-theme.css">
</head>
<body class="support-page organizer-chat-page">
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">

<header class="header">
    <div class="header-left">
        <a href="${pageContext.request.contextPath}/organizer-inbox" class="btn-back" aria-label="Quay lại"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg></a>
        <div class="student-avatar">SV</div>
        <div class="contact-info">
            <div class="contact-name"><c:out value="${student.name}"/></div>
            <div class="contact-sub">Sinh viên · <c:out value="${student.code}"/> · <c:out value="${student.email}"/></div>
        </div>
    </div>
    <div class="student-info-pill">Hỗ trợ 1:1</div>
</header>

<div class="chat-wrapper">
    <div class="messages-area" id="messagesArea">
        <c:choose>
            <c:when test="${empty messages}">
                <div class="empty-chat">
                    <div class="empty-icon"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#chat"/></svg></div>
                    <div>Sinh viên chưa gửi tin nhắn nào</div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="msg" items="${messages}">
                    <c:set var="isMe" value="${msg.senderType == 'STAFF'}"/>
                    <div class="msg-row ${isMe ? 'me' : ''}">
                        <div class="msg-avatar ${isMe ? 'staff-av' : 'student-av'}">
                            ${isMe ? 'NV' : 'SV'}
                        </div>
                        <div class="bubble ${isMe ? 'me' : 'other'}">
                            <c:out value="${msg.content}"/>
                            <div class="bubble-time">${dt:format(msg.sentAt)}</div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="input-area">
        <form method="post" action="${pageContext.request.contextPath}/organizer-inbox" class="input-form" id="replyForm">
            <input type="hidden" name="action" value="reply">
            <input type="hidden" name="studentId" value="${student.id}">
            <textarea name="content" id="msgInput"
                      placeholder="Nhập câu trả lời cho sinh viên..."
                      rows="1" maxlength="4000" required onkeydown="handleKey(event)" oninput="autoResize(this)"></textarea>
            <button type="submit" class="btn-send" title="Gửi" aria-label="Gửi tin nhắn"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#send"/></svg></button>
        </form>
        <div class="char-note">Nhấn Enter để gửi · Shift+Enter để xuống dòng</div>
    </div>
</div>

<script>
    const area = document.getElementById('messagesArea');
    area.scrollTop = area.scrollHeight;

    function handleKey(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            document.getElementById('replyForm').submit();
        }
    }

    function autoResize(el) {
        el.style.height = 'auto';
        el.style.height = Math.min(el.scrollHeight, 140) + 'px';
    }
</script>

        </div>
    </div>
</div>
</body>
</html>
