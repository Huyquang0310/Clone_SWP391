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
    <title>Inbox Dashboard - SWP391 G1</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg: #0f1117; --surface: #1a1d27; --surface2: #22263a;
            --border: #2e3348; --accent: #245b96; --accent2: #17385f;
            --success: #10b981; --warning: #f59e0b;
            --text: #e2e8f0; --text-muted: #94a3b8; --radius: 12px;
        }

        body { font-family: 'Inter', sans-serif; background: var(--bg); color: var(--text); min-height: 100vh; }

        .header {
            background: var(--surface); border-bottom: 1px solid var(--border);
            padding: 0 2rem; display: flex; align-items: center;
            justify-content: space-between; height: 64px;
            position: sticky; top: 0; z-index: 100;
        }

        .logo { display: flex; align-items: center; gap: 10px; font-weight: 700; color: var(--accent); }
        .logo-icon {
            width: 36px; height: 36px;
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            border-radius: 8px; display: flex; align-items: center; justify-content: center;
        }

        .nav-links { display: flex; gap: 0.5rem; }
        .nav-link {
            color: var(--text-muted); text-decoration: none; font-size: 0.875rem;
            font-weight: 500; padding: 6px 12px; border-radius: 8px; transition: all 0.2s;
        }
        .nav-link:hover { color: var(--text); background: var(--surface2); }
        .nav-link.active { color: var(--text); background: var(--surface2); }

        .unread-dot {
            display: inline-flex; align-items: center; justify-content: center;
            background: #ef4444; color: #fff; border-radius: 20px;
            font-size: 0.65rem; font-weight: 700; min-width: 18px; height: 18px;
            padding: 0 5px; margin-left: 4px;
        }

        .container { max-width: 900px; margin: 0 auto; padding: 2rem; }

        .page-header {
            display: flex; align-items: center; justify-content: space-between;
            margin-bottom: 2rem;
        }

        .page-title {
            font-size: 1.75rem; font-weight: 700;
            background: linear-gradient(135deg, #e2e8f0, var(--accent));
            -webkit-background-clip: text; -webkit-text-fill-color: transparent;
        }

        .page-subtitle { color: var(--text-muted); font-size: 0.875rem; margin-top: 4px; }

        .unread-banner {
            display: flex; align-items: center; gap: 0.75rem;
            background: rgba(36,91,150,0.1); border: 1px solid rgba(36,91,150,0.3);
            border-radius: var(--radius); padding: 1rem 1.5rem; margin-bottom: 1.5rem;
            font-size: 0.875rem;
        }

        .unread-count {
            background: var(--accent); color: #fff; border-radius: 20px;
            padding: 2px 10px; font-weight: 700; font-size: 0.875rem;
        }

        /* Conversation list */
        .conv-list { display: flex; flex-direction: column; gap: 0.875rem; }

        .conv-card {
            display: flex; align-items: center; gap: 1rem;
            background: var(--surface); border: 1px solid var(--border);
            border-radius: var(--radius); padding: 1.25rem 1.5rem;
            text-decoration: none; color: var(--text); transition: all 0.2s;
        }
        .conv-card:hover {
            border-color: var(--accent); transform: translateY(-2px);
            box-shadow: 0 6px 24px rgba(36,91,150,0.12);
        }
        .conv-card.unread { border-color: rgba(36,91,150,0.4); }

        .conv-avatar {
            width: 48px; height: 48px; border-radius: 50%; flex-shrink: 0;
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            display: flex; align-items: center; justify-content: center;
            font-size: 1.1rem; font-weight: 700; color: #fff;
        }

        .conv-info { flex: 1; min-width: 0; }
        .conv-name { font-weight: 700; font-size: 0.95rem; margin-bottom: 4px; }
        .conv-preview {
            font-size: 0.8rem; color: var(--text-muted);
            white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
        }

        .conv-right { display: flex; flex-direction: column; align-items: flex-end; gap: 6px; flex-shrink: 0; }
        .conv-time { font-size: 0.75rem; color: var(--text-muted); }
        .unread-badge {
            background: var(--accent); color: #fff;
            border-radius: 20px; font-size: 0.65rem; font-weight: 700;
            padding: 2px 7px;
        }

        /* Empty */
        .empty-state {
            text-align: center; padding: 4rem;
            background: var(--surface); border: 1px solid var(--border);
            border-radius: var(--radius); color: var(--text-muted);
        }
        .empty-icon { font-size: 3rem; margin-bottom: 1rem; }
    </style>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/support-theme.css">
</head>
<body class="support-page organizer-inbox-page">
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">

<header class="header">
    <div class="logo">
        <div class="logo-icon"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#mail"/></svg></div>
        &nbsp;Organizer Inbox
    </div>
    <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/qa-management" class="nav-link">Q&A</a>
        <a href="${pageContext.request.contextPath}/organizer-inbox" class="nav-link active">
            Inbox
            <c:if test="${unreadCount > 0}">
                <span class="unread-dot">${unreadCount}</span>
            </c:if>
        </a>
    </nav>
</header>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Inbox Dashboard</h1>
            <p class="page-subtitle">Tin nhắn riêng từ sinh viên · ${conversations.size()} cuộc trò chuyện</p>
        </div>
    </div>

    <c:if test="${unreadCount > 0}">
        <div class="unread-banner">
            <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#inbox"/></svg> Có <span class="unread-count">${unreadCount}</span> tin nhắn chưa đọc từ sinh viên
        </div>
    </c:if>

    <div class="conv-list">
        <c:choose>
            <c:when test="${empty conversations}">
                <div class="empty-state">
                    <div class="empty-icon"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#inbox"/></svg></div>
                    <div style="font-weight:600; margin-bottom:0.5rem;">Chưa có tin nhắn nào</div>
                    <div style="font-size:0.875rem;">Khi sinh viên gửi tin nhắn, chúng sẽ xuất hiện ở đây</div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="conv" items="${conversations}">
                    <a href="${pageContext.request.contextPath}/organizer-inbox?action=chat&studentId=${conv.studentId}"
                       class="conv-card ${not conv.read and conv.senderType == 'STUDENT' ? 'unread' : ''}">
                        <div class="conv-avatar">
                            SV
                        </div>
                        <div class="conv-info">
                            <div class="conv-name"><c:out value="${conv.studentName}"/></div>
                            <div class="conv-preview">
                                <c:choose>
                                    <c:when test="${conv.senderType == 'STAFF'}">Bạn: </c:when>
                                    <c:otherwise></c:otherwise>
                                </c:choose>
                                <c:out value="${conv.content}"/>
                            </div>
                        </div>
                        <div class="conv-right">
                            <span class="conv-time">${dt:format(conv.sentAt)}</span>
                            <c:if test="${not conv.read and conv.senderType == 'STUDENT'}">
                                <span class="unread-badge">Mới</span>
                            </c:if>
                        </div>
                    </a>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</main>

        </div>
    </div>
</div>
</body>
</html>
