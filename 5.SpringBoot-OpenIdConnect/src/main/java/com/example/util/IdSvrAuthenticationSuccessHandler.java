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
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sun.mail.handlers.message_rfc822;

@Component
public class IdSvrAuthenticationSuccessHandler  implements AuthenticationSuccessHandler {
	private Logger logger = LoggerFactory.getLogger(IdSvrAuthenticationSuccessHandler.class);
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest arg0, HttpServletResponse arg1, Authentication authentication)
			throws IOException, ServletException {
		
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		StringBuilder sbBuilder = new StringBuilder();
		authorities.forEach(authority -> {
			sbBuilder.append(authority.getAuthority() + ", ");
			if(authority.getAuthority().equals("ROLE_USER")) {
				try {
					redirectStrategy.sendRedirect(arg0, arg1, "/user");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(authority.getAuthority().equals("ROLE_ADMIN")) {
				try {
					redirectStrategy.sendRedirect(arg0, arg1, "/admin");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
	            throw new IllegalStateException();
	        }
		});
		System.out.println("----IdSvrAuthenticationSuccessHandler onAuthenticationSuccess: " + sbBuilder.toString());
	}
 
}
