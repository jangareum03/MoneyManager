package com.areum.moneymanager.config;

import com.areum.moneymanager.dto.ReqMemberDto;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final int TIME = 60 * 30;
    private static final String ROLE_USER = "ROLE_USER";

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/", "/join", "/help/**", "/login-fail").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin() //로그인 절차
                .loginPage("/")
                .loginProcessingUrl("/login")
                .usernameParameter("id")
                .passwordParameter("password")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        String auth = ((ReqMemberDto.AuthMember) authentication.getPrincipal()).getRole();

                        if( auth.equals(ROLE_USER) ) {
                            request.getSession().setMaxInactiveInterval(TIME); //세션 타임아웃 시간 설정

                            response.sendRedirect("/login-success");
                        }
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure( HttpServletRequest request, HttpServletResponse response, AuthenticationException exception ) throws IOException, ServletException {
                        String error, errorMessage;

                        if (exception instanceof BadCredentialsException) {
                            errorMessage = "아이디와 비밀번호가 일치하지 않습니다. 다시 확인해주세요.";
                            error = "BadInfo";
                        } else if (exception instanceof UsernameNotFoundException) {
                            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
                            error = "NotInfo";
                        }else if (exception instanceof InternalAuthenticationServiceException) {
                            errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
                            error = "SystemError";
                        }  else if (exception instanceof AuthenticationCredentialsNotFoundException) {
                            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
                            error = "RefuseAuth";
                        } else {
                            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
                            error = "NotKnow";
                        }

                        response.sendRedirect( "/login-fail?error=" + error + "&exception=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8) + "&id=" + request.getParameter("id") );
                    }
                })
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {
            @Override
            public void customize( WebSecurity web ) {
                web.ignoring()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
            }
        };
    }

}
