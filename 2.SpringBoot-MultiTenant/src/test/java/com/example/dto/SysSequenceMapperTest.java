package com.example.dto;

import org.junit.Assert;
import org.junit.Test;

import com.example.model.SysSequence;

public class SysSequenceMapperTest {

	@Test
	public void testToSysSequence() {
		SysSequence sequence = SysSequence.builder().sequencName("TestSeq").initValue(1).maxValue(10000).currentValue(1)
				.stepValue(1).currDate("2019-01-01").preFixString("CRU").postFixString("End").comments("Test").build();
		SysSequenceDTO sequenceDto = SysSequenceMapper.INSTANCE.toSysSequenceDTO(sequence);
		
		Assert.assertEquals(sequence.getSequencName(), sequenceDto.getSequencName());

	}

	@Test
	public void testToSysSequenceDTO() {
		SysSequenceDTO sequenceDto = SysSequenceDTO.builder().sequencName("TestSeq").initValue(1).maxValue(10000).currentValue(1)
				.stepValue(1).currDate("2019-01-01").preFixString("CRU").postFixString("End").comments("Test").build();
		SysSequence sequence = SysSequenceMapper.INSTANCE.toSysSequence(sequenceDto);
		
		Assert.assertEquals(sequence.getSequencName(), sequenceDto.getSequencName());
	}

}
