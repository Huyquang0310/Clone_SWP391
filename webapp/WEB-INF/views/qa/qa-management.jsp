<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="activeNav" value="" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Q&A - SWP391 G1</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg: #0f1117;
            --surface: #1a1d27;
            --surface2: #22263a;
            --border: #2e3348;
            --accent: #245b96;
            --accent2: #17385f;
            --success: #10b981;
            --warning: #f59e0b;
            --danger: #ef4444;
            --text: #e2e8f0;
            --text-muted: #94a3b8;
            --pending: #f59e0b;
            --answered: #10b981;
            --radius: 12px;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: var(--bg);
            color: var(--text);
            min-height: 100vh;
        }

        /* Header */
        .header {
            background: var(--surface);
            border-bottom: 1px solid var(--border);
            padding: 0 2rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 64px;
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 10px;
            font-weight: 700;
            font-size: 1.1rem;
            color: var(--accent);
        }

        .logo-icon {
            width: 36px; height: 36px;
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.1rem;
        }

        .nav-links { display: flex; gap: 1rem; align-items: center; }
        .nav-link {
            color: var(--text-muted); text-decoration: none;
            font-size: 0.875rem; font-weight: 500;
            padding: 6px 12px; border-radius: 8px;
            transition: all 0.2s;
        }
        .nav-link:hover, .nav-link.active {
            color: var(--text); background: var(--surface2);
        }

        /* Main */
        .container { max-width: 1200px; margin: 0 auto; padding: 2rem; }

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

        /* Filter tabs */
        .filter-tabs {
            display: flex; gap: 0.5rem;
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 6px;
            margin-bottom: 1.5rem;
            width: fit-content;
        }

        .filter-tab {
            padding: 8px 20px; border-radius: 8px;
            font-size: 0.875rem; font-weight: 500;
            text-decoration: none; color: var(--text-muted);
            transition: all 0.2s;
        }

        .filter-tab.active, .filter-tab:hover {
            background: var(--accent); color: #fff;
        }

        /* Stats bar */
        .stats-bar {
            display: grid; grid-template-columns: repeat(3, 1fr);
            gap: 1rem; margin-bottom: 2rem;
        }

        .stat-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1.25rem 1.5rem;
            display: flex; align-items: center; gap: 1rem;
        }

        .stat-icon {
            width: 44px; height: 44px; border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.25rem;
        }

        .stat-icon.total { background: rgba(36,91,150,0.15); }
        .stat-icon.pending { background: rgba(245,158,11,0.15); }
        .stat-icon.answered { background: rgba(16,185,129,0.15); }

        .stat-value { font-size: 1.5rem; font-weight: 700; }
        .stat-label { font-size: 0.75rem; color: var(--text-muted); }

        /* Question list */
        .question-list { display: flex; flex-direction: column; gap: 1rem; }

        .question-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1.5rem;
            transition: all 0.2s;
            cursor: pointer;
            text-decoration: none;
            display: block;
            position: relative;
            overflow: hidden;
        }

        .question-card::before {
            content: '';
            position: absolute; left: 0; top: 0; bottom: 0;
            width: 4px;
            background: var(--accent);
            opacity: 0;
            transition: opacity 0.2s;
        }

        .question-card.pending::before { background: var(--pending); opacity: 1; }
        .question-card.answered::before { background: var(--answered); opacity: 1; }

        .question-card:hover {
            border-color: var(--accent);
            transform: translateY(-2px);
            box-shadow: 0 8px 30px rgba(36,91,150,0.15);
        }

        .q-header { display: flex; align-items: flex-start; justify-content: space-between; gap: 1rem; }

        .q-title {
            font-size: 1rem; font-weight: 600; color: var(--text);
            margin-bottom: 0.5rem;
        }

        .q-content {
            font-size: 0.875rem; color: var(--text-muted);
            display: -webkit-box; -webkit-line-clamp: 2;
            -webkit-box-orient: vertical; overflow: hidden;
        }

        .q-meta {
            display: flex; align-items: center; gap: 1rem;
            margin-top: 1rem; flex-wrap: wrap;
        }

        .badge {
            display: inline-flex; align-items: center; gap: 4px;
            padding: 4px 10px; border-radius: 20px;
            font-size: 0.75rem; font-weight: 600;
        }

        .badge-pending { background: rgba(245,158,11,0.15); color: var(--pending); }
        .badge-answered { background: rgba(16,185,129,0.15); color: var(--answered); }
        .badge-anon { background: rgba(36,91,150,0.15); color: var(--accent); }

        .q-author { font-size: 0.8rem; color: var(--text-muted); }
        .q-date { font-size: 0.8rem; color: var(--text-muted); margin-left: auto; }

        /* Empty state */
        .empty-state {
            text-align: center; padding: 4rem 2rem;
            background: var(--surface); border: 1px solid var(--border);
            border-radius: var(--radius);
        }

        .empty-icon { font-size: 3rem; margin-bottom: 1rem; }
        .empty-title { font-size: 1.1rem; font-weight: 600; margin-bottom: 0.5rem; }
        .empty-desc { color: var(--text-muted); font-size: 0.875rem; }

        @media (max-width: 768px) {
            .stats-bar { grid-template-columns: 1fr; }
            .container { padding: 1rem; }
        }
    </style>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/support-theme.css">
