package com.aswinayyappadas.usingDatastrutures.user;

public class User {
    private String name;
    private String email;
    private String password;
    private String salt;
    private String jwt_secret_key;

    public String getJwt_secret_key() {
        return jwt_secret_key;
    }

    public void setJwt_secret_key(String jwt_secret_key) {
        this.jwt_secret_key = jwt_secret_key;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
