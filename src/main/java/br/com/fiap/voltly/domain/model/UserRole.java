package br.com.fiap.voltly.domain.model;

public enum UserRole {
    ADMIN("admin"),
    USER ("user");

    private final String value;
    UserRole(String value){ this.value = value; }
    public String getValue(){ return value; }
}
