<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="st" uri="http://swp391.g1/status" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="registration-forms" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý đợt đăng ký | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/registration.css?v=20261023-1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/action-menu.css?v=3">
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
                        <h1>Đăng ký tham gia hoạt động</h1>
                        <p>Tạo và theo dõi các form để sinh viên đăng ký tham gia hoạt động.</p>
                    </div>
                </header>

                <c:if test="${param.msg eq 'saved'}">
                    <div class="alert alert-success activity-alert">Đã lưu đợt đăng ký.</div>
                </c:if>
                <c:if test="${param.msg eq 'deleted'}">
                    <div class="alert alert-success activity-alert">Đã xóa form.</div>
                </c:if>
                <c:if test="${param.msg eq 'deleteError'}">
                    <div class="alert alert-error activity-alert">Không thể xóa form đã có sinh viên đăng ký.</div>
                </c:if>
                <c:if test="${param.msg eq 'statusUpdated'}">
                    <div class="alert alert-success activity-alert">Đã cập nhật trạng thái form.</div>
                </c:if>
                <c:if test="${param.msg eq 'statusError'}">
                    <div class="alert alert-error activity-alert"><c:out value="${param.error}" default="Không thể cập nhật trạng thái form."/></div>
                </c:if>

                <section class="card activity-table-card registration-table-card">
                    <div class="activity-table-heading">
                        <div>
                            <h2>Danh sách form</h2>
                            <p>Thông tin các đợt đăng ký hoạt động đã tạo</p>
                        </div>
                        <div class="activity-heading-actions">
                            <span class="activity-count"><c:out value="${fn:length(forms)}"/> form</span>
                            <a class="btn-create-activity" href="${pageContext.request.contextPath}/registration-forms?action=create">
                                <span aria-hidden="true">+</span>Tạo form
                            </a>
                        </div>
                    </div>
                    <div class="table-wrap activity-table-wrap registration-table-wrap" tabindex="0"
                         aria-label="Danh sách đợt đăng ký, cuộn ngang để xem thêm thông tin">
                        <table class="registration-table">
                            <thead>
                            <tr>
                                <th scope="col">Form</th>
                                <th scope="col">Hoạt động</th>
                                <th scope="col">Thời gian đăng ký</th>
                                <th scope="col" class="registration-sticky-status">Trạng thái</th>
                                <th scope="col" class="registration-sticky-count">Số đăng ký</th>
                                <th scope="col" class="registration-sticky-actions">Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${forms}" var="f">
                                <tr>
                                    <td class="registration-title-cell">
                                        <strong><c:out value="${f.title}"/></strong>
                                        <div class="muted"><c:out value="${f.description}"/></div>
                                    </td>
                                    <td><c:out value="${f.activityName}"/></td>
                                    <td class="registration-time-cell">
                                        <span><strong>Từ:</strong>
                                            <c:choose><c:when test="${not empty f.openAt}"><c:out value="${dt:format(f.openAt)}"/></c:when><c:otherwise>Không giới hạn</c:otherwise></c:choose>
                                        </span>
                                        <span><strong>Đến:</strong>
                                            <c:choose><c:when test="${not empty f.closeAt}"><c:out value="${dt:format(f.closeAt)}"/></c:when><c:otherwise>Không giới hạn</c:otherwise></c:choose>
                                        </span>
                                    </td>
                                    <td class="registration-sticky-status">
                                        <span class="badge status-pill ${st:css(f.status)}">${st:label(f.status)}</span>
                                    </td>
                                    <td class="registration-sticky-count"><span class="registration-count"><c:out value="${f.registrationCount}"/></span></td>
                                    <td class="registration-sticky-actions">
                                        <div class="actions activity-actions registration-row-actions">
                                            <a class="activity-action-link action-btn action-btn--view"
                                               href="${pageContext.request.contextPath}/registration-forms?action=view&amp;id=${f.id}">
                                                <span class="action-btn__icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/></svg></span>
                                                Chi tiết
                                            </a>
                                            <details class="action-menu">
                                                <summary aria-label="Thao tác khác"><span aria-hidden="true">⋯</span></summary>
                                                <div class="action-menu__items">
                                                    <a class="action-menu-item action-menu-item--edit"
                                                       href="${pageContext.request.contextPath}/registration-forms?action=edit&amp;id=${f.id}">
                                                        <span class="action-menu-item__icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4Z"/></svg></span>
                                                        Chỉnh sửa
                                                    </a>
                                                    <form method="post" action="${pageContext.request.contextPath}/registration-forms">
                                                        <input type="hidden" name="action" value="status">
                                                        <input type="hidden" name="id" value="${f.id}">
                                                        <input type="hidden" name="status" value="${f.status eq 'OPENING' ? 'CLOSED' : 'OPENING'}">
                                                        <button class="action-menu-item ${f.status eq 'OPENING' ? 'action-menu-item--undo' : 'action-menu-item--approve'}" type="submit">
                                                            <span class="action-menu-item__icon" aria-hidden="true"><svg viewBox="0 0 24 24"><c:choose><c:when test="${f.status eq 'OPENING'}"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></c:when><c:otherwise><path d="m5 12 4 4L19 6"/></c:otherwise></c:choose></svg></span>
                                                            <c:choose><c:when test="${f.status eq 'OPENING'}">Đóng đăng ký</c:when><c:when test="${f.status eq 'CLOSED'}">Mở lại đăng ký</c:when><c:otherwise>Mở đăng ký</c:otherwise></c:choose>
                                                        </button>
                                                    </form>
                                                    <form method="post" action="${pageContext.request.contextPath}/registration-forms"
                                                          onsubmit="return confirm('Bạn chắc chắn muốn xóa form này?')">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${f.id}">
                                                        <button class="action-menu-item action-menu-item--delete" type="submit">
                                                            <span class="action-menu-item__icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="m19 6-1 14H6L5 6"/><path d="M10 11v5"/><path d="M14 11v5"/></svg></span>
                                                            Xóa
                                                        </button>
                                                    </form>
                                                </div>
                                            </details>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty forms}">
                                <tr><td class="empty-state registration-empty-state" colspan="6">
                                    <span class="empty-state-mark" aria-hidden="true">+</span>
                                    <strong>Chưa có đợt đăng ký nào</strong>
                                    <span>Tạo đợt đăng ký cho hoạt động đã được duyệt; danh sách sinh viên sẽ nằm trong chi tiết đợt.</span>
                                    <a class="button button-primary" href="${pageContext.request.contextPath}/registration-forms?action=create">Tạo đợt đăng ký đầu tiên</a>
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
<script src="${pageContext.request.contextPath}/assets/js/action-menu.js?v=5"></script>
</body>
</html>
