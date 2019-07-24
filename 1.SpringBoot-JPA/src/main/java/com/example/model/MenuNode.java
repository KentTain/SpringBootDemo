package com.example.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tb_menu")
public class MenuNode extends TreeNode<MenuNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6362616183694462936L;
	private String Desc;

}
