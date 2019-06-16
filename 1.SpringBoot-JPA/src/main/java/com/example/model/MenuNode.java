package com.example.model;

import javax.persistence.Entity;

import lombok.Data;

@Data
@Entity(name="tb_menu")
public class MenuNode extends TreeNode<MenuNode> {

	public String Desc;

}
