package fu.libmanagement.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Nguồn tạo dòng trong loans: độc giả gửi online (UC11) hay thủ thư lập tại quầy (UC17). */
@Getter
@RequiredArgsConstructor
public enum LoanSource {
    ONLINE("Trực tuyến"),
    COUNTER("Tại quầy");

    private final String label;
}
