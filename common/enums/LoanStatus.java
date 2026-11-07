package fu.libmanagement.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * Trạng thái một dòng trong bảng loans (khớp CHECK constraint ck_loans_status).
 * Một dòng đi từ yêu cầu mượn (PENDING) đến phiếu mượn đã trả (RETURNED).
 */
@Getter
@RequiredArgsConstructor
public enum LoanStatus {
    PENDING("Chờ duyệt", "bg-warning text-dark"),
    APPROVED("Đã duyệt, chờ nhận sách", "bg-info text-dark"),
    REJECTED("Bị từ chối", "bg-danger"),
    CANCELLED("Đã hủy", "bg-secondary"),
    EXPIRED("Hết hạn nhận sách", "bg-secondary"),
    BORROWED("Đang mượn", "bg-primary"),
    OVERDUE("Quá hạn", "bg-danger"),
    RETURNED("Đã trả", "bg-success");

    private final String label;
    /** Lớp CSS Bootstrap để tô màu nhãn trạng thái trên giao diện. */
    private final String badgeClass;

    /**
     * Các trạng thái "đang hiệu lực": độc giả chưa xong với dòng này.
     * Dùng để đếm số sách đang mượn tối đa và chặn mượn trùng (khớp filtered index uq_loans_active_reader_book).
     */
    public static final Set<LoanStatus> ACTIVE = Set.of(PENDING, APPROVED, BORROWED, OVERDUE);
}
