package fu.libmanagement.borrowing.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Truy vấn SQL thuần tới các bảng thuộc module khác (book_copies, fines) mà UC11-15 chỉ cần ĐỌC
 * (riêng releaseHeldCopy có ghi). Làm vậy để không phải tạo lại entity BookCopy / Fine, tránh trùng
 * với entity của Khoa (UC21) và Văn An (UC19). Khi các entity đó có đủ, có thể thay bằng repository của họ.
 */
@Repository
public class BorrowingQueries {

    /** Tổng tiền phạt của một phiếu và còn khoản nào chưa thu hay không. */
    public record FineSummary(BigDecimal total, boolean unpaid) {
    }

    @PersistenceContext
    private EntityManager em;

    /** Số bản sao của đầu sách đang AVAILABLE (sẵn sàng cho mượn). */
    public long countAvailableCopies(Long bookId) {
        Number n = (Number) em.createNativeQuery(
                        "select count(*) from book_copies where book_id = :bookId and status = 'AVAILABLE'")
                .setParameter("bookId", bookId)
                .getSingleResult();
        return n.longValue();
    }

    /** Tổng tiền phạt chưa thu của độc giả (0 nếu không nợ). */
    public BigDecimal sumUnpaidFines(Long readerId) {
        Object result = em.createNativeQuery(
                        "select coalesce(sum(amount), 0) from fines where reader_id = :readerId and status = 'UNPAID'")
                .setParameter("readerId", readerId)
                .getSingleResult();
        return new BigDecimal(result.toString());
    }

    /** Gom các khoản phạt theo phiếu mượn, để hiện cạnh từng phiếu trong lịch sử (UC12). */
    @SuppressWarnings("unchecked")
    public Map<Long, FineSummary> findFinesByLoan(Long readerId, Collection<Long> loanIds) {
        Map<Long, FineSummary> result = new HashMap<>();
        if (loanIds.isEmpty()) {
            return result;
        }
        List<Object[]> rows = em.createNativeQuery(
                        "select loan_id, amount, status from fines where reader_id = :readerId and loan_id in (:loanIds)")
                .setParameter("readerId", readerId)
                .setParameter("loanIds", loanIds)
                .getResultList();
        for (Object[] row : rows) {
            Long loanId = ((Number) row[0]).longValue();
            BigDecimal amount = new BigDecimal(row[1].toString());
            boolean unpaid = "UNPAID".equals(row[2]);
            result.merge(loanId, new FineSummary(amount, unpaid),
                    (a, b) -> new FineSummary(a.total().add(b.total()), a.unpaid() || b.unpaid()));
        }
        return result;
    }

    /** Nhả bản sao đang được giữ (HELD) về AVAILABLE, dùng khi yêu cầu đã duyệt hết hạn nhận sách. */
    public int releaseHeldCopy(Long copyId) {
        return em.createNativeQuery(
                        "update book_copies set status = 'AVAILABLE' where id = :copyId and status = 'HELD'")
                .setParameter("copyId", copyId)
                .executeUpdate();
    }
}
