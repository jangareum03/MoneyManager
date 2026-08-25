package com.moneymanager.ledger.domain.entity;

import com.moneymanager.ledger.domain.dto.vo.Money;
import com.moneymanager.ledger.domain.dto.vo.Place;
import com.moneymanager.ledger.domain.enums.FixCycle;
import com.moneymanager.ledger.domain.enums.FixedType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.domain.ledger.entity<br>
 * * 파일이름       : Ledger<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 22. 11. 15<br>
 * * 설명              : LEDGER 테이블과 매칭되는 클래스
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
 * 		 	  <td>22. 11. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성(버전 1.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>22. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class Ledger {

    private final Long id;                                    //가계부 번호(내부용)
    private final String code;                            //가계부 코드(외부용)
    private final String memberId;                    //작성자(회원 고유번호)
    private final LocalDate date;                    //거래 날짜
    private String category;                            //카테고리 코드
    private String memo;                                    //메모

    private Money money;                                //금액정보
    private Place place;                                    //장소

    private FixedType fix;                                    //고정여부
    private FixCycle fixCycle;                            //고정주기

    private final LocalDateTime createdAt;            //등록일
    private LocalDateTime updatedAt;            //수정일

    //DB 컬럼에 없는 필드
    private boolean changed;

    private Ledger(
            Long id, String code, String memberId, LocalDate date, String category, FixedType fix, FixCycle fixCycle, String memo,
            Money money, Place place,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.id = id;
        this.code = code;
        this.memberId = memberId;
        this.date = date;
        this.category = category;
        this.money = money;
        this.place = place;
        this.fix = fix;
        this.fixCycle = fixCycle;
        this.memo = memo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //생성용
    public static Ledger of(
            String code, String memberId, LocalDate date, String category, FixedType fix, FixCycle fixCycle, String memo,
            Money money, Place place
    ) {
        return new Ledger(
                null,
                code,
                memberId,
                date,
                category,
                fix,
                fixCycle,
                memo,
                money,
                place,
                LocalDateTime.now(),
                null
        );
    }

    //DB용
    public static Ledger restore(
            Long id, String code, String memberId, LocalDate date, String category, FixedType fix, FixCycle fixCycle, String memo,
            Money money, Place place, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        return new Ledger(
                id, code, memberId, date, category, fix, fixCycle, memo,
                money, place,
                createdAt, updatedAt
        );
    }

    public void changeFixInfo(String fixed, String fixCycle) {
        FixedType newFix = FixedType.from(fixed);
        FixCycle newCycle = fixCycle == null ? null : FixCycle.from(fixCycle);

        if (Objects.equals(this.fix, newFix) && Objects.equals(this.fixCycle, newCycle)) {
            return;
        }

        this.fix = newFix;
        this.fixCycle = newCycle;
        markChanged();
    }

    public void changeCategory(String category) {
        if (this.category.equals(category)) {
            return;
        }

        this.category = category;
        markChanged();
    }

    public void changeMemo(String memo) {
        if (Objects.equals(this.memo, memo)) {
            return;
        }

        this.memo = memo;
        markChanged();
    }

    public void changeMoney(Money money) {
        if (this.money.equals(money)) return;

        this.money = money;
        markChanged();
    }

    public void changePlace(Place place) {
        if (Objects.equals(this.place, place)) {
            return;
        }

        this.place = place;
        markChanged();
    }


    //===== 유틸 메서드 =====
    private void markChanged() {
        this.updatedAt = LocalDateTime.now();
        this.changed = true;
    }

}