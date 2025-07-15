package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.main.QnADao;
import com.areum.moneymanager.dto.request.member.QuestionReq;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;



/**
 * Q&A와 관련된 작업을 처리하는 클래스</br>
 * 특정 Q&A 조회, 잠금여부 확인 등의 메서드 구현
 *
 * @version 1.0
 */
public class QnAService {


	/**
	 * 질문의 작성자가 맞는지 확인하는 메서드
	 *
	 * @param memberId        회원 식별번호
	 * @param id                                질문번호
	 * @return 질문번호의 작성자가 맞으면 true, 아니면 false
	 */
//	public boolean isAuthor( String memberId, String id ) {
//		try{
//			String author = qnADao.findMemberIdById(id);
//
//			return author.equals(memberId);
//		}catch ( EmptyResultDataAccessException e ) {
//			return false;
//		}
//	}
}
