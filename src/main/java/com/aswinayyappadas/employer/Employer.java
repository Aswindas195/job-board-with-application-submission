package com.aswinayyappadas.employer;

public class Employer {
    private int employerId;
    private String employerName;
    private String email;
    private String password;

    // Constructor
    public Employer(int employerId, String employerName, String email, String password) {
        this.employerId = employerId;
        this.employerName = employerName;
        this.email = email;
        this.password = password;
    }

    // Getter for employerId
    public int getEmployerId() {
        return employerId;
    }

    // Setter for employerId
    public void setEmployerId(int employerId) {
        this.employerId = employerId;
    }

    // Getter for employerName
    public String getEmployerName() {
        return employerName;
    }

    // Setter for employerName
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}
