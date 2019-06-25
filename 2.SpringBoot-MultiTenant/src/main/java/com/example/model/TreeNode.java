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

import com.example.StringExtensions;

import javax.persistence.InheritanceType;
//import org.springframework.lang.Nullable;
//import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tianc 树结构基类
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@AttributeOverride(name = "parent_id", column = @Column(name = "ParentId"))
@AttributeOverride(name = "tree_code", column = @Column(name = "TreeCode"))
public abstract class TreeNode<T> extends Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 子节点Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", unique = true, nullable = false)
	// @Getter(onMethod_={
	// @Id,
	// @GeneratedValue(strategy = GenerationType.IDENTITY),
	// @Column(name = "Id", unique = true, nullable = false)})
	private int id;

	/**
	 * 父节点Id
	 */
	// private Integer ParentId;

	/**
	 * 名称
	 */
	// @Getter(onMethod_={@Column(name = "Name", length = 128)})
	@Column(name = "Name")
	private String Name;

	/**
	 * 标识树形结构的编码: 一级树节点Id-二级树节点Id-三级树节点Id-四级树节点Id 1-1-1-1 ~~ 999-999-999-999
	 */
	// @Getter(onMethod_={@Column(name = "TreeCode", length = 512)})
	@Column(name = "treecode")
	private String TreeCode;

	/**
	 * 节点深度
	 */
	// @Getter(onMethod_={@Column(name = "Level")})
	@Column(name = "Level")
	private int Level;

	/**
	 * 是否叶节点
	 */
	// @Getter(onMethod_={@Column(name = "Leaf")})
	@Column(name = "Leaf")
	private boolean Leaf;

	/**
	 * 排序
	 */
	// @Getter(onMethod_={@Column(name = "Index")})
	@Column(name = "Index")
	private int Index;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "parentid")
	private T parentNode;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parentNode")
	private Set<T> ChildNodes = new HashSet<T>(0);

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		if (!super.equals(o))
			return false;

		TreeNode<T> node = (TreeNode<T>) o;

		if (!(getId() == node.getId()))
			return false;
		if (!getName().equals(node.getName()))
			return false;
		if (!getTreeCode().equals(node.getTreeCode()))
			return false;
		if (!(getLevel() == node.getLevel()))
			return false;
		if (!isLeaf() && node.isLeaf())
			return false;
		if (isLeaf() && !node.isLeaf())
			return false;
		if (!(getIndex() == node.getIndex()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + getId();
		if (!StringExtensions.isNullOrEmpty(getName()))
			result = 31 * result + getName().hashCode();
		if (!StringExtensions.isNullOrEmpty(getTreeCode()))
			result = 31 * result + getTreeCode().hashCode();
		result = 31 * result + getLevel();
		result = 31 * result + new Boolean(isLeaf()).hashCode();
		result = 31 * result + getIndex();
		return result;
	}
}
