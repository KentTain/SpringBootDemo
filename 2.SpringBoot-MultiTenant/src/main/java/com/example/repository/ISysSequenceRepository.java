package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.SysSequence;

public interface ISysSequenceRepository extends JpaRepository<SysSequence, String>{

	SysSequence findByPreFixString(String sequencName);

}
