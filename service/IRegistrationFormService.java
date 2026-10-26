package com.swp391.g1.service;

import com.swp391.g1.model.RegistrationForm;
import com.swp391.g1.model.RegistrationFormField;
import com.swp391.g1.model.RegistrationApplication;

import java.util.List;
import java.util.Map;

public interface IRegistrationFormService {
    List<RegistrationForm> getAll();

    List<RegistrationForm> getOpenForms();

    List<RegistrationForm> getOpenFormsForStudent(int studentId);

    RegistrationForm getById(int id);

    RegistrationForm getByActivityId(int activityId);

    RegistrationForm getOpenFormById(int id, int studentId);

    boolean hasStudentRegistered(int formId, int studentId);

    boolean isAtCapacity(int formId);

    boolean isOpenFormAtCapacity(int formId);

    boolean isActivityEligibleForApplicationReview(int formId);

    boolean hasEarlierPendingApplication(int registrationId);

    List<RegistrationApplication> getApplications(int formId);

    List<RegistrationApplication> getApplicationsForStudent(int studentId);

    RegistrationApplication getApplicationById(int registrationId);

    String reviewApplication(int registrationId);

    String rejectApplication(int registrationId);

    String undoApplicationRejection(int registrationId);

    String undoApplicationApproval(int registrationId);

    String save(RegistrationForm form);

    String updateStatus(int id, String status);

    void synchronizeFormStatuses();

    String delete(int id);

    String register(int formId, int studentId, String note);

    String registerWithAnswers(int formId, int studentId, String note, List<RegistrationFormField> answers,
            Map<String, String> personalInfo);

    Map<String, String> getSavedProfile(int studentId);
}