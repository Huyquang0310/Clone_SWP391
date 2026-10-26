package com.swp391.g1.dao;

import com.swp391.g1.model.RegistrationForm;
import com.swp391.g1.model.RegistrationFormField;
import com.swp391.g1.model.RegistrationApplication;

import java.util.List;
import java.util.Map;

public interface IRegistrationFormDAO {
    List<RegistrationForm> findAll();

    List<RegistrationForm> findOpenForms();

    List<RegistrationForm> findOpenFormsForStudent(int studentId);

    RegistrationForm findById(int id);

    RegistrationForm findByActivityId(int activityId);

    RegistrationForm findOpenFormById(int id, int studentId);

    List<RegistrationApplication> findApplications(int formId);

    RegistrationApplication findApplicationById(int registrationId);

    boolean isActivityApproved(int activityId);

    boolean isActivityUpcoming(int activityId);

    java.time.LocalDateTime findActivityStartTime(int activityId);

    boolean setActivityPublic(int activityId, boolean isPublic);

    List<RegistrationApplication> findApplicationsForStudent(int studentId);

    boolean isAtCapacity(int formId);

    boolean isOpenFormAtCapacity(int formId);

    boolean isActivityEligibleForApplicationReview(int formId);

    boolean hasEarlierPendingApplication(int registrationId);

    boolean hasFormForActivity(int activityId, int exceptFormId);

    void synchronizeFormStatuses();

    boolean insert(RegistrationForm form);

    boolean update(RegistrationForm form);

    boolean updateStatus(int id, String status);

    boolean delete(int id);

    boolean registerStudent(int formId, int studentId, String note);

    boolean hasStudentRegistered(int formId, int studentId);

    boolean registerStudentWithAnswers(int formId, int studentId, String note,
            List<RegistrationFormField> answers);

    boolean reviewApplication(int registrationId, String expectedStatus, String newStatus);

    Map<String, String> findSavedProfile(int studentId);
}
