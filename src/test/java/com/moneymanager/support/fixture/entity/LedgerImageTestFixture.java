package com.moneymanager.support.fixture.entity;

import com.moneymanager.ledger.domain.entity.LedgerImage;

import java.nio.file.Path;

public final class LedgerImageTestFixture {

	private Long id;
	private final Long ledgerId;
	private String imagePath;
	private int sortOrder;

	private LedgerImageTestFixture(Long ledgerId, Path imagePath) {
		this.ledgerId = ledgerId;
		this.imagePath = imagePath.toString();
	}

	public static LedgerImageTestFixture builder(Long ledgerId, Path relativePath) {
		return new LedgerImageTestFixture(ledgerId, relativePath);
	}

	public LedgerImageTestFixture imagePath(String imagePath) {
		this.imagePath = Path.of(this.imagePath)
				.resolve(imagePath)
				.toString();

		return this;
	}

	public LedgerImageTestFixture sortOrder(int order) {
		this.sortOrder = order;

		return this;
	}

	public LedgerImage build() {
		return LedgerImage.of(ledgerId, Path.of(imagePath), sortOrder);
	}

	public LedgerImage buildExisting(Long id) {
		return LedgerImage.restore(id, ledgerId, imagePath, sortOrder, null, null);
	}

}