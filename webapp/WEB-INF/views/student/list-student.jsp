<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="activeNav" value="students" scope="request"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity.css?v=20261020-11">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Student List</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
          rel="stylesheet">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/student.css?v=3">
</head>

<body>
<div class="activity-app-shell">
    <jsp:include page="/WEB-INF/views/layout/app-navigation.jsp"/>
    <div class="activity-app-layout">
        <jsp:include page="/WEB-INF/views/layout/app-sidebar.jsp"/>
        <div class="activity-app-main">

<div class="student-list-page">

    <!-- ================= HEADER ================= -->
    <header class="activity-hero student-hero">

        <div>
            <span class="eyebrow">SWP391-G1 <span aria-hidden="true">/</span> QUẢN LÝ SINH VIÊN</span>

            <h1>Danh sách sinh viên</h1>

            <p>
                Quản lý và tra cứu thông tin sinh viên
            </p>
        </div>

        <div class="student-hero-actions">
            <a class="student-home-button"
               href="${pageContext.request.contextPath}/home">Trang chủ</a>
        </div>

    </header>


    <!-- ================= SEARCH + FILTER ================= -->
    <div class="student-toolbar">

        <!-- Search -->
        <div class="student-search-wrapper">
            <input
                    type="text"
                    id="studentSearch"
                    class="student-search"
                    placeholder="Tìm theo mã, họ tên, email..."
            >
        </div>

        <!-- Filter -->
        <select
                id="studentStatusFilter"
                class="student-filter"
        >
            <option value="">Tất cả trạng thái</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
        </select>

    </div>


    <!-- ================= TABLE CARD ================= -->
    <div class="student-table-card">

        <div class="student-table-heading">

            <div>
                <h2>Student List</h2>
                <p>Danh sách sinh viên trong hệ thống</p>
            </div>

            <span class="student-count">
                <c:out value="${students.size()}"/> sinh viên
            </span>

        </div>


        <div class="student-table-wrap">

            <table>

                <thead>
                <tr>
                    <th>ID</th>
                    <th>Mã sinh viên</th>
                    <th>Họ và tên</th>
                    <th>Lớp</th>
                    <th>Ngày sinh</th>
                    <th>Giới tính</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Trạng thái</th>
                </tr>
                </thead>

                <tbody>

                <c:forEach var="student" items="${students}">

                    <!-- ================= ROW ================= -->
                    <tr class="student-row" data-status="${student.status}">

                        <td>
                            <c:out value="${student.id}"/>
                        </td>

                        <td>
                            <strong class="student-code">
                                <c:out value="${student.code}"/>
                            </strong>
                        </td>

                        <td>
                            <strong>
                                <c:out value="${student.name}"/>
                            </strong>
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${not empty student.mainClassCode}">
                                    <span class="student-class">
                                        <c:out value="${student.mainClassCode}"/>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="empty-value">--</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <c:out value="${student.dob}"/>
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${student.gender == true}">
                                    <span class="gender-badge male">Nam</span>
                                </c:when>
                                <c:when test="${student.gender == false}">
                                    <span class="gender-badge female">Nữ</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="empty-value">--</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <c:out value="${student.email}"/>
                        </td>

                        <td>
                            <c:out value="${student.phoneNumber}"/>
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${student.status == 'ACTIVE'}">
                                    <span class="status-badge active">Đang hoạt động</span>
                                </c:when>
                                <c:when test="${student.status == 'INACTIVE'}">
                                    <span class="status-badge inactive">Không hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge">
                                        <c:out value="${student.status}"/>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                    </tr>

                </c:forEach>

                </tbody>

            </table>

        </div>


        <!-- Hiển thị khi Search / Filter không có kết quả -->
        <div
                id="studentEmptyState"
                class="student-empty-state"
                style="display: none;"
        >
            <h3>Không tìm thấy sinh viên</h3>

            <p>
                Không có sinh viên nào phù hợp với điều kiện tìm kiếm.
            </p>
        </div>

    </div>

</div>


<!-- ================= JAVASCRIPT ================= -->
<script src="${pageContext.request.contextPath}/assets/js/student.js"></script>

        </div>
    </div>
</div>
</body>
</html>
