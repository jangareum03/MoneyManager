package com.moneymanager.support.fixture.vo;

import com.moneymanager.domain.ledger.dto.request.LedgerUpdateRequest;
import com.moneymanager.domain.ledger.vo.Place;

public final class PlaceFixture {

	private PlaceFixture() {}

	public static Place from(LedgerUpdateRequest request) {
		return Place.of(
				request.getPlaceName(),
				request.getRoadAddress(),
				request.getDetailAddress()
		);
	}

}
