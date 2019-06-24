package com.example.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.InheritanceType;
//import org.springframework.lang.Nullable;
//import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.Getter;

/**
 * @author tianc 树结构基类
 */
@MappedSuperclass
@AttributeOverride(name="parent_id",column=@Column(name="ParentId"))
@AttributeOverride(name="tree_code",column=@Column(name="TreeCode"))
public abstract class TreeNode<T> extends Entity implements Serializable{
	private static final long serialVersionUID = 1L;
	
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
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int userId) {
		this.id = userId;
	}
	/**
	 * 父节点Id
	 */
	//private Integer ParentId;

	/**
	 * 名称
	 */
	//@Getter(onMethod_={@Column(name = "Name", length = 128)})
	@Column(name = "Name")
	private String Name;
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}
	/**
	 * 标识树形结构的编码: 
	 * 		一级树节点Id-二级树节点Id-三级树节点Id-四级树节点Id 
	 * 		1-1-1-1 ~~ 999-999-999-999
	 */
	//@Getter(onMethod_={@Column(name = "TreeCode", length = 512)})
	@Column(name = "treecode")
	private String TreeCode;
	public String getTreeCode() {
		return TreeCode;
	}

	public void setTreeCode(String treeCode) {
		this.TreeCode = treeCode;
	}
	/**
	 * 是否叶节点
	 */
	//@Getter(onMethod_={@Column(name = "Leaf")})
	@Column(name = "Leaf")
	private boolean Leaf;
	public boolean getLeaf() {
		return Leaf;
	}

	public void setLeaf(boolean leaf) {
		this.Leaf = leaf;
	}
	/**
	 * 节点深度
	 */
	//@Getter(onMethod_={@Column(name = "Level")})
	@Column(name = "Level")
	private int Level;
	public int getLevel() {
		return Level;
	}

	public void setLevel(int level) {
		this.Level = level;
	}
	/**
	 * 排序
	 */
	//@Getter(onMethod_={@Column(name = "Index")})
	@Column(name = "Index")
	private int Index;
	public int getIndex() {
		return Index;
	}

	public void setIndex(int level) {
		this.Index = level;
	}
	
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="parentid")
	private T parentNode;
	public T getParentNode() {
		return parentNode;
	}

	public void setParentNode(T level) {
		this.parentNode = level;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy ="parentNode")
	private Set<T> ChildNodes = new HashSet<T>(0);
	public Set<T> getChildNodes() {
		return ChildNodes;
	}

	public void setChildNodes(Set<T> levels) {
		this.ChildNodes = levels;
	}
}
