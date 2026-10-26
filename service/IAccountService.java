package com.swp391.g1.service;

import java.util.List;
import java.util.Map;

import com.swp391.g1.model.Account;
import com.swp391.g1.model.AccountProfile;
import com.swp391.g1.model.AccountTypeRole;

public interface IAccountService {
    String STATUS_ACTIVE = "ACTIVE";
    List<String> ACCOUNT_STATUSES = List.of(STATUS_ACTIVE, "INACTIVE", "BLOCKED");

    List<Account> searchAccounts(String search, String type, String role, String status);

    Account getAccountById(int id);

    List<AccountProfile> findProfiles(int accountId);

    List<String> getAccountTypes();

    List<String> getAccountRoles();

    List<AccountTypeRole> getAccountTypeRoles();

    Map<String, List<String>> getRolesByType();

    AccountOperationResult createAccount(Account account);

    AccountOperationResult updateAccount(Account account);

    AccountOperationResult changeStatus(int id, String status);
}
