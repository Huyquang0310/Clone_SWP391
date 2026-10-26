<%@ page pageEncoding="UTF-8" %>
<%--@elvariable id="formData" type="com.swp391.g1.dto.request.PartnerStaffRequestDTO"--%>
<%--@elvariable id="partnerStaff" type="com.swp391.g1.model.PartnerStaff"--%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="selectedCompany" value="${not empty formData ? formData.companyId : partnerStaff.companyId}"/>
<c:set var="selectedGender" value="${not empty formData ? formData.gender : (partnerStaff.gender == null ? '' : (partnerStaff.gender ? '1' : '0'))}"/>
<c:set var="selectedStatus" value="${not empty formData ? formData.status : partnerStaff.status}"/>
<c:choose>
    <c:when test="${not empty formData.dob}">
        <c:set var="dobValue" value="${formData.dob}"/>
    </c:when>
    <c:when test="${not empty partnerStaff.dob}">
        <c:set var="dobValue" value="${fn:substring(partnerStaff.dob, 8, 10)}-${fn:substring(partnerStaff.dob, 5, 7)}-${fn:substring(partnerStaff.dob, 0, 4)}"/>
    </c:when>
</c:choose>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activeNav" value="partner-staff" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${isEdit}">Cập nhật nhân viên</c:when><c:otherwise>Thêm nhân viên đối tác</c:otherwise></c:choose> | SWP391-G1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/partner-staff.css">
</head>
<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">
<main class="page activity-list-page activity-form-page">
    <header class="activity-hero">
        <div class="activity-hero-copy">
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ ĐỐI TÁC</span>
            <h1><c:choose><c:when test="${isEdit}">Cập nhật nhân viên</c:when><c:otherwise>Thêm nhân viên đối tác</c:otherwise></c:choose></h1>
            <p>Nhập thông tin nhân viên và công ty đối tác liên quan.</p>
        </div>
        <a class="button activity-create-button" href="${pageContext.request.contextPath}/partner-staffs">
            <svg class="ui-icon" aria-hidden="true"><use href="${pageContext.request.contextPath}/assets/icons/ui-icons.svg#arrow-left"/></svg>
            Danh sách nhân viên
        </a>
    </header>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error activity-alert"><c:out value="${errorMessage}"/></div>
    </c:if>

    <section class="card activity-form-card">
        <div class="activity-table-heading">
            <div>
                <h2>Thông tin nhân viên</h2>
                <p>Các trường có dấu <span class="required-mark">*</span> là bắt buộc</p>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/partner-staffs">
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${not empty formData ? formData.id : partnerStaff.id}">
            </c:if>

            <div class="activity-form-content">
                <div class="activity-form-section">
                    <h3>Thông tin cơ bản</h3>
                    <p>Thông tin nhận diện và liên hệ công việc của nhân viên.</p>
                </div>
                <div class="form-grid activity-form-grid">
                    <div class="field">
                        <label for="code">Mã nhân viên <span class="required-mark">*</span></label>
                        <input id="code" name="code" type="text" maxlength="50" required
                               value="<c:out value='${not empty formData ? formData.code : partnerStaff.code}'/>">
                    </div>
                    <div class="field">
                        <label for="name">Họ và tên <span class="required-mark">*</span></label>
                        <input id="name" name="name" type="text" maxlength="255" required
                               value="<c:out value='${not empty formData ? formData.name : partnerStaff.name}'/>">
                    </div>
                    <div class="field">
                        <label for="companyId">Công ty đối tác <span class="required-mark">*</span></label>
                        <select id="companyId" name="companyId" required <c:if test="${isEdit}">disabled</c:if>>
                            <option value="">-- Chọn công ty --</option>
                            <c:forEach var="company" items="${partnerCompanies}">
                                <option value="${company.id}" <c:if test="${selectedCompany == company.id}">selected</c:if>>
                                    <c:out value="${company.code}"/> - <c:out value="${company.name}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${isEdit}">
                            <input type="hidden" name="companyId" value="<c:out value='${selectedCompany}'/>">
                        </c:if>
                    </div>
                    <div class="field">
                        <label for="position">Chức vụ</label>
                        <input id="position" name="position" type="text" maxlength="255"
                               value="<c:out value='${not empty formData ? formData.position : partnerStaff.position}'/>">
                    </div>
                    <div class="field">
                        <label for="dob">Ngày sinh</label>
                        <input id="dob" name="dob" type="text" inputmode="numeric" maxlength="10"
                               pattern="(?:0[1-9]|[12][0-9]|3[01])-(?:0[1-9]|1[0-2])-[0-9]{4}"
                               placeholder="dd-mm-yyyy" title="Nhập ngày theo định dạng dd-mm-yyyy"
                               value="<c:out value='${dobValue}'/>">
                    </div>
                    <div class="field">
                        <label for="gender">Giới tính</label>
                        <select id="gender" name="gender">
                            <option value="" <c:if test="${empty selectedGender}">selected</c:if>>-- Chưa cập nhật --</option>
                            <option value="1" <c:if test="${selectedGender == '1' or selectedGender == 'true'}">selected</c:if>>Nam</option>
                            <option value="0" <c:if test="${selectedGender == '0' or selectedGender == 'false'}">selected</c:if>>Nữ</option>
                        </select>
                    </div>
                    <c:if test="${isEdit}">
                        <div class="field">
                            <label for="status">Trạng thái</label>
                            <select id="status" name="status">
                                <option value="ACTIVE" <c:if test="${selectedStatus == 'ACTIVE'}">selected</c:if>>Đang hoạt động</option>
                                <option value="INACTIVE" <c:if test="${selectedStatus == 'INACTIVE'}">selected</c:if>>Không hoạt động</option>
                            </select>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="form-actions activity-form-actions">
                <a class="button activity-cancel-button" href="${pageContext.request.contextPath}/partner-staffs">Hủy</a>
                <button class="button button-primary" type="submit">
                    <c:choose><c:when test="${isEdit}">Lưu thay đổi</c:when><c:otherwise>Thêm nhân viên</c:otherwise></c:choose>
                </button>
            </div>
        </form>
    </section>
</main>
        </div>
    </div>
</div>
</body>
</html>
