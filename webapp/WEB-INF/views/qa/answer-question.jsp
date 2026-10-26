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
    <title>Trả lời câu hỏi - SWP391 G1</title>
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
            --radius: 12px;
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
            border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.1rem;
        }

        .btn-back {
            display: flex; align-items: center; gap: 6px;
            color: var(--text-muted); text-decoration: none;
            font-size: 0.875rem; font-weight: 500;
            padding: 8px 16px; border-radius: 8px;
            border: 1px solid var(--border); background: var(--surface2);
            transition: all 0.2s;
        }
        .btn-back:hover { color: var(--text); border-color: var(--accent); }

        .container { max-width: 860px; margin: 0 auto; padding: 2rem; }

        /* Toast notification */
        .toast {
            position: fixed; top: 80px; right: 20px; z-index: 200;
            padding: 12px 20px; border-radius: 10px; font-size: 0.875rem;
            font-weight: 500; animation: slideIn 0.3s ease;
            box-shadow: 0 8px 30px rgba(0,0,0,0.3);
        }
        .toast-success { background: var(--success); color: #fff; }
        .toast-error { background: var(--danger); color: #fff; }
        .toast-published { background: var(--accent); color: #fff; }
        .toast-deleted { background: #6b7280; color: #fff; }

        @keyframes slideIn { from { transform: translateX(100px); opacity: 0; } to { transform: translateX(0); opacity: 1; } }

        /* Question card */
        .question-section {
            background: var(--surface); border: 1px solid var(--border);
            border-radius: var(--radius); padding: 2rem; margin-bottom: 2rem;
            position: relative; overflow: hidden;
        }

        .question-section::before {
            content: ''; position: absolute; left: 0; top: 0; bottom: 0;
            width: 4px; background: linear-gradient(180deg, var(--accent), var(--accent2));
        }

        .q-label {
            display: inline-flex; align-items: center; gap: 6px;
            background: rgba(36,91,150,0.15); color: var(--accent);
            padding: 4px 12px; border-radius: 20px; font-size: 0.75rem;
            font-weight: 600; margin-bottom: 1rem;
        }

        .q-title {
            font-size: 1.4rem; font-weight: 700; margin-bottom: 1rem;
            line-height: 1.4;
        }

        .q-content {
            font-size: 0.95rem; color: var(--text-muted); line-height: 1.7;
            margin-bottom: 1.5rem;
        }

        .q-meta { display: flex; gap: 1.5rem; flex-wrap: wrap; }

        .meta-item { display: flex; align-items: center; gap: 6px; font-size: 0.8rem; color: var(--text-muted); }

        .badge {
            display: inline-flex; align-items: center; gap: 4px;
            padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600;
        }
        .badge-pending { background: rgba(245,158,11,0.15); color: #f59e0b; }
        .badge-answered { background: rgba(16,185,129,0.15); color: #10b981; }

        /* Divider */
        .divider {
            display: flex; align-items: center; gap: 1rem;
            margin: 2rem 0; color: var(--text-muted); font-size: 0.875rem; font-weight: 500;
        }
        .divider::before, .divider::after {
            content: ''; flex: 1; height: 1px; background: var(--border);
        }

        /* Answer cards */
        .answers-section { margin-bottom: 2rem; }

        .answer-card {
            background: var(--surface); border: 1px solid var(--border);
            border-radius: var(--radius); padding: 1.5rem; margin-bottom: 1rem;
            position: relative;
        }

        .answer-card.published { border-color: rgba(16,185,129,0.3); }
        .answer-card.draft { border-color: rgba(245,158,11,0.3); }

        .answer-header {
            display: flex; align-items: center; justify-content: space-between;
            margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;
        }

        .answer-author { display: flex; align-items: center; gap: 0.75rem; }

        .avatar {
            width: 36px; height: 36px; border-radius: 50%;
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            display: flex; align-items: center; justify-content: center;
            font-size: 0.875rem; font-weight: 700; color: #fff; flex-shrink: 0;
        }

        .author-name { font-weight: 600; font-size: 0.9rem; }
        .author-code { font-size: 0.75rem; color: var(--text-muted); }

        .answer-actions { display: flex; gap: 0.5rem; }

        .btn {
            display: inline-flex; align-items: center; gap: 6px;
            padding: 8px 16px; border-radius: 8px; font-size: 0.8rem;
            font-weight: 600; border: none; cursor: pointer; transition: all 0.2s;
            text-decoration: none;
        }

        .btn-publish {
            background: rgba(16,185,129,0.15); color: #10b981;
            border: 1px solid rgba(16,185,129,0.3);
        }
        .btn-publish:hover { background: rgba(16,185,129,0.25); }

        .btn-delete {
            background: rgba(239,68,68,0.12); color: #ef4444;
            border: 1px solid rgba(239,68,68,0.25);
        }
        .btn-delete:hover { background: rgba(239,68,68,0.2); }

        .answer-content {
            font-size: 0.9rem; line-height: 1.7; color: var(--text);
            padding: 1rem; background: var(--surface2); border-radius: 8px;
        }

        .answer-date { font-size: 0.75rem; color: var(--text-muted); margin-top: 0.75rem; }

        .status-published {
            display: inline-flex; align-items: center; gap: 4px;
            background: rgba(16,185,129,0.12); color: #10b981;
            padding: 3px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600;
        }

        .status-draft {
            display: inline-flex; align-items: center; gap: 4px;
            background: rgba(245,158,11,0.12); color: #f59e0b;
            padding: 3px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600;
        }

        /* Answer form */
        .answer-form {
            background: var(--surface); border: 1px solid var(--border);
            border-radius: var(--radius); padding: 2rem;
        }

        .form-title {
            font-size: 1.1rem; font-weight: 700; margin-bottom: 1.5rem;
            display: flex; align-items: center; gap: 8px;
        }

        .form-group { margin-bottom: 1.25rem; }

        .form-label {
            display: block; font-size: 0.875rem; font-weight: 600;
            color: var(--text-muted); margin-bottom: 0.5rem;
        }

        textarea {
            width: 100%; padding: 1rem; background: var(--surface2);
            border: 1px solid var(--border); border-radius: 10px;
            color: var(--text); font-family: 'Inter', sans-serif;
            font-size: 0.9rem; resize: vertical; min-height: 140px;
            transition: border-color 0.2s; line-height: 1.6;
        }
        textarea:focus { outline: none; border-color: var(--accent); }
        textarea::placeholder { color: var(--text-muted); }

        .form-actions { display: flex; gap: 0.75rem; flex-wrap: wrap; }

        .btn-draft {
            background: var(--surface2); color: var(--text);
            border: 1px solid var(--border); padding: 10px 20px; font-size: 0.875rem;
        }
        .btn-draft:hover { border-color: var(--accent); color: var(--accent); }

        .btn-submit {
            background: linear-gradient(135deg, var(--accent), var(--accent2));
            color: #fff; padding: 10px 24px; font-size: 0.875rem;
        }
        .btn-submit:hover { opacity: 0.9; transform: translateY(-1px); }
    </style>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/support-theme.css">
</head>
<body class="support-page answer-question-page">
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
    <a href="${pageContext.request.contextPath}/qa-management" class="btn-back"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg> Quay lại</a>
</header>

<%-- Toast notifications --%>
<c:if test="${not empty param.msg}">
    <div class="toast toast-${param.msg}" id="toast">
        <c:choose>
            <c:when test="${param.msg == 'success'}"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check"/></svg> Đã lưu câu trả lời</c:when>
            <c:when test="${param.msg == 'published'}"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check-circle"/></svg> Đã đăng câu trả lời lên Q&amp;A công khai</c:when>
            <c:when test="${param.msg == 'deleted'}"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#trash"/></svg> Đã xóa câu trả lời</c:when>
            <c:otherwise><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#alert"/></svg> Có lỗi xảy ra</c:otherwise>
        </c:choose>
    </div>
    <script>setTimeout(() => document.getElementById('toast')?.remove(), 3000);</script>
</c:if>

<main class="container">

    <%-- Question --%>
    <div class="question-section">
        <div class="q-label"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#question"/></svg> Câu hỏi từ sinh viên</div>
        <h1 class="q-title"><c:out value="${question.title}"/></h1>
        <div class="q-content"><c:out value="${question.content}"/></div>
        <div class="q-meta">
            <div class="meta-item">
                <c:choose>
                    <c:when test="${question.status == 'PENDING'}">
                        <span class="badge badge-pending"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#clock"/></svg> Chờ trả lời</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-answered"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check"/></svg> Đã trả lời</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <c:choose>
                <c:when test="${question.anonymous}">
                    <div class="meta-item"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#lock"/></svg> Ẩn danh</div>
                </c:when>
                <c:otherwise>
                    <div class="meta-item"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#user"/></svg> <c:out value="${question.studentName}"/> · <c:out value="${question.studentCode}"/></div>
                </c:otherwise>
            </c:choose>
            <div class="meta-item"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#clock"/></svg> ${dt:format(question.createdAt)}</div>
        </div>
    </div>

    <%-- Existing answers --%>
    <c:if test="${not empty answers}">
        <div class="divider"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#chat"/></svg> Câu trả lời (${answers.size()})</div>

        <div class="answers-section">
            <c:forEach var="ans" items="${answers}">
                <div class="answer-card ${ans.approvalStatus.toLowerCase()}">
                    <div class="answer-header">
                        <div class="answer-author">
                            <div class="avatar">
                                NV
                            </div>
                            <div>
                                <div class="author-name"><c:out value="${ans.staffName}"/></div>
                                <div class="author-code"><c:out value="${ans.staffCode}"/></div>
                            </div>
                        </div>
                        <div style="display:flex; align-items:center; gap:0.75rem;">
                            <c:choose>
                                <c:when test="${ans.approvalStatus == 'PUBLISHED'}">
                                    <span class="status-published"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#check"/></svg> Đã đăng</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-draft"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#file"/></svg> Nháp</span>
                                </c:otherwise>
                            </c:choose>
                            <div class="answer-actions">
                                <c:if test="${ans.approvalStatus == 'DRAFT'}">
                                    <form method="post" action="${pageContext.request.contextPath}/qa-management" style="display:inline">
                                        <input type="hidden" name="action" value="publish">
                                        <input type="hidden" name="answerId" value="${ans.id}">
                                        <input type="hidden" name="questionId" value="${question.id}">
                                        <button type="submit" class="btn btn-publish"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#upload"/></svg> Đăng công khai</button>
                                    </form>
                                </c:if>
                                <form method="post" action="${pageContext.request.contextPath}/qa-management" style="display:inline"
                                      onsubmit="return confirm('Xóa câu trả lời này?')">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="answerId" value="${ans.id}">
                                    <input type="hidden" name="questionId" value="${question.id}">
                                    <button type="submit" class="btn btn-delete" aria-label="Xóa câu trả lời"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#trash"/></svg></button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="answer-content"><c:out value="${ans.content}"/></div>
                    <div class="answer-date">
                        Viết lúc: ${dt:format(ans.createdAt)}
                        <c:if test="${ans.publishedAt != null}"> · Đăng lúc: ${dt:format(ans.publishedAt)}</c:if>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <c:if test="${empty answers}">
        <div class="divider"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#chat"/></svg> Chưa có câu trả lời nào</div>
    </c:if>

    <%-- Answer form --%>
    <div class="answer-form">
        <div class="form-title"><svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#file"/></svg> Viết câu trả lời</div>
        <form method="post" action="${pageContext.request.contextPath}/qa-management" id="answerForm">
            <input type="hidden" name="action" value="answer">
            <input type="hidden" name="questionId" value="${question.id}">
            <input type="hidden" name="publishNow" value="0" id="publishNowField">

            <div class="form-group">
                <label class="form-label">Nội dung trả lời <span style="color:#ef4444">*</span></label>
                <textarea name="content" placeholder="Viết câu trả lời chi tiết, rõ ràng để giúp sinh viên hiểu..." required></textarea>
            </div>

            <div class="form-actions">
                <button type="button" class="btn btn-draft" onclick="submitForm('0')">
                    <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#file"/></svg> Lưu nháp
                </button>
                <button type="button" class="btn btn-submit" onclick="submitForm('1')">
                    <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#upload"/></svg> Trả lời &amp; đăng công khai
                </button>
            </div>
        </form>
    </div>
</main>

<script>
    function submitForm(publishNow) {
        document.getElementById('publishNowField').value = publishNow;
        document.getElementById('answerForm').submit();
    }
</script>

        </div>
    </div>
</div>
</body>
</html>
