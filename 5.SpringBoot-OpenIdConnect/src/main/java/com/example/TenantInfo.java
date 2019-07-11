package com.example;

//@Entity
//@Table(name = "tenant_info")
public class TenantInfo {

	// @Id
	// @Column(name = "id")
	private Integer id;

	// @Column(name = "tenant_id")
	private String tenantId;

	// @Column(name = "tenant_type")
	private String tenantType;

	// @Column(name = "url")
	private String url;

	// @Column(name = "username")
	private String username;

	// @Column(name = "password")
	private String password;

	// @Column(name = "domain")
	private String domain;
	
	private String clientId;
	private String clientISecret;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantType() {
		return tenantType;
	}
	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientSecret() {
		return clientISecret;
	}
	public void setClientSecret(String clientISecret) {
		this.clientISecret = clientISecret;
	}
}