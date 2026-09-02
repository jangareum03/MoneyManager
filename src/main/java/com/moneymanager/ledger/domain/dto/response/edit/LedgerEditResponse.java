package com.moneymanager.ledger.domain.dto.response.edit;

import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.LedgerType;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.response<br>
 * 파일이름       : LedgerEditResponse<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 2<br>
 * 설명              : 가계부 수정 정보 응답을 위한 클래스
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
 * 		 	  <td>26. 9. 2</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerEditResponse {

    //저장된 가계부 기본 정보
    private final LedgerType type;												//가계부 유형
    private final String date;														//제목
    private final FixedType fixed;						                		//고정여부
    private final FixCycle fixCycle;					                			//고정주기
    private final Money money;                                                  //결제 정보
    private final String memo;                                                     //메모
    private final List<ImageSlot> imageSlot;								//이미지 슬롯 정보
    private final Place place;                                                      //장소 정보

    //카테고리 정보
    private final CategoryOptions categoryOptions;

    private LedgerEditResponse(LedgerType type, String date, FixedType fixed, FixCycle cycle, Money money, String memo, List<ImageSlot> imageSlot, Place place, CategoryOptions categoryOptions) {
        this.type = type;
        this.date = date;
        this.fixed = fixed;
        this.fixCycle = cycle;
        this.money = money;
        this.memo = memo;
        this.imageSlot =imageSlot;
        this.place = place;
        this.categoryOptions = categoryOptions;
    }

    public static LedgerEditResponse of(LedgerType type, String date, FixedType fixed, FixCycle cycle, Money money, String memo, List<ImageSlot> imageSlot, Place place, CategoryOptions categoryOptions) {
        return new LedgerEditResponse(type, date, fixed, cycle, money, memo, imageSlot, place, categoryOptions);
    }

}