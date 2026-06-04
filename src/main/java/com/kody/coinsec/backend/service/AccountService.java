package com.kody.coinsec.backend.service;

import com.kody.coinsec.backend.dto.AccountRequest;
import com.kody.coinsec.backend.entity.model.AccountEntity;

import java.util.List;

public interface AccountService {

    List<AccountEntity> getAccounts();

    AccountEntity createAccount(AccountRequest request);

    void updateAccount(Long id, AccountRequest request);

    void deleteAccount(Long id);
}
