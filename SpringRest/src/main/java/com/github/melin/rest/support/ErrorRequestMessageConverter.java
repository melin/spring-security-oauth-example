package com.github.melin.rest.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import com.github.melin.rest.support.error.MainError;

public class ErrorRequestMessageConverter {
	private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;
	
	/**
	 * 转换异常信息为format格式
	 * 
	 * @param httpServletResponse
	 * @param format
	 * @param mainError
	 * @throws IOException
	 */
	public void convertData(HttpServletRequest request, HttpServletResponse httpServletResponse,
			MainError mainError) throws IOException {
		final String format = (String) request.getAttribute("format");
		
		if("json".equals(format)) {
			jsonMessageConverter.write(mainError, MediaType.valueOf("application/json;charset=UTF-8"),
				new ServletServerHttpResponse(httpServletResponse));
		} else if("xml".equals(format)) {
			xmlMessageConverter.write(mainError, MediaType.valueOf("application/xml"), 
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
