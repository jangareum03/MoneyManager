package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.EmailUpdateRequest;
import com.moneymanager.member.domain.dto.request.MemberUpdateRequest;
import com.moneymanager.member.domain.dto.response.MemberUpdateResponse;
import com.moneymanager.member.domain.dto.response.MyPageResponse;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.domain.query.MyPageQuery;
import com.moneymanager.member.service.command.MemberCommandService;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.MemberProfileValidator;
import com.moneymanager.member.service.validation.MemberValidator;
import com.moneymanager.redis.service.EmailVerificationService;
import com.moneymanager.redis.service.SideBarMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_DATE;
import static com.moneymanager.global.domain.enums.DatePatterns.KOREAN_DATE_WITH_DAY;
import static com.moneymanager.global.exception.code.ErrorCode.*;

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

    private final MemberReadService memberReadService;
    private final MemberCommandService memberCommandService;
    private final MemberProfileImageService profileImageService;
    private final SideBarMemberService sideBarMemberService;
    private final EmailVerificationService emailVerification;
    private final MemberProfileValidator profileValidator;
    private final MemberValidator validator;
    private final CurrentUser currentUser;


    public void processSaveSideBar(String memberNumber) {
        SideBarUser sideBarUser = memberReadService.getSideBarUser(memberNumber);

        sideBarMemberService.saveNickname(memberNumber, sideBarUser.getNickname());
        sideBarMemberService.saveProfile(memberNumber, sideBarUser.getProfile());
    }

    public MyPageResponse getMemberProfile() {
        //1. 인증된 사용자 조회
        String memberNumber = currentUser.getMemberId();

        //2. 회원정보 조회
        MyPageQuery myProfileInfo = memberReadService.getMyProfile(memberNumber);

        //3. 프로필 상대 경로 조회
        String profileImagePath = profileImageService.get(memberNumber);

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

    public MemberUpdateResponse processMemberUpdate(MemberUpdateRequest request) {
        //1. 요청객체 검증
        if(request == null) {
            throw new ApplicationException(
                    REQUIRED_NOT_EXIST,
                    LogContent.of(
                            "회원 정보 수정",
                            MemberUpdateRequest.class
                    )
            ).withMessageKey("member.update.failed");
        }

        //2. 입력값 검증
        validator.validateMemberUpdate(request);

        //3 인증된 사용자 조회
        String memberNumber = currentUser.getMemberId();

        //4. 정보 수정
        if (request.getName() != null) {
            changeName(memberNumber, request.getName());

            return MemberUpdateResponse.of("name", request.getName());
        }

        if(request.getGender() != null) {
            changeGender(memberNumber, request.getGender());

            return MemberUpdateResponse.of("gender", request.getGender());
        }

        if(request.getPassword() != null) {
            changePassword(memberNumber, request.getPassword());

            return MemberUpdateResponse.of("password", "********");
        }

        throw new ApplicationException(
                INTERVAL_SERVER_ERROR,
                LogContent.of(
                        "회원 정보 수정",
                        MemberUpdateRequest.class,
                        "memberNumber", memberNumber
                )
        ).withMessageKey("member.update.failed");
    }

    public MemberUpdateResponse changeEmail(EmailUpdateRequest request) {
        //1 인증된 사용자 조회
        String memberNumber = currentUser.getMemberId();

        //2. 이메일 검증 완료 확인
        validateEmailVerification(request.getEmail(), request.getToken());

        //3. 기존 회원정보 조회
        MyPageQuery member = memberReadService.getMyProfile(memberNumber);

        //4. 이메일 수정
        memberCommandService.updateEmail(member.getId(), member.getEmail(), request.getEmail());

        //5. 인증토큰 삭제
        emailVerification.deleteToken(request.getEmail());

        return MemberUpdateResponse.of("email", request.getEmail());
    }

    public MemberUpdateResponse changeProfile(MultipartFile file) {
        //1. 인증된 사용자 조회
        String memberId = currentUser.getMemberId();

        //2. 기존회원 정보 조회
        String profileName = profileImageService.getProfileName(memberId);

        if(file == null) {
            memberCommandService.updateProfile(memberId, profileName, "");
            return MemberUpdateResponse.of("profile", profileImageService.getDefaultProfile());
        }

        //2. 이미지 검증
        profileValidator.validate(file);

        //3.이미지명 변경
        String saveName = profileImageService.changeName(file.getOriginalFilename());

        if(profileImageService.exists(saveName)) {
            throw new ApplicationException(
                    FILE_UPLOAD_FAILED,
                    LogContent.of(
                            "프로필 수정",
                            MultipartFile.class,
                            "originalName", file.getOriginalFilename()
                    )
            ).withMessageKey("member.update.failed");
        }

        //4. 파일 저장
        profileImageService.save(memberId, file, saveName);

        //5. 데이터 저장
        memberCommandService.updateProfile(memberId, file.getOriginalFilename(), saveName);

        return MemberUpdateResponse.of("profile", profileImageService.getRoot(memberId).resolve(saveName).toString());
    }


    //===== processMemberUpdate 보조 메서드 =====
    private void changeName(String memberNumber, String newName) {
        //1. 기존 회원정보 조회
        MyPageQuery member = memberReadService.getMyProfile(memberNumber);

        //2. 기본 이름과 동일한지 확인
        String name = member.getName();

        if(name.equals(newName)) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "name", StringUtil.maskMiddle(newName)
                    )
            ).withMessageKey("member.update.duplicate");
        }

        //3. 이름 수정
        memberCommandService.updateName(member.getId(), name, newName);
    }

    private void changeGender(String memberNumber, String newGender) {
        //1. 기존 회원정보 조회
        MyPageQuery member = memberReadService.getMyProfile(memberNumber);

        //2. 기본 성별과 동일한지 확인
        MemberGender gender = member.getGender();

        if(gender == MemberGender.fromValue(newGender)) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "gender", newGender
                    )
            ).withMessageKey("member.update.duplicate");
        }

        //3. 성별 수정
        memberCommandService.updateGender(member.getId(), member.getGender(), MemberGender.fromValue(newGender));
    }

    private void changePassword(String memberNumber, String newPassword) {
        //1. 기존 회원정보 조회
        MyPageQuery member = memberReadService.getMyProfile(memberNumber);

        //2. 기본 비밀번호와 동일한지 확인
        String password = member.getPassword();

        if(memberReadService.isPasswordMatching(password, newPassword)) {
            throw new ApplicationException(
                    REQUEST_DUPLICATE,
                    LogContent.of(
                            "회원 정보 수정",
                            Member.class,
                            "password", StringUtil.maskMiddle(newPassword)
                    )
            ).withMessageKey("member.update.duplicate");
        }

        //2. 비밀번호 수정
        memberCommandService.updatePassword(member.getId(), newPassword);
    }

    private void validateEmailVerification(String email, String token) {
        try{
            emailVerification.validateEmailToken(email, token);
        }catch (ApplicationException e) {
            e.withMessageKey("member.update.failed");

            throw e;
        };
    }

}