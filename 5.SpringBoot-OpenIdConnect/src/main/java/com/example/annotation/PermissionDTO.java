package com.example.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionDTO {

	public PermissionDTO()
	{
		children = new ArrayList<PermissionDTO>();
	}
    public int id;

    public String text;

    public Integer ParentId;

    public String TreeCode;

    public boolean Leaf;

    public int Level;

    public int Index;
    
    public PermissionDTO Parent;

    public String ApplicationName;

    public UUID ApplicationId;

    public String Description;

    public String DefaultRoleId ;

    public String Parameters ;

    public String ActionName;

    public String AreaName;

    public boolean IsEditMode;

    public String ControllerName;

    public String AuthorityId;

    public List<PermissionDTO> children;
}
