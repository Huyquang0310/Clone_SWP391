package fu.libmanagement.notification.controller;

import fu.libmanagement.auth.dto.SessionUser;
import fu.libmanagement.common.exception.BusinessException;
import fu.libmanagement.notification.entity.Notification;
import fu.libmanagement.notification.service.NotificationService;
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

/** UC15 Xem thông báo trong web. URL dưới /reader nên chỉ độc giả vào được. */
@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/reader/notifications")
    public String list(@RequestParam(defaultValue = "0") int page, HttpServletRequest request, Model model) {
        Page<Notification> result = notificationService.list(SessionUser.get(request).getId(), page);
        model.addAttribute("result", result);
        model.addAttribute("notifications", result.getContent());
        return "notification/notifications";
    }

    /** Đánh dấu một thông báo đã đọc, rồi quay lại đúng trang danh sách đang xem. */
    @PostMapping("/reader/notifications/{id}/read")
    public String markRead(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
                           HttpServletRequest request, RedirectAttributes ra) {
        try {
            notificationService.markRead(SessionUser.get(request).getId(), id);
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reader/notifications?page=" + Math.max(page, 0);
    }

    @PostMapping("/reader/notifications/read-all")
    public String markAllRead(HttpServletRequest request, RedirectAttributes ra) {
        int n = notificationService.markAllRead(SessionUser.get(request).getId());
        ra.addFlashAttribute("message", n == 0 ? "Không có thông báo chưa đọc" : "Đã đánh dấu " + n + " thông báo là đã đọc");
        return "redirect:/reader/notifications";
    }
}
