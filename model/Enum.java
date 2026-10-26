package com.swp391.g1.model;

public class Enum {
    // Status for entities
    public enum CommonStatus {
        ACTIVE, // Đang hoạt động
        INACTIVE // Không hoạt động
    }

    // Status for activity
    public enum ActivityStatus {
        UPCOMING, // Sắp diễn ra
        ONGOING, // Đang diễn ra
        COMPLETED, // Đã hoàn thành
        CANCELLED // Đã hủy
    }

    // Status for approval process
    public enum ApprovalStatus {
        DRAFT, // Bản nháp
        PENDING, // Chờ duyệt
        APPROVED, // Đã duyệt
        REJECTED // Đã từ chối
    }

    // Registration form status
    public enum RegistrationFormStatus {
        DRAFT, // Bản nháp
        OPENING, // Đang mở đăng ký
        CLOSED // Đã đóng đăng ký
    }

    // Student application review status
    public enum RegistrationApplicationStatus {
        SUBMITTED, // Chờ xử lý
        APPROVED, // Đã duyệt
        REJECTED // Đã từ chối
    }

    // Participation nature of an activity
    public enum ActivityNature {
        REQUIRE_REGISTRATION, // Yêu cầu đăng ký
        VOLUNTARY, // Tự nguyện
        MANDATORY // Bắt buộc
    }
}
