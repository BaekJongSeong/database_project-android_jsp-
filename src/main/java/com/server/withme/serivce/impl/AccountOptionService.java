package com.server.withme.serivce.impl;

import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.server.withme.entity.Account;
import com.server.withme.entity.AccountOption;
import com.server.withme.model.AccountOptionDto;
import com.server.withme.model.SignupDto;
import com.server.withme.repository.AccountOptionRepository;
import com.server.withme.repository.AccountRepository;
import com.server.withme.serivce.IAccountOptionService;
import com.server.withme.serivce.IAccountService;

import lombok.RequiredArgsConstructor;

/**
 * Service for AccountOption
 *
 * @author Jongseong Baek
 */

@RequiredArgsConstructor
@Service
public class AccountOptionService implements IAccountOptionService{

	private final IAccountService accountService;
	
	private final AccountRepository accountRepository;
	
	private final AccountOptionRepository accountOptionRepository;
	
	//양방향이니까 양쪽 다 들고 있어야함
	@Override
	public AccountOption signUpOption(SignupDto signupDto) {
		Account account = accountService.findByUsernameOrThrow(signupDto.getLoginDto().getUsername());
		AccountOption accountOption = AccountOption.builder()
										.boxSize(100)
										.distance(0.0)
										.initSafeZone(false)
										.safeMove(false)
										.xPoint(null)
										.yPoint(null)
										.account(account).build();
		return accountOptionRepository.save(accountOption);
	}
	
	@Override
	public AccountOption updateAccountOption(UUID accountId, Double latitude, Double longitude) {
		//먼저 uuid인 accountID로 id를 찾고 id를 사용해야지
		AccountOption accountOption = this.findByAccountIdOrThrow(accountId);
		accountOption.setInitSafeZone(true);
		accountOption.setXPoint(latitude);
		accountOption.setYPoint(longitude);
		return accountOptionRepository.save(accountOption);
	}
	
	@Override
	public AccountOptionDto createAccountOptionDto(AccountOption accountOption) {
		return AccountOptionDto.builder()
			.id(accountOption.getId())
			.boxSize(accountOption.getBoxSize())
			.distance(accountOption.getDistance())
			.xPoint(accountOption.getXPoint())
			.yPoint(accountOption.getYPoint())
			.accountId(accountOption.getAccount().getAccountId())
			.build();
	}
	//accountOption을 알고싶으면 => fetch join하면 account찾아서 내부 ref 객체로 AccountOption 있으니까 꺼내쓰면 되지
	@Override
	public AccountOption findByAccountIdOrThrow(UUID accountId) {
		Account account = accountRepository.findByFetchAccountOption(accountId).orElseThrow(() 
        		-> new UsernameNotFoundException("not found user"));
	
		return account.getAccountOption();
	}
}
