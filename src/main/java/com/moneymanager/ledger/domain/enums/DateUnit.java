package com.moneymanager.ledger.domain.enums;

import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;

import java.util.Arrays;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.enums<br>
 * 파일이름       : DateUnit<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 14<br>
 * 설명              : 날짜 단위를 정의한 클래스
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
 * 		 	  <td>26. 8. 14.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public enum DateUnit {

    YEAR {
        @Override
        public void validateDate(String date) {
            DateUnit.validateNullOrBlank(date);
            DateUnit.validateSize(date, 4);
            DateUnit.validateNumber(date, 4);
        }
    },
    MONTH {
        @Override
        public void validateDate(String date) {
            DateUnit.validateNullOrBlank(date);
            DateUnit.validateSize(date, 6);
            DateUnit.validateNumber(date, 6);
        }
    };

    public abstract void validateDate(String date);

    public static DateUnit from(String unit) {
        return Arrays.stream(DateUnit.values())
                     .filter(t -> t.name().equalsIgnoreCase(unit))
                     .findFirst()
                     .orElseThrow();
    }

    private static void validateNullOrBlank(String value) {
        if (StringUtil.isNullOrBlank(value)) {
            throw ValidationException.of(
                    CommonErrorCode.REQUIRED_VALUE,
                    LogContent.of(
                            "날짜 검증",
                            "date",
                            value
                    )
            );
        }
    }


    //===== 보조 메서드 =====
    private static void validateSize(String value, int size) {
        if (value.length() != size) {
            throw ValidationException.of(
                    CommonErrorCode.OUT_OF_RANGE,
                    LogContent.of(
                            "날짜 검증",
                            "date",
                            value
                    ).withOption("size", size)
            );
        }
    }

    private static void validateNumber(String value, int size) {
        if (!StringUtil.matchesPattern(value, "\\d+")) {
            throw ValidationException.of(
                    CommonErrorCode.INVALID_FORMAT,
                    LogContent.of(
                            "날짜 검증",
                            "date",
                            value
                    ).withOption("format", size + "자리 숫자")
            );
        }
    }

    private static ValidationException validateValue(String value) {
        return ValidationException.of(
                CommonErrorCode.INVALID_VALUE,
                LogContent.of(
                        "DateUnit 생성",
                        "unit",
                        value
                ).withOption("allowed", values())
        );
    }

}