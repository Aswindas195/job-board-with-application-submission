package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class GetServices implements UserData, JobListData, ApplicationsDataList {

    // Get user type (Employer or Jobseeker) based on user ID
    public String getUserTypeByUserId(int userId) {
        if (userData.get(userId) instanceof Employer) {
            return "Employer";
        }
        return "Jobseeker";
    }

    // Get email by user ID
    public String getEmailByUserId(int userId) {
        return userData.get(userId).getEmail();
    }

    // Get job posts by employer ID
    public JSONArray getJobPostsByEmployer(int employerId) {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : employerJobList.get(employerId)) {
            // Construct a JSON object for each job post
            JSONObject jobPost = new JSONObject()
                    .put("jobId", jobList.get(jobId).getJobId())
                    .put("title", jobList.get(jobId).getTitle())
                    .put("description", jobList.get(jobId).getDescription())
                    .put("requirements", jobList.get(jobId).getRequirements())
                    .put("location", jobList.get(jobId).getLoaction());
            jobPosts.put(jobPost);
        }
        return jobPosts;
    }

    // Get jobs by location
    public JSONArray getJobsByLocation(String location) {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : jobList.keySet()) {
            if (jobList.get(jobId).getLoaction().equals(location)) {
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobList.get(jobId).getJobId())
                        .put("title", jobList.get(jobId).getTitle())
                        .put("description", jobList.get(jobId).getDescription())
                        .put("requirements", jobList.get(jobId).getRequirements())
                        .put("location", jobList.get(jobId).getLoaction());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    // Get jobs by title
    public JSONArray getJobsByTitle(String title) {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : jobList.keySet()) {
            if (jobList.get(jobId).getTitle().equals(title)) {
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobList.get(jobId).getJobId())
                        .put("title", jobList.get(jobId).getTitle())
                        .put("description", jobList.get(jobId).getDescription())
                        .put("requirements", jobList.get(jobId).getRequirements())
                        .put("location", jobList.get(jobId).getLoaction());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    // Get all jobs from listings
    public JSONArray getAllJobsFromListings() {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : jobList.keySet()) {
            JSONObject jobPost = new JSONObject()
                    .put("jobId", jobList.get(jobId).getJobId())
                    .put("title", jobList.get(jobId).getTitle())
                    .put("description", jobList.get(jobId).getDescription())
                    .put("requirements", jobList.get(jobId).getRequirements())
                    .put("location", jobList.get(jobId).getLoaction());
            jobPosts.put(jobPost);
        }
        return jobPosts;
    }

    // Get applied jobs by job seeker
    public JSONArray getAppliedJobsByJobSeeker(int jobSeekerId) {
        JSONArray jobPosts = new JSONArray();
        if (!jobseekerApplicationList.containsKey(jobSeekerId)) {
            return jobPosts;
        } else {
            for (int jobId : jobseekerApplicationList.get(jobSeekerId)) {
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobList.get(jobId).getJobId())
                        .put("title", jobList.get(jobId).getTitle())
                        .put("description", jobList.get(jobId).getDescription())
                        .put("requirements", jobList.get(jobId).getRequirements())
                        .put("location", jobList.get(jobId).getLoaction());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getApplicationsByJob(int employerId, int jobId) {
        JSONArray applicationsArray = new JSONArray();

        if (employerJobList.containsKey(employerId) && employerJobList.get(employerId).contains(jobId)
                && jobApplicationsList.containsKey(jobId)) {
            for(int applicationId : jobApplicationsList.get(jobId)) {
                JSONObject applicationObject = new JSONObject();
                applicationObject.put("applicationId", applicationId);

                if (applicationList.containsKey(applicationId)) {
                    applicationObject.put("jobSeekerId", applicationList.get(applicationId).getJobseekerId());
                    applicationObject.put("submissionDate", applicationList.get(applicationId).getSubmissionDate());
                    applicationObject.put("coverLetter", applicationList.get(applicationId).getCoverLetter());
                    applicationObject.put("resumeFilePath", applicationList.get(applicationId).getResumeFilePath());

                    int jobSeekerId = applicationList.get(applicationId).getJobseekerId();

                    // Assuming you have additional information in the user data structure
                    if (userData.containsKey(jobSeekerId)) {
                        applicationObject.put("jobSeekerUsername", userData.get(jobSeekerId).getName());
                        applicationObject.put("jobSeekerEmail", userData.get(jobSeekerId).getEmail());
                    }
                }
                applicationsArray.put(applicationObject);
            }
            }

        return applicationsArray;
    }

}
