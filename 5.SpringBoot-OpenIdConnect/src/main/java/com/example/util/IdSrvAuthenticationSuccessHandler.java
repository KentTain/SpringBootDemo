package com.example.util;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sun.mail.handlers.message_rfc822;

@Component
public class IdSrvAuthenticationSuccessHandler  implements AuthenticationSuccessHandler {
	private Logger logger = LoggerFactory.getLogger(IdSrvAuthenticationSuccessHandler.class);
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest arg0, HttpServletResponse arg1, Authentication authentication)
			throws IOException, ServletException {
		Object  obj = authentication.getPrincipal();
		if(OidcUser.class.isInstance(obj))
		{
			OidcUser user = (OidcUser) obj; 
			String account = user.getName(); 
			System.out.println("----IdSvrAuthenticationSuccessHandler onAuthenticationSuccess user: " + account);
		}
	}
 
}
