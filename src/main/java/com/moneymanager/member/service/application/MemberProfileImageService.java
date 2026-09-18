package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.file.FileStorage;
import com.moneymanager.global.file.ImagePathResolver;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.query.MemberProfileQuery;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.generator.UuidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static com.moneymanager.global.exception.code.ErrorCode.FILE_UPLOAD_FAILED;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberProfileImageService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 15<br>
 * 설명              : 회원 프로필 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 9. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberProfileImageService {

    private static final String DEFAULT_PROFILE = "/image/default/profile.png";

    private final MemberRepository memberRepository;
    private final ImagePathResolver pathResolver;
    private final FileStorage fileStorage;
    private final UuidGenerator uuidGenerator;

    public String changeName(String fileName) {
        return uuidGenerator.generate() + fileStorage.extractFileExtension(fileName);
    }

    public boolean exists(String fileName) {
        return memberRepository.existsByProfile(fileName);
    }

    public Path getRoot(String memberId) {
        return pathResolver.profilePath(memberId);
    }

    public String getDefaultProfile() {
        return DEFAULT_PROFILE;
    }

    public String getProfileName(String memberId) {
        Optional<MemberProfileQuery> query = memberRepository.findProfileByMemberNumber(memberId);

        return query.get().getProfile();
    }

    public void save(String memberId, MultipartFile file, String saveName) {
        //1. 프로필 절대 경로
        Path root = getRoot(memberId);

        //2. 폴더 생성 및 파일 저장
        try{
            fileStorage.createDirectory(root);
            fileStorage.saveFile(root, file, saveName);
        }catch (IOException e) {
            throw new ApplicationException(
                    FILE_UPLOAD_FAILED,
                    LogContent.of(
                            "프로필 수정",
                            MultipartFile.class,
                            "originalName", file.getOriginalFilename()
                    )
            ).withMessageKey("member.update.failed");
        }
    }

    public String get(String memberNumber) {
        //1. 프로필 경로 조회
        Optional<MemberProfileQuery> query = memberRepository.findProfileByMemberNumber(memberNumber);

        //2. 회원 미존재
        if(query.isEmpty()) {
            throw new ApplicationException(
                    DATA_NOT_FOUND,
                    LogContent.of(
                            "회원 프로필 조회",
                            Member.class,
                            "memberNumber", memberNumber
                    )
            );
        }

        //3. 프로필 경로 반환
        String profile =  query.get().getProfile();

        if(profile == null) {
            return getDefaultProfile();
        }

        return pathResolver.profilePath(memberNumber).resolve(profile).toString();
    }

}