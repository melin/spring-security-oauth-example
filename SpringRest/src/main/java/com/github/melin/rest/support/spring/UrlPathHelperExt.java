package com.github.melin.rest.support.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

/**
 * 
 * @author libinsong1204@gmail.com
 *
 */
public class UrlPathHelperExt extends UrlPathHelper {

	@Override
	public String getLookupPathForRequest(HttpServletRequest request) {
		return "/" + request.getParameter("method");
	}
}
