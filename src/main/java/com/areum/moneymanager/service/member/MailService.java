package com.areum.moneymanager.service.member;

import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;

import static com.areum.moneymanager.enums.ErrorCode.*;


/**
 * 메일 전송을 담당하는 클래스</br>
 * 메일 형식, 인증키 확인, 메일 보내기 등의 기능을 처리
 *
 * @version 1.0
 */
@Service
public class MailService {

	private final Logger logger = LogManager.getLogger(this);
	private final JavaMailSender mailSender;

	@Value("${spring.mail.email}")
	private String address;	//수신 이메일 주소
	private String key;

	public MailService( JavaMailSender javaMailSender ) {
		this.mailSender = javaMailSender;
	}




	/**
	 * 이메일 유형에 따라서 이메일로 임시 비밀번호 또는 인증키를 전송합니다.<p>
	 * 전송이 성공하면 전송된 임시 비밀번호 또는 인증키를 반환합니다.
	 *
	 * @param type 		이메일 형식 유형
	 * @param name   이름
	 * @param to				전송할 이메일
	 * @return 이메일 전송 성공하면 유형에 따른 결과값, 전송 실패하면 null
	 */
	public String send( String type, String name, String to ) {
		try{
			if( type.equals("password") ) {
				mailSender.send( getPassword( to, name ) );
				logger.debug("		🍋[END] {} 회원님의 {} 이메일에 임시 비밀번호({})를 전송 성공했습니다.", name, to, key);
			}else if( type.equals("email") ) {
				logger.debug("		🍋[END] {} 회원님의 {} 이메일에 인증코드를 전송 성공했습니다.", name, to);
				mailSender.send( getEmailCode(to, name) );
			}else {
				logger.debug("		🍋[END] {} 이메일에 전송 실패했습니다. (원인: 이메일 형식 유형 알 수 없음)", to);
				throw new ErrorException(EMAIL_SEND_FORMAT);
			}
		}catch ( MessagingException | UnsupportedEncodingException e ) {
			logger.debug("		🍋[END] {} 이메일에 전송 실패했습니다. (원인: {})", to, e.getMessage());
			throw new ErrorException(EMAIL_SEND_UNKNOWN);
		}

		return key;
	}



	/**
	 * 이메일 인증하기 위한 이메일 내용을 작성하는 메서드
	 *
	 * @param to				발신 이메일
	 * @param name	사용자 이름
	 * @return	이메일의 내용이 HTML 구성된 객체
	 * @throws MessagingException	이메일 관련 작업 문제 발생 시
	 * @throws UnsupportedEncodingException	인코딩이 지원되지 않을 시
	 */
	private MimeMessage getEmailCode( String to, String name ) throws MessagingException, UnsupportedEncodingException {
		String greetings = Objects.isNull(name) ? "안녕하세요! 신규 가입을 환영하며, " : "안녕하세요. " + name +"님!<br>";

		MimeMessage message = mailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.TO, to);	//수신사 설정
		message.setSubject("[돈매니저] 회원가입 이메일 인증코드 보내드립니다.");	//메일 제목 설정

		this.key = createCode(); //이메일 인증코드 생성

		String content = "<html><body>" +
						"<meta http-equiv='Content-type' content='text/html; charset=utf-8'>" +greetings +
					 	"돈매니저를 선택해주셔서 진심으로 감사드립니다.<br>" +
						"회원님의 계정을 안전하게 보고하고 원활한 서비스 이용을 위한 이메일 주소 인증이 필요합니다.<br>" +
						"아래 인증코드를 입력하여 이메일 주소를 인증해주세요.<br>" +
						"<hr>" +
						"인증코드: " + "<span style='font-weight: bold:color: #2a50ae'>" +
						key +
						"</span><hr>" +
						"📢중요한 안내 사항<br>" +
						"✔ 인증코드는 발송 후 5분 동안만 유효합니다.<br>" +
						"✔ 인증시간이 초과되었으면 다시 한 번 전송버튼을 클릭해주세요.<br><br>" +
						"이메일 인증 과정에서 문제가 발생하거나 추가적인 도움이 필요하시다면 고객센터로 연락 부탁드립니다.<br>" +
						"다시 한 번 돈매니저에 가입해 주셔서 감사드리며, 더욱 편리하고 안전한 서비스를 제공하기 위해 항상 노력하겠습니다.<br>" +
						"감사합니다.<br>돈매니저 드림" +
						"</body></html>";

		message.setText( content, "UTF-8", "html" );
		message.setFrom( new InternetAddress(address, "돈매니") );

		return message;
	}



	/**
	 *  이메일 인증 시 필요한 코드를 생성하는 메서드
	 *
	 * @return	이메일 인증 코드
	 */
	private String createCode() {
		StringBuilder code = new StringBuilder();
		Random random = new Random();


		for( int i=0; i<6; i++ ) {
			int index = random.nextInt(3);

			switch ( index ) {
				case 0:
					code.append( (char)(random.nextInt(26) + 97) ); //영어 소문자
					break;
				case 1:
					code.append( (char)(random.nextInt(26) + 65) );	//영어 대문자
					break;
				case 2:
					code.append( random.nextInt(10) );	//숫자
					break;
			}
		}

		return code.toString();
	}



	/**
	 * 임시 비밀번호를 전송하기 위한 이메일 내용을 작성하는 메서드
	 *
	 * @param to				발신 이메일
	 * @param name	사용자 이름
	 * @return	이메일의 내용이 HTML 구성된 객체
	 * @throws MessagingException	이메일 관련 작업 문제 발생 시
	 * @throws UnsupportedEncodingException	인코딩이 지원되지 않을 시
	 */
	private MimeMessage getPassword( String to, String name ) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = mailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.TO, to);
		message.setSubject("[돈매니저] 임시 비밀번호를 발급해드립니다.");

		this.key = createPassword();

		String content = "<html><body>" +
						"<meta http-equiv='Content-type' content='text/html; charset=utf-8'>" +
						"안녕하세요. " + name + "님!<br>" +
						"돈매니저를 이용해주셔서 감사드립니다.<br>" +
						"회원님이 요청하신 임시 비밀번호를 발급해드렸습니다.<br>" +
						"아래 정보를 확안히시고 로그인 후 반드시 비밀번호를 변경해주세요.<br>" +
						"<hr>" +
						"임시 비밀번호: " + "<span style='font-weight: bold:color: #2a50ae'>" +
						key +
						"</span><hr>" +
						"고객님 본인이 요청하신 경우가 아니거나 문의사항이 있으시면 고객센터로 연락 부탁드립니다.<br>" +
						"감사합니다.<br>돈매니저 드림" +
						"</body></html>";

		message.setText(content, "UTF-8", "html" );
		message.setFrom( new InternetAddress(address, "돈매니저"));

		return message;
	}



	/**
	 *  임시 비밀번호를 생성하는 메서드
	 *
	 * @return	임시 비밀번호
	 */
	private String createPassword() {
		StringBuilder password = new StringBuilder();
		Random random = new Random();

		int min = 8, max = 20;
		int size= random.nextInt( max - min + 1 ) + min;

		for ( int i=0; i<=size; i++ ) {
			int index = random.nextInt(3);

			switch ( index ) {
				case 0:
					password.append( (char)(random.nextInt(26) + 97) ); //영어 소문자
					break;
				case 1:
					password.append( (char)(random.nextInt(26) + 65) );	//영어 대문자
					break;
				case 2:
					password.append( random.nextInt(10) );	//숫자
					break;
			}
		}

		return password.toString();
	}


}
