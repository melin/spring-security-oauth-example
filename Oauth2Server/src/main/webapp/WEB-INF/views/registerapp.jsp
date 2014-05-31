<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
	<title>Register Client</title>
	<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/default.css"/>" />
</head>
<body>
	<h1>注册引用哦</h1>
	<form id="registerForm" name="registerForm" action='registerApp.do' method="post">
		<p>
			<label>应用名称:<input type='text' name='appName' /></label>
		</p>
		<p>
			<label>应用站点:<input type="text" name='appUrl' ></label>
		</p>
		<p>
			<input name="login" value="提交" type="submit">
		</p>
	</form>
</body>
</html>