package com.areum.moneymanager.mapper;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.MemberInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberInfoMapper {

    MemberInfo toEntity(ReqMemberDto memberDto);

    Object toDTO(MemberInfo memberInfo);

}
