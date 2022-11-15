package com.areum.moneymanager.dto;

import lombok.Builder;
import lombok.Getter;

public class ReqHomeDto {

    private ReqHomeDto(){}

    //출석체크 확인
    @Getter
    @Builder
    public static class AttendCheck{
        private String mid;
        private String startDate;
        private String endDate;
    }

    //날짜 이동
    @Getter
    @Builder
    public static class MoveDate {
        private int year;
        private int month;
    }
}
