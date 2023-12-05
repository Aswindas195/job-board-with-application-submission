package com.aswinayyappadas.application;

import java.util.Date;

public class Application {
    private int applicationId;
    private int jobseekerId;
    private int jobId;
    private String resumeFilePath;
    private String coverLetter;
    private Date submissionDate;

    public Application(int applicationId, int jobseekerId, String resumeFilePath, String coverLetter, Date submissionDate) {
        this.applicationId = applicationId;
        this.jobseekerId = jobseekerId;
        this.jobId = jobId;
        this.resumeFilePath = resumeFilePath;
        this.coverLetter = coverLetter;
        this.submissionDate = submissionDate;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getJobseekerId() {
        return jobseekerId;
    }

    public int getJobId() {
        return jobId;
    }

    public String getResumeFilePath() {
        return resumeFilePath;
    }

    public void setResumeFilePath(String resumeFilePath) {
        this.resumeFilePath = resumeFilePath;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }
}
