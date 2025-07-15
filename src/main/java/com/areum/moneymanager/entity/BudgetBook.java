package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;

@Builder
@Getter
public class BudgetBook {
    /* 가계부 번호(PK) */
    private Long id;
    /* 작성자(FK: member_id)  */
    private Member member;
    /* 카테고리 정보(FK: category_code) */
    private Category category;
    /* 고정여부 */
    private String fix;
    /* 고정주기 */
    private String fixCycle;
    /* 가계부 날짜 */
    private String bookDate;
    /* 내용 */
    private String memo;
    /* 가격 */
    private Long price;
    /* 결제유형 */
    private String paymentType;
    /* 이미지1 */
    private String image1;
    /* 이미지2 */
    private String image2;
    /* 이미지3 */
    private String image3;
    /* 장소명 */
    private String placeName;
    /* 도로명주소 */
    private String roadAddress;
    /* 지번주소 */
    private String address;
    /* 등록일 */
    private Timestamp createdAt;
    /* 수정일 */
    private Timestamp updatedAt;
}
