<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="${isStudent ? 'activities' : 'registration-forms'}" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết đơn đăng ký | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/registration.css?v=20261021-14">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
            <main class="page activity-list-page activity-form-page activity-detail-page">
                <header class="activity-hero">
                    <div class="activity-hero-copy">
                        <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ ĐĂNG KÝ</span>
                        <h1><c:out value="${isStudent ? 'Chi tiết đơn đăng ký của tôi' : 'Chi tiết đơn sinh viên'}"/></h1>
                        <p><c:out value="${form.title}"/> · <c:out value="${form.activityName}"/></p>
                    </div>
                </header>

                <c:if test="${param.msg eq 'approved'}"><div class="alert alert-success activity-alert">Đã duyệt đơn đăng ký.</div></c:if>
                <c:if test="${param.msg eq 'rejected'}"><div class="alert alert-error activity-alert">Đã từ chối đơn đăng ký.</div></c:if>
                <c:if test="${param.msg eq 'rejectionUndone'}"><div class="alert alert-success activity-alert">Đã hủy từ chối; đơn trở về trạng thái chờ duyệt.</div></c:if>
                <c:if test="${param.msg eq 'approvalUndone'}"><div class="alert alert-success activity-alert">Đã hủy duyệt đơn; đơn đang chờ xử lý.</div></c:if>
                <c:if test="${param.msg eq 'reviewError'}"><div class="alert alert-error activity-alert">Trạng thái đơn đã thay đổi hoặc không thể cập nhật.</div></c:if>
                <c:if test="${param.msg eq 'fifoError'}"><div class="alert alert-error activity-alert">Không thể xử lý đơn này vì còn đơn chờ xử lý được gửi trước. Hãy xử lý các đơn theo thứ tự gửi.</div></c:if>
                <c:if test="${param.msg eq 'capacityError'}"><div class="alert alert-error activity-alert">Không thể khôi phục đơn: hoạt động đã đủ số lượng tối đa. Đơn bị từ chối vẫn được giữ nguyên.</div></c:if>

                <section class="card activity-form-card activity-detail-card registration-detail-card">
                    <div class="activity-table-heading">
                        <div>
                            <h2><c:out value="${application.studentName}"/></h2>
                            <p>Đơn #<c:out value="${application.id}"/></p>
                        </div>
                        <c:choose>
                            <c:when test="${application.status eq 'APPROVED'}"><span class="badge status-pill status-green">Đã duyệt</span></c:when>
                            <c:when test="${application.status eq 'REJECTED'}"><span class="badge status-pill status-red">Đã từ chối</span></c:when>
                            <c:otherwise><span class="badge status-pill status-yellow">Chờ xử lý</span></c:otherwise>
                        </c:choose>
                    </div>

                    <div class="activity-detail-content">
                        <section class="activity-detail-section">
                            <h3>Thông tin sinh viên</h3>
                            <dl class="detail-grid activity-detail-grid">
                                <div class="detail-item"><dt>Mã sinh viên</dt><dd><c:out value="${application.studentCode}" default="—"/></dd></div>
                                <div class="detail-item"><dt>Họ và tên</dt><dd><c:out value="${application.studentName}" default="—"/></dd></div>
                                <div class="detail-item"><dt>Lớp</dt><dd><c:out value="${application.classCode}" default="—"/></dd></div>
                                <div class="detail-item"><dt>Email</dt><dd><c:out value="${application.email}" default="—"/></dd></div>
                                <div class="detail-item"><dt>Thời gian gửi</dt><dd><c:out value="${dt:format(application.submittedAt)}"/></dd></div>
                            </dl>
                        </section>

                        <section class="activity-detail-section">
                            <h3>Câu trả lời trong đơn</h3>
                            <dl class="detail-grid activity-detail-grid">
                                <c:forEach items="${application.answers}" var="answer">
                                    <div class="detail-item">
                                        <dt><c:out value="${answer.label}"/></dt>
                                        <dd><c:out value="${answer.value}" default="—"/></dd>
                                    </div>
                                </c:forEach>
                                <c:if test="${not empty application.note}">
                                    <div class="detail-item field-wide"><dt>Ghi chú</dt><dd class="activity-detail-description"><c:out value="${application.note}"/></dd></div>
                                </c:if>
                            </dl>
                        </section>
                    </div>

                    <div class="form-actions activity-form-actions">
                        <c:choose><c:when test="${isStudent}"><a class="button activity-cancel-button" href="${pageContext.request.contextPath}/registration-forms?action=myApplications">Quay lại đơn đăng ký của tôi</a></c:when><c:otherwise><a class="button activity-cancel-button" href="${pageContext.request.contextPath}/registration-forms?action=applications&amp;id=${form.id}">Quay lại danh sách đơn</a></c:otherwise></c:choose>
                        <c:if test="${not isStudent}"><c:choose>
                            <c:when test="${application.status eq 'SUBMITTED'}">
                                <div class="registration-review-actions">
                                <form method="post" action="${pageContext.request.contextPath}/registration-forms">
                                    <input type="hidden" name="action" value="approveApplication">
                                    <input type="hidden" name="registrationId" value="${application.id}">
                                    <button class="button registration-review-button registration-review-button--approve" type="submit">Duyệt đơn</button>
                                </form>
                                <form method="post" action="${pageContext.request.contextPath}/registration-forms"
                                      onsubmit="return confirm('Từ chối đơn đăng ký này?')">
                                    <input type="hidden" name="action" value="rejectApplication">
                                    <input type="hidden" name="registrationId" value="${application.id}">
                                    <button class="button registration-review-button registration-review-button--reject" type="submit">Từ chối đơn</button>
                                </form>
                                </div>
                            </c:when>
                            <c:when test="${application.status eq 'APPROVED'}">
                                <form method="post" action="${pageContext.request.contextPath}/registration-forms"
                                      onsubmit="return confirm('Hủy duyệt đơn này và chuyển về trạng thái chờ xử lý?')">
                                    <input type="hidden" name="action" value="undoApplicationApproval">
                                    <input type="hidden" name="registrationId" value="${application.id}">
                                    <button class="button registration-review-button registration-review-button--undo-approval" type="submit">Hủy duyệt</button>
                                </form>
                            </c:when>
                            <c:when test="${application.status eq 'REJECTED'}">
                                <form method="post" action="${pageContext.request.contextPath}/registration-forms"
                                      onsubmit="return confirm('Hủy từ chối đơn này và chuyển về trạng thái chờ duyệt?')">
                                    <input type="hidden" name="action" value="undoApplicationRejection">
                                    <input type="hidden" name="registrationId" value="${application.id}">
                                    <button class="button registration-review-button registration-review-button--undo-rejection" type="submit">Hủy từ chối</button>
                                </form>
                            </c:when>
                        </c:choose></c:if>
                    </div>
                </section>
            </main>
        </div>
    </div>
</div>
</body>
</html>
