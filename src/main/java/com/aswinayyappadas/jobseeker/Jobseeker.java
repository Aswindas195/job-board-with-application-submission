package com.aswinayyappadas.jobseeker;

public class Jobseeker {
    private int jobseekerId;
    private String name;
    private String email;
    private String password;

    // Constructor
    public Jobseeker(int jobseekerId, String name, String email, String password) {
        this.jobseekerId = jobseekerId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getter for jobseekerId
    public int getJobseekerId() {
        return jobseekerId;
    }

    // Setter for jobseekerId
    public void setJobseekerId(int jobseekerId) {
        this.jobseekerId = jobseekerId;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
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
