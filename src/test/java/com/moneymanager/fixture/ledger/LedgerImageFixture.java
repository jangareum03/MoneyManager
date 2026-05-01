package com.moneymanager.fixture.ledger;

import com.moneymanager.domain.ledger.entity.LedgerImage;

import java.time.LocalDateTime;

public class LedgerImageFixture {

	public static LedgerImage.LedgerImageBuilder defaultImage(Long id, Long ledgerId) {
		return LedgerImage.builder()
				.id(id)
				.ledgerId(ledgerId)
				.imagePath("images/default.png")
				.sortOrder(1)
				.createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
				.updatedAt(null);
	}

	public static LedgerImage withUpdateAt(Long id, Long ledgerId, LocalDateTime localDateTime) {
		return defaultImage(id, ledgerId)
				.updatedAt(localDateTime)
				.build();
	}
}
