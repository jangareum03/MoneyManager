package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import lombok.Value;

import static com.moneymanager.global.exception.code.ErrorCode.*;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.vo<br>
 * 파일이름       : Place<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 8. 31.<br>
 * 설명              : 주소의 장소명, 도로명주소, 지번주소, 상세주소의 값을 나타내는 클래스
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
 * 		 	  <td>25. 8. 31</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 12. 27</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[메서드 삭제] validateAddress, validateJiBunAddress</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class Place {

    String placeName;            //장소명
    String roadAddress;        //도로명 주소
    String detailAddress;        //상세주소

    private Place(String placeName, String road, String detail) {
        this.placeName = placeName;
        this.roadAddress = road;
        this.detailAddress = detail;
    }

    public static Place ofOrNull(String placeName, String roadAddress, String detailAddress) {
        if (isNullOrBlank(placeName) && isNullOrBlank(roadAddress)) {
            return null;
        }

        validate(placeName, roadAddress, detailAddress);

        return new Place(placeName, roadAddress, detailAddress);
    }


    //===== of 보조 메서드 =====
    private static void validate(String placeName, String roadAddress, String detailAddress) {
        //1. 장소명 검증
        validatePlaceName(placeName);

        //2. 도로명 검증
        validateRoadAddress(roadAddress);

        //3.상세주소 검증
        if (!isNullOrBlank(detailAddress)) {
            validateDetailAddress(detailAddress);
        }

    }

    private static void validatePlaceName(String placeName) {
        if (StringUtil.isNullOrBlank(placeName)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "Place 생성",
                            Place.class,
                            "placeName", placeName
                    )
            );
        }

        if (placeName.length() > 100) {
            throw new ApplicationException(
                    OUT_OF_RANGE,
                    LogContent.of(
                                    "Place 생성",
                                    Place.class,
                                    "placeName", placeName
                            ).withOption("min", 1)
                            .withOption("max", 100)
            );
        }
    }

    private static void validateRoadAddress(String roadAddress) {
        if (StringUtil.isNullOrBlank(roadAddress)) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "Place 생성",
                            Place.class,
                            "roadAddress", roadAddress
                    )
            );
        }

        if (roadAddress.length() > 300) {
            throw new ApplicationException(
                    OUT_OF_RANGE,
                    LogContent.of(
                                    "Place 생성",
                                    Place.class,
                                    "roadAddress", roadAddress
                            ).withOption("min", 1)
                            .withOption("max", 300)
            );
        }
    }

    private static void validateDetailAddress(String detailAddress) {
        if (detailAddress.length() > 300) {
            throw new ApplicationException(
                    OUT_OF_RANGE,
                    LogContent.of(
                                    "Place 생성",
                                    Place.class,
                                    "detailAddress 길이", detailAddress.length()
                            )
                            .withOption("min", 0)
                            .withOption("max", 300)
            );
        }
    }

}