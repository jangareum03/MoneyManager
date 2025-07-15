package com.areum.moneymanager.service.main.validation;

import com.areum.moneymanager.enums.RegexPattern;
import com.areum.moneymanager.exception.ErrorException;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.areum.moneymanager.enums.ErrorCode.*;

/**
 * Q&A 데이터가 적합하게 입력되었는지 검증하는 클래스
 *
 * @version 1.0
 */
@Service
public class QnaValidationService {

	/**
	 * 제목이 적합한지 확인합니다.
	 *
	 * @param title	제목
	 */
	public static void checkTitleAvailability( String title ) {
		if( Objects.isNull(title) || title.trim().isEmpty() ) {
			throw new ErrorException(QUESTION_TITLE_NONE);
		}else if( !title.trim().matches(RegexPattern.QUESTION_TITLE.getPattern()) ) {
			throw new ErrorException(QUESTION_TITLE_FORMAT);
		}
	}



	/**
	 * 내용이 적합한지 확인합니다.
	 *
	 * @param content	내용
	 */
	public static void checkContentAvailability( String content ) {
		if( Objects.isNull(content) || content.trim().isEmpty() ) {
			throw new ErrorException(QUESTION_CONTENT_NONE);
		}else if( content.trim().length() >= 300 ) {
			throw new ErrorException(QUESTION_CONTENT_FORMAT);
		}
	}

}
