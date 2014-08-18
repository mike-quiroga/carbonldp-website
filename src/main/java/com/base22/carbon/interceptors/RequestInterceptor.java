package com.base22.carbon.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.base22.carbon.services.RepositoryService;
import com.base22.carbon.utils.HttpUtil;

public class RequestInterceptor implements HandlerInterceptor {

	@Autowired
	private RepositoryService repositoryService;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
		if ( LOG.isDebugEnabled() ) {
			String requestURL = HttpUtil.getRequestURL(request);
			// TODO: Create more accurate function for this
			if ( ! requestURL.matches(".+/static/.+") ) {
				LOG.debug("<< afterCompletion > Response info: {}", HttpUtil.printResponseInfo(response));
			}
		}
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		repositoryService.release();
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}
}
