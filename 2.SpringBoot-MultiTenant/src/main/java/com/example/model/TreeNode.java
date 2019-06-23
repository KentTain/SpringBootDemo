package com.example.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

//import org.springframework.lang.Nullable;
//import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.Getter;

/**
 * @author tianc 树结构基类
 */
@Data
@MappedSuperclass
public abstract class TreeNode<T> extends Entity {

	/**
	 * 子节点Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="Id", unique=true, nullable=false)
	//@Getter(onMethod_={
	//		@Id,
	//		@GeneratedValue(strategy = GenerationType.IDENTITY),
	//		@Column(name = "Id", unique = true, nullable = false)})
	private int Id;

	/**
	 * 父节点Id
	 */
	//private Integer ParentId;

	/**
	 * 名称
	 */
	@Getter(onMethod_={@Column(name = "Name", length = 128)})
	//@Column(name = "Name")
	private String Name;

	/**
	 * 标识树形结构的编码: 
	 * 		一级树节点Id-二级树节点Id-三级树节点Id-四级树节点Id 
	 * 		1-1-1-1 ~~ 999-999-999-999
	 */
	@Getter(onMethod_={@Column(name = "TreeCode", length = 512)})
	//@Column(name = "TreeCode")
	private String TreeCode;

	/**
	 * 是否叶节点
	 */
	@Getter(onMethod_={@Column(name = "Leaf")})
	//@Column(name = "Leaf")
	private boolean Leaf;

	/**
	 * 节点深度
	 */
	@Getter(onMethod_={@Column(name = "Level")})
	//@Column(name = "Level")
	private int Level;

	/**
	 * 排序
	 */
	@Getter(onMethod_={@Column(name = "Index")})
	//@Column(name = "Index")
	private int Index;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ParentId")
	private T ParentNode;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="ParentNode")
	//@JoinColumn(name="ParentId")
	private Set<T> ChildNodes = new HashSet<T>(0);
}
