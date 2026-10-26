<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="st" uri="http://swp391.g1/status" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<c:set var="activeNav" value="activities" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết hoạt động | EAMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261021-12">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>

    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <main class="page activity-list-page activity-form-page activity-detail-page">
            <header class="activity-hero">
                <div class="activity-hero-copy">
                    <span class="eyebrow">EAMS <span aria-hidden="true">/</span> QUẢN LÝ HOẠT ĐỘNG</span>
                    <h1>Chi tiết hoạt động</h1>
                    <p><span class="activity-code"><c:out value="${activity.code}"/></span> <c:out value="${activity.name}"/></p>
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

    <section class="card activity-form-card activity-detail-card">
        <div class="activity-table-heading">
            <div>
                <h2>Chi tiết hoạt động</h2>
                <p>Thông tin tổ chức, thời gian và nhân sự phụ trách hoạt động.</p>
            </div>
            <div class="activity-detail-header-actions">
                <c:if test="${canManageActivities && activity.activityStatus == 'UPCOMING'}">
                    <a class="button button-primary"
                       href="${pageContext.request.contextPath}/activities?action=edit&amp;id=${activity.id}">Chỉnh sửa</a>
                </c:if>
                <c:if test="${canManageActivities}">
                    <c:choose>
                        <c:when test="${not empty registrationForm}">
                            <a class="button button-primary"
                               href="${pageContext.request.contextPath}/registration-forms?action=view&amp;id=${registrationForm.id}">
                                Xem form và đơn đăng ký
                            </a>
                        </c:when>
                        <c:when test="${activity.approvalStatus == 'APPROVED'}">
                            <a class="button button-primary"
                               href="${pageContext.request.contextPath}/registration-forms?action=create&amp;activityId=${activity.id}">
                                Tạo form đăng ký
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="field-hint">Chỉ có thể tạo form đăng ký sau khi hoạt động được duyệt.</span>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </div>

        <div class="activity-tabs-nav" role="tablist">
            <button type="button" class="activity-tab-btn active" id="btn-tab-info" data-tab="tab-info" role="tab" aria-selected="true" aria-controls="tab-info">
                Thông tin chi tiết hoạt động
            </button>
            <button type="button" class="activity-tab-btn" id="btn-tab-staff" data-tab="tab-staff" role="tab" aria-selected="false" aria-controls="tab-staff">
                Đơn vị đồng phụ trách
            </button>
            <button type="button" class="activity-tab-btn" id="btn-tab-partner" data-tab="tab-partner" role="tab" aria-selected="false" aria-controls="tab-partner">
                Đối tác hỗ trợ hoạt động
            </button>
        </div>

        <div class="activity-detail-content">
            <!-- TAB 1: THÔNG TIN CHI TIẾT HOẠT ĐỘNG -->
            <div id="tab-info" class="activity-tab-pane active" role="tabpanel" aria-labelledby="btn-tab-info">
                <section class="activity-detail-section">
                    <h3>Thông tin cơ bản</h3>
                    <dl class="detail-grid activity-detail-grid">
                        <div class="detail-item"><dt>Mã hoạt động</dt><dd><c:out value="${activity.code}" default="—"/></dd></div>
                        <div class="detail-item"><dt>Tên hoạt động</dt><dd><c:out value="${activity.name}" default="—"/></dd></div>
                        <div class="detail-item"><dt>Học kỳ</dt><dd><c:out value="${activity.semesterCode}" default="—"/> <c:if test="${not empty activity.semesterName}">- <c:out value="${activity.semesterName}"/></c:if></dd></div>
                        <div class="detail-item"><dt>Loại hoạt động</dt><dd><c:out value="${activity.activityTypeCode}" default="—"/> <c:if test="${not empty activity.activityTypeName}">- <c:out value="${activity.activityTypeName}"/></c:if></dd></div>
                        <div class="detail-item"><dt>Trạng thái hoạt động</dt><dd><span class="badge status-pill ${st:css(activity.activityStatus)}">${st:label(activity.activityStatus)}</span></dd></div>
                        <div class="detail-item"><dt>Trạng thái duyệt</dt><dd><span class="badge status-pill ${st:css(activity.approvalStatus)}">${st:label(activity.approvalStatus)}</span></dd></div>
                    </dl>
                </section>

                <section class="activity-detail-section">
                    <h3>Phụ trách chính</h3>
                    <dl class="detail-grid activity-detail-grid">
                        <div class="detail-item"><dt>Đơn vị phụ trách chính</dt><dd><c:out value="${activity.responsibleDepartmentCode}" default="—"/> <c:if test="${not empty activity.responsibleDepartmentName}">- <c:out value="${activity.responsibleDepartmentName}"/></c:if></dd></div>
                        <div class="detail-item"><dt>Nhân viên phụ trách chính</dt><dd><c:out value="${activity.responsibleStaffCode}" default="—"/> <c:if test="${not empty activity.responsibleStaffName}">- <c:out value="${activity.responsibleStaffName}"/></c:if></dd></div>
                    </dl>
                </section>

                <section class="activity-detail-section">
                    <h3>Thời gian, địa điểm và cấu hình</h3>
                    <dl class="detail-grid activity-detail-grid">
                        <div class="detail-item"><dt>Bắt đầu</dt><dd><c:out value="${dt:format(activity.startTime)}" default="Chưa xác định"/></dd></div>
                        <div class="detail-item"><dt>Kết thúc</dt><dd><c:out value="${dt:format(activity.endTime)}" default="Chưa xác định"/></dd></div>
                        <div class="detail-item"><dt>Tính chất hoạt động</dt><dd><c:choose><c:when test="${activity.nature == 'REQUIRE_REGISTRATION'}">Cần đăng ký</c:when><c:when test="${activity.nature == 'VOLUNTARY'}">Tự nguyện</c:when><c:when test="${activity.nature == 'MANDATORY'}">Bắt buộc</c:when><c:otherwise>—</c:otherwise></c:choose></dd></div>
                        <div class="detail-item"><dt>Bắt buộc điền form</dt><dd><c:out value="${activity.registrationFormRequired ? 'Có' : 'Không'}"/></dd></div>
                        <div class="detail-item"><dt>Yêu cầu bài tổng kết</dt><dd><c:out value="${activity.summaryRequired ? 'Có' : 'Không'}"/></dd></div>
                        <div class="detail-item"><dt>Số lượng tối đa</dt><dd><c:out value="${activity.maxParticipants}" default="Không giới hạn"/></dd></div>
                        <div class="detail-item field-wide"><dt>Địa điểm</dt><dd><c:out value="${activity.address}" default="Chưa cập nhật"/></dd></div>
                        <div class="detail-item field-wide"><dt>Mô tả</dt><dd class="activity-detail-description"><c:out value="${activity.description}" default="Chưa có mô tả"/></dd></div>
                    </dl>
                </section>
            </div>

            <!-- TAB 2: THÔNG TIN NHÂN SỰ PHỤ TRÁCH -->
            <div id="tab-staff" class="activity-tab-pane" role="tabpanel" aria-labelledby="btn-tab-staff">
                <section class="activity-detail-section">
                    <div>
                        <h3 style="margin: 0;">Đối tượng phụ trách chính</h3>
                        <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                            Đơn vị phụ trách chính và nhân sự được phân công chịu trách nhiệm
                        </p>
                        <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                            Đơn vị: <strong><c:out value="${activity.responsibleDepartmentName}" default="Không có"/></strong>
                            - Nhân sự phụ trách chính: <strong><c:out value="${activity.responsibleStaffName}" default="Chưa có"/></strong>
                        </p>
                    </div>
                </section>

                <section class="activity-detail-section" style="grid-column: 1 / -1;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <div>
                            <h3 style="margin: 0;">Đơn vị đồng phụ trách</h3>
                            <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                                Các đơn vị cùng tham gia tổ chức hoạt động
                            </p>
                        </div>
                        <span class="activity-count"><c:out value="${empty coDepartments ? 0 : coDepartments.size()}"/> đơn vị</span>
                    </div>

                    <div class="table-wrap activity-table-wrap" style="margin-bottom: 1.5rem;">
                        <table>
                            <thead>
                            <tr>
                                <th scope="col" style="width: 60px;">STT</th>
                                <th scope="col">Mã đơn vị</th>
                                <th scope="col">Tên đơn vị</th>
                                <th scope="col">Trạng thái</th>
                                <th scope="col">Thời gian thêm</th>
                                <c:if test="${canRemoveStaff}">
                                    <th scope="col" style="text-align: right; width: 120px;">Thao tác</th>
                                </c:if>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty coDepartments}">
                                    <tr>
                                        <td colspan="${canRemoveStaff ? 6 : 5}" style="text-align: center; padding: 1.5rem; color: var(--text-muted, #888);">
                                            Chưa có đơn vị đồng phụ trách nào.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${coDepartments}" var="cd" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><strong><c:out value="${cd.departmentCode}"/></strong></td>
                                            <td><c:out value="${cd.departmentName}"/></td>
                                            <td><span class="badge status-pill ${st:css(cd.departmentStatus)}">${st:label(cd.departmentStatus)}</span></td>
                                            <td><c:out value="${cd.formattedCreatedAt}"/></td>
                                            <c:if test="${canRemoveStaff}">
                                                <td style="text-align: right;">
                                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display:inline"
                                                          data-confirm="Gỡ đơn vị này sẽ đồng thời gỡ các nhân sự chỉ thuộc đơn vị này khỏi hoạt động. Bạn có chắc chắn?">
                                                        <input type="hidden" name="action" value="removeDepartment">
                                                        <input type="hidden" name="activityId" value="${activity.id}">
                                                        <input type="hidden" name="departmentId" value="${cd.departmentId}">
                                                        <button type="submit" class="button" style="padding: 0.25rem 0.5rem; font-size: 0.8125rem; color: #dc2626; border-color: #fca5a5;">
                                                            Gỡ bỏ
                                                        </button>
                                                    </form>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${canAddStaff}">
                        <div style="background: var(--surface-secondary, #f8fafc); border: 1px solid var(--border, #e2e8f0); border-radius: 8px; padding: 1.25rem;">
                            <h4 style="margin: 0 0 0.5rem; font-size: 1rem;">Thêm đơn vị đồng phụ trách</h4>
                            <c:choose>
                                <c:when test="${not empty availableDepartments}">
                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
                                        <input type="hidden" name="action" value="addDepartment">
                                        <input type="hidden" name="activityId" value="${activity.id}">
                                        <div class="multi-select-combobox" data-multi-select data-item-label="đơn vị">
                                            <button type="button" class="multi-select-trigger" aria-expanded="false"
                                                    aria-controls="department-options">
                                                <span data-selection-label>Chọn đơn vị đồng phụ trách</span>
                                                <span aria-hidden="true">▾</span>
                                            </button>
                                            <div class="multi-select-dropdown" id="department-options" hidden>
                                                <input class="multi-select-search" type="search"
                                                       placeholder="Tìm đơn vị..." aria-label="Tìm đơn vị">
                                                <fieldset class="multi-select-options">
                                                    <legend class="visually-hidden">Chọn đơn vị</legend>
                                                    <c:forEach items="${availableDepartments}" var="dp">
                                                        <label class="multi-select-option">
                                                            <input type="checkbox" name="departmentIds" value="${dp.id}">
                                                            <span><c:out value="${dp.code}"/> - <c:out value="${dp.name}"/></span>
                                                        </label>
                                                    </c:forEach>
                                                </fieldset>
                                                <div class="multi-select-empty" hidden>Không tìm thấy đơn vị phù hợp.</div>
                                            </div>
                                            <span class="multi-select-count" aria-live="polite">Chưa chọn đơn vị</span>
                                        </div>
                                        <button type="submit" class="button button-primary">
                                            + Thêm đơn vị đồng phụ trách
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <p style="margin: 0; font-style: italic; font-size: 0.875rem; color: var(--text-muted, #64748b);">
                                        Không còn đơn vị đang hoạt động nào để thêm.
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </section>

                <section class="activity-detail-section" style="grid-column: 1 / -1;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <div>
                            <h3 style="margin: 0;">Danh sách nhân sự đồng phụ trách</h3>
                            <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                                Các nhân sự thuộc đơn vị đồng phụ trách tham gia điều phối hoạt động.
                            </p>
                            <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                                Nhân sự phụ trách chính: <strong><c:out value="${activity.responsibleStaffName}" default="Chưa có"/></strong>
                                - Đơn vị: <c:out value="${activity.responsibleDepartmentName}" default="Không có"/>
                            </p>
                        </div>
                        <span class="activity-count">
                            <c:choose>
                                <c:when test="${empty assignedStaffs}">0 nhân sự</c:when>
                                <c:otherwise><c:out value="${assignedStaffs.size()}"/> nhân sự</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="table-wrap activity-table-wrap" style="margin-bottom: 1.5rem;">
                        <table>
                            <thead>
                            <tr>
                                <th scope="col" style="width: 60px;">STT</th>
                                <th scope="col">Mã nhân sự</th>
                                <th scope="col">Họ và tên</th>
                                <th scope="col">Trạng thái nhân sự</th>
                                <th scope="col">Chức vụ</th>
                                <th scope="col">Thời gian phân công</th>
                                <c:if test="${canRemoveStaff && activity.activityStatus == 'UPCOMING'}">
                                    <th scope="col" style="text-align: right; width: 120px;">Thao tác</th>
                                </c:if>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty assignedStaffs}">
                                    <tr>
                                        <td colspan="${canRemoveStaff && activity.activityStatus == 'UPCOMING' ? 7 : 6}" style="text-align: center; padding: 1.5rem; color: var(--text-muted, #888);">
                                            Chưa có nhân sự nào được phân công phụ trách hoạt động này.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${assignedStaffs}" var="ast" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><strong><c:out value="${ast.staffCode}"/></strong></td>
                                            <td><c:out value="${ast.staffName}"/></td>
                                            <td><span class="badge status-pill ${st:css(ast.staffStatus)}">${st:label(ast.staffStatus)}</span></td>
                                            <td><c:out value="${ast.position}" default="Nhân viên"/></td>
                                            <td><c:out value="${ast.formattedAssignedOn}"/></td>
                                            <c:if test="${canRemoveStaff && activity.activityStatus == 'UPCOMING'}">
                                                <td style="text-align: right;">
                                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display:inline"
                                                      data-confirm="Bạn có chắc chắn muốn hủy phân công nhân sự này?">
                                                    <input type="hidden" name="action" value="unassignStaff">
                                                    <input type="hidden" name="activityId" value="${activity.id}">
                                                    <input type="hidden" name="staffId" value="${ast.staffId}">
                                                    <button type="submit" class="button" style="padding: 0.25rem 0.5rem; font-size: 0.8125rem; color: #dc2626; border-color: #fca5a5;">
                                                        Gỡ bỏ
                                                    </button>
                                                    </form>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${canAddStaff && activity.activityStatus == 'UPCOMING'}">
                        <div style="background: var(--surface-secondary, #f8fafc); border: 1px solid var(--border, #e2e8f0); border-radius: 8px; padding: 1.25rem;">
                            <h4 style="margin: 0 0 0.5rem; font-size: 1rem;">Phân công nhân sự mới</h4>
                            <p style="margin: 0 0 1rem; font-size: 0.875rem; color: var(--text-muted, #64748b);">
                                Chỉ nhân sự đang hoạt động thuộc đơn vị phụ trách chính (<strong><c:out value="${activity.responsibleDepartmentName}" default="${activity.responsibleDepartmentCode}"/></strong>) hoặc các đơn vị đồng phụ trách mới có thể được phân công.
                            </p>
                            <c:choose>
                                <c:when test="${not empty availableStaffs}">
                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
                                        <input type="hidden" name="action" value="assignStaff">
                                        <input type="hidden" name="activityId" value="${activity.id}">
                                        <div class="multi-select-combobox" data-multi-select data-item-label="nhân sự">
                                            <button type="button" class="multi-select-trigger" aria-expanded="false"
                                                    aria-controls="staff-options">
                                                <span data-selection-label>Chọn nhân sự để phân công</span>
                                                <span aria-hidden="true">▾</span>
                                            </button>
                                            <div class="multi-select-dropdown" id="staff-options" hidden>
                                                <input class="multi-select-search" type="search"
                                                       placeholder="Tìm nhân sự..." aria-label="Tìm nhân sự">
                                                <fieldset class="multi-select-options">
                                                    <legend class="visually-hidden">Chọn nhân sự</legend>
                                                    <c:forEach items="${availableStaffs}" var="st" >
                                                        <label class="multi-select-option">
                                                            <input type="checkbox" name="staffIds" value="${st.id}">
                                                            <span><c:if test="${not empty staffDepartmentLabels[st.id]}"><c:out value="${staffDepartmentLabels[st.id]}"/> - </c:if><c:out value="${st.code}"/> - <c:out value="${st.name}"/></span>
                                                        </label>
                                                    </c:forEach>
                                                </fieldset>
                                                <div class="multi-select-empty" hidden>Không tìm thấy nhân sự phù hợp.</div>
                                            </div>
                                            <span class="multi-select-count" aria-live="polite">Chưa chọn nhân sự</span>
                                        </div>
                                        <button type="submit" class="button button-primary">
                                            + Phân công vào hoạt động
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <p style="margin: 0; font-style: italic; font-size: 0.875rem; color: var(--text-muted, #64748b);">
                                        Không còn nhân sự đang hoạt động nào thuộc các đơn vị phụ trách chưa được phân công.
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </section>
            </div>

            <!-- TAB 3: ĐỐI TÁC HOẠT ĐỘNG -->
            <div id="tab-partner" class="activity-tab-pane" role="tabpanel" aria-labelledby="btn-tab-partner">
                <section class="activity-detail-section" style="grid-column: 1 / -1;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <div>
                            <h3 style="margin: 0;">Lời mời đối tác</h3>
                            <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                                Mời hợp tác hoặc mời ngừng hợp tác đều cần lời nhắn và chỉ có hiệu lực sau khi nhân sự được mời (hoặc trưởng/thư ký của công ty đối tác) chấp nhận.
                            </p>
                        </div>
                        <span class="activity-count"><c:out value="${empty invitations ? 0 : invitations.size()}"/> lời mời</span>
                    </div>
                    <div class="table-wrap activity-table-wrap" style="margin-bottom: 1.5rem;">
                        <table>
                            <thead>
                            <tr>
                                <th scope="col" style="width: 60px;">STT</th>
                                <th scope="col">Công ty đối tác</th>
                                <th scope="col">Nhân sự phụ trách chính</th>
                                <th scope="col">Loại lời mời</th>
                                <th scope="col">Lời nhắn</th>
                                <th scope="col">Trạng thái</th>
                                <th scope="col">Người mời</th>
                                <th scope="col">Thời gian mời</th>
                                <th scope="col" style="text-align: left;">Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty invitations}">
                                    <tr>
                                        <td colspan="9" style="text-align: center; padding: 1.5rem; color: var(--text-muted, #888);">
                                            Chưa có lời mời nào được gửi cho hoạt động này.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${invitations}" var="inv" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><strong><c:out value="${inv.partnerCompanyCode}"/></strong> - <c:out value="${inv.partnerCompanyName}"/></td>
                                            <td><c:out value="${inv.partnerStaffName}" default="—"/></td>
                                            <td>${inv.termination ? 'Mời ngừng hợp tác' : 'Mời hợp tác'}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty inv.message}">—</c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="button activity-message-view"
                                                                data-title="${inv.termination ? 'Lời nhắn mời ngừng hợp tác' : 'Lời nhắn mời hợp tác'}"
                                                                data-message="<c:out value='${inv.message}'/>">Xem</button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="badge status-pill ${st:css(inv.approvalStatus)}">
                                                    <c:choose>
                                                        <c:when test="${inv.approvalStatus == 'PENDING'}">Chờ phản hồi</c:when>
                                                        <c:when test="${!inv.termination && inv.approvalStatus == 'APPROVED' && inv.collaborationStatus != 'ACTIVE'}">Đã ngừng hợp tác</c:when>
                                                        <c:otherwise>${st:label(inv.approvalStatus)}</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                            <td><c:out value="${empty inv.createdByName ? inv.createdBy : inv.createdByName}"/></td>
                                            <td><c:out value="${inv.formattedCreatedAt}"/></td>
                                            <td style="text-align: right; white-space: nowrap;">
                                                <c:if test="${inv.canRespond && activity.activityStatus == 'UPCOMING'}">
                                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display:inline">
                                                        <input type="hidden" name="action" value="respondInvitation">
                                                        <input type="hidden" name="activityId" value="${activity.id}">
                                                        <input type="hidden" name="invitationId" value="${inv.id}">
                                                        <button type="submit" name="decision" value="accept" class="button button-approve">Chấp nhận</button>
                                                        <button type="submit" name="decision" value="reject" class="button button-reject">Từ chối</button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${inv.canRevoke}">
                                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display:inline"
                                                          data-confirm="Hủy phản hồi sẽ đưa lời mời về trạng thái chờ phản hồi${inv.approvalStatus == 'APPROVED' ? ' và gỡ các nhân sự đối tác của công ty khỏi hoạt động' : ''}. Bạn có chắc chắn?">
                                                        <input type="hidden" name="action" value="revokeInvitation">
                                                        <input type="hidden" name="activityId" value="${activity.id}">
                                                        <input type="hidden" name="invitationId" value="${inv.id}">
                                                        <button type="submit" class="button button-revoke">
                                                            ${inv.approvalStatus == 'APPROVED' ? 'Hủy duyệt' : 'Hủy từ chối'}
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${canManageActivities && inv.approvalStatus == 'PENDING' && activity.activityStatus == 'UPCOMING'}">
                                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display:inline"
                                                          data-confirm="Bạn có chắc chắn muốn hủy lời mời này?">
                                                        <input type="hidden" name="action" value="cancelInvitation">
                                                        <input type="hidden" name="activityId" value="${activity.id}">
                                                        <input type="hidden" name="invitationId" value="${inv.id}">
                                                        <button type="submit" class="button button-revoke">Hủy lời mời</button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${canManageActivities && activity.approvalStatus != 'APPROVED'}">
                        <p class="field-hint">Chỉ có thể tạo lời mời đối tác khi hoạt động đã được duyệt.</p>
                    </c:if>
                    <c:if test="${canManageActivities && activity.approvalStatus == 'APPROVED'}">
                        <button type="button" class="button button-primary" id="invite-form-toggle"
                                aria-expanded="false" aria-controls="invite-form-panel">Tạo lời mời mới</button>
                        <div id="invite-form-panel" hidden>
                        <form class="activity-partner-form" method="post" action="${pageContext.request.contextPath}/activities">
                            <input type="hidden" id="inviteAction" name="action" value="inviteCollaborator">
                            <input type="hidden" name="activityId" value="${activity.id}">
                            <div class="field" style="margin-bottom: 0.75rem;">
                                <label for="inviteType">Loại lời mời</label>
                                <select id="inviteType">
                                    <option value="inviteCollaborator">Mời hợp tác</option>
                                    <option value="terminatePartner">Mời ngừng hợp tác</option>
                                </select>
                            </div>
                            <div id="inviteTerminateFields" class="field" hidden>
                                <label id="partnerCompanyLinkLabel">Đối tác đang hợp tác</label>
                                <div class="multi-select-combobox" id="partnerLinkCombobox" data-multi-select data-single data-required
                                     data-item-label="đối tác" data-disabled>
                                    <button type="button" class="multi-select-trigger" aria-expanded="false"
                                            aria-controls="partner-link-options" aria-labelledby="partnerCompanyLinkLabel" disabled>
                                        <span data-selection-label>Chọn đối tác</span>
                                        <span aria-hidden="true">▾</span>
                                    </button>
                                    <div class="multi-select-dropdown" id="partner-link-options" hidden>
                                        <input class="multi-select-search" type="search"
                                               placeholder="Tìm đối tác..." aria-label="Tìm đối tác">
                                        <fieldset class="multi-select-options">
                                            <legend class="visually-hidden">Chọn đối tác</legend>
                                            <c:forEach var="partner" items="${approvedPartners}">
                                                <c:if test="${partner.active}">
                                                    <label class="multi-select-option">
                                                        <input type="radio" name="partnerCompanyLinkId" value="${partner.id}" disabled>
                                                        <span><c:out value="${partner.partnerCompanyCode}"/> - <c:out value="${partner.partnerCompanyName}"/></span>
                                                    </label>
                                                </c:if>
                                            </c:forEach>
                                        </fieldset>
                                        <div class="multi-select-empty" hidden>Không tìm thấy đối tác phù hợp.</div>
                                    </div>
                                    <span class="multi-select-count" aria-live="polite">Chưa chọn đối tác</span>
                                </div>
                            </div>
                            <div id="inviteCollabFields" class="form-grid activity-form-grid">
                                <div class="field">
                                    <label id="partnerCompanyLabel">Công ty đối tác</label>
                                    <div class="multi-select-combobox" id="partnerCompanyCombobox" data-multi-select data-single data-required
                                         data-item-label="công ty đối tác">
                                        <button type="button" class="multi-select-trigger" aria-expanded="false"
                                                aria-controls="partner-company-options" aria-labelledby="partnerCompanyLabel">
                                            <span data-selection-label>Chọn công ty đối tác</span>
                                            <span aria-hidden="true">▾</span>
                                        </button>
                                        <div class="multi-select-dropdown" id="partner-company-options" hidden>
                                            <input class="multi-select-search" type="search"
                                                   placeholder="Tìm công ty..." aria-label="Tìm công ty đối tác">
                                            <fieldset class="multi-select-options">
                                                <legend class="visually-hidden">Chọn công ty đối tác</legend>
                                                <c:forEach var="company" items="${partnerCompanies}">
                                                    <label class="multi-select-option">
                                                        <input type="radio" name="partnerCompanyId" value="${company.id}">
                                                        <span><c:out value="${company.code}"/> - <c:out value="${company.name}"/></span>
                                                    </label>
                                                </c:forEach>
                                            </fieldset>
                                            <div class="multi-select-empty" hidden>Không tìm thấy công ty phù hợp.</div>
                                        </div>
                                        <span class="multi-select-count" aria-live="polite">Chưa chọn công ty đối tác</span>
                                    </div>
                                </div>
                                <div class="field">
                                    <label id="partnerStaffLabel">Nhân sự đối tác phụ trách chính</label>
                                    <div class="multi-select-combobox" id="partnerStaffCombobox" data-multi-select data-single data-required
                                         data-item-label="nhân sự phụ trách chính" data-disabled
                                         data-input-name="partnerStaffId"
                                         data-staff-endpoint="${pageContext.request.contextPath}/activities">
                                        <button type="button" class="multi-select-trigger" aria-expanded="false"
                                                aria-controls="partner-staff-options" aria-labelledby="partnerStaffLabel" disabled>
                                            <span data-selection-label>Chọn nhân sự phụ trách chính</span>
                                            <span aria-hidden="true">▾</span>
                                        </button>
                                        <div class="multi-select-dropdown" id="partner-staff-options" hidden>
                                            <input class="multi-select-search" type="search"
                                                   placeholder="Tìm nhân sự..." aria-label="Tìm nhân sự đối tác">
                                            <fieldset class="multi-select-options">
                                                <legend class="visually-hidden">Chọn nhân sự phụ trách chính</legend>
                                            </fieldset>
                                            <div class="multi-select-empty" hidden>Không tìm thấy nhân sự phù hợp.</div>
                                        </div>
                                        <span class="multi-select-count" aria-live="polite">Chưa chọn nhân sự phụ trách chính</span>
                                    </div>
                                    <span id="partnerStaffMessage" class="field-hint" aria-live="polite"></span>
                                </div>
                            </div>
