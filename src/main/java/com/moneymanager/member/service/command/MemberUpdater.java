package com.moneymanager.member.service.command;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.EmailUpdateRequest;
import com.moneymanager.member.domain.dto.response.MemberUpdateResponse;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberHistory;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.repository.MemberHistoryRepository;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.application.EmailVerificationService;
import com.moneymanager.member.service.email.EmailMasker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

import static com.moneymanager.global.exception.code.ErrorCode.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberUpdater<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21<br>
 * 설명              : 회원 정보 수정 흐름을 관리하는 오케스트라 클래스
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
 * 		 	  <td>26. 9. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberUpdater {

    private static final String WORK = "회원정보 수정";

    private final EmailVerificationService emailVerificationService;
    private final MemberRepository memberRepository;
    private final MemberHistoryRepository historyRepository;

    private final Clock clock;
    private final PasswordEncoder passwordEncoder;

    public MemberUpdateResponse updateName(String memberId, String newName) {
        //기본 이름과 동일한지 확인
        Member member = memberRepository.findById(memberId);
        String oldName = member.getName();

        try {
            member.changeName(newName);
        } catch (IllegalStateException e) {
            throw new ApplicationException(
                    STATUS_NOT_ALLOWED,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "name", StringUtil.maskMiddle(newName)
                    )
            ).withMessageKey("member.update.not-allowed");
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "name", StringUtil.maskMiddle(newName)
                    )
            ).withMessageKey("member.update.duplicate");
        }

        //이름 수정
        if (!memberRepository.updateName(member)) {
            throw new ApplicationException(
                    DATA_PERSISTENCE_FAILED,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "name", StringUtil.masking(newName, 1, 1)
                    ).withCause("이름 수정 실패")
            ).withMessageKey("member.update.failed");
        }

        historyRepository.insertHistory(
                MemberHistory.update(memberId, "이름", oldName, member.getName(), LocalDateTime.now(clock))
        );

        return MemberUpdateResponse.of("name", member.getName());
    }

    public MemberUpdateResponse updateGender(String memberId, String newGender) {
        //기본 성별과 동일한지 확인
        Member member = memberRepository.findById(memberId);
        MemberGender oldGender = member.getInfo().getGender();

        try{
            member.changeGender(newGender);
        }catch (IllegalStateException e) {
            throw new ApplicationException(
                    STATUS_NOT_ALLOWED,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "gender", newGender
                    )
            ).withMessageKey("member.update.not-allowed");
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "gender", newGender
                    )
            ).withMessageKey("member.update.duplicate");
        }

        //성별 수정
        if (!memberRepository.updateGender(member)) {
            throw new ApplicationException(
                    DATA_PERSISTENCE_FAILED,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "gender", newGender
                    ).withCause("성별 수정 실패")
            ).withMessageKey("member.update.failed");
        }

        historyRepository.insertHistory(
                MemberHistory.update(memberId, "성별", oldGender.getValue(), member.getInfo().getGender().getValue(), LocalDateTime.now(clock))
        );

        return MemberUpdateResponse.of("gender", member.getInfo().getGender().getValue());
    }

    public MemberUpdateResponse updatePassword(String memberId, String newPassword) {
        //기본 비밀번호와 동일한지 확인
        Member member = memberRepository.findById(memberId);

        try{
            member.changePassword();

            if(passwordEncoder.matches(newPassword, member.getPassword())) {
                throw new ApplicationException(
                        REQUEST_DUPLICATE,
                        LogContent.of(
                                "회원 정보 수정",
                                Member.class,
                                "password", StringUtil.maskMiddle(newPassword)
                        )
                ).withMessageKey("member.update.duplicate");
            }
        }catch (IllegalStateException e) {
            throw new ApplicationException(
                    STATUS_NOT_ALLOWED,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "password", StringUtil.maskMiddle(newPassword)
                    )
            ).withMessageKey("member.update.not-allowed");
        }

        //비밀번호 수정
        if (!memberRepository.updatePassword(member)) {
            throw new ApplicationException(
                    DATA_PERSISTENCE_FAILED,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "password", StringUtil.maskMiddle(newPassword)
                    ).withCause("비밀번호 수정 실패")
            ).withMessageKey("member.update.failed");
        }

        historyRepository.insertHistory(
                MemberHistory.update(memberId, "비밀번호", null, null, LocalDateTime.now(clock))
        );

        return MemberUpdateResponse.of("password", "********");
    }

    public MemberUpdateResponse updateEmail(String memberId, EmailUpdateRequest request) {
        //이메일 검증 완료 확인
        emailVerificationService.verifyAuthToken(request.getEmail(), request.getToken());

        //기존 회원정보 조회
        Member member = memberRepository.findById(memberId);
        String oldEmail = member.getEmail();

        try{
            member.changeEmail(request.getEmail());
        }catch (IllegalStateException e) {
            throw new ApplicationException(
                    STATUS_NOT_ALLOWED,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "email", EmailMasker.mask(request.getEmail())
                    )
            ).withMessageKey("member.update.not-allowed");
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "email", EmailMasker.mask(request.getEmail())
                    )
            ).withMessageKey("member.update.duplicate");
        }

        //4. 이메일 수정
        if(!memberRepository.updateEmail(member)) {
            throw new ApplicationException(
                    DATA_PERSISTENCE_FAILED,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "email", EmailMasker.mask(request.getEmail())
                    ).withCause("이메일 수정 실패")
            ).withMessageKey("member.update.failed");
        }

        historyRepository.insertHistory(
                MemberHistory.update(memberId, "이메일", EmailMasker.mask(oldEmail), EmailMasker.mask(member.getEmail()), LocalDateTime.now(clock))
        );

        //인증토큰 삭제
        emailVerificationService.deleteToken(request.getEmail());

        return MemberUpdateResponse.of("email", member.getEmail());
    }

    public MemberUpdateResponse updateProfile(String memberId, String newProfile) {
        //기존 회원정보 조회
        Member member = memberRepository.findById(memberId);
        String oldProfile = member.getInfo().getProfile();

        try{
            member.changeProfile(newProfile);
        }catch (IllegalStateException e) {
            throw new ApplicationException(
                    STATUS_NOT_ALLOWED,
                    LogContent.of(
                            WORK,
                            Member.class,
                            "profile", newProfile
                    )
            ).withMessageKey("member.update.not-allowed");
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "profile", newProfile
                    )
            ).withMessageKey("member.update.duplicate");
        }

        if(!memberRepository.updateProfile(member)) {
            throw new ApplicationException(
                    DATA_PERSISTENCE_FAILED,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "profile", newProfile
                    ).withCause("프로필 수정 실패")
            ).withMessageKey("member.update.failed");
        }

        historyRepository.insertHistory(
                MemberHistory.update(memberId, "프로필", oldProfile, member.getInfo().getProfile(), LocalDateTime.now(clock))
        );

        return MemberUpdateResponse.of("profile", member.getInfo().getProfile());
    }

}