package com.example.util;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

public class IdSvrAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
	private Logger logger = LoggerFactory.getLogger(IdSvrAuthorizationRequestResolver.class);
	private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

    public static final String RegistrationId_IdentityServer4 = "idsvr";
    public IdSvrAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

    	this.defaultAuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    	//尝试使用DefaultOAuth2AuthorizationRequestResolver解析OAuth2AuthorizationRequest
        OAuth2AuthorizationRequest authorizationRequest =
                this.defaultAuthorizationRequestResolver.resolve(request);  
        //如果解析了OAuth2AuthorizationRequest而不是返回自定义版本，则返回null
        return authorizationRequest != null ?   
                customAuthorizationRequest(authorizationRequest) :
                null;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request, String clientRegistrationId) {
    	//尝试使用DefaultOAuth2AuthorizationRequestResolver解析OAuth2AuthorizationRequest
        OAuth2AuthorizationRequest authorizationRequest =
                this.defaultAuthorizationRequestResolver.resolve(
                    request, clientRegistrationId);    
        //如果解析了OAuth2AuthorizationRequest而不是返回自定义版本，则返回null
        return authorizationRequest != null ?   
                customAuthorizationRequest(authorizationRequest) :
                null;
    }

    private OAuth2AuthorizationRequest customAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest) {

    	String registrationId = this.resolveRegistrationId(authorizationRequest);
    	//将自定义参数添加到现有OAuth2AuthorizationRequest.additionalParameters
        Map<String, Object> additionalParameters = new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());
        if (registrationId == RegistrationId_IdentityServer4) {
        	additionalParameters.put("acr_values", "idp%3Aidsvr");
        	additionalParameters.put("acr_values", "tenant%3AcDba");
        	additionalParameters.put("tenantName", "cDba");
            //additionalParameters.put("nonce", "636979019622168388.N2U0MDI5N2UtY2IyYi00OTJjLThiYmItYTQwZjBhYTdjMGRiNDQ5NTk3MmQtNzQyYi00ZTc5LThhYzMtY2JlNzI4M2MwZWZh");
        }
        System.out.println("----IdSvrAuthorizationRequestResolver customAuthorizationRequest: " + registrationId);
        
        //创建默认OAuth2AuthorizationRequest的副本，该副本返回OAuth2AuthorizationRequest.Builder以进行进一步修改
        return OAuth2AuthorizationRequest.from(authorizationRequest)
        		//覆盖默认的additionalParameters
                .additionalParameters(additionalParameters) 
                .build();
    }
    
    private String resolveRegistrationId(OAuth2AuthorizationRequest authorizationRequest) {
        return (String) authorizationRequest.getAdditionalParameters().get(OAuth2ParameterNames.REGISTRATION_ID);
    }
}

