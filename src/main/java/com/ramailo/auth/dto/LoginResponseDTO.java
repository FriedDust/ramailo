package com.ramailo.auth.dto;

public class LoginResponseDTO {
	
	private Integer id;
    private String email;
    private TokenDTO token;
    private String role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TokenDTO getToken() {
        return token;
    }

    public void setToken(TokenDTO token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