<div class="field">
                                <label for="inviteMessage">Lời nhắn gửi đối tác</label>
                                <textarea id="inviteMessage" name="message" rows="2" maxlength="500" required
                                          placeholder="Nội dung lời mời / lý do ngừng hợp tác"></textarea>
                            </div>
                            <div class="activity-partner-form-actions">
                                <button type="submit" class="button button-primary">Gửi lời mời</button>
                            </div>
                        </form>
                        </div>
                    </c:if>
                </section>

                <section class="activity-detail-section" style="grid-column: 1 / -1;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <div>
                            <h3 style="margin: 0;">Đối tác tham gia hoạt động</h3>
                            <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                                Các đối tác của hoạt động và trạng thái tham gia; mỗi đối tác có một nhân sự phụ trách chính.
                            </p>
                        </div>
                        <span class="activity-count"><c:out value="${empty approvedPartners ? 0 : approvedPartners.size()}"/> đối tác</span>
                    </div>
                    <div class="table-wrap activity-table-wrap">
                        <table>
                            <thead>
                            <tr>
                                <th scope="col" style="width: 60px;">STT</th>
                                <th scope="col">Công ty đối tác</th>
                                <th scope="col">Nhân sự phụ trách chính</th>
                                <th scope="col">Chức vụ</th>
                                <th scope="col">Trạng thái</th>
                                <th scope="col">Ngày tham gia</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty approvedPartners}">
                                    <tr>
                                        <td colspan="6" style="text-align: center; padding: 1.5rem; color: var(--text-muted, #888);">
                                            Chưa có đối tác nào được mời tham gia hoạt động này.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${approvedPartners}" var="partner" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><strong><c:out value="${partner.partnerCompanyCode}"/></strong> - <c:out value="${partner.partnerCompanyName}"/></td>
                                            <td><c:out value="${partner.partnerStaffName}" default="—"/></td>
                                            <td><c:out value="${partner.partnerStaffPosition}" default="—"/></td>
                                            <td>
                                                <span class="badge status-pill ${partner.active ? 'status-green' : 'status-gray'}">
                                                    ${partner.active ? 'Đang hoạt động' : 'Không hoạt động'}
                                                </span>
                                            </td>
                                            <td><c:out value="${partner.formattedCreatedAt}"/></td>
                                                                                    </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                </section>

                <section class="activity-detail-section" style="grid-column: 1 / -1;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <div>
                            <h3 style="margin: 0;">Danh sách nhân sự đối tác phụ trách</h3>
                            <p style="margin: 0.25rem 0 0; font-size: 0.875rem; color: var(--text-muted, #666);">
                                Các nhân sự của đối tác tham gia điều phối hoạt động.
                            </p>
                        </div>
                        <span class="activity-count"><c:out value="${empty assignedPartnerStaffs ? 0 : assignedPartnerStaffs.size()}"/> nhân sự</span>
                    </div>

                    <div class="table-wrap activity-table-wrap" style="margin-bottom: 1.5rem;">
                        <table>
                            <thead>
                            <tr>
                                <th scope="col" style="width: 60px;">STT</th>
                                <th scope="col">Mã nhân sự</th>
                                <th scope="col">Họ và tên</th>
                                <th scope="col">Công ty</th>
                                <th scope="col">Chức vụ</th>
                                <th scope="col">Trạng thái</th>
                                <th scope="col">Thời gian phân công</th>
                                <c:if test="${canManagePartnerStaff && activity.activityStatus == 'UPCOMING'}">
                                    <th scope="col" style="text-align: right; width: 120px;">Thao tác</th>
                                </c:if>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty assignedPartnerStaffs}">
                                    <tr>
                                        <td colspan="${canManagePartnerStaff && activity.activityStatus == 'UPCOMING' ? 8 : 7}" style="text-align: center; padding: 1.5rem; color: var(--text-muted, #888);">
                                            Chưa có nhân sự đối tác nào được phân công phụ trách hoạt động này.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${assignedPartnerStaffs}" var="aps" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><strong><c:out value="${aps.partnerStaffCode}"/></strong></td>
                                            <td><c:out value="${aps.partnerStaffName}"/></td>
                                            <td><c:out value="${aps.companyName}" default="—"/></td>
                                            <td><c:out value="${aps.position}" default="—"/></td>
                                            <td><span class="badge status-pill ${st:css(aps.status)}">${st:label(aps.status)}</span></td>
                                            <td><c:out value="${aps.formattedAssignedOn}"/></td>
                                            <c:if test="${canManagePartnerStaff && activity.activityStatus == 'UPCOMING'}">
                                                <td style="text-align: right;">
                                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display:inline"
                                                          data-confirm="Bạn có chắc chắn muốn gỡ nhân sự đối tác này?">
                                                        <input type="hidden" name="action" value="unassignPartnerStaff">
                                                        <input type="hidden" name="activityId" value="${activity.id}">
                                                        <input type="hidden" name="partnerStaffId" value="${aps.partnerStaffId}">
                                                        <button type="submit" class="button" style="padding: 0.25rem 0.5rem; font-size: 0.8125rem; color: #dc2626; border-color: #fca5a5;">
                                                            Gỡ bỏ
                                                        </button>
                                                    </form>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${canManagePartnerStaff && activity.activityStatus == 'UPCOMING'}">
                        <div style="background: var(--surface-secondary, #f8fafc); border: 1px solid var(--border, #e2e8f0); border-radius: 8px; padding: 1.25rem;">
                            <h4 style="margin: 0 0 0.5rem; font-size: 1rem;">Phân công nhân sự đối tác mới</h4>
                            <p style="margin: 0 0 1rem; font-size: 0.875rem; color: var(--text-muted, #64748b);">
                                Chỉ nhân sự thuộc các công ty đối tác đã chấp nhận tham gia hoạt động mới có thể được phân công; không bao gồm nhân sự đối tác phụ trách chính.
                            </p>
                            <c:choose>
                                <c:when test="${not empty availablePartnerStaffs}">
                                    <form action="${pageContext.request.contextPath}/activities" method="post" style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
                                        <input type="hidden" name="action" value="assignPartnerStaff">
                                        <input type="hidden" name="activityId" value="${activity.id}">
                                        <div class="multi-select-combobox" data-multi-select data-item-label="nhân sự đối tác">
                                            <button type="button" class="multi-select-trigger" aria-expanded="false"
                                                    aria-controls="partner-staff-options">
                                                <span data-selection-label>Chọn nhân sự đối tác để phân công</span>
                                                <span aria-hidden="true">▾</span>
                                            </button>
                                            <div class="multi-select-dropdown" id="partner-staff-options" hidden>
                                                <input class="multi-select-search" type="search"
                                                       placeholder="Tìm nhân sự đối tác..." aria-label="Tìm nhân sự đối tác">
                                                <fieldset class="multi-select-options">
                                                    <legend class="visually-hidden">Chọn nhân sự đối tác</legend>
                                                    <c:forEach items="${availablePartnerStaffs}" var="pst">
                                                        <label class="multi-select-option">
                                                            <input type="checkbox" name="partnerStaffIds" value="${pst.id}">
                                                            <span><c:out value="${pst.code}"/> - <c:out value="${pst.name}"/></span>
                                                        </label>
                                                    </c:forEach>
                                                </fieldset>
                                                <div class="multi-select-empty" hidden>Không tìm thấy nhân sự phù hợp.</div>
                                            </div>
                                            <span class="multi-select-count" aria-live="polite">Chưa chọn nhân sự đối tác</span>
                                        </div>
                                        <button type="submit" class="button button-primary">+ Phân công vào hoạt động</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <p style="margin: 0; font-style: italic; font-size: 0.875rem; color: var(--text-muted, #64748b);">
                                        Không còn nhân sự đối tác nào có thể được phân công.
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </section>
            </div>
        </div>

        <div class="detail-actions activity-detail-actions">
            <a class="button" href="${pageContext.request.contextPath}/activities">Đóng</a>
            <%-- DRAFT / REJECTED: gửi duyệt --%>
            <c:if test="${activity.activityStatus == 'UPCOMING' && (activity.approvalStatus == 'DRAFT' || activity.approvalStatus == 'REJECTED')}">
                <form action="${pageContext.request.contextPath}/activity/approval" method="post" style="display:inline">
                    <input type="hidden" name="id" value="${activity.id}">
                    <button type="submit" name="action" value="submit" class="button button-send-approval button-primary">Gửi duyệt</button>
                </form>
            </c:if>

            <%-- PENDING: duyệt / từ chối --%>
            <c:if test="${canApproveActivities && activity.activityStatus == 'UPCOMING' && activity.approvalStatus == 'PENDING'}">
                <form action="${pageContext.request.contextPath}/activity/approval" method="post" style="display:inline">
                    <input type="hidden" name="id" value="${activity.id}">
                    <button type="submit" name="action" value="approve" class="button button-approve button-primary">Duyệt</button>
                    <button type="submit" name="action" value="reject" class="button button-reject">Từ chối</button>
                </form>
            </c:if>

            <%-- APPROVED (chỉ khi hoạt động chưa diễn ra): hủy duyệt --%>
            <c:if test="${canApproveActivities && activity.approvalStatus == 'APPROVED' && activity.activityStatus == 'UPCOMING'}">
                <form action="${pageContext.request.contextPath}/activity/approval" method="post" style="display:inline">
                    <input type="hidden" name="id" value="${activity.id}">
                    <button type="submit" name="action" value="revokeApproval" class="button button-revoke"
                            data-confirm="Hủy duyệt và đưa hoạt động về trạng thái chờ duyệt?">Hủy duyệt</button>
                </form>
            </c:if>

            <%-- REJECTED: hủy từ chối --%>
            <c:if test="${canApproveActivities && activity.activityStatus == 'UPCOMING' && activity.approvalStatus == 'REJECTED'}">
                <form action="${pageContext.request.contextPath}/activity/approval" method="post" style="display:inline">
                    <input type="hidden" name="id" value="${activity.id}">
                    <button type="submit" name="action" value="revokeRejection" class="button button-revoke"
                            data-confirm="Hủy từ chối và đưa hoạt động về trạng thái chờ duyệt?">Hủy từ chối</button>
                </form>
            </c:if>
        </div>
    </section>
        </main>
    </div>
</div>
<dialog id="activity-message-modal" class="activity-message-modal">
    <h3 id="activity-message-modal-title" style="margin-top: 0;">Lời nhắn</h3>
    <p id="activity-message-modal-body" style="white-space: pre-wrap; word-break: break-word;"></p>
    <div style="text-align: right;"><button type="button" class="button" id="activity-message-modal-close">Đóng</button></div>
</dialog>
<script src="${pageContext.request.contextPath}/assets/js/activity.js?v=20261020-2"></script>
</body>
</html>
