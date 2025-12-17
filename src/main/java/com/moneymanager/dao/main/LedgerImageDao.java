package com.moneymanager.dao.main;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.dao.main<br>
 * 파일이름       : LedgerImageDao<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 17<br>
 * 설명              : 가계부 이미지 데이터를 조작하는 킄래스
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
 * 		 	  <td>25. 12. 17.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Repository
public class LedgerImageDao {

	private final JdbcTemplate jdbcTemplate;

	public LedgerImageDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}



	/**
	 * 특정 가계부에 저장할 이미지를 모두 {@code ledger_image} 테이블에 저장합니다.
	 * <p>
	 *     매개변수로 전달받은 {@link LedgerImage} 객체가 null이면 저장하지 않습니다. 저장할 요소는 이미지 ID, 가계부 ID, 이미지 저장한 상대 경로, 정렬 순서, 등록일 입니다.
	 * </p>
	 *
	 * @param id				이미지를 등록할 가계부 ID
	 * @param image		이미지 정보를 담은 {@link LedgerImage} 객체
	 */
	public void insertImageByLedger(Long id, LedgerImage image) {
		String sql = "INSERT INTO ledger_image(id, ledger_id, image_path, sort_order, created_at) " +
				"VALUES(ledger_image_seq.NEXTVAL, ?, ?, ?, ?)";

		jdbcTemplate.update(
				sql,
				id, image.getImagePath(), image.getSortOrder(), image.getCreatedAt()
		);
	}


	/**
	 *	특정 가계부에 저장한 이미지 정보를 조회합니다.
	 *<p>
	 *     반환된 이미지 정보는 {@link LedgerImage} 객체 리스트로 제공되어, 각 LedgerImage에는 이미지 식별번호, 이미지 경로, 나열 순서, 등록일, 수정일이 포함되어 있습니다.
	 *</p>
	 *
	 * @param id		조회할 이미지의 가계부 ID
	 * @return	가계부에 등록된 {@link LedgerImage} 객체 리스트
	 */
	public List<LedgerImage> findImageListByLedger(Long id) {
		String sql = "SELECT * " +
								"FROM ledger_image " +
								"WHERE ledger_id = ?";

		return jdbcTemplate.query(
				sql,

				rs -> {
					List<LedgerImage> images = new ArrayList<>();

					while ( rs.next() ) {
						LedgerImage image = LedgerImage.builder()
								.id(rs.getLong("id"))
								.imagePath(rs.getString("image_path"))
								.sortOrder(rs.getInt("sort_order"))
								.createdAt(rs.getTimestamp("created_at").toLocalDateTime())
								.updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
								.build();

						images.add(image);
					}

					return images;
				}
		);
	}


	/**
	 *	특정 가계부에 저장한 이미지 정보를 데이터베이스에서 수정합니다.
	 *<p>
	 *     전달받은 {@link LedgerImage} 객체의 이미지 경로를 기준으로 가계부 ID에 저장된 이미지를 수정하며, 수정일시는 현재 시간으로 자동 수정됩니다.
	 *</p>
	 *
	 * @param image	수정할 이미지 정보를 담은 {@link LedgerImage} 객체
	 */
	public void updateImageByLedger(LedgerImage image) {
		String sql = "UPDATE ledger_image SET image_path = ? updated_at = SYSDATE WHERE ledger_id = ?";

		jdbcTemplate.update(
				sql,
				image.getImagePath(), image.getLedgerId().getId()
		);
	}
}