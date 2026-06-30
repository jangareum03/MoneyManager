package com.moneymanager.exception.log;

import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.error<br>
 * 파일이름       : DeveloperLogInfo<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 26<br>
 * 설명              : 개발 로그 정보를 위한 데이터 클래스
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
 * 		 	  <td>26. 6. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@Builder(toBuilder = true)
public class DeveloperLogInfo {

	private String work;						//기능
	private String cause;						//실패원인
	private Class<?> className;			//클래스명
	private String method;					//메서드명
	private Class<?> target;					//처리중인 대상
	private String field;						//필드
	private String value;						//값

	@Builder.Default
	private Map<String, Object> options = new LinkedHashMap<>();

	public DeveloperLogInfo addOption(String key, Object object) {
		options.put(key, value);

		return this;
	}

}
