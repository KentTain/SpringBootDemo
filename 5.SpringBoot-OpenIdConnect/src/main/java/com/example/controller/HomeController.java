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

import com.example.annotation.PermissionAnnotation;
import com.example.util.IdSrvOidcUser;

@RequestMapping("/home")
@Controller
public class HomeController {

	protected Authentication authentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	protected IdSrvOidcUser userDetails() {
		if (authentication().getPrincipal() instanceof IdSrvOidcUser) {
			return (IdSrvOidcUser) authentication().getPrincipal();
		}

		return null;
	}

	@RequestMapping("/index")
	@ResponseBody
	public String home() {
		Authentication authentication = authentication();
		Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
		IdSrvOidcUser userDetails = userDetails();
		if (userDetails != null) {
			Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
			String username = userDetails.getName();

			StringBuilder sb = new StringBuilder();
			for (GrantedAuthority authority : auths) {
				sb.append(authority.getAuthority() + ", ");
			}
			return "Welcome, " + username + ", authorities: " + sb.toString();
		}

		return "Welcome, " + authentication.getName();
	}

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@RequestMapping("/userinfo")
	@PreAuthorize("hasAuthority('126AC4CF-84CF-410B-8989-A4EB8397EC3F')")
	@PermissionAnnotation(MenuName = "首页", PermissionName = "用户信息", Url = "/home/userinfo", IsPage = true, Order = 1, 
		AuthorityId = "126AC4CF-84CF-410B-8989-A4EB8397EC3F", DefaultRoleId = "126AC4CF-84CF-410B-8989-A4EB8397EC3F")
	@ResponseBody
	public String userinfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
		IdSrvOidcUser userDetails = (IdSrvOidcUser) authentication.getPrincipal();
		if (userDetails != null) {
			Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
			String username = userDetails.getName();
			String accessToken = userDetails.getIdToken().getTokenValue();

			StringBuilder sb = new StringBuilder();
			for (GrantedAuthority authority : auths) {
				sb.append(authority.getAuthority() + ", ");
			}
			return "Welcome, " + username + ", accessToken: " + accessToken + ", authorities: " + sb.toString();
		}

		return "Welcome, " + authentication.getName();
	}
}
