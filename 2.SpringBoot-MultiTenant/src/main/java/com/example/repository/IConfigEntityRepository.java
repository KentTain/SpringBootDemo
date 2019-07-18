package com.example.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.ConfigEntity;

public interface IConfigEntityRepository extends JpaRepository<ConfigEntity, Integer>{
	
	@EntityGraph(value = "Graph.ConfigEntity.ConfigAttributes")
	ConfigEntity findByConfigName(String configName);
	
}
