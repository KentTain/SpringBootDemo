package com.example.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.EntityBase;

//@Repository
@Transactional(readOnly = true)
public class DbRepositoryBase<T extends EntityBase, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements IDbRepository<T, ID> {
	private Logger logger = LoggerFactory.getLogger(DbRepositoryBase.class);

	private final EntityManager entityManager;

	public DbRepositoryBase(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		// Keep the EntityManager around to used from the newly introduced methods.
		this.entityManager = entityManager;
	}

	public DbRepositoryBase(Class<T> domainClass, EntityManager entityManager) {
		this(JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager), entityManager);
	}

	@Override
	public boolean support(String modelType) {
		return super.getDomainClass().getName().equals(modelType);
	}
	
	
	
	@Transactional(readOnly = false)
	@Override
	public boolean executeUpdateSql(String sql, Object... parameters) {
		try {
			//logger.debug("---execute sql: " + sql );
			
            Query query=entityManager.createNativeQuery(sql);
            int result = query.executeUpdate();
            //entityManager.close();
            
            return result > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <K extends EntityBase> List<K> sqlQuery(String sql){
		try {
			//logger.debug("---execute sql: " + sql );
			
            Query query=entityManager.createQuery(sql);
            List<K> result = query.getResultList();
            //entityManager.close();
            
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
	}

}
