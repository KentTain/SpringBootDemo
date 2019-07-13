package com.example.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 登陆成功监听
 * 
 * @author
 */
@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
		Object obj = authenticationSuccessEvent.getAuthentication().getPrincipal();
		if (OidcUser.class.isInstance(obj)) {
			OidcUser user = (OidcUser) obj;
			String account = user.getName();
			System.out.println("----AuthenticationSuccessEventListener onApplicationEvent with user: " + account);
		}

	}
}
