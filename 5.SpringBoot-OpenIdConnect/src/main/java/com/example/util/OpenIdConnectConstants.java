package com.example.util;

public class OpenIdConnectConstants {
	public final static String ClientCookieName = "sso.idrserver";
    public final static String AuthScheme = "Cookies";
    //public final static String AuthScheme = "sso.oauth.cookies";
    public final static String ChallengeScheme = "oidc";
    public final static String ApiAuthScheme = "Bearer";
    public final static String ClientAuthScheme = "idsrv";

    public final static String CallbackPath = "/oidc/signin-callback";
    public final static String SignOutPath = "/oidc/signout-callback";

    public final static String ClaimTypes_TenantName = "tenantname";
    public final static String ClaimTypes_TargetTenantName = "targettenantname";

    public final static String ClaimTypes_Id = "sub";
    public final static String ClaimTypes_Name = "name";
    public final static String ClaimTypes_Email = "email";
    public final static String ClaimTypes_Phone = "phone";
    public final static String ClaimTypes_RoleId = "roleid";
    public final static String ClaimTypes_DisplayName = "disiplayname";
}
