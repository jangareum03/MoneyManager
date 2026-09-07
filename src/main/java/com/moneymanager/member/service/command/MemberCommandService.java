package com.moneymanager.member.service.command;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.enums.MemberType;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.generator.MemberNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moneymanager.global.exception.code.ErrorCode.CONSTRAINT_VIOLATION;
import static com.moneymanager.global.exception.code.ErrorCode.DATA_INTEGRITY;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : MemberCommandService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4<br>
 * 설명              : 회원 정보를 변경하는 클래스
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
 * 		 	  <td>26. 9. 4</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private static final int MAX_RETRY_COUNT = 3;

    private final PasswordEncoder passwordEncoder;
    private final MemberNumberGenerator numberGenerator;

    private final MemberRepository memberRepository;

    @Transactional
    public void save(Member member) {
        if(member.getInfo() == null) {
            throw new ApplicationException(
                    DATA_INTEGRITY,
                    LogContent.of(
                            "Member 저장",
                            MemberInfo.class
                    ).withCause("Member 와 MemberInfo 1:1 관계 필요")
            ).withUserMessage("member.signup.unavailable");
        }

        saveMember(member);
        memberRepository.insert(member.getInfo());
    }

    public Member create(MemberSignUpRequest request) {
        //1. 내부용 회원번호 생성
        String id = createMemberId();

        //2. 외부용 회원번호 생성
        String number = numberGenerator.generate();

        //3. 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(request.getPassword());

        //4. 회원 상세정보 생성
        MemberInfo memberInfo = MemberInfo.of(id, MemberGender.fromValue(request.getGender()));

        return Member.of(
                id,
                number,
                request.getUsername(),
                encodePassword,
                request.getName(),
                request.getBirthdate(),
                request.getNickname(),
                request.getEmail(),
                MemberType.COMMON,
                memberInfo
        );
    }


    //===== save 보조 메서드 =====
    private void saveMember(Member member) {
        for(int i=0; i < MAX_RETRY_COUNT; i++) {
            try{
                memberRepository.insert(member);
                return;
            }catch (DuplicateKeyException e){
                if(isMemberDuplicate(e)) {
                    AuditLogger.warn("중복된 회원번호(외부용)가 생성되어 새로운 회원번호로 재시도 합니다. memberNumber={}, attempt={}", member.getMemberNumber(), String.valueOf(i+1));

                    AuditLogger.throwable("회원번호 중복 상세 정보: {}", e);

                    member.changeMemberNumber(numberGenerator.generate());
                }

                if(isIdDuplicate(e)) {
                    AuditLogger.warn("중복된 회원번호(내부용)가 생성되어 새로운 회원번호로 재시도 합니다. id={}, attempt={}", member.getMemberNumber(), String.valueOf(i+1));

                    AuditLogger.throwable("회원번호 중복 상세 정보: {}", e);

                    member.changeId(createMemberId());
                }
            }
        }

        AuditLogger.error("회원 저정에 실패했습니다. 회원번호 중복으로 최대 재시도 횟수를 초과했습니다. memberNumber={}, maxAttempt={}", member.getMemberNumber(), String.valueOf(MAX_RETRY_COUNT));

        throw new ApplicationException(
                CONSTRAINT_VIOLATION,
                LogContent.of(
                        "Member 저장",
                        Member.class
                ).withCause("회원번호 중복")
        ).withUserMessage("member.signup.unavailable");
    }

    private boolean isMemberDuplicate(DuplicateKeyException e) {
        Throwable throwable = e.getCause();

        while (throwable != null) {
            String message = throwable.getMessage();

            if(message != null && message.contains("UK_MEMBER_NUMBER")) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

    private boolean isIdDuplicate(DuplicateKeyException e) {
        Throwable throwable = e.getCause();

        while (throwable != null) {
            String message = throwable.getMessage();

            if(message != null && message.contains("PK_MEMBER_ID")) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }

    //===== create 보조 메서드 =====
    private String createMemberId() {
        return UlidCreator.getUlid().toString();
    }

}