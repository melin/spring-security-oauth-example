package com.github.melin.rest.support;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;

import com.github.melin.rest.support.error.MainError;
import com.github.melin.rest.support.error.MainErrorType;
import com.github.melin.rest.support.error.MainErrors;

public class OpenServiceFilter implements Filter {
	
	private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		
		String localePart = request.getParameter("locale");
		Locale locale = StringUtils.parseLocaleString(localePart);
		String format = request.getParameter("format");
		
		MainError mainError = null;
        if (request.getParameter("method") == null) {
        	mainError = MainErrors.getError(MainErrorType.MISSING_METHOD, locale);
        }
        
        if(mainError != null) {
			convertData(response, httpServletResponse, format, mainError);
        } else {
        	chain.doFilter(request, response);
        }
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

	@Override
	public void destroy() {
		
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
