package fu.libmanagement.borrowing.dto;

import fu.libmanagement.common.enums.LoanStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Thông tin một đầu sách trên trang gửi yêu cầu mượn / đặt trước (UC11, UC13). */
@Getter
@RequiredArgsConstructor
public class BorrowBookInfo {

    private final Long id;
    private final String title;
    private final String isbn;
    private final String coverUrl;
    private final String authorNames;
    private final String categoryName;
    /** Số bản đang sẵn sàng cho mượn. */
    private final long availableCopies;
    /** Trạng thái phiếu đang hiệu lực của độc giả với cuốn này (null nếu chưa có). */
    private final LoanStatus activeLoanStatus;
    /** Độc giả đang đứng trong danh sách đặt trước cuốn này. */
    private final boolean reserved;
}
