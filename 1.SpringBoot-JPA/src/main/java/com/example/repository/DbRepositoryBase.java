package com.example.repository;

import java.io.Serializable;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.example.model.EntityBase;

public class DbRepositoryBase<T extends EntityBase, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements IDbRepository<T, ID> {
	private Logger logger = LoggerFactory.getLogger(DbRepositoryBase.class);

	private Class<T> domainClass;

	private EntityManager entityManager;

	public DbRepositoryBase(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		// Keep the EntityManager around to used from the newly introduced methods.
		this.entityManager = entityManager;
	}

	public DbRepositoryBase(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.domainClass = domainClass;
	}

	@Override
	public boolean support(String modelType) {
		return domainClass.getName().equals(modelType);
	}

	@Override
	public boolean executeSql(String query, Object... parameters) {
		
		logger.debug("---execute sql: " + query );
		// TODO Auto-generated method stub
		return true;
	}

}
