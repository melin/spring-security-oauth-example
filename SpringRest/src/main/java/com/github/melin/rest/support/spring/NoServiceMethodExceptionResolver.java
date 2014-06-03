package com.github.melin.rest.support.spring;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.github.melin.rest.support.error.MainError;
import com.github.melin.rest.support.error.MainErrorType;
import com.github.melin.rest.support.error.MainErrors;

public class NoServiceMethodExceptionResolver extends
		AbstractHandlerExceptionResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(NoServiceMethodExceptionResolver.class);
	
	private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		String localePart = request.getParameter("locale");
		Locale locale = StringUtils.parseLocaleString(localePart);
		String format = request.getParameter("format");
		
		if(ex instanceof NoHandlerFoundException) {
			MainError mainError = MainErrors.getError(MainErrorType.INVALID_METHOD, locale);
    		try {
				convertData(response, response, format, mainError);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
    		return new ModelAndView();
    	}
		return null;
	}

	private void convertData(ServletResponse response,
			HttpServletResponse httpServletResponse, String format,
			MainError mainError) throws IOException {
		if("json".equals(format)) {
			jsonMessageConverter.write(mainError, MediaType.valueOf("application/json;charset=UTF-8"),
				new ServletServerHttpResponse((HttpServletResponse)response));
		} else if("xml".equals(format)) {
			xmlMessageConverter.write(mainError, MediaType.valueOf("application/xml;charset=GBK"), 
				new ServletServerHttpResponse(httpServletResponse));
		}
	}

	public MappingJackson2HttpMessageConverter getJsonMessageConverter() {
		return jsonMessageConverter;
	}

	public void setJsonMessageConverter(
			MappingJackson2HttpMessageConverter jsonMessageConverter) {
		this.jsonMessageConverter = jsonMessageConverter;
	}

	public Jaxb2RootElementHttpMessageConverter getXmlMessageConverter() {
		return xmlMessageConverter;
	}

	public void setXmlMessageConverter(
			Jaxb2RootElementHttpMessageConverter xmlMessageConverter) {
		this.xmlMessageConverter = xmlMessageConverter;
	}
}
