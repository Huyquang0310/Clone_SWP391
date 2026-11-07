package fu.libmanagement.borrowing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Bật @Scheduled cho ứng dụng (LoanScheduler cần). Để riêng ở đây để không phải sửa class chính. */
@Configuration
@EnableScheduling
public class BorrowingSchedulingConfig {
}
