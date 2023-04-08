package com.areum.moneymanager.service;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.member.MemberService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class UserAuthService implements UserDetailsService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private MemberService memberService;

    @Override
    public UserDetails loadUserByUsername( String id ) throws UsernameNotFoundException {
        boolean isType = memberService.isMember( id );

        if( !isType ) {
            throw new UsernameNotFoundException( id );
        }

        return memberService.findAuthMember( id );
    }
}
