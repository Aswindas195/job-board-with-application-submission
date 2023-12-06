package com.aswinayyappadas.usingDatastrutures.applications;

import java.util.Date;

public class Applications {
    private String applicationId;
    private String jobId;
    private String jobseekerId;
    private Date submissionDate;
    private String resumeFilePath;
    private String coverLetter;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobseekerId() {
        return jobseekerId;
    }

    public void setJobseekerId(String jobseekerId) {
        this.jobseekerId = jobseekerId;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
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
}
