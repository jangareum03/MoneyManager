package com.areum.moneymanager.service.member;


import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.common.ImageDTO;
import com.areum.moneymanager.dto.member.log.UpdateLogDTO;
import com.areum.moneymanager.dto.member.request.MemberDeleteRequest;
import com.areum.moneymanager.dto.member.request.MemberRecoveryRequest;
import com.areum.moneymanager.dto.member.request.MemberSignUpRequest;
import com.areum.moneymanager.dto.member.request.MemberUpdateRequest;
import com.areum.moneymanager.dto.member.response.MemberInfoResponse;
import com.areum.moneymanager.dto.member.response.MemberMyPageResponse;
import com.areum.moneymanager.dto.member.response.MemberRecoveryResponse;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.enums.type.GenderType;
import com.areum.moneymanager.enums.type.HistoryType;
import com.areum.moneymanager.enums.type.MemberType;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.member.history.UpdateLogService;
import com.areum.moneymanager.service.member.validation.MemberValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.areum.moneymanager.enums.type.HistoryType.*;
import static com.areum.moneymanager.exception.code.ErrorCode.*;


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
@Service
public class MemberServiceImpl {

	@Autowired
	private PasswordEncoder passwordEncoder;


	private final ImageServiceImpl imageService;
	private final UpdateLogService historyService;
	private final MailService mailService;
	private final MemberInfoDaoImpl memberDAO;

	private final Logger logger = LogManager.getLogger(this);

