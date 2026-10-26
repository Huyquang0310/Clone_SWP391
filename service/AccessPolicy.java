package com.swp391.g1.service;

import com.swp391.g1.dto.CurrentUser;

/**
 * Central authorization policy: feature rules live here so callers never
 * duplicate type/role checks.
 */
public final class AccessPolicy {

    /** Account Management is owned by the ADMIN account type (confirmed rule). */
    private static final String ADMIN_TYPE = "ADMIN";

    private AccessPolicy() {
    }

    public static boolean canManageAccounts(CurrentUser user) {
        return hasType(user, ADMIN_TYPE);
    }

    /** ADMIN and STAFF can use activity management; STAFF operations are scope-checked separately. */
    public static boolean canManageActivities(CurrentUser user) {
        return hasType(user, ADMIN_TYPE) || hasType(user, "STAFF");
    }

    /** STAFF activity access is limited to a responsible staff member or unit role. */
    public static boolean canUseStaffActivityScope(CurrentUser user) {
        if (!hasType(user, "STAFF") || user.getRole() == null) {
            return false;
        }
        return switch (user.getRole()) {
            case "STAFF", "UNIT_DEPUTY_HEAD", "UNIT_HEAD", "UNIT_SECRETARY" -> true;
            default -> false;
        };
    }

    public static boolean hasType(CurrentUser user, String type) {
        return user != null && type.equals(user.getType());
    }

    /** Only ADMIN can approve, reject, or revoke the approval decision of an activity. */
    public static boolean canReviewActivities(CurrentUser user) {
        return hasType(user, ADMIN_TYPE);
    }

    /** ADMIN and selected STAFF roles can assign or remove staff from an activity. */
    public static boolean canAssignStaffToActivity(CurrentUser user) {
        if (hasType(user, ADMIN_TYPE)) {
            return true;
        }
        if (!hasType(user, "STAFF") || user.getRole() == null) {
            return false;
        }
        return switch (user.getRole()) {
            case "UNIT_DEPUTY_HEAD", "UNIT_HEAD", "UNIT_SECRETARY" -> true;
            default -> false;
        };
    }

    /** PARTNER_STAFF accounts with leader or secretary role can manage partner staff of an activity. */
    public static boolean canAssignPartnerStaffToActivity(CurrentUser user) {
        if (user == null || user.getRole() == null || !hasType(user, "PARTNER_STAFF")) {
            return false;
        }
        return switch (user.getRole()) {
            case "PARTNER_LEADER", "PARTNER_SECRETARY" -> true;
            default -> false;
        };
    }

    /** ADMIN and STAFF own the Q&A moderation workflow. */
    public static boolean canManageQuestions(CurrentUser user) {
        return hasType(user, ADMIN_TYPE) || hasType(user, "STAFF");
    }

    /** Only students can submit questions and use the student Q&A page. */
    public static boolean canUseStudentQuestions(CurrentUser user) {
        return hasType(user, "STUDENT");
    }

    /** ADMIN organizers and STAFF can access their resolved 1-to-1 inbox. */
    public static boolean canUseSupportInbox(CurrentUser user) {
        return hasType(user, ADMIN_TYPE) || hasType(user, "STAFF");
    }
}
