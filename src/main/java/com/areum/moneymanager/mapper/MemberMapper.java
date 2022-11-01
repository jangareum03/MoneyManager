package com.areum.moneymanager.mapper;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member toEntity(ReqMemberDto.Join member);

    ReqMemberDto.Join toReqJoinDTO(Member member);

}
