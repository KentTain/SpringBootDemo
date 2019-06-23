package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GlobalConfig {

    public static String ApplicationId ;
    public static UUID ApplicationUUID ;
    public static String EncryptKey ;
    public static String BlobStorage ;
    public static String AdminEmails ;
    public static String TempFilePath ;

    public static String DatabaseConnectionString ;
    public static String MySqlConnectionString ;
    public static String StorageConnectionString ;
    public static String QueueConnectionString ;
    public static String NoSqlConnectionString ;
    public static String RedisConnectionString ;
    public static String ServiceBusConnectionString ;

    public static String ClientId ;
    public static String ClientSecret ;

    public static String WeixinAppKey ;
    public static String WeixinAppSecret ;
    public static String WeixinAppToken ;

    public static String ZZZLicenseName ;
    public static String ZZZLicenseKey ;


    /// <summary>
    /// subdomain的sso地址：http://sso.kcloudy.com/
    ///     本地测试接口地址：http://localhost:1001/
    /// </summary>
    public static String SSOWebDomain ;
    /// <summary>
    /// subdomain的admin地址：http://admin.kcloudy.com/
    ///     本地测试接口地址：http://localhost:1003
    /// </summary>
    public static String AdminWebDomain ;
    
    /// <summary>
    /// subdomain的config地址：http://subdomain.cfg.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:1101/
    /// </summary>
    public static String CfgWebDomain ;
    /// <summary>
    /// subdomain的dictionary地址：http://subdomain.dic.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:1103/
    /// </summary>
    public static String DicWebDomain ;
    /// <summary>
    /// subdomain的app地址：http://subdomain.app.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:1105
    /// </summary>
    public static String AppWebDomain ;

    /// <summary>
    /// subdomain的account地址：http://subdomain.acc.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:2001/
    /// </summary>
    public static String AccWebDomain ;

    /// <summary>
    /// subdomain的crm地址：http://subdomain.crm.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:3001/
    /// </summary>
    public static String CRMWebDomain ;

    /// <summary>
    /// subdomain的电商地址：http://subdomain.shop.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:4001/
    /// </summary>
    public static String SXWebDomain ;

    /// <summary>
    /// subdomain的融资地址：http://subdomain.market.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:5001/
    /// </summary>
    public static String JRWebDomain ;

    /// <summary>
    /// subdomain的erp地址：http://subdomain.erp.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:6001/
    /// </summary>
    public static String ERPWebDomain ;

    /// <summary>
    /// subdomain的工作流地址：http://subdomain.flow.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:7001/
    /// </summary>
    public static String FlowWebDomain ;
    /// <summary>
    /// subdomain的sso地址：http://subdomain.pay.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:8001/
    /// </summary>
    public static String PayWebDomain ;
    /// <summary>
    /// subdomain的微信地址：http://subdomain.wx.kcloudy.com/
    ///     本地测试接口地址：http://subdomain.localhost:9001/
    /// </summary>
    public static String WXWebDomain ;

    public static String PlatformApiDomain ;

    public static String PlatformSSODomain ;

    /// <summary>
    /// subdomain的接口地址，无api/后缀：http://subdomain.api.kcloudy.com/
    /// </summary>
    public static String ApiWebDomain ;

    
}
