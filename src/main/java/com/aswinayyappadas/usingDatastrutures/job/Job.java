package com.aswinayyappadas.usingDatastrutures.job;

public class Job {
    private int jobId;
    private String title;
    private String description;
    private String loaction;
    private String requirements;

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLoaction() {
        return loaction;
    }

    public void setLoaction(String loaction) {
        this.loaction = loaction;
    }
}
