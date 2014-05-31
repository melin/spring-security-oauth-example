package com.github.melin.rop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.rop.client.CompositeResponse;
import com.rop.client.DefaultRopClient;
import com.rop.utils.RopUtils;

public class UserServiceClient {

    public static final String SERVER_URL = "http://localhost:8090/RopServer/api";
    public static final String APP_KEY = "00001";
    public static final String APP_SECRET = "abcdeabcdeabcdeabcdeabcde";
    private DefaultRopClient ropClient = new DefaultRopClient(SERVER_URL, APP_KEY, APP_SECRET);
    
    //@Test
    public void createSession() {
        LogonRequest ropRequest = new LogonRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setPassword("123456");
        CompositeResponse response = ropClient.buildClientRequest().get(ropRequest, LogonResponse.class, "user.getSession", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getSuccessResponse());
        assertTrue(response.getSuccessResponse() instanceof LogonResponse);
        assertEquals(((LogonResponse) response.getSuccessResponse()).getSessionId(), "mockSessionId1");
        ropClient.setSessionId(((LogonResponse) response.getSuccessResponse()).getSessionId());
    }
    
    @Test
    public void createSession1() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("method", "user.getSession");
        form.add("appKey", "00001");
        form.add("v", "1.0");
        form.add("userName", "tomson");
        form.add("locale", "en");
        form.add("access_token", "3ab2c4c3-51dd-4810-aeb0-df4e71211877");
        
        //对请求参数列表进行签名
        String sign = RopUtils.sign(form.toSingleValueMap(), "abcdeabcdeabcdeabcdeabcde");
        form.add("sign", sign);

        String response = restTemplate.postForObject(SERVER_URL, form, String.class);
        System.out.println("response:\n" + response);
    }
}