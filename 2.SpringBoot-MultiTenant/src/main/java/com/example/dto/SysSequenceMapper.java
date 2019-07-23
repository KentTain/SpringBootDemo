package com.example.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SysSequenceMapper {
	SysSequenceMapper INSTANCE = Mappers.getMapper(SysSequenceMapper.class);

	//@Mapping( source = "sequenceName", target = "sequenceName" )
	com.example.model.SysSequence toSysSequence(SysSequenceDTO source);

	//@Mapping( source = "sequenceName", target = "sequenceName" )
	SysSequenceDTO toSysSequenceDTO(com.example.model.SysSequence source);
}
