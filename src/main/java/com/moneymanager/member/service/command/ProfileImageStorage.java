package com.moneymanager.member.service.command;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.file.FileStorage;
import com.moneymanager.global.file.ImagePathResolver;
import com.moneymanager.global.generator.UuidGenerator;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.member.service.validation.MemberProfileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static com.moneymanager.global.exception.code.ErrorCode.FILE_UPLOAD_FAILED;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : ProfileImageStorage<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 21.<br>
 * 설명              :
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
 * 		 	  <td>26. 9. 21.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class ProfileImageStorage {

    private final MemberProfileValidator validator;
    private final UuidGenerator uuidGenerator;

    private final ImagePathResolver pathResolver;
    private final FileStorage fileStorage;

    public String replace(String memberId, MultipartFile file) {
        if(file == null || file.isEmpty()) {
            return null;
        }

        validator.validate(file);

        String saveName = changeName(file);

        //프로필 절대 경로
        Path path = pathResolver.profilePath(memberId);

        try{
            //폴더 생성 및 파일 저장
            fileStorage.createDirectory(path);
            fileStorage.saveFile(path, file, saveName);

            return saveName;
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

   public void delete(String memberId, String fileName) {
        Path path = pathResolver.profilePath(memberId);

        try{
            fileStorage.deleteFile(path.resolve(fileName));
        }catch (IOException e) {
            AuditLogger.warn("회원 프로필 이미지 삭제 실패했습니다. memberId={}, fileName={}", memberId, fileName);
        }
   }


    //===== 보조 메서드 =====
    private String changeName(MultipartFile fileName) {
        return uuidGenerator.generate() + fileStorage.extractFileExtension(fileName.getOriginalFilename());
    }

}