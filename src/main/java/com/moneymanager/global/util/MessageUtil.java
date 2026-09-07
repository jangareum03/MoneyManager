package com.moneymanager.global.util;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.util.string.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.util<br>
 * 파일이름       : MessageUtil<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 7<br>
 * 설명              : message.prepreties 키값을 읽어오는 클래스
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
 * 		 	  <td>26. 9. 7</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(
                code,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    public String get(ApplicationException e) {
        if(StringUtil.isNullOrBlank(e.getUserMessage())) {
            return null;
        }

        return get(e.getUserMessage());
    }

}