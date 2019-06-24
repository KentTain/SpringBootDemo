package com.example.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name="tb_menu")
@Inheritance(strategy=InheritanceType.JOINED) 
public class MenuNode extends TreeNode<MenuNode> implements Serializable{
	private static final long serialVersionUID = 3862416351900991824L;
	
	public MenuNode()
	{}
	
	private String Desc;
	@Column(name = "Desc")
	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		this.Desc = desc;
	}
}
