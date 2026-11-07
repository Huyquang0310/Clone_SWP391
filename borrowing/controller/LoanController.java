package fu.libmanagement.borrowing.controller;

import fu.libmanagement.auth.dto.SessionUser;
import fu.libmanagement.borrowing.dto.LoanView;
import fu.libmanagement.borrowing.service.LoanService;
import fu.libmanagement.common.enums.LoanStatus;
import fu.libmanagement.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * UC11 Gửi yêu cầu mượn, UC12 Lịch sử mượn trả, UC14 Gia hạn.
 * Mọi URL nằm dưới /reader nên chỉ độc giả đã đăng nhập mới vào được (xem WebConfig).
 */
@Controller
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    /**
     * Trang xác nhận mượn một đầu sách: /reader/loans/new?bookId=...
     * Trang chi tiết sách (UC8) chỉ cần link tới đây, hoặc nhúng fragment borrowing/borrow-actions.
     */
    @GetMapping("/reader/loans/new")
    public String newRequest(@RequestParam Long bookId, HttpServletRequest request, Model model) {
        model.addAttribute("info", loanService.getBorrowInfo(SessionUser.get(request).getId(), bookId));
        return "borrowing/borrow-form";
    }

    /** UC11 Gửi yêu cầu mượn (trạng thái PENDING). */
    @PostMapping("/reader/loans/request")
    public String request(@RequestParam Long bookId, HttpServletRequest request, RedirectAttributes ra) {
        try {
            loanService.requestLoan(SessionUser.get(request).getId(), bookId);
            ra.addFlashAttribute("message", "Đã gửi yêu cầu mượn. Vui lòng chờ thủ thư duyệt");
            return "redirect:/reader/loans";
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/reader/loans/new?bookId=" + bookId;
        }
    }

    /** UC12 Lịch sử mượn trả, lọc theo trạng thái, kèm khoản phạt nếu có. */
    @GetMapping("/reader/loans")
    public String history(@RequestParam(required = false) String status,
                          @RequestParam(defaultValue = "0") int page,
                          HttpServletRequest request, Model model) {
        LoanStatus filter = parseStatus(status);
        Page<LoanView> result = loanService.history(SessionUser.get(request).getId(), filter, page);

        model.addAttribute("result", result);
        model.addAttribute("loans", result.getContent());
        model.addAttribute("status", filter == null ? "" : filter.name());
        model.addAttribute("statuses", LoanStatus.values());
        return "borrowing/loan-history";
    }

    /** UC14 Gia hạn (một nút ở từng phiếu đang mượn). */
    @PostMapping("/reader/loans/{id}/renew")
    public String renew(@PathVariable Long id, HttpServletRequest request, RedirectAttributes ra) {
        try {
            var loan = loanService.renew(SessionUser.get(request).getId(), id);
            ra.addFlashAttribute("message", "Gia hạn thành công, ngày hẹn trả mới: "
                    + loan.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reader/loans";
    }

    /** Hủy yêu cầu mượn đang chờ duyệt. */
    @PostMapping("/reader/loans/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpServletRequest request, RedirectAttributes ra) {
        try {
            loanService.cancelRequest(SessionUser.get(request).getId(), id);
            ra.addFlashAttribute("message", "Đã hủy yêu cầu mượn");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reader/loans";
    }

    /** Giá trị lọc không hợp lệ hoặc rỗng thì coi như xem tất cả. */
    private LoanStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return LoanStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
