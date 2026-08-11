package com.moneymanager.support;

import com.moneymanager.delete.repository.member.MemberRepository;
import com.moneymanager.ledger.repository.LedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected LedgerRepository ledgerRepository;

}