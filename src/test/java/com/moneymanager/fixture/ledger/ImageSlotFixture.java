package com.moneymanager.fixture.ledger;


import com.moneymanager.domain.ledger.dto.response.ImageSlot;

public class ImageSlotFixture {

	public static ImageSlot lockedSlot() {
		return ImageSlot.ofLockedSlot();
	}

	public static ImageSlot emptySlot() {
		return ImageSlot.ofEmptySlot();
	}

	public static ImageSlot filledSlot() {
		return filledSlot("image.png");
	}

	public static ImageSlot filledSlot(String file) {
		return ImageSlot.ofFilledSlot(file);
	}

}
