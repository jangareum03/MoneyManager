package com.moneymanager.support;

import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.delete.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected LedgerRepository ledgerRepository;

}