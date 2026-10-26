<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dt" uri="http://swp391.g1/datetime" %>
<%--@elvariable id="formData" type="com.swp391.g1.dto.request.ExtracurricularActivityRequestDTO"--%>
<%--@elvariable id="activity" type="com.swp391.g1.model.ExtracurricularActivity"--%>
<c:set var="activeNav" value="activities" scope="request"/>
<c:set var="semesterValue" value="${not empty formData ? formData.semesterId : activity.semesterId}"/>
<c:set var="typeValue" value="${not empty formData ? formData.activityTypeId : activity.activityTypeId}"/>
<c:set var="departmentValue" value="${not empty formData ? formData.responsibleDepartmentId : activity.responsibleDepartmentId}"/>
<c:set var="staffValue" value="${not empty formData ? formData.responsibleStaffId : activity.responsibleStaffId}"/>
<c:set var="registrationValue" value="${not empty formData ? formData.registrationFormRequired == 'true' : activity.registrationFormRequired}"/>
<c:set var="summaryValue" value="${not empty formData ? formData.summaryRequired == 'true' : activity.summaryRequired}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${isEdit}">Cập nhật hoạt động</c:when><c:otherwise>Tạo hoạt động</c:otherwise></c:choose> | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.css">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>

    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <main class="page activity-list-page activity-form-page">
            <header class="activity-hero">
                <div class="activity-hero-copy">
                    <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ HOẠT ĐỘNG</span>
                    <h1><c:choose><c:when test="${isEdit}">Cập nhật hoạt động</c:when><c:otherwise>Tạo hoạt động mới</c:otherwise></c:choose></h1>
                    <p>Điền thông tin để quản lý và theo dõi hoạt động ngoại khóa.</p>
                </div>
            </header>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error activity-alert"><c:out value="${errorMessage}"/></div>
    </c:if>

    <section class="card activity-form-card">
        <div class="activity-table-heading">
            <div>
                <h2>Thông tin hoạt động</h2>
                <p>Các trường có dấu <span class="required-mark">*</span> là bắt buộc</p>
            </div>
        </div>
        <form id="activityForm" novalidate method="post" action="${pageContext.request.contextPath}/activities"
              <c:if test="${isEdit and activity.approvalStatus == 'APPROVED'}">data-confirm="Hoạt động đã duyệt mà có thay đổi nội dung sẽ chuyển về Bản nháp và cần gửi duyệt lại. Bạn có chắc chắn muốn cập nhật thông tin hoạt động?"</c:if>>
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${activity.id}">
            </c:if>

            <div class="activity-form-content">
            <div class="activity-form-section form-section">
                <h3>Thông tin cơ bản</h3>
                <p>Thông tin nhận diện và phân loại hoạt động.</p>
            </div>
            <div class="form-grid activity-form-grid">
                <div class="field form-group-custom">
                    <label for="code">Mã hoạt động <span class="required-mark">*</span></label>
                    <input id="code" name="code" type="text" maxlength="50" data-required
                           value="<c:out value='${not empty formData ? formData.code : activity.code}'/>">
                    <small class="text-danger error-msg"></small>
                </div>
                <div class="field form-group-custom">
                    <label for="name">Tên hoạt động <span class="required-mark">*</span></label>
                    <input id="name" name="name" type="text" maxlength="255" data-required
                           value="<c:out value='${not empty formData ? formData.name : activity.name}'/>">
                    <small class="text-danger error-msg"></small>
                </div>
                <div class="field form-group-custom">
                    <label for="semesterId">Học kỳ <span class="required-mark">*</span></label>
                    <select id="semesterId" name="semesterId" data-required>
                        <option value="">-- Chọn học kỳ --</option>
                        <c:forEach var="semester" items="${semesters}">
                            <option value="${semester.id}" <c:if test="${semesterValue == semester.id}">selected</c:if>>
                                <c:out value="${semester.code}"/> - <c:out value="${semester.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <small class="text-danger error-msg"></small>
                </div>
                <div class="field form-group-custom">
                    <label for="activityTypeId">Loại hoạt động <span class="required-mark">*</span></label>
                    <select id="activityTypeId" name="activityTypeId" data-required>
                        <option value="">-- Chọn loại hoạt động --</option>
                        <c:forEach var="type" items="${activityTypes}">
                            <option value="${type.id}" <c:if test="${typeValue == type.id}">selected</c:if>>
                                <c:out value="${type.code}"/> - <c:out value="${type.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <small class="text-danger error-msg"></small>
                </div>
            </div>

            <div class="activity-form-section form-section">
                <h3>Phụ trách chính</h3>
                <p>Chọn đơn vị phụ trách chính và nhân viên tổ chức. Đối tác được khai báo riêng trong tab "Đối tác hoạt động" ở trang chi tiết.</p>
            </div>
            <div class="form-grid activity-form-grid">
                <div class="field form-group-custom">
                    <label id="responsibleDepartmentLabel">Đơn vị phụ trách chính <span class="required-mark">*</span></label>
                    <div class="multi-select-combobox" id="departmentCombobox" data-multi-select data-single data-required
                         data-item-label="đơn vị" data-input-name="responsibleDepartmentId"
                         data-staff-endpoint="${pageContext.request.contextPath}/activities/staffs-by-department">
                        <button type="button" class="multi-select-trigger" aria-expanded="false"
                                aria-controls="department-options" aria-labelledby="responsibleDepartmentLabel">
                            <span data-selection-label>Chọn đơn vị</span>
                            <span aria-hidden="true">▾</span>
                        </button>
                        <div class="multi-select-dropdown" id="department-options" hidden>
                            <input class="multi-select-search" type="search" placeholder="Tìm đơn vị..." aria-label="Tìm đơn vị">
                            <fieldset class="multi-select-options">
                                <legend class="visually-hidden">Chọn đơn vị</legend>
                                <c:forEach var="department" items="${departments}">
                                    <label class="multi-select-option">
                                        <input type="radio" name="responsibleDepartmentId" value="${department.id}" <c:if test="${departmentValue == department.id}">checked</c:if>>
                                        <span><c:out value="${department.code}"/> - <c:out value="${department.name}"/></span>
                                    </label>
                                </c:forEach>
                            </fieldset>
                            <div class="multi-select-empty" hidden>Không tìm thấy đơn vị phù hợp.</div>
                        </div>
                        <span class="multi-select-count" aria-live="polite">Chưa chọn đơn vị</span>
                    </div>
                </div>
                <c:set var="staffLocked" value="${empty departmentValue or departmentValue == 0}"/>
                <c:set var="departmentLabel" value=""/>
                <c:forEach var="department" items="${departments}">
                    <c:if test="${departmentValue == department.id}"><c:set var="departmentLabel" value="${department.code} - ${department.name}"/></c:if>
                </c:forEach>
                <div class="field form-group-custom">
                    <label id="responsibleStaffLabel">Nhân viên phụ trách chính <span class="required-mark">*</span></label>
                    <div class="multi-select-combobox" id="staffCombobox" data-multi-select data-single data-required
                         data-item-label="nhân viên" data-input-name="responsibleStaffId"
                         <c:if test="${staffLocked}">data-disabled</c:if>>
                        <button type="button" class="multi-select-trigger" aria-expanded="false"
                                aria-controls="staff-options" aria-labelledby="responsibleStaffLabel"
                                <c:if test="${staffLocked}">disabled</c:if>>
                            <span data-selection-label><c:choose><c:when test="${staffLocked}">Chọn đơn vị phụ trách trước</c:when><c:otherwise>Chọn nhân viên</c:otherwise></c:choose></span>
                            <span aria-hidden="true">▾</span>
                        </button>
                        <div class="multi-select-dropdown" id="staff-options" hidden>
                            <input class="multi-select-search" type="search" placeholder="Tìm nhân viên..." aria-label="Tìm nhân viên">
                            <fieldset class="multi-select-options">
                                <legend class="visually-hidden">Chọn nhân viên</legend>
                                <c:forEach var="staff" items="${staffs}">
                                    <label class="multi-select-option">
                                        <input type="radio" name="responsibleStaffId" value="${staff.id}" <c:if test="${staffValue == staff.id}">checked</c:if> <c:if test="${staffLocked}">disabled</c:if>>
                                        <span><c:out value="${staff.code}"/> - <c:out value="${staff.name}"/></span>
                                    </label>
                                </c:forEach>
                            </fieldset>
                            <div class="multi-select-empty" hidden>Không tìm thấy nhân viên phù hợp.</div>
                        </div>
                        <span class="multi-select-count" aria-live="polite">Chưa chọn nhân viên</span>
                    </div>
                    <span id="responsibleStaffMessage" class="field-hint helper-text" aria-live="polite"></span>
                </div>
            </div>

            <div class="activity-form-section form-section">
                <h3>Thời gian và địa điểm</h3>
                <p>Sắp xếp thời gian diễn ra và nơi tổ chức hoạt động.</p>
            </div>
            <div class="form-grid activity-form-grid">
                <div class="field form-group-custom">
                    <label for="startTime">Thời gian bắt đầu <span class="required-mark">*</span></label>
                    <div class="date-input-group">
                        <input id="startTime" name="startTime" type="text" autocomplete="off" placeholder="dd/mm/yyyy HH:mm" data-required
                           value="<c:out value='${not empty formData ? formData.startTime : dt:formatInput(activity.startTime)}'/>">
                        <span class="date-input-icon" aria-hidden="true"><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg></span>
                    </div>
                    <small class="text-danger error-msg"></small>
                </div>
                <div class="field form-group-custom">
                    <label for="endTime">Thời gian kết thúc <span class="required-mark">*</span></label>
                    <div class="date-input-group">
                        <input id="endTime" name="endTime" type="text" autocomplete="off" placeholder="dd/mm/yyyy HH:mm" data-required
                           value="<c:out value='${not empty formData ? formData.endTime : dt:formatInput(activity.endTime)}'/>">
                        <span class="date-input-icon" aria-hidden="true"><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg></span>
                    </div>
                    <small class="text-danger error-msg"></small>
                </div>
                <div class="field field-wide">
                    <label for="address">Địa điểm</label>
                    <input id="address" name="address" type="text" maxlength="500"
                           value="<c:out value='${not empty formData ? formData.address : activity.address}'/>">
                </div>
            </div>

            <div class="activity-form-section form-section">
                <h3>Cấu hình và mô tả</h3>
                <p>Cấu hình tính chất, số lượng tham gia và thông tin bổ sung.</p>
            </div>
            <div class="form-grid activity-form-grid">
                <div class="field form-group-custom">
                    <label for="nature">Tính chất <span class="required-mark">*</span></label>
                    <c:set var="natureValue" value="${not empty formData ? formData.nature : activity.nature}"/>
                    <select id="nature" name="nature" data-required>
                        <option value="">-- Chọn tính chất --</option>
                        <option value="REQUIRE_REGISTRATION" ${natureValue == 'REQUIRE_REGISTRATION' ? 'selected' : ''}>Cần đăng ký</option>
                        <option value="VOLUNTARY" ${natureValue == 'VOLUNTARY' ? 'selected' : ''}>Tự nguyện</option>
                        <option value="MANDATORY" ${natureValue == 'MANDATORY' ? 'selected' : ''}>Bắt buộc</option>
                    </select>
                    <small class="text-danger error-msg"></small>
                </div>
                <div class="field form-group-custom">
                    <label for="maxParticipants">Số lượng tham gia tối đa</label>
                    <input id="maxParticipants" name="maxParticipants" type="number" min="1" step="1"
                           placeholder="Không giới hạn"
                           value="<c:out value='${not empty formData ? formData.maxParticipants : activity.maxParticipants}'/>">
                </div>
                <div class="checkbox-group-container activity-checkbox-row">
                    <div class="checkbox-inline">
                        <input id="summaryRequired" name="summaryRequired" type="checkbox" value="true" <c:if test="${summaryValue}">checked</c:if>>
                        <label for="summaryRequired">Yêu cầu nộp bài tổng kết</label>
                    </div>
                </div>
                <div>
                    <div class="checkbox-inline">
                        <input id="registrationFormRequired" name="registrationFormRequired" type="checkbox" value="true" <c:if test="${registrationValue}">checked</c:if>>
                        <label for="registrationFormRequired">Yêu cầu sinh viên đăng ký bằng form</label>
                    </div>
                </div>
                <div class="field field-wide">
                    <label for="description">Mô tả</label>
                    <textarea id="description" name="description"><c:out value="${not empty formData ? formData.description : activity.description}"/></textarea>
                </div>
            </div>
            </div>

            <div class="form-actions-footer activity-form-actions">
                <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/activities">Hủy</a>
                <button class="button button-primary" type="submit">
                    <c:choose><c:when test="${isEdit}">Lưu thay đổi</c:when><c:otherwise>Tạo hoạt động</c:otherwise></c:choose>
                </button>
            </div>
        </form>
    </section>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/l10n/vn.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/activity.js?v=20261016-1"></script>
<script src="${pageContext.request.contextPath}/assets/js/activity-form.js?v=20261020-9"></script>
</body>
</html>
