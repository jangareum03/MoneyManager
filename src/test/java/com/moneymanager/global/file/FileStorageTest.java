package com.moneymanager.global.file;

import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.file<br>
 * 파일이름       : FileStorageTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 18<br>
 * 설명              : FileStorage 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class FileStorageTest extends IntegrationTest {

    @Autowired
    FileStorage target;

    MultipartFile file;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        file = ImageFixture.jpg("test");
    }

    @Nested
    @DisplayName("파일 저장할 때")
    class SaveFile {
        
        @Test
        @DisplayName("지정한 경로에 파일을 저장한다.")
        void saveFileToSpecifiedPath_whenPathIsValid() throws IOException {
        	//given
            String fileName = "test.jpg";
        	
        	//when
            target.saveFile(tempDir, file,  fileName);
        	
        	//then
        	assertThat(tempDir.resolve(fileName)).exists().isRegularFile();
            assertThat(Files.size(tempDir.resolve(fileName))).isGreaterThan(0);
        }

        @Test
        @DisplayName("저장된 파일 내용이 원본 내용과 동일한다.")
        void saveFile_whenContentMatchesOriginal() throws IOException {
        	//given
            String fileName = "test.jpg";

        	//when
            target.saveFile(tempDir, file, fileName);

        	//then
            assertThat(Files.readAllBytes(tempDir.resolve(fileName)))
                    .isEqualTo(file.getBytes());
        }
        
        @Test
        @DisplayName("존재하는 파일명과 동일하면 덮여씌어진다.")
        void overwriteExistingFile_whenFileNameAlreadyExists() throws IOException {
        	//given
            String fileName = "test.jpg";

            target.saveFile(tempDir, file, fileName);

            assertThat(tempDir.resolve(fileName)).exists().isRegularFile();

            file = new MockMultipartFile(fileName, "안뇽".getBytes());
        	
        	//when:
            target.saveFile(tempDir, file, fileName);
        	
        	//then
            assertThat(Files.readAllBytes(tempDir.resolve(fileName)))
                    .isEqualTo("안뇽".getBytes());
        }
        
        @Test
        @DisplayName("파일명이 잘못되면 저장할 수 없다.")
        void throwException_whenFileNameIsInvalid() {
        	//given
            String fileName = ".../test";
        	
        	//when
            assertThatThrownBy(() -> target.saveFile(tempDir, file, fileName))
                    .isInstanceOf(IOException.class);
        }

    }


    @Nested
    @DisplayName("확장자 추출할 때")
    class ExtractExtension {
        
        @Test
        @DisplayName("파일명에 확장자를 반환한다.")
        void extractExtension_whenFileHasExtension_returnsExtension() {
        	//given
            String fileName = "test.jpg";
        	
        	//when
            String result = target.extractFileExtension(fileName);
        	
        	//then
            assertThat(result).isEqualTo(".jpg");
        }

    }

}