	@Autowired
	public MemberServiceImpl(@Qualifier("profileImage") ImageServiceImpl imageService, UpdateLogService historyService, MailService mailService, MemberInfoDaoImpl memberDAO) {
		this.imageService = imageService;
		this.historyService = historyService;
		this.mailService = mailService;
		this.memberDAO = memberDAO;
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

			MemberInfo memberInfo = MemberInfo.builder()
					.member(Member.builder().id(memberId).type("C").role("ROLE_USER")
							.userName(signUp.getId()).password(passwordEncoder.encode(signUp.getPassword()))
							.name(signUp.getName()).nickName(signUp.getNickName()).email(signUp.getEmail())
							.build())
					.gender(signUp.getGender().toUpperCase().charAt(0)).build();


			if (memberDAO.saveMemberInfo(memberInfo)) {    //신규 회원 추가된 경우
				//생성된 회원 내역 추가
				UpdateLogDTO log =
						UpdateLogDTO.builder()
								.success(true).type("CREATE").item(HistoryType.MEMBER_JOIN)
								.memberId(memberId)
								.build();

				historyService.createLog(log);
				logger.debug("회원가입에 성공했습니다. (아이디: {}, 닉네임: {}, 이름: {}, 성별: {}, 이메일: {})", signUp.getId(), signUp.getNickName(), signUp.getName(), signUp.getGender(), signUp.getEmail());
			}
		} catch (IllegalArgumentException | DataAccessException | ErrorException e) {
			logger.debug("원인: {}", e.getMessage());
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

		String lastMemberId = memberDAO.findMaxMemberIdByLike(memberId + '%');

		if (Objects.isNull(lastMemberId)) {
			endNum = 1;
		} else {
			endNum = Integer.parseInt(lastMemberId.substring(5));

			if (endNum >= 999) {
				logger.debug("회원 식별번호 생성 불가 - 아이디: {}, 자동증가값: {}, 원인: 회원 식별번호의 자동증가값의 허용 범위({}) 초과", id, endNum, 999);
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
		return memberDAO.findUsernameByMemberId(memberId);
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

		logger.debug("마스킹 성공했습니다. (전: {}, 후: {})", value, String.valueOf(valueArr));
		return String.valueOf(valueArr);
	}


	/**
	 * 회원번호에 해당하는 회원정보를 반환합니다.
	 *
	 * @param memberId 회원번호
	 * @return 회원번호가 있으면 회원정보
	 */
	public MemberInfoResponse getMemberInfo(String memberId) {
		Member member = memberDAO.findMemberById(memberId);
		MemberInfo memberInfo = memberDAO.findMemberInfoById(memberId);

		//회원 프로필 이미지와 경로를 조회
		String profileImage = imageService.findImage(memberId);

		//날짜 포맷
		String formattedLastLoginDate = memberInfo.getLoginAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일"));
		String formattedJoinDate = member.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));


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
	 * @param memberId 회원 식별번호
	 * @return 마이페이지에 필요한 회원정보를 반환합니다.
	 */
	public MemberMyPageResponse.MemberInfo getMemberSummary(String memberId) {
		Member member = memberDAO.findMemberById(memberId);
		MemberInfo memberInfo = memberDAO.findMemberInfoById(memberId);

		//회원 프로필 이미지와 경로를 조회
		String profileImage = imageService.findImage(memberId);

		//날짜 포맷
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일");
		String formattedLastLogin = memberInfo.getLoginAt().toLocalDateTime().format(formatter);


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
			String id = memberDAO.findUsernameByMemberId(memberId);

			//이름 수정할 경우
			if (Objects.nonNull(update.getName())) {
				item = HistoryType.MEMBER_UPDATE_NAME;
				MemberValidationService.checkNameAvailability(update.getName());

				if (memberDAO.updateName(memberId, update.getName())) {
					historyService.createLog(UpdateLogDTO.builder().success(true).item(item).beforeInfo(beforeInfo.getName()).afterInfo(update.getName()).build());

					value = getMemberInfo(memberId).getName();
				}
			}

			//성별 수정할 경우
			if (Objects.nonNull(update.getGender())) {
				item = HistoryType.MEMBER_UPDATE_GENDER;

				if (memberDAO.updateGender(memberId, update.getGender().charAt(0))) {
					historyService.createLog(UpdateLogDTO.builder().success(true).item(item).beforeInfo(beforeInfo.getGender().getText()).afterInfo(update.getGender()).build());

					value = getMemberInfo(memberId).getGender().getText();
				}
			}

			//이메일 수정할 경우
			if (Objects.nonNull(update.getEmail())) {
				item = HistoryType.MEMBER_UPDATE_EMAIL;

				if (memberDAO.updateEmail(id, update.getEmail())) {
					historyService.createLog(UpdateLogDTO.builder().success(true).item(item).beforeInfo(beforeInfo.getEmail()).afterInfo(update.getEmail()).build());

					value = getMemberInfo(memberId).getEmail();
				}
			}
		} catch (DataAccessException | ErrorException e) {
			UpdateLogDTO history;

			if (e instanceof DataAccessException) {
				history = UpdateLogDTO.builder().success(false).item(item).cause("데이터베이스 문제로 수정 실패").build();
			} else if (e instanceof ErrorException) {
				history = UpdateLogDTO.builder().success(false).item(item).cause("'" + ((ErrorException) e).getErrorMessage() + "' 문제로 수정 실패").build();
			} else {
				history = UpdateLogDTO.builder().success(false).item(item).cause("알 수 없는 이유로 수정 실패").build();
			}

			historyService.createLog(history);
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
		String currentProfile = memberDAO.findProfileImageNameById(memberId);
		String changeFileName = imageService.changeFileName(profile.getAfterImage());

		MemberUpdateRequest.Profile changeProfile
				= MemberUpdateRequest.Profile.builder().reset(profile.isReset()).beforeImage(currentProfile)
				.afterImage(ImageDTO.builder().file(profile.getAfterImage().getFile()).fileExtension(profile.getAfterImage().getFileExtension()).fileName(changeFileName).build())
				.build();

		try {
			if (profile.isReset()) {
				if (memberDAO.updateProfile(memberId, null)) {
					imageService.deleteProfile(currentProfile);
				}
			} else {
				ImageDTO changeImage = changeProfile.getAfterImage();
				if (memberDAO.updateProfile(memberId, changeImage.getFileName())) {
					imageService.changeProfile(memberId, changeProfile);
				}
			}
		} catch (IOException e) {

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
		//DTO → Entity 변환
		Member memberEntity = Member.builder().name(findID.getName()).birthDate(findID.getBirth()).build();


		String id = memberDAO.findUsernameByNameAndBirth(memberEntity);
		if (Objects.isNull(id)) {    //아이디가 존재하지 않은 경우
			throw new ErrorException(ErrorCode.MEMBER_FIND_NONE);
		}


		//마스킹된 아이디와 마지막 접속일 조회
		String maskId = maskValue(id, id.length() - 3, id.length(), '*');
		Timestamp date = memberDAO.findLastLoginDateByUserName(id);


		//회원상태 별 안내문구
		String message;
		Member member = memberDAO.findLoginMemberByUsername(id);
		switch (member.getStatus().toUpperCase()) {
			case "A":
			case "L":
				logger.debug("정상 회원으로 아이디 찾기 성공했습니다. (아이디: {}, 마지막접속일: {})", id, date);
				message = "<span>찾으시는 아이디는 아래와 같습니다.</span>" +
						"<p>개인정보를 위해 일부 아이디는 *로 표시됩니다.</br>" +
						"아이디 전체를 알고 싶으시면 고객센터에 문의해주세요.</p>";
				break;
			case "D":
				Period period = Period.between(LocalDate.from(member.getDeletedAt().toLocalDateTime()), LocalDate.now());

				if (period.getDays() > 30) {
					message = "찾으시는 아이디는 탈퇴일로부터 30일이 지나 복구 불가능한 아이디입니다.<br>서비스를 이용하시려면 다시 가입해주시길 바랍니다.";
				} else {
					message = "찾으시는 아이디의 현재 상태는 아래와 같습니다.<br><br>탈퇴일로부터 30일이 지나지 않아 복구 가능한 아이디입니다.<br>복구를 원하신다면 로그인을 해주시길 바랍니다.<br>복구를 원하시지 않는다면 30일이 지난 후 다른 아이디로 다시 가입해주시길 바랍니다.";
				}
				break;
			default:
				throw new ErrorException(ErrorCode.MEMBER_FIND_ID);
		}

		if (Objects.isNull(date)) {        //한번도 접속하지 않은 회원인 경우
			return MemberRecoveryResponse.Id.builder().id(maskId).message(message).build();
		} else {
			String lastDate = date.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 hh시 mm분 ss초"));

			return MemberRecoveryResponse.Id.builder().id(maskId).message(message).lastDate(lastDate).build();
		}

	}


	/**
	 * 임시 비밀번호를 이메일로 전송합니다.<p>
	 * 이메일이 없다면 이메일은 전송되지 않으며, 이메일은 일부가 마스킹 처리됩니다.
	 *
	 * @param request http 요청정보를 담은 객체
	 * @param findPwd 비밀번호 찾기 정보
	 * @return 이메일이 있으면 마스킹된 이메일, 없으면 null
	 */
	public MemberRecoveryResponse.Password sendTemporaryPassword(HttpServletRequest request, MemberRecoveryRequest.Password findPwd) {
		//DTO → Entity 변환
		Member memberEntity = Member.builder().id(findPwd.getId()).name(findPwd.getName()).build();

		//회원 이메일 조회
		String email = memberDAO.findEmailByIdAndName(memberEntity);
		if (Objects.isNull(email)) {    //이메일이 없는 경우
			throw new ErrorException(ErrorCode.MEMBER_FIND_NONE);
		}

		//회원 상태별 안내문구
		String message;
		Member member = memberDAO.findStatusByUsername(findPwd.getId());
		switch (member.getStatus().toUpperCase()) {
			case "D":        //탈퇴한 경우
				Period period = Period.between(LocalDate.from(member.getDeletedAt().toLocalDateTime()), LocalDate.now());

				if (period.getDays() > 30) {
					message = "복구 불가능한 계정으로 비밀번호를 찾을 수 없습니다.";

					return MemberRecoveryResponse.Password.builder().message(message).build();
				}
			case "A":    //정상 로그인 가능한 경우
			case "L":        //계정이 잠긴 경우
				message = "<span>임의의 비밀번호를 아래 이메일로 보내드립니다.</span>" +
						"<span>해당 이메일로 확인 후 로그인 부탁드립니다.</span>";

				try {
					String password = mailService.send("password", findPwd.getName(), email);

					if (Objects.nonNull(password)) {    //임시 비밀번호 전송한 경우
						changePassword(member.getId(), password);

						request.getSession().setAttribute(email, password);

						//마스킹된  이메일
						String maskEmail = maskValue(email, email.length() <= 3 ? 0 : email.length() - 3, email.length(), '*');

						return MemberRecoveryResponse.Password.builder().email(maskEmail).message(message).build();
					}
				} catch (ErrorException e) {
					logger.debug("");
				}
			default:
				logger.debug("{} 회원 상태가 알 수 없기  상태({})이기 때문에 비밀번호를 찾을 수 없습니다.", member.getId(), member.getStatus());
				throw new ErrorException(ErrorCode.MEMBER_FIND_PASSWORD);
		}
	}


	/**
	 * 비밀번호를 변경 후 결과를 반환합니다.<p>
	 * 변경할 비밀번호가 현재 비밀번호와 동일하면 변경할 수 없습니다.
	 *
	 * @param memberId 회원번호
	 * @param password 변경할 비밀번호
	 */
	public void changePassword(String memberId, String password) {
		String id = memberDAO.findUsernameByMemberId(memberId);
		String currentPassword = memberDAO.findPasswordByUsername(id);

		if (passwordEncoder.matches(password, currentPassword)) {
			UpdateLogDTO history = UpdateLogDTO.builder().success(false).type("UPDATE").item(MEMBER_UPDATE_PASSWORD).cause("기존 비밀번호와 동일하여 변경 불가").build();

			historyService.createLog(history);

			throw new IllegalStateException();
		} else {
			if (memberDAO.updatePassword(id, passwordEncoder.encode(password))) {
				UpdateLogDTO history = UpdateLogDTO.builder().success(true).memberId(memberId).type("UPDATE").item(MEMBER_UPDATE_PASSWORD).build();
				historyService.createLog(history);
			}
		}
	}


	public void changeStatus(String memberId, String status) {
		if (Objects.isNull(memberId) || Objects.isNull(status)) {
			;
			throw new IllegalArgumentException("변경하려는 회원의 번호나 상태를 입력하지 않았습니다.");
		}

		Member memberEntity = Member.builder().id(memberId).status(status).build();
		switch (status) {
			case "A":    //로그인 가능한 정상 상태로 변경
				memberDAO.updateStatus(memberEntity);
				logger.debug("		🍋[END] {}회원의 상태를 정상 상태로 변경", memberId);
				break;
			case "L":    //로그인 불가능한 정상 상태로 변경
				memberDAO.updateStatus(memberEntity);
				logger.debug("		🍋[END] {}회원의 상태를 로그인 잠금 상태로 변경", memberId);
				break;
			case "D":    //서비스 탈퇴 & 복구 가능한 상태로 변경
				memberDAO.updateStatus(memberEntity);
				logger.debug("		🍋[END] {}회원의 상태를 탈퇴한 상태로 변경", memberId);
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
			logger.debug("회원탈퇴 실패 - 탈퇴사유 불문명");

			historyService.createLog(UpdateLogDTO.builder().success(false).type("DELETE").item(MEMBER_DELETE).afterInfo("탈퇴사유 미선택").build());

			throw new ErrorException(MEMBER_EXIT_REASON);
		} else if (delete.getCode().equals("05") && Objects.isNull(delete.getCause())) {
			logger.debug("기타사항 미입력");

			historyService.createLog(UpdateLogDTO.builder().success(false).type("DELETE").item(MEMBER_DELETE).afterInfo("기타사항 미입력").build());

			throw new ErrorException(MEMBER_EXIT_ETC);
		}


		String password = memberDAO.findPasswordByUsername(delete.getId());

		if (!passwordEncoder.matches(delete.getPassword(), password)) {
			logger.debug("비밀번호 불일치");

			historyService.createLog(UpdateLogDTO.builder().success(false).type("DELETE").item(MEMBER_DELETE).afterInfo("비밀번호 불일치").build());

			throw new ErrorException(MEMBER_PASSWORD_MISMATCH);
		} else {
			try {
				changeStatus(memberId, "D");

				if (memberDAO.updateResignDate(delete.getId())) {
					historyService.createLog(UpdateLogDTO.builder().success(true).type("DELETE").item(MEMBER_DELETE).afterInfo(delete.getCause()).build());

					logger.debug("회원탈퇴 성공 - 회원: {}, 상태: 복구 가능한 상태로 전환", memberId);
				}
			} catch (IllegalArgumentException ie) {
				historyService.createLog(UpdateLogDTO.builder().success(false).type("DELETE").item(MEMBER_DELETE).afterInfo("알 수 없는 원인").build());

				throw new ErrorException(ErrorCode.MEMBER_EXIT_UNKNOWN);
			}
		}
	}
}
