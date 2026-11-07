package fu.libmanagement.borrowing.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Đọc các quy định mượn trả từ bảng settings (Khoa làm màn hình sửa ở UC24).
 * Chỉ ĐỌC; thiếu hoặc sai giá trị thì dùng mặc định trùng với dữ liệu mẫu trong file SQL.
 */
@Component
public class LoanSettings {

    @PersistenceContext
    private EntityManager em;

    /** Số sách tối đa một độc giả được mượn cùng lúc. */
    public int maxBooksPerReader() {
        return getInt("MAX_BOOKS_PER_READER", 5);
    }

    /** Số lần gia hạn tối đa cho mỗi phiếu. */
    public int maxRenewals() {
        return getInt("MAX_RENEWALS", 2);
    }

    /** Số ngày được cộng thêm mỗi lần gia hạn. */
    public int renewDays() {
        return getInt("RENEW_DAYS", 7);
    }

    /** Mức phạt trả trễ (VND mỗi ngày mỗi cuốn). */
    public BigDecimal finePerDay() {
        return BigDecimal.valueOf(getInt("FINE_PER_DAY", 2000));
    }

    /** Nhắc trước hạn trả bao nhiêu ngày. */
    public int dueReminderDays() {
        return getInt("DUE_REMINDER_DAYS", 2);
    }

    private int getInt(String key, int defaultValue) {
        List<?> rows = em.createNativeQuery("select setting_value from settings where setting_key = :key")
                .setParameter("key", key)
                .getResultList();
        if (rows.isEmpty() || rows.get(0) == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(rows.get(0).toString().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
