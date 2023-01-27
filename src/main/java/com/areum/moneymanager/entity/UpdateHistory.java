package com.areum.moneymanager.entity;

import com.areum.moneymanager.dto.ReqMemberDto;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class UpdateHistory {
    private Long id;                    //사용내역번호(PK)
    private String memberId;    //회원번호(FK)
    private char success;           //성공여부
    private Date datetime;          //변경날짜
    private char type;                  //수정항목
    private String bfInfo;              //변경전 정보
    private String afInfo;              //변경후 정보
    private String deleteType;        //탈퇴항목
    private String deleteCause;     //탈퇴사유

    public void updateSuccess( char success ) {
        this.success = success;
    }

}
