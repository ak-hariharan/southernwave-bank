package com.southernwavebank.account_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.southernwavebank.account_service.model.AccountType;
import com.southernwavebank.account_service.model.entity.Account;



@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	Optional<Account> findAccountByUserIdAndAccountType(Long userId, AccountType accountType);
	
	Optional<Account> findByAccountNumber(String accountNumber);

	boolean existsByAccountNumber(String accountNumber);
	
	Optional<Account> findAccountNumberByUserId(Long userId);
}
