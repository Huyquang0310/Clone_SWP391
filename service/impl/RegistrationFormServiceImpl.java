package com.swp391.g1.service.impl;

import com.swp391.g1.dao.IRegistrationFormDAO;
import com.swp391.g1.dao.impl.RegistrationFormDAOImpl;
import com.swp391.g1.model.Enum.RegistrationFormStatus;
import com.swp391.g1.model.RegistrationApplication;
import com.swp391.g1.model.RegistrationForm;
import com.swp391.g1.model.RegistrationFormField;
import com.swp391.g1.service.IRegistrationFormService;
import com.swp391.g1.util.DateTimeUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegistrationFormServiceImpl implements IRegistrationFormService {
    private final IRegistrationFormDAO dao = new RegistrationFormDAOImpl();

    public List<RegistrationForm> getAll() {
        return dao.findAll();
    }

    public List<RegistrationForm> getOpenForms() {
        return dao.findOpenForms();
    }

    @Override
    public List<RegistrationForm> getOpenFormsForStudent(int studentId) {
        return studentId > 0 ? dao.findOpenFormsForStudent(studentId) : List.of();
    }

    public RegistrationForm getById(int id) {
        return id > 0 ? dao.findById(id) : null;
    }

    @Override
    public RegistrationForm getByActivityId(int activityId) {
        return activityId > 0 ? dao.findByActivityId(activityId) : null;
    }

    @Override
    public RegistrationForm getOpenFormById(int id, int studentId) {
        return id > 0 && studentId > 0 ? dao.findOpenFormById(id, studentId) : null;
    }

    @Override
    public boolean hasStudentRegistered(int formId, int studentId) {
        return formId > 0 && studentId > 0 && dao.hasStudentRegistered(formId, studentId);
    }

    @Override
    public boolean isAtCapacity(int formId) {
        return formId > 0 && dao.isAtCapacity(formId);
    }

    @Override
    public boolean isOpenFormAtCapacity(int formId) {
        return formId > 0 && dao.isOpenFormAtCapacity(formId);
    }

    @Override
    public boolean isActivityEligibleForApplicationReview(int formId) {
        return formId > 0 && dao.isActivityEligibleForApplicationReview(formId);
    }

    @Override
    public boolean hasEarlierPendingApplication(int registrationId) {
        return registrationId > 0 && dao.hasEarlierPendingApplication(registrationId);
    }

    @Override
    public List<RegistrationApplication> getApplications(int formId) {
        return formId > 0 ? dao.findApplications(formId) : List.of();
    }

    @Override
    public List<RegistrationApplication> getApplicationsForStudent(int studentId) {
        return studentId > 0 ? dao.findApplicationsForStudent(studentId) : List.of();
    }

    @Override
    public RegistrationApplication getApplicationById(int registrationId) {
        return registrationId > 0 ? dao.findApplicationById(registrationId) : null;
    }

    @Override
    public String reviewApplication(int registrationId) {
        if (registrationId <= 0) return "Đơn đăng ký không hợp lệ.";
        if (dao.reviewApplication(registrationId, "SUBMITTED", "APPROVED")) return null;
        if (dao.hasEarlierPendingApplication(registrationId)) {
            return "Không thể duyệt đơn này vì còn đơn chờ xử lý được gửi trước.";
        }
        RegistrationApplication application = dao.findApplicationById(registrationId);
        if (application != null && "SUBMITTED".equals(application.getStatus())
                && !dao.isActivityEligibleForApplicationReview(application.getFormId())) {
            return "Không thể duyệt đơn vì hoạt động đã bắt đầu, bị hủy hoặc không còn đủ điều kiện.";
        }
        return "Chỉ có thể duyệt đơn đang chờ xử lý.";
    }

    @Override
    public String rejectApplication(int registrationId) {
        if (registrationId <= 0) return "Đơn đăng ký không hợp lệ.";
        if (dao.reviewApplication(registrationId, "SUBMITTED", "REJECTED")) return null;
        if (dao.hasEarlierPendingApplication(registrationId)) {
            return "Không thể từ chối đơn này vì còn đơn chờ xử lý được gửi trước.";
        }
        return "Chỉ có thể từ chối đơn đang chờ xử lý.";
    }

    @Override
    public String undoApplicationRejection(int registrationId) {
        if (registrationId <= 0) return "Đơn đăng ký không hợp lệ.";
        if (dao.reviewApplication(registrationId, "REJECTED", "SUBMITTED")) return null;
        RegistrationApplication application = dao.findApplicationById(registrationId);
        if (application != null && "REJECTED".equals(application.getStatus())
                && dao.isAtCapacity(application.getFormId())) {
            return "Không thể khôi phục đơn vì hoạt động đã đủ số lượng tối đa.";
        }
        if (application != null && "REJECTED".equals(application.getStatus())
                && !dao.isActivityEligibleForApplicationReview(application.getFormId())) {
            return "Không thể đưa đơn trở lại hàng chờ vì hoạt động đã bắt đầu, bị hủy hoặc không còn đủ điều kiện.";
        }
        return "Chỉ có thể hủy từ chối đơn đã bị từ chối.";
    }

    @Override
    public String undoApplicationApproval(int registrationId) {
        if (registrationId <= 0) return "Đơn đăng ký không hợp lệ.";
        if (dao.reviewApplication(registrationId, "APPROVED", "SUBMITTED")) return null;
        RegistrationApplication application = dao.findApplicationById(registrationId);
        if (application != null && "APPROVED".equals(application.getStatus())
                && !dao.isActivityEligibleForApplicationReview(application.getFormId())) {
            return "Không thể hủy duyệt vì hoạt động đã bắt đầu, bị hủy hoặc không còn đủ điều kiện.";
        }
        return "Chỉ có thể hủy duyệt đơn đã được duyệt.";
    }

    public String save(RegistrationForm f) {
        if (f.getActivityId() <= 0) return "Vui lòng chọn hoạt động.";
        if (!dao.isActivityApproved(f.getActivityId())) {
            return "Chỉ được tạo hoặc chỉnh sửa form cho hoạt động đã được duyệt.";
        }
        if (!dao.isActivityUpcoming(f.getActivityId())) {
            return "Chỉ được tạo hoặc chỉnh sửa form đăng ký cho hoạt động ở trạng thái Chưa diễn ra.";
        }
        if (dao.hasFormForActivity(f.getActivityId(), f.getId())) {
            return "Hoạt động này đã có form đăng ký. Mỗi hoạt động chỉ được tạo một form.";
        }
        if (f.getTitle() == null || f.getTitle().isBlank()) return "Vui lòng nhập tiêu đề form.";
        if (f.getTitle().trim().length() > 200) return "Tiêu đề không được vượt quá 200 ký tự.";
        if (f.getDescription() != null && f.getDescription().length() > 4000)
            return "Mô tả không được vượt quá 4.000 ký tự.";
        if (f.getOpenAt() != null && f.getCloseAt() != null && !f.getCloseAt().isAfter(f.getOpenAt()))
            return "Thời gian đóng đăng ký phải lớn hơn thời gian mở đăng ký.";
        LocalDateTime activityStartTime = dao.findActivityStartTime(f.getActivityId());
        if (activityStartTime == null) return "Hoạt động chưa có thời gian bắt đầu hợp lệ.";
        if (f.getOpenAt() != null && !f.getOpenAt().isBefore(activityStartTime))
            return "Thời gian mở đăng ký phải trước thời gian bắt đầu hoạt động.";
        if (f.getCloseAt() != null && !f.getCloseAt().isBefore(activityStartTime))
            return "Thời gian đóng đăng ký phải trước thời gian bắt đầu hoạt động.";
        LocalDateTime now = DateTimeUtil.now();
        RegistrationForm existingForm = f.getId() > 0 ? getById(f.getId()) : null;
        boolean openingTimeUnchanged = existingForm != null
                && Objects.equals(f.getOpenAt(), existingForm.getOpenAt());
        if (f.getOpenAt() != null && f.getOpenAt().isBefore(now) && !openingTimeUnchanged)
            return "Thời gian mở đăng ký không được ở quá khứ.";
        if (f.getCloseAt() != null && !f.getCloseAt().isAfter(now))
            return "Thời gian đóng đăng ký phải ở tương lai.";
        f.setTitle(f.getTitle().trim());
        f.setDescription(f.getDescription() == null ? "" : f.getDescription().trim());
        if (f.getStatus() == null) return "Vui lòng chọn trạng thái form.";
        try {
            RegistrationFormStatus.valueOf(f.getStatus());
        } catch (IllegalArgumentException e) {
            return "Trạng thái form không hợp lệ.";
        }
        if (RegistrationFormStatus.DRAFT.name().equals(f.getStatus())
                && f.getOpenAt() != null && !f.getOpenAt().isAfter(now)) {
            return "Form bản nháp không thể có thời gian mở đã qua; hãy chọn trạng thái phù hợp hoặc đặt thời gian mở mới.";
        }
        if (RegistrationFormStatus.OPENING.name().equals(f.getStatus())
                && f.getOpenAt() != null && f.getOpenAt().isAfter(now)) {
            return "Chưa đến thời gian mở đăng ký. Hãy lưu form ở trạng thái Bản nháp để form tự mở đúng hạn.";
        }
        if (RegistrationFormStatus.OPENING.name().equals(f.getStatus())
                && f.getCloseAt() != null && !f.getCloseAt().isAfter(now)) {
            return "Form mở đăng ký phải có thời gian đóng trong tương lai.";
        }
        String fieldError = validateFields(f.getFields());
        if (fieldError != null) return fieldError;
        boolean saved = f.getId() > 0 ? dao.update(f) : dao.insert(f);
        if (saved) {
            if (!dao.setActivityPublic(f.getActivityId(), true)) {
                return "Form đã lưu nhưng không thể cập nhật trạng thái hiển thị hoạt động. Vui lòng tải lại và kiểm tra.";
            }
            return null;
        }
        if (!dao.isActivityApproved(f.getActivityId())) {
            return "Không thể lưu form vì hoạt động chưa được duyệt.";
        }
        if (!dao.isActivityUpcoming(f.getActivityId())) {
            return "Không thể lưu form vì hoạt động không còn ở trạng thái Chưa diễn ra.";
        }
        if (dao.hasFormForActivity(f.getActivityId(), f.getId())) {
            return "Hoạt động này đã có form đăng ký. Mỗi hoạt động chỉ được tạo một form.";
        }
        now = DateTimeUtil.now();
        if (f.getOpenAt() != null && f.getOpenAt().isBefore(now) && !openingTimeUnchanged) {
            return "Thời gian mở đăng ký không được ở quá khứ.";
        }
        if (f.getCloseAt() != null && !f.getCloseAt().isAfter(now)) {
            return "Thời gian đóng đăng ký phải ở tương lai.";
        }
        return f.getId() > 0
                ? "Không thể sửa form vì đã có sinh viên đăng ký hoặc form không còn tồn tại."
                : "Không thể tạo form đăng ký. Vui lòng kiểm tra dữ liệu và thử lại.";
    }

    public String updateStatus(int id, String status) {
        if (id <= 0) return "Form đăng ký không hợp lệ.";
        if (status == null) return "Trạng thái form không hợp lệ.";
        try {
            RegistrationFormStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return "Trạng thái form không hợp lệ.";
        }
        if (RegistrationFormStatus.OPENING.name().equals(status)) {
            RegistrationForm form = getById(id);
            if (form == null) return "Không tìm thấy form đăng ký.";
            if (!dao.isActivityApproved(form.getActivityId())) {
                return "Chỉ được mở form khi hoạt động đã được duyệt.";
            }
            if (!dao.isActivityUpcoming(form.getActivityId())) {
                return "Không thể mở form vì hoạt động không còn ở trạng thái Chưa diễn ra.";
            }
            LocalDateTime now = DateTimeUtil.now();
            if (form.getOpenAt() != null && form.getOpenAt().isAfter(now)) {
                return "Chưa đến thời gian mở đăng ký. Hãy giữ form ở trạng thái Bản nháp để tự mở đúng hạn.";
            }
            if (form.getCloseAt() != null && !form.getCloseAt().isAfter(now)) {
                return "Không thể mở form vì thời gian đóng đăng ký đã qua.";
            }
        }
        return dao.updateStatus(id, status) ? null : "Không tìm thấy form đăng ký.";
    }

    public String delete(int id) {
        return dao.delete(id) ? null : "Không thể xóa form đã có đăng ký. Hãy chuyển form sang bản nháp thay vì xóa.";
    }

    @Override
    public void synchronizeFormStatuses() {
        dao.synchronizeFormStatuses();
    }

    public String register(int formId, int studentId, String note) {
        if (formId <= 0 || studentId <= 0) return "Thông tin đăng ký không hợp lệ.";
        if (note != null && note.length() > 2000) return "Ghi chú không được vượt quá 2.000 ký tự.";
        if (dao.registerStudent(formId, studentId, note == null ? "" : note.trim())) return null;
        if (dao.isOpenFormAtCapacity(formId)) return "Không thể đăng ký: hoạt động đã đủ số lượng tối đa.";
        return "Không thể đăng ký: form đã đóng, hoạt động chưa được duyệt hoặc bạn đã đăng ký trước đó.";
    }

    @Override
    public String registerWithAnswers(int formId, int studentId, String note, List<RegistrationFormField> answers,
            Map<String, String> personalInfo) {
        if (formId <= 0 || studentId <= 0) return "Thông tin đăng ký không hợp lệ.";
        if (note != null && note.length() > 2000) return "Ghi chú không được vượt quá 2.000 ký tự.";

        RegistrationForm form = getById(formId);
        if (form == null) return "Không tìm thấy form đăng ký.";
        Map<String, RegistrationFormField> submitted = new HashMap<>();
        List<RegistrationFormField> answerList = answers == null ? new ArrayList<>() : new ArrayList<>(answers);
        for (RegistrationFormField answer : answerList) {
            if (answer == null || answer.getFieldKey() == null
                    || submitted.put(answer.getFieldKey(), answer) != null) {
                return "Thông tin câu trả lời không hợp lệ.";
            }
        }
        if (submitted.size() != form.getFields().size()) return "Vui lòng tải lại form và gửi lại đăng ký.";

        for (RegistrationFormField field : form.getFields()) {
            RegistrationFormField answer = submitted.get(field.getFieldKey());
            if (answer == null) return "Vui lòng tải lại form và gửi lại đăng ký.";
            String value = answer.getValue() == null ? "" : answer.getValue().trim();
            if (field.isRequired() && value.isEmpty()) {
                return "Vui lòng nhập " + field.getLabel() + ".";
            }
            if (field.isRequired() && "CHECKBOX".equals(field.getFieldType()) && !"true".equals(value)) {
                return "Vui lòng xác nhận " + field.getLabel() + ".";
            }
            if (value.length() > 4000) return "Câu trả lời không được vượt quá 4.000 ký tự.";
            if (!isValidAnswer(field, value)) return "Giá trị của trường \"" + field.getLabel() + "\" không hợp lệ.";
            answer.setId(field.getId());
            answer.setFormId(formId);
            answer.setFieldKey(field.getFieldKey());
            answer.setFieldType(field.getFieldType());
            answer.setSaveToProfile(field.isSaveToProfile());
        }

        String personalInfoError = appendPersonalInfo(studentId, personalInfo, answerList);
        if (personalInfoError != null) return personalInfoError;
        if (dao.registerStudentWithAnswers(formId, studentId, note == null ? "" : note.trim(), answerList)) return null;
        if (dao.isOpenFormAtCapacity(formId)) return "Không thể đăng ký: hoạt động đã đủ số lượng tối đa.";
        return "Không thể đăng ký: form đã đóng, hoạt động chưa được duyệt hoặc bạn đã đăng ký trước đó.";
    }

    @Override
    public Map<String, String> getSavedProfile(int studentId) {
        return studentId > 0 ? dao.findSavedProfile(studentId) : Map.of();
    }

    private static String validateFields(List<RegistrationFormField> fields) {
        if (fields == null) return null;
        Map<String, Boolean> keys = new HashMap<>();
        for (RegistrationFormField field : fields) {
            if (field == null || field.getFieldKey() == null
                    || !field.getFieldKey().matches("[a-z][a-z0-9_]{0,79}")) {
                return "Định danh trường đăng ký không hợp lệ.";
            }
            if (keys.put(field.getFieldKey(), Boolean.TRUE) != null) return "Định danh trường không được trùng nhau.";
            if (field.getLabel() == null || field.getLabel().isBlank() || field.getLabel().length() > 200) {
                return "Tên trường phải có nội dung và không vượt quá 200 ký tự.";
            }
            if (field.getFieldType() == null
                    || !List.of("TEXT", "TEXTAREA", "NUMBER", "DATE", "EMAIL", "PHONE", "SELECT", "CHECKBOX")
                            .contains(field.getFieldType())) {
                return "Loại trường đăng ký không hợp lệ.";
            }
            if ("SELECT".equals(field.getFieldType()) && parseOptions(field).isEmpty()) {
                return "Trường chọn cần có ít nhất một lựa chọn.";
            }
        }
        return null;
    }

    private static String appendPersonalInfo(int studentId, Map<String, String> personalInfo,
            List<RegistrationFormField> answers) {
        if (personalInfo == null) return "Vui lòng nhập đầy đủ thông tin sinh viên.";
        String studentCode = value(personalInfo, "studentCode");
        String studentName = value(personalInfo, "studentName");
        String classCode = value(personalInfo, "classCode");
        String email = value(personalInfo, "email");
        if (studentCode.isBlank() || studentCode.length() > 50) {
            return "Mã sinh viên là bắt buộc và không được vượt quá 50 ký tự.";
        }
        if (studentName.isBlank() || studentName.length() > 255) {
            return "Họ và tên là bắt buộc và không được vượt quá 255 ký tự.";
        }
        if (classCode.isBlank() || classCode.length() > 50) {
            return "Lớp là bắt buộc và không được vượt quá 50 ký tự.";
        }
        if (email.isBlank() || email.length() > 255 || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            return "Vui lòng nhập địa chỉ email hợp lệ (tối đa 255 ký tự).";
        }
        for (RegistrationFormField answer : answers) {
            if (List.of("application_student_id", "application_student_code", "application_student_name",
                    "application_class_code", "application_email").contains(answer.getFieldKey())) {
                return "Form có trường trùng với thông tin hồ sơ sinh viên.";
            }
        }
        answers.add(personalAnswer("application_student_id", "ID", Integer.toString(studentId)));
        answers.add(personalAnswer("application_student_code", "Mã sinh viên", studentCode));
        answers.add(personalAnswer("application_student_name", "Họ và tên", studentName));
        answers.add(personalAnswer("application_class_code", "Lớp", classCode));
        answers.add(personalAnswer("application_email", "Email", email));
        return null;
    }

    private static RegistrationFormField personalAnswer(String key, String label, String value) {
        RegistrationFormField answer = new RegistrationFormField();
        answer.setFieldKey(key);
        answer.setLabel(label);
        answer.setValue(value);
        return answer;
    }

    private static String value(Map<String, String> values, String key) {
        String value = values.get(key);
        return value == null ? "" : value.trim();
    }

    private static boolean isValidAnswer(RegistrationFormField field, String value) {
        if (value.isEmpty()) return true;
        switch (field.getFieldType()) {
            case "NUMBER":
                try {
                    new BigDecimal(value);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "DATE":
                try {
                    LocalDate.parse(value);
                    return true;
                } catch (DateTimeParseException e) {
                    return false;
                }
            case "EMAIL":
                return value.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
            case "SELECT":
                return parseOptions(field).contains(value);
            case "CHECKBOX":
                return "true".equals(value) || "false".equals(value);
            default:
                return true;
        }
    }

    private static List<String> parseOptions(RegistrationFormField field) {
        return field.getOptions() == null || field.getOptions().isBlank()
                ? List.of()
                : field.getOptions().lines().map(String::trim).filter(option -> !option.isEmpty()).distinct().toList();
    }
}
