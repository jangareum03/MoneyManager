package com.moneymanager.ledger.service.storage;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.domain.FileMetadata;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDate;

import static com.moneymanager.global.exception.code.ErrorCode.FILE_NOT_FOUND;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.storage<br>
 * 파일이름       : LedgerImageStorage<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 20<br>
 * 설명              : 이미지 파일을 서버에 관리하는 클래스
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
 * 		 	  <td>26. 8. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Component
public class LedgerImageStorage {

    private final Clock clock;
    private final String rootPath;

    public LedgerImageStorage(Clock clock, @Value("${file.image.ledger}") String rootPath) {
        this.clock = clock;
        this.rootPath = rootPath;
    }

    public FileMetadata createFileMetadata(String memberId, MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()) {
            throw new ApplicationException(
                    FILE_NOT_FOUND,
                    LogContent.of(
                            "FileMetadata 생성",
                            MultipartFile.class
                    )
            );
        }

        //파일명 변경
        String originalFileName = file.getOriginalFilename();
        String generatedFilename = generateFileName(file);

        //폴더 생성
        Path directoryPath = resolveDirectory(memberId);
        Files.createDirectories(directoryPath);

        return FileMetadata.of(
                resolveFilePath(memberId, generatedFilename),
                resolveRelativeFilePath(memberId, generatedFilename),
                originalFileName,
                generatedFilename,
                file.getContentType()
        );
    }

    public Path resolveAbsolutePath(LedgerImage ledgerImage) {
        return Path.of(rootPath).resolve(ledgerImage.getImagePath());
    }

    public void saveFile(MultipartFile file, FileMetadata metadata) throws IOException {
        file.transferTo(metadata.getAbsolutePath());
    }

    public void moveToTemp(Path path) throws IOException {
        if(Files.isRegularFile(path)) {
            Path tempDirectory = Paths.get(rootPath, "temp");

            Files.createDirectories(tempDirectory);

            Path file = tempDirectory.resolve(path.getFileName());

            Files.move(path, file,  StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void deleteOrThrow(Path path) throws IOException {
        Files.delete(path);
    }

    public void deleteFile(Path path) {
        try{
            deleteOrThrow(path);
        }catch (IOException e) {
            AuditLogger.warn(
                    "파일 경로가 잘못되거나 존재하지 않은 파일이어서 삭제가 불가능합니다.",
                    path.toString(),
                    path +" 파일 삭제"
            );
        }
    }


    //===== 유틸 메서드 =====
    private Path resolveDirectory(String memberId) {
        return Path.of(rootPath).resolve(resolveRelativeDirectory(memberId));
    }

    private Path resolveRelativeDirectory(String memberId) {
        LocalDate now = LocalDate.now(clock);

        return Path.of(
                memberId,
                String.valueOf(now.getYear()),
                String.format("%02d", now.getMonthValue())
        );
    }

    private Path resolveFilePath(String memberId, String fileName) {
        return resolveDirectory(memberId).resolve(fileName);
    }

    private Path resolveRelativeFilePath(String memberId, String fileName) {
        return resolveRelativeDirectory(memberId).resolve(fileName);
    }

    private String generateFileName(MultipartFile file) {
        String ext = extractFileExtension(file.getOriginalFilename());

        return UlidCreator.getUlid() + "." + ext;
    }

    private String extractFileExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');

        return originalFilename.substring(index + 1);
    }

}