package com.github.melin.rest.support;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.github.melin.rest.support.error.MainError;
import com.github.melin.rest.support.error.MainErrorType;
import com.github.melin.rest.support.error.MainErrors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * 检测请求服务参数是否正确，测试：format、method、version、access_token
 * 
 * 通过反射从RequestMappingHandlerMapping中获取RequestMappingInfo信息。
 * 服务请求时，通过RequestMappingInfo信息验证method和version是否正确。
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class CheckOpenServiceFilter implements Filter, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckOpenServiceFilter.class);
	
	private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;
	private RequestMappingHandlerMapping handlerMapping;
	
	private Multimap<String, String> methodVersionMap = ArrayListMultimap.create(); 

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("通过反射从RequestMappingHandlerMapping中获取RequestMappingInfo信息");
		
		Field urlMapField = AbstractHandlerMethodMapping.class.getDeclaredField("urlMap");
		urlMapField.setAccessible(true);
		LinkedMultiValueMap<String, RequestMappingInfo> urlMap = 
				(LinkedMultiValueMap<String, RequestMappingInfo>) urlMapField.get(handlerMapping);
		
		for(Entry<String, List<RequestMappingInfo>> entry : urlMap.entrySet()) {
			List<RequestMappingInfo> mapping = entry.getValue();
			for(RequestMappingInfo mappingInfo : mapping) {
				LinkedHashSet<NameValueExpression<String>> params = 
						(LinkedHashSet<NameValueExpression<String>>) mappingInfo.getParamsCondition().getExpressions();
				
				for(NameValueExpression<String> exp : params) {
					if(exp.getName().equals("version")) {
						methodVersionMap.put(entry.getKey(), exp.getValue());
						break;
					}
				}
			}
		}
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
		
		request.setAttribute("locale", locale);
		request.setAttribute("format", format);
		
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
			convertData(httpServletResponse, format, mainError);
        } else {
        	chain.doFilter(request, response);
        }
	}
	
	private Locale getLocale(ServletRequest request) {
		String localePart = request.getParameter("locale");
		if(!StringUtils.hasText(localePart))
			localePart = "zh_CN";
		
		Locale locale = StringUtils.parseLocaleString(localePart);
		return locale;
	}
	
	/**
	 * 转换异常信息为format格式
	 * 
	 * @param httpServletResponse
	 * @param format
	 * @param mainError
	 * @throws IOException
	 */
	private void convertData(HttpServletResponse httpServletResponse, String format,
			MainError mainError) throws IOException {
		if("json".equals(format)) {
			jsonMessageConverter.write(mainError, MediaType.valueOf("application/json;charset=UTF-8"),
				new ServletServerHttpResponse(httpServletResponse));
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

	public RequestMappingHandlerMapping getHandlerMapping() {
		return handlerMapping;
	}

	public void setHandlerMapping(RequestMappingHandlerMapping handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

}
