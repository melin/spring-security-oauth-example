package com.github.melin.rest.support.spring;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.github.melin.rest.support.CheckOpenServiceFilter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * 通过反射从RequestMappingHandlerMapping中获取RequestMappingInfo信息。
 * 服务请求时，通过RequestMappingInfo信息验证method和version是否正确。
 * 
 * @author Windows User
 *
 */
public class RequestMappingHandlerMappingResolver implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMappingHandlerMappingResolver.class);
	
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	
	private Multimap<String, String> methodVersionMap = ArrayListMultimap.create(); 
	
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
		
		CheckOpenServiceFilter.setMethodVersionMap(methodVersionMap);
	}
}
