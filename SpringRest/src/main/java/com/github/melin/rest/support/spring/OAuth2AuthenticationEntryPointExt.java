package com.github.melin.rest.support.spring;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

import com.github.melin.rest.support.ErrorRequestMessageConverter;
import com.github.melin.rest.support.error.MainError;
import com.github.melin.rest.support.error.MainErrorType;
import com.github.melin.rest.support.error.MainErrors;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class OAuth2AuthenticationEntryPointExt extends OAuth2AuthenticationEntryPoint {
	private ErrorRequestMessageConverter messageConverter;

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		if(authException.getCause() instanceof InvalidTokenException) {
			MainError mainError = MainErrors.getError(MainErrorType.INVALID_ACCESS_TOKEN, (Locale)request.getAttribute("locale"));
			messageConverter.convertData(request, response, mainError);
		} else {
			doHandle(request, response, authException);
		}
	}

	public ErrorRequestMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(ErrorRequestMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
}
