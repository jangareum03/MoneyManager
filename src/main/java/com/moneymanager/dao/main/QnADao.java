package com.moneymanager.dao.main;

import com.moneymanager.dto.inquiry.request.InquirySearchRequest;
import com.moneymanager.entity.Admin;
import com.moneymanager.entity.Answer;
import com.moneymanager.entity.Member;
import com.moneymanager.entity.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.main<br>
 *  * 파일이름       : QnADao<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 문의사항 데이터를 조작하는 클래스
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
 *		</tbody>
 * </table>
 */
@Repository
public class QnADao {

	private final Logger logger = LogManager.getLogger(this);
	private final JdbcTemplate jdbcTemplate;

	public QnADao(DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}



	/**
	 * 질문 데이터를 추가하는 메서드
	 *
	 * @param question		등록하려는 질문 정보
	 * @return	데이터 추가 완료하면 true, 실패하면 false
	 */
	public Long saveQuestion( Question question ) {
		String query = "INSERT INTO tb_qa_question(id, member_id, title, content, open) VALUES (seq_question.NEXTVAL, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement stmt = con.prepareStatement(query, new String[]{"id"});

						stmt.setString(1, question.getMember().getId());
						stmt.setString(2, question.getTitle());
						stmt.setString(3, question.getContent());
						stmt.setString(4, String.valueOf(question.getOpen()));

						return stmt;
					}
				}, keyHolder
		);

		return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}


	/**
	 * 검색 조건에 맞는 질문의 총 개수를 조회하는 메서드
	 *
	 * @param search	질문 검색 조건
	 * @return 총 질문 개수
	 */
	public Integer countQuestionBySearch( InquirySearchRequest search ) {
		StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM tb_qa_question");

		List<Object> params = new ArrayList<>();
		switch ( search.getMode() ) {
			case "title":
				//제목으로 검색한 경우
				query.append(" WHERE title LIKE ?");
				params.add("%" + search.getKeyword().get(0) + "%");
				break;
			case "writer":
				//작성자 닉네임으로 검색한 경우
				query.append(" WHERE member_id IN (SELECT id FROM tb_member WHERE nickname LIKE ?)");
				params.add("%" + search.getKeyword().get(0) + "%");
				break;
			case "period":
				//등록일으로 검색한 경우
				query.append(" WHERE TRUNC(created_date) BETWEEN TO_CHAR(?, 'YYYYMMDD') AND TO_CHAR(?, 'YYYYMMDD')");

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				for( String keyword : search.getKeyword() ) {
					LocalDate period = LocalDate.parse( keyword, formatter );

					params.add(period);
				}
				break;
			case "all":
			default:
				//검색하지 않은 경우
		}

		return jdbcTemplate.queryForObject(
						query.toString(),
						Integer.class,
						params.toArray()
		);
	}



	/**
	 * Q&A 번호 사이에 존재하는 질문들을 조회하는 메서드
	 *
	 * @param offset		게시물 시작 번호
	 * @param size			한 페이지당 보여질 게시물 개수
	 * @return	시작번호와 종료번호에 해당하는 질문
	 */
	public List<Question> getQuestionsByPage( int offset, int size ) {
		String query = "SELECT tq.*, tm.nickname " +
									"FROM tb_qa_question tq, tb_member tm " +
									"WHERE tm.id = tq.member_id " +
									"ORDER BY created_date DESC " +
									"OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

		return jdbcTemplate.query(
				query,
				( rs, row ) -> {
					return Question.builder()
							.id(rs.getLong("id"))
							.title(rs.getString("title")).open(rs.getString("open").charAt(0))	.createdDate( rs.getTimestamp("created_date") )
							.answer(rs.getString("answer").charAt(0))
							.member( Member.builder().nickName(rs.getString("nickname")).build() )
							.build();
				},
				offset, size
		);
	}




	/**
	 * Q&A 번호 사이에 존재하는 질문들을 조회하는 메서드
	 *
	 * @param search		검색조건
	 * @return	시작번호와 종료번호에 해당하는 질문
	 */
	public List<Question> getQuestionsBySearch( InquirySearchRequest search ) {
		StringBuilder query
				= new StringBuilder("SELECT tq.*, tm.nickname " +
				"FROM tb_qa_question tq, tb_member tm " +
				"WHERE tm.id = tq.member_id ");


		List<Object> params = new ArrayList<>();
		switch ( search.getMode() ) {
			case "title":
				//제목으로 검색한 경우
				query.append("AND open = 'Y' ");
				query.append("AND title LIKE ?	");

				params.add("%" + search.getKeyword().get(0) + "%");
				break;
			case "writer":
				//작성자 닉네임으로 검색한 경우
				query.append("AND member_id IN (SELECT id FROM tb_member WHERE nickname LIKE ? )");

				params.add("%" + search.getKeyword().get(0) + "%");
				break;
			case "period":
				//등록일으로 검색한 경우
				query.append("AND TRUNC(created_date) BETWEEN TO_CHAR(?, 'YYYYMMDD') AND TO_CHAR(?, 'YYYYMMDD') ");

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				for( String keyword : search.getKeyword() ) {
					LocalDate period = LocalDate.parse( keyword, formatter );

					params.add(period);
				}
				break;
			case "answer":
				//답변여부로 검색한 경우
				query.append("AND answer = ? ");

				params.add(search.getKeyword().get(0));
				break;
			case "all":
			default:
				//검색하지 않은 경우
		}

		query.append("ORDER BY created_date DESC ");
		query.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

		return jdbcTemplate.query(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement stmt = con.prepareStatement(query.toString());

						int index = 0;
						for( int i=0; i<params.size(); i++ ) {
							index = i+1;
							stmt.setObject(index, params.get(i));
						}
						stmt.setInt( ++index, (search.getPage().getSize() - 1) * search.getPage().getSize() );
						stmt.setInt( ++index, search.getPage().getSize() );

						return stmt;
					}
				},
				( rs, row ) -> Question.builder()
						.id(rs.getLong("id"))
						.title(rs.getString("title")).open(rs.getString("open").charAt(0))	.createdDate( rs.getTimestamp("created_date") )
						.answer(rs.getString("answer").charAt(0))
						.member( Member.builder().nickName(rs.getString("nickname")).build() )
						.build()
		);
	}



	/**
	 * 번호에 해당하는 질문을 조회하는 메서드
	 *
	 * @param id	질문 번호
	 * @return	질문정보
	 */
	public Question findQuestionById( Long id ) {
		String query = "SELECT tq.created_date, tq.open, tq.title, tq.content, tq.answer, tm.nickname " +
									"FROM tb_qa_question tq, tb_member tm " +
									"WHERE tm.id = tq.member_id " +
										"AND tq.id = ?";

		return jdbcTemplate.queryForObject(
						query,
						(ResultSet rs, int row) ->
										Question.builder()
														.createdDate(rs.getTimestamp("created_date")).answer( rs.getString("answer").charAt(0) )
														.title(rs.getString("title")).content(rs.getString("content")).open(rs.getString("open").charAt(0))
														.member(Member.builder().nickName(rs.getString("nickname")).build())
														.build(),
						id
		);
	}



	/**
	 * 번호에 해당하는 답변을 조회하는 메서드
	 *
	 * @param id	질문 번호
	 * @return	답변정보
	 */
	public Answer findAnswerByQuestionId(Long id ) {
		 String sql = "SELECT tqq.title, tqa.content, tqa.created_date, ta.nickname " +
						 								"FROM tb_qa_question tqq " +
						 									"JOIN tb_qa_answer tqa ON tqq.id = tqa.question_id " +
						 									"JOIN tb_admin ta ON tqa.admin_id = ta.id " +
						 							"WHERE tqq.id = ?";

		 return jdbcTemplate.queryForObject(
						 sql,
						 (ResultSet rs, int row) ->
										 Answer.builder()
														 .question(Question.builder().title(rs.getString("title")).build())
														 .admin(Admin.builder().nickName(rs.getString("nickname")).build())
														 .content(rs.getString("content")).createdDate(rs.getDate("created_date"))
														 .build(),
						 id
		 );
	}



	public String findMemberIdByQuestionId( Long id ) {
		String query = "SELECT member_id " +
									"FROM tb_qa_question " +
									"WHERE id = ?";

		return jdbcTemplate.queryForObject(
				query,
				String.class,
				id
		);
	}



	public boolean updateQuestion( String memberId, Question question ) {
		String query = "UPDATE tb_qa_question SET title = ?, open = ?, content = ?, updated_date = SYSDATE WHERE member_id = ? AND id = ?";

		return jdbcTemplate.update(
				query,
				question.getTitle(), question.getOpen(), question.getContent(),
				memberId, question.getId()
		) == 1;
	}




	public boolean deleteQuestion( String memberId, Long id ) {
		String query = "DELETE FROM tb_qa_question WHERE member_id = ? AND id =?";

		return jdbcTemplate.update(
				query,
				memberId, id
		) == 1;
	}
}
