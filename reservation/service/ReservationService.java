package fu.libmanagement.reservation.service;

import fu.libmanagement.borrowing.repository.BorrowingQueries;
import fu.libmanagement.borrowing.repository.LoanRepository;
import fu.libmanagement.catalog.entity.Book;
import fu.libmanagement.catalog.repository.BookRepository;
import fu.libmanagement.common.enums.LoanStatus;
import fu.libmanagement.common.enums.ReservationStatus;
import fu.libmanagement.common.exception.BusinessException;
import fu.libmanagement.reservation.entity.Reservation;
import fu.libmanagement.reservation.repository.ReservationRepository;
import fu.libmanagement.user.service.ReaderCardService;
import fu.libmanagement.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UC13 Đặt trước sách đang hết. Chỉ ghi nhận (WAITING), không có hàng chờ tự động:
 * thủ thư xem danh sách đặt trước và tự liên hệ độc giả khi có sách.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    public static final int PAGE_SIZE = 10;

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final BorrowingQueries queries;
    private final ReaderCardService readerCardService;
    private final UserService userService;

    /** Đặt trước một đầu sách đang hết bản. */
    @Transactional
    public Reservation reserve(Long readerId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy sách"));
        if (!book.isVisible()) {
            throw new BusinessException("Sách này hiện không còn trong danh mục");
        }

        readerCardService.requireValidCard(readerId);

        if (queries.countAvailableCopies(bookId) > 0) {
            throw new BusinessException("Sách vẫn còn bản sẵn sàng, bạn có thể gửi yêu cầu mượn ngay");
        }
        if (loanRepository.existsByReaderIdAndBookIdAndStatusIn(readerId, bookId, LoanStatus.ACTIVE)) {
            throw new BusinessException("Bạn đang mượn hoặc đã gửi yêu cầu mượn cuốn sách này");
        }
        if (reservationRepository.existsByReaderIdAndBookIdAndStatusIn(readerId, bookId, ReservationStatus.ACTIVE)) {
            throw new BusinessException("Bạn đã đặt trước cuốn sách này rồi");
        }

        Reservation reservation = new Reservation();
        reservation.setReader(userService.getUser(readerId));
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.WAITING);
        try {
            return reservationRepository.saveAndFlush(reservation);
        } catch (DataIntegrityViolationException e) {
            // bấm hai lần cùng lúc: filtered index uq_reservations_active chặn dòng thứ hai
            throw new BusinessException("Bạn đã đặt trước cuốn sách này rồi");
        }
    }

    /** Độc giả hủy đặt trước của chính mình khi còn hiệu lực. */
    @Transactional
    public void cancel(Long readerId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdAndReaderId(reservationId, readerId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy đặt trước"));
        if (!ReservationStatus.ACTIVE.contains(reservation.getStatus())) {
            throw new BusinessException("Đặt trước này không còn hiệu lực để hủy");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    /** Danh sách đặt trước của độc giả, mới nhất trước. */
    @Transactional(readOnly = true)
    public Page<Reservation> list(Long readerId, int page) {
        return reservationRepository.findByReaderId(readerId, PageRequest.of(Math.max(page, 0), PAGE_SIZE,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))));
    }
}
