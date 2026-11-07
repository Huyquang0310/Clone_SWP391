package fu.libmanagement.borrowing.service;

import fu.libmanagement.borrowing.repository.BorrowingQueries;
import fu.libmanagement.borrowing.repository.LoanRepository;
import fu.libmanagement.common.enums.LoanStatus;
import fu.libmanagement.common.exception.BusinessException;
import fu.libmanagement.user.service.ReaderCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Các điều kiện để một độc giả được mượn sách. UC11 (gửi yêu cầu) gọi {@link #requireCanBorrow};
 * UC17 (lập phiếu tại quầy) dùng lại {@link #requireReaderEligible} và {@link #requireNoActiveLoanForBook}
 * (UC17 chọn sẵn bản sao nên không cần kiểm tra "còn bản" như UC11).
 * Tất cả ném BusinessException với thông báo nêu đúng lý do.
 */
@Service
@RequiredArgsConstructor
public class BorrowRuleService {

    private final ReaderCardService readerCardService;
    private final LoanRepository loanRepository;
    private final BorrowingQueries queries;
    private final LoanSettings settings;

    /** Điều kiện về độc giả: thẻ còn hạn, không nợ phạt, không có sách quá hạn, chưa vượt số sách tối đa. */
    @Transactional(readOnly = true)
    public void requireReaderEligible(Long readerId) {
        readerCardService.requireValidCard(readerId);

        BigDecimal unpaid = queries.sumUnpaidFines(readerId);
        if (unpaid.signum() > 0) {
            throw new BusinessException("Bạn còn khoản phạt chưa thanh toán ("
                    + NumberFormat.getInstance(Locale.forLanguageTag("vi-VN")).format(unpaid)
                    + " đ). Vui lòng đến quầy thanh toán trước khi mượn tiếp");
        }
        if (loanRepository.existsByReaderIdAndStatus(readerId, LoanStatus.OVERDUE)) {
            throw new BusinessException("Bạn đang có sách quá hạn chưa trả. Vui lòng trả sách trước khi mượn tiếp");
        }

        int max = settings.maxBooksPerReader();
        long current = loanRepository.countByReaderIdAndStatusIn(readerId, LoanStatus.ACTIVE);
        if (current >= max) {
            throw new BusinessException("Bạn đã đạt số sách tối đa được mượn cùng lúc (" + max
                    + " cuốn, tính cả yêu cầu đang chờ)");
        }
    }

    /** Độc giả chưa có yêu cầu hoặc phiếu đang hiệu lực cho đầu sách này. */
    @Transactional(readOnly = true)
    public void requireNoActiveLoanForBook(Long readerId, Long bookId) {
        if (loanRepository.existsByReaderIdAndBookIdAndStatusIn(readerId, bookId, LoanStatus.ACTIVE)) {
            throw new BusinessException("Bạn đang mượn hoặc đã gửi yêu cầu mượn cuốn sách này");
        }
    }

    /** Đầy đủ điều kiện để gửi yêu cầu mượn online: độc giả hợp lệ, chưa mượn cuốn này, và còn bản sao. */
    @Transactional(readOnly = true)
    public void requireCanBorrow(Long readerId, Long bookId) {
        requireReaderEligible(readerId);
        requireNoActiveLoanForBook(readerId, bookId);
        if (queries.countAvailableCopies(bookId) <= 0) {
            throw new BusinessException("Sách hiện đã hết bản. Bạn có thể đặt trước để thủ thư liên hệ khi có sách");
        }
    }
}
