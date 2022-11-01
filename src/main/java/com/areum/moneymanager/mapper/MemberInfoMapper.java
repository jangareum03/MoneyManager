package com.areum.moneymanager.mapper;


import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.entity.MemberInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberInfoMapper {

    //DTO -> Entity
    MemberInfo toEntity( ReqMemberDto.Join member );

    @Mapping(source="pwd", target = "password")
    MemberInfo toEntity( ReqMemberDto.Login member );

    MemberInfo toEntity( ReqMemberDto.FindId member );

    MemberInfo toEntity( ReqMemberDto.FindPwd member );


    //Entity -> DTO
    ResMemberDto.FindId toFindIdDto(MemberInfo memberInfo );

}
