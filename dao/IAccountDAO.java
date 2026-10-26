package com.swp391.g1.dao;

import java.util.List;

import com.swp391.g1.model.Account;
import com.swp391.g1.model.AccountProfile;
import com.swp391.g1.model.AccountTypeRole;

public interface IAccountDAO {
    List<Account> findByCriteria(String search, String type, String role, String status);

    Account findById(int id);

    Account findByUsernameWithCredentials(String username);

    String findPasswordById(int id);

    boolean usernameExists(String username);

    boolean usernameExistsExcluding(String username, int excludeId);

    int create(Account account);

    boolean update(Account account);

    boolean updateStatus(int id, String status);

    List<String> listAccountTypes();

    List<String> listAccountRoles();

    List<AccountTypeRole> listAccountTypeRoles();

    boolean accountTypeExists(String type);

    boolean accountRoleExists(String role);

    boolean accountTypeRoleExists(String type, String role);

    List<AccountProfile> findProfilesByAccountId(int accountId);
}
