<%@page import="com.github.melin.rop.UserServiceClientUtil"%>
<%@page import="org.springframework.util.LinkedMultiValueMap"%>
<%@page import="org.springframework.util.MultiValueMap"%>
<%@page import="org.springframework.web.client.RestTemplate"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
	<title>Spring Oauth2实例</title>
	<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/default.css"/>" />
</head>
<body>

	<h1>Spring Oauth2实例</h1>
	
	
	<%
		String code = request.getParameter("code");
		if(StringUtils.isNotEmpty(code)) {
			if(session.getAttribute("result") == null) {
				String url = "http://localhost:8080/Oauth2Server/oauth/token";
				
				RestTemplate restTemplate = new RestTemplate();
		        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		        form.add("client_id", "af4sdop16");
		        form.add("client_secret", "12345678");
		        form.add("redirect_uri", "http://localhost:8090/RopServer/");
		        form.add("grant_type", "authorization_code");
		        form.add("code", code);
	
		        String result = restTemplate.postForObject(url, form, String.class);
		        out.print(result);
		        
		        String repo = UserServiceClientUtil.invokerApi(result);
		        out.print("</br></br>调用服务接口结果：http://localhost:8090/RopServer/api, method = user.getSession </br><textarea rows='5' cols='100'>" + repo + "</textarea>");
		       	
		        session.setAttribute("result", result);
			} else {
				out.print(session.getAttribute("result"));
				
				String repo = UserServiceClientUtil.invokerApi((String)session.getAttribute("result"));
				out.print("</br></br>调用服务接口结果：http://localhost:8090/RopServer/api, method = user.getSession </br><textarea rows='5' cols='100'>" + repo + "</textarea>");
			}
		} else {
	%>
		<a href="http://localhost:8080/Oauth2Server/oauth/authorize?client_id=af4sdop16&response_type=code&scope=read&state=123456&redirect_uri=http://localhost:8090/RopServer/">获取authcode</a>
	<%}%>
</body>
</html>
