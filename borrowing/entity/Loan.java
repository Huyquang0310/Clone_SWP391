package fu.libmanagement.borrowing.entity;

import fu.libmanagement.catalog.entity.Book;
import fu.libmanagement.common.enums.LoanSource;
import fu.libmanagement.common.enums.LoanStatus;
import fu.libmanagement.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Một dòng trong bảng loans = một cuốn sách của một độc giả, đi từ yêu cầu mượn đến phiếu mượn đã trả.
 * Dùng chung cho UC11-15 (độc giả) và UC16-19 (thủ thư). Các cột "người xử lý" và copy_id
 * lưu dạng id thuần (Long) để không phụ thuộc entity của module khác.
 */
@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    private User reader;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Bản sao được gán khi thủ thư duyệt (UC16); yêu cầu PENDING chưa có. */
    @Column(name = "copy_id")
    private Long copyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 10)
    private LoanSource source = LoanSource.ONLINE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    /** Hạn đến nhận sách sau khi được duyệt. */
    @Column(name = "pickup_deadline")
    private LocalDateTime pickupDeadline;

    @Column(name = "issued_by")
    private Long issuedBy;

    @Column(name = "borrowed_at")
    private LocalDateTime borrowedAt;

    /** Ngày hẹn trả. */
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "renew_count", nullable = false)
    private int renewCount = 0;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "received_by")
    private Long receivedBy;

    @Column(name = "return_note", length = 255)
    private String returnNote;
}
