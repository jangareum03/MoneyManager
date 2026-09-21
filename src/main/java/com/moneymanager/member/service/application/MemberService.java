package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.request.FindPwdRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.dto.response.FindPwdResponse;
import com.moneymanager.member.domain.dto.response.MyPageResponse;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.query.MemberFindIdQuery;
import com.moneymanager.member.domain.query.MyPageQuery;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.email.EmailMasker;
import com.moneymanager.member.service.email.EmailSender;
import com.moneymanager.member.service.email.PasswordResetTokenManager;
import com.moneymanager.member.service.read.MemberReader;
import com.moneymanager.member.service.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_DATE;
import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_DATE_WITH_DAY;
import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static com.moneymanager.global.exception.code.ErrorCode.EXTERNAL_API_ERROR;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 5<br>
 * 설명              : 회원 기능 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 9. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordResetTokenManager tokenManager;
    private final MemberReader memberReader;
    private final EmailSender emailSender;
    private final MemberRepository memberRepository;

    private final MemberValidator validator;

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
        try{
            emailSender.sendPasswordResetLink(email, resetToken);
        }catch (MailException e) {
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

    public MyPageResponse getMyPageInfo(String memberId) {
        //회원정보 조회
        MyPageQuery myProfileInfo = memberRepository.findMyPageByMemberNumber(memberId)
                .orElseThrow(() -> new ApplicationException(
                                DATA_NOT_FOUND,
                                LogContent.of(
                                        "회원정보 조회",
                                        Member.class,
                                        "id", memberId
                                )
                        ).withMessageKey("member.info.failed")
                );

        //3. 프로필 경로 조회
        String profileImagePath = memberReader.getProfilePath(memberId);

        //4. 접속일 포맷 변경
        String loginDate = DateTimeUtil.formatDate(LocalDate.from(myProfileInfo.getLastLogin()), KOREAN_DATE_WITH_DAY.getPattern());
        String joinDate = DateTimeUtil.formatDate(LocalDate.from(myProfileInfo.getJoinDate()), KOREAN_DATE.getPattern());

        //5. 응답 반환
        return MyPageResponse.of(
                myProfileInfo.getType(),
                myProfileInfo.getName(),
                myProfileInfo.getNickname(),
                myProfileInfo.getGender(),
                myProfileInfo.getEmail(),
                profileImagePath,
                loginDate,
                joinDate,
                myProfileInfo.getAttendanceDays()
        );
    }

}