</head>
<body class="support-page qa-management-page">
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">

<header class="header">
    <div class="logo">
        <div class="logo-icon"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#chat"/></svg></div>
        Q&A Management
    </div>
    <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/qa-management" class="nav-link active">Q&A</a>
        <a href="${pageContext.request.contextPath}/home" class="nav-link">Trang chủ</a>
    </nav>
</header>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Quản lý Hỏi & Đáp</h1>
            <p class="page-subtitle">Xem và trả lời câu hỏi từ sinh viên</p>
        </div>
    </div>

    <%-- Stats --%>
    <div class="stats-bar">
        <div class="stat-card">
            <div class="stat-icon total"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#chart"/></svg></div>
            <div>
                <div class="stat-value">${questions.size()}</div>
                <div class="stat-label">Tổng câu hỏi</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon pending"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#clock"/></svg></div>
            <div>
                <div class="stat-value">
                    <c:set var="pendingCount" value="0"/>
                    <c:forEach var="q" items="${questions}">
                        <c:if test="${q.status == 'PENDING'}">
                            <c:set var="pendingCount" value="${pendingCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${pendingCount}
                </div>
                <div class="stat-label">Chưa trả lời</div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon answered"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check-circle"/></svg></div>
            <div>
                <div class="stat-value">
                    <c:set var="answeredCount" value="0"/>
                    <c:forEach var="q" items="${questions}">
                        <c:if test="${q.status == 'ANSWERED'}">
                            <c:set var="answeredCount" value="${answeredCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${answeredCount}
                </div>
                <div class="stat-label">Đã trả lời</div>
            </div>
        </div>
    </div>

    <%-- Filter tabs --%>
    <div class="filter-tabs">
        <a href="${pageContext.request.contextPath}/qa-management"
           class="filter-tab ${filter == 'all' ? 'active' : ''}">Tất cả</a>
        <a href="${pageContext.request.contextPath}/qa-management?filter=pending"
           class="filter-tab ${filter == 'pending' ? 'active' : ''}"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#clock"/></svg> Chờ trả lời</a>
    </div>

    <%-- Question list --%>
    <div class="question-list">
        <c:choose>
            <c:when test="${empty questions}">
                <div class="empty-state">
                    <div class="empty-icon"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check-circle"/></svg></div>
                    <div class="empty-title">Không có câu hỏi nào</div>
                    <div class="empty-desc">Tất cả câu hỏi đã được trả lời!</div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="q" items="${questions}">
                    <a href="${pageContext.request.contextPath}/qa-management?action=view&id=${q.id}"
                       class="question-card ${q.status.toLowerCase()}">
                        <div class="q-header">
                            <div style="flex:1">
                                <div class="q-title"><c:out value="${q.title}"/></div>
                                <div class="q-content"><c:out value="${q.content}"/></div>
                            </div>
                        </div>
                        <div class="q-meta">
                            <c:choose>
                                <c:when test="${q.status == 'PENDING'}">
                                    <span class="badge badge-pending"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#clock"/></svg> Chờ trả lời</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-answered"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check"/></svg> Đã trả lời</span>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${q.anonymous}">
                                    <span class="badge badge-anon"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#lock"/></svg> Ẩn danh</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="q-author"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#user"/></svg> <c:out value="${q.studentName}"/> (<c:out value="${q.studentCode}"/>)</span>
                                </c:otherwise>
                            </c:choose>
                            <span class="q-date"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#clock"/></svg> ${dt:format(q.createdAt)}</span>
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
