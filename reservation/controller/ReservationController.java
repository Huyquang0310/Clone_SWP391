package fu.libmanagement.reservation.controller;

import fu.libmanagement.auth.dto.SessionUser;
import fu.libmanagement.common.exception.BusinessException;
import fu.libmanagement.reservation.entity.Reservation;
import fu.libmanagement.reservation.service.ReservationService;
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

/** UC13 Đặt trước sách đang hết. URL dưới /reader nên chỉ độc giả vào được. */
@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reader/reservations")
    public String reserve(@RequestParam Long bookId, HttpServletRequest request, RedirectAttributes ra) {
        try {
            reservationService.reserve(SessionUser.get(request).getId(), bookId);
            ra.addFlashAttribute("message", "Đã đặt trước. Thủ thư sẽ liên hệ khi sách có lại");
            return "redirect:/reader/reservations";
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/reader/loans/new?bookId=" + bookId;
        }
    }

    @GetMapping("/reader/reservations")
    public String list(@RequestParam(defaultValue = "0") int page, HttpServletRequest request, Model model) {
        Page<Reservation> result = reservationService.list(SessionUser.get(request).getId(), page);
        model.addAttribute("result", result);
        model.addAttribute("reservations", result.getContent());
        return "borrowing/reservations";
    }

    @PostMapping("/reader/reservations/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpServletRequest request, RedirectAttributes ra) {
        try {
            reservationService.cancel(SessionUser.get(request).getId(), id);
            ra.addFlashAttribute("message", "Đã hủy đặt trước");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reader/reservations";
    }
}
