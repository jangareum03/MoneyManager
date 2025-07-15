package com.areum.moneymanager.dto.response.main;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 서버에서 가계부 관련 작업을 사용자에게 전송할 데이터 객체
 */
public class BudgetBookResponseDTO {


	/**
	 * 가게부 작성 시 필요한 객체입니다.<p>
	 * <b color='white'>date</b>는 제목,
	 * <b color='white'>type</b>는 유형,
	 * <b color='white'>categoryList</b>카테고리 리스트,
	 * <b color='white'>maxImage</b>등록할 수 있는 이미지 개수입니다.
	 */
	@Builder
	@Getter
	public static class Write {
		private String date;
		private String type;
		private List<CategoryResponseDTO.Read> categories;
		private int maxImage;
	}



	/**
	 * 가계부 내역의 요약정보를 볼 때 필요한 객체입니다.<br>
	 *  <b color='white'>details</b> 요약정보, <b color='white'>price</b> 가격입니다.
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class Summary {
		private Long id;
		private String code;
		private String name;
		private String memo;
		private Long price;
	}



	/**
	 * 가계부 내역의 요약정보를 볼 때 필요한 객체입니다.<br>
	 *  <b color='white'>info</b> 요약정보, <b color='white'>price</b> 가격입니다.
	 */
	@Builder
	@Getter
	@ToString
	public static class Preview {
		private Map<String, List<BudgetBookResponseDTO.Summary>> info;
		private Map<String, Long> price;
	}


	@Builder
	@Getter
	// 특정 가계부 내역 읽어올 때
	public static class Detail {
		private Long id;
		private FormatDate date;
		private String fix;
		private String fixCycle;
		private CategoryResponseDTO.Read category;
		private String memo;
		private Long price;
		private String paymentType;
		private List<String> images;
		private Place place;
	}


	/**
	 * 가계부 내역의 날짜를 읽어올 때 필요한 객체입니다. <br>
	 * <b color='white'>read</b>는 time 태그용,
	 * <b color='white'>text</b>는 html 문자용입니다.
	 */
	@Builder
	@Getter
	public static class FormatDate {
		private String read;
		private String text;
	}


	@Builder
	@Getter
	// 가계부 위치
	public static class Place {
		private String name;
		private String roadAddress;
		private String address;
	}



	/**
	 * 가계부 내역을 검색 시 필요한 객체입니다.<br>
	 *  <b color='white'>mode</b>는 검색유형, <b color='white'>type</b>가계부 날짜 기간 범위, <b color='white'>menu</b>는 검색 키워드입니다.
	 */
	@Builder
	@Getter
	public static class Search {
		private String mode;
		private String type;
		private Map<String, Object> menu;
	}




	public abstract static class Chart {	}

	@Builder
	@Getter
	// 가계부 차트(월)를 볼 때
	public static class MonthChart extends Chart {
		private String name;
		private Long price;
	}

	@Builder
	@Getter
	// 가계부 차트(주)를 볼 때
	public static class WeekChart extends Chart {
		private String week;
		private Long incomePrice;
		private Long outlayPrice;
	}


	@Builder
	@Getter
	// 가계부 차트(년)를 볼 때
	public static class YearChart extends Chart {
		private String month;
		private Long incomePrice;
		private Long outlayPrice;
	}

}
