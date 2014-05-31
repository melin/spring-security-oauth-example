<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page isELIgnored="false"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
	<title>Spring Oauth2实例</title>
	<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/default.css"/>" />
</head>
<body>

	<h1>Spring Oauth2实例</h1>

	<h1>
		<authz:authorize ifNotGranted="ROLE_USER">
			<div style="text-align: center">
				<a href='<c:url value="/login"/>'>
					登录
				</a>
			</div>
		</authz:authorize>
		<authz:authorize ifAllGranted="ROLE_USER">
			<div style="text-align: center">
				<a href='<c:url value="/registerapp"/>'>注册应用</a>
				&nbsp;&nbsp;&nbsp;
				<a href='<c:url value="/logout.do"/>'>退出</a>
			</div>
		</authz:authorize>
	</h1>

	<div id="footer">
		Sample application for 
		<a href="http://github.com/SpringSource/spring-security-oauth" target="_blank">
			Spring Security OAuth
		</a>
	</div>


</body>
</html>
