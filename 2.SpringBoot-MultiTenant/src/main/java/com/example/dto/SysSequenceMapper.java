package com.example.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.model.SysSequence;


@Mapper
public interface SysSequenceMapper {
	SysSequenceMapper INSTANCE = Mappers.getMapper(SysSequenceMapper.class);

	//@Mapping( source = "sequencName", target = "sequencName" )
	com.example.model.SysSequence toSysSequence(SysSequenceDTO source);

	//@Mapping( source = "sequencName", target = "sequencName" )
	SysSequenceDTO toSysSequenceDTO(com.example.model.SysSequence source);
}
