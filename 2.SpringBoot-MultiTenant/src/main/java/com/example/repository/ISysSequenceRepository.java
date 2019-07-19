package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.SysSequence;

@Repository
public interface ISysSequenceRepository extends JpaRepository<SysSequence, String>{

	SysSequence findByPreFixString(String preFix);
	
	SysSequence findByPostFixString(String postFix);

}
