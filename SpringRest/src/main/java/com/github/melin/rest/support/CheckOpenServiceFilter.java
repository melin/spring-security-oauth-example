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
import com.google.common.collect.Multimap;

/**
 * 检测请求服务参数是否正确，测试：format、method、version、access_token
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class CheckOpenServiceFilter implements Filter {
	
	private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;
	
	private static Multimap<String, String> methodVersionMap;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		
		Locale locale = getLocale(request);
		String format = request.getParameter("format");
		if(!StringUtils.hasText(format)) {
			format = "xml";
		}
		
		MainError mainError = null;
		String method = request.getParameter("method");
		method = "/" + method.trim();
        if (method.equals("/")) {
        	//缺少method参数
        	mainError = MainErrors.getError(MainErrorType.MISSING_METHOD, locale);
        } else {
        	if(methodVersionMap.containsKey(method)) {
        		String version = request.getParameter("version");
        		
        		if(!StringUtils.hasText(version)) {
        			//版本号为空
        			mainError = MainErrors.getError(MainErrorType.MISSING_VERSION, locale);
        		} else {
	        		if(!methodVersionMap.get(method).contains(version)) {
	        			//对应版本号不存在
	        			mainError = MainErrors.getError(MainErrorType.UNSUPPORTED_VERSION, locale);
	        		}
        		}
        	} else {
        		//method不存在
        		mainError = MainErrors.getError(MainErrorType.INVALID_METHOD, locale);
        	}
        }
        
        if(mainError == null) {
        	String accessToken = request.getParameter("token");
        	if(!StringUtils.hasText(accessToken)) {
        		mainError = MainErrors.getError(MainErrorType.MISSING_ACCESS_TOKEN, locale);
        	}
        }
        
        if(mainError != null) {
        	httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			convertData(response, httpServletResponse, format, mainError);
        } else {
        	chain.doFilter(request, response);
        }
	}
	
	public static void setMethodVersionMap(Multimap<String, String> methodVersionMap) {
		CheckOpenServiceFilter.methodVersionMap = methodVersionMap;
	}

	private Locale getLocale(ServletRequest request) {
		String localePart = request.getParameter("locale");
		if(!StringUtils.hasText(localePart))
			localePart = "zh_CN";
		
		Locale locale = StringUtils.parseLocaleString(localePart);
		return locale;
	}
	
	private void convertData(ServletResponse response,
			HttpServletResponse httpServletResponse, String format,
			MainError mainError) throws IOException {
		if("json".equals(format)) {
			jsonMessageConverter.write(mainError, MediaType.valueOf("application/json;charset=UTF-8"),
				new ServletServerHttpResponse((HttpServletResponse)response));
		} else if("xml".equals(format)) {
			xmlMessageConverter.write(mainError, MediaType.valueOf("application/xml"), 
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
