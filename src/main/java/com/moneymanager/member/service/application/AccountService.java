package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.request.FindPwdRequest;
import com.moneymanager.member.domain.dto.request.MemberWithdrawalRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.dto.response.FindPwdResponse;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.WithdrawalReason;
import com.moneymanager.member.domain.query.MemberFindIdQuery;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.command.MemberUpdater;
import com.moneymanager.member.service.email.EmailMasker;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.member.service.email.PasswordResetTokenManager;
import com.moneymanager.member.service.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.moneymanager.global.exception.code.ErrorCode.*;


/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : AccountService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : 계정 로직을 관리하는 오케스트라 클래스
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
 * 		 	  <td>26. 9. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final MemberUpdater memberUpdater;
    private final MemberValidator validator;

    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenManager tokenManager;

    public FindIdResponse findId(FindIdRequest request) {
        //이름과 이메일 입력 검증
        validator.validateFindId(request);

        //아이디 + 회원상태 조회
        MemberFindIdQuery memberFindIdQuery = memberRepository.findUsernameAndStatusByNameAndEmail(request.getName(), request.getEmail())
                .orElseThrow(() -> new ApplicationException(
                        DATA_NOT_FOUND,
                        LogContent.of(
                                "아이디 및 상태 조회",
                                Member.class,
                                "name",
                                StringUtil.maskMiddle(request.getName()),
                                "email",
                                EmailMasker.mask(request.getEmail())
                        )
                ));

        //마스킹 처리
        String id = memberFindIdQuery.getUsername();
        String maskingId = StringUtil.masking(id, 1, id.length() / 2);

        return new FindIdResponse(maskingId, memberFindIdQuery.getStatus());
    }

    @Transactional
    public FindPwdResponse findPassword(FindPwdRequest request) {
        //이름과 아이디 입력 검증
        validator.validateFindPassword(request);

        //이메일 조회
        String email = memberRepository.findEmailByNameAndUsername(request.getName(), request.getUsername())
                .orElseThrow(() ->
                        new ApplicationException(
                                DATA_NOT_FOUND,
                                LogContent.of(
                                        "이메일 조회",
                                        Member.class,
                                        "name", StringUtil.maskMiddle(request.getName()),
                                        "username", StringUtil.maskMiddle(request.getUsername())
                                )
                        )
                );

        //토큰 생성
        String resetToken = tokenManager.createToken();
        tokenManager.saveToken(resetToken);

        //이메일 발송
        try {
            emailSender.sendPasswordResetLink(email, resetToken);
        } catch (MailException e) {
            tokenManager.deleteToken(resetToken);

            throw new ApplicationException(
                    EXTERNAL_API_ERROR,
                    LogContent.of(
                            "이메일 전송",
                            "email",
                            EmailMasker.mask(email)
                    ).withCause("이메일 발송 실패")
            )
                    .withMessageKey("email.send.failed")
                    .withMessageArgs("비밀번호 초기화");
        }

        //이메일 마스킹
        String maskedEmail = EmailMasker.mask(email);

        return new FindPwdResponse(maskedEmail);
    }

    public void withdrawal(String memberId, MemberWithdrawalRequest request) {
        //입력값 검증
        validator.validateWithdrawal(request);

        //탈퇴사유 검증
        WithdrawalReason reason = WithdrawalReason.from(request.reason());
        if (reason == WithdrawalReason.OTHER && StringUtil.isNullOrBlank(request.other())) {
            throw new ApplicationException(
                    REQUIRED_VALUE,
                    LogContent.of(
                            "탈퇴사유 확인",
                            MemberWithdrawalRequest.class,
                            "other"
                    ).withCause("기타 미입력")
            ).withMessageKey("member.withdrawal.other");
        }

        //회원 조회
        Member member = memberRepository.findById(memberId);

        //비밀번호 일치확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ApplicationException(
                    MISMATCH,
                    LogContent.of(
                            "비밀번호 일치 확인",
                            MemberWithdrawalRequest.class,
                            "password", StringUtil.maskMiddle(request.password())
                    )
            ).withMessageKey("member.password.mismatch");
        }

        //탈퇴
        memberUpdater.changeToWithdrawn(member);
    }

}