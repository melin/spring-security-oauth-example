package com.github.melin.rest.support.spring;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.github.melin.rest.support.error.MainError;
import com.github.melin.rest.support.error.SubError;
import com.github.melin.rest.support.error.SubErrorType;
import com.github.melin.rest.support.error.SubErrors;

public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
	
	private static final Map<String, SubErrorType> INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS = new LinkedHashMap<String, SubErrorType>();

    static {
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("typeMismatch", SubErrorType.ISV_PARAMETERS_MISMATCH);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("NotNull", SubErrorType.ISV_MISSING_PARAMETER);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("NotEmpty", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Size", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Range", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Pattern", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Min", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Max", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("DecimalMin", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("DecimalMax", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Digits", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Past", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("Future", SubErrorType.ISV_INVALID_PARAMETE);
        INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.put("AssertFalse", SubErrorType.ISV_INVALID_PARAMETE);
    }
    
    private MappingJackson2HttpMessageConverter jsonMessageConverter;
	private Jaxb2RootElementHttpMessageConverter xmlMessageConverter;
	
	public DefaultHandlerExceptionResolver() {
		setOrder(Ordered.LOWEST_PRECEDENCE);
	}
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		try {
			if (ex instanceof BindException) {
				handleBindException((BindException) ex, request, response, handler);
			}
		}
		catch (Exception handlerException) {
			//logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
		}
		return new ModelAndView();
	}
	
	protected void handleBindException(BindException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
 		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
 		
 		List<ObjectError> errorList = ex.getBindingResult().getAllErrors();

        //将Bean数据绑定时产生的错误转换为Rop的错误
        if (errorList != null && errorList.size() > 0) {
        	MainError mainError = toMainErrorOfSpringValidateErrors(errorList, (Locale)request.getAttribute("locale"));
        	
        	String format = (String) request.getAttribute("format");
        	convertData(response, format, mainError);
        }
	}
	
	/**
	 * 转换异常信息为format格式
	 * 
	 * @param httpServletResponse
	 * @param format
	 * @param mainError
	 * @throws IOException
	 */
	private void convertData(HttpServletResponse httpServletResponse, String format,
			MainError mainError) throws IOException {
		if("json".equals(format)) {
			jsonMessageConverter.write(mainError, MediaType.valueOf("application/json;charset=UTF-8"),
				new ServletServerHttpResponse(httpServletResponse));
		} else if("xml".equals(format)) {
			xmlMessageConverter.write(mainError, MediaType.valueOf("application/xml"), 
				new ServletServerHttpResponse(httpServletResponse));
		}
	}
	
	/**
     * 将通过JSR 303框架校验的错误转换为Rop的错误体系
     *
     * @param allErrors
     * @param locale
     * @return
     */
    private MainError toMainErrorOfSpringValidateErrors(List<ObjectError> allErrors, Locale locale) {
        if (hastSubErrorType(allErrors, SubErrorType.ISV_MISSING_PARAMETER)) {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_MISSING_PARAMETER);
        } else if (hastSubErrorType(allErrors, SubErrorType.ISV_PARAMETERS_MISMATCH)) {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_PARAMETERS_MISMATCH);
        } else {
            return getBusinessParameterMainError(allErrors, locale, SubErrorType.ISV_INVALID_PARAMETE);
        }
    }
    
    /**
     * 判断错误列表中是否包括指定的子错误
     *
     * @param allErrors
     * @param subErrorType1
     * @return
     */
    private boolean hastSubErrorType(List<ObjectError> allErrors, SubErrorType subErrorType1) {
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.containsKey(fieldError.getCode())) {
                    SubErrorType tempSubErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                    if (tempSubErrorType == subErrorType1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 生成对应子错误的错误类
     *
     * @param allErrors
     * @param locale
     * @param subErrorType
     * @return
     */
    private MainError getBusinessParameterMainError(List<ObjectError> allErrors, Locale locale, SubErrorType subErrorType) {
        MainError mainError = SubErrors.getMainError(subErrorType, locale);
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                SubErrorType tempSubErrorType = INVALIDE_CONSTRAINT_SUBERROR_MAPPINGS.get(fieldError.getCode());
                if (tempSubErrorType == subErrorType) {

                    String subErrorCode = SubErrors.getSubErrorCode(tempSubErrorType, fieldError.getField(),fieldError.getRejectedValue());

                    SubError subError = SubErrors.getSubError(subErrorCode, tempSubErrorType.value(), locale,
                            fieldError.getField(), fieldError.getRejectedValue());
                    mainError.addSubError(subError);
                }
            }
        }
        return mainError;
    }

	public MappingJackson2HttpMessageConverter getJsonMessageConverter() {
		return jsonMessageConverter;
	}

	public void setJsonMessageConverter(
			MappingJackson2HttpMessageConverter jsonMessageConverter) {
		this.jsonMessageConverter = jsonMessageConverter;
	}

	public Jaxb2RootElementHttpMessageConverter getXmlMessageConverter() {
		return xmlMessageConverter;
	}

	public void setXmlMessageConverter(
			Jaxb2RootElementHttpMessageConverter xmlMessageConverter) {
		this.xmlMessageConverter = xmlMessageConverter;
	}
}
