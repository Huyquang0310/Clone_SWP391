package fu.libmanagement.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Loại thông báo (khớp ck_notifications_type). {@code path} là trang để mở khi bấm vào thông báo. */
@Getter
@RequiredArgsConstructor
public enum NotificationType {
    DUE_SOON("Sắp đến hạn trả", "bi-alarm", "/reader/loans"),
    OVERDUE("Sách quá hạn", "bi-exclamation-triangle", "/reader/loans"),
    LOAN_APPROVED("Yêu cầu mượn được duyệt", "bi-check-circle", "/reader/loans"),
    LOAN_REJECTED("Yêu cầu mượn bị từ chối", "bi-x-circle", "/reader/loans"),
    RESERVATION_READY("Sách đặt trước đã có", "bi-bookmark-check", "/reader/reservations"),
    NEW_BOOK("Sách mới", "bi-journal-plus", "/books"),
    FINE_CREATED("Khoản phạt mới", "bi-cash-coin", "/reader/loans"),
    SYSTEM("Thông báo hệ thống", "bi-info-circle", "/reader/notifications");

    private final String label;
    /** Tên icon Bootstrap Icons. */
    private final String icon;
    private final String path;
}
