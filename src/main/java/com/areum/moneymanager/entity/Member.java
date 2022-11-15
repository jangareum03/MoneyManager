package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Member {
    private String mid;
    private char type;
    private char resign;
    private char locked;
    private char restore;
}
