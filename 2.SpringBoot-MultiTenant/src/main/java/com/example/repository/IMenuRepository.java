package com.example.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.MenuNode;
import com.example.model.User;

public interface IMenuRepository extends ITreeNodeRepository<MenuNode, Integer> {
	@Transactional(timeout = 10)
	@Query(value = "select top 1 * from tb_menu u WITH (ROWLOCK) where u.Name = :name", nativeQuery = true)
	MenuNode findByName(@Param("name") String name);

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	MenuNode findById(int id);
}
