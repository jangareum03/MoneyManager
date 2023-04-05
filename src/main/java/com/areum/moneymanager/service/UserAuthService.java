package com.areum.moneymanager.service;

import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.member.MemberService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService implements UserDetailsService {

    private final Logger LOGGER = LogManager.getLogger(UserAuthService.class);

    @Autowired
    private MemberService memberService;

    @Override
    public UserDetails loadUserByUsername( String id ) throws UsernameNotFoundException {
        boolean isType = memberService.isMember( id );

        if( isType ) {
            ResMemberDto.AuthMember member = memberService.findAuthMember( id );
            LOGGER.debug("로그인 시도 아이디: {}, 비밀번호: {}", member.getId(), member.getPassword());

            return member;
        }

        return null;
    }
}
