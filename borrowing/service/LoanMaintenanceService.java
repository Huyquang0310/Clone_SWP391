package fu.libmanagement.borrowing.service;

import fu.libmanagement.borrowing.entity.Loan;
import fu.libmanagement.borrowing.repository.BorrowingQueries;
import fu.libmanagement.borrowing.repository.LoanRepository;
import fu.libmanagement.common.enums.LoanStatus;
import fu.libmanagement.common.enums.NotificationType;
import fu.libmanagement.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Việc chạy định kỳ cho các trạng thái "tự đổi theo thời gian" (DB không có trigger làm việc này):
 *  1. BORROWED quá ngày hẹn trả          -> OVERDUE, gửi thông báo quá hạn (UC15)
 *  2. BORROWED sắp đến hạn               -> gửi thông báo nhắc (UC15)
 *  3. APPROVED quá hạn đến nhận sách     -> EXPIRED, nhả bản sao HELD về AVAILABLE
 * Chạy lúc ứng dụng khởi động và mỗi ngày (xem LoanScheduler). Chạy lại nhiều lần vẫn an toàn.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanMaintenanceService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LoanRepository loanRepository;
    private final BorrowingQueries queries;
    private final LoanSettings settings;
    private final NotificationService notificationService;

    @Transactional
    public void run() {
        LocalDate today = LocalDate.now();
        int overdue = markOverdue(today);
        int reminded = remindDueSoon(today);
        int expired = expireUnclaimed(LocalDateTime.now());
        log.info("Loan maintenance: {} phiếu quá hạn, {} nhắc sắp đến hạn, {} yêu cầu hết hạn nhận sách",
                overdue, reminded, expired);
    }

    /** Phiếu quá ngày hẹn trả chuyển OVERDUE; thông báo gửi một lần cho mỗi phiếu. */
    private int markOverdue(LocalDate today) {
        List<Loan> loans = loanRepository.findByStatusInAndDueDateBefore(
                Set.of(LoanStatus.BORROWED, LoanStatus.OVERDUE), today);
        int changed = 0;
        for (Loan loan : loans) {
            if (loan.getStatus() == LoanStatus.BORROWED) {
                loan.setStatus(LoanStatus.OVERDUE);
                changed++;
            }
            notificationService.notifyOnce(loan.getReader().getId(), NotificationType.OVERDUE,
                    "Sách đã quá hạn trả",
                    "Sách \"" + loan.getBook().getTitle() + "\" đã quá hạn trả từ ngày "
                            + loan.getDueDate().format(DATE) + ". Vui lòng trả sách sớm để tránh bị phạt thêm.",
                    loan.getId());
        }
        return changed;
    }

    /** Nhắc các phiếu đến hạn trong DUE_REMINDER_DAYS ngày tới; mỗi ngày hẹn trả chỉ nhắc một lần. */
    private int remindDueSoon(LocalDate today) {
        List<Loan> loans = loanRepository.findByStatusAndDueDateBetween(
                LoanStatus.BORROWED, today, today.plusDays(settings.dueReminderDays()));
        int sent = 0;
        for (Loan loan : loans) {
            boolean created = notificationService.notifyOnce(loan.getReader().getId(), NotificationType.DUE_SOON,
                    "Sắp đến hạn trả sách",
                    "Sách \"" + loan.getBook().getTitle() + "\" đến hạn trả vào ngày "
                            + loan.getDueDate().format(DATE) + ". Bạn có thể gia hạn nếu cần.",
                    loan.getId());
            if (created) {
                sent++;
            }
        }
        return sent;
    }

    /** Yêu cầu đã duyệt mà độc giả không đến nhận trước pickup_deadline: hết hạn, trả bản sao về kệ. */
    private int expireUnclaimed(LocalDateTime now) {
        List<Loan> loans = loanRepository.findByStatusAndPickupDeadlineBefore(LoanStatus.APPROVED, now);
        for (Loan loan : loans) {
            loan.setStatus(LoanStatus.EXPIRED);
            if (loan.getCopyId() != null) {
                queries.releaseHeldCopy(loan.getCopyId());
            }
            notificationService.notify(loan.getReader().getId(), NotificationType.SYSTEM,
                    "Yêu cầu mượn đã hết hạn nhận sách",
                    "Bạn không đến nhận \"" + loan.getBook().getTitle()
                            + "\" trước hạn nên yêu cầu mượn đã hết hiệu lực. Bạn có thể gửi yêu cầu mới.",
                    loan.getId());
        }
        return loans.size();
    }
}
