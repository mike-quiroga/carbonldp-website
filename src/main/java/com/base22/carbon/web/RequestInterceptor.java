package com.base22.carbon.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.base22.carbon.HTTPHeaders;
import com.base22.carbon.HTTPHeaders.CORSHeader;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.utils.HTTPUtil;

public class RequestInterceptor implements HandlerInterceptor {

	@Autowired
	private RepositoryService repositoryService;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< afterCompletion > Response info: {}", HTTPUtil.printResponseInfo(response));
		}
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// repositoryService.release();
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		for (CORSHeader header : CORSHeader.values()) {
			response.addHeader(HTTPHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, header.getKey());
		}
		return true;
	}
}
