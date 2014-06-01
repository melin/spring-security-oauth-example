package com.github.melin.rest;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class UserServiceClientUtil {

    public static final String SERVER_URL = "http://localhost:8090/SpringRest/api";
    public static final String APP_KEY = "00001";
    public static final String APP_SECRET = "abcdeabcdeabcdeabcdeabcde";
    
    public static String invokerApi(String result) throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
        HashMap map = objectMapper.readValue(result, HashMap.class);
        String accessCode = (String)map.get("access_token");
        System.out.println("accessCode = " + accessCode);
        
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("method", "user.get");
        form.add("version", "1.0");
        form.add("id", "1");
        form.add("access_token", accessCode);
        
        String rep = restTemplate.postForObject(SERVER_URL, form, String.class);
        System.out.println(rep);
        return rep;
    }
}