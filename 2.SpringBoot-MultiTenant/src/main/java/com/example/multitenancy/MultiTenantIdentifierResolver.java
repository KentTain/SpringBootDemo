package com.example.multitenancy;


import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 这个类是由Hibernate提供的用于识别tenantId的类，当每次执行sql语句被拦截就会调用这个类中的方法来获取tenantId
 * @author lanyuanxiaoyao
 * @version 1.0
 * https://dzone.com/articles/spring-boot-hibernate-multitenancy-implementation
 */
@Component
public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver{
	private Logger logger = LoggerFactory.getLogger(MultiTenantIdentifierResolver.class.getName());
	private final String DEFAULT_TENANT_ID = "devdb";
    // 获取tenantId的逻辑在这个方法里面写
	@Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("-----MultiTenantIdentifierResolver get tenant: " + tenantId);
        if (tenantId != null) {
            return tenantId;
        }
        return DEFAULT_TENANT_ID;
    }
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
