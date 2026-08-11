package com.moneymanager.global.log;

import com.moneymanager.global.exception.code.CommonErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.error<br>
 * 파일이름       : LogContent<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 26<br>
 * 설명              : 로그 정보를 위한 데이터 클래스
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
public class LogContent {

	private String work;							//기능
	private String cause;							//실패원인
	private Class<?> target;						//처리중인 대상
	private String field;							//필드
	private String value;							//값

	@Builder.Default
	private Map<String, Object> options = new LinkedHashMap<>();


	public static LogContent of(String work, String cause, Object... value) {
		return LogContent.builder()
				.work(work)
				.cause(cause)
				.value(valueOf(value))
				.build();
	}

	public static LogContent ofTarget(String work, String cause, Class<?> target, Object... value) {
		return LogContent.builder()
				.work(work)
				.cause(cause)
				.target(target)
				.value(toValue(value))
				.build();
	}
	
	public static LogContent ofField(String work, String cause, String field, Object value) {
		return LogContent.builder()
				.work(work)
				.cause(cause)
				.field(field)
				.value(String.valueOf(value))
				.build();
	}

	public LogContent addOption(String key, Object value) {
		Map<String, Object> newOptions = new LinkedHashMap<>(options);
		newOptions.put(key, value);

		return this.toBuilder()
				.options(newOptions)
				.build();
	}


	//===== 보조 메서드 =====
	private static String toValue(Object[] values) {
		if(values.length == 1) {
			return String.valueOf(values[0]);
		}

		return valueOf(values);
	}

	private static String valueOf(Object... values) {
		if(values.length %2 != 0) {
			throw InternalException.of(
					CommonErrorCode.INVALID_REQUEST,
					LogContent.ofTarget(
							"로그 값 생성",
							"key-value 형식 불일치",
							LogContent.class,
							"value",
							Arrays.toString(values)
					)
			);
		}

		StringJoiner joiner = new StringJoiner(", ", "{", "}");

		for(int i=0; i<values.length; i+=2) {
			joiner.add(values[i] + ": " + values[i+1]);
		}

		return joiner.toString();
	}

}