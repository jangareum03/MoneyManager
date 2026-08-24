package com.moneymanager.global.exception.advice;

import com.moneymanager.global.exception.annotation.WebController;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.log.DevLogger;
import com.moneymanager.global.operation.OperationContext;
import com.moneymanager.global.operation.holder.OperationContextHolder;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.advice<br>
 * 파일이름       : GlobalControllerAdvice<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 12<br>
 * 설명              : 각 컨트롤러에서 처리하는 로직을 보조하는 클래스
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
 * 		 	  <td>26. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ControllerAdvice(annotations = WebController.class)
public class GlobalControllerAdvice {

	@ModelAttribute("sidebarUser")
	public SideBarUser currentUser(@AuthenticationPrincipal CustomUserDetails user) {
		if(user == null) {
			return null;
		}

		return new SideBarUser(
				user.getNickname(),
				user.getProfile()
		);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public String handleMissingParameters(Model model) {
		model.addAttribute("message", "필수값이 없습니다.");

		return "error/400";
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public String handleNotSupported(Model model) {
		model.addAttribute("message", "잘못된 요청입니다.");

		return "error/400";
	}

	@ExceptionHandler(ApplicationException.class)
	public RedirectView handleValidationException(ApplicationException e, HttpServletRequest request) {
		OperationContext context = OperationContextHolder.get();

		try{
			context.addOption("error", e.getErrorCode().getCode());
			context.addOption("log", e.getLogContent());
			context.addOption("message", e.getUserMessage());

			DevLogger.debug(context);

			if(context.getAction().getPath() == null || StringUtil.isNullOrBlank(context.getAction().getPath())) {
				String referer = request.getHeader("referer");

				if(isValidInternalUrl(referer)) {
					return new RedirectView(referer);
				}
			}

			return new RedirectView(context.getAction().getPath());
		}finally {
			AuditLogger.info(context);

			OperationContextHolder.clear();
		}
	}


	private boolean isValidInternalUrl(String url) {
		return url != null
				&& url.startsWith("/")
				&& !url.startsWith("//");
	}

}