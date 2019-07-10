package com.example.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.util.IdSvrOidcUser;

@RequestMapping("/home")
@Controller
public class HomeController {
	@RequestMapping("/index")
    @ResponseBody
    public String home() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
		IdSvrOidcUser  userDetails = (IdSvrOidcUser) authentication.getPrincipal();
        
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String username = userDetails.getName();
        
        StringBuilder sb = new StringBuilder();
        for(GrantedAuthority authority : authorities)
        {
        	sb.append(authority.getAuthority() + ", ");
        }
        return "Welcome, " + username + ", authorities: " + sb.toString();
    }
	
	@Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping("/userinfo")
    @PreAuthorize("hasRole('缓存管理-数据库池管理')") //有ROLE_USER权限的用户可以访问
    @ResponseBody
    public String userinfo(OAuth2AuthenticationToken authentication) {
        // authentication.getAuthorizedClientRegistrationId() returns the
        // registrationId of the Client that was authorized during the oauth2Login() flow
        OAuth2AuthorizedClient authorizedClient =
            this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        return accessToken.getTokenValue();
    }
}
