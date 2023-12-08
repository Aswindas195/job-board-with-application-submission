package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatabase.util.application.ApplicationUtils;
import com.aswinayyappadas.usingDatastrutures.applications.Application;
import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.util.application.ApplicationIdGenerator;
import com.aswinayyappadas.usingDatastrutures.util.application.CheckApplicationIdValidity;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;

public class ApplicationService implements ApplicationsDataList, JobListData {
    private ApplicationIdGenerator applicationIdGenerator;
    private CheckApplicationIdValidity checkApplicationIdValidity;

    public ApplicationService() {
        // Initialize ApplicationIdGenerator and CheckApplicationIdValidity
        this.applicationIdGenerator = new ApplicationIdGenerator();
        this.checkApplicationIdValidity = new CheckApplicationIdValidity();
    }

    // Check if the user has applied for a specific job
    public boolean hasUserAppliedForJob(int jobSeekerId, int jobId) {
        if (!jobseekerApplicationList.containsKey(jobSeekerId)) {
            return false;
        } else return jobseekerApplicationList.get(jobSeekerId).contains(jobId);
    }

    // Apply for a job and return application details as JSON
    public JSONObject applyForJob(int jobSeekerId, int jobId) throws ExceptionHandler {
        // Check if the user has already applied for the job
        if (hasUserAppliedForJob(jobSeekerId, jobId)) {
            throw new ExceptionHandler("Error applying for the job. User has already applied.");
        }

        // Generate random cloud file location path
        String resumeFilePath = ApplicationUtils.generateRandomFilePath();

        // Generate cover letter text
        String coverLetter = ApplicationUtils.generateCoverLetter();
        Date date = new Date();

        // Create an Application object
        Application application = new Application();
        application.setApplicationId(getValidApplicationId());
        application.setJobId(jobId);
        application.setJobseekerId(jobSeekerId);
        application.setSubmissionDate(date);
        application.setCoverLetter(coverLetter);
        application.setResumeFilePath(resumeFilePath);

        // Add the application to the applicationList
        applicationList.put(application.getApplicationId(), application);

        // Update jobseekerApplicationList
        jobApplicationsList.putIfAbsent(jobId, new HashSet<>());
        jobApplicationsList.get(jobId).add(application.getApplicationId());
        jobseekerApplicationList.putIfAbsent(jobSeekerId, new HashSet<>());
        jobseekerApplicationList.get(jobSeekerId).add(jobId);

        // Create a JSON object with application details
        JSONObject applicationJson = new JSONObject();
        applicationJson.put("applicationId", application.getApplicationId());
        applicationJson.put("jobId", application.getJobId());
        applicationJson.put("jobseekerId", application.getJobseekerId());
        applicationJson.put("submissionDate", application.getSubmissionDate());
        applicationJson.put("coverLetter", application.getCoverLetter());
        applicationJson.put("resumeFilePath", application.getResumeFilePath());

        return applicationJson;
    }

    // Get a valid application ID
    public int getValidApplicationId() {
        int applicationId = -1;
        while (true) {
            applicationId = applicationIdGenerator.generateRandomUserId();
            if (!checkApplicationIdValidity.isValidApplicationId(applicationId, applicationList)) break;
        }
        return applicationId;
    }

    // Delete a job application for a specific job seeker
    public void deleteJobApplicationByJobSeekerId(int jobSeekerId, int jobId) {
        if (jobseekerApplicationList.containsKey(jobSeekerId) && jobseekerApplicationList.get(jobSeekerId).contains(jobId)) {
            jobseekerApplicationList.get(jobSeekerId).remove(jobId);
        }
    }

    // Update resume file path for a specific job seeker and job
    public String updateResumeFilePath(int jobSeekerId, int jobId, String newResumeFilePath) {
        for (int applicationId : applicationList.keySet()) {
            if (applicationList.get(applicationId).getJobId() == jobId && applicationList.get(applicationId).getJobseekerId() == jobSeekerId) {
                applicationList.get(applicationId).setResumeFilePath(newResumeFilePath);
            }
        }
        return newResumeFilePath;
    }

    // Update cover letter for a specific job seeker and job
    public String updateCoverLetter(int jobSeekerId, int jobId, String newCoverLetter) {
        for (int applicationId : applicationList.keySet()) {
            if (applicationList.get(applicationId).getJobId() == jobId && applicationList.get(applicationId).getJobseekerId() == jobSeekerId) {
                applicationList.get(applicationId).setCoverLetter(newCoverLetter);
            }
        }
        return newCoverLetter;
    }

    // Display updated application details for a specific job seeker and job
    public JSONObject displayUpdatedApplication(int jobSeekerId, int jobId) {
        JSONObject applicationJson = new JSONObject();
        if (jobseekerApplicationList.get(jobSeekerId).contains(jobId)) {
            for (int applicationId : applicationList.keySet()) {
                if (applicationList.get(applicationId).getJobId() == jobId && applicationList.get(applicationId).getJobseekerId() == jobSeekerId) {
                    // Create a JSON object with application details
                    applicationJson.put("applicationId", applicationList.get(applicationId).getApplicationId());
                    applicationJson.put("jobId", applicationList.get(applicationId).getJobId());
                    applicationJson.put("jobseekerId", applicationList.get(applicationId).getJobseekerId());
                    applicationJson.put("submissionDate", applicationList.get(applicationId).getSubmissionDate());
                    applicationJson.put("coverLetter", applicationList.get(applicationId).getCoverLetter());
                    applicationJson.put("resumeFilePath", applicationList.get(applicationId).getResumeFilePath());
                }
            }
        }
        return applicationJson;
    }
}
