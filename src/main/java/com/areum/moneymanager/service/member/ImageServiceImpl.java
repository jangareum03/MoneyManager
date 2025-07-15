package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.member.MemberInfoDao;
import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.request.member.UpdateRequestDTO;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static com.areum.moneymanager.enums.ErrorCode.MEMBER_UPDATE_PROFILE;


/**
 * 회원 이미지와 관련된 작업을 처리하는 클래스</br>
 * 폴더 생성, 이미지 저장, 이미지 이름 설정 등의 메서드 구현
 *
 * @version 1.0
 */
@Service("profileImage")
public class ImageServiceImpl {

	@Value("${image.profile.downPath}")
	private String downPath;

	private final MemberInfoDao memberInfoDao;
	private final Logger logger = LogManager.getLogger(this);


	public ImageServiceImpl( MemberInfoDaoImpl memberInfoDao  ) {
		this.memberInfoDao = memberInfoDao;
	}



	public void saveProfile( String fileName, MultipartFile file ) throws IOException {
		if( Objects.isNull(file) || file.isEmpty() ) {
			throw new ErrorException( MEMBER_UPDATE_PROFILE );
		}

		//폴더와 저장할 이미지 얻은 후 서버에 저장
		File folder = makeDirectory();
		File saveImage = new File( folder, fileName );

		file.transferTo(saveImage);
	}



	public void changeProfile(String memberId, UpdateRequestDTO.Profile profile ) {
		try{
			//기존 프로필 삭제
			boolean isDelete = Objects.isNull(profile.getBeforeImage()) || profile.getBeforeImage().isBlank() || deleteProfile(profile.getBeforeImage());
			if( isDelete ) {
				//프로필 삭제 성공 후 데이터베이스 변경 완료
				if( memberInfoDao.updateProfile( memberId, profile.getAfterImage() ) ) {
					saveProfile( profile.getAfterImage(), profile.getFile() );
				}
			}

		}catch ( IOException e ) {
			logger.debug("삭제할 프로필 이미지 미존재로 삭제 불가");
			throw new ErrorException(MEMBER_UPDATE_PROFILE);
		}
	}



	public String changeFileName( MultipartFile file ) {
		return	String.format("%s.%s", UUID.randomUUID(), StringUtils.getFilenameExtension(file.getOriginalFilename()));
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
			logger.debug("{} 회원은 프로필 정보를 수정한 내역이 존재하지 않아 기본 이미지로 대체됩니다.", memberId);

			return null;
		}
	}




	/**
	 * 회원의 이전 프로필 사진을 서버에서 삭제합니다. <br>
	 * 이전 프로필 사진이 서버에 존재하지 않는다면 {@link ErrorException}이 발생합니다.
	 *
	 */
	public boolean deleteProfile( String deleteImage ) throws IOException {
		File folder = makeDirectory();

		//폴더가 없거나 폴더가 아닌 경우
		if( !folder.exists() || !folder.isDirectory() ) {
			logger.debug("폴더 미존재로 프로필 이미지 삭제 불가");
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
