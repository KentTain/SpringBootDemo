package com.example.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.ConfigAttribute;


public interface IConfigAttributeRepository extends JpaRepository<ConfigAttribute, Integer>{

	ConfigAttribute findByName(String name);
	
}
