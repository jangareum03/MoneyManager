package com.moneymanager.support.fixture.vo;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.dto.vo.Place;

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
