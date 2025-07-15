package com.areum.moneymanager.dto.request.main;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 회원의 가계부를 서버로 전송하는 데이터 객체
 */
public class BudgetBookRequestDTO {


	/**
	 * 가계부의 날짜를 설정할 때 필요한 객체입니다.<p>
	 * <b color='white'>date</b>는 가계부 날짜, <b color='white'>type</b>은 가계부 유형입니다.
	 */
	@Builder
	@Getter
	public static class Setting {
		private String date;
		private String type;
	}



	/**
	 * 가계부 등록할 때 필요한 객체입니다.<br>
	 * <b color='white'>date</b>는 가계부 날짜,
	 * <b color='white'>fix</b>는 고정/변동,
	 * <b color='white'>category</b>는 카테고리 코드
	 * <b color='white'>memo</b>는 내용,
	 * <b color='white'>price</b>는 금액,
	 * <b color='white'>paymentType</b>는 금액우형,
	 * <b color='white'>image</b>는 사진,
	 * <b color='white'>place</b>는 위치입니다.
	 */
	@Getter
	@Setter
	@Builder
	@ToString
	public static class Create {
		private String date;
		private Fix fix;
		private String category;
		private String memo;
		private Long price;
		private String paymentType;
		private List<MultipartFile> image;
		private Place place;
	}



	@Data
	// 가계부 수정할 때
	public static class Update {
		private String date;
		private Fix fix = new Fix();	//null 방지
		private String category;
		private String memo;
		private Long price;
		private String paymentType;
		private List<MultipartFile> image;
		private Place map = new Place();
	}



	@Builder
	@Getter
	public static class FileMeta {
		private MultipartFile file;
		private String imageName;
	}



	/**
	 * 가계부의 옵션을 설정할 때 필요한 객체입니다.<br>
	 *	<b color='white'>option</b>은 고정옵션이고, <b color='white'>cycle</b>은 고정주기입니다.
	 */
	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	public static class Fix {
		private String option;
		private String cycle;

		public Fix()  {
			this.option = "n";
			this.cycle = "";
		}
	}


	/**
	 * 가계부 장소를 설정할 때 필요한 객체입니다.<br>
	 * <b color='white'>name</b>은 장소명, <b color='white'>roadAddress</b>는 도로명주소, <b color='white'>address</b>는 지번주소입니다.
	 */
	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	public static class Place {
		private String name;
		private String roadAddress;
		private String address;

		public Place(){
			this.name = "";
			this.roadAddress = "";
			this.address = "";
		}
	}



	/**
	 * 가계부 검색기간 날짜를 설정할 때 필요한 객체입니다.<p>
	 * 해당 객체는 form으로 데이터를 전송할 때 사용합니다.<br>
	 * <b color='white'>year</b>는 년, <b color='white'>month</b>은 월, <b color='white'>week</b> 주입니다.
	 */
	@Getter
	@Builder
	public static class DateForm {
		private String year;
		private String month;
		private String week;
	}



	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class ChartJson {
		private String type;
		private String year;
		private String month;
		private String week;
	}




	/**
	 * 가계부 검색할 때 필요한 객체입니다.<br>
	 * <b color='white'>range</b>는 검색기간, <b color='white'>mode</b>은 검색유형, <b color='white'>keywords</b>검색어입니다.
	 */
	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class Search {
		private ChartJson range;
		private String mode;
		private List<String> keywords;
	}




	@Builder
	@Getter
	public static class Image {
		private Long id;
		private String memberId;
		private String date;
		private String originalImage;
		private MultipartFile changeImage;
	}

}
