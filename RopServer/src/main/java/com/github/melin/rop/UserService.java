package com.github.melin.rop;

import org.springframework.security.core.context.SecurityContextHolder;

import com.rop.annotation.ServiceMethodBean;

//①标注Rop的注解，使UserService成为一个Rop的服务Bean
@ServiceMethodBean 
public class UserService implements UserServiceInterface {

	public Object getSession(LogonRequest request) {
		System.out.println("auth = " + SecurityContextHolder.getContext().getAuthentication());
		// 返回响应
		LogonResponse logonResponse = new LogonResponse();
		logonResponse.setSessionId("mockSessionId1");
		return logonResponse;
	}
}