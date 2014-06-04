package com.github.melin.rest.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.github.melin.rest.support.error.MainErrors;
import com.github.melin.rest.support.error.SubErrors;

public class InitializingService implements InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializingService.class);
	
	private static final String I18N_ROP_ERROR = "META-INF/i18n/error";

	@Override
	public void afterPropertiesSet() throws Exception {
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(I18N_ROP_ERROR);
        LOGGER.info("加载资源文件：" + I18N_ROP_ERROR);
        
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
	}

}
