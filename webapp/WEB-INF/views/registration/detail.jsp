<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="st" uri="http://swp391.g1/status" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="registration-forms" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết đợt đăng ký | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/registration.css?v=20261021-13">
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
                        <h1>Chi tiết đợt đăng ký</h1>
                        <p><c:out value="${form.title}"/></p>
                    </div>
                </header>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error activity-alert"><c:out value="${sessionScope.errorMessage}"/></div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <section class="card activity-form-card activity-detail-card registration-detail-card">
                    <div class="activity-table-heading">
                        <div>
                            <h2>Thông tin form</h2>
                            <p>Chi tiết nội dung và tình trạng đăng ký của form.</p>
                        </div>
                        <div class="registration-detail-actions">
                            <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/registration-forms">Danh sách form</a>
                            <a class="button button-primary" href="${pageContext.request.contextPath}/registration-forms?action=edit&amp;id=${form.id}">Chỉnh sửa</a>
                        </div>
                    </div>

                    <div class="activity-tabs-nav registration-detail-tabs" role="tablist">
                        <button type="button" class="activity-tab-btn active" id="btn-tab-form-info"
                                data-tab="tab-form-info" role="tab" aria-selected="true" aria-controls="tab-form-info">
                            Thông tin form
                        </button>
                        <button type="button" class="activity-tab-btn" id="btn-tab-form-applications"
                                data-tab="tab-form-applications" role="tab" aria-selected="false" aria-controls="tab-form-applications">
                            Đơn đã gửi <span class="activity-tab-badge"><c:out value="${form.registrationCount}"/></span>
                        </button>
                    </div>

                    <div class="activity-detail-content registration-detail-tab-content">
                        <div id="tab-form-info" class="activity-tab-pane active" role="tabpanel" aria-labelledby="btn-tab-form-info">
                        <section class="activity-detail-section">
                            <h3>Thông tin cơ bản</h3>
                            <dl class="detail-grid activity-detail-grid">
                                <div class="detail-item">
                                    <dt>Tiêu đề form</dt>
                                    <dd><c:out value="${form.title}"/></dd>
                                </div>
                                <div class="detail-item">
                                    <dt>Hoạt động</dt>
                                    <dd><c:out value="${form.activityName}"/></dd>
                                </div>
                                <div class="detail-item">
                                    <dt>Trạng thái</dt>
                                    <dd><span class="badge status-pill ${st:css(form.status)}">${st:label(form.status)}</span></dd>
                                </div>
                                <div class="detail-item">
                                    <dt>Số lượng đăng ký</dt>
                                    <dd><span class="registration-count"><c:out value="${form.registrationCount}"/></span></dd>
                                </div>
                                <div class="detail-item">
                                    <dt>Số chỗ đang giữ</dt>
                                    <dd>
                                        <c:choose>
                                            <c:when test="${not empty form.maxParticipants}">
                                                <c:out value="${form.reservedSeatCount}"/> / <c:out value="${form.maxParticipants}"/>
                                            </c:when>
                                            <c:otherwise><c:out value="${form.reservedSeatCount}"/> / Không giới hạn</c:otherwise>
                                        </c:choose>
                                    </dd>
                                </div>
                            </dl>
                        </section>

                        <section class="activity-detail-section">
                            <h3>Thời gian đăng ký</h3>
                            <dl class="detail-grid activity-detail-grid">
                                <div class="detail-item">
                                    <dt>Mở đăng ký</dt>
                                    <dd><c:choose><c:when test="${not empty form.openAt}"><c:out value="${dt:format(form.openAt)}"/></c:when><c:otherwise>Không giới hạn</c:otherwise></c:choose></dd>
                                </div>
                                <div class="detail-item">
                                    <dt>Đóng đăng ký</dt>
                                    <dd><c:choose><c:when test="${not empty form.closeAt}"><c:out value="${dt:format(form.closeAt)}"/></c:when><c:otherwise>Không giới hạn</c:otherwise></c:choose></dd>
                                </div>
                            </dl>
                        </section>

                        <section class="activity-detail-section">
                            <h3>Mô tả và hướng dẫn</h3>
                            <dl class="detail-grid activity-detail-grid">
                                <div class="detail-item field-wide">
                                    <dt>Nội dung</dt>
                                    <dd class="activity-detail-description"><c:out value="${form.description}" default="Chưa có mô tả"/></dd>
                                </div>
                            </dl>
                        </section>
                        </div>

                        <div id="tab-form-applications" class="activity-tab-pane" role="tabpanel" aria-labelledby="btn-tab-form-applications">
                            <section class="activity-detail-section registration-applications-tab">
                                <div class="registration-applications-heading">
                                    <div>
                                        <h3>Đơn sinh viên đăng ký</h3>
                                        <p>Các đơn đã gửi cho form này, bao gồm trạng thái xét duyệt.</p>
                                    </div>
                                </div>
                                <div class="table-wrap activity-table-wrap">
                                    <table class="registration-table registration-applications-table">
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
                                                <td>
                                                    <strong><c:out value="${application.studentName}"/></strong>
                                                    <div class="muted">Mã sinh viên: <c:out value="${application.studentCode}"/></div>
                                                </td>
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
                                            <tr>
                                                <td class="empty-state registration-empty-state" colspan="6">
                                                    <strong>Chưa có sinh viên gửi đơn</strong>
                                                    <span>Đơn đăng ký sẽ xuất hiện tại đây sau khi sinh viên gửi form.</span>
                                                </td>
                                            </tr>
                                        </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </section>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/activity.js?v=20261020-2"></script>
</body>
</html>
