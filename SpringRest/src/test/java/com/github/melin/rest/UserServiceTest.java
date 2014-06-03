package com.github.melin.rest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.github.melin.rest.support.spring.NoServiceMethodExceptionResolver;

public class UserServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceTest.class);
	
	public static final String SERVER_URL = "http://localhost:8090/SpringRest/api";

	@Test
	public void test0() {
		RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("method", "user.get");
        form.add("version", "1.0");
        form.add("id", "1");
        form.add("locale", "zh_CN");
        form.add("format", "xml");
        
        String rep = restTemplate.postForObject(SERVER_URL, form, String.class);
        LOGGER.info(rep);
	}
	
	@Test
	public void test1() {
		RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("method", "user.get1");
        form.add("version", "1.0");
        form.add("id", "1");
        form.add("locale", "zh_CN");
        form.add("format", "xml");
        
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
			
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
			
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				System.out.println(IOUtils.toString(response.getBody()));
			}
		});
        String rep = restTemplate.postForObject(SERVER_URL, form, String.class);
        LOGGER.info(rep);
	}
}
