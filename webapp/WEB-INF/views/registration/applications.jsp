<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="registration-forms" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đơn sinh viên đăng ký | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/registration.css?v=20261021-14">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
            <main class="page activity-list-page registration-management-page">
                <header class="activity-hero">
                    <div class="activity-hero-copy">
                        <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ ĐĂNG KÝ</span>
                        <h1>Đơn sinh viên đăng ký</h1>
                        <p><c:out value="${form.title}"/> · <c:out value="${form.activityName}"/></p>
                    </div>
                    <a class="button activity-create-button" href="${pageContext.request.contextPath}/registration-forms?action=view&amp;id=${form.id}">Quay lại form</a>
                </header>

                <c:if test="${param.msg eq 'approved'}"><div class="alert alert-success activity-alert">Đã duyệt đơn đăng ký.</div></c:if>
                <c:if test="${param.msg eq 'rejected'}"><div class="alert alert-error activity-alert">Đã từ chối đơn đăng ký.</div></c:if>
                <c:if test="${param.msg eq 'approvalUndone'}"><div class="alert alert-success activity-alert">Đã hủy duyệt; đơn trở về trạng thái chờ xử lý.</div></c:if>
                <c:if test="${param.msg eq 'reviewError'}"><div class="alert alert-error activity-alert">Trạng thái đơn đã thay đổi hoặc không thể cập nhật.</div></c:if>
                <c:if test="${param.msg eq 'fifoError'}"><div class="alert alert-error activity-alert">Không thể xử lý đơn này vì còn đơn chờ xử lý được gửi trước. Hãy xử lý các đơn theo thứ tự gửi.</div></c:if>

                <section class="card activity-table-card registration-table-card">
                    <div class="activity-table-heading">
                        <div><h2>Danh sách đơn</h2><p><c:out value="${fn:length(applications)}"/> đơn đã gửi</p></div>
                    </div>
                    <div class="table-wrap activity-table-wrap">
                        <table class="registration-table registration-applications-list-table">
                            <thead>
                            <tr>
                                <th>Sinh viên (Mã sinh viên)</th>
                                <th>Lớp</th>
                                <th>Email</th>
                                <th>Thời gian gửi</th>
                                <th class="registration-sticky-status">Trạng thái</th>
                                <th class="registration-sticky-actions">Chi tiết</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${applications}" var="application">
                                <tr>
                                    <td><strong><c:out value="${application.studentName}"/></strong><div class="muted">Mã sinh viên: <c:out value="${application.studentCode}"/></div></td>
                                    <td><c:out value="${application.classCode}" default="—"/></td>
                                    <td><c:out value="${application.email}" default="—"/></td>
                                    <td><c:out value="${dt:format(application.submittedAt)}"/></td>
                                    <td class="registration-sticky-status">
                                        <c:choose>
                                            <c:when test="${application.status eq 'APPROVED'}"><span class="badge status-pill status-green">Đã duyệt</span></c:when>
                                            <c:when test="${application.status eq 'REJECTED'}"><span class="badge status-pill status-red">Đã từ chối</span></c:when>
                                            <c:otherwise><span class="badge status-pill status-yellow">Chờ xử lý</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="registration-sticky-actions"><a class="activity-action-link" href="${pageContext.request.contextPath}/registration-forms?action=application&amp;id=${application.id}">Xem đơn</a></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty applications}">
                                <tr><td class="empty-state registration-empty-state" colspan="6">
                                    <strong>Chưa có sinh viên gửi đơn</strong>
                                    <span>Đơn đăng ký sẽ xuất hiện ở đây sau khi sinh viên gửi form.</span>
                                </td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>
    </div>
</div>
</body>
</html>
