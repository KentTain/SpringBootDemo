package com.example.repository;


import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.User;

public interface IUserRepository extends JpaRepository<User, Long> {
	@Transactional(timeout = 10)
	@Query(value="select top 1 * from tb_user u WITH (ROWLOCK, UPDLOCK) where u.user_name = :userName ",nativeQuery=true)
    User findByUserName(@Param("userName") String userName);
	
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	User findByUserId(String userId);
}
