package com.example.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="tb_menu")
public class MenuNode extends TreeNode<MenuNode> {

	private String Desc;

}
