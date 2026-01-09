package com.moneymanager.controller.web.map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 패키지이름    : com.moneymanager.controller.web.map<br>
 * 파일이름       : MapController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10<br>
 * 설명              : 지도 관련 요청을 처리하는 클래스
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
@Controller
@RequestMapping("/map")
public class MapController {

	/**
	 * 카카오에서 제공하는 지도 페이지를 반환합니다.
	 *
	 * @return	카카오 지도를 표시하는 화면의 경로
	 */
	@GetMapping("/kakao")
	public String kakaoMap() {
		return "/main/kakaoMap";
	}

}
