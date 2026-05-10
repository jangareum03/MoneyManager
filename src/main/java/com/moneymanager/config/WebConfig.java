package com.moneymanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.config<br>
 *  * 파일이름       : WebConfig<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 12. 26<br>
 *  * 설명              : 웹 설정과 관련된 작업하는 클래스
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
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.image.ledger.resource-path}")
    private String ledgerResourcePath;
    @Value("${image.profile.resourcePath}")
    private String profileResourcePath;
    @Value("${image.profile.connectPath}")
    private String profileConnectPath;

    @Override
    public void addResourceHandlers( ResourceHandlerRegistry registry ) {
        registry.addResourceHandler("/uploads/ledger/**").addResourceLocations(ledgerResourcePath);
        registry.addResourceHandler(profileConnectPath).addResourceLocations(profileResourcePath);
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
    }

}
