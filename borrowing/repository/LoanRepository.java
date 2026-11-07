package fu.libmanagement.borrowing.repository;

import fu.libmanagement.borrowing.entity.Loan;
import fu.libmanagement.common.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    /** Lịch sử mượn của độc giả (nạp luôn sách để hiện tên và ảnh bìa). */
    @EntityGraph(attributePaths = "book")
    Page<Loan> findByReaderId(Long readerId, Pageable pageable);

    @EntityGraph(attributePaths = "book")
    Page<Loan> findByReaderIdAndStatus(Long readerId, LoanStatus status, Pageable pageable);

    Optional<Loan> findByIdAndReaderId(Long id, Long readerId);

    long countByReaderIdAndStatusIn(Long readerId, Collection<LoanStatus> statuses);

    boolean existsByReaderIdAndStatus(Long readerId, LoanStatus status);

    boolean existsByReaderIdAndBookIdAndStatusIn(Long readerId, Long bookId, Collection<LoanStatus> statuses);

    Optional<Loan> findFirstByReaderIdAndBookIdAndStatusIn(Long readerId, Long bookId, Collection<LoanStatus> statuses);

    // ----- dùng cho tác vụ định kỳ (LoanMaintenanceService) -----

    List<Loan> findByStatusInAndDueDateBefore(Collection<LoanStatus> statuses, LocalDate date);

    List<Loan> findByStatusAndDueDateBetween(LoanStatus status, LocalDate from, LocalDate to);

    List<Loan> findByStatusAndPickupDeadlineBefore(LoanStatus status, LocalDateTime time);
}
