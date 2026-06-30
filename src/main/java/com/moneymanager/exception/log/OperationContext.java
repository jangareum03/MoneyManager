package com.moneymanager.exception.log;

import com.moneymanager.exception.ServiceAction;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.log<br>
 * 파일이름       : OperationContext<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 1<br>
 * 설명              : 운영로그 정보를 위한 데이터 클래스
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
 * 		 	  <td>26. 7. 1</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
@Builder
public class OperationContext {

	private ServiceAction action;					//요청 기능
	private Class<?> resource;							//요청정보가 담긴 클래스

	@Builder.Default
	private Map<String, Object> options = new LinkedHashMap<>();		//옵션

	public void addOption(String key, Object value) {
		options.put(key, value);
	}

}
