package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.NoticeDao;
import com.areum.moneymanager.dto.request.main.SupportRequestDTO;
import com.areum.moneymanager.dto.response.main.SupportResponseDTO;
import com.areum.moneymanager.entity.Notice;
import com.areum.moneymanager.enums.type.NoticeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.areum.moneymanager.enums.ErrorCode.NOTICE_FIND_NONE;

@Service
public class NoticeService {

	private final Logger logger = LogManager.getLogger(this);
	private final NoticeDao noticeDao;


	public NoticeService( NoticeDao noticeDao ) {
		this.noticeDao = noticeDao;
	}



	public SupportResponseDTO.NoticeList getNoticesByPage(SupportRequestDTO.Page page) {
		//페이징 처리
		SupportRequestDTO.Page currentPage = initPage(page);

		int totalCount = noticeDao.countAll();
		SupportResponseDTO.Page noticePage = getPage( currentPage, totalCount );

		List<SupportResponseDTO.NoticeRow> noticeRows = new ArrayList<>();
		for( Notice notice : noticeDao.findNoticesByPage( (currentPage.getNum() - 1) * currentPage.getSize(), currentPage.getSize() )) {
			//날짜 포맷
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
			String formatDate = notice.getCreatedDate().toLocalDate().format(formatter);

			noticeRows.add( SupportResponseDTO.NoticeRow.builder().id(notice.getId()).title(notice.getTitle()).type( SupportResponseDTO.NoticeType.builder().code( notice.getType() ).text( NoticeType.match(notice.getType()) ).build() ).date(formatDate).view(notice.getViewCount()).build() );
		}

		return SupportResponseDTO.NoticeList.builder().notices( noticeRows ).page( noticePage ).build();
	}



	/**
	 * 공지사항에 해당하는 페이징을 초기화 진행 후 반환합니다. <br>
	 * 현재 페이지와 한번에 표시할 공지사항 수가 없다면 서비스에서 설정한 기본 설정값으로 초기화됩니다.
	 *
	 * @param page			선택 페이지 정보
	 * @return	공지사항 정보
	 */
	private SupportRequestDTO.Page initPage( SupportRequestDTO.Page page ) {
		int currentPage = Objects.isNull(page.getNum()) ? 1 : page.getNum();
		int size = Objects.isNull(page.getSize()) ? 10: page.getSize();

		return SupportRequestDTO.Page.builder().num(currentPage).size(size).build();
	}



	/**
	 * 공지사항 페이징을 반환합니다. <br>
	 *
	 * @param page									전달받은 페이지 객체
	 * @param totalCount				총 공지사항 개수
	 * @return	공지사항에 따른 페이징 처리
	 */
	private SupportResponseDTO.Page getPage(SupportRequestDTO.Page page, int totalCount ) {
		int limit = 5;
		boolean isPrev = true, isNext = true;

		int end = (int) Math.ceil( page.getNum() / (double)limit ) * limit;
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

		return SupportResponseDTO.Page.builder().page( page.getNum() ).size( page.getSize() ).start( start ).end( end ).isPrev( isPrev ).isNext( isNext ).build();
	}





	/**
	 * 공지사항 번호에 해당하는 공지사항 상세 정보를 반환합니다.
	 *
	 * @param id			공지사항 번호
	 * @return	공지사항 정보
	 */
	public SupportResponseDTO.Notice getNoticeById( String id ) {
		int count = noticeDao.countNoticeById(id);

		if( count != 1 ) {
			throw new IllegalArgumentException(NOTICE_FIND_NONE.getMessage());
		}

		Notice notice = noticeDao.findNoticeById(id);
		increaseViewCount(id);

		//날짜 포맷
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일");
		String formatDate = notice.getCreatedDate().toLocalDate().format(formatter);

		return SupportResponseDTO.Notice.builder().title( notice.getTitle() ).content(notice.getContent()).date(formatDate).build();
	}


	private void increaseViewCount( String id ) {
		Long count = noticeDao.updateReadCount(id);

		logger.debug("{} 공지사항 조회수 증가 - {}", id, count);
	}
}
