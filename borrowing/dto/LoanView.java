package fu.libmanagement.borrowing.dto;

import fu.libmanagement.common.enums.LoanStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Một dòng trong lịch sử mượn trả của độc giả (UC12), đã tính sẵn các nút được phép bấm. */
@Getter
@RequiredArgsConstructor
public class LoanView {

    private final Long id;
    private final Long bookId;
    private final String bookTitle;
    private final String coverUrl;
    private final LoanStatus status;
    private final LocalDateTime requestedAt;
    private final LocalDateTime pickupDeadline;
    private final LocalDate dueDate;
    private final LocalDateTime returnedAt;
    private final int renewCount;
    private final int maxRenewals;
    private final String rejectReason;
    /** Tiền phạt của phiếu; null nếu không có. */
    private final BigDecimal fineAmount;
    private final boolean fineUnpaid;
    /** true nếu chưa có khoản phạt chính thức, số tiền chỉ là ước tính theo số ngày trễ hiện tại. */
    private final boolean fineEstimated;
    private final boolean canRenew;
    private final boolean canCancel;
    /** Lý do không gia hạn được (chỉ có khi phiếu đang mượn mà không gia hạn được). */
    private final String renewNote;
}
