<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activeNav" value="staff" scope="request"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <meta charset="UTF-8">
    <title>Staff List</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>

<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">

<div class="page staff-list-page">

    <!-- ================= HEADER ================= -->
    <div class="activity-hero staff-hero">

        <div>

            <span class="eyebrow">
                SWP391-G1 <span aria-hidden="true">/</span> STAFF MANAGEMENT
            </span>

            <h1>
                Danh sách cán bộ / nhân viên
            </h1>

            <p>
                Quản lý và tra cứu thông tin cán bộ, nhân viên
            </p>

        </div>

        <div class="staff-hero-actions">
            <a href="${pageContext.request.contextPath}/home"
               class="staff-home-button">
                Trang chủ
            </a>
        </div>

    </div>


    <!-- ================= SEARCH + FILTER ================= -->
    <div class="staff-toolbar">

        <!-- Search -->
        <div class="staff-search-wrapper">

            <input
                    type="text"
                    id="staffSearch"
                    class="staff-search"
                    placeholder="Tìm theo mã hoặc họ tên..."
            >

        </div>


        <!-- Filter -->
        <select
                id="staffStatusFilter"
                class="staff-filter"
        >
            <option value="">Tất cả trạng thái</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
        </select>

    </div>


    <!-- ================= TABLE CARD ================= -->
    <div class="card staff-table-card">

        <div class="staff-table-heading">

            <div>

                <h2>
                    Staff List
                </h2>

                <p>
                    Danh sách cán bộ / nhân viên trong hệ thống
                </p>

            </div>

            <span class="staff-count">
                <c:out value="${staffList.size()}"/> cán bộ
            </span>

        </div>


        <!-- ================= TABLE ================= -->
        <div class="staff-table-wrap">

            <table>

                <thead>

                <tr>
                    <th>ID</th>
                    <th>Mã cán bộ</th>
                    <th>Họ và tên</th>
                    <th>Ngày sinh</th>
                    <th>Giới tính</th>
                    <th>Account ID</th>
                    <th>Trạng thái</th>
                </tr>

                </thead>


                <tbody>

                <c:forEach var="staff" items="${staffList}">

                    <tr
                            class="staff-row"
                            data-status="${staff.status}"
                    >

                        <!-- ID -->
                        <td>
                            <c:out value="${staff.id}"/>
                        </td>


                        <!-- Staff Code -->
                        <td>

                            <strong class="staff-code">
                                <c:out value="${staff.code}"/>
                            </strong>

                        </td>


                        <!-- Name -->
                        <td>

                            <strong>
                                <c:out value="${staff.name}"/>
                            </strong>

                        </td>


                        <!-- Date of Birth -->
                        <td>

                            <c:choose>

                                <c:when test="${not empty staff.dob}">
                                    <c:out value="${staff.dob}"/>
                                </c:when>

                                <c:otherwise>
                                    <span class="empty-value">
                                        --
                                    </span>
                                </c:otherwise>

                            </c:choose>

                        </td>


                        <!-- Gender -->
                        <td>

                            <c:choose>

                                <c:when test="${staff.gender == 'true'}">

                                    <span class="gender-badge male">
                                        Nam
                                    </span>

                                </c:when>

                                <c:when test="${staff.gender == 'false'}">

                                    <span class="gender-badge female">
                                        Nữ
                                    </span>

                                </c:when>

                                <c:otherwise>

                                    <span class="empty-value">
                                        --
                                    </span>

                                </c:otherwise>

                            </c:choose>

                        </td>


                        <!-- Account ID -->
                        <td>

                            <c:choose>

                                <c:when test="${staff.accountId > 0}">
                                    <c:out value="${staff.accountId}"/>
                                </c:when>

                                <c:otherwise>
                                    <span class="empty-value">
                                        --
                                    </span>
                                </c:otherwise>

                            </c:choose>

                        </td>


                        <!-- Status -->
                        <td>

                            <c:choose>

                                <c:when test="${staff.status == 'ACTIVE'}">

                                    <span class="status-badge active">
                                        Đang hoạt động
                                    </span>

                                </c:when>

                                <c:when test="${staff.status == 'INACTIVE'}">

                                    <span class="status-badge inactive">
                                        Không hoạt động
                                    </span>

                                </c:when>

                                <c:otherwise>

                                    <span class="status-badge">
                                        <c:out value="${staff.status}"/>
                                    </span>

                                </c:otherwise>

                            </c:choose>

                        </td>

                    </tr>

                </c:forEach>

                </tbody>

            </table>

        </div>


        <!-- ================= EMPTY STATE ================= -->
        <div
                id="staffEmptyState"
                class="staff-empty-state"
                style="display: none;"
        >

            <h3>
                Không tìm thấy cán bộ
            </h3>

            <p>
                Không có cán bộ nào phù hợp với điều kiện tìm kiếm.
            </p>

        </div>

    </div>

</div>


<!-- ================= JAVASCRIPT ================= -->
<script src="${pageContext.request.contextPath}/assets/js/staff.js"></script>

        </div>
    </div>
</div>
</body>

</html>