package com.example.util;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.ApplicationListener; 
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component; 
import java.util.Map; 
/** 
 * 登陆失败监听 
 * 
 * @author 
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> { 
  @Override
  public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) { 
	  Object obj = authenticationFailureBadCredentialsEvent.getAuthentication().getPrincipal();
		if (OidcUser.class.isInstance(obj)) {
			OidcUser user = (OidcUser) obj;
			String account = user.getName();
			System.out.println("----AuthenticationFailureListener onApplicationEvent with user: " + account);
		}

  } 
}
