package com.aswinayyappadas.job;

public class Job {
    private int jobId;
    private String title;
    private String description;
    private String location;
    private String requirements;

    // Constructor
    public Job(int jobId, String title, String description, String location, String requirements) {
        this.jobId = jobId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.requirements = requirements;
    }

    // Getter for jobId
    public int getJobId() {
        return jobId;
    }

    // Setter for jobId
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for location
    public String getLocation() {
        return location;
    }

    // Setter for location
    public void setLocation(String location) {
        this.location = location;
    }

    // Getter for requirements
    public String getRequirements() {
        return requirements;
    }

    // Setter for requirements
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}
