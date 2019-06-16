package com.example.model;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.lang.Nullable;

import lombok.Data;

/**
 * @author tianc 树结构基类
 */
@Data
public abstract class TreeNode<T> extends Entity {

	/**
	 * 子节点Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int Id;

	/**
	 * 父节点Id
	 */
	@Nullable
	public Integer ParentId;

	/**
	 * 名称
	 */
	public String Name;

	/**
	 * 标识树形结构的编码: 
	 * 		一级树节点Id-二级树节点Id-三级树节点Id-四级树节点Id 
	 * 		1-1-1-1 ~~ 999-999-999-999
	 */
	public String TreeCode;

	/**
	 * 是否叶节点
	 */
	public boolean Leaf;

	/**
	 * 节点深度
	 */
	public int Level;

	/**
	 * 排序
	 */
	public int Index;

	public T ParentNode;

	public List<T> ChildNodes;
}
