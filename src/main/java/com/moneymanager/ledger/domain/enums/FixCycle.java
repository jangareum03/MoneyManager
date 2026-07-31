package com.moneymanager.ledger.domain.enums;


import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.exception.log.DeveloperLogInfo;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_VALUE;
import static com.moneymanager.global.exception.code.CommonErrorCode.REQUIRED_VALUE;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;


/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.enums<br>
 * 파일이름       : FixCycle<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : 가계부 고정주기를 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum FixCycle {
		WEEKLY("일주일", "W"),
		MONTHLY("한달", "M"),
		YEARLY("일년", "Y");

	private final String label;
	private final String value;

	FixCycle(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public static FixCycle from(String cycle) {
		if(isNullOrBlank(cycle)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					DeveloperLogInfo.of("고정주기 생성", "고정 주기 없음", "fixCycle", cycle),
					"고정 주기를 선택해주세요."
			);
		}

		return Arrays.stream(values())
				.filter(c -> c.value.equalsIgnoreCase(cycle))
				.findFirst()
				.orElseThrow(() -> ValidationException.of(
						INVALID_VALUE,
						DeveloperLogInfo.of("고정주기 생성", "허용되지 않은 고정 주기", "fixCycle", cycle)
						.addOption("allowed", getAllowedValues()),
						"허용하지 않은 고정 주기입니다."
				)
		);
	}

	private static String getAllowedValues() {
		return Arrays.stream(FixCycle.values())
				.map(FixCycle::getValue)
				.collect(Collectors.joining(", "));
	}

}
