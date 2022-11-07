package com.areum.moneymanager.dto;

import lombok.Builder;
import lombok.Getter;



public class ResHomeDto {

    private ResHomeDto() {}

    //출석체크 확인
    @Getter
    @Builder
    public static class AttendCheck{
        private String date;
    }
}
