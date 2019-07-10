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

import com.example.util.IdSrvOidcUser;

@RequestMapping("/home")
@Controller
public class HomeController {
	
	protected Authentication authentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	protected IdSrvOidcUser userDetails () { 
		return (IdSrvOidcUser) authentication().getPrincipal(); 
	}
	
	@RequestMapping("/index")
    @ResponseBody
    public String home() {
		Authentication authentication = authentication();
		Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
		IdSrvOidcUser  userDetails = userDetails();
        
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String username = userDetails.getName();
        
        StringBuilder sb = new StringBuilder();
        for(GrantedAuthority authority : auths)
        {
        	sb.append(authority.getAuthority() + ", ");
        }
        return "Welcome, " + username + ", authorities: " + sb.toString();
    }
	
	@Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @RequestMapping("/userinfo")
    @PreAuthorize("hasAuthority('缓存管理-删除单个缓存')")
    @ResponseBody
    public String userinfo() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
		IdSrvOidcUser  userDetails = (IdSrvOidcUser) authentication.getPrincipal();
        
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String username = userDetails.getName();
        String accessToken = userDetails.getIdToken().getTokenValue();
        
        StringBuilder sb = new StringBuilder();
        for(GrantedAuthority authority : auths)
        {
        	sb.append(authority.getAuthority() + ", ");
        }
        return "Welcome, " + username + ", accessToken: " + accessToken + ", authorities: " + sb.toString();
    }
}
