package com.example.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import com.example.model.EntityBase;
import com.example.model.TreeNode;

public class TreeNodeRepositoryFactoryBean<R extends JpaRepository<T, I>, T extends EntityBase, I extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, I> {

	public TreeNodeRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new MyRepositoryFactory(em);
	}

	private static class MyRepositoryFactory<T extends TreeNode<T>, I extends Serializable>
			extends JpaRepositoryFactory {

		private final EntityManager em;

		public MyRepositoryFactory(EntityManager em) {
			super(em);
			this.em = em;
		}

		//@Override
		@SuppressWarnings("unchecked")
		protected JpaRepository<T, I> getTargetRepository(RepositoryMetadata metadata, EntityManager em) {
	 
			JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());

			Class<?> baseClass = TreeNodeRepositoryBase.class;
			Object repository = getTargetRepositoryViaReflection(baseClass, entityInformation, em);
			
			Assert.isInstanceOf(TreeNodeRepositoryBase.class, repository);

			return (TreeNodeRepositoryBase<T, I>) repository;
		}

		
		@SuppressWarnings("unchecked")
		protected Object getTargetRepository(RepositoryMetadata metadata) {
			return new TreeNodeRepositoryBase<T, I>((Class<T>) metadata.getDomainType(), em);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return TreeNodeRepositoryBase.class;
		}
	}
}