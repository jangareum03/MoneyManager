package com.moneymanager.service.member;


import com.moneymanager.dao.member.MemberDaoImpl;
import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.dto.common.ImageDTO;
import com.moneymanager.dto.member.request.MemberDeleteRequest;
import com.moneymanager.dto.member.request.MemberRecoveryRequest;
import com.moneymanager.dto.member.request.MemberSignUpRequest;
import com.moneymanager.dto.member.request.MemberUpdateRequest;
import com.moneymanager.dto.member.response.MemberInfoResponse;
import com.moneymanager.dto.member.response.MemberMyPageResponse;
import com.moneymanager.dto.member.response.MemberRecoveryResponse;
import com.moneymanager.entity.Member;
import com.moneymanager.entity.MemberInfo;
import com.moneymanager.enums.type.*;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.ErrorException;
import com.moneymanager.service.member.validation.MemberValidationService;
import com.moneymanager.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.moneymanager.exception.code.ErrorCode.*;


/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.member<br>
 * * 파일이름       : MemberServiceImpl<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 22. 10. 24<br>
 * * 설명              : 회원정보 관련 비즈니스 로직을 처리하는 클래스
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
 * 		 	  <td>22. 10. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성(버전 1.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Service
public class MemberServiceImpl {

	private final PasswordEncoder passwordEncoder;

	private final MemberDaoImpl memberDao;
	private final MemberInfoDaoImpl memberInfoDao;

	private final ImageServiceImpl imageService;
	private final MailService mailService;


	public MemberServiceImpl(@Qualifier("profileImage") ImageServiceImpl imageService, MailService mailService, MemberDaoImpl memberDao, MemberInfoDaoImpl memberInfoDao, PasswordEncoder passwordEncoder) {
		this.imageService = imageService;
		this.mailService = mailService;
		this.memberDao = memberDao;
		this.memberInfoDao = memberInfoDao;
		this.passwordEncoder = passwordEncoder;
	}


	/**
	 * 입력한 정보로 회원가입을 진행합니다.<br>
	 *
	 * @param signUp 회원가입 정보
	 * @throws ErrorException 회원가입이 불가할 때 발생
	 */
	@Transactional(rollbackFor = Exception.class)
	public void createMember(MemberSignUpRequest signUp) {
		try {
			String memberId = createMemberId(signUp.getId());

			Member member = signUp.toEntity(passwordEncoder.encode(signUp.getPassword()));
			MemberInfo memberInfo = member.getInfo();
			if (memberDao.saveMember(member) && memberInfoDao.saveMemberInfo(memberInfo)) {    //신규 회원 추가된 경우
				log.debug("회원가입에 성공했습니다. (아이디: {}, 닉네임: {}, 이름: {}, 성별: {}, 이메일: {})", signUp.getId(), signUp.getNickName(), signUp.getName(), signUp.getGender(), signUp.getEmail());
			}
		} catch (IllegalArgumentException | DataAccessException | ErrorException e) {
			log.debug("원인: {}", e.getMessage());
			throw new ErrorException(MEMBER_JOIN_UNKNOWN);
		}
	}


	/**
	 * 사용자가 입력한 아이디로 회원 식별번호를 생성하는 메서드
	 * 회원 식별번호 형식: U + 유형(1) + 아이디 앞글자(1) + 가입월(2) + 자동증가값(3)
	 * - 유형: 일반회원(A), 카카오(K), 구글(G), 네이버(N)
	 *
	 * @param id 사용자가 입력한 아이디
	 * @return 회원 식별번호
	 */
	private String createMemberId(String id) {
		String memberId = "UA" + id.charAt(0);

		int month = LocalDate.now().getMonthValue();
		memberId += month < 10 ? "0" + month : month;

		int endNum = 0;

		String lastMemberId = memberDao.findLatestId(memberId);

		if (Objects.isNull(lastMemberId)) {
			endNum = 1;
		} else {
			endNum = Integer.parseInt(lastMemberId.substring(5));

			if (endNum >= 999) {
				throw new IllegalArgumentException("최대값을 초과하여 회원 식별번호를 생성 불가");
			}

			endNum++;
		}


		return memberId.concat(String.format("%03d", endNum));
	}


	/**
	 * 회원번호에 해당하는 아이디를 반환합니다.
	 *
	 * @param memberId 회원번호
	 * @return 회원번호가 있으면 아이디, 없으면 null
	 */
	public String getId(String memberId) {
		return memberDao.findUsernameByMemberId(memberId);
	}


	/**
	 * 입력한 정보를 시작위치부터 종료위치까지 변경할 문자로 마스킹한 후 반환합니다.
	 *
	 * @param value      마스킹할 정보
	 * @param startIndex 마스킹 시작위치
	 * @param endIndex   마스킹 종료위치
	 * @param change     변경할 문자
	 * @return 마스킹 된 정보
	 */
	public String maskValue(String value, int startIndex, int endIndex, char change) {
		char[] valueArr = value.toCharArray();

		for (int i = startIndex; i < endIndex; i++) {
			valueArr[i] = change;
		}

		return String.valueOf(valueArr);
	}



	/**
	 * 회원번호에 해당하는 회원정보를 반환합니다.
	 *
	 * @param id 회원 식별번호
	 * @return 회원번호가 있으면 회원정보
	 */
	public MemberInfoResponse getMemberInfo(String id) {
		Member member = memberDao.findMemberById(id);
		MemberInfo memberInfo = memberInfoDao.findMemberInfoById(id);

		//회원 프로필 이미지와 경로를 조회
		String profileImage = imageService.findImage(id);

		//날짜 포맷
		String formattedLastLoginDate = memberInfo.getLoginAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일"));
		String formattedJoinDate = member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));


		return MemberInfoResponse.builder()
				.info(MemberMyPageResponse.MemberInfo.builder()
						.type(MemberType.match(member.getType().charAt(0)))
						.nickName(member.getNickName()).profile(profileImage).lastLoginDate(String.format("마지막 접속일은 %s 입니다.", formattedLastLoginDate))
						.build()
				)
				.name(member.getName()).gender(GenderType.match(memberInfo.getGender())).email(member.getEmail()).joinDate(formattedJoinDate).attendCount(memberInfo.getConsecutiveDays())
				.build();
	}


	/**
	 * 회원번호에 해당하는 일부분의 회원정보를 반환합니다.
	 *
	 * @param id 회원 식별번호
	 * @return 마이페이지에 필요한 회원정보를 반환합니다.
	 */
	public MemberMyPageResponse.MemberInfo getMemberSummary(String id) {
		Member member = memberDao.findMemberById(id);
		MemberInfo memberInfo = memberInfoDao.findMemberInfoById(id);

		//회원 프로필 이미지와 경로를 조회
		String profileImage = imageService.findImage(id);

		//날짜 포맷
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일");
		String formattedLastLogin = memberInfo.getLoginAt().format(formatter);


		return MemberMyPageResponse.MemberInfo.builder()
				.type(MemberType.match(member.getType().charAt(0)))
				.nickName(member.getNickName())
				.profile(profileImage)
				.lastLoginDate(String.format("마지막 접속일은 %s 입니다.", formattedLastLogin))
				.build();
	}


	/**
	 * 입력한 회원정보를 수정 성공하면 회원 수정내역 추가합니다.
	 *
	 * @param memberId 회원 식별번호
	 * @param update   수정할 회원 정보
	 */
	@Transactional
	public String changeMember(String memberId, MemberUpdateRequest.MemberInfo update) {
		String value = null;
		/*
				수정내역 반영을 위한 변수
					beforeInfo				: 변경 전 회원정보(= 기존정보)
					updateHistory 	: 수정내역을 담은 requestDTO 객체
					item										: 변경된 수정항목
					message						: 실패 원인
		*/
		MemberInfoResponse beforeInfo = getMemberInfo(memberId);
		HistoryType item = HistoryType.MEMBER_UPDATE_NONE;

		try {
			//이름 수정할 경우
			if (Objects.nonNull(update.getName())) {
				item = HistoryType.MEMBER_UPDATE_NAME;
				MemberValidationService.checkNameAvailability(update.getName());

				Member member = Member.builder().id(memberId).name(update.getName()).build();
				if (memberDao.updateName(member)) {
					value = getMemberInfo(memberId).getName();
				}
			}

			//성별 수정할 경우
			if (Objects.nonNull(update.getGender())) {
				MemberInfo memberInfo = MemberInfo.builder().id(memberId).gender(update.getGender().charAt(0)).build();
				if (memberInfoDao.updateGender(memberInfo) ) {
					value = getMemberInfo(memberId).getGender().getText();
				}
			}

			//이메일 수정할 경우
			if (Objects.nonNull(update.getEmail())) {
				item = HistoryType.MEMBER_UPDATE_EMAIL;

				Member member = Member.builder().id(memberId).email(update.getEmail()).build();
				if (memberDao.updateEmail(member)) {
					value = getMemberInfo(memberId).getEmail();
				}
			}
		} catch (DataAccessException | ErrorException e) {

		}

		return value;
	}


	/**
	 * 회원이 변경한 이미지로 프로필을 변경합니다.
	 *
	 * @param memberId 회원 식별번호
	 * @param profile  프로필 이미지 정보를 담은 객체
	 */
	@Transactional
	public void changeProfile(String memberId, MemberUpdateRequest.Profile profile) {
		String currentProfile = memberInfoDao.findProfileImageNameById(memberId);
		String changeFileName = imageService.changeFileName(profile.getAfterImage());

		MemberUpdateRequest.Profile changeProfile
				= MemberUpdateRequest.Profile.builder().reset(profile.isReset()).beforeImage(currentProfile)
				.afterImage(ImageDTO.builder().file(profile.getAfterImage().getFile()).fileExtension(profile.getAfterImage().getFileExtension()).fileName(changeFileName).build())
				.build();

		try {
			MemberInfo memberInfo;

			if (profile.isReset()) {
				memberInfo = MemberInfo.builder().id(memberId).profile(null).build();
				if (memberInfoDao.updateProfile(memberInfo)) {
					imageService.deleteProfile(currentProfile);
				}
			} else {
				memberInfo = MemberInfo.builder().id(memberId).profile(changeProfile.getAfterImage().getFileName()).build();
				if (memberInfoDao.updateProfile(memberInfo)) {
					imageService.changeProfile(memberId, changeProfile);
				}
			}
		} catch (IOException e) {
			log.debug("파일 오류");
		}
	}


	/**
	 * 아이디 존재 여부를 판단합니다.<p>
	 * 입력한 정보에 해당하는 아이디가 존재하면 아이디와 마지막 접속일을 반환합니다.<br>
	 * 아이디는 일부가 마스킹 처리되며, 한 번도 접속 하지 않았다면 마지막 접속일은 제공되지 않습니다.<p>
	 * 입력한 정보에 해당하는 아이다가 미존재면 {@link ErrorException}이 발생합니다.
	 *
	 * @param findID 아이디를 찾기 위한 정보
	 * @return 아이디가 있으면 마스킹된 아이디
	 */
	public MemberRecoveryResponse.Id findMaskedIdAndMessage(MemberRecoveryRequest.Id findID) {
		Member member = memberDao.findMemberByNameAndBirth(findID.toEntity());
		if (Objects.isNull(member)) {    //아이디가 존재하지 않은 경우
			throw new ErrorException(ErrorCode.MEMBER_FIND_NONE);
		}

		String message = getIdGuideByStatus(member);    //회원 상태별 안내 메시지 얻기

		String username = member.getUserName();
		String maskUsername = maskValue(username, username.length() - 3, username.length(), '*');
		try {
			LocalDateTime date = memberInfoDao.findLastLoginDateByUserName(username);
			String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 hh시 mm분 ss초"));

			return MemberRecoveryResponse.Id.builder().id(maskUsername).message(message).lastDate(formatDate).build();
		} catch (EmptyResultDataAccessException | NullPointerException e) {
			return MemberRecoveryResponse.Id.builder().id(maskUsername).message(message).build();
		}
	}


	/**
	 * 회원 상태에 따라 아이디 안내 메시지를 반환합니다.
	 * <p>
	 * 회원 상태(MemberStatus)에 따라 사용자에게 보여줄 안내 메시지가 달라지며, 다음과 같은 기준으로 메시지가 구성됩니다.
	 * </p>
	 * <ul>
	 *     <li><b>ACTIVE</b>, <b>LOCKED</b>: 정상 또는 잠긴 계정일 경우로, 아이디 일부를 표시하고 고객센터 안내 메시지</li>
	 *     <li><b>DELETE</b>: 탈퇴한 계정일 경우로, 탈퇴일로부터 30일 전이면 복구 가능 안내 메시지, 30일 이후면 복구 불가 및 재가입 안내 메시지</li>
	 *     <li><b>기타 예외 상황</b>: 기본적인 시스템 오류 안내 메시지</li>
	 * </ul>
	 *
	 * @param member 회원 상태와 탈퇴일 등의 정보를 담은 회원 정보
	 * @return 회원 상태별 다르게 구성된 아이디 안내 메시지(HTML 형식 포함)
	 */
	private String getIdGuideByStatus(Member member) {
		StringBuilder message = new StringBuilder("현재 일시적인 문제로 찾으시는 아이디를 알 수 없습니다. <br>");

		MemberStatus status = member.getStatus();
		switch (status) {
			case ACTIVE:
			case LOCKED:
				message = new StringBuilder("<span>찾으시는 아이디는 아래와 같습니다.</span>");
				message.append("<p>개인정보를 위해 일부 아이디는 *로 표시됩니다.</br>");
				message.append("아이디 전체를 알고 싶으시면 고객센터에 문의해주세요.</p>");
				break;
			case DELETE:
				boolean isDelete = DateTimeUtils.isPastDays(member.getDeletedAt(), 30);
				if (isDelete) {
					message = new StringBuilder("찾으시는 아이디의 현재 상태는 아래와 같습니다.<br><br>탈퇴일로부터 30일이 지나지 않아 복구 가능한 아이디입니다.<br>복구를 원하신다면 로그인을 해주시길 바랍니다.<br>복구를 원하시지 않는다면 30일이 지난 후 다른 아이디로 다시 가입해주시길 바랍니다.");
				} else {
					message = new StringBuilder("찾으시는 아이디는 탈퇴일로부터 30일이 지나 복구 불가능한 아이디입니다.<br>서비스를 이용하시려면 다시 가입해주시길 바랍니다.");
				}
				break;
		}

		return message.toString();
	}


	/**
	 * 임시 비밀번호를 이메일로 전송 후 비밀번호가 변경됩니다.<p>
	 * 이메일이 없다면 이메일은 전송되지 않으며, 이메일은 일부가 마스킹 처리됩니다.
	 *
	 * @param request http 요청정보를 담은 객체
	 * @param findPwd 비밀번호 찾기 정보
	 * @return 이메일이 있으면 마스킹된 이메일, 없으면 null
	 */
	public MemberRecoveryResponse.Password recoverPassword(HttpServletRequest request, MemberRecoveryRequest.Password findPwd) {
		//회원 조회
		Member member = memberDao.findMemberByUsernameAndName(findPwd.toEntity());
		if (Objects.isNull(member)) {    //회원이 없는 경우
			throw new ErrorException(ErrorCode.MEMBER_FIND_NONE);
		}

		//회원 상태별 안내문구
		String message = getPasswordGuideByStatus(member);
		MemberStatus status = member.getStatus();


		switch (status) {
			case ACTIVE:
			case LOCKED:
				return sendTemporaryPasswordAndChange(request, member, message);
			case DELETE:
			default:
				return MemberRecoveryResponse.Password.builder()
						.message(message)
						.build();
		}
	}


	/**
	 *	임시 비밀번호를 이메일로 전송하고, 해당 임시 비밀번호로 회원의 비밀번호를 변경합니다.
	 *	<p>
	 *	   메일 전송이 실패하거나 임시 비밀번호가 생성되지 않으면 예외가 발생합니다.<br>
	 *	   또한, 임시 비밀번호는 세션에 저장되고, 이메일 주소는 일부 마스킹되어 반환됩니다.
	 *	</p>
	 *
	 * @param request			HTTP 요청 정보
	 * @param member			임시 비밀번호로 변경할 회원
	 * @param message		회원 상태별 안내 메시지
	 * @return	마스킹된 이메일과 안내 메시지가 포함된 비밀번호 찾기 응답 객체
	 * @throws ErrorException	이메일 전송 실패 또는 알 수 없는 오류가 발생할 경우 발생
	 */
	private MemberRecoveryResponse.Password sendTemporaryPasswordAndChange( HttpServletRequest request, Member member, String message ) {
		String email = member.getEmail();

		try{
			String tempPassword = mailService.send(MailType.TEMP_PASSWORD, member.getName(), email);

			if( tempPassword == null ) {
				throw new ErrorException(EMAIL_SEND_UNKNOWN);
			}

			changePassword(member.getUserName(), tempPassword);

			HttpSession session = request.getSession();
			session.setAttribute(email, tempPassword);

			String maskEmail = maskValue(email, email.length() <= 3 ? 0 : email.length() - 3, email.length(), '*');

			return MemberRecoveryResponse.Password.builder()
					.email(maskEmail)
					.message(message)
					.build();
		}catch ( Exception e ) {
			throw new ErrorException(EMAIL_SEND_UNKNOWN);
		}
	}


	/**
	 * 회원 상태에 따라 비밀번호 안내 메시지를 반환합니다.
	 * <p>
	 * 회원 상태(MemberStatus)에 따라 사용자에게 보여줄 안내 메시지가 달라지며, 다음과 같은 기준으로 메시지가 구성됩니다.
	 * </p>
	 * <ul>
	 *     <li><b>ACTIVE</b>, <b>LOCKED</b>: 정상 또는 잠긴 계정일 경우로, 아이디 일부를 표시하고 고객센터 안내 메시지</li>
	 *     <li><b>DELETE</b>: 탈퇴한 계정일 경우로, 탈퇴일로부터 30일 전이면 복구 가능 안내 메시지, 30일 이후면 복구 불가 및 재가입 안내 메시지</li>
	 *     <li><b>기타 예외 상황</b>: 기본적인 시스템 오류 안내 메시지</li>
	 * </ul>
	 *
	 * @param member 회원 상태와 탈퇴일 등의 정보를 담은 회원 정보
	 * @return 회원 상태별 다르게 구성된 비밀번호 안내 메시지(HTML 형식 포함)
	 */
	private String getPasswordGuideByStatus(Member member) {
		StringBuilder message = new StringBuilder("현재 일시적인 문제로 찾으시는 비밀번호를 알 수 없습니다. <br>");

		MemberStatus status = member.getStatus();
		switch (status) {
			case ACTIVE:
			case LOCKED:
				message = new StringBuilder("<span>임의의 비밀번호를 아래 이메일로 보내드립니다.</span>");
				message.append("<span>해당 이메일로 확인 후 로그인 부탁드립니다.</span>");
				break;
			case DELETE:
				boolean isDelete = DateTimeUtils.isPastDays(member.getDeletedAt(), 30);
				if (isDelete) {
					message = new StringBuilder("복구 불가능한 계정으로 비밀번호를 찾을 수 없습니다.");
				}
				break;
		}

		return message.toString();
	}


	/**
	 * 비밀번호를 변경 후 결과를 반환합니다.<p>
	 * 변경할 비밀번호가 현재 비밀번호와 동일하면 변경할 수 없습니다.
	 *
	 * @param username  비밀번호 변경할 회원 아이디
	 * @param password 변경할 비밀번호
	 */
	public void changePassword(String username, String password) {
		String currentPassword = memberDao.findPasswordByUsername(username);

		if (passwordEncoder.matches(password, currentPassword)) {
			throw new IllegalStateException();
		} else {
			Member member = Member.builder().userName(username).password(passwordEncoder.encode(password)).build();
			memberDao.updatePassword(member);
		}
	}


	public void changeStatus(String memberId, MemberStatus status) {
		if (Objects.isNull(memberId) || Objects.isNull(status)) {
			throw new IllegalArgumentException("변경하려는 회원의 번호나 상태를 입력하지 않았습니다.");
		}

		Member memberEntity = Member.builder().id(memberId).status(status).build();
		switch (status) {
			case ACTIVE:    //로그인 가능한 정상 상태로 변경
				memberDao.updateStatus(memberEntity);
				log.debug("		🍋[END] {}회원의 상태를 정상 상태로 변경", memberId);
				break;
			case LOCKED:    //로그인 불가능한 정상 상태로 변경
				memberDao.updateStatus(memberEntity);
				log.debug("		🍋[END] {}회원의 상태를 로그인 잠금 상태로 변경", memberId);
				break;
			case DELETE:    //서비스 탈퇴 & 복구 가능한 상태로 변경
				memberDao.updateStatus(memberEntity);
				log.debug("		🍋[END] {}회원의 상태를 탈퇴한 상태로 변경", memberId);
				break;
		}
	}


	/**
	 * 회원 식별번호로 회원 탈퇴를 진행하는 메서드
	 *
	 * @param memberId 회원번호
	 * @param delete   사용자가 입력한 탈퇴 사유
	 */
	public void deleteMember(String memberId, MemberDeleteRequest delete) {
		if (delete.getCode().equals("00")) {
			log.debug("회원탈퇴 실패 - 탈퇴사유 불문명");

			throw new ErrorException(MEMBER_EXIT_REASON);
		} else if (delete.getCode().equals("05") && Objects.isNull(delete.getCause())) {
			log.debug("기타사항 미입력");

			throw new ErrorException(MEMBER_EXIT_ETC);
		}


		String password = memberDao.findPasswordByUsername(delete.getUsername());

		if (!passwordEncoder.matches(delete.getPassword(), password)) {
			log.debug("비밀번호 불일치");


			throw new ErrorException(MEMBER_PASSWORD_MISMATCH);
		} else {
			try {
				changeStatus(memberId, MemberStatus.DELETE);

				if (memberDao.updateResignDateByUsername(delete.getUsername())) {

					log.debug("회원탈퇴 성공 - 회원: {}, 상태: 복구 가능한 상태로 전환", memberId);
				}
			} catch (IllegalArgumentException ie) {

				throw new ErrorException(ErrorCode.MEMBER_EXIT_UNKNOWN);
			}
		}
	}
}
