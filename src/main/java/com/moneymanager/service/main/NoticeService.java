package com.moneymanager.service.main;

import com.moneymanager.dao.main.NoticeDao;
import com.moneymanager.dto.common.request.PageRequest;
import com.moneymanager.dto.common.response.PageResponse;
import com.moneymanager.dto.notice.response.NoticeDetailResponse;
import com.moneymanager.dto.notice.response.NoticeListResponse;
import com.moneymanager.entity.Notice;
import com.moneymanager.enums.type.NoticeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.main<br>
 *  * 파일이름       : NoticeService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 23. 2. 6<br>
 *  * 설명              : 공지사항 관련 비즈니스 로직을 처리하는 클래스
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
 *		 	  <td>23. 2. 6</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Service
public class NoticeService {

	private final Logger logger = LogManager.getLogger(this);
	private final NoticeDao noticeDao;


	public NoticeService( NoticeDao noticeDao ) {
		this.noticeDao = noticeDao;
	}



	public NoticeListResponse getNoticesByPage(PageRequest page) {
		//페이징 처리
		PageRequest currentPage = initPage(page);

		int totalCount = noticeDao.countAll();
		PageResponse noticePage = getPage( currentPage, totalCount );

		List<NoticeListResponse.Row> noticeRows = new ArrayList<>();
		for( Notice notice : noticeDao.findNoticesByPage( (currentPage.getPage() - 1) * currentPage.getSize(), currentPage.getSize() )) {
			//날짜 포맷
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
			String formatDate = notice.getCreatedDate().toLocalDate().format(formatter);

			noticeRows.add( NoticeListResponse.Row.builder().id(notice.getId()).title(notice.getTitle()).type( NoticeListResponse.Type.builder().code( notice.getType() ).text( NoticeType.match(notice.getType()) ).build() ).date(formatDate).view(notice.getViewCount()).build() );
		}

		return NoticeListResponse.builder().list( noticeRows ).page( noticePage ).build();
	}



	/**
	 * 공지사항에 해당하는 페이징을 초기화 진행 후 반환합니다. <br>
	 * 현재 페이지와 한번에 표시할 공지사항 수가 없다면 서비스에서 설정한 기본 설정값으로 초기화됩니다.
	 *
	 * @param page			선택 페이지 정보
	 * @return	공지사항 정보
	 */
	private PageRequest initPage( PageRequest page ) {
		int currentPage = Objects.isNull(page.getPage()) ? 1 : page.getPage();
		int size = Objects.isNull(page.getSize()) ? 10: page.getSize();

		return new PageRequest( currentPage, size );
	}



	/**
	 * 공지사항 페이징을 반환합니다. <br>
	 *
	 * @param page									전달받은 페이지 객체
	 * @param totalCount				총 공지사항 개수
	 * @return	공지사항에 따른 페이징 처리
	 */
	private PageResponse getPage(PageRequest page, int totalCount ) {
		int limit = 5;
		boolean isPrev = true, isNext = true;

		int end = (int) Math.ceil( page.getPage() / (double)limit ) * limit;
		int start = end - (limit - 1);

		//마지막 페이지 설정
		int max = (int)Math.ceil( totalCount / (double) page.getSize() ) == 0 ? 1 : (int)Math.ceil( totalCount / (double) page.getSize() );
		if( end >= max  ) {
			end = max;
			isNext = false;
		}

		if( start == 1 ) {
			isPrev = false;
		}

		return PageResponse.builder().page( page.getPage() ).size( page.getSize() ).startPage( start ).endPage( end ).isPrev( isPrev ).isNext( isNext ).build();
	}





	/**
	 * 공지사항 번호에 해당하는 공지사항 상세 정보를 반환합니다.
	 *
	 * @param id			공지사항 번호
	 * @return	공지사항 정보
	 */
	public NoticeDetailResponse getNoticeById(String id ) {
		int count = noticeDao.countNoticeById(id);

		if( count != 1 ) {

		}

		Notice notice = noticeDao.findNoticeById(id);
		increaseViewCount(id);

		//날짜 포맷
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일");
		String formatDate = notice.getCreatedDate().toLocalDate().format(formatter);

		return NoticeDetailResponse.builder().title( notice.getTitle() ).content(notice.getContent()).date(formatDate).build();
	}


	private void increaseViewCount( String id ) {
		Long count = noticeDao.updateReadCount(id);

		logger.debug("{} 공지사항 조회수 증가 - {}", id, count);
	}
}
