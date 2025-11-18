package com.moneymanager.service.member;

import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.dto.MemberUpdateRequest;
import com.moneymanager.domain.member.MemberInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.member<br>
 *  * 파일이름       : ImageServiceImpl<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 프로필 이미지 관련 비즈니스 로직을 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 8. 11</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[메서드 추가] getBasePath - 프로필 기본 경로 반환</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Slf4j
@Service("profileImage")
public class ImageServiceImpl {

	@Value("${image.profile.downPath}")
	private String downPath;
	private static final String BASE_PATH = "/image/profile/";

	private final MemberInfoDaoImpl memberInfoDao;


	public ImageServiceImpl( MemberInfoDaoImpl memberInfoDao  ) {
		this.memberInfoDao = memberInfoDao;
	}


	/**
	 * 파일의 확장자를 반환합니다.
	 *
	 * @param file	파일
	 * @return	파일 확장자
	 */
	public static String getExtension( MultipartFile file ) {
		String fileName = file.getOriginalFilename();

		int index = fileName.indexOf('.');
		if( index == -1 ) {
			throw new IllegalArgumentException();
		}

		return fileName.substring( ++index );
	}


	/**
	 * 프로필 이미지를 보기위한 기본 경로를 반환합니다.
	 *
	 * @return 프로필 기존 경로
	 */
	public static String getBasePath() {
		return BASE_PATH;
	}


	public void saveProfile( ImageDTO image ) throws IOException {
		//폴더와 저장할 이미지 얻은 후 서버에 저장
		File folder = makeDirectory();
		File saveImage = new File( folder, image.getFileName() );

		image.getFile().transferTo(saveImage);
	}



	public void changeProfile( String memberId, MemberUpdateRequest.Profile profile ) throws IOException {
		//기존 프로필 삭제
		boolean isDelete = Objects.isNull(profile.getBeforeImage()) || profile.getBeforeImage().isBlank() || deleteProfile(profile.getBeforeImage());

		if( isDelete ) {
			//프로필 삭제 성공 후 데이터베이스 변경 완료
			Member member = Member.builder().id(memberId).detail(MemberInfo.builder().profile(profile.getAfterImage().getFileName()).build()).build();
			if( memberInfoDao.updateProfile( member ) ) {
				saveProfile( profile.getAfterImage() );
			}
		}
	}



	public String changeFileName( ImageDTO imageDTO ) {
		return	String.format("%s.%s", UUID.randomUUID(), imageDTO.getFileExtension());
	}



	/**
	 * 지정된 경로에 폴더를 생성합니다.
	 *
	 * @return	생성된 폴더
	 */
	private File makeDirectory( ) {
		File directory = new File(downPath);

		if( !directory.exists() ) {
			directory.mkdirs();
		}

		return directory;
	}



	/**
	 * HTML 파일 내에서 사용자가 볼 수 있는 이미지 경로를 반환합니다.
	 *
	 * @param memberId  회원번호
	 * @return 사진과 폴더가 있으면 '상대경로+사진', 없으면 null
	 */
	public String findImage( String memberId ) {
		try{
			//회원정보 조회
			String image = memberInfoDao.findProfileImageNameById(memberId);

			return Objects.isNull(image) ? "/image/default/profile.png" :  "/image/profile/" + image;
		}catch( NullPointerException e ) {
			log.debug("{} 회원은 프로필 정보를 수정한 내역이 존재하지 않아 기본 이미지로 대체됩니다.", memberId);

			return null;
		}
	}




	/**
	 * 회원의 이전 프로필 사진을 서버에서 삭제합니다. <br>
	 *
	 */
	public boolean deleteProfile( String deleteImage ) throws IOException {
		File folder = makeDirectory();

		//폴더가 없거나 폴더가 아닌 경우
		if( !folder.exists() || !folder.isDirectory() ) {
			log.debug("폴더 미존재로 프로필 이미지 삭제 불가");
			return false;
		}

		if( Objects.nonNull(deleteImage) ) {
			Path path = Paths.get( folder + "\\" + deleteImage );
			Files.deleteIfExists(path);

			return true;
		}

		return false;
	}
}
