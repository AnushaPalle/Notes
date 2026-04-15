package com.course.apimanagement.gamma.config;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

//@Component
public class WhitelistIPFilter extends GenericFilterBean {

	@Value("${security.whitelist-ip}")
	private String[] authorizedIp;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (!ArrayUtils.contains(authorizedIp, request.getRemoteAddr())) {
			httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
			httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			PrintWriter writer = response.getWriter();
			writer.print("{\"message\":\"Your IP is forbidden " + request.getRemoteAddr() + "\"}");
			return;
		}
		chain.doFilter(request, response);
	}

}
