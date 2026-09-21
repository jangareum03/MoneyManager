package com.moneymanager.member.service.command;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.DuplicateConstraint;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.repository.MemberHistoryRepository;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.generator.MemberIdGenerator;
import com.moneymanager.member.service.generator.MemberNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_INTEGRITY;
import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.appender<br>
 * 파일이름       : MemberAppender<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 20<br>
 * 설명              : 회원 객체를 생성하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 9. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class MemberAppender {

    private static final int MAX_RETRY_COUNT = 3;

    private final MemberRepository memberRepository;
    private final MemberHistoryRepository memberHistoryRepository;

    private final MemberIdGenerator idGenerator;
    private final MemberNumberGenerator numberGenerator;
    private final PasswordEncoder passwordEncoder;

    public Member create(MemberSignUpRequest request) {
        MemberGender gender;

        try{
            gender = MemberGender.fromValue(request.getGender());
        }catch (NoSuchElementException e) {
            throw new ApplicationException(
                    INVALID_VALUE,
                    LogContent.of(
                            "Member 생성",
                            Member.class,
                            "gender", request.getGender()
                    )
            ).withMessageKey("member.signup.unavailable");
        }

        String id = createMemberId();
        String number = createMemberNumber();
        String encodePassword = passwordEncoder.encode(request.getPassword());

        return Member.createForJoin(id, number, request.getUsername(), encodePassword, request.getName(), request.getBirthdate(), request.getNickname(), request.getEmail(), gender);
    }

    @Transactional
    public void save(Member member) {
        if(member.getInfo() == null) {
            throw new ApplicationException(
                    DATA_INTEGRITY,
                    LogContent.of(
                            "Member 저장",
                            MemberInfo.class
                    ).withCause("Member 와 MemberInfo 1:1 관계 필요")
            ).withMessageKey("member.signup.unavailable");
        }

        saveWithRetry(member);
        memberRepository.insert(member.getInfo());

        memberHistoryRepository.insertHistory(
                MemberHistory.create(member.getId(), member.getCreatedAt())
        );
    }


    //===== create 보조 메서드 =====
    private String createMemberNumber() {
        String number;

        do {
            number = numberGenerator.generate();
        }while (memberRepository.existsByMemberNumber(number));

        return number;
    }


    //===== saveMember 보조 메서드 =====
    private void saveWithRetry(Member member) {
        DuplicateKeyException exception = null;

        for(int i=0; i < MAX_RETRY_COUNT; i++) {
            try{
                memberRepository.insert(member);

                return;
            }catch (DuplicateKeyException e) {
                exception = e;

                DuplicateConstraint constraint = getConstraint(e)
                        .orElseThrow(() -> e);

                if(constraint == DuplicateConstraint.ID) {
                    AuditLogger.warn("중복된 회원번호(내부용)가 생성되어 새로운 회원번호로 재시도 합니다. id={}, attempt={}", member.getId(), String.valueOf(i+1));

                    member.changeId(createMemberId());
                    continue;
                }

                if(constraint == DuplicateConstraint.NUMBER) {
                    AuditLogger.warn("중복된 회원번호(외부용)가 생성되어 새로운 회원번호로 재시도 합니다. memberNumber={}, attempt={}", member.getMemberNumber(), String.valueOf(i+1));

                    member.changeMemberNumber(createMemberNumber());
                    continue;
                }

                if(constraint == DuplicateConstraint.USERNAME) {
                    AuditLogger.error("아이디 중복으로 회원 저정에 실패했습니다.");
                }

                if(constraint == DuplicateConstraint.EMAIL) {
                    AuditLogger.error("이메일 중복으로 회원 저정에 실패했습니다.");
                }

                throw e;
            }
        }

        throw exception;
    }

    private Optional<DuplicateConstraint> getConstraint(DuplicateKeyException e) {
        Throwable cause = e.getCause();

        while (cause != null) {
            String message = cause.getMessage();

            if(message != null) {
                if(message.contains("PK_MEMBER_ID")) {
                    return Optional.of(DuplicateConstraint.ID);
                }

                if(message.contains("UK_MEMBER_NUMBER")) {
                    return Optional.of(DuplicateConstraint.NUMBER);
                }

                if(message.contains("UK_MEMBER_USERNAME")) {
                    return Optional.of(DuplicateConstraint.USERNAME);
                }

                if(message.contains("UK_MEMBER_EMAIL")) {
                    return Optional.of(DuplicateConstraint.EMAIL);
                }
            }

            cause = cause.getCause();
        }

        return Optional.empty();
    }


    //==== 유틸 메서드 =====
    private String createMemberId() {
        return idGenerator.generate();
    }

}