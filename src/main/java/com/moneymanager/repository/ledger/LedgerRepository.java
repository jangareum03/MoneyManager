package com.moneymanager.repository.ledger;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Place;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 패키지이름    : com.moneymanager.repository.ledger<br>
 * 파일이름       : LedgerRepository<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10.<br>
 * 설명              : 가계부 데이터를 조작하는 클래스
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
 * 		 	  <td>26. 1. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class LedgerRepository {

	private final JdbcTemplate jdbcTemplate;

	private final String baseInsertQuery = "INSERT INTO ledger(id, code, member_id, transaction_date, category_id, amount%s) VALUES (ledger_seq.NEXTVAL, ?, ?, ?, ?, ?%s)";

	public LedgerRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	/**
	 *	{@code ledger} 테이블에 가계부 정보를 저장합니다.
	 *<p>
	 *    다음과 같은 정보를 저장할 수 있습니다.
	 *    <ul>
	 *        <li>필수정보 - 고유번호({@code code}), 회원ID({@code memberId}), 거래날짜({@code date}), 카테고리({@code category}), 금액({@code amount})</li>
	 *        <li>선택정보 - 메모({@code memo}), 금액유형({@code paymentType}), 주소({@code placeName}, {@code roadAddress}, {@code detailAddress}), 가계부 반복({@code fix}, {@code fixCycle})</li>
	 *    </ul>
	 *</p>
	 * 필수 정보 중 하나라도 없거나 {@code NULL}인 경우
	 * {@link org.springframework.dao.DataIntegrityViolationException}이 발생합니다.
	 *
	 * @param ledger 저장할 가계부 정보를 담은 {@link Ledger} 객체
	 * @return	저장된 가계부의 생성된 식별자
	 * @throws org.springframework.dao.DataIntegrityViolationException 제약 조건을 위반했을 경우
	 */
	public Long insertLedger(Ledger ledger) {
		Map<String, Object> sqlResult = buildSqlAndParams(baseInsertQuery, ledger);

		String sql = (String) sqlResult.get("sql");
		List<Object> params = (List<Object>) sqlResult.get("params");

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(
				con -> {
					PreparedStatement ps =
							con.prepareStatement(sql, new String[] {"id"});

							for( int i=0; i<params.size(); i++ ) {
								ps.setObject(i+1, params.get(i));
							}

							return ps;
				},
				keyHolder
		);

		return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}


	/**
	 * {@code ledger}테이블에 저장된 가계부 정보를 조회합니다.
	 * <p>
	 *     가계부 번호({@code id})를 기준으로 가계부 정보가 존재하면 반환하며,
	 *     조회된 정보가 없는 경우 {@link org.springframework.dao.EmptyResultDataAccessException}이 발생합니다.
	 * </p>
	 *
	 * @param id	가계부 번호
	 * @return	번호에 해당하는 가계부 정보를 담은 {@link Ledger} 객체
	 * @throws org.springframework.dao.EmptyResultDataAccessException 조회된 정보가 없는 경우
	 */
	public Ledger selectLedgerById(Long id) {
		String sql = "SELECT id, code, member_id, category_id, fix, fix_cycle, transaction_date, memo, amount, payment_type, place_name, road_address, detail_address, created_at, updated_at" +
							"	FROM ledger" +
							"	WHERE id = ?";

		return jdbcTemplate.queryForObject(
				sql,

				(rs, row) -> {
					String fixed = rs.getString("fix");
					String fixCycle = rs.getString("fix_cycle");

					FixedYN fixedYN = FixedYN.of(fixed);

					FixedPeriod cycleType = null;
					if( fixedYN.isFixed() ) {
						cycleType = FixedPeriod.of(fixCycle);
					}

					return Ledger.builder()
							.id(rs.getLong("id"))
							.code(rs.getString("code"))
							.memberId(rs.getString("member_id"))
							.category(rs.getNString("category_id"))
							.fix(fixedYN)
							.fixCycle(cycleType)
							.date(rs.getString("transaction_date"))
							.memo(rs.getString("memo"))
							.amount(rs.getLong("amount"))
							.paymentType(PaymentType.of(rs.getString("payment_type")))
							.place(
									new Place(rs.getString("place_name"),rs.getString("road_address"), rs.getString("detail_address"))
							)
							.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
							.updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
							.build();
				},

				id
		);
	}

	//기본 쿼리문으로 최종적으로 사용할 쿼리문 및 파라미터 생성
	private Map<String, Object> buildSqlAndParams(String base, Ledger ledger) {
		Map<String, Object> result = new HashMap<>();

		List<String> columns = new ArrayList<>();
		List<Object> values = new ArrayList<>();

		//기본값(필수)
		values.add(ledger.getCode());
		values.add(ledger.getMemberId());
		values.add(ledger.getDate());
		values.add(ledger.getCategory());
		values.add(ledger.getAmount());

		//선택값
		if(ledger.getFix() != null && ledger.getFix().isFixed()) {
			columns.add("fix");
			values.add(ledger.getFix().getValue());

			columns.add("fix_cycle");
			values.add(ledger.getFixCycle().getValue());
		}

		if(ledger.getMemo() != null) {
			columns.add("memo");
			values.add(ledger.getMemo());
		}

		if(ledger.getPaymentType() != null && ledger.getPaymentType() != PaymentType.NONE) {
			columns.add("payment_type");
			values.add(ledger.getPaymentType().getValue());
		}

		if(ledger.getPlace().getName() != null && ledger.getPlace().getRoadAddress() != null) {
			columns.add("place_name");
			values.add(ledger.getPlace().getName());

			columns.add("road_address");
			values.add(ledger.getPlace().getRoadAddress());
		}

		if(ledger.getPlace().getDetailAddress() != null) {
			columns.add("detail_address");
			values.add(ledger.getPlace().getDetailAddress());
		}

		if(ledger.getUpdatedAt() != null) {
			columns.add("updated_at");
			values.add(ledger.getUpdatedAt());
		}

		//쿼리문
		String columnSql = "";
		String valueSql = "";
		if(!columns.isEmpty()) {
			columnSql = ", " + String.join(", ", columns);
			valueSql = ", " + columns.stream().map(c -> "?")
					.collect(Collectors.joining(", "));
		}

		String sql = String.format(
				base,
				columnSql,
				valueSql
		);

		result.put("sql", sql);
		result.put("params", values);

		return result;
	}

}
