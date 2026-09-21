package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.request.EmailUpdateRequest;
import com.moneymanager.member.domain.dto.request.MemberUpdateRequest;
import com.moneymanager.member.domain.dto.response.MemberUpdateResponse;
import com.moneymanager.member.domain.enums.UpdateType;
import com.moneymanager.member.service.command.MemberUpdater;
import com.moneymanager.member.service.command.ProfileImageStorage;
import com.moneymanager.member.service.read.MemberReader;
import com.moneymanager.member.service.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberUpdateService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21<br>
 * 설명              : 회원 수정 로직을 관리하는 클래스
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
public class MemberUpdateService {

    private final MemberUpdater updater;
    private final MemberReader memberReader;
    private final ProfileImageStorage profileImageStorage;

    private final MemberValidator validator;

    public MemberUpdateResponse update(String memberId, MemberUpdateRequest request) {
        //수정유형 판단
        UpdateType type = request.determineUpdateType();

        //입력값 검증
        validator.validateMemberUpdate(request);

        return switch (type) {
            case NAME -> updater.updateName(memberId, request.getName());
            case GENDER -> updater.updateGender(memberId, request.getGender());
            case PASSWORD -> updater.updatePassword(memberId, request.getPassword());
        };
    }

    public MemberUpdateResponse update(String memberId, EmailUpdateRequest request) {
        //입력값 검증
        validator.validateEmail(request.getEmail());

        return updater.updateEmail(memberId, request);
    }

    public MemberUpdateResponse update(String memberId, MultipartFile file) {
        //입력값 검증
        validator.validateProfile(file);

        String oldProfile = memberReader.getProfileName(memberId);
        String newProfile = profileImageStorage.replace(memberId, file);

        try {
            MemberUpdateResponse response = updater.updateProfile(memberId, newProfile);

            profileImageStorage.delete(memberId, oldProfile);

            return response;
        } catch (ApplicationException e) {
            //파일 삭제
            profileImageStorage.delete(memberId, newProfile);
            throw e;
        }
    }

}