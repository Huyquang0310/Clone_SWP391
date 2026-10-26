<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="st" uri="http://swp391.g1/status" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="partner-invitations" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lời mời hợp tác | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/partner-invitation.css?v=13">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/action-menu.css?v=3">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<main class="page activity-list-page">
    <header class="activity-hero">
        <div class="activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ ĐỐI TÁC</span>
            <h1>Lời mời hợp tác</h1>
            <p>Danh sách lời mời hợp tác trong các hoạt động ngoại khóa.</p>
        </div>
    </header>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success activity-alert"><c:out value="${sessionScope.successMessage}"/></div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-error activity-alert"><c:out value="${sessionScope.errorMessage}"/></div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <section class="card activity-table-card" aria-labelledby="invitation-table-title">
        <div class="activity-table-heading">
            <div>
                <h2 id="invitation-table-title">Danh sách lời mời hợp tác</h2>
                <p>Lời nhắn, trạng thái lời mời và thao tác luôn hiển thị cố định bên phải</p>
            </div>
            <span class="activity-count"><c:out value="${totalCount}"/> lời mời</span>
        </div>
        <details class="activity-filter-panel" id="invitation-filter-panel">
        <summary class="activity-filter-toggle">
            <span>Tìm kiếm &amp; Bộ lọc</span>
            <c:if test="${activeFilterCount > 0}">
                <span class="activity-filter-badge"><c:out value="${activeFilterCount}"/></span>
            </c:if>
        </summary>
        <form class="activity-filter" action="${pageContext.request.contextPath}/partner-invitations" method="get">
            <input type="hidden" name="pageSize" value="${pageSize}">
            <div class="activity-filter-field activity-filter-search">
                <label for="filter-keyword">Tìm kiếm</label>
                <input id="filter-keyword" type="search" name="keyword" value="<c:out value='${keyword}'/>"
                       placeholder="Mã hoặc tên hoạt động">
            </div>
            <div class="activity-filter-field">
                <label for="filter-type">Loại hoạt động</label>
                <select id="filter-type" name="activityType">
                    <option value="">Tất cả</option>
                    <c:forEach var="type" items="${activityTypes}">
                        <option value="<c:out value='${type}'/>" ${selectedActivityType == type ? 'selected' : ''}><c:out value="${type}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="activity-filter-field">
                <label for="filter-activity-approval">Trạng thái duyệt HĐ</label>
                <select id="filter-activity-approval" name="activityApprovalStatus">
                    <option value="">Tất cả</option>
                    <c:forEach var="status" items="${approvalStatuses}">
                        <option value="${status}" ${selectedActivityApprovalStatus == status.name() ? 'selected' : ''}>${st:label(status)}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="activity-filter-field">
                <label for="filter-activity-status">Trạng thái hoạt động</label>
                <select id="filter-activity-status" name="activityStatus">
                    <option value="">Tất cả</option>
                    <c:forEach var="status" items="${activityStatuses}">
                        <option value="${status}" ${selectedActivityStatus == status.name() ? 'selected' : ''}>${st:label(status)}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="activity-filter-field">
                <label for="filter-invitation-status">Trạng thái lời mời</label>
                <select id="filter-invitation-status" name="invitationStatus">
                    <option value="">Tất cả</option>
                    <c:forEach var="status" items="${invitationStatuses}">
                        <option value="${status}" ${selectedInvitationStatus == status ? 'selected' : ''}>${st:label(status)}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="activity-filter-buttons">
                <button type="submit" class="button button-primary button-sm">Tìm kiếm &amp; Lọc</button>
                <c:if test="${filtered}">
                    <a class="button button-sm" href="${pageContext.request.contextPath}/partner-invitations?pageSize=${pageSize}">Xóa Bộ lọc</a>
                </c:if>
            </div>
        </form>
        </details>
        <div class="table-wrap activity-table-wrap">
            <table class="partner-invitation-table">
                <thead>
                <tr>
                    <th scope="col">Mã hoạt động</th>
                    <th scope="col">Tên hoạt động</th>
                    <th scope="col">Loại hoạt động</th>
                    <th scope="col" class="col-wrap">Trạng thái duyệt HĐ</th>
                    <th scope="col">Thời gian</th>
                    <th scope="col" class="activity-run-status"><span>Trạng thái</span><span>hoạt động</span></th>
                    <th scope="col" class="freeze freeze-message">Lời nhắn</th>
                    <th scope="col" class="freeze freeze-status invitation-status-column"><span>Trạng thái</span><span>lời mời</span></th>
                    <th scope="col" class="freeze freeze-actions">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty invitations}">
                        <tr>
                            <td class="empty-state activity-empty-state" colspan="9">
                                <c:choose>
                                    <c:when test="${filtered}"><strong>Không tìm thấy lời mời phù hợp</strong></c:when>
                                    <c:otherwise><strong>Chưa có lời mời hợp tác nào</strong></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="inv" items="${invitations}">
                            <tr>
                                <td><span class="activity-code"><c:out value="${inv.activityCode}"/></span></td>
                                <td><strong class="activity-name"><c:out value="${inv.activityName}"/></strong></td>
                                <td><c:out value="${inv.activityTypeName}" default="—"/></td>
                                <td class="col-wrap"><span class="badge status-pill ${st:css(inv.activityApprovalStatus)}">${st:label(inv.activityApprovalStatus)}</span></td>
                                <td class="activity-time">
                                    <c:choose>
                                        <c:when test="${not empty inv.activityStartTime}">
                                            ${dt:format(inv.activityStartTime)}
                                            <c:if test="${not empty inv.activityEndTime}"><span>đến</span>${dt:format(inv.activityEndTime)}</c:if>
                                        </c:when>
                                        <c:otherwise>Chưa xác định</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="activity-run-status"><span class="badge status-pill ${st:css(inv.activityStatus)}">${st:label(inv.activityStatus)}</span></td>
                                <td class="freeze freeze-message">
                                    <c:choose>
                                        <c:when test="${empty inv.message}">—</c:when>
                                        <c:otherwise>
                                            <button type="button" class="button button-sm invitation-message-view"
                                                    data-title="${inv.termination ? 'Lời nhắn mời ngừng hợp tác' : 'Lời nhắn mời hợp tác'}"
                                                    data-message="<c:out value='${inv.message}'/>">Xem</button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="freeze freeze-status invitation-status-column">
                                    <span class="badge status-pill ${st:css(inv.approvalStatus)}">
                                        <c:choose>
                                            <c:when test="${inv.approvalStatus == 'PENDING'}">Chờ phản hồi</c:when>
                                            <c:when test="${!inv.termination && inv.approvalStatus == 'APPROVED' && inv.collaborationStatus != 'ACTIVE'}">Đã ngừng hợp tác</c:when>
                                            <c:otherwise>${st:label(inv.approvalStatus)}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td class="freeze freeze-actions">
                                    <div class="actions activity-actions">
                                        <a class="activity-action-link" href="${pageContext.request.contextPath}/activities?action=detail&amp;id=${inv.activityId}#tab-partner">
                                            Chi tiết
                                        </a>
                                        <c:if test="${inv.canRespond || inv.canRevoke}">
                                            <details class="action-menu">
                                                <summary aria-label="Thao tác khác"><span aria-hidden="true">⋯</span></summary>
                                                <div class="action-menu__items">
                                                    <c:if test="${inv.canRespond}">
                                                        <form action="${pageContext.request.contextPath}/activities" method="post">
                                                            <input type="hidden" name="action" value="respondInvitation">
                                                            <input type="hidden" name="returnTo" value="partner-invitations">
                                                            <input type="hidden" name="activityId" value="${inv.activityId}">
                                                            <input type="hidden" name="invitationId" value="${inv.id}">
                                                            <button type="submit" name="decision" value="accept" class="action-menu-item action-menu-item--approve">Chấp nhận lời mời</button>
                                                            <button type="submit" name="decision" value="reject" class="action-menu-item action-menu-item--reject">Từ chối lời mời</button>
                                                        </form>
                                                    </c:if>
                                                    <c:if test="${inv.canRevoke}">
                                                        <form action="${pageContext.request.contextPath}/activities" method="post"
                                                              data-confirm="Hủy phản hồi sẽ đưa lời mời về trạng thái chờ phản hồi${inv.approvalStatus == 'APPROVED' ? ' và gỡ các nhân sự đối tác của công ty khỏi hoạt động' : ''}. Bạn có chắc chắn?">
                                                            <input type="hidden" name="action" value="revokeInvitation">
                                                            <input type="hidden" name="returnTo" value="partner-invitations">
                                                            <input type="hidden" name="activityId" value="${inv.activityId}">
                                                            <input type="hidden" name="invitationId" value="${inv.id}">
                                                            <button type="submit" class="action-menu-item action-menu-item--undo">${inv.approvalStatus == 'APPROVED' ? 'Hủy duyệt' : 'Hủy từ chối'}</button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </details>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
        <div class="activity-pagination">
            <form class="activity-page-size" action="${pageContext.request.contextPath}/partner-invitations" method="get">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="keyword" value="<c:out value='${keyword}'/>">
                <input type="hidden" name="activityType" value="<c:out value='${selectedActivityType}'/>">
                <input type="hidden" name="activityStatus" value="<c:out value='${selectedActivityStatus}'/>">
                <input type="hidden" name="activityApprovalStatus" value="<c:out value='${selectedActivityApprovalStatus}'/>">
                <input type="hidden" name="invitationStatus" value="<c:out value='${selectedInvitationStatus}'/>">
                <label for="invitation-page-size">Hiển thị</label>
                <select id="invitation-page-size" name="pageSize" onchange="this.form.submit()">
                    <option value="5" ${pageSize == 5 ? 'selected' : ''}>5</option>
                    <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                    <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                    <option value="25" ${pageSize == 25 ? 'selected' : ''}>25</option>
                </select>
                <span>bản ghi mỗi trang</span>
            </form>
            <span class="activity-pagination-summary">
                Hiển thị <c:out value="${firstNumber}"/>–<c:out value="${lastNumber}"/>
                trong <c:out value="${totalCount}"/> lời mời
            </span>
            <c:if test="${totalPages > 1}">
                <nav class="activity-pagination-nav" aria-label="Phân trang danh sách lời mời">
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <a class="activity-page-link"
                               href="${pageContext.request.contextPath}/partner-invitations?page=${currentPage - 1}&amp;pageSize=${pageSize}<c:out value="${filterQuery}"/>"
                               aria-label="Trang trước">‹</a>
                        </c:when>
                        <c:otherwise><span class="activity-page-link is-disabled" aria-hidden="true">‹</span></c:otherwise>
                    </c:choose>
                    <c:forEach var="pageNumber" begin="1" end="${totalPages}">
                        <a class="activity-page-link${pageNumber == currentPage ? ' is-active' : ''}"
                           href="${pageContext.request.contextPath}/partner-invitations?page=${pageNumber}&amp;pageSize=${pageSize}<c:out value="${filterQuery}"/>"
                           aria-current="${pageNumber == currentPage ? 'page' : ''}"><c:out value="${pageNumber}"/></a>
                    </c:forEach>
                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <a class="activity-page-link"
                               href="${pageContext.request.contextPath}/partner-invitations?page=${currentPage + 1}&amp;pageSize=${pageSize}<c:out value="${filterQuery}"/>"
                               aria-label="Trang tiếp theo">›</a>
                        </c:when>
                        <c:otherwise><span class="activity-page-link is-disabled" aria-hidden="true">›</span></c:otherwise>
                    </c:choose>
                </nav>
            </c:if>
        </div>    </section>
</main>
        </div>
    </div>
</div>

<dialog id="invitation-message-modal" class="invitation-message-modal">
    <h3 id="invitation-message-modal-title" style="margin-top: 0;">Lời nhắn</h3>
    <p id="invitation-message-modal-body" style="white-space: pre-wrap; word-break: break-word;"></p>
    <div style="text-align: right;"><button type="button" class="button" id="invitation-message-modal-close">Đóng</button></div>
</dialog>
<script src="${pageContext.request.contextPath}/assets/js/action-menu.js?v=5"></script>
<script src="${pageContext.request.contextPath}/assets/js/partner-invitation.js?v=3"></script>
</body>
</html>