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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;

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
@AttributeOverride(name="parent_id",column=@Column(name="ParentId"))
@AttributeOverride(name="tree_code",column=@Column(name="TreeCode"))
//@NamedEntityGraph(name = "TreeNode.Graph", attributeNodes = {@NamedAttributeNode("ChildNodes")})
@NamedEntityGraphs({
    @NamedEntityGraph(name = "Graph.TreeNode.ChildNodes",
            attributeNodes = {@NamedAttributeNode("items")})
})
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
	private int Id;

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

		if (Id != node.Id)
			return false;
		if (Name != null ? !Name.equals(node.Name) : node.Name != null)
			return false;
		if (Name != null ? !TreeCode.equals(node.TreeCode) : node.TreeCode != null)
			return false;
		if (Level != node.Level)
			return false;
		if (!Leaf && node.Leaf)
			return false;
		if (Leaf && !node.Leaf)
			return false;
		if (Index != node.Index)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Id;
		result = 31 * result + (Name != null ? Name.hashCode() : 0 );
		result = 31 * result + (TreeCode != null ? TreeCode.hashCode() : 0 );
		result = 31 * result + Level;
		result = 31 * result + new Boolean(Leaf).hashCode();
		result = 31 * result + Index;
		return result;
	}
}
