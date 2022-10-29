package com.areum.moneymanager.mapper;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.MemberInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberInfoMapper {

    MemberInfo toEntity(ReqMemberDto.Join member);
    @Mapping(source = "pwd", target = "password")
    MemberInfo toEntity(ReqMemberDto.Login login);

    ReqMemberDto.Join toReqJoinDTO(MemberInfo memberInfo);
    @Mapping(source = "password", target = "pwd")
    ReqMemberDto.Login toReqLoginDTO(MemberInfo memberInfo);

}
