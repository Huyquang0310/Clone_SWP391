package com.swp391.g1.model;

/**
 * A valid (type, role) combination from [account_type_role_domain].
 */
public class AccountTypeRole {

    private String accountType;
    private String accountRole;

    public AccountTypeRole() {
    }

    public AccountTypeRole(String accountType, String accountRole) {
        this.accountType = accountType;
        this.accountRole = accountRole;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(String accountRole) {
        this.accountRole = accountRole;
    }
}