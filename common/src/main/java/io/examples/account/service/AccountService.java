package io.examples.account.service;

import io.examples.account.domain.Account;

import java.util.Optional;

/**
 * @author Gary Cheng
 */
public interface AccountService {
    /**
     * Find account by account id
     *
     * @param id the account id
     * @return
     */
    Optional<Account> findById(Integer id);

    /**
     * Find account by account no
     *
     * @param accountNo the account no
     * @return
     */
    Optional<Account> findByAccountNo(String accountNo);

    /**
     * Add a new account
     *
     * @param addAccount the new account to add
     * @return
     */
    Account add(Account addAccount);

    /**
     * Update account
     *
     * @param updateAccount
     * @return
     */
    Account update(Account updateAccount);
}
